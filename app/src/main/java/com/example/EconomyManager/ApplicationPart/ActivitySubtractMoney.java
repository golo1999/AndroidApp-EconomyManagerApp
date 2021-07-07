package com.example.EconomyManager.ApplicationPart;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.EconomyManager.MyCustomMethods;
import com.example.EconomyManager.MyCustomVariables;
import com.example.EconomyManager.R;
import com.example.EconomyManager.Transaction;
import com.example.EconomyManager.Types;

import java.util.ArrayList;
import java.util.Collections;

public class ActivitySubtractMoney extends AppCompatActivity {
    private Button saveChanges;
    private Button cancel;
    private RadioGroup radioGroup;
    private EditText note;
    private EditText value;
    private TextView error;
    private final RadioButton[] radioButton1 = new RadioButton[15];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtract_money);
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
        saveChanges = findViewById(R.id.subtractMoneySaveButton);
        radioGroup = findViewById(R.id.subtractMoneyRadioGroup);
        note = findViewById(R.id.subtractMoneyNote);
        value = findViewById(R.id.subtractMoneyValue);
        error = findViewById(R.id.subtractMoneyError);
        cancel = findViewById(R.id.subtractMoneyCancelButton);
    }

    private void createRadioButtons() {
        final ArrayList<String> buttonTextArray = new ArrayList<>();

        buttonTextArray.add(getResources().getString(R.string.subtract_money_bills).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_car).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_clothes).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_communications).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_eating_out).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_entertainment).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_food).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_gifts).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_health).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_house).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_pets).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_sports).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_taxi).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_toiletry).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_transport).trim());
        Collections.sort(buttonTextArray);

        for (int i = 0; i < buttonTextArray.size(); i++) {
            final int ID = buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_bills).trim()) ?
                    R.id.subtractMoneyRadioButtonBills : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_car).trim()) ?
                    R.id.subtractMoneyRadioButtonCar : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_clothes).trim()) ?
                    R.id.subtractMoneyRadioButtonClothes : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_communications).trim()) ?
                    R.id.subtractMoneyRadioButtonCommunications : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_eating_out).trim()) ?
                    R.id.subtractMoneyRadioButtonEatingOut : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_entertainment).trim()) ?
                    R.id.subtractMoneyRadioButtonEntertainment : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_food).trim()) ?
                    R.id.subtractMoneyRadioButtonFood : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_gifts).trim()) ?
                    R.id.subtractMoneyRadioButtonGifts : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_health).trim()) ?
                    R.id.subtractMoneyRadioButtonHealth : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_house).trim()) ?
                    R.id.subtractMoneyRadioButtonHouse : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_pets).trim()) ?
                    R.id.subtractMoneyRadioButtonPets : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_sports).trim()) ?
                    R.id.subtractMoneyRadioButtonSports : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_taxi).trim()) ?
                    R.id.subtractMoneyRadioButtonTaxi : buttonTextArray.get(i)
                    .equals(getResources().getString(R.string.subtract_money_toiletry).trim()) ?
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

    private void setOnClickListeners() {
        saveChanges.setOnClickListener(v -> {
            final int selectedID = radioGroup.getCheckedRadioButtonId();

            MyCustomMethods.closeTheKeyboard(ActivitySubtractMoney.this);

            // daca a fost selectat vreun buton radio
            if (selectedID != -1 && MyCustomVariables.getFirebaseAuth().getUid() != null) {
                if (!String.valueOf(value.getText()).trim().equals("")) {
                    if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                        final RadioButton radioButton = findViewById(selectedID);

                        final int transactionCategoryIndex = Transaction.getIndexFromCategory(Types.
                                getTypeInEnglish(ActivitySubtractMoney.this,
                                        String.valueOf(radioButton.getText()).trim()));

                        final Transaction newTransaction = !String.valueOf(note.getText()).trim().equals("") ?
                                new Transaction(transactionCategoryIndex,
                                        0,
                                        String.valueOf(note.getText()).trim(),
                                        String.valueOf(value.getText()).trim()) :
                                new Transaction(transactionCategoryIndex,
                                        0,
                                        String.valueOf(value.getText()).trim());

                        MyCustomVariables.getDatabaseReference()
                                .child(MyCustomVariables.getFirebaseAuth().getUid())
                                .child("PersonalTransactions")
                                .child(newTransaction.getId())
                                .setValue(newTransaction)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ActivitySubtractMoney.this,
                                            getResources().getString(R.string.expense) + " " +
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
                                    Toast.makeText(ActivitySubtractMoney.this,
                                            "Try again",
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    error.setText(R.string.money_error4);
                }
            } else {
                error.setText(R.string.money_error1);
            }
        });

        cancel.setOnClickListener(v -> onBackPressed());
    }

    private void limitTwoDecimals() {
        value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                final int textLength = String.valueOf(s).length();
                if (String.valueOf(s).contains(".")) {
                    // salvam pozitia punctului ce desparte partea intreaga a numarului de partea zecimala a sa
                    final int positionOfComma = String.valueOf(s).indexOf(".");

                    // daca adaugam prima data punctul (ex: .5)
                    if (positionOfComma == 0 && textLength == 1) {
                        final String text = "0" + value.getText();
                        // adaugam un 0 in fata numarului
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
                        // punem cursorul pe ultima pozitie
                        // (cursorul sare pe prima pozitie dupa instructiunea de mai sus)
                        value.setSelection(String.valueOf(value.getText()).length());
                    }
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });
    }
}