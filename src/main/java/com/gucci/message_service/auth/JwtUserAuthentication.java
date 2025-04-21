package com.gucci.message_service.auth;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class JwtUserAuthentication extends AbstractAuthenticationToken {

    private final Long userId;
    private final String nickname;

    public JwtUserAuthentication(Long userId, String nickname) {
        super(null);
        this.userId = userId;
        this.nickname = nickname;
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
