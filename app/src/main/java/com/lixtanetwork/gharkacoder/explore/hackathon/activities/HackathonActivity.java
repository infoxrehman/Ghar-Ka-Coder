package com.lixtanetwork.gharkacoder.explore.hackathon.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.ActivityHackathonBinding;
import com.lixtanetwork.gharkacoder.global.ComingSoonFragment;
import com.lixtanetwork.gharkacoder.global.dashboard.fragments.ProfileFragment;

public class HackathonActivity extends AppCompatActivity {

    private ActivityHackathonBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHackathonBinding.inflate(getLayoutInflater());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ComingSoonFragment comingSoonFragment = new ComingSoonFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.frameLayout.getId(), comingSoonFragment, "ComingSoonFragment");
        fragmentTransaction.commit();

//        binding.hostHackathonBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HackathonActivity.this, HostHackathonActivity.class));
//            }
//        });

    }
}