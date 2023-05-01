package me.mirosh.spiritualread.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

import me.mirosh.spiritualread.activities.MyApplication;
import me.mirosh.spiritualread.activities.PdfDetailActivity;
import me.mirosh.spiritualread.databinding.RowPdfUserBinding;
import me.mirosh.spiritualread.filters.FilterPdfUser;
import me.mirosh.spiritualread.model.ModelPdf;

public class AdapterPdfUser extends RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser> implements Filterable {
    private Context context;
    public ArrayList<ModelPdf> pdfArrayList,filterList;
    //progress dialog
    private ProgressDialog progressDialog;
    private FilterPdfUser filter;
    private RowPdfUserBinding binding;
    private static final String TAG="ADAPTER_PDF_UER_TAG";


    public AdapterPdfUser(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList=pdfArrayList;
    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind the view
        binding=RowPdfUserBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfUser(binding.getRoot()) ;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfUser holder, int position) {
        //get data,set data, handle click
        //get data
        ModelPdf model=pdfArrayList.get(position);
        String bookId=model.getId();
        String title=model.getTitle();
        String description=model.getDescription();
        String pdfUrl=model.getUrl();
        String categoryId=model.getCategoryId();
        long timestamp=model.getTimestamp();
        //setup progress dialog
        progressDialog= new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        //convert time
        String date = MyApplication.formatTimestamp(timestamp);

        //setdata
        holder.titleTv.setText(title);
        holder.decriptionTv.setText(description);
        holder.dateTv.setText(date);
         MyApplication.LoadPdfFromUrlSinglePage(
                 ""+pdfUrl,
                 ""+title,
                 holder.pdfView,
                 holder.progressBar,null
         );

         MyApplication.LoadCategory(
                 ""+categoryId,
                 holder.cateogryTv
         );
         MyApplication.LoadPdfSize(
                 ""+pdfUrl,
                 ""+title,
                 holder.sizeTv
         );
        MyApplication.LoadPdfFromUrlSinglePage(
                ""+pdfUrl,
                ""+title,
                binding.pdfView,
                binding.progressBar,
                binding.pagesTv

        );

         //handle click show pdf details actitiy
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterPdfUser(filterList,this);
        }
        return filter;
    }
    class  HolderPdfUser extends RecyclerView.ViewHolder{

        TextView titleTv,decriptionTv,cateogryTv,sizeTv,dateTv;
        PDFView pdfView;
        ProgressBar progressBar;
        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);

            titleTv=binding.titleTv;
            decriptionTv=binding.descriptionTv;
            cateogryTv=binding.categoryTv;
            sizeTv=binding.sizeTv;
            dateTv=binding.dateTv;
            progressBar=binding.progressBar;


        }
    }

}
