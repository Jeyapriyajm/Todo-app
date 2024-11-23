package com.example.mytodo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class updateActivity extends AppCompatActivity {

    private EditText task_input, desc_input;
    private Button update_button, delete_button;

    private String id, task, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        task_input = findViewById(R.id.Task1);
        desc_input = findViewById(R.id.discription1);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);

        getIntentData();

        // Set action bar title dynamically to match the task name
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Update: " + task);
        }

        // Update button click listener
        update_button.setOnClickListener(v -> {
            String updatedTask = task_input.getText().toString().trim();
            String updatedDescription = desc_input.getText().toString().trim();

            if (!updatedTask.isEmpty() && !updatedDescription.isEmpty()) {
                Databasehelper myDB = new Databasehelper(updateActivity.this);

                // Update the task with the new values
                myDB.updateTask(id, updatedTask, updatedDescription);
                myDB.close();

                Toast.makeText(updateActivity.this, "Task Updated Successfully!", Toast.LENGTH_SHORT).show();

                // Inform the calling activity of the success
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(updateActivity.this, "Please fill in both fields.", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete button click listener
        delete_button.setOnClickListener(v -> confirmDialog());
    }

    // Fetch the task details passed from the previous activity
    void getIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("task") && getIntent().hasExtra("description")) {
            id = getIntent().getStringExtra("id");
            task = getIntent().getStringExtra("task");
            description = getIntent().getStringExtra("description");

            task_input.setText(task);
            desc_input.setText(description);
        } else {
            Toast.makeText(this, "No data received!", Toast.LENGTH_SHORT).show();
        }
    }

    // Show a confirmation dialog before deleting the task
    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Task?");
        builder.setMessage("Are you sure you want to delete the task: " + task + "?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            Databasehelper myDB = new Databasehelper(updateActivity.this);
            myDB.deleteTask(id); // Delete the task from the database
            myDB.close();

            // Notify the previous activity that deletion was successful
            setResult(RESULT_OK);
            finish();  // Close this activity
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {
            // Do nothing on "No"
        });
        builder.create().show();
    }
}
