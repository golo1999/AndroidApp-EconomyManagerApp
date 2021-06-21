package com.example.EconomyManager.ApplicationPart;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.EconomyManager.R;
import com.example.EconomyManager.Transaction;
import com.example.EconomyManager.Types;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class ActivityAddMoney extends AppCompatActivity {
    private Button saveChanges;
    private Button cancel;
    private FirebaseAuth fbAuth;
    private DatabaseReference myRef;
    private RadioGroup radioGroup;
    private EditText note;
    private EditText value;
    private RadioButton radioButton;
    private TextView error;
    private RadioButton[] radioButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
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
        saveChanges = findViewById(R.id.addMoneySaveButton);
        fbAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        radioGroup = findViewById(R.id.addMoneyRadioGroup);
        note = findViewById(R.id.addMoneyNote);
        value = findViewById(R.id.addMoneyValue);
        error = findViewById(R.id.addMoneyError);
        cancel = findViewById(R.id.addMoneyCancelButton);
        radioButton1 = new RadioButton[4];
    }

    private void createRadioButtons() {
        ArrayList<String> buttonTextArray = new ArrayList<>();
        int ID;

        buttonTextArray.add(getResources().getString(R.string.add_money_deposits).trim());
        buttonTextArray.add(getResources().getString(R.string.add_money_independent_sources).trim());
        buttonTextArray.add(getResources().getString(R.string.salary).trim());
        buttonTextArray.add(getResources().getString(R.string.saving).trim());
        Collections.sort(buttonTextArray);

        for (int i = 0; i < buttonTextArray.size(); i++) {
            if (buttonTextArray.get(i).equals(getResources().getString(R.string.add_money_deposits).trim()))
                ID = R.id.addMoneyRadioButtonDeposits;
            else if (buttonTextArray.get(i).equals(getResources().getString(R.string.add_money_independent_sources).trim()))
                ID = R.id.addMoneyRadioButtonIndependentSources;
            else if (buttonTextArray.get(i).equals(getResources().getString(R.string.salary).trim()))
                ID = R.id.addMoneyRadioButtonSalary;
            else ID = R.id.addMoneyRadioButtonSaving;

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
        saveChanges.setOnClickListener(v -> {
            int selectedID = radioGroup.getCheckedRadioButtonId();

            closeTheKeyboard();

            // daca a fost selectat vreun buton radio
            if (selectedID != -1 && fbAuth.getUid() != null) {
                if (!String.valueOf(value.getText()).trim().equals("")) {
                    if (fbAuth.getUid() != null) {
                        radioButton = findViewById(selectedID);

                        Transaction newTransaction;
                        int transactionCategoryIndex = Transaction.getIndexFromCategory(Types.
                                getTypeInEnglish(this,
                                        String.valueOf(radioButton.getText()).trim()));

                        if (!String.valueOf(note.getText()).trim().equals("")) {
                            newTransaction = new Transaction(transactionCategoryIndex,
                                    1,
                                    String.valueOf(note.getText()).trim(),
                                    String.valueOf(value.getText()).trim());
                        } else {
                            newTransaction = new Transaction(transactionCategoryIndex,
                                    1,
                                    String.valueOf(value.getText()).trim());
                        }

                        myRef.child(fbAuth.getUid())
                                .child("PersonalTransactions")
                                .child(newTransaction.getId())
                                .setValue(newTransaction)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ActivityAddMoney.this,
                                            getResources().getString(R.string.income) + " " +
                                                    getResources().getString(R.
                                                            string.add_money_added_successfully),
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left,
                                            R.anim.slide_out_right);
                                })
                                .addOnFailureListener(e -> {
                                    myRef.child(fbAuth.getUid())
                                            .child("PersonalTransactions")
                                            .child(newTransaction.getId())
                                            .removeValue();
                                    Toast.makeText(ActivityAddMoney.this,
                                            "Try again",
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                } else error.setText(R.string.money_error4);
            } else error.setText(R.string.money_error1);
        });

        cancel.setOnClickListener(v -> onBackPressed());
    }

    private void closeTheKeyboard() {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager manager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void limitTwoDecimals() {
        value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int positionOfComma, textLength = String.valueOf(s).length();
                if (String.valueOf(s).contains(".")) {
                    // salvam pozitia punctului ce desparte partea intreaga a numarului
                    // de partea zecimala a sa
                    positionOfComma = String.valueOf(s).indexOf(".");

                    // daca adaugam prima data punctul, adaugam un zero in fata (ex: .5)
                    if (positionOfComma == 0 && textLength == 1) {
                        String text = "0" + value.getText();
                        value.setText(text);
                        // punem cursorul pe ultima pozitie
                        // (cursorul sare pe prima pozitie dupa instructiunea de mai sus)
                        value.setSelection(String.valueOf(value.getText()).length());
                    }

                    // daca adaugam mai mult de 2 zecimale
                    if (textLength - 1 - positionOfComma > 2) {
                        // punem doar primele 2 zecimale (ca si cum nu ne mai lasa sa adaugam
                        // caractere dupa ce am depasit cele 2 zecimale)
                        value.setText(String.valueOf(value.getText())
                                .substring(0, positionOfComma + 3));
                        // punem cursorul pe ultima pozitie (cursorul sare pe prima pozitie
                        // dupa instructiunea de mai sus)
                        value.setSelection(String.valueOf(value.getText()).length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}