package me.mirosh.spiritualread.Dashboards;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.mirosh.spiritualread.Auth.loginOnboard;
import me.mirosh.spiritualread.activities.CategoryAdd;
import me.mirosh.spiritualread.activities.PdfAddActivity;
import me.mirosh.spiritualread.adapters.AdapterCategory;
import me.mirosh.spiritualread.databinding.ActivityAdminBinding;
import me.mirosh.spiritualread.model.ModelCategory;

public class Admin extends AppCompatActivity {

 private ActivityAdminBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;


    //array list to a store categories
    private ArrayList<ModelCategory> categoryArrayList;

    //adapter
    private AdapterCategory adapterCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
        loadCategories();

        //edit text change listen , search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            //called as and when user type each letter
                try {
                    adapterCategory.getFilter().filter(s);
                }catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //handle click , logout
        binding.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });



         // handle click  start category add screen
binding.addCategory.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       startActivity(new Intent(Admin.this, CategoryAdd.class));
       finish();
    }
});
        // handle click  start pdf add screen
        binding.addPdfFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Admin.this, PdfAddActivity.class));
                finish();
            }
        });

    }
    private void loadCategories() {
        //init array list
        categoryArrayList=new ArrayList<>();

        //get all categories from firebase >categories
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            //clear Array list before adding values
                categoryArrayList.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    //getData
                    ModelCategory model=ds.getValue(ModelCategory.class);

                    //add to array list
                    categoryArrayList.add(model);
                }

                //setup adapter
                adapterCategory =new AdapterCategory(Admin.this, categoryArrayList);
                LinearLayoutManager manager = new LinearLayoutManager(Admin.this);
                binding.categoriesRv.setAdapter(adapterCategory);
                binding.categoriesRv.setLayoutManager(manager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
           binding.tv1.setText(email);

        }
    }
}