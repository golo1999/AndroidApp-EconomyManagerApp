package com.example.economy_manager.main_part.viewmodels;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.views.fragments.DatePickerFragment;
import com.example.economy_manager.utilities.MyCustomMethods;

import java.time.LocalDate;

public class AddMoneyViewModel extends ViewModel {
    private LocalDate transactionDate = LocalDate.now();

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDepositsText(final Context context) {
        return context.getResources().getString(R.string.add_money_deposits).trim();
    }

    public String getIndependentSourcesText(final Context context) {
        return context.getResources().getString(R.string.add_money_independent_sources).trim();
    }

    public String getSalaryText(final Context context) {
        return context.getResources().getString(R.string.salary).trim();
    }

    public String getSavingText(final Context context) {
        return context.getResources().getString(R.string.saving).trim();
    }

    // method for limiting the number to only two decimals
    public void limitTwoDecimals(final EditText field) {
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s,
                                          final int start,
                                          final int count,
                                          final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s,
                                      final int start,
                                      final int before,
                                      final int count) {
                final int textLength = String.valueOf(s).length();

                // if the number is decimal (contains comma)
                if (String.valueOf(s).contains(".")) {
                    // saving comma's position
                    final int positionOfComma = String.valueOf(s).indexOf(".");
                    // adding a zero before if the first character is a dot (i.e: .5 => 0.5)
                    if (positionOfComma == 0 && textLength == 1) {
                        final String text = "0" + field.getText();

                        field.setText(text);
                        // putting the cursor at the end
                        field.setSelection(String.valueOf(field.getText()).length());
                    }
                    // if we add more than two decimals
                    if (textLength - positionOfComma > 3) {
                        // putting only the first two decimals
                        field.setText(String.valueOf(field.getText()).substring(0, positionOfComma + 3));
                        // putting the cursor at the end
                        field.setSelection(String.valueOf(field.getText()).length());
                    }
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });
    }

    public void onDateTextClicked(final FragmentManager fragmentManager) {
        final DialogFragment datePickerFragment = new DatePickerFragment(getTransactionDate());

        datePickerFragment.show(fragmentManager, "date_picker");
    }

    public void onSaveButtonClicked() {

    }

    public void setDateText(final LocalDate date,
                            final TextView dateTextView) {
        final String formattedDate = MyCustomMethods.getFormattedDate(date);

        if (!getTransactionDate().equals(date)) {
            setTransactionDate(date);
        }

        if (!String.valueOf(dateTextView.getText()).trim().equals(formattedDate)) {
            dateTextView.setText(formattedDate);
        }
    }
}