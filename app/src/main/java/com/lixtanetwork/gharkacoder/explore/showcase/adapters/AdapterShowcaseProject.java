package com.lixtanetwork.gharkacoder.explore.showcase.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.RowShowcaseListBinding;
import com.lixtanetwork.gharkacoder.explore.showcase.activities.ShowcaseDetailsActivity;
import com.lixtanetwork.gharkacoder.explore.showcase.filters.FilterShowcaseProject;
import com.lixtanetwork.gharkacoder.explore.showcase.models.ModelShowcaseProject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterShowcaseProject extends RecyclerView.Adapter<AdapterShowcaseProject.HolderShowcaseProject> implements Filterable {

    private final Context context;
    public ArrayList<ModelShowcaseProject> showcaseArrayList, filterList;
    private FilterShowcaseProject filter;
    private RowShowcaseListBinding binding;
    private FirebaseFirestore firestore;
    private String currentUserId;

    public AdapterShowcaseProject(Context context, ArrayList<ModelShowcaseProject> showcaseArrayList, String currentUserId) {
        this.context = context;
        this.showcaseArrayList = showcaseArrayList;
        this.filterList = showcaseArrayList;
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public AdapterShowcaseProject.HolderShowcaseProject onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowShowcaseListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderShowcaseProject(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterShowcaseProject.HolderShowcaseProject holder, int position) {

        ModelShowcaseProject model = showcaseArrayList.get(position);
        String projectId = model.getProjectId();
        String projectLogo = model.getProjectLogo();
        String projectTitle = model.getProjectTitle();
        String projectTagLine = model.getProjectTagline();
        int projectVotes = model.getProjectVotes();

        Glide.with(context).load(projectLogo).placeholder(R.drawable.profile_icon).into(holder.projectImage);
        holder.projectTitle.setText(projectTitle);
        holder.projectTagline.setText(projectTagLine);
        holder.voteText.setText(String.valueOf(projectVotes));

        checkUserVoteStatus(holder, projectId);

        holder.voteIcon.setOnClickListener(v -> {
            toggleVote(holder, projectId, projectVotes);
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowcaseDetailsActivity.class);
                intent.putExtra("projectId", projectId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return showcaseArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new FilterShowcaseProject(filterList, this);
        return filter;
    }

    class HolderShowcaseProject extends RecyclerView.ViewHolder {

        RelativeLayout item;
        ImageView projectImage, voteIcon;
        TextView projectTitle, projectTagline, voteText;

        public HolderShowcaseProject(@NonNull View itemView) {
            super(itemView);

            item = binding.item;
            projectImage = binding.projectImage;
            voteIcon = binding.upvoteIcon;
            projectTitle = binding.projectTitle;
            projectTagline = binding.projectTagline;
            voteText = binding.upvoteText;
        }
    }

    private void checkUserVoteStatus(HolderShowcaseProject holder, String projectId) {
        firestore.collection("Projects Showcase").document(projectId)
                .collection("voters").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        holder.voteIcon.setImageResource(R.drawable.upvote_fill_icon);
                    } else {
                        holder.voteIcon.setImageResource(R.drawable.upvote_empty_icon);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to check vote status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleVote(HolderShowcaseProject holder, String projectId, int currentVotes) {
        firestore.collection("Projects Showcase").document(projectId)
                .collection("voters").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        removeVote(holder, projectId, currentVotes);
                    } else {
                        addVote(holder, projectId, currentVotes);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to toggle vote: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addVote(HolderShowcaseProject holder, String projectId, int currentVotes) {
        firestore.runTransaction(transaction -> {
            DocumentReference projectRef = firestore.collection("Projects Showcase").document(projectId);
            DocumentReference voterRef = projectRef.collection("voters").document(currentUserId);

            transaction.update(projectRef, "projectVotes", currentVotes + 1);
            transaction.set(voterRef, new HashMap<String, Object>() {{
                put("voted", true);
            }});

            return null;
        }).addOnSuccessListener(aVoid -> {
            holder.voteIcon.setImageResource(R.drawable.upvote_fill_icon);
            holder.voteText.setText(String.valueOf(currentVotes + 1));
            Toast.makeText(context, "Vote added!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to add vote: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void removeVote(HolderShowcaseProject holder, String projectId, int currentVotes) {
        firestore.runTransaction(transaction -> {
            DocumentReference projectRef = firestore.collection("Projects Showcase").document(projectId);
            DocumentReference voterRef = projectRef.collection("voters").document(currentUserId);

            transaction.update(projectRef, "projectVotes", currentVotes - 1);
            transaction.delete(voterRef);

            return null;
        }).addOnSuccessListener(aVoid -> {
            holder.voteIcon.setImageResource(R.drawable.upvote_empty_icon);
            holder.voteText.setText(String.valueOf(currentVotes - 1));
            Toast.makeText(context, "Vote removed!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to remove vote: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}