package com.example.loginretrofit.network;

import com.example.loginretrofit.models.LoginRequest;
import com.example.loginretrofit.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * API Service Interface
 *
 * This interface defines all the API endpoints using Retrofit annotations
 * Retrofit will automatically implement this interface at runtime
 *
 * Educational Note: Retrofit uses annotations to define HTTP requests:
 * - @POST: HTTP POST method
 * - @Body: Request body (automatically converted to JSON)
 * - @Header: HTTP headers
 * - @Path: URL path parameters
 * - @Query: URL query parameters
 *
 * Base URL: https://learn-api.cambofreelance.com
 *
 * @author Han Vatana (PP50130)
 * @course Mobile Programming
 * @assignment Android Login with Retrofit
 */
public interface ApiService {

    /**
     * Login endpoint
     *
     * Endpoint: POST /api/oauth/token
     * Request Body: LoginRequest (phoneNumber, password)
     * Response: LoginResponse (accessToken, refreshToken)
     *
     * Educational Note:
     * - The @POST annotation specifies this is a POST request
     * - The endpoint path is relative to the base URL
     * - @Body tells Retrofit to serialize the LoginRequest object to JSON
     * - Call<T> is Retrofit's way of making asynchronous network calls
     *
     * @param loginRequest The login credentials (phone number and password)
     * @return Call object for executing the login request
     */
    @POST("api/oauth/token")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    /**
     * Refresh Token endpoint
     *
     * Endpoint: POST /api/oauth/refresh
     * This endpoint would be used to refresh expired access tokens
     *
     * Educational Note: This is prepared for future implementation
     * Most production apps need token refresh functionality
     *
     * @param refreshRequest Object containing the refresh token
     * @return Call object for executing the refresh request
     */
    @POST("api/oauth/refresh")
    Call<LoginResponse> refreshToken(@Body Object refreshRequest);

    /*
     * Additional endpoints can be added here as the application grows
     * Examples:
     *
     * @POST("api/oauth/register")
     * Call<RegisterResponse> register(@Body RegisterRequest request);
     *
     * @GET("api/user/profile")
     * Call<UserProfile> getUserProfile(@Header("Authorization") String authToken);
     *
     * @POST("api/user/logout")
     * Call<Void> logout(@Header("Authorization") String authToken);
     */
}