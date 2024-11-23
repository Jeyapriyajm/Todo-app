package com.example.mytodo;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Get reference to the ImageView
        ImageView animatedImageView = findViewById(R.id.animated_image);

        // Check if the drawable is an instance of AnimatedVectorDrawable
        AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) animatedImageView.getDrawable();

        // Start the animation
        if (animatedVectorDrawable != null) {
            animatedVectorDrawable.start();
        }
        new Handler().postDelayed(() -> {
            startActivity(new Intent(Splash.this, MainActivity2.class));
            finish();
        }, 3000); // Adjust the delay as needed


        // You may also add a delay here to transition to the next activity
    }
}
