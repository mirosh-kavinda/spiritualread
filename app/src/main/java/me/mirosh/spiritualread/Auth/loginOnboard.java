package me.mirosh.spiritualread.Auth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.mirosh.spiritualread.Dashboards.Admin;
import me.mirosh.spiritualread.Dashboards.Guest;
import me.mirosh.spiritualread.Dashboards.UserExplore;
import me.mirosh.spiritualread.Onboarding.OnboardingActivity;
import me.mirosh.spiritualread.R;
import me.mirosh.spiritualread.databinding.ActivityLoginOnboardBinding;


public class loginOnboard extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    WebView webView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me.mirosh.spiritualread.databinding.ActivityLoginOnboardBinding binding = ActivityLoginOnboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
//assign variable and initialize it
        webView=binding.webView;
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //initiallis connectivity manager
        ConnectivityManager connectivityManager= (ConnectivityManager)
        getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        //get active network info
        NetworkInfo networkInfo= connectivityManager.getActiveNetworkInfo();

        if (networkInfo==null ||!networkInfo.isConnected()||!networkInfo.isAvailable()){
            //when internet is inactive

            //initialize the dialog
            Dialog dialog=new Dialog(this);
            //set content
            dialog.setContentView(R.layout.alert_dialog_layout);
            //set outside touch
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().getAttributes().windowAnimations= android.R.style.Animation_Dialog;

            Button TryAgain =dialog.findViewById(R.id.btnTry);

            TryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
            dialog.show();

        }else{
            CheckUser();
            binding.btn1.setOnClickListener(view -> startActivity(new Intent(loginOnboard.this,LoginActivity.class)));
            binding.btn3.setOnClickListener(view -> startActivity(new Intent(loginOnboard.this, Guest.class)));
            binding.learnBtn.setOnClickListener(v -> startActivity(new Intent(loginOnboard.this, OnboardingActivity.class)));
        }




    }
    private void CheckUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser!=null) {
            //user  logged in
//            start main screen
            //user logged in check user type, same as done in login screen
            //check in db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    get user type
                    String userType = (String) snapshot.child("userType").getValue();

                    //check user type
                    assert userType != null;
                    if (userType.equals("user")) {
                        //this is simple user , open user dashboard
                        startActivity(new Intent(loginOnboard.this, UserExplore.class));
                        finish();
                    } else {
                        //this is admin , open user dashboard
                        startActivity(new Intent(loginOnboard.this, Admin.class));
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}
