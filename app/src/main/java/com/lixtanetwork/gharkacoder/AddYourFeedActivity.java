package com.lixtanetwork.gharkacoder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.lixtanetwork.gharkacoder.databinding.ActivityAddYourFeedBinding;

import java.util.HashMap;
import java.util.Map;

public class AddYourFeedActivity extends AppCompatActivity {

    private ActivityAddYourFeedBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Uri imageUri = null;
    private ProgressDialog progressDialog;
    private String userName, profilePicUrl, name, feedContent, feedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddYourFeedBinding.inflate(getLayoutInflater());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        feedId = firebaseFirestore.collection("Feeds").document().getId();

        binding.feedUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndUploadFeed();
            }
        });

        binding.selectFeedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageGallery();
            }
        });

        loadUserInfo();
    }

    private void validateAndUploadFeed() {

        feedContent = binding.feedContent.getText().toString().trim();

        if (TextUtils.isEmpty(feedContent)) {
            Toast.makeText(this, "Feed content cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            uploadFeed("");
        } else {
            uploadImage();
        }
    }

    private void uploadImage() {
        progressDialog.setMessage("Uploading image...");
        progressDialog.show();

        String filePathAndName = "Feed Images/" + firebaseAuth.getUid();

        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedImageUrl = uriTask.getResult().toString();
                        uploadFeed(uploadedImageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddYourFeedActivity.this, "Failed to upload image due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadFeed(String imageUrl) {
        progressDialog.setMessage("Uploading feed...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        feedContent = binding.feedContent.getText().toString().trim();
        Map<String, Object> feedDataMap = new HashMap<>();
        feedDataMap.put("feedId", feedId);
        feedDataMap.put("feedContent", feedContent);
        feedDataMap.put("userName", userName);
        feedDataMap.put("uid", firebaseAuth.getUid());
        feedDataMap.put("name", name);
        feedDataMap.put("profilePic", profilePicUrl);
        feedDataMap.put("feedDate", timestamp);

        if (!TextUtils.isEmpty(imageUrl)) {
            feedDataMap.put("feedImage", imageUrl);
        }

        firebaseFirestore.collection("Feeds").document(feedId)
                .set(feedDataMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(AddYourFeedActivity.this, "Yeah! Feed uploaded", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddYourFeedActivity.this, "Failed to upload feed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserInfo() {
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getUid());

        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    name = documentSnapshot.getString("name");
                    userName = documentSnapshot.getString("userName");
                    profilePicUrl = documentSnapshot.getString("profileImage");

                }
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
                        binding.selectFeedImage.setImageURI(imageUri);
                    } else {
                        Toast.makeText(AddYourFeedActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}
