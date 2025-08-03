package com.example.loginretrofit.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.loginretrofit.models.ApiErrorResponse;
import com.example.loginretrofit.models.LoginRequest;
import com.example.loginretrofit.models.LoginResponse;
import com.example.loginretrofit.network.ApiClient;
import com.example.loginretrofit.network.ApiService;
import com.example.loginretrofit.network.NetworkUtils;
import com.example.loginretrofit.storage.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Authentication Repository
 *
 * This class acts as a single source of truth for authentication data
 * It handles all authentication-related API calls and data management
 *
 * Educational Note: Repository pattern provides a clean API for data access
 * It abstracts the data sources (network, database, cache) from the UI layer
 * ViewModels interact with repositories instead of directly with network/database
 *
 * Benefits of Repository Pattern:
 * - Centralized data access logic
 * - Easy to test (can mock the repository)
 * - Clean separation of concerns
 * - Easy to switch data sources
 *
 * @author Han Vatana (PP50130)
 * @course Mobile Programming
 * @assignment Android Login with Retrofit
 */
public class AuthRepository {

    /**
     * API service instance for making network calls
     */
    private ApiService apiService;

    /**
     * Token manager for storing/retrieving authentication tokens
     */
    private TokenManager tokenManager;

    /**
     * Constructor
     *
     * @param tokenManager Token manager instance
     */
    public AuthRepository(TokenManager tokenManager) {
        this.apiService = ApiClient.getInstance().getApiService();
        this.tokenManager = tokenManager;
    }

    /**
     * Perform login operation
     *
     * Educational Note: This method returns LiveData which allows the UI
     * to observe the result of the login operation. LiveData is lifecycle-aware
     * and automatically manages subscriptions.
     *
     * The method uses Retrofit's asynchronous callback mechanism to make
     * network calls without blocking the main thread.
     *
     * @param phoneNumber User's phone number
     * @param password User's password
     * @return LiveData containing the result of the login operation
     */
    public LiveData<AuthResult<LoginResponse>> login(String phoneNumber, String password) {
        MutableLiveData<AuthResult<LoginResponse>> result = new MutableLiveData<>();

        // Immediately return loading state
        result.setValue(AuthResult.loading());

        // Create login request object
        LoginRequest loginRequest = new LoginRequest(phoneNumber, password);

        // Make API call
        Call<LoginResponse> call = apiService.login(loginRequest);

        // Execute call asynchronously
        // Educational Note: enqueue() makes the call on a background thread
        // The callbacks (onResponse, onFailure) are called on the main thread
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Login successful
                    LoginResponse loginResponse = response.body();

                    // Save tokens for future use
                    tokenManager.saveTokens(
                            loginResponse.getAccessToken(),
                            loginResponse.getRefreshToken(),
                            loginResponse.getTokenType(),
                            loginResponse.getExpiresIn()
                    );

                    // Return success result
                    result.setValue(AuthResult.success(loginResponse));
                } else {
                    // Login failed - parse error response
                    ApiErrorResponse errorResponse = NetworkUtils.parseErrorResponse(response);
                    result.setValue(AuthResult.error(errorResponse.getUserFriendlyMessage()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Network error or other failure
                String errorMessage;

                if (NetworkUtils.isNetworkError(t)) {
                    errorMessage = "Network error. Please check your internet connection.";
                } else {
                    errorMessage = "Login failed. Please try again.";
                }

                result.setValue(AuthResult.error(errorMessage));
            }
        });

        return result;
    }

    /**
     * Logout user
     *
     * Educational Note: This method clears stored tokens and returns
     * a success result. In a more complex app, you might also want to
     * make an API call to invalidate the token on the server side.
     *
     * @return LiveData containing logout result
     */
    public LiveData<AuthResult<Void>> logout() {
        MutableLiveData<AuthResult<Void>> result = new MutableLiveData<>();

        // Clear stored tokens
        tokenManager.clearTokens();

        // Return success
        result.setValue(AuthResult.success(null));

        return result;
    }

    /**
     * Check if user is currently logged in
     *
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return tokenManager.isLoggedIn() && !tokenManager.isTokenExpired();
    }

    /**
     * Get current access token
     *
     * @return Access token or null if not logged in
     */
    public String getAccessToken() {
        if (isLoggedIn()) {
            return tokenManager.getAccessToken();
        }
        return null;
    }

    /**
     * Get authorization header for API calls
     *
     * @return Authorization header value or null if not logged in
     */
    public String getAuthorizationHeader() {
        if (isLoggedIn()) {
            return tokenManager.getAuthorizationHeader();
        }
        return null;
    }

    /**
     * Refresh access token (for future implementation)
     *
     * Educational Note: This method would be used to refresh expired
     * access tokens using the refresh token. Most production apps
     * implement automatic token refresh.
     *
     * @return LiveData containing refresh result
     */
    public LiveData<AuthResult<LoginResponse>> refreshToken() {
        MutableLiveData<AuthResult<LoginResponse>> result = new MutableLiveData<>();

        String refreshToken = tokenManager.getRefreshToken();
        if (refreshToken == null) {
            result.setValue(AuthResult.error("No refresh token available"));
            return result;
        }

        // TODO: Implement refresh token API call
        // This would make a call to /api/oauth/refresh endpoint

        result.setValue(AuthResult.error("Token refresh not implemented"));
        return result;
    }

    /**
     * AuthResult wrapper class
     *
     * Educational Note: This is a sealed class pattern that represents
     * the different states of an operation (loading, success, error)
     * It provides a clean way to handle async operations in the UI
     *
     * @param <T> Type of data returned on success
     */
    public static class AuthResult<T> {
        public enum Status {
            LOADING,
            SUCCESS,
            ERROR
        }

        private Status status;
        private T data;
        private String message;

        private AuthResult(Status status, T data, String message) {
            this.status = status;
            this.data = data;
            this.message = message;
        }

        public static <T> AuthResult<T> loading() {
            return new AuthResult<>(Status.LOADING, null, null);
        }

        public static <T> AuthResult<T> success(T data) {
            return new AuthResult<>(Status.SUCCESS, data, null);
        }

        public static <T> AuthResult<T> error(String message) {
            return new AuthResult<>(Status.ERROR, null, message);
        }

        // Getters
        public Status getStatus() { return status; }
        public T getData() { return data; }
        public String getMessage() { return message; }

        // Utility methods
        public boolean isLoading() { return status == Status.LOADING; }
        public boolean isSuccess() { return status == Status.SUCCESS; }
        public boolean isError() { return status == Status.ERROR; }
    }
}