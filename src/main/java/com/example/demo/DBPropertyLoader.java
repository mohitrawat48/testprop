package com.example.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DBPropertyLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String PROPERTY_SOURCE_NAME = "databaseProperties";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        ConfigurableEnvironment configEnv = applicationContext.getEnvironment();

        List<Object> allPropertySources = configEnv.getPropertySources().stream()
                .map(PropertySource::getSource).collect(Collectors.toList());

        List<Map<?, ?>> allMapSources = allPropertySources.stream()
                .filter(obj -> Map.class.isAssignableFrom(obj.getClass()))
                .map(object -> (Map<?, ?>) object)
                .collect(Collectors.toList());

        String profile = (String) allMapSources.stream()
                .flatMap(map -> map.entrySet().stream())
                .filter(entry -> entry.getKey().equals("spring.profiles.active"))
                .map(entry -> entry.getValue().toString())
                .findFirst().orElse("");

        List<Map<?, ?>> yamlSources = allPropertySources.stream()
                .filter(object -> object.getClass().equals(Collections.unmodifiableMap(Collections.emptyMap()).getClass()))
                .map(object -> (Map<?, ?>) object)
                .collect(Collectors.toList());

        List<Map.Entry<?, ?>> yamlEntries = yamlSources.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toList());

        Map<String, String> yamlProperties = new HashMap<String, String>();
        for (Map.Entry<?, ?> entry : yamlEntries) {
            String key = entry.getKey().toString();
            String val = entry.getValue().toString();

            if (!yamlProperties.containsKey(key))
                yamlProperties.put(key, val);
        }

        Map<String, Object> propertySource = getPropertyMapFromDatabase(profile, yamlProperties);
        applicationContext.getEnvironment().getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, propertySource));
    }

    private Map<String, Object> getPropertyMapFromDatabase(String profile, Map<String, String> appConfigProp) {

        Map<String, Object> propertySource = new HashMap<>();
        Map<String, String> allDBProperties = new HashMap<>();

        final String url = appConfigProp.get("spring.datasource.url");
        final String driverClassName = appConfigProp.get("spring.datasource.driverClassName");
        final String username = appConfigProp.get("spring.datasource.username");
        final String password = appConfigProp.get("spring.datasource.password");
        final String query = appConfigProp.get("customQuery");

        // Now check for database properties
        DataSource ds = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            ds = DataSourceBuilder.create()
                    .username(username)
                    .password(password)
                    .url(url)
                    .driverClassName(driverClassName)
                    .build();

            connection = ds.getConnection();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, profile);
            rs = preparedStatement.executeQuery();
            // Populate all properties into the property source
            while (rs.next()) {
                final String propName = rs.getString("prop_key");
                final String propValue = rs.getString("prop_value");
                allDBProperties.put(propName, propValue);
            }

            for (Map.Entry<String, String> entry : appConfigProp.entrySet()) {
                String value = entry.getValue();
                Pattern pattern = Pattern.compile("(?<=\\$\\{).*?(?=\\})");
                Matcher matcher = pattern.matcher(value);

                if (matcher.find()) {
                    String key = matcher.group();
                    String val = allDBProperties.getOrDefault(key, "");
                    propertySource.put(key, val);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                // supress
            }
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
                // supress
            }
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                // supress
            }
        }
        return propertySource;
    }
}
