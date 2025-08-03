package com.example.loginretrofit.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginretrofit.R;
import com.example.loginretrofit.databinding.ActivityLoginBinding;
import com.example.loginretrofit.models.LoginResponse;
import com.example.loginretrofit.network.NetworkUtils;
import com.example.loginretrofit.repository.AuthRepository;
import com.example.loginretrofit.viewmodel.LoginViewModel;

/**
 * Login Activity
 *
 * This activity provides the user interface for user authentication
 * Implements modern Android development practices with ViewBinding and MVVM
 *
 * Educational Note: This activity demonstrates:
 * - ViewBinding for type-safe view references
 * - ViewModel integration for business logic separation
 * - LiveData observation for reactive UI updates
 * - Material Design components for modern UI
 * - Proper error handling and user feedback
 * - Input validation with real-time feedback
 *
 * @author Han Vatana (PP50130)
 * @course Mobile Programming
 * @assignment Android Login with Retrofit
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * ViewBinding instance for type-safe view access
     * Educational Note: ViewBinding eliminates findViewById calls
     * and provides compile-time safety for view references
     */
    private ActivityLoginBinding binding;

    /**
     * ViewModel instance for handling business logic
     */
    private LoginViewModel viewModel;

    /**
     * Called when the activity is first created
     *
     * Educational Note: onCreate is part of the Activity lifecycle
     * This is where we initialize the UI and set up data binding
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        // Educational Note: ViewBinding generates a binding class for each layout
        // This provides direct references to all views with IDs
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        // Educational Note: ViewModelProvider ensures the ViewModel
        // survives configuration changes like screen rotation
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Check if user is already logged in
        checkLoginStatus();

        // Set up UI components
        setupUI();

        // Set up observers
        setupObservers();
    }

    /**
     * Check if user is already logged in and redirect if necessary
     *
     * Educational Note: This prevents users from seeing the login screen
     * if they're already authenticated
     */
    private void checkLoginStatus() {
        if (viewModel.isLoggedIn()) {
            navigateToDashboard();
            finish(); // Close login activity
        }
    }

    /**
     * Set up UI components and their listeners
     *
     * Educational Note: This method centralizes all UI setup
     * Making the code more organized and maintainable
     */
    private void setupUI() {
        // Set up login button click listener
        binding.btnLogin.setOnClickListener(v -> performLogin());

        // Set up forgot password click listener
        binding.tvForgotPassword.setOnClickListener(v -> {
            // TODO: Implement forgot password functionality
            Toast.makeText(this, "Forgot password feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Set up sign up click listener
        binding.tvSignUp.setOnClickListener(v -> {
            // TODO: Navigate to registration activity
            Toast.makeText(this, "Registration feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Set up real-time validation
        setupRealTimeValidation();
    }

    /**
     * Set up real-time input validation
     *
     * Educational Note: Real-time validation provides immediate feedback
     * Improves user experience by catching errors early
     */
    private void setupRealTimeValidation() {
        // Phone number field validation
        binding.etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate as user types
                viewModel.validateField("phone_number", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this implementation
            }
        });

        // Password field validation
        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Validate as user types
                viewModel.validateField("password", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this implementation
            }
        });
    }

    /**
     * Set up LiveData observers for reactive UI updates
     *
     * Educational Note: LiveData is lifecycle-aware and automatically
     * manages subscriptions based on the activity's lifecycle state
     */
    private void setupObservers() {
        // Observe phone number validation errors
        viewModel.getPhoneNumberError().observe(this, error -> {
            binding.tilPhoneNumber.setError(error);
            binding.tilPhoneNumber.setErrorEnabled(error != null);
        });

        // Observe password validation errors
        viewModel.getPasswordError().observe(this, error -> {
            binding.tilPassword.setError(error);
            binding.tilPassword.setErrorEnabled(error != null);
        });

        // Observe saved form data (for configuration changes)
        viewModel.getPhoneNumber().observe(this, phoneNumber -> {
            if (phoneNumber != null && !phoneNumber.equals(binding.etPhoneNumber.getText().toString())) {
                binding.etPhoneNumber.setText(phoneNumber);
            }
        });

        viewModel.getPassword().observe(this, password -> {
            if (password != null && !password.equals(binding.etPassword.getText().toString())) {
                binding.etPassword.setText(password);
            }
        });
    }

    /**
     * Perform login operation
     *
     * Educational Note: This method handles the complete login flow:
     * 1. Get user input
     * 2. Check network connectivity
     * 3. Call ViewModel to perform login
     * 4. Observe the result and update UI accordingly
     */
    private void performLogin() {
        // Get input values
        String phoneNumber = binding.etPhoneNumber.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Check network connectivity first
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showError("No internet connection. Please check your network and try again.");
            return;
        }

        // Clear any existing errors
        clearErrors();

        // Show loading state
        showLoading(true);

        // Perform login via ViewModel
        viewModel.login(phoneNumber, password).observe(this, result -> {
            // Hide loading state
            showLoading(false);

            if (result.isLoading()) {
                // Show loading state (already handled above)
                showLoading(true);
            } else if (result.isSuccess()) {
                // Login successful
                handleLoginSuccess(result.getData());
            } else if (result.isError()) {
                // Login failed
                handleLoginError(result.getMessage());
            }
        });
    }

    /**
     * Handle successful login
     *
     * Educational Note: This method is called when login is successful
     * It shows success feedback and navigates to the main app
     *
     * @param loginResponse The successful login response
     */
    private void handleLoginSuccess(LoginResponse loginResponse) {
        // Show success message
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

        // Navigate to dashboard
        navigateToDashboard();

        // Finish login activity
        finish();
    }

    /**
     * Handle login error
     *
     * Educational Note: This method provides user-friendly error feedback
     * Different error types can be handled differently if needed
     *
     * @param errorMessage The error message to display
     */
    private void handleLoginError(String errorMessage) {
        if (errorMessage != null) {
            showError(errorMessage);
        } else {
            showError(getString(R.string.error_unknown));
        }

        // Clear password field for security
        binding.etPassword.setText("");

        // Focus on phone number field for retry
        binding.etPhoneNumber.requestFocus();
    }

    /**
     * Show loading state
     *
     * Educational Note: Good UX practices include showing loading states
     * to inform users that something is happening
     *
     * @param isLoading true to show loading, false to hide
     */
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.btnLogin.setEnabled(false);
            binding.btnLogin.setText(getString(R.string.authenticating));
            binding.progressLoading.setVisibility(View.VISIBLE);
        } else {
            binding.btnLogin.setEnabled(true);
            binding.btnLogin.setText(getString(R.string.login));
            binding.progressLoading.setVisibility(View.GONE);
        }
    }

    /**
     * Show error message to user
     *
     * @param message Error message to display
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Clear all error messages
     */
    private void clearErrors() {
        binding.tilPhoneNumber.setError(null);
        binding.tilPassword.setError(null);
        binding.tilPhoneNumber.setErrorEnabled(false);
        binding.tilPassword.setErrorEnabled(false);
    }

    /**
     * Navigate to dashboard activity
     *
     * Educational Note: Intent is used to navigate between activities
     * FLAG_CLEAR_TOP ensures we don't create multiple instances
     */
    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Clean up resources when activity is destroyed
     *
     * Educational Note: Always clean up ViewBinding to prevent memory leaks
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    /**
     * Handle back button press
     *
     * Educational Note: Override back button to provide custom behavior
     * In login screen, we might want to minimize app instead of closing
     */
    @Override
    public void onBackPressed() {
        // Move task to back instead of finishing
        moveTaskToBack(true);
    }
}