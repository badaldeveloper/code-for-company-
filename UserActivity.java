package com.example.vivify_technocrats;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserActivity extends AppCompatActivity {

    private TextView userNameTextView, dateTextView, timeTextView;
    private Button helmetYesButton, helmetNoButton, shoeYesButton, shoeNoButton, submitButton, backButton;
    private DBHelper DB;
    private String helmetStatus = "", shoeStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialize UI components
        userNameTextView = findViewById(R.id.userNameTextView);
        dateTextView = findViewById(R.id.dateTextView);
        timeTextView = findViewById(R.id.timeTextView);
        helmetYesButton = findViewById(R.id.helmetYesButton);
        helmetNoButton = findViewById(R.id.helmetNoButton);
        shoeYesButton = findViewById(R.id.shoeYesButton);
        shoeNoButton = findViewById(R.id.shoeNoButton);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
        DB = new DBHelper(this);

        // Receive data from LoginActivity
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        // Retrieve user details from the database
        Cursor cursor = DB.getUserData(username);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
            userNameTextView.setText("Name: " + name);
            cursor.close();
        } else {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
        }

        // Display current date and time
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        dateTextView.setText("Date: " + currentDate);
        timeTextView.setText("Time: " + currentTime);

        // Define colors for selected and unselected states
        int selectedColor = Color.parseColor("#FFEB3B"); // Yellow for selected
        int defaultColor = Color.parseColor("#FFFFFF"); // White for unselected

        // Set button click listeners
        helmetYesButton.setOnClickListener(view -> {
            helmetStatus = "yes";
            updateButtonState(helmetYesButton, helmetNoButton, selectedColor, defaultColor);
        });
        helmetNoButton.setOnClickListener(view -> {
            helmetStatus = "no";
            updateButtonState(helmetNoButton, helmetYesButton, selectedColor, defaultColor);
        });

        shoeYesButton.setOnClickListener(view -> {
            shoeStatus = "yes";
            updateButtonState(shoeYesButton, shoeNoButton, selectedColor, defaultColor);
        });
        shoeNoButton.setOnClickListener(view -> {
            shoeStatus = "no";
            updateButtonState(shoeNoButton, shoeYesButton, selectedColor, defaultColor);
        });

        submitButton.setOnClickListener(view -> {
            if (helmetStatus.isEmpty() || shoeStatus.isEmpty()) {
                Toast.makeText(UserActivity.this, "Please select helmet and shoe status", Toast.LENGTH_SHORT).show();
            } else {
                // Save details to the database
                boolean isUpdated = DB.insertUserDetails(username, currentDate, currentTime, helmetStatus, shoeStatus);
                if (isUpdated) {
                    Toast.makeText(UserActivity.this, "Data submitted successfully", Toast.LENGTH_SHORT).show();

                    // Navigate to PlayingVideos activity
                    Intent intentToPlayVideos = new Intent(UserActivity.this, PlayingVideos.class);
                    startActivity(intentToPlayVideos);
                } else {
                    Toast.makeText(UserActivity.this, "Error in submitting data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(view -> {
            Intent intentBack = new Intent(UserActivity.this, LoginActivity.class);
            startActivity(intentBack);
        });
    }

    private void updateButtonState(Button selectedButton, Button unselectedButton, int selectedColor, int defaultColor) {
        selectedButton.setBackgroundColor(selectedColor);
        unselectedButton.setBackgroundColor(defaultColor);
    }
}
