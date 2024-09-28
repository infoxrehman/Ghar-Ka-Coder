package com.lixtanetwork.gharkacoder.global.dashboard.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.lixtanetwork.gharkacoder.global.dashboard.fragments.FeedFragment;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.global.dashboard.fragments.ExploreFragment;
import com.lixtanetwork.gharkacoder.global.dashboard.fragments.ProfileFragment;
import com.lixtanetwork.gharkacoder.global.dashboard.fragments.HomeFragment;
import com.lixtanetwork.gharkacoder.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        setContentView(binding.getRoot());

        showHomeFragment();

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.home) {
                    showHomeFragment();
                    return true;

                } else if (itemId == R.id.explore) {
                    showExploreFragment();
                    return true;

                } else if (itemId == R.id.feed) {
                    showFeedFragment();
                    return true;

                } else if (itemId == R.id.profile) {
                    showProfileFragment();
                    return true;

                } else {
                    return false;
                }
            }
        });

    }

    private void showHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.frameLayout.getId(), homeFragment, "HomeFragment");
        fragmentTransaction.commit();
    }

    private void showExploreFragment() {
        ExploreFragment exploreFragment = new ExploreFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.frameLayout.getId(), exploreFragment, "ExploreFragment");
        fragmentTransaction.commit();
    }

    private void showFeedFragment() {
        FeedFragment feedFragment = new FeedFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.frameLayout.getId(), feedFragment, "feedFragment");
        fragmentTransaction.commit();
    }

    private void showProfileFragment() {
        ProfileFragment profileFragment = new ProfileFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.frameLayout.getId(), profileFragment, "ProfileFragment");
        fragmentTransaction.commit();
    }

}
