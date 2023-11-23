package com.example.serversdk.auth.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "Unsecured Data";
    }

    @GetMapping("/secured")
    public String securedData() {
        return "Secured Data";
    }

    @GetMapping("/admin")
    public String adminData() {
        return "Admin Data";
    }
}
