package com.lixtanetwork.gharkacoder.explore.showcase.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.RowImagesPickedBinding;
import com.lixtanetwork.gharkacoder.explore.showcase.models.ModelBannerPick;

import java.util.ArrayList;

public class AdapterBannerPick extends RecyclerView.Adapter<AdapterBannerPick.HolderBannerPick> {

    private RowImagesPickedBinding binding;
    private Context mContext;
    private ArrayList<ModelBannerPick> imagePickedArrayList;

    private String projectId;

    public AdapterBannerPick(Context mContext, ArrayList<ModelBannerPick> imagePickedArrayList, String projectId) {
        this.mContext = mContext;
        this.imagePickedArrayList = imagePickedArrayList;
        this.projectId = projectId;
    }

    @NonNull
    @Override
    public HolderBannerPick onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = RowImagesPickedBinding.inflate(LayoutInflater.from(mContext), parent, false);

        return new HolderBannerPick(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderBannerPick holder, @SuppressLint("RecyclerView") int position) {
        ModelBannerPick model = imagePickedArrayList.get(position);

        if (model.getFromInternet()) {
            String imageUrl = model.getImageUrl();
            try {
                Glide.with(mContext).load(imageUrl).placeholder(R.drawable.profile_icon).into(holder.imageIv);
            } catch (Exception e) {

            }
        } else {
            Uri imageUri = model.getImageUri();
            try {
                Glide.with(mContext).load(imageUri).placeholder(R.drawable.profile_icon).into(holder.imageIv);
            } catch (Exception e) {

            }
        }

        holder.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickedArrayList.remove(model);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagePickedArrayList.size();
    }

    class HolderBannerPick extends RecyclerView.ViewHolder {

        ImageView imageIv, closeBtn;

        public HolderBannerPick(@NonNull View itemView) {
            super(itemView);

            imageIv = binding.imageIv;
            closeBtn = binding.closeBtn;
        }
    }
}
