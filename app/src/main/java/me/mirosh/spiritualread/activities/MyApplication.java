package me.mirosh.spiritualread.activities;

import static me.mirosh.spiritualread.Constants.MAX_BYTES_PDF;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import me.mirosh.spiritualread.Constants;

//application class runs before your launcher acticity
public class MyApplication extends Application {
    private static final String TAG_DOWNLOAD = "DOWNLOAD_PDF_TAG";

    @Override
    public void onCreate() {
        super.onCreate();
    }



//created method to convert timestamp into proper date format , so we can use it everywhere ,
    public  static final String formatTimestamp(long timestamp){
        Calendar cal =Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);

        //format timestamp to dd.mm/yyyy
        return DateFormat.format("dd/MM/yyyy",cal).toString();
    }

   public static void deleteBook(Context context,String bookId,String bookTitle,String bookUrl) {
        String TAG="DELETE_BOOK_TAG";


        Log.d(TAG, "deleteBook: Deleting...");
       ProgressDialog progressDialog=new ProgressDialog(context);
       progressDialog.setMessage("Please Wait..");
        progressDialog.setMessage("Deleting "+bookTitle+"...");//e.. Deleteing Book ABC ...
        progressDialog.show();

        Log.d(TAG, "deleteBook: Deleting from Storage .. ");
        StorageReference ref= FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Deleted from storagte");
                        Log.d(TAG, "onSuccess: Now deleting info from db ");

                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Books");
                        reference.child(bookId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: Deleted from db too");
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Book Deleted Succefully ...", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Failde to delete from db due to "+e.getMessage());
                                        progressDialog.dismiss();
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to delete from storage due to "+e.getMessage());


                    }
                });
    }

    public static void LoadPdfSize(String pdfUrl, String pdfTitle, TextView sizeTv) {

        String TAG="PDF_SIZE_TAG";
        //using url we can get file and its metadata from firebase storage

        StorageReference ref= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        //                        get size in bytes
                        double bytes=storageMetadata.getSizeBytes();
                        Log.d(TAG,"onSuccess:"+pdfTitle+" "+bytes);

                        //conver bytes into KB
                        double kb=bytes/1024;
                        double mb=kb/1024;
                        if(mb>=1){
                            sizeTv.setText(String.format("%.2f",mb)+"MB");

                        }else if(kb>=1){
                            sizeTv.setText(String.format("%.2f",kb)+"KB");
                        }else{
                            sizeTv.setText(String.format("%.2f",bytes)+"Bytes");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to getting metada
                        Log.d(TAG,"onFailure : "+e.getMessage());
                    }
                });

    }


  public static void LoadPdfFromUrlSinglePage(String pdfUrl, String pdfTitle, PDFView pdfView, ProgressBar progressBar,TextView pagesTv) {

        String TAG="PDF_LOAD_SINGLE_TAG";
        //using url we can get file and its metadata from firebase storage
        StorageReference ref=FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte [] bytes) {
                        Log.d(TAG,"onSuccess :"+pdfTitle+"succesfully got the file  ");

                        //set to pdf view
                        try{
                            pdfView.fromBytes(bytes)
                                    .pages(0)//show only first page
                                    .spacing(0)
                                    .swipeHorizontal(false)
                                    .enableSwipe(false)
                                    .onError(new OnErrorListener() {
                                        @Override
                                        public void onError(Throwable t) {
                                            //hide progress
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Log.d(TAG,"onError : "+t.getMessage());
                                        }
                                    }).onPageError(new OnPageErrorListener() {
                                        @Override
                                        public void onPageError(int page, Throwable t) {
                                            Log.d(TAG,"onPageError : "+t.getMessage());
                                        }
                                    })
                                    .onLoad(new OnLoadCompleteListener() {
                                        @Override
                                        public void loadComplete(int nbPages) {
                                            //pdf loaded
                                            //hide progress
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Log.d(TAG,"LoadComplete:pdf loaded");

                                            //if pages is not null hten set page number
                                            if(pagesTv!=null){
                                                pagesTv.setText(""+nbPages);
                                            }
                                        }
                                    })
                                    .load();
                        }catch (Exception e){

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //hide progress
                        progressBar.setVisibility(View.INVISIBLE);
                        //failed to getting metada
                        Log.d(TAG,"onFailure : failed to getting file from url due to  "+e.getMessage());
                    }
                });
    }



   public static void LoadCategory(String categoryId, TextView categoryTv) {


        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String category= ""+snapshot.child("category").getValue();

                        //set value to category text view
                        categoryTv.setText(category);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void incrementBookViewCount(String bookId){
        //get book view count
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get view count
                        String viewsCount=""+snapshot.child("viewsCount").getValue();

                        //in case  on null replace with 0
                        if(viewsCount.equals("")||viewsCount.equals("null")){
                            viewsCount="0";
                        }

                        //increment view count
                        long newViewsCount=Long.parseLong(viewsCount)+1;

                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("viewsCount",newViewsCount);

                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Books");
                        reference.child(bookId)
                                .updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public static void downloadBook(Context context, String bookId, String bookTitle, String bookUrl){
        Log.d(TAG_DOWNLOAD, "downloadBook: downloading book...");
        
        String nameWithExtension=bookTitle +".pdf";
        Log.d(TAG_DOWNLOAD, "downloadBook: Name : "+nameWithExtension+"...");
        
        //proogress dialog
        ProgressDialog progressDialog= new ProgressDialog(context);
        progressDialog.setTitle("Please wait..");
        progressDialog.setMessage("Downloading "+nameWithExtension+"....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        
        //downloda fro firebase storage using url
        StorageReference storageReference=FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG_DOWNLOAD, "onSuccess: Book Downloaded ");
                        Log.d(TAG_DOWNLOAD, "onSuccess: Saving Book... ");
                        saveDownloadedBook(context,progressDialog,bytes,nameWithExtension,bookId);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG_DOWNLOAD, "onFailure: Failed to download dur to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed to download dur to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void saveDownloadedBook(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWithExtension, String bookId) {
        Log.d(TAG_DOWNLOAD, "saveDownloadedBook: Saving Downloaded book");
        try {
            File downloadsFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadsFolder.mkdir();

            String filePath= downloadsFolder.getPath()+"/"+nameWithExtension;

            FileOutputStream out=new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Toast.makeText(context, "Saved to Download Folder", Toast.LENGTH_SHORT).show();
            Log.d(TAG_DOWNLOAD, "saveDownloadedBook: Saved to Download Folder");
            progressDialog.dismiss();

            incrementBookDownloadCount(bookId);

        }catch (Exception e){
            Log.d(TAG_DOWNLOAD, "saveDownloadedBook: Failed saving to Download folder due to "+e.getMessage());
            Toast.makeText(context, "Failed saving to Download Folder due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        }

    }

    private static void incrementBookDownloadCount(String bookId) {
        Log.d(TAG_DOWNLOAD, "incrementBookDownloadCount: Incrementing Book Download Count");
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String downloadsCount=""+snapshot.child("downloadsCount").getValue();
                        Log.d(TAG_DOWNLOAD, "onDataChange: Donwnloads Count "+downloadsCount);

                        if(downloadsCount.equals("")||downloadsCount.equals("null")){
                            downloadsCount="0";
                        }

                        //convert to logn and increment 1
                        long newDownloadsCount=Long.parseLong(downloadsCount)+1;
                        Log.d(TAG_DOWNLOAD, "onDataChange: New Download Count "+newDownloadsCount);

                        //setup data to update
                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("downloadsCount",newDownloadsCount);

                        //update the new icremented downloas count to db
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Books");
                        reference.child(bookId).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG_DOWNLOAD, "onSuccess: Downloads Count updated... ");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG_DOWNLOAD, "onFailure: Failed to update Downloads cCount due to  "+e.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    public static void loadPdfPageCount(Context context , String pdfUrl, TextView pagesTv){
        //load pdf  from firebase storage using url
        StorageReference storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        storageReference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        ///file reveive

                        //load pdf pages using pdfvie
                        PDFView pdfView=new PDFView(context,null);
                        pdfView.fromBytes(bytes)
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        pagesTv.setText(""+nbPages);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    public static void addToFavorite(Context context, String bookId){
        //check if user loged in 
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            //not logged in , cant add fav
            Toast.makeText(context, "You 're not loged in ", Toast.LENGTH_SHORT).show();
        }else{
            long timestamp =System.currentTimeMillis();

            //setup datat to add firebase db of current use
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("bookId",""+bookId);
            hashMap.put("timestamp",""+timestamp);

            //save to db
            DatabaseReference ref=FirebaseDatabase .getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favourites").child(bookId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Added to your favoite list", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to add your favoite due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    public static void removeFromFavorite(Context context, String bookId){
        //check if user loged in
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            //not logged in , cant remove fav
            Toast.makeText(context, "You 're not loged in ", Toast.LENGTH_SHORT).show();
        }else{
            long timestamp =System.currentTimeMillis();


            //remove from
            DatabaseReference ref=FirebaseDatabase .getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favourites").child(bookId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Removed from your  favoite list", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to remove your favoite due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }
}
