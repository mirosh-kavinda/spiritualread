package me.mirosh.spiritualread.Auth;

import android.content.Intent;
import android.os.Bundle;

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
import me.mirosh.spiritualread.Dashboards.User;
import me.mirosh.spiritualread.Onboarding.OnboardingActivity;
import me.mirosh.spiritualread.databinding.ActivityLoginOnboardBinding;

public class loginOnboard extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me.mirosh.spiritualread.databinding.ActivityLoginOnboardBinding binding = ActivityLoginOnboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        CheckUser();
        binding.btn1.setOnClickListener(view -> startActivity(new Intent(loginOnboard.this,LoginActivity.class)));
        binding.btn3.setOnClickListener(view -> startActivity(new Intent(loginOnboard.this, Guest.class)));
        binding.learnBtn.setOnClickListener(v -> startActivity(new Intent(loginOnboard.this, OnboardingActivity.class)));
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
                        startActivity(new Intent(loginOnboard.this, User.class));
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
