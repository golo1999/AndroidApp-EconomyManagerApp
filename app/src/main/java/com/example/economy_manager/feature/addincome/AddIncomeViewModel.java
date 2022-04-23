package com.example.economy_manager.feature.addincome;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.DatePickerFragment;
import com.example.economy_manager.utility.MyCustomMethods;

import java.time.LocalDate;

public class AddIncomeViewModel extends ViewModel {

    private LocalDate transactionDate = LocalDate.now();
    private UserDetails userDetails;

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public String getDepositsText(final Context context) {
        return context.getResources().getString(R.string.deposits).trim();
    }

    public String getIndependentSourcesText(final Context context) {
        return context.getResources().getString(R.string.independent_sources).trim();
    }

    public String getSalaryText(final Context context) {
        return context.getResources().getString(R.string.salary).trim();
    }

    public String getSavingText(final Context context) {
        return context.getResources().getString(R.string.saving).trim();
    }

//    public void addTransactionToDatabase(final @NonNull Activity currentActivity,
//                                         final int selectedID,
//                                         final @NonNull String userID) {
//        final RadioButton radioButton = currentActivity.findViewById(selectedID);
//
//        final int transactionCategoryIndex = Transaction.getIndexFromCategory(Types.
//                getTypeInEnglish(currentActivity, String.valueOf(radioButton.getText()).trim()));
//
//        final Transaction newTransaction = !String.valueOf(binding.noteField.getText()).trim().isEmpty() ?
//                new Transaction(transactionCategoryIndex,
//                        1,
//                        String.valueOf(binding.noteField.getText()).trim(),
//                        String.valueOf(binding.valueField.getText()).trim()) :
//                new Transaction(transactionCategoryIndex,
//                        1,
//                        String.valueOf(binding.valueField.getText()).trim());
//
//        // setting transaction's time
//        final LocalDate newTransactionDate = getTransactionDate();
//
//        newTransaction.setTime(new MyCustomTime(newTransactionDate.getYear(),
//                newTransactionDate.getMonthValue(),
//                String.valueOf(newTransactionDate.getMonth()),
//                newTransactionDate.getDayOfMonth(),
//                String.valueOf(newTransactionDate.getDayOfWeek()),
//                0,
//                0,
//                0));
//
//
//        MyCustomVariables.getDatabaseReference()
//                .child(userID)
//                .child("PersonalTransactions")
//                .child(newTransaction.getId())
//                .setValue(newTransaction)
//                .addOnSuccessListener((final Void aVoid) -> {
//                    MyCustomMethods.showShortMessage(currentActivity,
//                            currentActivity.getResources().getString(R.string.income) + " " +
//                                    currentActivity.getResources().getString(R.string.add_money_added_successfully));
//
//                    currentActivity.finish();
//                    currentActivity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//                })
//                .addOnFailureListener((final Exception e) -> {
//                    MyCustomVariables.getDatabaseReference()
//                            .child(userID)
//                            .child("PersonalTransactions")
//                            .child(newTransaction.getId())
//                            .removeValue();
//
//                    MyCustomMethods.showShortMessage(currentActivity,
//                            currentActivity.getResources().getString(R.string.please_try_again));
//                });
//    }

    // method for limiting the number to only two decimals
    public void limitTwoDecimals(final EditText field) {
        field.addTextChangedListener(new TextWatcher() {
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

                // if the number NOT is decimal (doesn't contain comma)
                if (!String.valueOf(s).contains(".")) {
                    return;
                }

                // saving comma's position
                final int positionOfComma = String.valueOf(s).indexOf(".");

                // adding a zero before if the first character is a dot (i.e: .5 => 0.5)
                if (positionOfComma == 0 && textLength == 1) {
                    final String text = "0" + field.getText();

                    field.setText(text);
                    // putting the cursor at the end
                    field.setSelection(String.valueOf(field.getText()).length());
                }

                // if we add more than two decimals
                if (textLength - positionOfComma > 3) {
                    // putting only the first two decimals
                    field.setText(String.valueOf(field.getText()).substring(0, positionOfComma + 3));
                    // putting the cursor at the end
                    field.setSelection(String.valueOf(field.getText()).length());
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });
    }

    public void onDateTextClicked(final FragmentManager fragmentManager) {
        new DatePickerFragment(getTransactionDate()).show(fragmentManager, "date_picker");
    }

//    public void onSaveButtonClicked(final @NonNull Activity currentActivity) {
//        final int selectedID = binding.optionsLayout.getCheckedRadioButtonId();
//
//        MyCustomMethods.closeTheKeyboard(currentActivity);
//        // if there was any radio button checked
//        if (selectedID != -1 && MyCustomVariables.getFirebaseAuth().getUid() != null) {
//            if (!String.valueOf(binding.valueField.getText()).trim().isEmpty()) {
//                final String userID = MyCustomVariables.getFirebaseAuth().getUid();
//
//                addTransactionToDatabase(currentActivity, selectedID, userID);
//            } else {
//                MyCustomMethods.showShortMessage(currentActivity, currentActivity.getResources().getString(R.string.money_error4));
//            }
//        }
//        // if there wasn't a radio button checked
//        else {
//            MyCustomMethods.showShortMessage(currentActivity, currentActivity.getResources().getString(R.string.money_error1));
//        }
//    }

    public void setDateText(final LocalDate date,
                            final TextView dateTextView) {
        final String formattedDate = MyCustomMethods.getFormattedDate(date);

        if (!getTransactionDate().equals(date)) {
            setTransactionDate(date);
        }

        if (!String.valueOf(dateTextView.getText()).trim().equals(formattedDate)) {
            dateTextView.setText(formattedDate);
        }
    }
}