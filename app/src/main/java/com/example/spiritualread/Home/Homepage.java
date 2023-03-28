package com.example.spiritualread.Home;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spiritualread.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

public class Homepage extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
//    Button btnlogout;
    BottomNavigationView bottomNavigationView;

    //    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.person);
    }
   message firstFragment = new message();
    overview secondFragment = new overview();
   person thirdFragment = new person();


    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.person:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, firstFragment).commit();
                return true;

            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, secondFragment).commit();
                return true;

            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, thirdFragment).commit();
                return true;
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
//
//            btnlogout=findViewById(R.id.btnLogout);
//            mAuth = FirebaseAuth.getInstance());
//            btnlogout.setOnClickListener(view->{
////                mAuth.signOut();
//                startActivity(new Intent(Homepage.this, loginOnboard.class));
//
//            });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user =mAuth.getCurrentUser();
//        if(user==null){
//            startActivity(new Intent(Homepage.this,LoginActivity.class));
//        }
