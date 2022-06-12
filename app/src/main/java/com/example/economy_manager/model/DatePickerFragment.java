package com.example.economy_manager.model;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;

public class DatePickerFragment extends DialogFragment {

    private LocalDate datePickerStartDate = LocalDate.now();

    public DatePickerFragment() {
        // Required empty public constructor
    }

    public DatePickerFragment(final LocalDate datePickerStartDate) {
        this.datePickerStartDate = datePickerStartDate;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final @Nullable Bundle savedInstanceState) {
        final DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(),
                (DatePickerDialog.OnDateSetListener) requireActivity(),
                datePickerStartDate.getYear(),
                datePickerStartDate.getMonthValue() - 1,
                datePickerStartDate.getDayOfMonth());

        // setting max date as today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        return datePickerDialog;
    }
}