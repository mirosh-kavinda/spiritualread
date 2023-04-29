package me.mirosh.spiritualread.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import me.mirosh.spiritualread.Dashboards.UserExplore;
import me.mirosh.spiritualread.databinding.ActivityRegisterBinding;


public class RegisterActivity extends AppCompatActivity {

private ActivityRegisterBinding binding;

    //firebase Auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        //setup progress dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,loginOnboard.class));
                finish();
            }
        });

        binding.btnRegister.setOnClickListener(view -> createUser());
        binding.tvLoginHere.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
        }


    private String name="";
    private String email="";
    private String password="";


    private void createUser() {
        name = binding.etRegName.getText().toString().trim();
        email = binding.etRegEmail.getText().toString();
        password = binding.etRegPass.getText().toString();
        String cpassword = binding.etRegCPass.getText().toString();

        if (TextUtils.isEmpty(name)) {
            binding.etRegName.setError("Name must be filled");
            binding.etRegName.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegEmail.setError("invalid email pattern");
            binding.etRegEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            binding.etRegPass.setError("Password cannot be empty");
            binding.etRegPass.requestFocus();
        } else if (TextUtils.isEmpty(cpassword)) {
            binding.etRegCPass.setError("Confirm Password cannot be empty");
            binding.etRegCPass.requestFocus();
        } else if (!password.equals(cpassword)) {
            binding.etRegCPass.setError("mismatch with previous password");
            binding. etRegCPass.requestFocus();
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

            startActivity(new Intent(RegisterActivity.this, UserExplore.class));
            finish();
        })
        .addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}