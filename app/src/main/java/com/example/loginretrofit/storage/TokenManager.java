package com.example.loginretrofit.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Token Manager Class
 *
 * This class handles secure storage and retrieval of authentication tokens
 * Uses SharedPreferences for persistent storage across app sessions
 *
 * Educational Note: SharedPreferences is Android's built-in key-value storage
 * Perfect for storing small pieces of data like tokens, user preferences, etc.
 * For sensitive data, consider using EncryptedSharedPreferences in production
 *
 * @author Han Vatana (PP50130)
 * @course Mobile Programming
 * @assignment Android Login with Retrofit
 */
public class TokenManager {

    /**
     * SharedPreferences file name
     */
    private static final String PREF_NAME = "LoginRetrofitPrefs";

    /**
     * Keys for storing different values
     */
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String KEY_LOGIN_TIME = "login_time";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    /**
     * SharedPreferences instance
     */
    private SharedPreferences sharedPreferences;

    /**
     * SharedPreferences editor for writing data
     */
    private SharedPreferences.Editor editor;

    /**
     * Constructor
     *
     * Educational Note: Context is needed to access SharedPreferences
     * MODE_PRIVATE ensures only this app can access the preferences
     *
     * @param context Application context
     */
    public TokenManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Save authentication tokens after successful login
     *
     * Educational Note: This method stores all token-related information
     * The login time is stored to calculate token expiration
     *
     * @param accessToken JWT access token
     * @param refreshToken Refresh token for getting new access tokens
     * @param tokenType Token type (usually "Bearer")
     * @param expiresIn Token expiration time in seconds
     */
    public void saveTokens(String accessToken, String refreshToken, String tokenType, long expiresIn) {
        long currentTime = System.currentTimeMillis();

        editor.putString(KEY_ACCESS_TOKEN, accessToken);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.putString(KEY_TOKEN_TYPE, tokenType);
        editor.putLong(KEY_EXPIRES_IN, expiresIn);
        editor.putLong(KEY_LOGIN_TIME, currentTime);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // Apply changes asynchronously for better performance
        // Educational Note: apply() is faster than commit() as it's asynchronous
        editor.apply();
    }

    /**
     * Get the access token
     *
     * @return Access token or null if not found
     */
    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    /**
     * Get the refresh token
     *
     * @return Refresh token or null if not found
     */
    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    /**
     * Get the token type
     *
     * @return Token type (usually "Bearer")
     */
    public String getTokenType() {
        return sharedPreferences.getString(KEY_TOKEN_TYPE, "Bearer");
    }

    /**
     * Get full authorization header value
     *
     * Educational Note: Most APIs expect the Authorization header in the format:
     * "Bearer {access_token}"
     *
     * @return Authorization header value or null if no token
     */
    public String getAuthorizationHeader() {
        String accessToken = getAccessToken();
        if (accessToken != null) {
            return getTokenType() + " " + accessToken;
        }
        return null;
    }

    /**
     * Check if user is currently logged in
     *
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) &&
                getAccessToken() != null;
    }

    /**
     * Check if the access token is expired
     *
     * Educational Note: This is a basic expiration check based on the
     * expiration time provided by the server. In a real app, you might
     * want to refresh the token automatically when it's about to expire.
     *
     * @return true if token is expired, false otherwise
     */
    public boolean isTokenExpired() {
        if (!isLoggedIn()) {
            return true;
        }

        long loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0);
        long expiresIn = sharedPreferences.getLong(KEY_EXPIRES_IN, 0);
        long currentTime = System.currentTimeMillis();

        // Convert expiresIn from seconds to milliseconds and check if expired
        return (currentTime - loginTime) > (expiresIn * 1000);
    }

    /**
     * Clear all stored tokens and logout user
     *
     * Educational Note: This method should be called when:
     * - User manually logs out
     * - Token refresh fails
     * - User wants to switch accounts
     */
    public void clearTokens() {
        editor.clear();
        editor.apply();
    }

    /**
     * Update only the access token (useful for token refresh)
     *
     * @param newAccessToken New access token
     * @param expiresIn New expiration time
     */
    public void updateAccessToken(String newAccessToken, long expiresIn) {
        long currentTime = System.currentTimeMillis();

        editor.putString(KEY_ACCESS_TOKEN, newAccessToken);
        editor.putLong(KEY_EXPIRES_IN, expiresIn);
        editor.putLong(KEY_LOGIN_TIME, currentTime);
        editor.apply();
    }

    /**
     * Get time remaining until token expires (in minutes)
     *
     * @return Minutes until expiration, or 0 if expired
     */
    public long getTokenExpiryMinutes() {
        if (!isLoggedIn()) {
            return 0;
        }

        long loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0);
        long expiresIn = sharedPreferences.getLong(KEY_EXPIRES_IN, 0);
        long currentTime = System.currentTimeMillis();

        long elapsedTime = currentTime - loginTime;
        long expirationTime = expiresIn * 1000; // Convert to milliseconds

        if (elapsedTime >= expirationTime) {
            return 0; // Already expired
        }

        long remainingTime = expirationTime - elapsedTime;
        return remainingTime / (1000 * 60); // Convert to minutes
    }
}