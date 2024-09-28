package com.lixtanetwork.gharkacoder.global.loginandregister.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    private String name = "", email = "", password = "", cPassword = "", userName = "";
    private Uri imageUri = null;
    String bio = "", representBy = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        binding.backBtn.setVisibility(View.VISIBLE);
        binding.detailLabel.setText("Enter your details");
        binding.registerBasicLayout.setVisibility(View.VISIBLE);
        binding.registerPostLayout.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
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
        password = binding.passwordEt.getText().toString().trim();
        cPassword = binding.cPasswordEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cPassword)) {
            Toast.makeText(this, "Confirm password!", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(cPassword)) {
            Toast.makeText(this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Invalid username!", Toast.LENGTH_SHORT).show();
        } else {
            checkUsernameAvailability();
        }
    }

    private void checkUsernameAvailability() {
        progressDialog.setMessage("Checking username availability...");
        progressDialog.show();

        firestore.collection("Users")
                .whereEqualTo("userName", userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                Toast.makeText(RegisterActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                createUserAccount();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error checking username", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                        }
                    }
                });
    }

    private void createUserAccount() {
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUserInfoToDb();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void updateUserInfoToDb() {

        progressDialog.setMessage("Please wait");

        String uid = firebaseAuth.getUid();
        long timestamp = System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("userName", userName);
        hashMap.put("profileImage", "");
        hashMap.put("dob", "");
        hashMap.put("bio", "");
        hashMap.put("representBy", "");
        hashMap.put("accountType", "Email");
        hashMap.put("timestamp", timestamp);

        firestore.collection("Users").document(uid)
                .set(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        postRegistrationDetails(uid);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void postRegistrationDetails(String uid) {
        binding.backBtn.setVisibility(View.GONE);
        binding.detailLabel.setText("Just few more details, don't go back");
        binding.registerBasicLayout.setVisibility(View.GONE);
        binding.registerPostLayout.setVisibility(View.VISIBLE);

        setupSpinner();

        binding.profileImagePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageGallery();
            }
        });

        binding.letGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateBio(uid);
            }
        });

    }

    private void setupSpinner() {
        String[] options = {"Professional", "Developer", "Student", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.representAsSpinner.setAdapter(adapter);
    }

    private void validateBio(String uid) {

        representBy = binding.representAsSpinner.getSelectedItem().toString();

        bio = binding.bioTv.getText().toString().trim();

        if (bio.isEmpty()) {
            Toast.makeText(this, "Please enter your bio", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (imageUri == null) {
                savePostDetails("", uid);
            } else {
                uploadImage(uid);
            }
        }
    }

    private void savePostDetails(String imageUrl, String uid) {

        HashMap<String, Object> userDetails = new HashMap<>();
        userDetails.put("bio", bio);
        userDetails.put("representBy", representBy);

        if (imageUri != null) {
            userDetails.put("profileImage", imageUrl);
        }

        firestore.collection("Users").document(uid)
                .update(userDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RegisterActivity.this, "Yoo! Account created successfully", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RegisterActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void uploadImage(String uid) {
        progressDialog.setMessage("Just a few second more...");
        progressDialog.show();

        String filePathAndName = "Profile Images/" + firebaseAuth.getUid();

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    String uploadedImageUrl = uriTask.getResult().toString();
                    savePostDetails(uploadedImageUrl, uid);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Failed to upload image due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}