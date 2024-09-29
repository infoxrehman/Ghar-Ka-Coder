package com.lixtanetwork.gharkacoder.global;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lixtanetwork.gharkacoder.R;
import com.lixtanetwork.gharkacoder.databinding.FragmentComingSoonBinding;

public class ComingSoonFragment extends Fragment {

    private FragmentComingSoonBinding binding;
    private Context mContext;


    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentComingSoonBinding.inflate(LayoutInflater.from(mContext), container, false);
        Window window = this.getActivity().getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.background));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}