package com.example.economy_manager.main_part.viewmodels;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.main_part.adapters.EditTransactionsRecyclerViewAdapter;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.Months;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EditTransactionsViewModel extends ViewModel {
    private String activityTitle;
    private Fragment editSpecificTransactionFragment;
    private EditTransactionsRecyclerViewAdapter editTransactionsRecyclerViewAdapter;
    private Transaction selectedTransaction;
    private int selectedTransactionListPosition;
    private LocalDate transactionDate = LocalDate.now();
    private LocalTime transactionTime = LocalTime.now();
    private UserDetails userDetails;

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
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

    public void setEditTransactionsRecyclerViewAdapter(EditTransactionsRecyclerViewAdapter editTransactionsRecyclerViewAdapter) {
        this.editTransactionsRecyclerViewAdapter = editTransactionsRecyclerViewAdapter;
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
                                                  final ArrayList<String> monthsList,
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
}
