package com.example.economy_manager.main_part.viewmodels;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.main_part.adapters.EditTransactionsRecyclerViewAdapter;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.models.UserDetails;

public class EditTransactionsViewModel extends ViewModel {
    private UserDetails userDetails;
    private Fragment editSpecificTransactionFragment;
    private String activityTitle;
    private Transaction selectedTransaction;
    private int selectedTransactionListPosition;
    private EditTransactionsRecyclerViewAdapter editTransactionsRecyclerViewAdapter;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public Fragment getEditSpecificTransactionFragment() {
        return editSpecificTransactionFragment;
    }

    public void setEditSpecificTransactionFragment(Fragment editSpecificTransactionFragment) {
        this.editSpecificTransactionFragment = editSpecificTransactionFragment;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
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

    public EditTransactionsRecyclerViewAdapter getEditTransactionsRecyclerViewAdapter() {
        return editTransactionsRecyclerViewAdapter;
    }

    public void setEditTransactionsRecyclerViewAdapter(EditTransactionsRecyclerViewAdapter editTransactionsRecyclerViewAdapter) {
        this.editTransactionsRecyclerViewAdapter = editTransactionsRecyclerViewAdapter;
    }
}
