package com.gucci.message_service.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtUserAuthentication extends AbstractAuthenticationToken {

    private final Long userId;

    public JwtUserAuthentication(Long userId) {
        super(null);
        this.userId = userId;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
