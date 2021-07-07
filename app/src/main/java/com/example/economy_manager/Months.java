package com.example.economy_manager;

import android.content.Context;

public class Months {
    public static String getTranslatedMonth(final Context context, final String monthName) {
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

    public static String getMonthInEnglish(final Context context, final String monthName) {
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
}
