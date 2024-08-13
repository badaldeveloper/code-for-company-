package com.example.vivify_technocrats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    EditText username, name, password, email, branch, phoneNumber;
    Button btnRegister, btnSignin;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI elements
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        branch = findViewById(R.id.branch);
        phoneNumber = findViewById(R.id.phone_number);
        btnRegister = findViewById(R.id.btnsignup);
        btnSignin = findViewById(R.id.btnsignin);

        // Initialize database helper
        DB = new DBHelper(this);

        // Register button click listener
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String userName = name.getText().toString();
                String pass = password.getText().toString();
                String userEmail = email.getText().toString();
                String userBranch = branch.getText().toString();
                String userPhoneNumber = phoneNumber.getText().toString();
                String isVideoAttended = "no"; // Initial status

                if (user.equals("") || userName.equals("") || pass.equals("") || userEmail.equals("") || userBranch.equals("") || userPhoneNumber.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    boolean checkUser = DB.checkUsername(user);
                    if (!checkUser) {
                        boolean insert = DB.insertNewUser(user, userName, pass, userEmail, userBranch, userPhoneNumber, isVideoAttended);
                        if (insert) {
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Sign In button click listener
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
