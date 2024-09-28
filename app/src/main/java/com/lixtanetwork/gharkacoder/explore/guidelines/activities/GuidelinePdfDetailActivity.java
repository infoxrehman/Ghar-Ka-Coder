package com.lixtanetwork.gharkacoder.explore.guidelines.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.ActivityGuidelinePdfDetailBinding;
import com.lixtanetwork.gharkacoder.packages.Constants;

public class GuidelinePdfDetailActivity extends AppCompatActivity {

    private String bookId, bookTitle, bookUrl, categoryId;
    private final ActivityResultLauncher<String> resultPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        Constants.downloadBook(GuidelinePdfDetailActivity.this, bookId, bookTitle, bookUrl);
                    } else {
                        Constants.downloadBook(GuidelinePdfDetailActivity.this, bookId, bookTitle, bookUrl);
                    }
                }
            }
    );
    private ActivityGuidelinePdfDetailBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuidelinePdfDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        categoryId = intent.getStringExtra("categoryId");

        binding.downloadBookBtn.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);


        loadBookDetails();

        Constants.incrementBookViewCount(categoryId, bookId);

        Constants.incrementBookViewCount(categoryId, bookId);

        binding.backBtn.setOnClickListener(view -> onBackPressed());

        binding.readBookBtn.setOnClickListener(view -> {

            Intent intent1 = new Intent(GuidelinePdfDetailActivity.this, GuidelinePdfViewActivity.class);
            intent1.putExtra("bookId", bookId);
            intent1.putExtra("categoryId", categoryId);
            startActivity(intent1);
        });

        binding.downloadBookBtn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(GuidelinePdfDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Constants.downloadBook(GuidelinePdfDetailActivity.this, bookId, bookTitle, bookUrl);
                Constants.incrementBookDownloadCount(categoryId, bookId);
            } else {
                resultPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
    }

    private void loadBookDetails() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference bookRef = db.collection("Guideline Categories").document(categoryId).collection("Guideline Books").document(bookId);

        bookRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            bookTitle = documentSnapshot.getString("title");
                            String description = documentSnapshot.getString("description");
                            String viewsCount = "" + documentSnapshot.get("viewsCount");
                            bookUrl = documentSnapshot.getString("url");
                            String timestamp = "" + documentSnapshot.get("timestamp");

                            binding.downloadBookBtn.setVisibility(View.VISIBLE);

                            String date = Constants.formatTimestamp(Long.parseLong(timestamp));

                            Constants.loadPdfFromUrlSinglePage(bookUrl, binding.pdfView, binding.progressBar, null, GuidelinePdfDetailActivity.this);
                            Constants.loadPdfSize(bookUrl, bookTitle, binding.sizeTv);

                            binding.titleTv.setText(bookTitle);
                            binding.descriptionTv.setText(description);
                            binding.viewCountTv.setText(viewsCount.replace("null", "N/A"));
                            binding.dateTv.setText(date);
                        } else {
                            Toast.makeText(GuidelinePdfDetailActivity.this, "Book not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GuidelinePdfDetailActivity.this, "Failed to load book details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
