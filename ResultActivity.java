package com.example.vivify_technocrats;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    Button goforLocation;
    Button userhistory;
    Button report;
    TextView textViewCorrectCount, textViewWrongCount;
    ImageView back, exit;

    private int correctCount;
    private int wrongCount;

    private DatabaseReference userScoresReference;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        goforLocation = findViewById(R.id.goforlocation);
        goforLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, googlemap.class);
                startActivity(intent);
            }
        });

        userhistory = findViewById(R.id.userhistory);
        report = findViewById(R.id.report);

        back = findViewById(R.id.ic_back2);
        exit = findViewById(R.id.ic_exitbutton3);

        userScoresReference = FirebaseDatabase.getInstance().getReference("UserScores");
        firestore = FirebaseFirestore.getInstance();

        // Get the correct and wrong counts from the Intent
        correctCount = getIntent().getIntExtra("correct", 0);
        wrongCount = getIntent().getIntExtra("wrong", 0);

        // Update scores in Firebase
        updateScoresInFirebase();

        userhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, UserHistoryActivity.class);
                intent.putExtra("url", "https://lethost-52.onrender.com/");
                startActivity(intent);
                finish();
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, UserHistoryActivity.class);
                intent.putExtra("url", "https://barstastusuploadshowing.onrender.com/");
                startActivity(intent);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, Go_for_mcq.class);
                startActivity(intent);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });

        TextView textView = findViewById(R.id.textView);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        textView.startAnimation(slideUpAnimation);
    }

    private void updateScoresInFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Retrieve user's name from Firestore
        firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String userName = document.getString("name");

                    // Update scores in Firebase Realtime Database
                    String sessionId = generateSessionId();
                    Map<String, Object> scoreData = new HashMap<>();
                    scoreData.put("correctCount", correctCount);
                    scoreData.put("wrongCount", wrongCount);
                    scoreData.put("userName", userName);

                    // Set the data under UserScores -> userId -> sessionId
                    userScoresReference.child(userId).child(sessionId).setValue(scoreData)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d("UpdateScores", "Scores updated successfully");
                                } else {
                                    Log.e("UpdateScores", "Failed to update scores", task1.getException());
                                }
                            });
                } else {
                    Log.d("UserInfo", "No such document");
                }
            } else {
                Log.d("UserInfo", "get failed with ", task.getException());
            }
        });
    }

    private String generateSessionId() {
        return String.valueOf(System.currentTimeMillis());
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
