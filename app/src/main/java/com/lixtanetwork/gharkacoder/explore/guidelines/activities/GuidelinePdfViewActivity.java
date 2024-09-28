package com.lixtanetwork.gharkacoder.explore.guidelines.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.ActivityGuidelinePdfViewBinding;
import com.lixtanetwork.gharkacoder.packages.Constants;

public class GuidelinePdfViewActivity extends AppCompatActivity {

    private ActivityGuidelinePdfViewBinding binding;
    private String bookId, categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuidelinePdfViewBinding.inflate(getLayoutInflater());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        bookId = intent.getStringExtra("bookId");
        categoryId = intent.getStringExtra("categoryId");

        loadBookDetails();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
                                              String pdfUrl = documentSnapshot.getString("url");
                                              loadBookFromUrl(pdfUrl);
                                          }
                                      }
                );


    }

    private void loadBookFromUrl(String pdfUrl) {
        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        reference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        binding.pdfView.fromBytes(bytes)
                                .swipeHorizontal(false)
                                .onPageChange(new OnPageChangeListener() {
                                    @Override
                                    public void onPageChanged(int page, int pageCount) {
                                        int currentPage = (page + 1);
                                        binding.toolbarSubTitleTv.setText(currentPage + "/" + pageCount);
                                    }
                                })
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        Toast.makeText(GuidelinePdfViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        Toast.makeText(GuidelinePdfViewActivity.this, "Error on page " + page + " " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .load();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }
}