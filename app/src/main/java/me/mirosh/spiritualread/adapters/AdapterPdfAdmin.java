package me.mirosh.spiritualread.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

import me.mirosh.spiritualread.MyApplication;
import me.mirosh.spiritualread.activities.PdfDetailActivity;
import me.mirosh.spiritualread.activities.PdfEditActivity;
import me.mirosh.spiritualread.databinding.RowPdfAdminBinding;
import me.mirosh.spiritualread.filters.FilterPdfAdmin;
import me.mirosh.spiritualread.model.ModelPdf;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {

    //context
    private Context context;

    //arraylist ot hold list of data of type modelpdf
    public ArrayList<ModelPdf> pdfArrayList,filterList;

    //view binding row_pdf_admin.xml
    private RowPdfAdminBinding binding;


    private FilterPdfAdmin filter;


    //constructor
    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        //initi progresss dialog
        //progresss
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait ..0");
        progressDialog.setCanceledOnTouchOutside(false);


    }


    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind  layout using view bvinding
        binding=RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent, false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfAdmin.HolderPdfAdmin holder, int position) {
        // get data, set data , handle click etc

        //get data
        ModelPdf model=pdfArrayList.get(position);

        String title=model.getTitle();
        String description=model.getDescription();
        String pdfUrl=model.getUrl();
        String pdfId=model.getId();
        String categoryId=model.getCategoryId();
        long timestamp=model.getTimestamp();

        //we need to convert timestamp to dd/MM/yyy fromat
        String formatDate= MyApplication.formatTimestamp(timestamp);

        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formatDate);

        //load further details like cateogry, pdf from url, pdf size in separate functions

        MyApplication.LoadCategory(
                ""+categoryId,
                holder.categoryTv
        );

        MyApplication.LoadPdfFromUrlSinglePage(
                ""+pdfUrl,
                ""+title,
                holder.pdfView,
                holder.progressBar
                );

        MyApplication.LoadPdfSize(
                ""+pdfUrl,
                ""+title,
                 holder.sizeTv
        );

        //handle click, show dialog with option 1) Edit, 2)Delete
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptionsDialog(model,holder);
            }
        });

        //handle book/pdf click, open pdf details page, pass pdf/book id to get details of it
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });
    }

    private void moreOptionsDialog(ModelPdf model, HolderPdfAdmin holder) {
        String bookId=model.getId();
        String  bookUrl=model.getUrl();
        String bookTitle=model.getTitle();

        //options to show in dialog
        String[] options={"Edit","Delete"};

        //alert dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Choose Option")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle dialog optoion click
                        if(which==0){
                            //Edit Clicked , Open PdfEditActivity to edit the book  info
                            Intent intent=new Intent(context, PdfEditActivity.class);
                            intent.putExtra("bookId",bookId);
                            context.startActivity(intent);

                        }else if(which==1){
                            //Delete Clicked
                            MyApplication.deleteBook(
                                    context,
                                    ""+bookId,
                                    ""+bookTitle,
                                    ""+bookUrl
                            );

                        }
                    }
                }).show();

    }


    /*View holder class for row_pdf_admin.xma*/
    class HolderPdfAdmin extends RecyclerView.ViewHolder {
//Ui views of row_pdf andmin.xml
        PDFView pdfView;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,categoryTv, sizeTv,dateTv;
        ImageButton moreBtn;

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);

            //init ui views
            pdfView=binding.pdfView;
            progressBar=binding.progressBar;
            titleTv=binding.titleTv;
            descriptionTv=binding.descriptionTv;
            categoryTv=binding.categoryTv;
            sizeTv=binding.sizeTv;
            dateTv=binding.dateTv;
            moreBtn=binding.moreBtn;

        }
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
        //return number of records
    }

    @Override
    public Filter getFilter() {
        if(filter==null){
            filter=new FilterPdfAdmin(filterList,this);
        }
        return filter;
    }

}
