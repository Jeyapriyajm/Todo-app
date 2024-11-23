package com.example.mytodo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class Databasehelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Todo.db";
    private static final int DATABASE_VERSION = 2;

    // Table and column names for tasks
    private static final String TABLE_TASKS = "Todo_Task";
    private static final String COLUMN_TASK_ID = "task_id";
    private static final String COLUMN_TASK_TITLE = "Task";
    private static final String COLUMN_TASK_DESCRIPTION = "Description";

    // Table and column names for users
    private static final String TABLE_USERS = "Users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PROFILE_PICTURE = "profile_picture"; // Profile picture URI

    public Databasehelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryUsers = "CREATE TABLE " + TABLE_USERS +
                " (" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PROFILE_PICTURE + " TEXT);"; // Changed to TEXT

        db.execSQL(queryUsers);

        String queryTasks = "CREATE TABLE " + TABLE_TASKS +
                " (" + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK_TITLE + " TEXT, " +
                COLUMN_TASK_DESCRIPTION + " TEXT, " +
                COLUMN_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE );";
        db.execSQL(queryTasks);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String alterTable = "ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_PROFILE_PICTURE + " TEXT"; // Corrected type to TEXT
            db.execSQL(alterTable);
        }
    }
    public boolean updateUser(int userId, String username, String email, String profilePictureUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PROFILE_PICTURE, profilePictureUri);

        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        return rowsUpdated > 0; // Return true if at least one row is updated
    }



    public long registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    public Cursor getUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        return cursor; // Caller must close the cursor
    }

    @SuppressLint("Range")
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int userId = -1;

        try {
            cursor = db.rawQuery("SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        return userId;
    }

    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isValid = false;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{username, password});
            isValid = cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
        }

        return isValid;
    }

    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }

    public void addTask(String task, String description, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, task);
        values.put(COLUMN_TASK_DESCRIPTION, description);
        values.put(COLUMN_USER_ID, userId);

        long result = db.insert(TABLE_TASKS, null, values);
        db.close();

        if (result == -1) {
            Toast.makeText(context, "Failed to Add Task", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Task Added Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllData(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_USER_ID + " = ? ORDER BY " + COLUMN_TASK_ID + " ASC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public void updateTask(String taskId, String task, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, task);
        values.put(COLUMN_TASK_DESCRIPTION, description);

        db.update(TABLE_TASKS, values, COLUMN_TASK_ID + "=?", new String[]{taskId});
        db.close();
    }

    public void deleteTask(String taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_TASK_ID + "=?", new String[]{taskId});
        db.close();
    }
    // Method to verify user credentials
    public boolean verifyUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, password});

        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;  // User found with matching username and password
        }

        if (cursor != null) {
            cursor.close();
        }
        return false;  // No matching user found
    }





}
