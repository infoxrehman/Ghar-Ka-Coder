package com.lixtanetwork.gharkacoder.global.dashboard.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lixtanetwork.gharkacoder.databinding.DailyCodingChallengeQuestionDialogBinding;
import com.lixtanetwork.gharkacoder.explore.hackathon.activities.HackathonActivity;
import com.lixtanetwork.gharkacoder.explore.showcase.activities.ShowcaseActivity;
import com.lixtanetwork.gharkacoder.global.dashboard.adapters.AdapterFeaturedGuideline;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.FragmentHomeBinding;
import com.lixtanetwork.gharkacoder.explore.guidelines.models.ModelGuidelinePdf;
import com.lixtanetwork.gharkacoder.global.dashboard.models.DailyCodingChallengeModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Context mContext;
    private ArrayList<ModelGuidelinePdf> pdfArrayList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseMessaging firebaseMessaging;
    private int currentQuestionIndex = 0;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(mContext), container, false);
        Window window = this.getActivity().getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseMessaging = FirebaseMessaging.getInstance();

        getFCMToken();

        loadPdfList();

        loadTopProjectShowcase();

        checkForPoints(firebaseUser.getUid());

        binding.dailyCodingChallengeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDailyQuestionDialog();
            }
        });

        binding.listYourProjectsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, ShowcaseActivity.class));
            }
        });

        binding.hackathonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, HackathonActivity.class));
            }
        });

    }

    void getFCMToken() {
        firebaseMessaging.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("fcmToken", token);

                    firebaseFirestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });
                }
            }
        });
    }

    private void loadTopProjectShowcase() {
        firebaseFirestore.collection("Projects Showcase")
                .orderBy("projectVotes", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot topProject = queryDocumentSnapshots.getDocuments().get(0);

                            String projectName = topProject.getString("projectTitle");
                            String projectTagline = topProject.getString("projectTagline");
                            String projectIcon = topProject.getString("projectLogo");

                            Glide.with(mContext).load(projectIcon).placeholder(R.drawable.showcase_src).into(binding.projectImage);
                            binding.projectTitle.setText(projectName);
                            binding.projectTagline.setText(projectTagline);

                        }
                    }
                });
    }

    private void loadPdfList() {
        pdfArrayList = new ArrayList<>();
        String categoryId = "wsXk5ibtuXzApZaYHNIX";
        CollectionReference ref = firebaseFirestore.collection("Guideline Categories")
                .document(categoryId)
                .collection("Guideline Books");

        ref.whereGreaterThanOrEqualTo("viewsCount", 10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ModelGuidelinePdf model = document.toObject(ModelGuidelinePdf.class);
                            pdfArrayList.add(model);
                        }
                        AdapterFeaturedGuideline adapter = new AdapterFeaturedGuideline(mContext, pdfArrayList);
                        binding.guidelineRv.setAdapter(adapter);
                    }
                });
    }

    public void showDailyQuestionDialog() {

        firebaseFirestore.collection("Daily Coding Challenge Questions").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> dslist = queryDocumentSnapshots.getDocuments();
                if (!dslist.isEmpty()) {
                    int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                    currentQuestionIndex = dayOfYear % dslist.size();
                    DailyCodingChallengeModel dailyQuestionsModel = dslist.get(currentQuestionIndex).toObject(DailyCodingChallengeModel.class);

                    binding.dailyCodingChallengeTv.setText(dailyQuestionsModel.getQuestion());

                    binding.dailyCodingChallengeTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showQuestionInDialog(dailyQuestionsModel);
                        }
                    });

                } else {
                    binding.dailyCodingChallengeTv.setText("No question found");
                }
            }
        });

    }

    private void showQuestionInDialog(DailyCodingChallengeModel dailyCodingChallengeModel) {

        String uid = firebaseUser.getUid();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DocumentReference responseRef = firebaseFirestore.collection("Daily Coding Challenge Responses").document(date).collection("responses").document(uid);

        responseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                Toast.makeText(mContext, "Already you have submitted your today's response! Come back tomorrow", Toast.LENGTH_SHORT).show();
                return;
            }

            DailyCodingChallengeQuestionDialogBinding dailyCodingChallengeQuestionDialogBinding = DailyCodingChallengeQuestionDialogBinding.inflate(LayoutInflater.from(mContext));
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(dailyCodingChallengeQuestionDialogBinding.getRoot());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.setCanceledOnTouchOutside(false);

            dailyCodingChallengeQuestionDialogBinding.questionTitle.setText(dailyCodingChallengeModel.getQuestion());
            dailyCodingChallengeQuestionDialogBinding.option1.setText(dailyCodingChallengeModel.getChoice1());
            dailyCodingChallengeQuestionDialogBinding.option2.setText(dailyCodingChallengeModel.getChoice2());
            dailyCodingChallengeQuestionDialogBinding.option3.setText(dailyCodingChallengeModel.getChoice3());
            dailyCodingChallengeQuestionDialogBinding.option4.setText(dailyCodingChallengeModel.getChoice4());

            dailyCodingChallengeQuestionDialogBinding.submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String answer = getSelectedAnswer(dailyCodingChallengeQuestionDialogBinding, dailyCodingChallengeModel);
                    if (answer != null && answer.equals(dailyCodingChallengeModel.getCorrectOne())) {
                        saveUserResponseToFirestore(dailyCodingChallengeModel, answer);
                        Toast.makeText(mContext, "Woo! Correct, you are quiet intelligent doh!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } else if (answer != null) {
                        saveUserResponseToFirestore(dailyCodingChallengeModel, answer);
                        Toast.makeText(mContext, "Ohh ohh! You went wrong this time!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(mContext, "Answer to select karle mere jaan", Toast.LENGTH_SHORT).show();
                    }
                    alertDialog.dismiss();
                }
            });
        });
    }

    private String getSelectedAnswer(DailyCodingChallengeQuestionDialogBinding dailyCodingChallengeQuestionDialogBinding, DailyCodingChallengeModel dailyCodingChallengeModel) {

        RadioButton radioButton = dailyCodingChallengeQuestionDialogBinding.getRoot().findViewById(dailyCodingChallengeQuestionDialogBinding.optionGroup.getCheckedRadioButtonId());
        if (radioButton != null) {
            return radioButton.getText().toString();
        } else {
            return null;
        }
    }

    private void saveUserResponseToFirestore(DailyCodingChallengeModel dailyCodingChallengeModel, String answer) {
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            String name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            DocumentReference docRef = firebaseFirestore.collection("Daily Coding Challenge Responses").document(date).collection("responses").document(uid);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("uid", uid);
            responseMap.put("name", name);
            responseMap.put("email", email);
            responseMap.put("question", dailyCodingChallengeModel.getQuestion());
            responseMap.put("correctAnswer", dailyCodingChallengeModel.getCorrectOne());
            responseMap.put("answer", answer);

            boolean isCorrect = answer.equals(dailyCodingChallengeModel.getCorrectOne());

            docRef.set(responseMap).addOnSuccessListener(aVoid -> {
                Toast.makeText(mContext, "Response saved", Toast.LENGTH_SHORT).show();
                if (isCorrect) {
                    updateUserPoints(uid, true);
                } else {
                    updateUserPoints(uid, false);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(mContext, "Failed to save response", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateUserPoints(String uid, boolean isCorrect) {

        DocumentReference userDocRef = firebaseFirestore.collection("Users").document(uid);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long currentPoints = documentSnapshot.getLong("points");

                if (currentPoints == null) {
                    currentPoints = 0L;
                }
                if (isCorrect) {
                    currentPoints += 2;
                } else {
                    currentPoints = 0L;
                }

                HashMap<String, Object> userDetails = new HashMap<>();
                userDetails.put("points", currentPoints);

                userDocRef.update(userDetails)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
            }
        });
    }

    private void checkForPoints(String uid) {
        DocumentReference userDocRef = firebaseFirestore.collection("Users").document(uid);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long currentPoints = documentSnapshot.getLong("points");

                if (currentPoints == null) {
                    userDocRef.update("points", 0L)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                }
            }
        });
    }

}