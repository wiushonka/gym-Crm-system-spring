package com.example.controllers.rest;

import com.example.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    @Autowired
    public AuthController(AuthenticationManager authManager, JwtService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String password = body.get("password");

        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        String token = jwtService.generateJwtToken(auth.getName());
        return Map.of("token", token);
    }
}
