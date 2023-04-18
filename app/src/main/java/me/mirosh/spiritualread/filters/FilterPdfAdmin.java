package me.mirosh.spiritualread.filters;

import android.widget.Filter;

import java.util.ArrayList;

import me.mirosh.spiritualread.adapters.AdapterPdfAdmin;
import me.mirosh.spiritualread.model.ModelPdf;

public class FilterPdfAdmin extends Filter {
    //array lisst in whihc we want to search
    ArrayList<ModelPdf> filterList;
    //adapter in which filter need to be implemtned
    AdapterPdfAdmin adapterPdfAdmin;
    //constructorf

    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, AdapterPdfAdmin adapterPdfAdmin) {
        this.filterList = filterList;
        this.adapterPdfAdmin = adapterPdfAdmin;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results= new FilterResults();
        //value should not be null and emply

        if(constraint != null && constraint.length()>0){
            //change to upper case, or lover case to avoid case sensitivity
            constraint=constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filteredModels=new ArrayList<>();
            for(int i=0;i<filterList.size();i++){
                //validate
                if(filterList.get(i).getTitle().toUpperCase().contains(constraint)){
                    //add to filtered list
                    filteredModels.add(filterList.get(i));
                }

            }

            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else{
            results.count=filterList.size();
            results.values=filterList;

        }
        return results; //
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        //app filtere changes
        adapterPdfAdmin.pdfArrayList=(ArrayList<ModelPdf>)results.values;

        //notify changes
        adapterPdfAdmin.notifyDataSetChanged();
    }
}
