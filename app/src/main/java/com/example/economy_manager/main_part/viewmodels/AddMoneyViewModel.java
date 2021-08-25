package com.example.economy_manager.main_part.viewmodels;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;

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

    public void setDateText(final LocalDate date, final TextView dateText) {
        final String dayName = date.getDayOfWeek().name().charAt(0) +
                date.getDayOfWeek().name().substring(1).toLowerCase();

        final String monthName = String.valueOf(date.getMonth()).charAt(0) +
                String.valueOf(date.getMonth()).substring(1).toLowerCase();

        final int day = date.getDayOfMonth();

        final StringBuilder transactionDate = new StringBuilder(dayName)
                .append(", ")
                .append(monthName)
                .append(" ")
                .append(day);
        // displaying the year if it's not the current one
        if (date.getYear() != LocalDate.now().getYear()) {
            transactionDate.append(", ")
                    .append(date.getYear());
        }

        if (!getTransactionDate().equals(date)) {
            setTransactionDate(date);
        }

        dateText.setText(transactionDate);
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
}