package com.jw.account.response;

public class RefreshTokenResponse {
    private final String token;
    private final String refreshToken;

    public RefreshTokenResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
