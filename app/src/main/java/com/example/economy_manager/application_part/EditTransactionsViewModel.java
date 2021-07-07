package com.example.economy_manager.application_part;

import androidx.lifecycle.ViewModel;

import com.example.economy_manager.Transaction;

public class EditTransactionsViewModel extends ViewModel {
    private Transaction selectedTransaction;

    public Transaction getSelectedTransaction() {
        return selectedTransaction;
    }

    public void setSelectedTransaction(Transaction selectedTransaction) {
        this.selectedTransaction = selectedTransaction;
    }
}
