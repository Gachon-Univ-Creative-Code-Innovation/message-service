package com.gucci.message_service.auth;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class JwtUserAuthentication extends AbstractAuthenticationToken {

    private final Long userId;
    private final String nickname;
    private final String token;

    public JwtUserAuthentication(Long userId, String nickname, String token) {
        super(null);
        this.userId = userId;
        this.nickname = nickname;
        this.token = token;
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
