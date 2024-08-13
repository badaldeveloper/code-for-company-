package com.example.vivify_technocrats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayingVideos extends AppCompatActivity {

    private static final String TAG = "PlayingVideos";
    private ImageView buttonback2;
    private YouTubePlayerView youTubePlayerView;
    private DBHelper dbHelper;
    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_videos);

        // Initialize components
        buttonback2 = findViewById(R.id.buttonback2);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        dbHelper = new DBHelper(this);

        // Get user data from Intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("name");
        userId = intent.getStringExtra("userId");

        Log.d(TAG, "userId: " + userId); // Log the userId

        buttonback2.setOnClickListener(v -> onBackPressed());

        // Load video URL and play video
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("videos").child("video1");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String videoUrl = dataSnapshot.getValue(String.class);
                if (videoUrl != null) {
                    playVideo(videoUrl);
                } else {
                    Toast.makeText(PlayingVideos.this, "Video URL not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PlayingVideos.this, "Failed to load video URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playVideo(final String videoUrl) {
        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(0).build();
        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = extractVideoIdFromUrl(videoUrl);
                youTubePlayer.loadVideo(videoId, 0);

                youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                        if (state == PlayerConstants.PlayerState.ENDED) {
                            updateVideoAttendance("yes");
                            navigateToMCQActivity();
                        }
                    }
                });
            }
        }, options);
    }

    private void updateVideoAttendance(final String status) {
        if (userId != null) {
            Log.d(TAG, "Updating video attendance for userId: " + userId + " with status: " + status);

            // Update Firebase first
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users2").child(userId);
            userRef.child("IsVideoAttended").setValue(status).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Update SQLite database
                    boolean isUpdated = dbHelper.updateVideoAttendance(userName, status); // Use userName as email
                    if (isUpdated) {
                        Log.d(TAG, "Video attendance updated successfully in SQLite to " + status);
                    } else {
                        Log.e(TAG, "Failed to update video attendance in SQLite");
                    }
                } else {
                    Log.e(TAG, "Failed to update video attendance in Firebase");
                }
            });
        } else {
            Log.e(TAG, "UserId is null in updateVideoAttendance()");
        }
    }

    private String extractVideoIdFromUrl(String url) {
        String videoId = null;
        String pattern = "^(?:https?:\\/\\/)?(?:www\\.|m\\.)?(?:youtube\\.com\\/watch\\?v=|youtu.be\\/)([a-zA-Z0-9-_]+)";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
    }

    private void navigateToMCQActivity() {
        Intent intent = new Intent(PlayingVideos.this, Go_for_mcq.class);
        intent.putExtra("userName", userName);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Update video attendance to "no" if the video was not completed
        if (userId != null) {
            updateVideoAttendance("no");
        }
    }
}
