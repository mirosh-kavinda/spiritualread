package me.mirosh.spiritualread.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import me.mirosh.spiritualread.Dashboards.Admin;
import me.mirosh.spiritualread.databinding.ActivityPdfAddBinding;

public class PdfAddActivity extends AppCompatActivity {

    //binding
    private ActivityPdfAddBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;
    //progres bar
    private ProgressDialog progressDialog;

    //arraylist to hold pdf categories
    private ArrayList<String> categoryTitleArrayList, categoryIdArrayList;
    //uri picked for pdf
    private Uri pdfUri=null;

    //TAG for debugging
    private static final String TAG="ADD_PDF_TAG";

    private static final int PDF_PICK_CODE=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding =ActivityPdfAddBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    //init firebase auth
    firebaseAuth=FirebaseAuth.getInstance();
    loadPdfCategories();

    //setup progresss dialog
    progressDialog=new ProgressDialog(this);
    progressDialog.setTitle("Please wait");
    progressDialog.setCanceledOnTouchOutside(false);


    //handle click, go to previous activity
    binding.backBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
       startActivity(new Intent(PdfAddActivity.this, Admin.class));

        }
    });
    //handle click , attach pdf
    binding.attachBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pdfPickIntent();
        }
    });

    //hanlde click , pcik category
    binding.categoryTv.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            categoryPickDialog();
        }
    });

        //handle click , upload pdf
    binding.submitBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //validate data
            validateData();
        }
    });

    }

    private String title="", description="";
    private void validateData() {
    //Step 1 : Validate Data
    Log.d(TAG,"validateData: validating data...");

    //get data
    title=binding.titleEt.getText().toString().trim();
    description=binding.descriptionEt.getText().toString().trim();


    //validate data
    if (TextUtils.isEmpty(title)) {

        Toast.makeText(this, "Enter Title...", Toast.LENGTH_SHORT).show();
    }else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show();
    }else if(TextUtils.isEmpty(selectedCategoryTitle)){
            Toast.makeText(this, "Pick Category...", Toast.LENGTH_SHORT).show();
    }else if(pdfUri==null){
        Toast.makeText(this, "Pick Pdf...", Toast.LENGTH_SHORT).show();
    }
    else{
        //all data is valid, can upload now
        uploadPdfToStorage();
    }
    }

    private void uploadPdfToStorage() {
    //Step 02 : Upload Pdf to firebase storage
    Log.d(TAG,"uploadPdfToStorage: uploading to storage...");

    //show progress
    progressDialog.setMessage("Uploading Pdf...");
    progressDialog.show();

    //timestamp
    long timestamp= System.currentTimeMillis();

    //path of pdf in firebase storage
    String filePathAndName="Books/"+timestamp;
    //storage reference
    StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
    storageReference.putFile(pdfUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Log.d(TAG,"onSuccess: PDF uploaded to storage ..");
                Log.d(TAG,"onSuccess: getting pdf url ..");

               //get pdf url
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful()) {

                }
                String uploadedPdfUrl=""+uriTask.getResult();
                //upload to firebase db
                uploadePdfInfoToDb(uploadedPdfUrl,timestamp);
            }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
    Log.d(TAG,"OnFailure: PDF upload failed due to "+e.getMessage());
                Toast.makeText(PdfAddActivity.this, "PDF upload failed due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
}

    private void uploadePdfInfoToDb(String uploadedPdfUrl, long timestamp) {
        //Step 3: upload pdf infor to firebase db
        Log.d(TAG,"uploadPdfToStorage: uploading Pdf info to firebase db ..");
         progressDialog.setMessage("Uploading pdf info ...");

         String uid=firebaseAuth.getUid();


         //setup data to upload , alos add view count , download count while uploading
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("id",""+timestamp);
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+selectedCategoryId);
        hashMap.put("url",""+uploadedPdfUrl);
        hashMap.put("timestamp",timestamp);
        hashMap.put("viewsCount",0);
        hashMap.put("downloadsCount",0);

        //db reference : DB>Books
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onSuccess: Successfully uploaded ...");
                        Toast.makeText(PdfAddActivity.this, "Successfully uploaded ...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG,"onFailure: Failed to upload to db due to "+e.getMessage());
                        Toast.makeText(PdfAddActivity.this, " Failed to upload to db due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadPdfCategories() {
        Log.d(TAG,"LoadPdfCategories:Loading pdf categories...");
        categoryTitleArrayList =new ArrayList<>();
        categoryIdArrayList=new ArrayList<>();

        //db reference to load cateogiries ... db>vategories
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIdArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                 //get id and title of category
                    String categoryId= ""+ds.child("id").getValue();
                    String categoryTitle= ""+ds.child("category").getValue();

                    //add to respenctive arraylists
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //selected category id and category title
    private String selectedCategoryId,selectedCategoryTitle;

    private void categoryPickDialog() {
        Log.d(TAG,"categoryPickDialog: showing category pick dialog");

        //get array o categories from arraylist
        String []categoriesArray=new String[categoryTitleArrayList.size()];
        for(int i = 0; i< categoryTitleArrayList.size(); i++){
            categoriesArray[i]= categoryTitleArrayList.get(i);
        }


        //alert dialog
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                //hanlde item click
              //get clicked item from list
                selectedCategoryTitle=categoryTitleArrayList.get(which);
                selectedCategoryId=categoryIdArrayList.get(which);
              //set to category textview
                binding.categoryTv.setText(selectedCategoryTitle);

                Log.d(TAG,"onClick: Selected Category : "+selectedCategoryId+" "+selectedCategoryTitle);
                    }
                }).show();

    }

    private void pdfPickIntent() {
        Log.d(TAG,"PdfPickIntent: starting pdf pick intent");
        Intent intent =new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Pdf"),PDF_PICK_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if(resultCode==RESULT_OK){
           if(requestCode==PDF_PICK_CODE){
                Log.d(TAG,"onActivityResult : PDF Picked");

               if (data != null) {
                   pdfUri=data.getData();
               }
               Log.d(TAG,"onActivityResult: URI : "+pdfUri);
                
           }
       } else{
           Log.d(TAG,"onActivityResult : cancelled picking pdf");
           Toast.makeText(this, "cancelld picking pdf", Toast.LENGTH_SHORT).show();
       }
    }
}