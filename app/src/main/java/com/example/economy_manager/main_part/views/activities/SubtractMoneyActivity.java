package com.example.economy_manager.main_part.views.activities;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.viewmodels.SubtractMoneyViewModel;
import com.example.economy_manager.main_part.views.fragments.DatePickerFragment;
import com.example.economy_manager.models.MyCustomTime;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.example.economy_manager.utilities.Types;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class SubtractMoneyActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private SubtractMoneyViewModel viewModel;
    private TextView dateText;
    private EditText valueField;
    private EditText noteField;
    private RadioGroup radioGroup;
    private Button cancelButton;
    private Button saveChangesButton;
    private final RadioButton[] radioButton1 = new RadioButton[15];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subtract_money_activity);
        setVariables();
        setDateText(LocalDate.now());
        limitTwoDecimals();
        createRadioButtons();
        setOnClickListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onDateSet(final DatePicker datePicker,
                          final int year,
                          final int month,
                          final int dayOfMonth) {
        final LocalDate newTransactionDate = LocalDate.of(year, month + 1, dayOfMonth);

        setDateText(newTransactionDate);
    }

    private void setVariables() {
        viewModel = new ViewModelProvider(this).get(SubtractMoneyViewModel.class);
        dateText = findViewById(R.id.subtract_money_date);
        valueField = findViewById(R.id.subtract_money_value);
        noteField = findViewById(R.id.subtract_money_note);
        radioGroup = findViewById(R.id.subtract_money_radio_group);
        cancelButton = findViewById(R.id.subtract_money_cancel_button);
        saveChangesButton = findViewById(R.id.subtract_money_save_button);
    }

    private void setOnClickListeners() {
        cancelButton.setOnClickListener((final View v) -> onBackPressed());

        dateText.setOnClickListener((final View v) -> {
            final DialogFragment datePickerFragment = new DatePickerFragment(viewModel.getTransactionDate());

            datePickerFragment.show(getSupportFragmentManager(), "date_picker");
        });

        saveChangesButton.setOnClickListener((final View v) -> {
            final int selectedID = radioGroup.getCheckedRadioButtonId();

            MyCustomMethods.closeTheKeyboard(SubtractMoneyActivity.this);

            // if there was any radio button checked
            if (selectedID != -1 && MyCustomVariables.getFirebaseAuth().getUid() != null) {
                if (!String.valueOf(valueField.getText()).trim().equals("")) {
                    if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                        final RadioButton radioButton = findViewById(selectedID);

                        final int transactionCategoryIndex = Transaction.getIndexFromCategory(Types.
                                getTypeInEnglish(SubtractMoneyActivity.this,
                                        String.valueOf(radioButton.getText()).trim()));

                        final Transaction newTransaction = !String.valueOf(noteField.getText()).trim().equals("") ?
                                new Transaction(transactionCategoryIndex,
                                        0,
                                        String.valueOf(noteField.getText()).trim(),
                                        String.valueOf(valueField.getText()).trim()) :
                                new Transaction(transactionCategoryIndex,
                                        0,
                                        String.valueOf(valueField.getText()).trim());

                        // setting transaction's time
                        final LocalDate newTransactionDate = viewModel.getTransactionDate();

                        newTransaction.setTime(new MyCustomTime(newTransactionDate.getYear(),
                                newTransactionDate.getMonthValue(),
                                String.valueOf(newTransactionDate.getMonth()),
                                newTransactionDate.getDayOfMonth(),
                                String.valueOf(newTransactionDate.getDayOfWeek()),
                                0,
                                0,
                                0));

                        MyCustomVariables.getDatabaseReference()
                                .child(MyCustomVariables.getFirebaseAuth().getUid())
                                .child("PersonalTransactions")
                                .child(newTransaction.getId())
                                .setValue(newTransaction)
                                .addOnSuccessListener(aVoid -> {
                                    MyCustomMethods.showShortMessage(this,
                                            getResources().getString(R.string.expense) + " " +
                                                    getResources().getString(R.string.add_money_added_successfully));
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left,
                                            R.anim.slide_out_right);
                                })
                                .addOnFailureListener(e -> {
                                    MyCustomVariables.getDatabaseReference()
                                            .child(MyCustomVariables.getFirebaseAuth().getUid())
                                            .child("PersonalTransactions")
                                            .child(newTransaction.getId())
                                            .removeValue();

                                    MyCustomMethods.showShortMessage(this,
                                            "Try again");
                                });
                    }
                } else {
                    MyCustomMethods.showShortMessage(this, getResources().getString(R.string.money_error4));
                }
            } else {
                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.money_error1));
            }
        });
    }

    private void createRadioButtons() {
        final ArrayList<String> buttonTextArray = new ArrayList<>();

        final String billsText = getResources().getString(R.string.subtract_money_bills).trim();

        final String carText = getResources().getString(R.string.subtract_money_car).trim();

        final String clothesText = getResources().getString(R.string.subtract_money_clothes).trim();

        final String communicationsText = getResources().getString(R.string.subtract_money_communications).trim();

        final String eatingOutText = getResources().getString(R.string.subtract_money_eating_out).trim();

        final String entertainmentText = getResources().getString(R.string.subtract_money_entertainment).trim();

        final String foodText = getResources().getString(R.string.subtract_money_food).trim();

        final String giftsText = getResources().getString(R.string.subtract_money_gifts).trim();

        final String healthText = getResources().getString(R.string.subtract_money_health).trim();

        final String houseText = getResources().getString(R.string.subtract_money_house).trim();

        final String petsText = getResources().getString(R.string.subtract_money_pets).trim();

        final String sportsText = getResources().getString(R.string.subtract_money_sports).trim();

        final String taxiText = getResources().getString(R.string.subtract_money_taxi).trim();

        final String toiletryText = getResources().getString(R.string.subtract_money_toiletry).trim();

        final String transportText = getResources().getString(R.string.subtract_money_transport).trim();

        buttonTextArray.add(billsText);
        buttonTextArray.add(carText);
        buttonTextArray.add(clothesText);
        buttonTextArray.add(communicationsText);
        buttonTextArray.add(eatingOutText);
        buttonTextArray.add(entertainmentText);
        buttonTextArray.add(foodText);
        buttonTextArray.add(giftsText);
        buttonTextArray.add(healthText);
        buttonTextArray.add(houseText);
        buttonTextArray.add(petsText);
        buttonTextArray.add(sportsText);
        buttonTextArray.add(taxiText);
        buttonTextArray.add(toiletryText);
        buttonTextArray.add(transportText);

        Collections.sort(buttonTextArray);

        for (int i = 0; i < buttonTextArray.size(); i++) {
            final int ID = buttonTextArray.get(i).equals(billsText) ?
                    R.id.subtractMoneyRadioButtonBills : buttonTextArray.get(i).equals(carText) ?
                    R.id.subtractMoneyRadioButtonCar : buttonTextArray.get(i).equals(clothesText) ?
                    R.id.subtractMoneyRadioButtonClothes : buttonTextArray.get(i).equals(communicationsText) ?
                    R.id.subtractMoneyRadioButtonCommunications : buttonTextArray.get(i).equals(eatingOutText) ?
                    R.id.subtractMoneyRadioButtonEatingOut : buttonTextArray.get(i).equals(entertainmentText) ?
                    R.id.subtractMoneyRadioButtonEntertainment : buttonTextArray.get(i).equals(foodText) ?
                    R.id.subtractMoneyRadioButtonFood : buttonTextArray.get(i).equals(giftsText) ?
                    R.id.subtractMoneyRadioButtonGifts : buttonTextArray.get(i).equals(healthText) ?
                    R.id.subtractMoneyRadioButtonHealth : buttonTextArray.get(i).equals(houseText) ?
                    R.id.subtractMoneyRadioButtonHouse : buttonTextArray.get(i).equals(petsText) ?
                    R.id.subtractMoneyRadioButtonPets : buttonTextArray.get(i).equals(sportsText) ?
                    R.id.subtractMoneyRadioButtonSports : buttonTextArray.get(i).equals(taxiText) ?
                    R.id.subtractMoneyRadioButtonTaxi : buttonTextArray.get(i).equals(toiletryText) ?
                    R.id.subtractMoneyRadioButtonToiletry : R.id.subtractMoneyRadioButtonTransport;

            radioButton1[i] = new RadioButton(this);
            radioButton1[i].setText(buttonTextArray.get(i));
            radioButton1[i].setId(ID);
            radioButton1[i].setTextSize(18);
            radioButton1[i].setTypeface(Typeface.DEFAULT_BOLD);
            radioButton1[i].setButtonTintList(ColorStateList.valueOf(Color.WHITE));
            radioButton1[i].setTextColor(Color.WHITE);
            radioGroup.addView(radioButton1[i]);
        }
    }

    // method for limiting the number to only two decimals
    private void limitTwoDecimals() {
        valueField.addTextChangedListener(new TextWatcher() {
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
                        final String text = "0" + valueField.getText();

                        valueField.setText(text);
                        // putting the cursor at the end
                        valueField.setSelection(String.valueOf(valueField.getText()).length());
                    }
                    // if we add more than two decimals
                    if (textLength - positionOfComma > 3) {
                        // putting only the first two decimals
                        valueField.setText(String.valueOf(valueField.getText())
                                .substring(0, positionOfComma + 3));
                        // putting the cursor at the end
                        valueField.setSelection(String.valueOf(valueField.getText()).length());
                    }
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });
    }

    private void setDateText(final LocalDate date) {
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

        if (!viewModel.getTransactionDate().equals(date)) {
            viewModel.setTransactionDate(date);
        }

        dateText.setText(transactionDate);
    }
}