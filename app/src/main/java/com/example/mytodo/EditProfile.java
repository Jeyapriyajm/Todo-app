package com.example.mytodo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class EditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText uploadName, uploadEmail;
    private ImageView uploadImage;
    private Button saveButton, deleteButton;

    private Databasehelper dbHelper;
    private int userId;
    private String profilePictureUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize UI components
        uploadName = findViewById(R.id.uploadName);
        uploadEmail = findViewById(R.id.uploadEmail);
        uploadImage = findViewById(R.id.uploadImage);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        dbHelper = new Databasehelper(this);

        // Get the logged-in user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyTodoAppPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing user data
        loadUserProfile();

        // Set click listener for the profile image
        uploadImage.setOnClickListener(v -> openImagePicker());

        // Save button logic
        saveButton.setOnClickListener(v -> saveProfile());

        // Delete profile logic
        deleteButton.setOnClickListener(v -> deleteProfile());
    }

    @SuppressLint("Range")
    private void loadUserProfile() {
        Cursor cursor = dbHelper.getUser(userId);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("username"));
            String email = cursor.getString(cursor.getColumnIndex("email"));
            profilePictureUri = cursor.getString(cursor.getColumnIndex("profile_picture"));

            uploadName.setText(name);
            uploadEmail.setText(email);

            if (profilePictureUri != null && !profilePictureUri.isEmpty()) {
                // Load the image using setImageURI instead of Glide
                Uri imageUri = Uri.parse(profilePictureUri);
                uploadImage.setImageURI(imageUri);
            } else {
                uploadImage.setImageResource(R.drawable.profile); // Default profile image
            }

        } else {
            Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Set the selected image URI to the ImageView
                uploadImage.setImageURI(imageUri);
                profilePictureUri = getPathFromUri(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    private void saveProfile() {
        String name = uploadName.getText().toString().trim();
        String email = uploadEmail.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = dbHelper.updateUser(userId, name, email, profilePictureUri);
        if (isUpdated) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditProfile.this, ProfileActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteProfile() {
        boolean isDeleted = dbHelper.deleteUser(userId);
        if (isDeleted) {
            Toast.makeText(this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();

            // Clear preferences and redirect to login
            SharedPreferences sharedPreferences = getSharedPreferences("MyTodoAppPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear().apply();

            startActivity(new Intent(EditProfile.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Failed to delete profile", Toast.LENGTH_SHORT).show();
        }
    }
}
