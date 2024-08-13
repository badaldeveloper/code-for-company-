package com.example.vivify_technocrats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user55.db";
    private static final int DATABASE_VERSION = 4; // Updated version number
    private static final String TABLE_USER = "user_details";
    private static final String TAG = "DBHelper"; // For logging

    // Column names
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_HELMET = "helmet";
    public static final String COLUMN_SHOE = "shoe";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_BRANCH = "branch";
    public static final String COLUMN_PHONE_NUMBER = "phone_number"; // New column
    public static final String COLUMN_VIDEO_ATTENDED = "IsVideoAttended"; // New column
    public static final String COLUMN_CORRECT_ANSWERS = "correctAnswers"; // New column
    public static final String COLUMN_WRONG_ANSWERS = "wrongAnswers"; // New column

    private Context context; // To access Firebase context

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_BRANCH + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_HELMET + " TEXT, " +
                COLUMN_SHOE + " TEXT, " +
                COLUMN_PHONE_NUMBER + " TEXT, " +
                COLUMN_VIDEO_ATTENDED + " TEXT, " +
                COLUMN_CORRECT_ANSWERS + " INTEGER, " +
                COLUMN_WRONG_ANSWERS + " INTEGER)"; // Include new columns
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COLUMN_DATE + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COLUMN_TIME + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COLUMN_HELMET + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COLUMN_SHOE + " TEXT");
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COLUMN_PHONE_NUMBER + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COLUMN_VIDEO_ATTENDED + " TEXT");
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COLUMN_CORRECT_ANSWERS + " INTEGER");
            db.execSQL("ALTER TABLE " + TABLE_USER + " ADD COLUMN " + COLUMN_WRONG_ANSWERS + " INTEGER");
        }
    }

    // Insert new user
    public boolean insertNewUser(String username, String name, String password, String email, String branch, String phoneNumber, String videoAttended) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_BRANCH, branch);
        values.put(COLUMN_PHONE_NUMBER, phoneNumber); // Include phone number
        values.put(COLUMN_VIDEO_ATTENDED, videoAttended); // Include video attendance
        values.put(COLUMN_CORRECT_ANSWERS, 0); // Initialize to 0
        values.put(COLUMN_WRONG_ANSWERS, 0); // Initialize to 0

        long result = db.insert(TABLE_USER, null, values);
        return result != -1; // Returns true if insertion is successful
    }

    // Retrieve user data based on username
    public Cursor getUserData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USER, null, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
    }

    // Insert user details into the database
    public boolean insertUserDetails(String username, String date, String time, String helmet, String shoe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_HELMET, helmet);
        values.put(COLUMN_SHOE, shoe);

        // Check if the user already exists in the database
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        if (cursor.getCount() > 0) {
            // Update existing user record
            int rowsAffected = db.update(TABLE_USER, values, COLUMN_USERNAME + "=?", new String[]{username});
            cursor.close();
            return rowsAffected > 0; // Returns true if rows were updated
        } else {
            cursor.close();
            // Insert new user record
            long result = db.insert(TABLE_USER, null, values);
            return result != -1; // Returns true if insertion is successful
        }
    }

    // Update user password
    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        int rowsAffected = db.update(TABLE_USER, values, COLUMN_USERNAME + "=?", new String[]{username});
        return rowsAffected > 0; // Returns true if rows were updated
    }

    // Update helmet status for a user
    public boolean updateHelmetStatus(String username, String helmetStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HELMET, helmetStatus);

        int rowsAffected = db.update(TABLE_USER, values, COLUMN_USERNAME + "=?", new String[]{username});
        return rowsAffected > 0; // Returns true if rows were updated
    }

    // Update shoe status for a user
    public boolean updateShoeStatus(String username, String shoeStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SHOE, shoeStatus);

        int rowsAffected = db.update(TABLE_USER, values, COLUMN_USERNAME + "=?", new String[]{username});
        return rowsAffected > 0; // Returns true if rows were updated
    }

    // Update video attendance status
    public boolean updateVideoAttendance(String username, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VIDEO_ATTENDED, status);

        int rowsAffected = db.update(TABLE_USER, values, COLUMN_USERNAME + "=?", new String[]{username});
        return rowsAffected > 0; // Returns true if rows were updated
    }

    // Update user scores
    public void updateUserScores(String userId, int correctCount, int wrongCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", userId);
        contentValues.put("correctCount", correctCount);
        contentValues.put("wrongCount", wrongCount);

        db.replace("UserScores", null, contentValues);
        db.close();
    }

    // Check username and password
    public boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{username, password}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check if username exists
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Insert data (username and password) into the database
    public boolean insertData(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USER, null, values);
        return result != -1; // Returns true if insertion is successful
    }

    // Fetch data from Firebase Realtime Database and update local SQLite database
    public void fetchAndUpdateUserData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userScoresRef = databaseReference.child("UserScores");
        DatabaseReference users2Ref = databaseReference.child("Users2");

        // Fetch UserScores data
        userScoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SQLiteDatabase db = getWritableDatabase();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    DataSnapshot scoreSnapshot = snapshot.child("scores");
                    int correctAnswers = scoreSnapshot.child("correctCount").getValue(Integer.class);
                    int wrongAnswers = scoreSnapshot.child("wrongCount").getValue(Integer.class);

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_CORRECT_ANSWERS, correctAnswers);
                    values.put(COLUMN_WRONG_ANSWERS, wrongAnswers);

                    int rowsAffected = db.update(TABLE_USER, values, COLUMN_USERNAME + "=?", new String[]{userId});
                    if (rowsAffected == 0) {
                        Log.e(TAG, "No user found with ID: " + userId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch UserScores data", databaseError.toException());
            }
        });

        // Fetch Users2 data
        users2Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SQLiteDatabase db = getWritableDatabase();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    DataSnapshot userSnapshot = snapshot.child("details");
                    String name = userSnapshot.child("name").getValue(String.class);
                    String date = userSnapshot.child("date").getValue(String.class);
                    String time = userSnapshot.child("time").getValue(String.class);
                    String helmet = userSnapshot.child("helmet").getValue(String.class);
                    String shoe = userSnapshot.child("shoe").getValue(String.class);
                    String phoneNumber = userSnapshot.child("phone_number").getValue(String.class);
                    String videoAttended = userSnapshot.child("IsVideoAttended").getValue(String.class);

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NAME, name);
                    values.put(COLUMN_DATE, date);
                    values.put(COLUMN_TIME, time);
                    values.put(COLUMN_HELMET, helmet);
                    values.put(COLUMN_SHOE, shoe);
                    values.put(COLUMN_PHONE_NUMBER, phoneNumber);
                    values.put(COLUMN_VIDEO_ATTENDED, videoAttended);

                    int rowsAffected = db.update(TABLE_USER, values, COLUMN_USERNAME + "=?", new String[]{userId});
                    if (rowsAffected == 0) {
                        Log.e(TAG, "No user found with ID: " + userId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch Users2 data", databaseError.toException());
            }
        });
    }
}
