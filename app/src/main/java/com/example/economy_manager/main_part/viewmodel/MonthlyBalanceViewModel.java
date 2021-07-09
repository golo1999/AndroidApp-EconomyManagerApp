package com.example.economy_manager.main_part.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.economy_manager.utility.Months;
import com.example.economy_manager.model.UserDetails;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;

public class MonthlyBalanceViewModel extends ViewModel {
    private UserDetails userDetails;
    private final SimpleDateFormat currentMonthFormat = new SimpleDateFormat("LLLL", Locale.ENGLISH);
    private final Calendar currentTime = Calendar.getInstance();

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public SimpleDateFormat getCurrentMonthFormat() {
        return currentMonthFormat;
    }

    public Calendar getCurrentTime() {
        return currentTime;
    }

    public int getCurrentYear() {
        return getCurrentTime().get(Calendar.YEAR);
    }

    public String getCurrentMonthName() {
        return getCurrentMonthFormat().format(getCurrentTime().getTime()).trim();
    }

    public String getDayPrefix(final int dayFromDaysList) {
        return Locale.getDefault().getDisplayLanguage().equals("Deutsch") ?
                // german
                "der" :
                Locale.getDefault().getDisplayLanguage().equals("español") ||
                        Locale.getDefault().getDisplayLanguage().equals("português") ?
                        // spanish or portuguese
                        "de" :
                        Locale.getDefault().getDisplayLanguage().equals("italiano") ?
                                // italian
                                "il" :
                                // everything else
                                dayFromDaysList % 10 == 1 ?
                                        // day number ending with one
                                        "st" :
                                        dayFromDaysList % 10 == 2 ?
                                                // day number ending with two
                                                "nd" :
                                                dayFromDaysList % 10 == 3 ?
                                                        // day number ending with three
                                                        "rd" :
                                                        // every other day
                                                        "th";
    }

    public String getTranslatedMonth(final Context context) {
        final LocalDate currentDate = LocalDate.now();

        return Months.getTranslatedMonth(context, String.valueOf(currentDate.getMonth())
                .charAt(0) + String.valueOf(currentDate.getMonth()).substring(1).toLowerCase()).trim();
    }

    public String getDateTranslated(final Context context, final int dayFromDaysList) {
        return Locale.getDefault().getDisplayLanguage().equals("Deutsch") ?
                // german
                getDayPrefix(dayFromDaysList) + " " + dayFromDaysList + " " + getTranslatedMonth(context) :
                Locale.getDefault().getDisplayLanguage().equals("español") ||
                        Locale.getDefault().getDisplayLanguage().equals("português") ?
                        // spanish or portuguese
                        dayFromDaysList + " " + getDayPrefix(dayFromDaysList) + " " +
                                getTranslatedMonth(context).toLowerCase() :
                        Locale.getDefault().getDisplayLanguage().equals("français") ||
                                Locale.getDefault().getDisplayLanguage().equals("română") ?
                                // french or romanian
                                dayFromDaysList + " " + getTranslatedMonth(context).toLowerCase() :
                                Locale.getDefault().getDisplayLanguage().equals("italiano") ?
                                        // italian
                                        getDayPrefix(dayFromDaysList) + " " +
                                                dayFromDaysList + " " + getTranslatedMonth(context).toLowerCase() :
                                        // english
                                        getTranslatedMonth(context) + " " +
                                                dayFromDaysList + getDayPrefix(dayFromDaysList);
    }
}
