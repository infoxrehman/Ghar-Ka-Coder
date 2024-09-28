package com.lixtanetwork.gharkacoder.packages;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Locale;

public class Constants extends Application {

    public static final long MAX_BYTES_PDF = 50000000;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static String formatTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);

        String date = DateFormat.format("dd/MM/yyyy", cal).toString();

        return date;
    }

    public static void loadPdfSize(String pdfUrl, String pdfTitle, TextView sizeTv) {

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        double bytes = storageMetadata.getSizeBytes();

                        double kb = bytes / 1024;
                        double mb = kb / 1024;

                        if (mb >= 1) {
                            sizeTv.setText(String.format("%.2f", mb) + "MB");
                        } else if (kb >= 1) {
                            sizeTv.setText(String.format("%.2f", kb) + "KB");
                        } else {
                            sizeTv.setText(String.format("%.2f", bytes) + "bytes");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    public static void loadPdfFromUrlSinglePage(String pdfUrl, PDFView pdfView, ProgressBar progressBar, TextView pagesTv, Context context) {

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        pdfView.fromBytes(bytes)
                                .pages(0)
                                .spacing(0)
                                .swipeHorizontal(false)
                                .enableSwipe(false)
                                .enableDoubletap(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {
                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        if (pagesTv != null) {
                                            pagesTv.setText("" + nbPages);
                                        }
                                    }
                                })
                                .load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    public static void incrementBookViewCount(String categoryId, String bookId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference bookRef = db.collection("Guideline Categories")
                .document(categoryId)
                .collection("Guideline Books")
                .document(bookId);

        bookRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long viewsCount = documentSnapshot.getLong("viewsCount");
                if (viewsCount == null) {
                    viewsCount = 0L;
                }
                bookRef.update("viewsCount", FieldValue.increment(1))
                        .addOnSuccessListener(aVoid -> {
                        })
                        .addOnFailureListener(e -> {
                        });
            } else {
            }
        }).addOnFailureListener(e -> {
        });
    }

    public static void downloadBook(Context context, String bookId, String bookTitle, String bookUrl) {
        String nameWithExtension = bookTitle + ".pdf";

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Downloading " + nameWithExtension + "....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        storageReference.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        saveDownloadedBook(context, progressDialog, bytes, nameWithExtension, bookId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed to download guideline due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private static void saveDownloadedBook(Context context, ProgressDialog progressDialog, byte[] bytes, String nameWithExtension, String bookId) {
        try {
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            downloadFolder.mkdirs();
            String filePath = downloadFolder.getPath() + "/" + nameWithExtension;

            FileOutputStream out = new FileOutputStream(filePath);
            out.write(bytes);
            out.close();

            Toast.makeText(context, "Saved to Download Folder", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        } catch (Exception e) {
            Toast.makeText(context, "Failed saving to Download Folder due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    public static void incrementBookDownloadCount(String categoryId, String bookId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference bookRef = db.collection("Guideline Categories")
                .document(categoryId)
                .collection("Guideline Books")
                .document(bookId);

        bookRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long downloadsCount = documentSnapshot.getLong("downloadsCount");
                if (downloadsCount == null) {
                    downloadsCount = 0L;
                }
                bookRef.update("downloadsCount", FieldValue.increment(1))
                        .addOnSuccessListener(aVoid -> {
                        })
                        .addOnFailureListener(e -> {
                        });
            }
        }).addOnFailureListener(e -> {
        });
    }

}
