package com.example.loginretrofit.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.loginretrofit.models.ApiErrorResponse;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Response;

/**
 * Network Utility Class
 *
 * This class provides utility methods for network operations
 * Including connectivity checks and error handling
 *
 * Educational Note: Utility classes contain static methods that provide
 * common functionality used throughout the application
 *
 * @author Han Vatana (PP50130)
 * @course Mobile Programming
 * @assignment Android Login with Retrofit
 */
public class NetworkUtils {

    /**
     * Check if device has internet connectivity
     *
     * Educational Note: This method checks if the device is connected to
     * a network (WiFi, Mobile data, etc.). It doesn't guarantee internet access,
     * but it's a good first check before making network requests.
     *
     * @param context Application context
     * @return true if connected to a network, false otherwise
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Parse error response from API
     *
     * Educational Note: When API calls fail, the server often returns
     * error details in JSON format. This method extracts those details
     * so we can show meaningful error messages to users.
     *
     * @param response The failed response from Retrofit
     * @return ApiErrorResponse object with error details
     */
    public static ApiErrorResponse parseErrorResponse(Response<?> response) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();

        try {
            if (response.errorBody() != null) {
                String errorBodyString = response.errorBody().string();

                // Try to parse as JSON error response
                Gson gson = new Gson();
                ApiErrorResponse parsedError = gson.fromJson(errorBodyString, ApiErrorResponse.class);

                if (parsedError != null && parsedError.getMessage() != null) {
                    return parsedError;
                }
            }
        } catch (IOException | com.google.gson.JsonSyntaxException e) {
            // If parsing fails, fall back to generic error
        }

        // Set default error message based on HTTP status code
        errorResponse.setCode(String.valueOf(response.code()));
        errorResponse.setMessage(getDefaultErrorMessage(response.code()));

        return errorResponse;
    }

    /**
     * Get user-friendly error message based on HTTP status code
     *
     * Educational Note: HTTP status codes have standard meanings:
     * - 400-499: Client errors (bad request, unauthorized, etc.)
     * - 500-599: Server errors (internal server error, etc.)
     *
     * @param statusCode HTTP status code
     * @return User-friendly error message
     */
    private static String getDefaultErrorMessage(int statusCode) {
        switch (statusCode) {
            case 400:
                return "Invalid request. Please check your input.";
            case 401:
                return "Invalid credentials. Please check your phone number and password.";
            case 403:
                return "Access denied. You don't have permission to perform this action.";
            case 404:
                return "Service not found. Please try again later.";
            case 408:
                return "Request timeout. Please check your internet connection.";
            case 422:
                return "Invalid data provided. Please check your input.";
            case 429:
                return "Too many requests. Please wait a moment and try again.";
            case 500:
                return "Server error. Please try again later.";
            case 502:
                return "Service temporarily unavailable. Please try again later.";
            case 503:
                return "Service unavailable. Please try again later.";
            case 504:
                return "Request timeout. Please try again later.";
            default:
                return "An unexpected error occurred. Please try again.";
        }
    }

    /**
     * Check if the error is a network connectivity issue
     *
     * @param throwable The error/exception that occurred
     * @return true if it's a connectivity issue, false otherwise
     */
    public static boolean isNetworkError(Throwable throwable) {
        return throwable instanceof IOException ||
                throwable.getMessage() != null &&
                        (throwable.getMessage().contains("Unable to resolve host") ||
                                throwable.getMessage().contains("timeout") ||
                                throwable.getMessage().contains("Connection refused"));
    }
}