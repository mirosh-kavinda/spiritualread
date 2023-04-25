package me.mirosh.spiritualread.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.mirosh.spiritualread.Dashboards.User;
import me.mirosh.spiritualread.adapters.AdapterFavourite;
import me.mirosh.spiritualread.databinding.ActivityFavouriteBinding;
import me.mirosh.spiritualread.model.ModelPdf;

public class FavouriteActivity extends AppCompatActivity {
    private ArrayList<ModelPdf>pdfArrayList;

    //adapter set in recycle view
    private AdapterFavourite adapterFavourite;
    private FirebaseAuth firebaseAuth;

    private ActivityFavouriteBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding= ActivityFavouriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();

        loadFavBooks();
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(new Intent(FavouriteActivity.this, User.class));
             finish();

            }
        });



    }
    private void loadFavBooks(){
        pdfArrayList=new ArrayList<>();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favourites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();

                        for(DataSnapshot ds:snapshot.getChildren()){
                            String bookId =""+ds.child("bookId").getValue();

                            ModelPdf modelPdf=new ModelPdf();
                            modelPdf.setId(bookId);

                            pdfArrayList.add(modelPdf);
                        }

                        adapterFavourite=new AdapterFavourite(FavouriteActivity.this,pdfArrayList);
                        binding.booksRv.setAdapter(adapterFavourite);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}