package com.example.economy_manager.main_part.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.main_part.view.fragment.FragmentBudgetReview;
import com.example.economy_manager.main_part.view.fragment.FragmentLastTenTransactions;
import com.example.economy_manager.main_part.view.fragment.FragmentMoneySpent;
import com.example.economy_manager.main_part.view.fragment.FragmentMoneySpentPercentage;
import com.example.economy_manager.main_part.view.fragment.FragmentShowSavings;
import com.example.economy_manager.main_part.view.fragment.FragmentTopFiveExpenses;

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
            // german
            "der" : Locale.getDefault().getDisplayLanguage().equals("italiano") ?
            // italian
            "il" : Locale.getDefault().getDisplayLanguage().equals("português") ?
            // portuguese
            "de" :
            // every other language
            "";
    private final String daySuffix = !Locale.getDefault().getDisplayLanguage().equals("English") ?
            // every other language but english
            "" :
            // english
            getCurrentTime().get(Calendar.DAY_OF_MONTH) % 10 == 1 ?
                    // day number ending with one
                    "st" : getCurrentTime().get(Calendar.DAY_OF_MONTH) % 10 == 2 ?
                    // day number ending with two
                    "nd" : getCurrentTime().get(Calendar.DAY_OF_MONTH) % 10 == 3 ?
                    // day number ending with three
                    "rd" :
                    // every other day
                    "th";
    private final String separator = Locale.getDefault().getDisplayLanguage().equals("español") ?
            // spanish
            "de" :
            // every other language but spanish
            "";
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL",
            Locale.getDefault().getDisplayLanguage().equals("Deutsch") ?
                    //german
                    Locale.GERMAN : Locale.getDefault().getDisplayLanguage().equals("español") ?
                    // spanish
                    Locale.forLanguageTag("es-ES") : Locale.getDefault().getDisplayLanguage().equals("français") ?
                    // french
                    Locale.FRENCH : Locale.getDefault().getDisplayLanguage().equals("italiano") ?
                    // italian
                    Locale.ITALIAN : Locale.getDefault().getDisplayLanguage().equals("português") ?
                    // portuguese
                    Locale.forLanguageTag("pt-PT") : Locale.getDefault().getDisplayLanguage().equals("română") ?
                    // romanian
                    Locale.forLanguageTag("ro-RO") :
                    // every other language
                    Locale.ENGLISH);
    private final String currentDateTranslated = Locale.getDefault().getDisplayLanguage().equals("Deutsch") ||
            Locale.getDefault().getDisplayLanguage().equals("italiano") ||
            Locale.getDefault().getDisplayLanguage().equals("română") ?
            // german, italian or romanian
            getDatePrefix() + " " + getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " +
                    getMonthFormat().format(getCurrentTime().getTime()) + " " + getCurrentTime().get(Calendar.YEAR) :
            Locale.getDefault().getDisplayLanguage().equals("español") ?
                    // spanish
                    getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " + getSeparator() + " " +
                            getMonthFormat().format(getCurrentTime().getTime()) + " " + getSeparator() + " " +
                            getCurrentTime().get(Calendar.YEAR) :
                    Locale.getDefault().getDisplayLanguage().equals("français") ?
                            // french
                            getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " +
                                    getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                    getCurrentTime().get(Calendar.YEAR) :
                            Locale.getDefault().getDisplayLanguage().equals("português") ?
                                    // portuguese
                                    getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " + getDatePrefix() + " " +
                                            getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                            getDatePrefix() + " " + getCurrentTime().get(Calendar.YEAR) :
                                    // every other language
                                    getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                            getCurrentTime().get(Calendar.DAY_OF_MONTH) + getDaySuffix() + ", "
                                            + getCurrentTime().get(Calendar.YEAR);

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