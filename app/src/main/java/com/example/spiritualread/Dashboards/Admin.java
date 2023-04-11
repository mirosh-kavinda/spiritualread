package com.example.spiritualread.Dashboards;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spiritualread.Auth.loginOnboard;
import com.example.spiritualread.R;
import com.example.spiritualread.adapters.AdapterCategory;
import com.example.spiritualread.model.CategoryAdd;
import com.example.spiritualread.model.ModelCategory;
import com.example.spiritualread.model.PdfAddActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Admin extends AppCompatActivity {

    TextView Text1;
    Button Btn1,AddCat;
    RecyclerView   CategoryRV;
    EditText  searchEdt;
FloatingActionButton addPdf;
    //firebase auth
    private FirebaseAuth firebaseAuth;


    //array list to a store categories
    private ArrayList<ModelCategory> categoryArrayList;

    //adapter
    private AdapterCategory adapterCategory;

    ImageButton DelBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Text1=findViewById(R.id.tv1);
        Btn1=findViewById(R.id.btn1);
        AddCat=findViewById(R.id.addCategory);
        CategoryRV  =findViewById(R.id.recyclerview);
        searchEdt=findViewById(R.id.searchEt);
        addPdf=findViewById(R.id.addPdfFab);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();
        loadCategories();

        //edit text change listen , search
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            //called as and when user type each letter
                try {
                    adapterCategory.getFilter().filter(s);
                }catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //handle click , logout
        Btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });



         // handle click  start category add screen
AddCat.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       startActivity(new Intent(Admin.this, CategoryAdd.class));
       finish();
    }
});
        // handle click  start pdf add screen
        addPdf.setOnClickListener(new View.OnClickListener() {
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
                //setAdapter to recycler view
                CategoryRV.setAdapter(adapterCategory);
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
           Text1.setText(email);

        }
    }
}