package com.example.spiritualread.model;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spiritualread.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CategoryAdd extends AppCompatActivity {


 
    Button submitBtn;
    ImageButton backButton;
    EditText CatName;

    //firebase auth
    private FirebaseAuth firebaseAuth;

//progress dialog
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);

        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();

        //init binding
        submitBtn=findViewById(R.id.submit);
        CatName=findViewById(R.id.categoryEt);
        backButton=findViewById(R.id.backBtn);


        //configure progresss dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click , bhgin upload category
submitBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        validateData();
    }
});

backButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        onBackPressed();
    }
});


    }

    private String category="";
    private void validateData() {
        //before add validate the data

        //get data
        category=CatName.getText().toString().trim();
        
        //validate if note empty
        if(TextUtils.isEmpty(category)){
            Toast.makeText(this, "Please Enter the Category", Toast.LENGTH_SHORT).show();
        }else{
            addCategoryFirebase();
        }
    }

    private void addCategoryFirebase() {
        //show progresss
        progressDialog.setMessage("Adding Category...");
        progressDialog.show();

        //get timestamp
        long timestamp=System.currentTimeMillis();

        //setup infor to add in firebase db
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("category",""+category);
        hashMap.put("timestamp",timestamp);
        hashMap.put("uid",""+firebaseAuth.getUid());

        //add to firebase db ..... database root > Categories->category id->category-info
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//category added success
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAdd.this, "Category Added Succesfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
//category add failed
                       progressDialog.dismiss();
                        Toast.makeText(CategoryAdd.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}