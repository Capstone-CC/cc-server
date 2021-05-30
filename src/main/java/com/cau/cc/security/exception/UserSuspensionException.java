package com.cau.cc.security.exception;

import org.springframework.security.core.AuthenticationException;

public class UserSuspensionException extends AuthenticationException {
    public UserSuspensionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public UserSuspensionException(String msg) {
        super(msg);
    }
}
