package com.example.economy_manager.utility;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.economy_manager.utility.MyCustomVariables;

import java.util.ArrayList;

public class TransactionTypesSpinnerAdapter extends ArrayAdapter<String> {

    public TransactionTypesSpinnerAdapter(final Context context,
                                          final int textViewResourceId,
                                          final ArrayList<String> transactionTypesList) {
        super(context, textViewResourceId, transactionTypesList);
    }

    @Override
    public View getDropDownView(final int position,
                                final @Nullable View convertView,
                                final @NonNull ViewGroup parent) {
        final View dropDownView = super.getDropDownView(position, convertView, parent);

        final boolean darkTheme = MyCustomVariables.getUserDetails() != null ?
                MyCustomVariables.getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

        final int itemsColor = !darkTheme ? Color.WHITE : Color.BLACK;

        ((TextView) dropDownView).setGravity(Gravity.CENTER);
        // setting items' text color based on the selected theme
        ((TextView) dropDownView).setTextColor(itemsColor);

        return dropDownView;
    }
}