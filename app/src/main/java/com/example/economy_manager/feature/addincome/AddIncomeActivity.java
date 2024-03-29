package com.example.economy_manager.feature.addincome;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.AddIncomeActivityBinding;
import com.example.economy_manager.model.MyCustomTime;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.utility.Types;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

public class AddIncomeActivity
        extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    private AddIncomeActivityBinding binding;
    private AddIncomeViewModel viewModel;
    private final RadioButton[] optionsArray = new RadioButton[4];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityVariables();
        setLayoutVariables();
        setUserDetails();
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
        setDateText(LocalDate.of(year, month + 1, dayOfMonth));
    }

    private void addTransactionToDatabase(final int selectedID,
                                          final @NonNull String userID) {
        final RadioButton radioButton = findViewById(selectedID);
        final LocalTime currentTime = LocalTime.now();

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
                currentTime.getHour(),
                currentTime.getMinute(),
                currentTime.getSecond()));


        MyCustomVariables.getDatabaseReference()
                .child(userID)
                .child("personalTransactions")
                .child(newTransaction.getId())
                .setValue(newTransaction)
                .addOnSuccessListener((final Void aVoid) -> {
                    MyCustomMethods.showShortMessage(this,
                            getResources().getString(R.string.income_added_successfully));

                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                })
                .addOnFailureListener((final Exception e) -> {
                    MyCustomVariables.getDatabaseReference()
                            .child(userID)
                            .child("personalTransactions")
                            .child(newTransaction.getId())
                            .removeValue();

                    MyCustomMethods.showShortMessage(this,
                            getResources().getString(R.string.please_try_again));
                });
    }

    private void createRadioButtons() {
        final ArrayList<String> buttonTextArray = new ArrayList<>();
        final int color = viewModel.getUserDetails().getApplicationSettings().isDarkThemeEnabled() ?
                getColor(R.color.secondaryDark) : getColor(R.color.quaternaryLight);

        final String depositsText = viewModel.getDepositsText(this);
        final String independentSourcesText = viewModel.getIndependentSourcesText(this);
        final String salaryText = viewModel.getSalaryText(this);
        final String savingText = viewModel.getSavingText(this);

        final int depositsValue = R.id.add_money_radio_button_deposits;
        final int independentSourcesValue = R.id.add_money_radio_button_independent_sources;
        final int salaryValue = R.id.add_money_radio_button_salary;
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
            optionsArray[i].setButtonTintList(ColorStateList.valueOf(color));
            optionsArray[i].setTextColor(color);

            binding.optionsLayout.addView(optionsArray[i]);
        }
    }

    public void onSaveButtonClicked() {
        final int selectedID = binding.optionsLayout.getCheckedRadioButtonId();

        MyCustomMethods.closeTheKeyboard(AddIncomeActivity.this);

        // if the entered value is empty
        if (String.valueOf(binding.valueField.getText()).trim().isEmpty()) {
            binding.valueField.setError(getResources().getString(R.string.should_not_be_empty,
                    getResources().getString(R.string.the_value)));
            return;
        }
        // if there was NOT any radio button checked
        if (selectedID == -1 || MyCustomVariables.getFirebaseAuth().getUid() == null) {
            MyCustomMethods.showShortMessage(this,
                    getResources().getString(R.string.please_select_an_option));
            return;
        }

        addTransactionToDatabase(selectedID, MyCustomVariables.getFirebaseAuth().getUid());
    }

    private void setDateText(final LocalDate date) {
        final String formattedDate = MyCustomMethods.getFormattedDate(this, date);

        if (!viewModel.getTransactionDate().equals(date)) {
            viewModel.setTransactionDate(date);
        }

        if (!String.valueOf(binding.dateText.getText()).trim().equals(formattedDate)) {
            binding.dateText.setText(formattedDate);
        }
    }

    private void setActivityVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.add_income_activity);
        viewModel = new ViewModelProvider(this).get(AddIncomeViewModel.class);
    }

    private void setLayoutVariables() {
        binding.setActivity(this);
        binding.setFragmentManager(getSupportFragmentManager());
        binding.setViewModel(viewModel);
    }

    private void setUserDetails() {
        final String currentUserID = MyCustomVariables.getFirebaseAuth().getUid();

        if (currentUserID == null) {
            return;
        }

        if (MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this)
                != null) {
            viewModel.setUserDetails(MyCustomSharedPreferences
                    .retrieveUserDetailsFromSharedPreferences(this));
        }

        if (viewModel.getUserDetails() != null) {
            binding.setIsDarkThemeEnabled(viewModel.getUserDetails().getApplicationSettings()
                    .isDarkThemeEnabled());
        }
    }
}