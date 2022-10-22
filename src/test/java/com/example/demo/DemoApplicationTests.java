package com.example.demo;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DemoApplicationTests {

	@Test
	void contextLoads() {
		Pattern pattern = Pattern.compile("(?<=\\$\\{).*?(?=\\})");
		Matcher matcher = pattern.matcher("${test1}asdf${test2}");
		while (matcher.find()) {
			System.out.println("found");
			System.out.println(matcher.group());
		}
	}

}
