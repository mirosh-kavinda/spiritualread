package com.example.spiritualread;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spiritualread.Dashboards.Admin;
import com.example.spiritualread.Dashboards.User;
import com.example.spiritualread.Onboarding.OnboardingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    int SPLASH_TIME = 3000; //This is 3 seconds
    ProgressBar splashProgress;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        //This is additional feature, used to run a progress bar
        splashProgress = findViewById(R.id.splashProgress);
        playProgress();

        //Code to start timer and take action after the timer ends
        new Handler(getMainLooper()).postDelayed(() -> {
            CheckUser();
        }, SPLASH_TIME);
    }
    private void CheckUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            //user not logged in
//            start main screen
            Intent mySuperIntent = new Intent(MainActivity.this, OnboardingActivity.class);
            startActivity(mySuperIntent);
            finish();
        } else {
            //user logged in check user type, same as done in login screen
            //check in db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
//                    get user type
                    String userType = (String) snapshot.child("userType").getValue();

                    //check user type
                    if (userType.equals("user")) {
                        //this is simple user , open user dashboard
                        startActivity(new Intent(MainActivity.this, User.class));
                        finish();
                    } else {
                        //this is admin , open user dashboard
                        startActivity(new Intent(MainActivity.this, Admin.class));
                        finish();
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
    }

    //Method to run progress bar for 5 seconds
    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(2000)
                .start();
    }
}

