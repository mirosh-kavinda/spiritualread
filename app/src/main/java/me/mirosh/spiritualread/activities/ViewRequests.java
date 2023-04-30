package me.mirosh.spiritualread.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.mirosh.spiritualread.Dashboards.Admin;
import me.mirosh.spiritualread.adapters.AdapterRequested;
import me.mirosh.spiritualread.databinding.ActivityViewRequestsBinding;
import me.mirosh.spiritualread.model.ModelRequested;

public class ViewRequests extends AppCompatActivity {


    //adapter set in recycle view
    private FirebaseAuth firebaseAuth;


    //array list to a store categories
    private ArrayList<ModelRequested> requestedArrayList;

    //adapter
    private AdapterRequested adapterCategory;
    private ActivityViewRequestsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityViewRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth=FirebaseAuth.getInstance();
        loadRequested();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ViewRequests.this, Admin.class));
                finish();
            }
        });

    }


    private void loadRequested() {
        //init array list
        requestedArrayList=new ArrayList<>();

        //get all categories from firebase >categories
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Requests");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear Array list before adding values
                requestedArrayList.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    //getData
                    ModelRequested model=ds.getValue(ModelRequested.class);

                    //add to array list
                    requestedArrayList.add(model);
                }

                //setup adapter
                adapterCategory =new AdapterRequested(ViewRequests.this, requestedArrayList);
                LinearLayoutManager manager = new LinearLayoutManager(ViewRequests.this);
                binding.requestedRv.setAdapter(adapterCategory);
                binding.requestedRv.setLayoutManager(manager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}