package com.example.economy_manager.main_part.views.activities;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class SubtractMoneyActivity
        extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {
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
        viewModel.limitTwoDecimals(valueField);
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

    private void addTransactionToDatabase(final int selectedID,
                                          final @NonNull String userID) {
        final RadioButton radioButton = findViewById(selectedID);

        final int transactionCategoryIndex = Transaction.getIndexFromCategory(Types.
                getTypeInEnglish(SubtractMoneyActivity.this,
                        String.valueOf(radioButton.getText()).trim()));

        final Transaction newTransaction = !String.valueOf(noteField.getText()).trim().isEmpty() ?
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
                .child(userID)
                .child("PersonalTransactions")
                .child(newTransaction.getId())
                .setValue(newTransaction)
                .addOnSuccessListener((final Void aVoid) -> {
                    MyCustomMethods.showShortMessage(this,
                            getResources().getString(R.string.expense) + " " +
                                    getResources().getString(R.string.add_money_added_successfully));
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                })
                .addOnFailureListener((final Exception e) -> {
                    MyCustomVariables.getDatabaseReference()
                            .child(userID)
                            .child("PersonalTransactions")
                            .child(newTransaction.getId())
                            .removeValue();

                    MyCustomMethods.showShortMessage(this,
                            getResources().getString(R.string.please_try_again));
                });
    }

    private void createRadioButtons() {
        final ArrayList<String> buttonTextArray = new ArrayList<>();

        final String billsText = viewModel.getBillsText(this);

        final int billsValue = R.id.add_money_radio_button_bills;

        final String carText = viewModel.getCarText(this);

        final int carValue = R.id.add_money_radio_button_car;

        final String clothesText = viewModel.getClothesText(this);

        final int clothesValue = R.id.add_money_radio_button_clothes;

        final String communicationsText = viewModel.getCommunicationsText(this);

        final int communicationsValue = R.id.add_money_radio_button_communications;

        final String eatingOutText = viewModel.getEatingOutText(this);

        final int eatingOutValue = R.id.add_money_radio_button_eating_out;

        final String entertainmentText = viewModel.getEntertainmentText(this);

        final int entertainmentValue = R.id.add_money_radio_button_entertainment;

        final String foodText = viewModel.getFoodText(this);

        final int foodValue = R.id.add_money_radio_button_food;

        final String giftsText = viewModel.getGiftsText(this);

        final int giftsValue = R.id.add_money_radio_button_gifts;

        final String healthText = viewModel.getHealthText(this);

        final int healthValue = R.id.add_money_radio_button_health;

        final String houseText = viewModel.getHouseText(this);

        final int houseValue = R.id.add_money_radio_button_house;

        final String petsText = viewModel.getPetsText(this);

        final int petsValue = R.id.add_money_radio_button_pets;

        final String sportsText = viewModel.getSportsText(this);

        final int sportsValue = R.id.add_money_radio_button_sports;

        final String taxiText = viewModel.getTaxiText(this);

        final int taxiValue = R.id.add_money_radio_button_taxi;

        final String toiletryText = viewModel.getToiletryText(this);

        final int toiletryValue = R.id.add_money_radio_button_toiletry;

        final String transportText = viewModel.getTransportText(this);

        final int transportValue = R.id.add_money_radio_button_transport;

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
                    billsValue : buttonTextArray.get(i).equals(carText) ?
                    carValue : buttonTextArray.get(i).equals(clothesText) ?
                    clothesValue : buttonTextArray.get(i).equals(communicationsText) ?
                    communicationsValue : buttonTextArray.get(i).equals(eatingOutText) ?
                    eatingOutValue : buttonTextArray.get(i).equals(entertainmentText) ?
                    entertainmentValue : buttonTextArray.get(i).equals(foodText) ?
                    foodValue : buttonTextArray.get(i).equals(giftsText) ?
                    giftsValue : buttonTextArray.get(i).equals(healthText) ?
                    healthValue : buttonTextArray.get(i).equals(houseText) ?
                    houseValue : buttonTextArray.get(i).equals(petsText) ?
                    petsValue : buttonTextArray.get(i).equals(sportsText) ?
                    sportsValue : buttonTextArray.get(i).equals(taxiText) ?
                    taxiValue : buttonTextArray.get(i).equals(toiletryText) ?
                    toiletryValue : transportValue;

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

    private void setDateText(final LocalDate date) {
        final String formattedDate = MyCustomMethods.getFormattedDate(date);

        if (!viewModel.getTransactionDate().equals(date)) {
            viewModel.setTransactionDate(date);
        }

        if (!String.valueOf(dateText.getText()).trim().equals(formattedDate)) {
            dateText.setText(formattedDate);
        }
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
                if (!String.valueOf(valueField.getText()).trim().isEmpty()) {
                    final String userID = MyCustomVariables.getFirebaseAuth().getUid();

                    addTransactionToDatabase(selectedID, userID);
                } else {
                    MyCustomMethods.showShortMessage(this, getResources().getString(R.string.money_error4));
                }
            } else {
                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.money_error1));
            }
        });
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
}