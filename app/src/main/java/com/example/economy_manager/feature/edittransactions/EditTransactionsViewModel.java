package com.example.economy_manager.feature.edittransactions;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.EditSpecificTransactionFragmentBinding;
import com.example.economy_manager.model.MyCustomTime;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.DatePickerFragment;
import com.example.economy_manager.utility.Months;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.utility.TimePickerFragment;
import com.example.economy_manager.utility.Types;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditTransactionsViewModel extends ViewModel {

    private EditSpecificTransactionFragmentBinding binding;
    private Fragment editSpecificTransactionFragment;
    private EditTransactionsRecyclerViewAdapter editTransactionsRecyclerViewAdapter;
    private Transaction selectedTransaction;
    private int selectedTransactionListPosition;
    private final ArrayList<String> transactionTypesList = new ArrayList<>();
    private LocalDate transactionDate = LocalDate.now();
    private LocalTime transactionTime = LocalTime.now();
    private UserDetails userDetails;

    public void setBinding(EditSpecificTransactionFragmentBinding binding) {
        this.binding = binding;
    }

    public EditSpecificTransactionFragmentBinding getBinding() {
        return binding;
    }

    public Fragment getEditSpecificTransactionFragment() {
        return editSpecificTransactionFragment;
    }

    public void setEditSpecificTransactionFragment(Fragment editSpecificTransactionFragment) {
        this.editSpecificTransactionFragment = editSpecificTransactionFragment;
    }

    public EditTransactionsRecyclerViewAdapter getEditTransactionsRecyclerViewAdapter() {
        return editTransactionsRecyclerViewAdapter;
    }

    public void setEditTransactionsRecyclerViewAdapter(EditTransactionsRecyclerViewAdapter recyclerViewAdapter) {
        this.editTransactionsRecyclerViewAdapter = recyclerViewAdapter;
    }

    public Transaction getSelectedTransaction() {
        return selectedTransaction;
    }

    public void setSelectedTransaction(Transaction selectedTransaction) {
        this.selectedTransaction = selectedTransaction;
    }

    public int getSelectedTransactionListPosition() {
        return selectedTransactionListPosition;
    }

    public void setSelectedTransactionListPosition(int selectedTransactionListPosition) {
        this.selectedTransactionListPosition = selectedTransactionListPosition;
    }

    public ArrayList<String> getTransactionTypesList() {
        return transactionTypesList;
    }

    public void setTransactionTypesList(final @NonNull Context context) {
        transactionTypesList.add(context.getResources().getString(R.string.bills).trim());
        transactionTypesList.add(context.getResources().getString(R.string.car).trim());
        transactionTypesList.add(context.getResources().getString(R.string.clothes).trim());
        transactionTypesList.add(context.getResources().getString(R.string.communications).trim());
        transactionTypesList.add(context.getResources().getString(R.string.deposits).trim());
        transactionTypesList.add(context.getResources().getString(R.string.eating_out).trim());
        transactionTypesList.add(context.getResources().getString(R.string.entertainment).trim());
        transactionTypesList.add(context.getResources().getString(R.string.food).trim());
        transactionTypesList.add(context.getResources().getString(R.string.gifts).trim());
        transactionTypesList.add(context.getResources().getString(R.string.health).trim());
        transactionTypesList.add(context.getResources().getString(R.string.house).trim());
        transactionTypesList.add(context.getResources().getString(R.string.pets).trim());
        transactionTypesList.add(context.getResources().getString(R.string.independent_sources).trim());
        transactionTypesList.add(context.getResources().getString(R.string.salary).trim());
        transactionTypesList.add(context.getResources().getString(R.string.saving).trim());
        transactionTypesList.add(context.getResources().getString(R.string.sports).trim());
        transactionTypesList.add(context.getResources().getString(R.string.taxi).trim());
        transactionTypesList.add(context.getResources().getString(R.string.toiletry).trim());
        transactionTypesList.add(context.getResources().getString(R.string.transport).trim());

        Collections.sort(transactionTypesList);
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public Fragment getCurrentDisplayedFragment(final List<Fragment> fragmentsList) {
        if (fragmentsList != null) {
            for (final Fragment fragment : fragmentsList) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }

        return null;
    }

    public int getNearestMonthPositionFromSpinner(final Context context,
                                                  final @NonNull ArrayList<String> monthsList,
                                                  final int monthIndex) {
        for (int listIteratorCounter = 0; listIteratorCounter < monthsList.size(); ++listIteratorCounter) {
            final String listIteratorInEnglish = Months.getMonthInEnglish(context,
                    monthsList.get(listIteratorCounter));

            if (listIteratorInEnglish.equals(Months.getMonthFromIndex(context, monthIndex))) {
                return listIteratorCounter;
            }
        }

        return -1;
    }

    public int getTransactionPositionInList(final String searchedType) {
        int positionInTheTransactionTypesList = -1;

        for (final String type : getTransactionTypesList()) {
            ++positionInTheTransactionTypesList;

            if (type.equals(searchedType)) {
                break;
            }
        }

        return positionInTheTransactionTypesList;
    }

    public void modifyTransactionIntoDatabase(final @NonNull Activity activity) {
        final Transaction selectedTransaction = getSelectedTransaction();

        if (selectedTransaction == null) {
            return;
        }

        final String editedNote = !String.valueOf(getBinding().noteField.getText()).trim().isEmpty() ?
                String.valueOf(getBinding().noteField.getText()).trim() : selectedTransaction.getNote();
        final String editedValue = !String.valueOf(getBinding().valueField.getText()).trim().isEmpty() ?
                String.valueOf(getBinding().valueField.getText()).trim() : selectedTransaction.getValue();
        final int parsedCategoryIndex = Transaction.getIndexFromCategory(Types.
                getTypeInEnglish(activity, String.valueOf(getBinding().typeSpinner.getSelectedItem()).trim()));
        final int editedCategoryIndex = parsedCategoryIndex >= 0 && parsedCategoryIndex <= 18 &&
                parsedCategoryIndex != selectedTransaction.getCategory() ?
                parsedCategoryIndex : selectedTransaction.getCategory();

        final int selectedYear = getTransactionDate().getYear();
        final int selectedMonth = getTransactionDate().getMonthValue();
        final int selectedDay = getTransactionDate().getDayOfMonth();
        final int selectedHour = getTransactionTime().getHour();
        final int selectedMinute = getTransactionTime().getMinute();
        final int selectedSecond =
                (selectedYear != selectedTransaction.getTime().getYear() &&
                        selectedMonth != selectedTransaction.getTime().getMonth() &&
                        selectedDay != selectedTransaction.getTime().getDay() &&
                        selectedHour != selectedTransaction.getTime().getHour() &&
                        selectedMinute != selectedTransaction.getTime().getMinute()) ?
                        LocalDateTime.now().getSecond() : selectedTransaction.getTime().getSecond();

        final LocalDate selectedLocalDate =
                LocalDate.of(selectedYear, selectedMonth, selectedDay);

        final MyCustomTime editedTime = new MyCustomTime(selectedYear,
                selectedMonth,
                String.valueOf(selectedLocalDate.getMonth()),
                selectedDay,
                String.valueOf(selectedLocalDate.getDayOfWeek()),
                selectedHour,
                selectedMinute,
                selectedSecond);
        final int editedCategoryType = (editedCategoryIndex >= 0 && editedCategoryIndex <= 3) ? 1 : 0;
        final Transaction editedTransaction = new Transaction(selectedTransaction.getId(),
                editedCategoryIndex, editedTime, editedCategoryType, editedNote, editedValue);

        updateTransaction(activity, selectedTransaction);

        if (selectedTransaction.equals(editedTransaction) ||
                getEditTransactionsRecyclerViewAdapter() == null) {
            return;
        }

        getEditTransactionsRecyclerViewAdapter().notifyItemChanged(getSelectedTransactionListPosition());
    }

    public void onDateTextClickedInFragment(final FragmentManager fragmentManager) {
        final int selectedTransactionYear = getSelectedTransaction().getTime().getYear();
        final int selectedTransactionMonth = getSelectedTransaction().getTime().getMonth();
        final int selectedTransactionDay = getSelectedTransaction().getTime().getDay();

        final LocalDate selectedTransactionDate =
                LocalDate.of(selectedTransactionYear, selectedTransactionMonth, selectedTransactionDay);

        new DatePickerFragment(selectedTransactionDate).show(fragmentManager, "date_picker");
    }

    public void onSaveChangesButtonClicked(final @NonNull Activity currentActivity) {
        MyCustomMethods.closeTheKeyboard(currentActivity);

        if (MyCustomVariables.getUserDetails() != null) {
            modifyTransactionIntoDatabase(currentActivity);
        }

        currentActivity.onBackPressed();
    }

    public void onTimeTextClickedInFragment(final FragmentManager fragmentManager) {
        final int selectedTransactionHour = getSelectedTransaction().getTime().getHour();
        final int selectedTransactionMinute = getSelectedTransaction().getTime().getMinute();
        final int selectedTransactionSecond = getSelectedTransaction().getTime().getSecond();

        final LocalTime selectedTransactionTime =
                LocalTime.of(selectedTransactionHour, selectedTransactionMinute, selectedTransactionSecond);

        new TimePickerFragment(selectedTransactionTime).show(fragmentManager, "time_picker");
    }

    private void updateTransaction(final @NonNull Activity activity,
                                   final @NonNull Transaction initialTransaction) {
        final int selectedYear = getTransactionDate().getYear();
        final int selectedMonth = getTransactionDate().getMonthValue();
        final int selectedDay = getTransactionDate().getDayOfMonth();
        final int selectedHour = getTransactionTime().getHour();
        final int selectedMinute = getTransactionTime().getMinute();
        final int selectedSecond =
                (selectedYear != initialTransaction.getTime().getYear() &&
                        selectedMonth != initialTransaction.getTime().getMonth() &&
                        selectedDay != initialTransaction.getTime().getDay() &&
                        selectedHour != initialTransaction.getTime().getHour() &&
                        selectedMinute != initialTransaction.getTime().getMinute()) ?
                        LocalDateTime.now().getSecond() : initialTransaction.getTime().getSecond();

        final LocalDate selectedTimeLocalDate =
                LocalDate.of(selectedYear, selectedMonth, selectedDay);

        final MyCustomTime editedTime = new MyCustomTime(selectedYear,
                selectedMonth,
                String.valueOf(selectedTimeLocalDate.getMonth()),
                selectedDay,
                String.valueOf(selectedTimeLocalDate.getDayOfWeek()),
                selectedHour,
                selectedMinute,
                selectedSecond);

        boolean hasBeenModified = false;

        if ((initialTransaction.getNote() != null &&
                !String.valueOf(getBinding().noteField.getText()).trim().equals(initialTransaction.getNote().trim()) &&
                !String.valueOf(getBinding().noteField.getText()).trim().isEmpty()) ||
                (initialTransaction.getNote() == null && !String.valueOf(getBinding().noteField.getText()).trim().isEmpty())) {
            final String editedNote = !String.valueOf(getBinding().noteField.getText()).trim().isEmpty() ?
                    String.valueOf(getBinding().noteField.getText()).trim() : null;

            initialTransaction.setNote(editedNote);
            hasBeenModified = true;
        }

        if (!String.valueOf(getBinding().valueField.getText()).trim().
                equals(String.valueOf(getBinding().valueField.getHint()).trim()) &&
                !String.valueOf(getBinding().valueField.getText()).trim().isEmpty()) {
            final String editedValue = String.valueOf(getBinding().valueField.getText()).trim();

            initialTransaction.setValue(editedValue);

            if (!hasBeenModified) {
                hasBeenModified = true;
            }
        }

        if (!editedTime.equals(initialTransaction.getTime())) {
            initialTransaction.setTime(editedTime);

            if (!hasBeenModified) {
                hasBeenModified = true;
            }
        }

        final String initialCategoryName = Types.getTranslatedType(activity,
                Transaction.getTypeFromIndexInEnglish(initialTransaction.getCategory()));

        final String selectedCategoryName = String.valueOf(getBinding().typeSpinner.getSelectedItem()).trim();

        if (initialCategoryName != null && !initialCategoryName.equals(selectedCategoryName)) {
            final int editedCategory =
                    Transaction.getIndexFromCategory(Types.getTypeInEnglish(activity, selectedCategoryName));
            final int editedType = editedCategory >= 0 && editedCategory <= 3 ? 1 : 0;

            initialTransaction.setCategory(editedCategory);

            if (editedType != initialTransaction.getType()) {
                initialTransaction.setType(editedType);
            }

            if (!hasBeenModified) {
                hasBeenModified = true;
            }
        }

        if (!hasBeenModified || MyCustomVariables.getFirebaseAuth().getUid() == null) {
            return;
        }

        // updating the transaction into the Firebase database
        MyCustomVariables.getDatabaseReference()
                .child(MyCustomVariables.getFirebaseAuth().getUid())
                .child("personalTransactions")
                .child(initialTransaction.getId())
                .setValue(initialTransaction);
    }

    public void resetTransactionTypesList() {
        if (getTransactionTypesList().isEmpty()) {
            return;
        }

        transactionTypesList.clear();
    }
}