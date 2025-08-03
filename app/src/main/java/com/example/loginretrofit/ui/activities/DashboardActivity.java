package com.example.loginretrofit.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginretrofit.R;
import com.example.loginretrofit.databinding.ActivityDashboardBinding;
import com.example.loginretrofit.viewmodel.LoginViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Dashboard Activity
 *
 * This activity is displayed after successful login
 * Shows user information and provides logout functionality
 *
 * Educational Note: This activity demonstrates:
 * - Post-login user interface
 * - Token management and validation
 * - User session information display
 * - Logout functionality
 * - Navigation flow after authentication
 *
 * @author Han Vatana (PP50130)
 * @course Mobile Programming
 * @assignment Android Login with Retrofit
 */
public class DashboardActivity extends AppCompatActivity {

    /**
     * ViewBinding instance for type-safe view access
     */
    private ActivityDashboardBinding binding;

    /**
     * ViewModel instance for handling authentication logic
     */
    private LoginViewModel viewModel;

    /**
     * Called when the activity is first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Check authentication status
        checkAuthenticationStatus();

        // Set up UI
        setupUI();

        // Load user information
        loadUserInformation();
    }

    /**
     * Check if user is still authenticated
     * Redirect to login if not authenticated
     */
    private void checkAuthenticationStatus() {
        if (!viewModel.isLoggedIn()) {
            // User is not logged in, redirect to login
            navigateToLogin();
            finish();
        }
    }

    /**
     * Set up UI components and listeners
     */
    private void setupUI() {
        // Set up toolbar
//        setSupportActionBar(binding.toolbar);

        // Set up logout button
        binding.btnLogout.setOnClickListener(v -> performLogout());
    }

    /**
     * Load and display user information
     *
     * Educational Note: In a real app, this would typically
     * make an API call to get user profile information
     */
    private void loadUserInformation() {
        // Set welcome message
        // Note: In a real app, you would get the user's name from the API
        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String welcomeMessage = getString(R.string.welcome_user, "User");
        binding.tvWelcomeMessage.setText(welcomeMessage);

        // Set login time (current time as placeholder)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String loginTime = dateFormat.format(new Date());
        binding.tvLoginTime.setText(loginTime);

        // Set token status
        binding.tvTokenStatus.setText("Valid");
    }

    /**
     * Perform logout operation
     *
     * Educational Note: This method handles the complete logout flow:
     * 1. Show confirmation (in a real app)
     * 2. Call ViewModel to perform logout
     * 3. Clear stored tokens
     * 4. Navigate back to login screen
     */
    private void performLogout() {
        // In a real app, you might want to show a confirmation dialog

        // Perform logout via ViewModel
        viewModel.logout().observe(this, result -> {
            if (result.isSuccess()) {
                // Logout successful
                Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show();

                // Navigate to login screen
                navigateToLogin();

                // Finish dashboard activity
                finish();
            } else if (result.isError()) {
                // Logout failed (should rarely happen)
                Toast.makeText(this, "Logout failed: " + result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Navigate to login activity
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Clean up resources when activity is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    /**
     * Handle back button press
     *
     * Educational Note: In dashboard, back button minimizes the app
     * instead of going back to login (which wouldn't make sense)
     */
    @Override
    public void onBackPressed() {
        // Move task to back instead of finishing
        moveTaskToBack(true);
    }
}