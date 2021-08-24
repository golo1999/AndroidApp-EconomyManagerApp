package com.example.economy_manager.main_part.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.economy_manager.R;
import com.example.economy_manager.models.UserDetails;

import java.util.ArrayList;

public class MonthsSpinnerAdapter_NOT_WORKING extends ArrayAdapter<String> {
    private final UserDetails userDetails;

    public MonthsSpinnerAdapter_NOT_WORKING(final Context context, final ArrayList<String> monthsList, final UserDetails userDetails) {
        super(context, 0, monthsList);

        this.userDetails = userDetails;
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

        // all spinner elements are aligned to center
        ((TextView) convertView).setGravity(Gravity.CENTER);

        if (userDetails != null) {
            final boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
            final int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;

            // setting text color based on the selected theme
            ((TextView) convertView).setTextColor(itemsColor);
        }

        return convertView;
    }
}
