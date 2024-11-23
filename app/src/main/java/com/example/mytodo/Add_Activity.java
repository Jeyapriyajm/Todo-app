package com.example.mytodo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Add_Activity.java
public class Add_Activity extends AppCompatActivity {

    private EditText taskInput, descriptionInput;
    private Button addButton;
    private Databasehelper myDB;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        taskInput = findViewById(R.id.Task);
        descriptionInput = findViewById(R.id.discription);
        addButton = findViewById(R.id.button);

        myDB = new Databasehelper(this);

        // Get the logged-in user's ID
        SharedPreferences sharedPreferences = getSharedPreferences("MyTodoAppPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        addButton.setOnClickListener(v -> {
            String task = taskInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();

            if (!task.isEmpty() && !description.isEmpty() && userId != -1) {
                myDB.addTask(task, description, userId);  // Pass userId to the AddTask method

                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(Add_Activity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
