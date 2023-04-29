package me.mirosh.spiritualread.activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import me.mirosh.spiritualread.Auth.loginOnboard;
import me.mirosh.spiritualread.R;

public class MainActivity extends AppCompatActivity {
    int SPLASH_TIME = 1000; //This is 3 seconds
    ProgressBar splashProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This is additional feature, used to run a progress bar
        splashProgress = findViewById(R.id.splashProgress);
        playProgress();

        //Code to start timer and take action after the timer ends
        new Handler(getMainLooper()).postDelayed(() -> {

            startActivity(new Intent(MainActivity.this, loginOnboard.class));
            finish();

        }, SPLASH_TIME);

    }

    protected void onDestroy(){
            super.onDestroy();
            finish();

        }

    //Method to run progress bar for 5 seconds
    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(1000)
                .start();
    }
}

