package me.mirosh.spiritualread.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.mirosh.spiritualread.R;
import me.mirosh.spiritualread.adapters.AdapterFavourite;
import me.mirosh.spiritualread.databinding.ActivityProfileBinding;
import me.mirosh.spiritualread.model.ModelPdf;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding ;

    private ArrayList<ModelPdf>pdfArrayList;

    //adapter set in recycle view
    private AdapterFavourite adapterFavourite;
    private FirebaseAuth firebaseAuth;


    private static final String TAG="Profile_View";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();
        loadUserInfo();

        //handle click start
        binding.profEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,ProfileEditActivity.class));

            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading user info of user "+firebaseAuth.getUid());

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String email=""+snapshot.child("email").getValue();
                        String name=""+snapshot.child("name").getValue();
                        String profileImage=""+snapshot.child("profileImage").getValue();
                        String timestamp=""+snapshot.child("timestamp").getValue();
                        String uid= ""+snapshot.child("uid").getValue();
                        String userType=""+snapshot.child("userType").getValue();

                        loadFavBooks();
                        String formatedDate=MyApplication.formatTimestamp(Long.parseLong(timestamp));

                         binding.emailTv.setText(email);
                         binding.nameTv.setText(name);
                         binding.memberDateTv.setText(formatedDate);
                         binding.accountType.setText(userType);

                         try{
                             Glide.with(ProfileActivity.this)
                                     .load(profileImage)
                                     .placeholder(R.drawable.ic_person_white)
                                     .into(binding.profileIv);
                         }catch (Exception e){
                             Toast.makeText(ProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                         }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

                        adapterFavourite=new AdapterFavourite(ProfileActivity.this,pdfArrayList);
                        binding.booksRv.setAdapter(adapterFavourite);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}