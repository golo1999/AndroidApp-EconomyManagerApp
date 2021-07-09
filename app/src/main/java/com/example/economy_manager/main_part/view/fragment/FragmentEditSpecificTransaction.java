package com.example.economy_manager.main_part.view.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.economy_manager.R;

public class FragmentEditSpecificTransaction extends Fragment {
    public FragmentEditSpecificTransaction() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_edit_specific_transaction, container, false);

        return view;
    }
}