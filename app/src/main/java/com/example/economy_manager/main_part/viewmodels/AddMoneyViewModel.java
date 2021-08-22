package com.example.economy_manager.main_part.viewmodels;

import androidx.lifecycle.ViewModel;

import java.time.LocalDate;

public class AddMoneyViewModel extends ViewModel {
    private LocalDate transactionDate = LocalDate.now();

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}