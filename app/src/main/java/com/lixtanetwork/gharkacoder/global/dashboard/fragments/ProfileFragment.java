package com.lixtanetwork.gharkacoder.global.dashboard.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lixtanetwork.gharkacoder.databinding.CodrAirdropTokenDropDailogBinding;
import com.lixtanetwork.gharkacoder.global.loginandregister.activities.LoginActivity;
import com.lixtanetwork.gharkacoder.profile.activities.EditProfileActivity;
import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Context mContext;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(LayoutInflater.from(mContext), container, false);
        Window window = this.getActivity().getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        loadUserInfo();

        binding.editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, EditProfileActivity.class));
            }
        });

        binding.getVerifiedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Oops! My friend seem that you account age is too early. Engagement more to get verified and earn badges.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.privacyPolicyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLink();
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        binding.codrTokenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDefaultTokenLaunchDialog();
            }
        });

    }

    private void showDefaultTokenLaunchDialog() {
        CodrAirdropTokenDropDailogBinding codrAirdropTokenDropDailogBinding = CodrAirdropTokenDropDailogBinding.inflate(LayoutInflater.from(mContext));
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(codrAirdropTokenDropDailogBinding.getRoot());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

        codrAirdropTokenDropDailogBinding.okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(mContext, LoginActivity.class));
            getActivity().finishAffinity();
        }
    }

    private void loadUserInfo() {
        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String email = documentSnapshot.getString("email");
                            String name = documentSnapshot.getString("name");
                            String userName = documentSnapshot.getString("userName");
                            String bio = documentSnapshot.getString("bio");
                            String representBy = documentSnapshot.getString("representBy");
                            String profileImage = documentSnapshot.getString("profileImage");

                            Glide.with(mContext).load(profileImage).placeholder(R.drawable.profile_icon).into(binding.profileIv);

                            binding.usernameTv.setText(userName);
                            binding.bioTv.setText(bio);

                        }
                    }
                });
    }

    private void goLink() {
        Uri uri = Uri.parse("https://gharkacoder.netlify.app/privacypolicy");
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

}