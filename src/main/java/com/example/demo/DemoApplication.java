package com.example.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

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
    prop_value VARCHAR(200) NOT NULL,
    profile VARCHAR(200) NOT NULL
    );

    INSERT INTO MyProperties( prop_key, prop_value, profile)
    values('dbuser', 'devuser', 'dev');
    INSERT INTO MyProperties( prop_key, prop_value, profile)
    values('dbpassword', 'devpassword', 'dev');
    INSERT INTO MyProperties( prop_key, prop_value, profile)
    values('hostdb', 'dev-spring.io', 'dev');
    INSERT INTO MyProperties( prop_key, prop_value, profile)
    values('portdb', 'dev-83', 'dev');

    INSERT INTO MyProperties( prop_key, prop_value, profile)
    values('dbuser', 'produser', 'prod');
    INSERT INTO MyProperties( prop_key, prop_value, profile)
    values('dbpassword', 'prodpassword', 'prod');
    INSERT INTO MyProperties( prop_key, prop_value, profile)
    values('hostdb', 'prod-spring.io', 'prod');
    INSERT INTO MyProperties( prop_key, prop_value, profile)
    values('portdb', 'prod-83', 'prod');

	Select * from MyProperties

 */

/*

open below url to see user and password

http://localhost:8080/getprop

* */

