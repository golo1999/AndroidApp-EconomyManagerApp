package com.example.economy_manager.utility;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.economy_manager.R;

import java.util.ArrayList;

public class TransactionTypesSpinnerAdapter extends ArrayAdapter<String> {

    private final Context context;

    public TransactionTypesSpinnerAdapter(final Context context,
                                          final int textViewResourceId,
                                          final ArrayList<String> transactionTypesList) {
        super(context, textViewResourceId, transactionTypesList);
        this.context = context;
    }

    @Override
    public View getDropDownView(final int position,
                                final @Nullable View convertView,
                                final @NonNull ViewGroup parent) {
        final View dropDownView = super.getDropDownView(position, convertView, parent);
        final boolean isDarkThemeEnabled = MyCustomVariables.getUserDetails() != null ?
                MyCustomVariables.getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

        // centering all spinner's items' text
        ((TextView) dropDownView).setGravity(Gravity.CENTER);

        dropDownView.setBackgroundColor(context.getColor(isDarkThemeEnabled ?
                R.color.primaryLight : R.color.primaryDark));
        ((TextView) dropDownView).setTextColor(context.getColor(isDarkThemeEnabled ?
                R.color.quaternaryLight : R.color.secondaryDark));

        return dropDownView;
    }
}