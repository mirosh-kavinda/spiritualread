package me.mirosh.spiritualread.activities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import me.mirosh.spiritualread.R;
import me.mirosh.spiritualread.adapters.AdapterComment;
import me.mirosh.spiritualread.databinding.ActivityPdfDetailBinding;
import me.mirosh.spiritualread.databinding.DialogCommentAddBinding;
import me.mirosh.spiritualread.model.ModelComments;


public class PdfDetailActivity extends AppCompatActivity {

    //view binding
    private ActivityPdfDetailBinding binding;

    //pdf id, get form intent
    String bookId="";
    String bookTitle="";
    String bookUrl="";

    //arraylist for hlde comment
    private ArrayList<ModelComments> commentsArrayList;

    //array adapter for hold commen
    private AdapterComment adapterComment;

    private ProgressDialog progressDialog;
    boolean IsInMyFavourite=false;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get data from intent e.g bookId
        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");

        //at start hide download button,
        binding.downloadBookBtn.setVisibility(View.GONE);

        //initialize progress dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            checkIsFavorite();
        }
        loadBookDetails();
        loadComments();

binding.addCommentBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(firebaseAuth.getCurrentUser()==null){
            Toast.makeText(PdfDetailActivity.this, "You 're not loged in..", Toast.LENGTH_SHORT).show();
        }else{
            addCommentDialog();
        }
    }
});

        //handle click ,go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();

            }
        });

        //handle click read pdf
        binding.readBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PdfDetailActivity.this, PdfViewActivity.class);
                intent1.putExtra("bookId", bookId);
                startActivity(intent1);
            }
        });

        //handle click download pdf
        binding.downloadBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Checking permission ");
                int result = ContextCompat.checkSelfPermission(PdfDetailActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                //not support above android 10 version to write storage
                if (result == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onClick: Permission already granted, can download book");
                    MyApplication.downloadBook(PdfDetailActivity.this,""+bookId,""+bookTitle,""+bookUrl);
                }else{
                    Log.d(TAG, "onClick: Persmission was not granted");
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        //handle click favorite
        binding.favoriteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(firebaseAuth.getCurrentUser()==null){
                    Toast.makeText(PdfDetailActivity.this, "You're not logged in ", Toast.LENGTH_SHORT).show();
                }else{
                    if(IsInMyFavourite){
                        MyApplication.removeFromFavorite(PdfDetailActivity.this,bookId);
                    }else{
                        MyApplication.addToFavorite(PdfDetailActivity.this,bookId);
                    }
                }
            }
        });
    }

    private void loadComments() {
        //init arraylist
        commentsArrayList=new ArrayList<>();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments").orderByChild(Objects.requireNonNull(firebaseAuth.getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear arraylist
                        commentsArrayList.clear();

                        for(DataSnapshot ds:snapshot.getChildren()) {
                            //get data as model,
                            ModelComments model = ds.getValue(ModelComments.class);
                            //add to arraylist
                            commentsArrayList.add(model);
                        }
                        //setup adapter
                        adapterComment=new AdapterComment(PdfDetailActivity.this,commentsArrayList);
                        //set adapter to recyclerview
                        binding.commentRv.setAdapter(adapterComment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private String comment="";

    private void addCommentDialog() {
        //inflate bind view
        DialogCommentAddBinding commentAddBinding=DialogCommentAddBinding.inflate(LayoutInflater.from(this));
        //set alert dialog builder
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(commentAddBinding.getRoot());

        //create add show alert dialog
        AlertDialog alertDialog=builder.create();
        alertDialog.show();

        //handle click add comment
        commentAddBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        //handle click , add commentc
        commentAddBinding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data
                comment= Objects.requireNonNull(commentAddBinding.commentEt.getText()).toString().trim();
                
                //validate data 
                if(TextUtils.isEmpty(comment)){
                    Toast.makeText(PdfDetailActivity.this, "Comment can't be empty", Toast.LENGTH_SHORT).show();

                }
                else{
                    alertDialog.dismiss();
                    addComment();
                }
            }
        });
    }

    private void addComment() {
        //show progress
        progressDialog.setMessage("Adding Comment...");
        progressDialog.show();

        //timestamp for comment id, commetn tme
        String timestamp=""+ System.currentTimeMillis();

        //setup data to add in db for cmment
        HashMap<String, Object>hashMap=new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("bookId",""+bookId);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("comment",""+comment);
        hashMap.put("uid",""+firebaseAuth.getUid());

        //db patht o add data into it
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).child("Comments").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PdfDetailActivity.this, "Comment Added ...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(PdfDetailActivity.this, "Error encounter :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if(isGranted){
            Log.d(TAG, "Permission Granted ");
            MyApplication.downloadBook(this,""+bookId,""+bookTitle,""+bookUrl);
        }
        else{
            Log.d(TAG, "Permission wad denied... ");
            Toast.makeText(this, "Permission wad denied...", Toast.LENGTH_SHORT).show();
        }
        
    });




    private void loadBookDetails() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        bookTitle=""+snapshot.child("title").getValue();
                        String description=""+snapshot.child("description").getValue();
                        String categoryId=""+snapshot.child("categoryId").getValue();
                        String viewsCount=""+snapshot.child("viewsCount").getValue();
                        String downloadsCount=""+snapshot.child("downloadsCount").getValue();
                        bookUrl=""+snapshot.child("url").getValue();
                        String timestamp =""+snapshot.child("timestamp").getValue();

                        //requireined data is loaded , show donwnload button
                        binding.downloadBookBtn.setVisibility(View.VISIBLE);


                        //format date
                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.LoadCategory(
                                ""+categoryId,
                                binding.categoryTv
                        );
                        MyApplication.LoadPdfFromUrlSinglePage(
                                ""+bookUrl,
                                ""+bookTitle,
                                binding.pdfView,
                                binding.progressBar,
                                binding.pagesTv
                        );
                        MyApplication.LoadPdfSize(
                                ""+bookUrl,
                                ""+bookTitle,
                                binding.sizeTv
                        );
                        MyApplication.loadPdfPageCount(
                                PdfDetailActivity.this,
                                ""+bookUrl,
                                binding.pagesTv);

                        //set data
                        binding.titleTv.setText(bookTitle);
                        binding.decriptionTv.setText(description);
                        binding.viewTv.setText(viewsCount.replace("null",viewsCount));
                        binding.downloadsTv.setText(downloadsCount.replace("null",downloadsCount));
                        binding.dateTv.setText(date);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkIsFavorite(){
        //loged in
        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Users");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Favourites").child(bookId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        IsInMyFavourite=snapshot.exists();

                        if(IsInMyFavourite){
                            //exist in favorite
                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_white,0,0);
                            binding.favoriteBtn.setText("Remove Favourite");
                        }else{
                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_border_white,0,0);
                            binding.favoriteBtn.setText("Add Favourite");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}