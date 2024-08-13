package com.example.vivify_technocrats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Go_for_mcq extends AppCompatActivity {

    private DatabaseReference userActivityReference;
    private Button btnGoForMCQ;
    private String userId;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_for_mcq);

        btnGoForMCQ = findViewById(R.id.stylish_button);
        userActivityReference = FirebaseDatabase.getInstance().getReference().child("UserActivity");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        btnGoForMCQ.setOnClickListener(v -> checkMCQAttemptStatus());
    }

    private void checkMCQAttemptStatus() {
        String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());

        // Assuming you have a reference to the database where user activity is logged
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users2").child(userId);
        userReference.child("IsVideoAttended").setValue("yes").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Go_for_mcq.this, "MCQ activity started", Toast.LENGTH_SHORT).show();
                // Proceed with DashboardActivity
                Intent intent = new Intent(Go_for_mcq.this, DashboardActivity.class);
                startActivity(intent);
                // Optionally, finish the current activity
                finish();
            } else {
                Toast.makeText(Go_for_mcq.this, "Failed to update attendance status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
