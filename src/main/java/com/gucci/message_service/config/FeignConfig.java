package com.gucci.message_service.config;

import com.gucci.message_service.auth.JwtUserAuthentication;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignConfig implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtUserAuthentication jwtAuth) {
            String token = jwtAuth.getToken();
            template.header("Authorization", "Bearer " + token);
        }
    }
}
