package com.lixtanetwork.gharkacoder.global.dashboard.fragments;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lixtanetwork.gharkacoder.AddYourFeedActivity;
import com.lixtanetwork.gharkacoder.ModelFeeds;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.SwipeAdapter;
import com.lixtanetwork.gharkacoder.databinding.FragmentFeedBinding;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
    private FragmentFeedBinding binding;
    private Context mContext;
    private SwipeAdapter swipeAdapter;
    private List<ModelFeeds> list;
    private FirebaseFirestore db;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(LayoutInflater.from(mContext), container, false);
        Window window = this.getActivity().getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = new ArrayList<>();
        swipeAdapter = new SwipeAdapter(mContext, list);
        binding.koloda.setAdapter(swipeAdapter);

        db = FirebaseFirestore.getInstance();

        fetchFeedItems();

        binding.addYourFeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AddYourFeedActivity.class));
            }
        });
    }

    private void fetchFeedItems() {
        db.collection("Feeds")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ModelFeeds item = document.toObject(ModelFeeds.class);
                                list.add(item);
                            }
                            swipeAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}