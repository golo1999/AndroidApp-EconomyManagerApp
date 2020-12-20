package com.example.EconomyManager;

import android.content.Context;

public class Months
{
    public static String getTranslatedMonth(Context context, String monthName)
    {
        switch(monthName)
        {
            case "January":
                return context.getString(R.string.month_january);
            case "February":
                return context.getString(R.string.month_february);
            case "March":
                return context.getString(R.string.month_march);
            case "April":
                return context.getString(R.string.month_april);
            case "May":
                return context.getString(R.string.month_may);
            case "June":
                return context.getString(R.string.month_june);
            case "July":
                return context.getString(R.string.month_july);
            case "August":
                return context.getString(R.string.month_august);
            case "September":
                return context.getString(R.string.month_september);
            case "October":
                return context.getString(R.string.month_october);
            case "November":
                return context.getString(R.string.month_november);
            case "December":
                return context.getString(R.string.month_december);
            default:
                return "";
        }
    }

    public static String getMonthInEnglish(Context context, String monthName)
    {
        if(monthName.trim().equals(context.getString(R.string.month_january)))
            return "January";
        else if(monthName.trim().equals(context.getString(R.string.month_february)))
            return "February";
        else if(monthName.trim().equals(context.getString(R.string.month_march)))
            return "March";
        else if(monthName.trim().equals(context.getString(R.string.month_april)))
            return "April";
        else if(monthName.trim().equals(context.getString(R.string.month_may)))
            return "May";
        else if(monthName.trim().equals(context.getString(R.string.month_june)))
            return "June";
        else if(monthName.trim().equals(context.getString(R.string.month_july)))
            return "July";
        else if(monthName.trim().equals(context.getString(R.string.month_august)))
            return "August";
        else if(monthName.trim().equals(context.getString(R.string.month_september)))
            return "September";
        else if(monthName.trim().equals(context.getString(R.string.month_october)))
            return "October";
        else if(monthName.trim().equals(context.getString(R.string.month_november)))
            return "November";
        else if(monthName.trim().equals(context.getString(R.string.month_december)))
            return "December";
        else return "";
    }
}
