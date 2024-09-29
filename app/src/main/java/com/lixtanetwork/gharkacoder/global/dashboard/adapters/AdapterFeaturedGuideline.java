package com.lixtanetwork.gharkacoder.global.dashboard.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lixtanetwork.gharkacoder.databinding.RowFeaturedGuidelinesBinding;
import com.lixtanetwork.gharkacoder.explore.guidelines.activities.GuidelinePdfDetailActivity;
import com.lixtanetwork.gharkacoder.explore.guidelines.models.ModelGuidelinePdf;

import java.util.ArrayList;

public class AdapterFeaturedGuideline extends RecyclerView.Adapter<AdapterFeaturedGuideline.HolderFeaturedGuideline> {

     private final Context context;

     public ArrayList<ModelGuidelinePdf> pdfArrayList;

    private RowFeaturedGuidelinesBinding binding;

    public AdapterFeaturedGuideline(Context context, ArrayList<ModelGuidelinePdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }


    @NonNull
    @Override
    public HolderFeaturedGuideline onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowFeaturedGuidelinesBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderFeaturedGuideline(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderFeaturedGuideline holder, int position) {
        //get data
        ModelGuidelinePdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String title = model.getTitle();
        String description = model.getDescription();
        String categoryId = model.getCategoryId();
        String pdfUrl = model.getUrl();
//        String categoryName = model.getCategoryName();
        long timestamp = model.getTimestamp();

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);


         holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GuidelinePdfDetailActivity.class);
                intent.putExtra("bookId", pdfId);
                intent.putExtra("categoryId", categoryId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }


    public class HolderFeaturedGuideline extends RecyclerView.ViewHolder {

        TextView titleTv, descriptionTv;

        public HolderFeaturedGuideline(@NonNull View itemView) {
            super(itemView);

            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
        }
    }
}
