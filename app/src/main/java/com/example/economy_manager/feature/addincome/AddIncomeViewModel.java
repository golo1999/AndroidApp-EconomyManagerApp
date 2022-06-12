package com.example.economy_manager.feature.addincome;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.model.DatePickerFragment;

import java.time.LocalDate;

public class AddIncomeViewModel extends ViewModel {

    private LocalDate transactionDate = LocalDate.now();
    private UserDetails userDetails;

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public String getDepositsText(@NonNull final Context context) {
        return context.getResources().getString(R.string.deposits).trim();
    }

    public String getIndependentSourcesText(@NonNull final Context context) {
        return context.getResources().getString(R.string.independent_sources).trim();
    }

    public String getSalaryText(@NonNull final Context context) {
        return context.getResources().getString(R.string.salary).trim();
    }

    public String getSavingText(@NonNull final Context context) {
        return context.getResources().getString(R.string.saving).trim();
    }

    // method for limiting the number to only two decimals
    public void limitTwoDecimals(final @NonNull EditText field) {
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

                // if the number NOT is decimal (doesn't contain comma)
                if (!String.valueOf(s).contains(".")) {
                    return;
                }

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

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });
    }

    public void onDateTextClicked(final FragmentManager fragmentManager) {
        new DatePickerFragment(getTransactionDate()).show(fragmentManager, "date_picker");
    }
}