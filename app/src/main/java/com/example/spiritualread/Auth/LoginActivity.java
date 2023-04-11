package com.example.spiritualread.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spiritualread.Dashboards.Admin;
import com.example.spiritualread.Dashboards.User;
import com.example.spiritualread.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText etLoginPassword,etLoginEmail;
    TextView tvRegisterHere;
    Button btnLogin;

//firebase auth
    private FirebaseAuth firebaseAuth;

//progress dialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //binding java object to UI elements
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPass);
        tvRegisterHere = findViewById(R.id.tvRegisterHere);
        btnLogin = findViewById(R.id.btnLogin);

        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();

        //setup progress dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        btnLogin.setOnClickListener(view ->ValidateUser());
        tvRegisterHere.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private String email="", password="";


    private void ValidateUser() {
        email = etLoginEmail.getText().toString();
         password = etLoginPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etLoginEmail.setError("Email cannot be empty");
            etLoginEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etLoginPassword.setError("Password cannot be empty");
            etLoginPassword.requestFocus();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etLoginEmail.setError("invalid email pattern");
            etLoginEmail.requestFocus();
        }else{
          loginUser();
        }
    }

    private void loginUser() {

        //show progress
        progressDialog.setMessage("Loggin in ..");
        progressDialog.show();

        //login user
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> {
            //login success,check if user is user or admin
            checkUser();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void checkUser() {
        progressDialog.setMessage("Checking User..");

        //check if user is user or admin from realtime datbase
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(Objects.requireNonNull(firebaseUser).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
//                get user type
                String userType = (String) snapshot.child("userType").getValue();

                //check user type
                if (Objects.equals(userType, "user")) {
                    //this is simple user , open user dashboard
                    startActivity(new Intent(LoginActivity.this, User.class));
                    finish();
                } else {
                    //this is admin ,open user dashboard
                    startActivity(new Intent(LoginActivity.this, Admin.class));
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}
