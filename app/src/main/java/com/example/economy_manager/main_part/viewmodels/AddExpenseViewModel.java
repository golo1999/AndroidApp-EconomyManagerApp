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
        return context.getResources().getString(R.string.subtract_money_bills).trim();
    }

    public String getCarText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_car).trim();
    }

    public String getClothesText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_clothes).trim();
    }

    public String getCommunicationsText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_communications).trim();
    }

    public String getEatingOutText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_eating_out).trim();
    }

    public String getEntertainmentText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_entertainment).trim();
    }

    public String getFoodText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_food).trim();
    }

    public String getGiftsText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_gifts).trim();
    }

    public String getHealthText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_health).trim();
    }

    public String getHouseText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_house).trim();
    }

    public String getPetsText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_pets).trim();
    }

    public String getSportsText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_sports).trim();
    }

    public String getTaxiText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_taxi).trim();
    }

    public String getToiletryText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_toiletry).trim();
    }

    public String getTransportText(final Context context) {
        return context.getResources().getString(R.string.subtract_money_transport).trim();
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
                        field.setText(String.valueOf(field.getText())
                                .substring(0, positionOfComma + 3));
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
}