package com.example.economy_manager.feature.addexpense;

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
import com.example.economy_manager.databinding.AddExpenseActivityBinding;
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

public class AddExpenseActivity
        extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    private AddExpenseActivityBinding binding;
    private AddExpenseViewModel viewModel;
    private final RadioButton[] optionsArray = new RadioButton[15];

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
                getTypeInEnglish(AddExpenseActivity.this,
                        String.valueOf(radioButton.getText()).trim()));
        final Transaction newTransaction =
                !String.valueOf(binding.noteField.getText()).trim().isEmpty() ?
                        new Transaction(transactionCategoryIndex,
                                0,
                                String.valueOf(binding.noteField.getText()).trim(),
                                String.valueOf(binding.valueField.getText()).trim()) :
                        new Transaction(transactionCategoryIndex,
                                0,
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
                            getResources().getString(R.string.expense_added_successfully));
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

        final int billsValue = R.id.add_money_radio_button_bills;
        final int carValue = R.id.add_money_radio_button_car;
        final int clothesValue = R.id.add_money_radio_button_clothes;
        final int communicationsValue = R.id.add_money_radio_button_communications;
        final int eatingOutValue = R.id.add_money_radio_button_eating_out;
        final int entertainmentValue = R.id.add_money_radio_button_entertainment;
        final int foodValue = R.id.add_money_radio_button_food;
        final int giftsValue = R.id.add_money_radio_button_gifts;
        final int healthValue = R.id.add_money_radio_button_health;
        final int houseValue = R.id.add_money_radio_button_house;
        final int petsValue = R.id.add_money_radio_button_pets;
        final int sportsValue = R.id.add_money_radio_button_sports;
        final int taxiValue = R.id.add_money_radio_button_taxi;
        final int toiletryValue = R.id.add_money_radio_button_toiletry;
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

        MyCustomMethods.closeTheKeyboard(AddExpenseActivity.this);
        // if the entered value is empty
        if (String.valueOf(binding.valueField.getText()).trim().isEmpty()) {
            binding.valueField.setError(getResources().getString(R.string.should_not_be_empty,
                    getResources().getString(R.string.the_value)));
            return;
        }
        // if there was NOT any radio button checked or the user is not logged in
        if (selectedID == -1 || MyCustomVariables.getFirebaseAuth().getUid() == null) {
            MyCustomMethods.showShortMessage(this,
                    getResources().getString(R.string.please_select_an_option));
            return;
        }

        addTransactionToDatabase(selectedID, MyCustomVariables.getFirebaseAuth().getUid());
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

    private void setActivityVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.add_expense_activity);
        viewModel = new ViewModelProvider(this).get(AddExpenseViewModel.class);
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