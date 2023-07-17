package com.jw.account.models;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticatedProfile extends AbstractAuthenticationToken {

    private final String username;
    private final String token;

    public JwtAuthenticatedProfile(String username, Collection<? extends GrantedAuthority> authorities, String token) {
        super(authorities);
        this.username = username;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}

