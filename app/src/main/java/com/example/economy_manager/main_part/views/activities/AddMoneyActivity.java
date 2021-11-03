package com.example.economy_manager.main_part.views.activities;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.AddMoneyActivityBinding;
import com.example.economy_manager.main_part.viewmodels.AddMoneyViewModel;
import com.example.economy_manager.models.MyCustomTime;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.example.economy_manager.utilities.Types;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class AddMoneyActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private AddMoneyActivityBinding binding;
    private AddMoneyViewModel viewModel;
    private final RadioButton[] optionsArray = new RadioButton[4];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVariables();
        setDateText(viewModel.getTransactionDate());
        viewModel.limitTwoDecimals(binding.valueField);
        createRadioButtons();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyCustomMethods.finishActivityWithFadeTransition(this);
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
                getTypeInEnglish(this, String.valueOf(radioButton.getText()).trim()));

        final Transaction newTransaction = !String.valueOf(binding.noteField.getText()).trim().isEmpty() ?
                new Transaction(transactionCategoryIndex,
                        1,
                        String.valueOf(binding.noteField.getText()).trim(),
                        String.valueOf(binding.valueField.getText()).trim()) :
                new Transaction(transactionCategoryIndex,
                        1,
                        String.valueOf(binding.valueField.getText()).trim());

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
                            getResources().getString(R.string.income) + " " +
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

        final String depositsText = viewModel.getDepositsText(this);

        final int depositsValue = R.id.add_money_radio_button_deposits;

        final String independentSourcesText = viewModel.getIndependentSourcesText(this);

        final int independentSourcesValue = R.id.add_money_radio_button_independent_sources;

        final String salaryText = viewModel.getSalaryText(this);

        final int salaryValue = R.id.add_money_radio_button_salary;

        final String savingText = viewModel.getSavingText(this);

        final int savingValue = R.id.add_money_radio_button_saving;

        buttonTextArray.add(depositsText);
        buttonTextArray.add(independentSourcesText);
        buttonTextArray.add(salaryText);
        buttonTextArray.add(savingText);

        Collections.sort(buttonTextArray);

        for (int i = 0; i < buttonTextArray.size(); i++) {
            final int ID = buttonTextArray.get(i).equals(depositsText) ?
                    depositsValue : buttonTextArray.get(i).equals(independentSourcesText) ?
                    independentSourcesValue : buttonTextArray.get(i).equals(salaryText) ?
                    salaryValue : savingValue;

            optionsArray[i] = new RadioButton(this);

            optionsArray[i].setText(buttonTextArray.get(i));
            optionsArray[i].setId(ID);
            optionsArray[i].setTextSize(18);
            optionsArray[i].setTypeface(Typeface.DEFAULT_BOLD);
            optionsArray[i].setButtonTintList(ColorStateList.valueOf(Color.WHITE));
            optionsArray[i].setTextColor(Color.WHITE);

            binding.optionsLayout.addView(optionsArray[i]);
        }
    }

    public void onSaveButtonClicked() {
        final int selectedID = binding.optionsLayout.getCheckedRadioButtonId();

        MyCustomMethods.closeTheKeyboard(AddMoneyActivity.this);
        // if there was any radio button checked
        if (selectedID != -1 && MyCustomVariables.getFirebaseAuth().getUid() != null) {
            if (!String.valueOf(binding.valueField.getText()).trim().isEmpty()) {
                final String userID = MyCustomVariables.getFirebaseAuth().getUid();

                addTransactionToDatabase(selectedID, userID);
            } else {
                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.money_error4));
            }
        }
        // if there wasn't a radio button checked
        else {
            MyCustomMethods.showShortMessage(this, getResources().getString(R.string.money_error1));
        }
    }

    private void setDateText(final LocalDate date) {
        final String formattedDate = MyCustomMethods.getFormattedDate(date);

        if (!viewModel.getTransactionDate().equals(date)) {
            viewModel.setTransactionDate(date);
        }

        if (!String.valueOf(binding.dateText.getText()).trim().equals(formattedDate)) {
            binding.dateText.setText(formattedDate);
        }
    }

    private void setVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.add_money_activity);
        viewModel = new ViewModelProvider(this).get(AddMoneyViewModel.class);

        binding.setActivity(this);
        binding.setFragmentManager(getSupportFragmentManager());
        binding.setViewModel(viewModel);
    }
}