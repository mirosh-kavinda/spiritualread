package com.example.spiritualread.Dashboards;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spiritualread.Auth.loginOnboard;
import com.example.spiritualread.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User extends AppCompatActivity {
    TextView Text1;
    Button Btn1;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


        Text1=findViewById(R.id.tv1);
        Btn1=findViewById(R.id.btn1);

        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();

        checkUser();

        //handle click , logout
        Btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

    }

    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null){
            //not logged in , goto main screen
            startActivity(new Intent(this, loginOnboard.class));

        }else {
            //logged in , get user info
            String email=firebaseUser.getEmail();
            //set in text view of toolbar
            Text1.setText(email);

        }
    }
    }
