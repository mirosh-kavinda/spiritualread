package me.mirosh.spiritualread.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import java.util.HashMap;

import me.mirosh.spiritualread.R;
import me.mirosh.spiritualread.databinding.ActivityProfileEditBinding;

public class ProfileEditActivity extends AppCompatActivity {

    private ActivityProfileEditBinding binding;
    private FirebaseAuth firebaseAuth;

    private Uri imageUrl=null;

    private String name ="";
    private ProgressDialog progressDialog;

private static final String TAG="PROFILE_EDIT_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseAuth=FirebaseAuth.getInstance();
        loadUserInfo();



    binding.backBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    });


    binding.profileIv.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showImageAttachMenu();
        }

       
    });

    binding.updateBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            validateData();
        }
    });
    }

    private void validateData() {
        name= binding.nameEt.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();


        }else{
            if(imageUrl==null){
                updateProfile("");

            }else{
                 uploadImage();
            }
        }

    }
    private void updateProfile(String ImageUrl) {
        Log.d(TAG, "updateProfile: Updating user profile");
        progressDialog.setMessage("Updating user profile");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        if (imageUrl != null) {
            hashMap.put("profileImage",""+ImageUrl);
        }


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Profile Updated....");
                        progressDialog.dismiss();

                        Toast.makeText(ProfileEditActivity.this, "profile Updated...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: update failed due to "+e.getMessage());

                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Failed to update db due to "+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }
    private void uploadImage(){
        Log.d(TAG, "uploadImage: Uploadig profile image ... ");

        progressDialog.setMessage("Updating profile image");
        progressDialog.show();

        String filePathAndName="ProfileImages/"+firebaseAuth.getUid();

        StorageReference reference= FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Profile image upload");
                        Log.d(TAG, "onSuccess: Getting url of uploaded image");
                        Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedImageUrl=""+uriTask.getResult();

                        Log.d(TAG, "onSuccess: uploaded image url"+uploadedImageUrl);
                        updateProfile(uploadedImageUrl);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to uploade image due to "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImageAttachMenu() {
        PopupMenu popupMenu=new PopupMenu(this, binding.profileIv);
        popupMenu.getMenu().add(Menu.NONE, 0,0,"camera");
        popupMenu.getMenu().add(Menu.NONE,1,1,"Gallery");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int which=item.getItemId();
                if(which==0){
                    pickImageCamera();
                }
                else if(which==1){
            pickImageGallery();
                }
                return false;
            }
        });
    }

    private void pickImageGallery() {

        Intent intent=new Intent( Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityLauncher.launch(intent);

    }

    private void pickImageCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Image Description");
        imageUrl=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUrl);
        cameraActivityLauncher.launch(intent);


    }

private ActivityResultLauncher<Intent>cameraActivityLauncher=registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== Activity.RESULT_OK){
                    Log.d(TAG, "onActivityResult: "+imageUrl);
                    Intent data =result.getData();
                    imageUrl=data.getData();
                    Log.d(TAG, "onActivityResult: Picked from Gallery"+imageUrl);
                    binding.profileIv.setImageURI(imageUrl);


                }else{
                    Toast.makeText(ProfileEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                }

            }
        }
);
    private ActivityResultLauncher<Intent>galleryActivityLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + imageUrl);
                        Intent data = result.getData();

                        imageUrl=data.getData();
                        Log.d(TAG, "onActivityResult: picked form gallery"+imageUrl);
                        binding.profileIv.setImageURI(imageUrl);
                    }
                    else{
                        Toast.makeText(ProfileEditActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading user infor of user "+firebaseAuth.getUid());

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

                        binding.nameEt.setText(name);
                        try{
                            Glide.with(ProfileEditActivity.this)
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_person_white)
                                    .into(binding.profileIv);

                        }catch (Exception e){
                            Toast.makeText(ProfileEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}