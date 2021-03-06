package com.example.economy_manager.main_part.view.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.R;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.utility.Types;

import java.util.ArrayList;
import java.util.Collections;

public class AddMoneyActivity extends AppCompatActivity {
    private Button saveChangesButton;
    private Button cancelButton;
    private RadioGroup radioGroup;
    private EditText noteField;
    private EditText valueField;
    private final RadioButton[] radioButton1 = new RadioButton[4];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_money_activity);
        setVariables();
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

    private void setVariables() {
        saveChangesButton = findViewById(R.id.add_money_save_button);
        radioGroup = findViewById(R.id.add_money_radio_group);
        noteField = findViewById(R.id.add_money_note_field);
        valueField = findViewById(R.id.add_money_value_field);
        cancelButton = findViewById(R.id.add_money_cancel_button);
    }

    private void createRadioButtons() {
        final ArrayList<String> buttonTextArray = new ArrayList<>();

        buttonTextArray.add(getResources().getString(R.string.add_money_deposits).trim());
        buttonTextArray.add(getResources().getString(R.string.add_money_independent_sources).trim());
        buttonTextArray.add(getResources().getString(R.string.salary).trim());
        buttonTextArray.add(getResources().getString(R.string.saving).trim());

        Collections.sort(buttonTextArray);

        for (int i = 0; i < buttonTextArray.size(); i++) {
            final int ID = buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.add_money_deposits).trim()) ?
                    R.id.addMoneyRadioButtonDeposits : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.add_money_independent_sources).trim()) ?
                    R.id.addMoneyRadioButtonIndependentSources : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.salary).trim()) ?
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

    private void setOnClickListeners() {
        saveChangesButton.setOnClickListener(v -> {
            final int selectedID = radioGroup.getCheckedRadioButtonId();

            MyCustomMethods.closeTheKeyboard(AddMoneyActivity.this);

            // if there was any radio button checked
            if (selectedID != -1 && MyCustomVariables.getFirebaseAuth().getUid() != null) {
                if (!String.valueOf(valueField.getText()).trim().equals("")) {
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

                        MyCustomVariables.getDatabaseReference()
                                .child(MyCustomVariables.getFirebaseAuth().getUid())
                                .child("PersonalTransactions")
                                .child(newTransaction.getId())
                                .setValue(newTransaction)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AddMoneyActivity.this,
                                            getResources().getString(R.string.income) + " " +
                                                    getResources().getString(R.
                                                            string.add_money_added_successfully),
                                            Toast.LENGTH_SHORT).show();

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

                                    Toast.makeText(AddMoneyActivity.this,
                                            "Try again",
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Toast.makeText(AddMoneyActivity.this, R.string.money_error4, Toast.LENGTH_SHORT).show();
                }
            }
            // if there wasn't a radio button checked
            else {
                Toast.makeText(AddMoneyActivity.this, R.string.money_error1, Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> onBackPressed());
    }

    // method for limiting the number to only two decimals
    private void limitTwoDecimals() {
        valueField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
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
}