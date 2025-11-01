package com.example.controllers.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginLogoutController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout-success")
    public String logout() {
        return "logout";
    }

    @GetMapping("/blocked")
    public String blocked() {
        return "blocked";
    }
}
