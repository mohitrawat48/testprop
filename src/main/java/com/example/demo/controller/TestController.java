package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class TestController {

    @Value("${myuser}")
    private String user;

    @Value("${mypassword}")
    private String password;

    @GetMapping("/getprop")
    public List getAppName() {
        return List.of(user, password);
    }
}
