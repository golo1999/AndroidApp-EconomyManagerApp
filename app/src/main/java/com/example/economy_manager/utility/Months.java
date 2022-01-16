package com.example.economy_manager.utility;

import android.content.Context;

import com.example.economy_manager.R;

public final class Months {
    private Months() {

    }

    public static int getIndexFromMonth(final Context context,
                                        final String monthName) {
        return monthName.trim().equals(context.getString(R.string.month_january)) ?
                0 : monthName.trim().equals(context.getString(R.string.month_february)) ?
                1 : monthName.trim().equals(context.getString(R.string.month_march)) ?
                2 : monthName.trim().equals(context.getString(R.string.month_april)) ?
                3 : monthName.trim().equals(context.getString(R.string.month_may)) ?
                4 : monthName.trim().equals(context.getString(R.string.month_june)) ?
                5 : monthName.trim().equals(context.getString(R.string.month_july)) ?
                6 : monthName.trim().equals(context.getString(R.string.month_august)) ?
                7 : monthName.trim().equals(context.getString(R.string.month_september)) ?
                8 : monthName.trim().equals(context.getString(R.string.month_october)) ?
                9 : monthName.trim().equals(context.getString(R.string.month_november)) ?
                10 : monthName.trim().equals(context.getString(R.string.month_december)) ?
                11 : -1;
    }

    public static String getMonthFromIndex(final Context context,
                                           final int index) {
        return index == 0 ?
                context.getString(R.string.month_january) : index == 1 ?
                context.getString(R.string.month_february) : index == 2 ?
                context.getString(R.string.month_march) : index == 3 ?
                context.getString(R.string.month_april) : index == 4 ?
                context.getString(R.string.month_may) : index == 5 ?
                context.getString(R.string.month_june) : index == 6 ?
                context.getString(R.string.month_july) : index == 7 ?
                context.getString(R.string.month_august) : index == 8 ?
                context.getString(R.string.month_september) : index == 9 ?
                context.getString(R.string.month_october) : index == 10 ?
                context.getString(R.string.month_november) : index == 11 ?
                context.getString(R.string.month_december) : null;
    }

    public static String getMonthInEnglish(final Context context,
                                           final String monthName) {
        return monthName.trim().equals(context.getString(R.string.month_january)) ?
                "January" : monthName.trim().equals(context.getString(R.string.month_february)) ?
                "February" : monthName.trim().equals(context.getString(R.string.month_march)) ?
                "March" : monthName.trim().equals(context.getString(R.string.month_april)) ?
                "April" : monthName.trim().equals(context.getString(R.string.month_may)) ?
                "May" : monthName.trim().equals(context.getString(R.string.month_june)) ?
                "June" : monthName.trim().equals(context.getString(R.string.month_july)) ?
                "July" : monthName.trim().equals(context.getString(R.string.month_august)) ?
                "August" : monthName.trim().equals(context.getString(R.string.month_september)) ?
                "September" : monthName.trim().equals(context.getString(R.string.month_october)) ?
                "October" : monthName.trim().equals(context.getString(R.string.month_november)) ?
                "November" : monthName.trim().equals(context.getString(R.string.month_december)) ?
                "December" : "";
    }

    public static String getTranslatedMonth(final Context context,
                                            final String monthName) {
        return monthName.equals("January") ?
                context.getString(R.string.month_january) : monthName.equals("February") ?
                context.getString(R.string.month_february) : monthName.equals("March") ?
                context.getString(R.string.month_march) : monthName.equals("April") ?
                context.getString(R.string.month_april) : monthName.equals("May") ?
                context.getString(R.string.month_may) : monthName.equals("June") ?
                context.getString(R.string.month_june) : monthName.equals("July") ?
                context.getString(R.string.month_july) : monthName.equals("August") ?
                context.getString(R.string.month_august) : monthName.equals("September") ?
                context.getString(R.string.month_september) : monthName.equals("October") ?
                context.getString(R.string.month_october) : monthName.equals("November") ?
                context.getString(R.string.month_november) : monthName.equals("December") ?
                context.getString(R.string.month_december) : "";
    }
}
