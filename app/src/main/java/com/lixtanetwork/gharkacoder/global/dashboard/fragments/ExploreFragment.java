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
import android.widget.Toast;

import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.FragmentExploreBinding;
import com.lixtanetwork.gharkacoder.explore.guidelines.activities.GuidelinesActivity;
import com.lixtanetwork.gharkacoder.explore.hackathon.activities.HackathonActivity;
import com.lixtanetwork.gharkacoder.explore.showcase.activities.ShowcaseActivity;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private Context mContext;

    public ExploreFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExploreBinding.inflate(LayoutInflater.from(mContext), container, false);
        Window window = this.getActivity().getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.guidelinesTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, GuidelinesActivity.class));
            }
        });

        binding.communityTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Woo! we are still working on this feature buddy...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.showcaseTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ShowcaseActivity.class));
            }
        });


        binding.hackathonTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, HackathonActivity.class));
            }
        });

    }
}