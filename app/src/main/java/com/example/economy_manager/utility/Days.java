package com.example.economy_manager.utility;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.economy_manager.R;

public final class Days {

    private Days() {

    }

    public static String getTranslatedDayName(final @NonNull Context context,
                                              final @NonNull String dayName) {
        return dayName.equals("Monday") ?
                context.getString(R.string.monday) : dayName.equals("Tuesday") ?
                context.getString(R.string.tuesday) : dayName.equals("Wednesday") ?
                context.getString(R.string.wednesday) : dayName.equals("Thursday") ?
                context.getString(R.string.thursday) : dayName.equals("Friday") ?
                context.getString(R.string.friday) : dayName.equals("Saturday") ?
                context.getString(R.string.saturday) : dayName.equals("Sunday") ?
                context.getString(R.string.sunday) : "";
    }
}