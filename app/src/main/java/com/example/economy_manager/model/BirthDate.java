package com.example.economy_manager.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class BirthDate implements Serializable {
    private int year;
    private int month;
    private int day;

    public BirthDate() {
        // Required empty public constructor
        this(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BirthDate birthDate = (BirthDate) o;
        return year == birthDate.year &&
                month == birthDate.month &&
                day == birthDate.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day);
    }

    @NonNull
    @Override
    public String toString() {
        return year + "-" + month + "-" + day;
    }
}
