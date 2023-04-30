package me.mirosh.spiritualread.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.mirosh.spiritualread.R;
import me.mirosh.spiritualread.activities.MyApplication;
import me.mirosh.spiritualread.databinding.RowCommentBinding;
import me.mirosh.spiritualread.model.ModelComments;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.HolderComment >{
    //context
    private Context context;
  //arraylist to hold ocomment
    private ArrayList<ModelComments> commentsArrayList;
    private FirebaseAuth firebaseAuth;

    //view binding
    private RowCommentBinding binding;



    public AdapterComment(Context context, ArrayList<ModelComments> commentsArrayList) {
        this.context = context;
        this.commentsArrayList = commentsArrayList;
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding= RowCommentBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderComment(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {

        ModelComments modelComments=commentsArrayList.get(position);
        String uid=modelComments.getUid();

        //load use details
        loadUserDetails(modelComments,holder);

        //handle click , show option to delte comment
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(firebaseAuth.getCurrentUser()!=null&&uid.equals(firebaseAuth.getUid())){
                    deleteComment(modelComments,holder);
                }
            }
        });



            }
    private void loadUserDetails(ModelComments modelComments,HolderComment holderComment){
        String uid=modelComments.getUid();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name=""+snapshot.child("name").getValue();
                        String profImage=""+snapshot.child("profileImage").getValue();

                        //formate date, salready made function
                        String date= MyApplication.formatTimestamp(Long.parseLong(modelComments.getTimestamp()));
                        //set data
                        holderComment.nameTv.setText(name);
                        holderComment.CommentTv.setText(modelComments.getComment());
                        holderComment.DateTv.setText(date);

                        try{
                            Glide.with(context)
                                    .load(profImage)
                                    .placeholder(R.drawable.ic_person_white)
                                    .into(holderComment.profileIv);
                        }catch (Exception e){
                            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            holderComment.profileIv.setImageResource(R.drawable.ic_person_white);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void deleteComment(ModelComments modelComments, HolderComment holder) {
    //show confirm dialog before deleting comment
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Delete Comment")
                .setMessage("Are you sure want to delete this comment ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
                        ref.child(modelComments.getBookId()).child("Comments").child(modelComments.getBookId())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "delete comment successfully..", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed due to delte comment :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

    }



    @Override
    public int getItemCount() {
        return commentsArrayList.size();
    }

    class HolderComment extends RecyclerView.ViewHolder{

        //ui view of rown comment
        ShapeableImageView profileIv;
        TextView nameTv, DateTv, CommentTv;
        public HolderComment(@NonNull View itemView) {
            super(itemView);
            profileIv=binding.profileIv;
            nameTv=binding.nameTv;
            DateTv=binding.dateTv;
            CommentTv=binding.commentTv;

        }
    }
}
