package me.mirosh.spiritualread.filters;

import android.widget.Filter;

import java.util.ArrayList;

import me.mirosh.spiritualread.adapters.AdapterPdfUser;
import me.mirosh.spiritualread.model.ModelPdf;

public class FilterPdfUser extends Filter {
    //arraylist in which we want to search
    ArrayList<ModelPdf> filterList;
    //adapter inwchich filter need to be implementd
    AdapterPdfUser adapterPdfUser;
    //constructure


    public FilterPdfUser(ArrayList<ModelPdf> filterList, AdapterPdfUser adapterPdfUser) {
        this.filterList = filterList;
        this.adapterPdfUser = adapterPdfUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
      FilterResults results=new FilterResults();
      //value to be searched should not be null/empty
        if(constraint!=null&& constraint.length()>0){
            //not null nor empty
            //chnage to uppercase or lowe case to aboid case sensitivity
            constraint=constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filteredModels=new ArrayList<>();

            for(int i=0;i<filteredModels.size();i++){
                //validate
                if(filteredModels.get(i).getTitle().toUpperCase().contains(constraint)){
                    //search matches,add to list
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
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
//apply filter changes
        adapterPdfUser.pdfArrayList=(ArrayList<ModelPdf>)results.values;
        //notify changes
        adapterPdfUser.notifyDataSetChanged();

    }
}
