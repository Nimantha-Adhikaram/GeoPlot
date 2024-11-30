// SplashScreen2.java
package com.example.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash2);

        // Find the getStarted ImageView
        ImageView getStarted = findViewById(R.id.getStarted);

        // Set an onClick listener on the getStarted ImageView
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the DashboardActivity when clicked
                Intent intent = new Intent(SplashScreen2.this, DashboardActivity.class);
                startActivity(intent);
            }
        });
    }
}
