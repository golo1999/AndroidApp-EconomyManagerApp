package com.example.economy_manager.main_part.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;

public class EditSpecificTransactionViewModel extends ViewModel {
    private UserDetails userDetails;
    private String activityTitle;
    private Transaction selectedTransaction;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
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
}