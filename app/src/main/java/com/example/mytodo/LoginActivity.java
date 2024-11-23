package com.example.mytodo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private Databasehelper db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SharedPreferences and check if the user is already logged in
        sharedPreferences = getSharedPreferences("MyTodoAppPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");
        String profilePictureUri = sharedPreferences.getString("profile_picture", "");

        // Use this data to show the user profile
        if (isUserLoggedIn()) {
            navigateToMain(); // Skip login if already logged in
            return;
        }

        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login2);

        db = new Databasehelper(this);

        loginButton.setOnClickListener(v -> login());
    }

    // Method to check if the user is already logged in
    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify user credentials
        boolean isValid = db.verifyUser(username, password);

        if (isValid) {
            int userId = db.getUserId(username); // Get user ID by username

            // Save user ID and login status in SharedPreferences
            sharedPreferences.edit()
                    .putInt("userId", userId)
                    .putBoolean("isLoggedIn", true)
                    .apply();

            navigateToMain();  // Navigate to the main activity after login
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Close LoginActivity
    }
}
