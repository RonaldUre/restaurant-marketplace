package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto;

public class TokenResponse {
    private String tokenType = "Bearer";
    private String accessToken;
    private long accessExpiresInSeconds;
    private String refreshToken;

    public TokenResponse(String accessToken, long accessExpiresInSeconds, String refreshToken) {
        this.accessToken = accessToken;
        this.accessExpiresInSeconds = accessExpiresInSeconds;
        this.refreshToken = refreshToken;
    }

    public String getTokenType() { return tokenType; }
    public String getAccessToken() { return accessToken; }
    public long getAccessExpiresInSeconds() { return accessExpiresInSeconds; }
    public String getRefreshToken() { return refreshToken; }
}
