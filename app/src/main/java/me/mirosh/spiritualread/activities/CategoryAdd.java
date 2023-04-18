package me.mirosh.spiritualread.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import me.mirosh.spiritualread.databinding.ActivityCategoryAddBinding;

public class CategoryAdd extends AppCompatActivity {

    private ActivityCategoryAddBinding binding;


    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //init firebase auth
        firebaseAuth=FirebaseAuth.getInstance();


        //configure progresss dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //handle click , bhgin upload category
        binding.submit.setOnClickListener(v -> validateData());
        binding.backBtn.setOnClickListener(v -> onBackPressed());


    }

    private String category="";
    private void validateData() {
        //before add validate the data

        //get data
        category=binding.categoryEt.getText().toString().trim();
        
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
                    public void onFailure(@NonNull Exception e) {
//category add failed
                       progressDialog.dismiss();
                        Toast.makeText(CategoryAdd.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}