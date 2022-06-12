package com.example.economy_manager.model;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.economy_manager.feature.settings.SettingsActivity;

public class CurrenciesSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;

    public CurrenciesSpinnerAdapter(final Context context,
                                    final int textViewResourceId,
                                    final String[] currenciesList) {
        super(context, textViewResourceId, currenciesList);
        this.context = context;
    }

    @Override
    public View getDropDownView(final int position,
                                final @Nullable View convertView,
                                final @NonNull ViewGroup parent) {
        final View dropDownView = super.getDropDownView(position, convertView, parent);

        ((TextView) dropDownView).setGravity(Gravity.CENTER);
        ((SettingsActivity) context).setCurrencySelectorSpinnerTheme(dropDownView);

        return dropDownView;
    }
}