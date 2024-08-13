package com.example.vivify_technocrats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ImageView ic_back;
    private LinearLayout next_button;
    public static ArrayList<ModelClass> list;
    private int index = 0;
    private TextView card_question;
    private RadioGroup radioGroupOptions;
    private RadioButton radioOptionA, radioOptionB, radioOptionC, radioOptionD;
    private int correctCount = 0;
    private int wrongCount = 0;
    private boolean optionsEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Question");
        ic_back = findViewById(R.id.ic_back);
        next_button = findViewById(R.id.next_button);
        card_question = findViewById(R.id.card_question);
        radioGroupOptions = findViewById(R.id.radio_group_options);
        radioOptionA = findViewById(R.id.cardA);
        radioOptionB = findViewById(R.id.cardB);
        radioOptionC = findViewById(R.id.cardC);
        radioOptionD = findViewById(R.id.cardD);

        ic_back.setOnClickListener(v -> showExitConfirmationDialog());
        list = new ArrayList<>();
        retrieveQuestionsFromFirebase();

        next_button.setOnClickListener(v -> {
            if (radioGroupOptions.getCheckedRadioButtonId() == -1) {
                Toast.makeText(DashboardActivity.this, "Please select an answer before moving to the next question.", Toast.LENGTH_SHORT).show();
            } else {
                moveToNextQuestion();
                radioGroupOptions.clearCheck();
                enableOptions();
            }
        });

        radioGroupOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (optionsEnabled) {
                RadioButton selectedRadioButton = findViewById(checkedId);
                if (selectedRadioButton != null) {
                    checkAnswer(selectedRadioButton.getText().toString());
                }
            }
        });
    }

    private void retrieveQuestionsFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelClass modelClass = snapshot.getValue(ModelClass.class);
                    if (modelClass != null) {
                        list.add(modelClass);
                    }
                }
                setQuestion(index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashboardActivity.this, "Failed to load questions.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAnswer(String selectedAnswer) {
        optionsEnabled = false;
        if (index >= list.size()) return; // Avoid IndexOutOfBoundsException
        String correctAnswer = list.get(index).getAns();
        if (selectedAnswer.equals(correctAnswer)) {
            correctCount++;
        } else {
            wrongCount++;
        }
    }

    private void enableOptions() {
        optionsEnabled = true;
    }

    private void setQuestion(int index) {
        if (index >= list.size()) {
            gameWon();
            return;
        }
        ModelClass modelClass = list.get(index);
        card_question.setText(modelClass.getQuestion());
        radioOptionA.setText(modelClass.getqA());
        radioOptionB.setText(modelClass.getqB());
        radioOptionC.setText(modelClass.getqC());
        radioOptionD.setText(modelClass.getqD());
    }

    private void moveToNextQuestion() {
        if (index < list.size() - 1) {
            index++;
            setQuestion(index);
            animateNextButton();
        } else {
            gameWon();
        }
    }

    private void animateNextButton() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(500);
        next_button.startAnimation(fadeIn);
    }

    private void gameWon() {
        // Update local SQLite database
        DBHelper dbHelper = new DBHelper(DashboardActivity.this);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getUid();
            dbHelper.updateUserScores(username, correctCount, wrongCount);
        }
        Intent intent = new Intent(DashboardActivity.this, ResultActivity.class);
        intent.putExtra("correct", correctCount);
        intent.putExtra("wrong", wrongCount);
        startActivity(intent);
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
}
