package com.example.spiritualread.Home;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spiritualread.R;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

public class Homepage extends AppCompatActivity {
    Button btnlogout;
//    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

//
//            btnlogout=findViewById(R.id.btnLogout);
//            mAuth = FirebaseAuth.getInstance());
//            btnlogout.setOnClickListener(view->{
////                mAuth.signOut();
//                startActivity(new Intent(Homepage.this, loginOnboard.class));
//
//            });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseUser user =mAuth.getCurrentUser();
//        if(user==null){
//            startActivity(new Intent(Homepage.this,LoginActivity.class));
//        }
    }
}