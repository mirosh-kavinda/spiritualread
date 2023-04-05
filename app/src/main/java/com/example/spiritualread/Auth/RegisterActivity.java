package com.example.spiritualread.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spiritualread.R;


public class RegisterActivity extends AppCompatActivity {
    EditText etRegEmail,  etRegPassword;
    TextView tvLoginHere;
    Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        tvLoginHere = findViewById(R.id.tvLoginHere);
        btnRegister = findViewById(R.id.btnRegister);



        btnRegister.setOnClickListener(view -> createUser());
        tvLoginHere.setOnClickListener(view-> startActivity(new Intent(RegisterActivity.this,LoginActivity.class)));
    }
    private  void createUser(){
        String email=etRegEmail.getText().toString();
        String password=etRegPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            etRegEmail.setError("Email cannot be empty");
            etRegEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
        }else{
//            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
//                if(task.isSuccessful()){
//                    Toast.makeText(RegisterActivity.this, "User Registered successfully", Toast.LENGTH_SHORT).show();
//                    startActivity((new Intent(RegisterActivity.this,LoginActivity.class)));
//            }else{
//                    Toast.makeText(RegisterActivity.this, "Registration Error"+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                }
//        });
        }
    }

}