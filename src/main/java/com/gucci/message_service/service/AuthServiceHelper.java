package com.gucci.message_service.service;

import com.gucci.common.exception.CustomException;
import com.gucci.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceHelper {
    public Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        return (Long) authentication.getPrincipal();
    }
}
