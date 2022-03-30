package com.example.economy_manager.feature.addexpense;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.utility.DatePickerFragment;

import java.time.LocalDate;

public class AddExpenseViewModel extends ViewModel {

    private LocalDate transactionDate = LocalDate.now();

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getBillsText(final Context context) {
        return context.getResources().getString(R.string.bills).trim();
    }

    public String getCarText(final Context context) {
        return context.getResources().getString(R.string.car).trim();
    }

    public String getClothesText(final Context context) {
        return context.getResources().getString(R.string.clothes).trim();
    }

    public String getCommunicationsText(final Context context) {
        return context.getResources().getString(R.string.communications).trim();
    }

    public String getEatingOutText(final Context context) {
        return context.getResources().getString(R.string.eating_out).trim();
    }

    public String getEntertainmentText(final Context context) {
        return context.getResources().getString(R.string.entertainment).trim();
    }

    public String getFoodText(final Context context) {
        return context.getResources().getString(R.string.food).trim();
    }

    public String getGiftsText(final Context context) {
        return context.getResources().getString(R.string.gifts).trim();
    }

    public String getHealthText(final Context context) {
        return context.getResources().getString(R.string.health).trim();
    }

    public String getHouseText(final Context context) {
        return context.getResources().getString(R.string.house).trim();
    }

    public String getPetsText(final Context context) {
        return context.getResources().getString(R.string.pets).trim();
    }

    public String getSportsText(final Context context) {
        return context.getResources().getString(R.string.sports).trim();
    }

    public String getTaxiText(final Context context) {
        return context.getResources().getString(R.string.taxi).trim();
    }

    public String getToiletryText(final Context context) {
        return context.getResources().getString(R.string.toiletry).trim();
    }

    public String getTransportText(final Context context) {
        return context.getResources().getString(R.string.transport).trim();
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

                // if the number is NOT decimal (does NOT contain comma)
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