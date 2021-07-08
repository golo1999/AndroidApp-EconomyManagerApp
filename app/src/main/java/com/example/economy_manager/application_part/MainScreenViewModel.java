package com.example.economy_manager.application_part;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.UserDetails;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainScreenViewModel extends ViewModel {
    private UserDetails userDetails;
    private final FragmentShowSavings fragmentShowSavings = FragmentShowSavings.newInstance();
    private final FragmentBudgetReview fragmentBudgetReview = FragmentBudgetReview.newInstance();
    private final FragmentMoneySpent fragmentMoneySpent = FragmentMoneySpent.newInstance();
    private final FragmentLastTenTransactions fragmentLastTenTransactions = FragmentLastTenTransactions.newInstance();
    private final FragmentTopFiveExpenses fragmentTopFiveExpenses = FragmentTopFiveExpenses.newInstance();
    private final FragmentMoneySpentPercentage fragmentMoneySpentPercentage = FragmentMoneySpentPercentage.newInstance();
    private final Calendar currentTime = Calendar.getInstance();
    private final int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
    private final String datePrefix = Locale.getDefault().getDisplayLanguage().equals("Deutsch") ?
            "der" : Locale.getDefault().getDisplayLanguage().equals("italiano") ?
            "il" : Locale.getDefault().getDisplayLanguage().equals("português") ?
            "de" : "";
    private final String daySuffix = !Locale.getDefault().getDisplayLanguage().equals("English") ?
            "" : getCurrentTime().get(Calendar.DAY_OF_MONTH) % 10 == 1 ?
            "st" : getCurrentTime().get(Calendar.DAY_OF_MONTH) % 10 == 2 ?
            "nd" : getCurrentTime().get(Calendar.DAY_OF_MONTH) % 10 == 3 ?
            "rd" : "th";
    private final String separator = Locale.getDefault().getDisplayLanguage().equals("español") ?
            "de" : "";
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL",
            Locale.getDefault().getDisplayLanguage().equals("Deutsch") ?
                    Locale.GERMAN : Locale.getDefault().getDisplayLanguage().equals("español") ?
                    Locale.forLanguageTag("es-ES") : Locale.getDefault().getDisplayLanguage().equals("français") ?
                    Locale.FRENCH : Locale.getDefault().getDisplayLanguage().equals("italiano") ?
                    Locale.ITALIAN : Locale.getDefault().getDisplayLanguage().equals("português") ?
                    Locale.forLanguageTag("pt-PT") : Locale.getDefault().getDisplayLanguage().equals("română") ?
                    Locale.forLanguageTag("ro-RO") : Locale.ENGLISH);
    private final String currentDateTranslated = Locale.getDefault().getDisplayLanguage().equals("Deutsch") ||
            Locale.getDefault().getDisplayLanguage().equals("italiano") ||
            Locale.getDefault().getDisplayLanguage().equals("română") ?
            (getDatePrefix() + " " + getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " +
                    getMonthFormat().format(getCurrentTime().getTime()) + " " + getCurrentTime().get(Calendar.YEAR)) :
            Locale.getDefault().getDisplayLanguage().equals("español") ?
                    (getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " + getSeparator() + " " +
                            getMonthFormat().format(getCurrentTime().getTime()) + " " + getSeparator() + " " +
                            getCurrentTime().get(Calendar.YEAR)) :
                    Locale.getDefault().getDisplayLanguage().equals("français") ?
                            (getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " +
                                    getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                    getCurrentTime().get(Calendar.YEAR)) :
                            Locale.getDefault().getDisplayLanguage().equals("português") ?
                                    (getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " + getDatePrefix() + " " +
                                            getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                            getDatePrefix() + " " + getCurrentTime().get(Calendar.YEAR)) :
                                    (getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                            getCurrentTime().get(Calendar.DAY_OF_MONTH) + getDaySuffix() + ", "
                                            + getCurrentTime().get(Calendar.YEAR));

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public FragmentShowSavings getFragmentShowSavings() {
        return fragmentShowSavings;
    }

    public FragmentBudgetReview getFragmentBudgetReview() {
        return fragmentBudgetReview;
    }

    public FragmentMoneySpent getFragmentMoneySpent() {
        return fragmentMoneySpent;
    }

    public FragmentLastTenTransactions getFragmentLastTenTransactions() {
        return fragmentLastTenTransactions;
    }

    public FragmentTopFiveExpenses getFragmentTopFiveExpenses() {
        return fragmentTopFiveExpenses;
    }

    public FragmentMoneySpentPercentage getFragmentMoneySpentPercentage() {
        return fragmentMoneySpentPercentage;
    }

    public Calendar getCurrentTime() {
        return currentTime;
    }

    public int getCurrentHour() {
        return currentHour;
    }

    public String getGreetingMessage(Context context) {
        return getCurrentHour() < 12 ?
                context.getResources().getString(R.string.greet_good_morning).trim() : getCurrentHour() < 18 ?
                context.getResources().getString(R.string.greet_good_afternoon).trim() :
                context.getResources().getString(R.string.greet_good_evening).trim();
    }

    public String getDatePrefix() {
        return datePrefix;
    }

    public String getDaySuffix() {
        return daySuffix;
    }

    public String getSeparator() {
        return separator;
    }

    public SimpleDateFormat getMonthFormat() {
        return monthFormat;
    }

    public String getCurrentDateTranslated() {
        return currentDateTranslated;
    }
}