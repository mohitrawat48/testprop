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
    prop_value VARCHAR(200) NOT NULL
    );

    INSERT INTO MyProperties( prop_key, prop_value)
    values('userdb', 'testuser');
    INSERT INTO MyProperties( prop_key, prop_value)
    values('passworddb', 'testpassword');
    INSERT INTO MyProperties( prop_key, prop_value)
    values('hostdb', 'spring.io');
    INSERT INTO MyProperties( prop_key, prop_value)
    values('portdb', '83');

	Select * from MyProperties

 */

/*

open below url to see user and password

http://localhost:8080/getprop

* */

