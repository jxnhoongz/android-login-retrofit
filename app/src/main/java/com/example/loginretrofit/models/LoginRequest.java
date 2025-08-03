package com.example.loginretrofit.models;

/**
 * Login Request Model
 *
 * This class represents the data structure for login requests
 * sent to the API endpoint /api/oauth/token
 *
 * Educational Note: This is a POJO (Plain Old Java Object) that will be
 * automatically converted to JSON by Retrofit using Gson converter
 *
 * API Endpoint: POST https://learn-api.cambofreelance.com/api/oauth/token
 *
 * @author Han Vatana (PP50130)
 * @course Mobile Programming
 * @assignment Android Login with Retrofit
 */
public class LoginRequest {

    /**
     * Phone number field for login
     * Note: The API uses "phoneNumber" as the username field
     */
    private String phoneNumber;

    /**
     * Password field for authentication
     */
    private String password;

    /**
     * Default constructor required for Gson serialization
     */
    public LoginRequest() {
    }

    /**
     * Constructor with parameters for easy object creation
     *
     * @param phoneNumber User's phone number (used as username)
     * @param password User's password
     */
    public LoginRequest(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    // Getter and Setter methods
    // These are required for Gson to properly serialize/deserialize the object

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Override toString for debugging purposes
     * Note: Never log passwords in production!
     */
    @Override
    public String toString() {
        return "LoginRequest{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", password='[HIDDEN]'" +
                '}';
    }
}