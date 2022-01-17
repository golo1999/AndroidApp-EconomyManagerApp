package com.example.economy_manager.feature.monthlybalance;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Languages;
import com.example.economy_manager.utility.Months;
import com.example.economy_manager.utility.MyCustomVariables;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MonthlyBalanceViewModel extends ViewModel {
    private UserDetails userDetails;
    private final SimpleDateFormat currentMonthFormat = new SimpleDateFormat("LLLL", Locale.ENGLISH);
    private final Calendar currentTime = Calendar.getInstance();

    public boolean checkIfDayCanBeAddedToList(final ArrayList<Integer> daysList,
                                              final int dayToBeAdded) {
        if (!daysList.isEmpty()) {
            for (final int dayFromList : daysList) {
                if (dayFromList == dayToBeAdded) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getActivityTheme() {
        final boolean checked = getUserDetails() != null ?
                getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

        return !checked ? R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift;
    }

    public String getActivityTitle(final Context context) {
        final int year = getCurrentYear();

        final String month = Months.getTranslatedMonth(context, getCurrentMonthName());

        return month.trim() + " " + year;
    }

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
        return Locale.getDefault().getDisplayLanguage().equals(Languages.getGERMAN_LANGUAGE()) ?
                // german
                "der" :
                Locale.getDefault().getDisplayLanguage().equals(Languages.getSPANISH_LANGUAGE()) ||
                        Locale.getDefault().getDisplayLanguage().equals(Languages.getPORTUGUESE_LANGUAGE()) ?
                        // spanish or portuguese
                        "de" :
                        Locale.getDefault().getDisplayLanguage().equals(Languages.getITALIAN_LANGUAGE()) ?
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
        return Locale.getDefault().getDisplayLanguage().equals(Languages.getGERMAN_LANGUAGE()) ?
                // german
                getDayPrefix(dayFromDaysList) + " " + dayFromDaysList + " " + getTranslatedMonth(context) :
                Locale.getDefault().getDisplayLanguage().equals(Languages.getSPANISH_LANGUAGE()) ||
                        Locale.getDefault().getDisplayLanguage().equals(Languages.getPORTUGUESE_LANGUAGE()) ?
                        // spanish or portuguese
                        dayFromDaysList + " " + getDayPrefix(dayFromDaysList) + " " +
                                getTranslatedMonth(context).toLowerCase() :
                        Locale.getDefault().getDisplayLanguage().equals(Languages.getFRENCH_LANGUAGE()) ||
                                Locale.getDefault().getDisplayLanguage().equals(Languages.getROMANIAN_LANGUAGE()) ?
                                // french or romanian
                                dayFromDaysList + " " + getTranslatedMonth(context).toLowerCase() :
                                Locale.getDefault().getDisplayLanguage().equals(Languages.getITALIAN_LANGUAGE()) ?
                                        // italian
                                        getDayPrefix(dayFromDaysList) + " " +
                                                dayFromDaysList + " " + getTranslatedMonth(context).toLowerCase() :
                                        // english
                                        getTranslatedMonth(context) + " " +
                                                dayFromDaysList + getDayPrefix(dayFromDaysList);
    }
}
