package me.mirosh.spiritualread.filters;

import android.widget.Filter;

import java.util.ArrayList;

import me.mirosh.spiritualread.adapters.AdapterCategory;
import me.mirosh.spiritualread.model.ModelCategory;

public class FilterCategory extends Filter {
    //array lisst in whihc we want to search
    ArrayList<ModelCategory> filterList;
    //adapter in which filter need to be implemtned
    AdapterCategory adapterCategory;
    //constructorf

    public FilterCategory(ArrayList<ModelCategory> filterList, AdapterCategory adapterCategory) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results= new FilterResults();
        //value should not be null and emply
        if(constraint != null && constraint.length()>0){
            //change to upper case, or lover case to avoid case sensitivity
            constraint=constraint.toString().toUpperCase();
            ArrayList<ModelCategory> filteredModels=new ArrayList<>();
            for(int i=0;i<filterList.size();i++){
                //validate
                if(filterList.get(i).getCategory().toUpperCase().contains(constraint)){
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
        adapterCategory.categoryArrayList=(ArrayList<ModelCategory>)results.values;
        //notify changes
        adapterCategory.notifyDataSetChanged();
    }
}
