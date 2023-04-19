package me.mirosh.spiritualread.activities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.mirosh.spiritualread.MyApplication;
import me.mirosh.spiritualread.databinding.ActivityPdfDetailBinding;

public class PdfDetailActivity extends AppCompatActivity {

    //view binding
    private ActivityPdfDetailBinding binding;

    //pdf id, get form intent
    String bookId,bookTitle,bookUrl;

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

        loadBookDetails();

        //increment book view ocount , whenever this page starts

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
                                binding.progressBar
                        );
                        MyApplication.LoadPdfSize(
                                ""+bookUrl,
                                ""+bookTitle,
                                binding.sizeTv
                        );

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


}