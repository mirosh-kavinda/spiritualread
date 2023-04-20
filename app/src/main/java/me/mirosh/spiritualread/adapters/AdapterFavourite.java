package me.mirosh.spiritualread.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.mirosh.spiritualread.activities.MyApplication;
import me.mirosh.spiritualread.activities.PdfDetailActivity;
import me.mirosh.spiritualread.databinding.RowPdfFavouriteBinding;
import me.mirosh.spiritualread.model.ModelPdf;

public class AdapterFavourite  extends  RecyclerView.Adapter<AdapterFavourite.HolderPdfFavourite>{

    private Context context;
    private ArrayList<ModelPdf> pdfArrayList;
    private static String TAG="Fav_Books";

    //view binding ofor row_pdf_favorite
    private RowPdfFavouriteBinding binding;


    public AdapterFavourite(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfFavourite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding=RowPdfFavouriteBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderPdfFavourite(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfFavourite holder, int position) {

        ModelPdf model= pdfArrayList.get(position);
        LoadBookDetails(model,holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",model.getId());
                context.startActivity(intent);
            }
        });

        holder.removeFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.removeFromFavorite(context,model.getId());
            }
        });
    }

    private void LoadBookDetails(ModelPdf model, HolderPdfFavourite holder) {
        String bookId=model.getId();
        Log.d(TAG, "LoadBookDetails: Book Deatils of BookID :"+bookId);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String bookTitle=""+snapshot.child("title").getValue();
                        String bookDesc=""+snapshot.child("description").getValue();
                        String categoryId=""+snapshot.child("categoryId").getValue();
                        String bookUrl=""+snapshot.child("url").getValue();
                        String timestamp=""+snapshot.child("timestamp").getValue();
                        String uid=""+snapshot.child("uid").getValue();
                        String viewsCount=""+snapshot.child("viewsCount").getValue();
                        String downloadsCount=""+snapshot.child("downloadsCount").getValue();


                        model.setFavorite(true);
                        model.setTitle(bookTitle);
                        model.setDescription(bookDesc);
                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setCategoryId(categoryId);
                        model.setUid(uid);
                        model.setUrl(bookUrl);

                        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));

                        MyApplication.LoadCategory(categoryId,holder.categoryTv);
                        MyApplication.LoadPdfFromUrlSinglePage(""+bookUrl,""+bookTitle,holder.pdfView,holder.progressBar,null);
                        MyApplication.LoadPdfSize(""+bookUrl,""+bookTitle,holder.sizeTv);

                        holder.titleTv.setText(date);
                        holder.descriptionTv.setText(bookDesc);
                        holder.dateTv.setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    //view Holder class
        class HolderPdfFavourite extends RecyclerView.ViewHolder{

        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv, descriptionTv,categoryTv, sizeTv,dateTv;
        ImageButton removeFavBtn;

        public HolderPdfFavourite(@NonNull View itemView) {
            super(itemView);


            //init ui views
            pdfView=binding.pdfView;
            progressBar=binding.progressBar;
            titleTv=binding.titleTv;
            descriptionTv=binding.descriptionTv;
            categoryTv=binding.categoryTv;
            sizeTv=binding.sizeTv;
            dateTv=binding.dateTv;
            removeFavBtn=binding.removeFavBtn;

        }
    }
}
