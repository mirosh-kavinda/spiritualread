package me.mirosh.spiritualread.activities;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import me.mirosh.spiritualread.Auth.loginOnboard;
import me.mirosh.spiritualread.R;
import me.mirosh.spiritualread.model.ConnectionReceiver;

public class MainActivity extends AppCompatActivity {
    int SPLASH_TIME = 3000; //This is 3 seconds
    ProgressBar splashProgress;

BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        broadcastReceiver = new ConnectionReceiver();


        //This is additional feature, used to run a progress bar
        splashProgress = findViewById(R.id.splashProgress);
        playProgress();
        registerNetworkBroadcast();
        //Code to start timer and take action after the timer ends
        new Handler(getMainLooper()).postDelayed(() -> {

            startActivity(new Intent(MainActivity.this, loginOnboard.class));

        }, SPLASH_TIME);

    }

        protected  void registerNetworkBroadcast(){
            registerReceiver(broadcastReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        protected void unregisterNetwork(){
            try {
                unregisterReceiver(broadcastReceiver);
            }catch (IllegalAccessError e){
                e.printStackTrace();
            }
        }
    protected void onDestroy(){
            super.onDestroy();
            unregisterNetwork();
        }

    //Method to run progress bar for 5 seconds
    private void playProgress() {
        ObjectAnimator.ofInt(splashProgress, "progress", 100)
                .setDuration(1000)
                .start();
    }
}

