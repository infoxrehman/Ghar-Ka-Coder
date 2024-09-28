package com.lixtanetwork.gharkacoder.explore.guidelines.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lixtanetwork.gharkacoder.databinding.RowGuidelinesBinding;
import com.lixtanetwork.gharkacoder.explore.guidelines.activities.GuidelinePdfDetailActivity;
import com.lixtanetwork.gharkacoder.explore.guidelines.models.ModelGuidelinePdf;
import com.lixtanetwork.gharkacoder.packages.Constants;

import java.util.ArrayList;

public class AdapterGuidelinePdf extends RecyclerView.Adapter<AdapterGuidelinePdf.HolderGuidelinePdf> {

    private final Context context;
    public ArrayList<ModelGuidelinePdf> pdfArrayList;
    private RowGuidelinesBinding binding;

    public AdapterGuidelinePdf(Context context, ArrayList<ModelGuidelinePdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public HolderGuidelinePdf onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowGuidelinesBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderGuidelinePdf(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGuidelinePdf holder, int position) {

        ModelGuidelinePdf model = pdfArrayList.get(position);
        String pdfId = model.getId();
        String categoryId = model.getCategoryId();
        String title = model.getTitle();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        long timestamp = model.getTimestamp();

        String formattedDate = Constants.formatTimestamp(timestamp);

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.dateTv.setText(formattedDate);

        Constants.loadPdfSize(
                "" + pdfUrl,
                "" + title,
                holder.sizeTv
        );

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

    class HolderGuidelinePdf extends RecyclerView.ViewHolder {

        TextView titleTv, descriptionTv, sizeTv, dateTv;

        public HolderGuidelinePdf(@NonNull View itemView) {
            super(itemView);

            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
        }
    }

}