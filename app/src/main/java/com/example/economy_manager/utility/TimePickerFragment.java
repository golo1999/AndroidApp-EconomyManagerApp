package com.example.economy_manager.utility;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.LocalTime;

public class TimePickerFragment extends DialogFragment {

    private LocalTime timePickerStartDate = LocalTime.now();

    public TimePickerFragment() {
        // Required empty public constructor
    }

    public TimePickerFragment(final LocalTime timePickerStartDate) {
        this.timePickerStartDate = timePickerStartDate;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final @Nullable Bundle savedInstanceState) {
        return new TimePickerDialog(requireActivity(),
                (TimePickerDialog.OnTimeSetListener) requireActivity(),
                timePickerStartDate.getHour(),
                timePickerStartDate.getMinute(),
                DateFormat.is24HourFormat(requireActivity()));
    }
}