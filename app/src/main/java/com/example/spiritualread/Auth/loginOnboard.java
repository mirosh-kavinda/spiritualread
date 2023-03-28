package com.example.spiritualread.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spiritualread.Home.Homepage;
import com.example.spiritualread.R;

public class loginOnboard extends AppCompatActivity {

    Button b1,b2,b3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_onboard);

        b1=findViewById(R.id.btn1);
        b2=findViewById(R.id.btn2);
        b3=findViewById(R.id.btn3);

        b1.setOnClickListener(view -> startActivity(new Intent(loginOnboard.this,LoginActivity.class)));
        b2.setOnClickListener(view -> startActivity(new Intent(loginOnboard.this,RegisterActivity.class)));
        b3.setOnClickListener(view -> startActivity(new Intent(loginOnboard.this, Homepage.class)));
    }
}