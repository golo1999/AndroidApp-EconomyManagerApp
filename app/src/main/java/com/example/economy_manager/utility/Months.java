package com.example.economy_manager.utility;

import android.content.Context;

import com.example.economy_manager.R;

public final class Months {
    private Months() {

    }

    public static int getIndexFromMonth(final Context context,
                                        final String monthName) {
        return monthName.trim().equals(context.getString(R.string.january)) ?
                0 : monthName.trim().equals(context.getString(R.string.february)) ?
                1 : monthName.trim().equals(context.getString(R.string.march)) ?
                2 : monthName.trim().equals(context.getString(R.string.april)) ?
                3 : monthName.trim().equals(context.getString(R.string.may)) ?
                4 : monthName.trim().equals(context.getString(R.string.june)) ?
                5 : monthName.trim().equals(context.getString(R.string.july)) ?
                6 : monthName.trim().equals(context.getString(R.string.august)) ?
                7 : monthName.trim().equals(context.getString(R.string.september)) ?
                8 : monthName.trim().equals(context.getString(R.string.october)) ?
                9 : monthName.trim().equals(context.getString(R.string.november)) ?
                10 : monthName.trim().equals(context.getString(R.string.december)) ?
                11 : -1;
    }

    public static String getMonthFromIndex(final Context context,
                                           final int index) {
        return index == 0 ?
                context.getString(R.string.january) : index == 1 ?
                context.getString(R.string.february) : index == 2 ?
                context.getString(R.string.march) : index == 3 ?
                context.getString(R.string.april) : index == 4 ?
                context.getString(R.string.may) : index == 5 ?
                context.getString(R.string.june) : index == 6 ?
                context.getString(R.string.july) : index == 7 ?
                context.getString(R.string.august) : index == 8 ?
                context.getString(R.string.september) : index == 9 ?
                context.getString(R.string.october) : index == 10 ?
                context.getString(R.string.november) : index == 11 ?
                context.getString(R.string.december) : null;
    }

    public static String getMonthInEnglish(final Context context,
                                           final String monthName) {
        return monthName.trim().equals(context.getString(R.string.january)) ?
                "January" : monthName.trim().equals(context.getString(R.string.february)) ?
                "February" : monthName.trim().equals(context.getString(R.string.march)) ?
                "March" : monthName.trim().equals(context.getString(R.string.april)) ?
                "April" : monthName.trim().equals(context.getString(R.string.may)) ?
                "May" : monthName.trim().equals(context.getString(R.string.june)) ?
                "June" : monthName.trim().equals(context.getString(R.string.july)) ?
                "July" : monthName.trim().equals(context.getString(R.string.august)) ?
                "August" : monthName.trim().equals(context.getString(R.string.september)) ?
                "September" : monthName.trim().equals(context.getString(R.string.october)) ?
                "October" : monthName.trim().equals(context.getString(R.string.november)) ?
                "November" : monthName.trim().equals(context.getString(R.string.december)) ?
                "December" : "";
    }

    public static String getTranslatedMonth(final Context context,
                                            final String monthName) {
        return monthName.equals("January") ?
                context.getString(R.string.january) : monthName.equals("February") ?
                context.getString(R.string.february) : monthName.equals("March") ?
                context.getString(R.string.march) : monthName.equals("April") ?
                context.getString(R.string.april) : monthName.equals("May") ?
                context.getString(R.string.may) : monthName.equals("June") ?
                context.getString(R.string.june) : monthName.equals("July") ?
                context.getString(R.string.july) : monthName.equals("August") ?
                context.getString(R.string.august) : monthName.equals("September") ?
                context.getString(R.string.september) : monthName.equals("October") ?
                context.getString(R.string.october) : monthName.equals("November") ?
                context.getString(R.string.november) : monthName.equals("December") ?
                context.getString(R.string.december) : "";
    }
}
