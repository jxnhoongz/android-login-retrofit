package com.example.loginretrofit.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * API Client Singleton
 *
 * This class is responsible for creating and configuring the Retrofit instance
 * Implements Singleton pattern to ensure only one instance exists
 *
 * Educational Note:
 * - Singleton pattern ensures we don't create multiple Retrofit instances
 * - OkHttpClient handles the actual HTTP communication
 * - Gson converter automatically converts JSON to Java objects and vice versa
 * - Logging interceptor helps with debugging by showing network requests/responses
 *
 * @author Han Vatana (PP50130)
 * @course Mobile Programming
 * @assignment Android Login with Retrofit
 */
public class ApiClient {

    /**
     * Base URL for all API calls
     * Educational Note: Always use HTTPS in production for security
     */
    private static final String BASE_URL = "https://learn-api.cambofreelance.com/";

    /**
     * Singleton instance of ApiClient
     */
    private static ApiClient instance;

    /**
     * Retrofit instance for making API calls
     */
    private Retrofit retrofit;

    /**
     * Private constructor to prevent direct instantiation (Singleton pattern)
     */
    private ApiClient() {
        initializeRetrofit();
    }

    /**
     * Get singleton instance of ApiClient
     * Thread-safe implementation using synchronized keyword
     *
     * Educational Note: This is the standard way to implement thread-safe singleton
     *
     * @return ApiClient instance
     */
    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    /**
     * Initialize Retrofit with all necessary configurations
     *
     * Educational Note: This method sets up:
     * 1. HTTP logging for debugging
     * 2. Connection timeouts for reliability
     * 3. JSON converter for automatic serialization/deserialization
     */
    private void initializeRetrofit() {
        // Create HTTP logging interceptor for debugging
        // Educational Note: This shows all network requests/responses in the log
        // Should be disabled in production builds for security and performance
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create OkHttpClient with custom configuration
        // Educational Note: OkHttpClient is the underlying HTTP client used by Retrofit
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)  // Add logging for debugging
                .connectTimeout(30, TimeUnit.SECONDS)  // Connection timeout
                .readTimeout(30, TimeUnit.SECONDS)     // Read timeout
                .writeTimeout(30, TimeUnit.SECONDS);   // Write timeout

        // Build the OkHttpClient
        OkHttpClient httpClient = httpClientBuilder.build();

        // Create Retrofit instance
        // Educational Note: Retrofit is built on top of OkHttpClient
        // It provides a high-level interface for making HTTP requests
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)  // Set the base URL for all requests
                .client(httpClient)  // Use our configured OkHttpClient
                .addConverterFactory(GsonConverterFactory.create())  // JSON converter
                .build();
    }

    /**
     * Get the ApiService implementation
     *
     * Educational Note: Retrofit automatically creates an implementation
     * of the ApiService interface using dynamic proxy pattern
     *
     * @return ApiService instance for making API calls
     */
    public ApiService getApiService() {
        return retrofit.create(ApiService.class);
    }

    /**
     * Get the raw Retrofit instance
     * Useful for creating other service interfaces
     *
     * @return Retrofit instance
     */
    public Retrofit getRetrofit() {
        return retrofit;
    }

    /**
     * Update base URL if needed (for testing or different environments)
     *
     * Educational Note: This is useful for switching between development,
     * staging, and production environments
     *
     * @param newBaseUrl The new base URL
     */
    public void updateBaseUrl(String newBaseUrl) {
        // Recreate Retrofit with new base URL
        retrofit = retrofit.newBuilder()
                .baseUrl(newBaseUrl)
                .build();
    }
}