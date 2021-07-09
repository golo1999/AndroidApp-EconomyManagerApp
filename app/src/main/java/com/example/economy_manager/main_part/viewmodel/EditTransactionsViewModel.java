package com.example.economy_manager.main_part.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.economy_manager.model.Transaction;

public class EditTransactionsViewModel extends ViewModel {
    private Transaction selectedTransaction;

    public Transaction getSelectedTransaction() {
        return selectedTransaction;
    }

    public void setSelectedTransaction(Transaction selectedTransaction) {
        this.selectedTransaction = selectedTransaction;
    }
}
