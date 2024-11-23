package com.example.mytodo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private Button editProfileButton,back;
    private ImageView navImage;  // ImageView to display the profile picture

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        navImage = findViewById(R.id.navImage);
        TextView navName = findViewById(R.id.navName);
        TextView navEmail = findViewById(R.id.navEmail);
        editProfileButton = findViewById(R.id.editProfileButton);

        // Initialize database helper
        Databasehelper dbHelper = new Databasehelper(this);

        // Get the logged-in user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyTodoAppPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        if (userId != -1) {
            // Fetch user data from the database
            Cursor cursor = dbHelper.getUser(userId);
            if (cursor != null && cursor.moveToFirst()) {
                // Extract user details from the cursor
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("username"));
                @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex("email"));
                @SuppressLint("Range") String profilePictureUri = cursor.getString(cursor.getColumnIndex("profile_picture"));

                // Set name and email to the TextViews
                navName.setText(name);
                navEmail.setText(email);

                // Load and set the profile picture
                if (profilePictureUri != null && !profilePictureUri.isEmpty()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(profilePictureUri);
                    if (bitmap != null) {
                        navImage.setImageBitmap(bitmap);
                    } else {
                        navImage.setImageResource(R.drawable.profile);
                        Log.w("ProfileActivity", "Failed to decode profile picture. Using default.");
                    }
                } else {
                    navImage.setImageResource(R.drawable.profile);
                    Log.w("ProfileActivity", "Profile picture URI is null or empty. Using default.");
                }
            } else {
                navImage.setImageResource(R.drawable.profile);
                Toast.makeText(this, "No profile details found.", Toast.LENGTH_SHORT).show();
                Log.e("ProfileActivity", "Failed to fetch user details from the database.");
            }
            if (cursor != null) {
                cursor.close(); // Close the cursor to avoid memory leaks
            }
        } else {
            navImage.setImageResource(R.drawable.profile);
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            Log.e("ProfileActivity", "Invalid user ID from SharedPreferences.");
        }

        // Set up the edit profile button to navigate to the EditProfile activity
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
            startActivity(intent);
        });
    }


}
