package com.example.auth;

import com.example.exception.UserLockedException;
import com.example.service.BruteForceProtectionService;
import com.example.service.CustomUserDetailsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired private CustomUserDetailsService customUserDetailsService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private BruteForceProtectionService bruteForceProtectionService;


    @Override
    public Authentication authenticate(@NotNull Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        User user = (User) customUserDetailsService.loadUserByUsername(username);

        if(user == null){
            throw new BadCredentialsException("username or password was incorrect");
        }

        if(bruteForceProtectionService.isLocked(username)){
            throw new UserLockedException("User Account is locked due to many failed attempts try again in 5 minutes");
        }

        if(!passwordEncoder.matches(password, user.getPassword())){
            bruteForceProtectionService.loginFailed(username);
            throw new BadCredentialsException("Invalid username or password");
        }

        bruteForceProtectionService.goodLogin(username);
        return new UsernamePasswordAuthenticationToken(username,password,user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
