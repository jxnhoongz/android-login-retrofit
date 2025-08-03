package com.example.loginretrofit.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.loginretrofit.models.LoginResponse;
import com.example.loginretrofit.repository.AuthRepository;
import com.example.loginretrofit.storage.TokenManager;

/**
 * Login ViewModel
 *
 * This class handles the business logic for the login screen
 * It communicates between the UI and the repository layer
 *
 * Educational Note: ViewModel is part of Android Architecture Components
 * Benefits of using ViewModel:
 * - Survives configuration changes (like screen rotation)
 * - Separates UI logic from business logic
 * - Provides a clean interface for the UI layer
 * - Manages UI-related data in a lifecycle-conscious way
 *
 * AndroidViewModel vs ViewModel:
 * - AndroidViewModel has access to Application context
 * - Use AndroidViewModel when you need context (for database, SharedPreferences, etc.)
 * - Use regular ViewModel when you don't need context
 *
 * @author Han Vatana (PP50130)
 * @course Mobile Programming
 * @assignment Android Login with Retrofit
 */
public class LoginViewModel extends AndroidViewModel {

    /**
     * Repository for handling authentication operations
     */
    private AuthRepository authRepository;

    /**
     * LiveData for form validation errors
     * Educational Note: Using separate LiveData objects allows the UI
     * to observe specific types of changes and react accordingly
     */
    private MutableLiveData<String> phoneNumberError = new MutableLiveData<>();
    private MutableLiveData<String> passwordError = new MutableLiveData<>();

    /**
     * LiveData for form fields (optional, for preserving data during config changes)
     */
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();

    /**
     * Constructor
     *
     * Educational Note: AndroidViewModel constructor receives Application
     * This gives us access to application context for dependencies
     *
     * @param application Application instance
     */
    public LoginViewModel(@NonNull Application application) {
        super(application);

        // Initialize repository with required dependencies
        TokenManager tokenManager = new TokenManager(application);
        authRepository = new AuthRepository(tokenManager);
    }

    /**
     * Perform login operation
     *
     * Educational Note: This method validates input, then delegates
     * the actual login operation to the repository. It returns LiveData
     * so the UI can observe the login progress and result.
     *
     * @param phoneNumber User's phone number
     * @param password User's password
     * @return LiveData containing login result
     */
    public LiveData<AuthRepository.AuthResult<LoginResponse>> login(String phoneNumber, String password) {
        // Validate input before making API call
        if (!validateInput(phoneNumber, password)) {
            // Return error result if validation fails
            MutableLiveData<AuthRepository.AuthResult<LoginResponse>> errorResult = new MutableLiveData<>();
            errorResult.setValue(AuthRepository.AuthResult.error("Please fix the errors and try again"));
            return errorResult;
        }

        // Clear any previous errors
        clearErrors();

        // Store current values (useful for config changes)
        this.phoneNumber.setValue(phoneNumber);
        this.password.setValue(password);

        // Delegate to repository
        return authRepository.login(phoneNumber, password);
    }

    /**
     * Logout current user
     *
     * @return LiveData containing logout result
     */
    public LiveData<AuthRepository.AuthResult<Void>> logout() {
        return authRepository.logout();
    }

    /**
     * Check if user is currently logged in
     *
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }

    /**
     * Validate login form input
     *
     * Educational Note: Client-side validation provides immediate feedback
     * and prevents unnecessary network calls. However, always validate
     * on the server side as well for security.
     *
     * @param phoneNumber Phone number to validate
     * @param password Password to validate
     * @return true if valid, false otherwise
     */
    private boolean validateInput(String phoneNumber, String password) {
        boolean isValid = true;

        // Validate phone number
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            phoneNumberError.setValue("Phone number is required");
            isValid = false;
        } else if (phoneNumber.trim().length() < 8) {
            phoneNumberError.setValue("Please enter a valid phone number");
            isValid = false;
        } else {
            phoneNumberError.setValue(null); // Clear error
        }

        // Validate password
        if (password == null || password.trim().isEmpty()) {
            passwordError.setValue("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordError.setValue("Password must be at least 6 characters");
            isValid = false;
        } else {
            passwordError.setValue(null); // Clear error
        }

        return isValid;
    }

    /**
     * Clear all validation errors
     */
    public void clearErrors() {
        phoneNumberError.setValue(null);
        passwordError.setValue(null);
    }

    /**
     * Clear form data (useful for logout or reset)
     */
    public void clearFormData() {
        phoneNumber.setValue("");
        password.setValue("");
        clearErrors();
    }

    // Getters for LiveData (UI will observe these)

    /**
     * Get phone number error LiveData
     * UI can observe this to show/hide error messages
     */
    public LiveData<String> getPhoneNumberError() {
        return phoneNumberError;
    }

    /**
     * Get password error LiveData
     * UI can observe this to show/hide error messages
     */
    public LiveData<String> getPasswordError() {
        return passwordError;
    }

    /**
     * Get phone number LiveData
     * Useful for preserving form data during configuration changes
     */
    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Get password LiveData
     * Useful for preserving form data during configuration changes
     */
    public LiveData<String> getPassword() {
        return password;
    }

    /**
     * Validate single field (for real-time validation)
     *
     * Educational Note: This allows for real-time validation as user types
     * Provides better user experience with immediate feedback
     *
     * @param fieldType Type of field (phone_number or password)
     * @param value Current field value
     */
    public void validateField(String fieldType, String value) {
        switch (fieldType) {
            case "phone_number":
                if (value == null || value.trim().isEmpty()) {
                    phoneNumberError.setValue("Phone number is required");
                } else if (value.trim().length() < 8) {
                    phoneNumberError.setValue("Please enter a valid phone number");
                } else {
                    phoneNumberError.setValue(null);
                }
                break;

            case "password":
                if (value == null || value.trim().isEmpty()) {
                    passwordError.setValue("Password is required");
                } else if (value.length() < 6) {
                    passwordError.setValue("Password must be at least 6 characters");
                } else {
                    passwordError.setValue(null);
                }
                break;
        }
    }
}