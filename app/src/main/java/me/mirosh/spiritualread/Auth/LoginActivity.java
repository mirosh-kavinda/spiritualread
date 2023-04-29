package me.mirosh.spiritualread.Auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import me.mirosh.spiritualread.Dashboards.Admin;
import me.mirosh.spiritualread.Dashboards.UserExplore;
import me.mirosh.spiritualread.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding= ActivityLoginBinding.inflate(getLayoutInflater());
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
            startActivity(new Intent(LoginActivity.this,loginOnboard.class));
            finish();
        }
    });
    binding.btnLogin.setOnClickListener(view ->ValidateUser());
    binding.tvRegisterHere.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private String email="", password="";


    private void ValidateUser() {
        email = binding.etLoginEmail.getText().toString();
         password = binding.etLoginPass.getText().toString();

        if (TextUtils.isEmpty(email)) {
            binding.etLoginEmail.setError("Email cannot be empty");
            binding.etLoginEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            binding.etLoginPass.setError("Password cannot be empty");
            binding.etLoginPass.requestFocus();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etLoginEmail.setError("invalid email pattern");
            binding.etLoginEmail.requestFocus();
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
            // get user type
            String userType = (String) snapshot.child("userType").getValue();

            //check user type
            if (Objects.equals(userType, "user")) {
                //this is simple user , open user dashboard
                startActivity(new Intent(LoginActivity.this, UserExplore.class));
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
