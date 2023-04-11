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

import androidx.appcompat.app.AppCompatActivity;

import com.example.spiritualread.Home.Homepage;
import com.example.spiritualread.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {

    EditText etRegEmail, etRegPassword, etRegCPassword, etRegName;
    TextView tvLoginHere;
    Button btnRegister;

    //firebase Auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        //setup progress dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //bind UI elements to java Objects
        etRegName = findViewById(R.id.etRegName);
        etRegCPassword = findViewById(R.id.etRegCPass);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPass);
        tvLoginHere = findViewById(R.id.tvLoginHere);
        btnRegister = findViewById(R.id.btnRegister);


        btnRegister.setOnClickListener(view -> createUser());
        tvLoginHere.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
        }


    private String name="", email="", password="", cpassword="";


    private void createUser() {
        name = etRegName.getText().toString().trim();
        email = etRegEmail.getText().toString();
        password = etRegPassword.getText().toString();
        cpassword = etRegCPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            etRegName.setError("Name must be filled");
            etRegName.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegEmail.setError("invalid email pattern");
            etRegEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            etRegPassword.setError("Password cannot be empty");
            etRegPassword.requestFocus();
        } else if (TextUtils.isEmpty(cpassword)) {
            etRegCPassword.setError("Confirm Password cannot be empty");
            etRegCPassword.requestFocus();
        } else if (!password.equals(cpassword)) {
            etRegCPassword.setError("mismatch with previous password");
            etRegCPassword.requestFocus();
        } else {
            createUserAccont();
        }
    }

    private void createUserAccont() {
    //show progress
    progressDialog.setMessage("Creating Account ...");
    progressDialog.show();

    //create user in firebase auth
    firebaseAuth.createUserWithEmailAndPassword(email,password)
    .addOnSuccessListener(authResult -> {

        //account creation success,now add in firebase realtime database
        progressDialog.dismiss();
        updateUserInfo();
    })
        .addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUserInfo() {

        progressDialog.setMessage("Saving user info ...");

        //timestamp
        long timestamp=System.currentTimeMillis();

        //get current user uid ,since user is registered so we can get now
        String uid=firebaseAuth.getUid();

        //setup data to add in db
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("email",email);
        hashMap.put("name",name);
        hashMap.put("timestamp",timestamp);
        hashMap.put("profileImage","");//for moment keep empty
        hashMap.put("userType","user");// possible value are admin and user/ make admin manually in the firebase realtime datbase by changeing this value

        //set data to db

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(Objects.requireNonNull(uid)).setValue(hashMap)
        .addOnSuccessListener(unused -> {
            Toast.makeText(RegisterActivity.this, "Account Created...", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(RegisterActivity.this, Homepage.class));
            finish();
        })
        .addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}