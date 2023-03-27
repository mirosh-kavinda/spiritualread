package com.example.spiritualread;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spiritualread.Auth.loginOnboard;

public class MainActivity extends AppCompatActivity {
    int SPLASH_TIME = 3000; //This is 3 seconds
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
            //Do any action here. Now we are moving to next page
            Intent mySuperIntent = new Intent(MainActivity.this, loginOnboard.class);
            startActivity(mySuperIntent);

            //This 'finish()' is for exiting the app when back button pressed from Home page which is ActivityHome
            finish();

        }, SPLASH_TIME);
    }

    //Method to run progress bar for 5 seconds
    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(2000)
                .start();
    }

}

