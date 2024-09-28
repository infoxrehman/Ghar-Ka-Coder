package com.lixtanetwork.gharkacoder.profile.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.ActivityEditProfileBinding;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private Uri imageUri = null;
    private ProgressDialog progressDialog;
    private String myAccountType = "", name = "", dob = "", email = "", bio = "", userName = "", representBy = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        setupSpinner();

        loadUserInfo();

        binding.profileImagePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageGallery();
            }
        });

        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void validateData() {

        name = binding.nameEt.getText().toString().trim();
        userName = binding.userNameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        bio = binding.bioTv.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(bio)) {
            Toast.makeText(this, "Enter a bio", Toast.LENGTH_SHORT).show();
        } else {
            if (imageUri == null) {
                updateProfile("");
            } else {
                uploadImage();
            }
        }
    }

    private void updateProfile(String imageUrl) {
        progressDialog.setMessage("Updating user profile");
        progressDialog.show();

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("dob", dob);
        if (imageUri != null) {
            hashMap.put("profileImage", imageUrl);
        }

        hashMap.put("email", email);
        hashMap.put("representBy", representBy);

        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());
        userRef.update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Failed to update db due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImage() {
        progressDialog.setMessage("Updating profile image");
        progressDialog.show();

        String filePathAndName = "Profile Images/" + firebaseAuth.getUid();

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedImageUrl = uriTask.getResult().toString();
                        updateProfile(uploadedImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Failed to upload image due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void pickImageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        binding.profileIv.setImageURI(imageUri);
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void loadUserInfo() {

        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                String email = documentSnapshot.getString("email");
                String name = documentSnapshot.getString("name");
                String profileImage = documentSnapshot.getString("profileImage");
                representBy = documentSnapshot.getString("representBy");
                String userName = documentSnapshot.getString("userName");
                String bio = documentSnapshot.getString("bio");
                myAccountType = documentSnapshot.getString("accountType");

                binding.emailEt.setEnabled(false);

                binding.nameEt.setText(name);
                binding.emailEt.setText(email);
                binding.bioTv.setText(bio);
                binding.userNameEt.setText(userName);

                ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.representAsSpinner.getAdapter();
                if (adapter != null) {
                    int position = adapter.getPosition(representBy);
                    binding.representAsSpinner.setSelection(position);
                }

                try {
                    Glide.with(getApplicationContext())
                            .load(profileImage)
                            .placeholder(R.drawable.profile_icon)
                            .into(binding.profileIv);
                } catch (Exception ignored) {
                }
            }
        });
    }

    private void setupSpinner() {
        String[] options = {"Professional", "Developer", "Student", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.representAsSpinner.setAdapter(adapter);
    }

}