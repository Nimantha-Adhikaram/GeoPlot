package com.example.splashscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class DashboardActivity extends AppCompatActivity {

    private boolean isDarkMode = false; // Default theme state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved theme state
        SharedPreferences preferences = getSharedPreferences("ThemePreferences", MODE_PRIVATE);
        isDarkMode = preferences.getBoolean("isDarkMode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_dashboard);

        // Theme toggle icon (ImageButton)
        ImageButton themeToggleButton = findViewById(R.id.themeToggleButton);
        themeToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTheme();
            }
        });

        // Set the click listener for the compass button
        ImageView compassButton = findViewById(R.id.compass);
        compassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the Compass activity
                Intent intent = new Intent(DashboardActivity.this, Compass.class);
                startActivity(intent);
            }
        });

        // Set the click listener for the shortest button
        ImageView shortestButton = findViewById(R.id.shortest);
        shortestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the Shortest activity
                Intent intent = new Intent(DashboardActivity.this, Shortest.class);
                startActivity(intent);
            }
        });

        // Set the click listener for the coordinates button
        ImageView coordinatesButton = findViewById(R.id.cordinates);
        coordinatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the CurrentMain activity
                Intent intent = new Intent(DashboardActivity.this, CurrentMain.class);
                startActivity(intent);
            }
        });

        // Set the click listener for the navigation button
        ImageView navigationButton = findViewById(R.id.navigation);
        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the Navigation activity
                Intent intent = new Intent(DashboardActivity.this, Navigation.class);
                startActivity(intent);
            }
        });

        // Set the click listener for the unit converter button
        ImageView unitButton = findViewById(R.id.unit);
        unitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the UnitConverter activity
                Intent intent = new Intent(DashboardActivity.this, UnitConverter.class);
                startActivity(intent);
            }
        });

        // Set the click listener for the area button
        ImageView areaButton = findViewById(R.id.area);
        areaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the Area activity
                Intent intent = new Intent(DashboardActivity.this, Area.class);
                startActivity(intent);
            }
        });

        // Set the click listener for the question mark button
        ImageButton questionButton = findViewById(R.id.questionButton);
        questionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog with the video
                showVideoDialog();
            }
        });
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode; // Toggle the mode

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Save theme state in preferences
        SharedPreferences.Editor editor = getSharedPreferences("ThemePreferences", MODE_PRIVATE).edit();
        editor.putBoolean("isDarkMode", isDarkMode);
        editor.apply();

        // Recreate the activity to apply the theme change
        recreate();
    }

    private void showVideoDialog() {
        // Create a dialog
        Dialog dialog = new Dialog(DashboardActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_video); // Custom dialog layout

        // Find the VideoView in the dialog layout
        VideoView videoView = dialog.findViewById(R.id.videoView);

        // Set the path to the video
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.full);
        videoView.setVideoURI(videoUri);

        // Add media controls to the VideoView
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // Start playing the video
        videoView.start();

        // Show the dialog
        dialog.show();
    }
}
