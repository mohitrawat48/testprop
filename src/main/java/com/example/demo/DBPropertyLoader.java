package com.example.demo;


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
import java.util.stream.Collectors;

@Component
public class DBPropertyLoader implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String PROPERTY_SOURCE_NAME = "databaseProperties";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        ConfigurableEnvironment configEnv = applicationContext.getEnvironment();

        List<Object> allPropertySources = configEnv.getPropertySources().stream()
                .map(PropertySource::getSource).collect(Collectors.toList());

        List<Map<?, ?>> unmodifiableMaps = allPropertySources.stream()
                .filter(object -> object.getClass().equals(Collections.unmodifiableMap(Collections.emptyMap()).getClass()))
                .map(object -> (Map<?, ?>) object)
                .collect(Collectors.toList());

        Map<?, ?> propertyMap = unmodifiableMaps.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, Object> propertySource = getPropertyMapFromDatabase(propertyMap);
        applicationContext.getEnvironment().getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, propertySource));
    }

    private Map<String, Object> getPropertyMapFromDatabase(Map<?, ?> appConfigProp) {
        Map<String, Object> propertySource = new HashMap<>();

        final String url =  appConfigProp.get("spring.datasource.url").toString();
        final String driverClassName = appConfigProp.get("spring.datasource.driverClassName").toString();
        final String username = appConfigProp.get("spring.datasource.username").toString();
        final String password = appConfigProp.get("spring.datasource.password").toString();
        final String query = appConfigProp.get("customQuery").toString();

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
            rs = preparedStatement.executeQuery();
            // Populate all properties into the property source
            while (rs.next()) {
                final String propName = rs.getString("prop_key");
                final String propValue = rs.getString("prop_value");
                propertySource.put(propName, propValue);
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
