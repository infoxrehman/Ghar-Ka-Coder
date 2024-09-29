package com.lixtanetwork.gharkacoder.explore.showcase.filters;

import android.widget.Filter;

import com.lixtanetwork.gharkacoder.explore.guidelines.models.ModelGuidelinePdf;
import com.lixtanetwork.gharkacoder.explore.showcase.adapters.AdapterShowcaseProject;
import com.lixtanetwork.gharkacoder.explore.showcase.models.ModelShowcaseProject;

import java.util.ArrayList;

public class FilterShowcaseProject extends Filter {

    ArrayList<ModelShowcaseProject> filterList;
    AdapterShowcaseProject adapterShowcaseProject;

    public FilterShowcaseProject(ArrayList<ModelShowcaseProject> filterList, AdapterShowcaseProject adapterShowcaseProject) {
        this.filterList = filterList;
        this.adapterShowcaseProject = adapterShowcaseProject;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {

            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelShowcaseProject> filteredModels = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                //validate
                if (filterList.get(i).getProjectTitle().toUpperCase().contains(constraint)) {
                    //add to filtered list
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterShowcaseProject.showcaseArrayList = (ArrayList<ModelShowcaseProject>) results.values;
        adapterShowcaseProject.notifyDataSetChanged();
    }

}