package com.lixtanetwork.gharkacoder.explore.showcase.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.ActivityShowcaseDetailsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ShowcaseDetailsActivity extends AppCompatActivity {

    private ActivityShowcaseDetailsBinding binding;
    private int bannerPosition = 0;
    private Handler handler;
    private Runnable runnable;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private String projectId;
    private int currentVotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowcaseDetailsBinding.inflate(getLayoutInflater());
        Window window = getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        handler = new Handler(Looper.getMainLooper());

        projectId = getIntent().getStringExtra("projectId");

        loadShowcaseProjectDetails();
        setupVoteListener();
        loadRealTimeVoteUpdates();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void loadShowcaseProjectDetails() {

        firebaseFirestore.collection("Projects Showcase").document(projectId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();

                            uid = document.getString("uid");
                            String projectLogo = document.getString("projectLogo");
                            String projectTitle = document.getString("projectTitle");
                            String projectTagline = document.getString("projectTagline");
                            String projectDescription = document.getString("projectDescription");
                            String projectType = document.getString("projectType");
                            Long uploadedProjectDate = document.getLong("uploadedProjectDate");
                            currentVotes = document.getLong("projectVotes").intValue();
                            ArrayList<String> optionalBanners = (ArrayList<String>) document.get("optionalBanners");

                            Glide.with(ShowcaseDetailsActivity.this).load(projectLogo).into(binding.projectImage);
                            binding.projectTitle.setText(projectTitle);
                            binding.projectTagline.setText(projectTagline);

                            if (uploadedProjectDate != null) {
                                Date uploadedProjectDateTimestamp = new Date(uploadedProjectDate);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
                                String formattedDate = dateFormat.format(uploadedProjectDateTimestamp);
                                binding.submittedOn.setText(formattedDate);
                            }

                            binding.votesTv.setText(String.valueOf(currentVotes));
                            binding.descriptionTv.setText(projectDescription);

                            loadBanners(optionalBanners);
                            loadShowcaseProjectCreatorDetails(uid);
                            checkUserVoteState();
                        } else {
                            Toast.makeText(ShowcaseDetailsActivity.this, "Failed to load project details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadShowcaseProjectCreatorDetails(String uid) {

        firebaseFirestore.collection("Users").document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();

                            String name = document.getString("name");
                            String bio = document.getString("bio");
                            String profileImage = document.getString("profileImage");

                            Glide.with(ShowcaseDetailsActivity.this).load(profileImage).placeholder(R.drawable.profile_icon).into(binding.creatorImage);
                            binding.creatorName.setText(name);
                            binding.creatorBio.setText(bio);

                        } else {
                            Toast.makeText(ShowcaseDetailsActivity.this, "Failed to load creator details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadBanners(ArrayList<String> banners) {
        if (banners != null && !banners.isEmpty()) {
            startAutoScroll(banners);
        }
    }

    private void startAutoScroll(ArrayList<String> banners) {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (bannerPosition == banners.size()) {
                    bannerPosition = 0;
                }
                Glide.with(ShowcaseDetailsActivity.this)
                        .load(banners.get(bannerPosition))
                        .into(binding.bannerImageView);

                bannerPosition++;
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }

    private void setupVoteListener() {
        binding.votesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVote();
            }
        });
    }

    private void toggleVote() {
        firebaseFirestore.collection("Projects Showcase").document(projectId)
                .collection("voters").document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            removeVote();
                        } else {
                            addVote();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShowcaseDetailsActivity.this, "Failed to toggle vote: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addVote() {
        firebaseFirestore.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference projectRef = firebaseFirestore.collection("Projects Showcase").document(projectId);
                DocumentReference voterRef = projectRef.collection("voters").document(firebaseAuth.getCurrentUser().getUid());
                transaction.update(projectRef, "projectVotes", currentVotes + 1);
                transaction.set(voterRef, new HashMap<String, Object>() {{
                    put("voted", true);
                }});
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Object>() {
            @Override
            public void onSuccess(Object o) {
                currentVotes++;
                binding.upvoteIcon.setImageResource(R.drawable.upvote_fill_icon);
                binding.votesLayout.setBackgroundResource(R.drawable.primary_button);
                binding.upvoteText.setText("Upvoted");
                binding.votesTv.setText(String.valueOf(currentVotes));
                Toast.makeText(ShowcaseDetailsActivity.this, "Vote added!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowcaseDetailsActivity.this, "Failed to add vote: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeVote() {
        firebaseFirestore.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference projectRef = firebaseFirestore.collection("Projects Showcase").document(projectId);
                DocumentReference voterRef = projectRef.collection("voters").document(firebaseAuth.getCurrentUser().getUid());
                transaction.update(projectRef, "projectVotes", currentVotes - 1);
                transaction.delete(voterRef);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Object>() {
            @Override
            public void onSuccess(Object o) {
                currentVotes--;
                binding.votesLayout.setBackgroundResource(R.drawable.global_edittext);
                binding.upvoteIcon.setImageResource(R.drawable.upvote_empty_icon);
                binding.upvoteText.setText("Upvote");
                binding.votesTv.setText(String.valueOf(currentVotes));
                Toast.makeText(ShowcaseDetailsActivity.this, "Vote removed!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowcaseDetailsActivity.this, "Failed to remove vote: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUserVoteState() {
        firebaseFirestore.collection("Projects Showcase").document(projectId)
                .collection("voters").document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            binding.upvoteIcon.setImageResource(R.drawable.upvote_fill_icon);
                            binding.upvoteText.setText("Upvoted");
                        } else {
                            binding.upvoteIcon.setImageResource(R.drawable.upvote_empty_icon);
                            binding.upvoteText.setText("Upvote");
                        }
                    }
                });
    }

    private void loadRealTimeVoteUpdates() {
        firebaseFirestore.collection("Projects Showcase").document(projectId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(ShowcaseDetailsActivity.this, "Failed to load real-time votes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            currentVotes = documentSnapshot.getLong("projectVotes").intValue();
                            binding.votesTv.setText(String.valueOf(currentVotes));
                        }
                    }
                });
    }
}