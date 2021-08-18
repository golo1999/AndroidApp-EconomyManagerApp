package com.example.economy_manager.main_part.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.economy_manager.R;

import java.util.ArrayList;

public class MonthsSpinnerAdapter extends ArrayAdapter<String> {
    public MonthsSpinnerAdapter(final Context context, final ArrayList<String> monthsList) {
        super(context, 0, monthsList);
    }

    @NonNull
    @Override
    public View getView(final int position, final @Nullable View convertView, final @NonNull ViewGroup parent) {
        return initializeView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(final int position, final @Nullable View convertView, final @NonNull ViewGroup parent) {
        return initializeView(position, convertView, parent);
    }

    private View initializeView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_item, parent, false);
        }

        return convertView;
    }
}
