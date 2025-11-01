package com.example.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// custom exception handler to redirect user to blocked page if exception with too many incorrect attempts is thrown

@Component
public class CustomAuthFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @NotNull AuthenticationException e) throws IOException, ServletException {
        if(e instanceof UserLockedException){
            response.sendRedirect("/blocked");
        }else{
            response.sendRedirect("/login?error");
        }
    }
}
