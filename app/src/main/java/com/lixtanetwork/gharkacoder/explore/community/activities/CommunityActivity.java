package com.lixtanetwork.gharkacoder.explore.community.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.ActivityCommunityBinding;

public class CommunityActivity extends AppCompatActivity {

    private ActivityCommunityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommunityBinding.inflate(getLayoutInflater());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}