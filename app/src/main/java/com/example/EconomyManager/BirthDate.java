package com.example.EconomyManager;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDate;

public class BirthDate implements Serializable {
    private int year;
    private int month;
    private int day;

    public BirthDate(LocalDate date) {
        this.year = date.getYear();
        this.month = date.getMonthValue();
        this.day = date.getDayOfMonth();
    }

    public BirthDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @NonNull
    @Override
    public String toString() {
        return day + "." + month + "." + year;
    }
}
