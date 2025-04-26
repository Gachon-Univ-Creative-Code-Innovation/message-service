package com.gucci.message_service.auth.util;

import com.gucci.common.exception.CustomException;
import com.gucci.common.exception.ErrorCode;
import com.gucci.message_service.auth.JwtUserAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class JwtUtil {

    public static JwtUserAuthentication getCurrentUserAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtUserAuthentication jwtAuth && jwtAuth.isAuthenticated()) {
            return jwtAuth;
        }
        throw new CustomException(ErrorCode.FORBIDDEN);
    }

    public static Long getCurrentUserId() {
        return getCurrentUserAuthentication().getUserId();
    }

    public static String getCurrentNickname() {
        return getCurrentUserAuthentication().getNickname();
    }
}
