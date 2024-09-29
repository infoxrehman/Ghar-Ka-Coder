package com.lixtanetwork.gharkacoder.profile.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lixtanetwork.gharkacoder.databinding.ActivityCodrWalletBinding;

public class CodrWalletActivity extends AppCompatActivity {

    private ActivityCodrWalletBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCodrWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}