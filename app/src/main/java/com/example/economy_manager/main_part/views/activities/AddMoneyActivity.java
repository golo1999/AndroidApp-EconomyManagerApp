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
import com.example.economy_manager.main_part.viewmodels.AddMoneyViewModel;
import com.example.economy_manager.main_part.views.fragments.DatePickerFragment;
import com.example.economy_manager.models.MyCustomTime;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.example.economy_manager.utilities.Types;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class AddMoneyActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private AddMoneyViewModel viewModel;
    private TextView dateText;
    private EditText valueField;
    private EditText noteField;
    private RadioGroup radioGroup;
    private Button cancelButton;
    private Button saveChangesButton;
    private final RadioButton[] radioButton1 = new RadioButton[4];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_money_activity);
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
        viewModel = new ViewModelProvider(this).get(AddMoneyViewModel.class);
        dateText = findViewById(R.id.add_money_date);
        valueField = findViewById(R.id.add_money_value_field);
        noteField = findViewById(R.id.add_money_note_field);
        radioGroup = findViewById(R.id.add_money_radio_group);
        cancelButton = findViewById(R.id.add_money_cancel_button);
        saveChangesButton = findViewById(R.id.add_money_save_button);
    }

    private void setOnClickListeners() {
        cancelButton.setOnClickListener((final View v) -> onBackPressed());

        dateText.setOnClickListener((final View v) -> {
            final DialogFragment datePickerFragment = new DatePickerFragment(viewModel.getTransactionDate());

            datePickerFragment.show(getSupportFragmentManager(), "date_picker");
        });

        saveChangesButton.setOnClickListener((final View v) -> {
            final int selectedID = radioGroup.getCheckedRadioButtonId();

            MyCustomMethods.closeTheKeyboard(AddMoneyActivity.this);
            // if there was any radio button checked
            if (selectedID != -1 &&
                    MyCustomVariables.getFirebaseAuth().getUid() != null) {
                if (!String.valueOf(valueField.getText()).trim().isEmpty()) {
                    if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                        final RadioButton radioButton = findViewById(selectedID);

                        final int transactionCategoryIndex = Transaction.getIndexFromCategory(Types.
                                getTypeInEnglish(this, String.valueOf(radioButton.getText()).trim()));

                        final Transaction newTransaction = !String.valueOf(noteField.getText()).trim().equals("") ?
                                new Transaction(transactionCategoryIndex,
                                        1,
                                        String.valueOf(noteField.getText()).trim(),
                                        String.valueOf(valueField.getText()).trim()) :
                                new Transaction(transactionCategoryIndex,
                                        1,
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
                                            getResources().getString(R.string.income) + " " +
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
            }
            // if there wasn't a radio button checked
            else {
                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.money_error1));
            }
        });
    }

    private void createRadioButtons() {
        final ArrayList<String> buttonTextArray = new ArrayList<>();

        final String depositsText = getResources().getString(R.string.add_money_deposits).trim();

        final String independentSourcesText = getResources().getString(R.string.add_money_independent_sources).trim();

        final String salaryText = getResources().getString(R.string.salary).trim();

        final String savingText = getResources().getString(R.string.saving).trim();

        buttonTextArray.add(depositsText);
        buttonTextArray.add(independentSourcesText);
        buttonTextArray.add(salaryText);
        buttonTextArray.add(savingText);

        Collections.sort(buttonTextArray);

        for (int i = 0; i < buttonTextArray.size(); i++) {
            final int ID = buttonTextArray.get(i).equals(depositsText) ?
                    R.id.addMoneyRadioButtonDeposits : buttonTextArray.get(i).equals(independentSourcesText) ?
                    R.id.addMoneyRadioButtonIndependentSources : buttonTextArray.get(i).equals(salaryText) ?
                    R.id.addMoneyRadioButtonSalary : R.id.addMoneyRadioButtonSaving;

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
                        valueField.setText(String.valueOf(valueField.getText()).substring(0, positionOfComma + 3));
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