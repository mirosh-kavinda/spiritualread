package me.mirosh.spiritualread.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import me.mirosh.spiritualread.databinding.RowRequestedBooksBinding;
import me.mirosh.spiritualread.model.ModelRequested;


public class AdapterRequested  extends RecyclerView.Adapter<AdapterRequested.HolderCategory>  {

    public Context context;
    public ArrayList<ModelRequested> requestedArrayList;

    private RowRequestedBooksBinding binding;
    //instance of our filter class

    public AdapterRequested (Context context, ArrayList<ModelRequested> requestedArrayList) {
        this.context = context;
        this.requestedArrayList = requestedArrayList;

    }

    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind row_category.xml
        binding = RowRequestedBooksBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        //get data
        ModelRequested model = requestedArrayList.get(position);
        String id = model.getId();
        String category = model.getCategoryId();
        String author = model.getAuthor();
        long timestamp = model.getTimestamp();
        String title=model.getTitle();

        //set data
        holder.categoryTv.setText(category);
        holder.bookTv.setText(title);
        holder.authorTv.setText(author);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hadle click delete category
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Confirm Fulfilling").setMessage("Are you fulfilled this Book request ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //begin delete
                                Toast.makeText(context, "Acccepting...", Toast.LENGTH_SHORT).show();
                                deleteCategory(model, holder);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

    }
    class HolderCategory extends RecyclerView.ViewHolder {

        //ui views of row category.xml
        TextView categoryTv,bookTv,authorTv;
        ImageButton deleteBtn;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);
            //init ui view
            categoryTv = binding.categoryTv;
            bookTv=binding.bookTv;
            authorTv=binding.authorTv;
            deleteBtn = binding.deleteBtn;
        }
    }

    private void deleteCategory(ModelRequested model, HolderCategory holder) {
        //get id of category to delete
        String cid = model.getId();

        //firebaseDb >Categories>categoryId

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Requests");
        ref.child(cid)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //deleted succeffully
                        Toast.makeText(context, "Succesfully Accomplished .", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //failed to delte
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    public int getItemCount() {
        return requestedArrayList.size();
    }

    //view holder class o hld UI views for row_category.xml





}