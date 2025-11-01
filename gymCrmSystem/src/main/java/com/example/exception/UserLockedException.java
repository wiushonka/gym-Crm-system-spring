package com.example.exception;

import org.springframework.security.core.AuthenticationException;

public class UserLockedException extends AuthenticationException {
    public UserLockedException(String msg){
        super(msg);
    }
}
