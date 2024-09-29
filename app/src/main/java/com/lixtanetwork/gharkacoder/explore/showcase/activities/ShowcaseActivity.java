package com.lixtanetwork.gharkacoder.explore.showcase.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.ActivityShowcaseBinding;
import com.lixtanetwork.gharkacoder.explore.showcase.adapters.AdapterShowcaseProject;
import com.lixtanetwork.gharkacoder.explore.showcase.models.ModelShowcaseProject;

import java.util.ArrayList;

public class ShowcaseActivity extends AppCompatActivity {

    private ActivityShowcaseBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    private AdapterShowcaseProject adapterShowcaseProject;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowcaseBinding.inflate(getLayoutInflater());
        Window window = getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        currentUserId = firebaseUser.getUid();

        loadShowcaseProjects();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.addYourShowcaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowcaseActivity.this, AddShowcaseActivity.class));
            }
        });

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapterShowcaseProject != null) {
                    try {
                        adapterShowcaseProject.getFilter().filter(charSequence);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void loadShowcaseProjects() {
        firebaseFirestore.collection("Projects Showcase")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<ModelShowcaseProject> projectList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        ModelShowcaseProject model = document.toObject(ModelShowcaseProject.class);
                        projectList.add(model);
                    }
                    adapterShowcaseProject = new AdapterShowcaseProject(ShowcaseActivity.this, projectList, currentUserId);
                    binding.showcaseProjectsRv.setAdapter(adapterShowcaseProject);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ShowcaseActivity.this, "Failed to load projects: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}