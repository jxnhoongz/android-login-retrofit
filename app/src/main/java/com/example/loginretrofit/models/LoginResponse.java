package com.example.loginretrofit.models;

/**
 * Login Response Model
 *
 * This class represents the response structure from the login API
 * Contains authentication tokens and user information
 *
 * Educational Note: This class maps the JSON response from the server
 * The field names must match the JSON keys exactly (or use @SerializedName)
 *
 * API Response Example:
 * {
 *   "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
 *   "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
 *   "tokenType": "Bearer",
 *   "expiresIn": 3600
 * }
 *
 * @author Han Vatana (PP50130)
 * @course Mobile Programming
 * @assignment Android Login with Retrofit
 */
public class LoginResponse {

    /**
     * JWT Access Token for authenticated API calls
     * This token should be included in Authorization header as "Bearer {accessToken}"
     */
    private String accessToken;

    /**
     * Refresh Token for getting new access tokens when they expire
     */
    private String refreshToken;

    /**
     * Token type - typically "Bearer" for JWT tokens
     */
    private String tokenType;

    /**
     * Token expiration time in seconds
     */
    private long expiresIn;

    /**
     * Default constructor required for Gson deserialization
     */
    public LoginResponse() {
    }

    /**
     * Constructor with all parameters
     */
    public LoginResponse(String accessToken, String refreshToken, String tokenType, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
    }

    // Getter and Setter methods

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * Utility method to get full authorization header value
     * @return "Bearer {accessToken}"
     */
    public String getAuthorizationHeader() {
        return (tokenType != null ? tokenType : "Bearer") + " " + accessToken;
    }

    /**
     * Check if the response contains valid tokens
     * @return true if access token is not null or empty
     */
    public boolean isValid() {
        return accessToken != null && !accessToken.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "accessToken='" + (accessToken != null ? "[TOKEN_PRESENT]" : "null") + '\'' +
                ", refreshToken='" + (refreshToken != null ? "[TOKEN_PRESENT]" : "null") + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}