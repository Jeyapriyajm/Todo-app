package com.example.mytodo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button;
    private ImageView emptyImage;
    private TextView emptyText;
    private TextView emptySubText;

    BottomNavigationView bottomNavigationView;

    Databasehelper myDB;
    ArrayList<String> TaskID, TaskName, TaskDescription;
    CustomAdapter customAdapter;
    int userId;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is logged in; if not, redirect to LoginActivity
        SharedPreferences sharedPreferences = getSharedPreferences("MyTodoAppPrefs", MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();  // End MainActivity so it can't be accessed without login
            return;
        }

        setContentView(R.layout.activity_main);

        // Retrieve logged-in user ID
        userId = sharedPreferences.getInt("userId", -1);

        // Initialize views
        initializeViews();

        // Set up the RecyclerView and BottomNavigationView
        setupRecyclerView();
        setupBottomNavigationView();

        // Display initial data
        displayData();
    }

    // Initialize views
    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        add_button = findViewById(R.id.Add_Button);
        emptyImage = findViewById(R.id.empty_image);
        emptyText = findViewById(R.id.empty_text);
        emptySubText = findViewById(R.id.empty_subtext);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        myDB = new Databasehelper(MainActivity.this);

        TaskID = new ArrayList<>();
        TaskName = new ArrayList<>();
        TaskDescription = new ArrayList<>();

        // Set up FloatingActionButton to open Add_Activity when clicked
        add_button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Add_Activity.class);
            startActivityForResult(intent, 1); // Start Add_Activity and expect result
        });
    }

    // Set up RecyclerView
    private void setupRecyclerView() {
        customAdapter = new CustomAdapter(MainActivity.this, this, TaskID, TaskName, TaskDescription);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        // Apply layout animation
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fade_slide);
        recyclerView.setLayoutAnimation(controller);
    }

    // Set up BottomNavigationView
    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_index) {
                Toast.makeText(MainActivity.this, "Index", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.navigation_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_index);
    }

    // Fetch data from the database and update the RecyclerView
    private void displayData() {
        Cursor cursor = myDB.readAllData(userId);
        TaskID.clear();
        TaskName.clear();
        TaskDescription.clear();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                TaskID.add(cursor.getString(0));
                TaskName.add(cursor.getString(1));
                TaskDescription.add(cursor.getString(2));
            }
        }
        cursor.close();
        customAdapter.notifyDataSetChanged();
        checkIfListIsEmpty();
    }

    // Check if the task list is empty and update the UI accordingly
    private void checkIfListIsEmpty() {
        if (customAdapter.getItemCount() == 0) {
            emptyImage.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            emptySubText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyImage.setVisibility(View.GONE);
            emptyText.setVisibility(View.GONE);
            emptySubText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Refresh the task list when returning from Add_Activity
    @Override
    protected void onResume() {
        super.onResume();
        displayData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            displayData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu); // Inflate the main_menu XML
        return true;
    }




    // Logout function
    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyTodoAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false); // Clear isLoggedIn flag
        editor.apply();

        // Navigate back to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
