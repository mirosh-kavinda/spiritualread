package com.example.spiritualread.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spiritualread.databinding.RowPdfAdminBinding;
import com.example.spiritualread.model.ModelPdf;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> {

    //context
    private Context context;

    //arraylist ot hold list of data of type modelpdf
    private ArrayList<ModelPdf> pdfArrayList;

    //view binding row_pdf_admin.xml
    private RowPdfAdminBinding binding;

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind  layout using view bvinding
        binding=RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent, false);

        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPdfAdmin.HolderPdfAdmin holder, int position) {

    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();//return number of records
    }

    //construcotr
    /*View holder class for row_pdf_admin.xma*/
    class HolderPdfAdmin extends RecyclerView.ViewHolder {

        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);
        }

    }
}
