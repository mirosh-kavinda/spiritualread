package me.mirosh.spiritualread.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import me.mirosh.spiritualread.Constants;
import me.mirosh.spiritualread.databinding.ActivityPdfViewBinding;


public class PdfViewActivity extends AppCompatActivity {


    // view binding
    private ActivityPdfViewBinding binding;

    private String bookId;

    private static final String TAG="PDF_VIEW_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPdfViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get bookId from that we passed in Inteent
        Intent intent =getIntent();
        bookId=intent.getStringExtra("bookId");
        Log.d(TAG, "onCreate: BookId : "+ bookId);

        loadBookDetails();


        //handle clikc goback
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Get Pdf URl...");
        //datbase reference to get book details
        // get book url using book id
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get book url
                        String pdfUrl=""+snapshot.child("url").getValue();
                        Log.d(TAG, "onDataChange: Pdf URL :" +pdfUrl);
                        //load pdf using that url from firebase storage
                        loadBooksFromUrl(pdfUrl);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void loadBooksFromUrl(String pdfUrl){


        StorageReference reference= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        MyApplication.incrementBookViewCount(bookId);
                        //load pdf using bytes
                        binding .pdfView.fromBytes(bytes)
                                .swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        //setcurrent and total pages in toolbar subtitle 
                                        int currentPage=(page+1);//do+1 becuause pag estarts from 0
                                        binding.toolbarSubtitleTv.setText(currentPage+"/"+pageCount);
                                        Log.d(TAG, "onPageChanged: "+currentPage+"/"+pageCount);
                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Log.d(TAG, "onError: "+t.getMessage());
                                        Toast.makeText(PdfViewActivity.this, "" +t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Log.d(TAG, "onPageError: "+t.getMessage());
                                        Toast.makeText(PdfViewActivity.this, "Error on Page"+page+ t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }).load();
                        binding.progressBar.setVisibility(View.GONE);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}