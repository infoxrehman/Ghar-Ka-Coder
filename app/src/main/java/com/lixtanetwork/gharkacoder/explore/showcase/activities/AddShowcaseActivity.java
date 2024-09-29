package com.lixtanetwork.gharkacoder.explore.showcase.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.ActivityAddShowcaseBinding;
import com.lixtanetwork.gharkacoder.explore.showcase.adapters.AdapterBannerPick;
import com.lixtanetwork.gharkacoder.explore.showcase.models.ModelBannerPick;

import java.util.ArrayList;
import java.util.HashMap;

public class AddShowcaseActivity extends AppCompatActivity {

    private ActivityAddShowcaseBinding binding;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;
    private ArrayList<ModelBannerPick> bannerImagesList;
    private AdapterBannerPick adapterImagePicked;
    private Uri logoUri;
    private String projectTitle = "", projectTagline = "", projectDescription = "", projectType = "", projectUrl = "";
    private String projectId;
    private final int MAX_BANNERS = 3;

    private final ActivityResultLauncher<Intent> logoActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    logoUri = selectedImageUri;
                    binding.projectIconIv.setImageURI(logoUri);
                }
            } else {
                Toast.makeText(AddShowcaseActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    });

    private final ActivityResultLauncher<Intent> bannerActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    if (bannerImagesList.size() < MAX_BANNERS) {
                        String timestamp = "" + System.currentTimeMillis();
                        ModelBannerPick modelImagePicked = new ModelBannerPick(timestamp, selectedImageUri, null, false);
                        bannerImagesList.add(modelImagePicked);
                        loadBanners();
                    } else {
                        Toast.makeText(AddShowcaseActivity.this, "You can select up to 3 banner images only", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(AddShowcaseActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddShowcaseBinding.inflate(getLayoutInflater());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        setContentView(binding.getRoot());

        setupSpinner();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        projectId = firestore.collection("Projects Showcase").document().getId();

        bannerImagesList = new ArrayList<>();
        loadBanners();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.profileImagePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageGallery(true);
            }
        });

        binding.optionalBannerAddIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageGallery(false);
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void validateData() {
        projectTitle = binding.projectTitleEt.getText().toString().trim();
        projectTagline = binding.projectTaglineEt.getText().toString().trim();
        projectDescription = binding.projectDescriptionEt.getText().toString().trim();
        projectType = binding.projectTypeSpinner.getSelectedItem().toString();
        projectUrl = binding.projectUrlEt.getText().toString().trim();

        if (projectTitle.isEmpty()) {
            Toast.makeText(AddShowcaseActivity.this, "Please enter a project title", Toast.LENGTH_SHORT).show();
        } else if (projectTagline.isEmpty()) {
            Toast.makeText(AddShowcaseActivity.this, "Please enter a project tagline", Toast.LENGTH_SHORT).show();
        } else if (projectDescription.isEmpty()) {
            Toast.makeText(AddShowcaseActivity.this, "Please enter a project description", Toast.LENGTH_SHORT).show();
        } else if (projectType.isEmpty()) {
            Toast.makeText(AddShowcaseActivity.this, "Please select a project type", Toast.LENGTH_SHORT).show();
        } else if (logoUri == null) {
            Toast.makeText(AddShowcaseActivity.this, "Please select a project logo", Toast.LENGTH_SHORT).show();
        } else {
            checkIfUserHasProject();
        }
    }

    private void uploadProjectData() {
        progressDialog.setMessage("Publishing your project...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String uid = firebaseUser.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("projectId", projectId);
        hashMap.put("projectTitle", projectTitle);
        hashMap.put("projectTagline", projectTagline);
        hashMap.put("projectDescription", projectDescription);
        hashMap.put("projectType", projectType);
        hashMap.put("projectUrl", projectUrl);
        hashMap.put("projectVotes", 0);
        hashMap.put("uploadedProjectDate", timestamp);

        firestore.collection("Projects Showcase").document(projectId)
                .set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        uploadLogoAndBanners(projectId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddShowcaseActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadLogoAndBanners(String projectId) {
        if (logoUri != null) {
            String filePathAndName = "Projects Showcase/" + projectId + "/Logo/" + System.currentTimeMillis();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(logoUri)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(uriTask -> {
                        if (uriTask.isSuccessful()) {
                            String logoUrl = uriTask.getResult().toString();
                            firestore.collection("Projects Showcase").document(projectId)
                                    .update("projectLogo", logoUrl);
                        }
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddShowcaseActivity.this, "Failed to upload logo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        ArrayList<String> bannerUrls = new ArrayList<>();
        for (int i = 0; i < bannerImagesList.size(); i++) {
            ModelBannerPick modelImagePicked = bannerImagesList.get(i);
            String filePathAndName = "Projects Showcase/" + projectId + "/Banners/" + modelImagePicked.getId();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(modelImagePicked.getImageUri())
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(uriTask -> {
                        if (uriTask.isSuccessful()) {
                            String bannerUrl = uriTask.getResult().toString();
                            bannerUrls.add(bannerUrl);

                            if (bannerUrls.size() == bannerImagesList.size()) {
                                 firestore.collection("Projects Showcase").document(projectId)
                                        .update("optionalBanners", bannerUrls)
                                        .addOnSuccessListener(aVoid -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddShowcaseActivity.this, "Project uploaded successfully!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(AddShowcaseActivity.this, "Failed to update banners: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddShowcaseActivity.this, "Failed to upload banner: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void loadBanners() {
        adapterImagePicked = new AdapterBannerPick(this, bannerImagesList, projectId);
        binding.images.setVisibility(View.VISIBLE);
        binding.images.setAdapter(adapterImagePicked);
    }

    private void pickImageGallery(boolean isLogo) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (isLogo) {
            logoActivityResultLauncher.launch(intent);
        } else {
            bannerActivityResultLauncher.launch(intent);
        }
    }

    private void setupSpinner() {
        String[] options = {"Website", "App", "Web App"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.projectTypeSpinner.setAdapter(adapter);
    }

    private void checkIfUserHasProject() {
        firestore.collection("Projects Showcase")
                .whereEqualTo("uid", firebaseUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                Toast.makeText(AddShowcaseActivity.this, "You have already submitted a project.", Toast.LENGTH_SHORT).show();
                            } else {
                                uploadProjectData();
                            }
                        } else {
                            Toast.makeText(AddShowcaseActivity.this, "Error checking existing projects: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}