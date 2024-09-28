package com.lixtanetwork.gharkacoder.explore.guidelines.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.ActivityGuidelinesBinding;
import com.lixtanetwork.gharkacoder.explore.guidelines.adapters.AdapterGuidelinePdf;
import com.lixtanetwork.gharkacoder.explore.guidelines.models.ModelGuidelineCategory;
import com.lixtanetwork.gharkacoder.explore.guidelines.models.ModelGuidelinePdf;

import java.util.ArrayList;

public class GuidelinesActivity extends AppCompatActivity {

    private ActivityGuidelinesBinding binding;
    private ArrayList<ModelGuidelineCategory> categoryArrayList;
    private ArrayList<ModelGuidelinePdf> guidelinePdfArrayList;
    private AdapterGuidelinePdf adapterGuidelinePdf;
    private FirebaseFirestore firebaseFirestore;
    private String selectedCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuidelinesBinding.inflate(getLayoutInflater());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();

        loadGuidelineCategories();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void loadGuidelineCategories() {
        categoryArrayList = new ArrayList<>();
        firebaseFirestore.collection("Guideline Categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        categoryArrayList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                ModelGuidelineCategory model = document.toObject(ModelGuidelineCategory.class);
                                categoryArrayList.add(model);
                            }
                            setupTabs();
                        }
                    }
                });
    }

    private void setupTabs() {
        binding.tabLayout.removeAllTabs();
        for (ModelGuidelineCategory category : categoryArrayList) {
            TabLayout.Tab tab = binding.tabLayout.newTab();
            tab.setText(category.getCategory());
            tab.setTag(category.getId());
            binding.tabLayout.addTab(tab);
        }

        if (binding.tabLayout.getTabCount() > 0) {
            TabLayout.Tab firstTab = binding.tabLayout.getTabAt(0);
            if (firstTab != null) {
                selectedCategoryId = (String) firstTab.getTag();
                loadGuidelines(selectedCategoryId);
            }
        }

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedCategoryId = (String) tab.getTag();
                loadGuidelines(selectedCategoryId);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                selectedCategoryId = (String) tab.getTag();
                loadGuidelines(selectedCategoryId);
            }
        });
    }

    private void loadGuidelines(String categoryId) {
        guidelinePdfArrayList = new ArrayList<>();
        CollectionReference booksRef = firebaseFirestore.collection("Guideline Categories").document(categoryId).collection("Guideline Books");
        booksRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        guidelinePdfArrayList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                ModelGuidelinePdf model = document.toObject(ModelGuidelinePdf.class);
                                guidelinePdfArrayList.add(model);
                            }
                            adapterGuidelinePdf = new AdapterGuidelinePdf(GuidelinesActivity.this, guidelinePdfArrayList);
                            binding.guidelinePdfsRv.setAdapter(adapterGuidelinePdf);
                        }
                    }
                });
    }
}