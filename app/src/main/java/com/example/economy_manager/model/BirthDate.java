package com.example.economy_manager.model;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

@Data
public class BirthDate implements Serializable {
    private int year;
    private int month;
    private int day;

    public BirthDate() {
        // Required empty public constructor
        this(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth());
    }

    public BirthDate(final LocalDate date) {
        this.year = date.getYear();
        this.month = date.getMonthValue();
        this.day = date.getDayOfMonth();
    }

    public BirthDate(final int year,
                     final int month,
                     final int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
}