package com.example.loginretrofit.models;

/**
 * API Error Response Model
 *
 * This class represents error responses from the API
 * Used for handling various error scenarios with proper error messages
 *
 * Educational Note: Not all APIs return the same error format
 * This class provides a flexible structure for common error patterns
 *
 * @author Han Vatana (PP50130)
 * @course Mobile Programming
 * @assignment Android Login with Retrofit
 */
public class ApiErrorResponse {

    /**
     * Error message from the server
     */
    private String message;

    /**
     * Error code (could be HTTP status code or custom error code)
     */
    private String code;

    /**
     * Detailed error information (optional)
     */
    private String details;

    /**
     * Timestamp of the error (optional)
     */
    private String timestamp;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }

    // Getter and Setter methods

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get user-friendly error message
     * Falls back to generic message if server message is empty
     */
    public String getUserFriendlyMessage() {
        if (message != null && !message.trim().isEmpty()) {
            return message;
        }
        return "An error occurred. Please try again.";
    }

    @Override
    public String toString() {
        return "ApiErrorResponse{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", details='" + details + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}