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
        viewModel.setDateText(LocalDate.now(), dateText);
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

        viewModel.setDateText(newTransactionDate, dateText);
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
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

        final String billsText = viewModel.getBillsText(this);

        final String carText = viewModel.getCarText(this);

        final String clothesText = viewModel.getClothesText(this);

        final String communicationsText = viewModel.getCommunicationsText(this);

        final String eatingOutText = viewModel.getEatingOutText(this);

        final String entertainmentText = viewModel.getEntertainmentText(this);

        final String foodText = viewModel.getFoodText(this);

        final String giftsText = viewModel.getGiftsText(this);

        final String healthText = viewModel.getHealthText(this);

        final String houseText = viewModel.getHouseText(this);

        final String petsText = viewModel.getPetsText(this);

        final String sportsText = viewModel.getSportsText(this);

        final String taxiText = viewModel.getTaxiText(this);

        final String toiletryText = viewModel.getToiletryText(this);

        final String transportText = viewModel.getTransportText(this);

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
}