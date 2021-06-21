package com.example.EconomyManager.ApplicationPart;

import androidx.lifecycle.ViewModel;

import com.example.EconomyManager.Transaction;

public class EditTransactionsViewModel extends ViewModel {
    private Transaction selectedTransaction;

    public Transaction getSelectedTransaction() {
        return selectedTransaction;
    }

    public void setSelectedTransaction(Transaction selectedTransaction) {
        this.selectedTransaction = selectedTransaction;
    }
}
