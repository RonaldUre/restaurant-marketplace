package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.infrastructure.web.auth.dto;

public class TokenResponse {
    private String tokenType = "Bearer";
    private String accessToken;
    private long expiresInSeconds;

    public TokenResponse(String accessToken, long expiresInSeconds) {
        this.accessToken = accessToken;
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getTokenType() { return tokenType; }
    public String getAccessToken() { return accessToken; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
}