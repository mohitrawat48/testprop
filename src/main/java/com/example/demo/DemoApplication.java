package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.ConfigurableApplicationContext;

//@EnableConfigServer
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {

		new SpringApplicationBuilder(DemoApplication.class)
				.initializers(new DBPropertyLoader())
				.run(args);
	}

}

/*
Open H2-Console


http://localhost:8080/h2-console

* */

//Run in H2
/*

    DROP TABLE IF EXISTS MyProperties;

    CREATE TABLE MyProperties (
    prop_key VARCHAR(200) NOT NULL,
    prop_value VARCHAR(200) NOT NULL
    );

    INSERT INTO MyProperties( prop_key, prop_value)
    values('user', 'testuser');
    INSERT INTO MyProperties( prop_key, prop_value)
    values('password', 'testpassword');
    INSERT INTO MyProperties( prop_key, prop_value)
    values('host', 'spring.io');
    INSERT INTO MyProperties( prop_key, prop_value)
    values('port', '83');

	Select * from MyProperties

 */

/*

open below url to see user and password

http://localhost:8080/getprop

* */

