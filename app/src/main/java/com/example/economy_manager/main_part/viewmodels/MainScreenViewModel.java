package com.example.economy_manager.main_part.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.views.fragments.BudgetReviewFragment;
import com.example.economy_manager.main_part.views.fragments.LastTenTransactionsFragment;
import com.example.economy_manager.main_part.views.fragments.MoneySpentFragment;
import com.example.economy_manager.main_part.views.fragments.MoneySpentPercentageFragment;
import com.example.economy_manager.main_part.views.fragments.ShowSavingsFragment;
import com.example.economy_manager.main_part.views.fragments.TopFiveExpensesFragment;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.Languages;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.facebook.login.LoginManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainScreenViewModel extends ViewModel {
    private UserDetails userDetails;

    private final ShowSavingsFragment showSavingsFragment = ShowSavingsFragment.newInstance();

    private final BudgetReviewFragment budgetReviewFragment = BudgetReviewFragment.newInstance();

    private final MoneySpentFragment moneySpentFragment = MoneySpentFragment.newInstance();

    private final LastTenTransactionsFragment lastTenTransactionsFragment = LastTenTransactionsFragment.newInstance();

    private final TopFiveExpensesFragment topFiveExpensesFragment = TopFiveExpensesFragment.newInstance();

    private final MoneySpentPercentageFragment moneySpentPercentageFragment = MoneySpentPercentageFragment.newInstance();

    private final Calendar currentTime = Calendar.getInstance();

    private final int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);

    private final String datePrefix = Locale.getDefault().getDisplayLanguage().equals(Languages.getGermanLanguage()) ?
            // german
            "der" : Locale.getDefault().getDisplayLanguage().equals(Languages.getItalianLanguage()) ?
            // italian
            "il" : Locale.getDefault().getDisplayLanguage().equals(Languages.getPortugueseLanguage()) ?
            // portuguese
            "de" :
            // every other language
            "";

    private final String daySuffix = !Locale.getDefault().getDisplayLanguage().equals(Languages.getEnglishLanguage()) ?
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

    private final String separator = Locale.getDefault().getDisplayLanguage().equals(Languages.getSpanishLanguage()) ?
            // spanish
            "de" :
            // every other language but spanish
            "";

    private final SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL",
            Locale.getDefault().getDisplayLanguage().equals(Languages.getGermanLanguage()) ?
                    //german
                    Locale.GERMAN : Locale.getDefault().getDisplayLanguage().equals(Languages.getSpanishLanguage()) ?
                    // spanish
                    Locale.forLanguageTag("es-ES") : Locale.getDefault().getDisplayLanguage().equals(Languages.getFrenchLanguage()) ?
                    // french
                    Locale.FRENCH : Locale.getDefault().getDisplayLanguage().equals(Languages.getItalianLanguage()) ?
                    // italian
                    Locale.ITALIAN : Locale.getDefault().getDisplayLanguage().equals(Languages.getPortugueseLanguage()) ?
                    // portuguese
                    Locale.forLanguageTag("pt-PT") : Locale.getDefault().getDisplayLanguage().equals(Languages.getRomanianLanguage()) ?
                    // romanian
                    Locale.forLanguageTag("ro-RO") :
                    // every other language
                    Locale.ENGLISH);

    private final String currentDateTranslated = Locale.getDefault().getDisplayLanguage().equals(Languages.getGermanLanguage()) ||
            Locale.getDefault().getDisplayLanguage().equals(Languages.getItalianLanguage()) ||
            Locale.getDefault().getDisplayLanguage().equals(Languages.getRomanianLanguage()) ?
            // german, italian or romanian
            getDatePrefix() + " " + getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " +
                    getMonthFormat().format(getCurrentTime().getTime()) + " " + getCurrentTime().get(Calendar.YEAR) :
            Locale.getDefault().getDisplayLanguage().equals(Languages.getSpanishLanguage()) ?
                    // spanish
                    getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " + getSeparator() + " " +
                            getMonthFormat().format(getCurrentTime().getTime()) + " " + getSeparator() + " " +
                            getCurrentTime().get(Calendar.YEAR) :
                    Locale.getDefault().getDisplayLanguage().equals(Languages.getFrenchLanguage()) ?
                            // french
                            getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " +
                                    getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                    getCurrentTime().get(Calendar.YEAR) :
                            Locale.getDefault().getDisplayLanguage().equals(Languages.getPortugueseLanguage()) ?
                                    // portuguese
                                    getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " + getDatePrefix() + " " +
                                            getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                            getDatePrefix() + " " + getCurrentTime().get(Calendar.YEAR) :
                                    // every other language
                                    getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                            getCurrentTime().get(Calendar.DAY_OF_MONTH) + getDaySuffix() + ", "
                                            + getCurrentTime().get(Calendar.YEAR);

    private int timerCounter = 0;

    public int getActivityTheme() {
        final boolean darkThemeEnabled = getUserDetails() != null ?
                getUserDetails().getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        return !darkThemeEnabled ?
                R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public ShowSavingsFragment getFragmentShowSavings() {
        return showSavingsFragment;
    }

    public BudgetReviewFragment getFragmentBudgetReview() {
        return budgetReviewFragment;
    }

    public MoneySpentFragment getFragmentMoneySpent() {
        return moneySpentFragment;
    }

    public LastTenTransactionsFragment getFragmentLastTenTransactions() {
        return lastTenTransactionsFragment;
    }

    public TopFiveExpensesFragment getFragmentTopFiveExpenses() {
        return topFiveExpensesFragment;
    }

    public MoneySpentPercentageFragment getFragmentMoneySpentPercentage() {
        return moneySpentPercentageFragment;
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

    public void goToActivity(final @NonNull Activity currentActivity,
                             final @NonNull Class<? extends Activity> nextActivity) {
        MyCustomMethods.goToActivityWithFadeTransition(currentActivity, nextActivity);
    }

    public int getTextColor(final @NonNull Activity activity) {
        final boolean darkThemeEnabled = getUserDetails() != null ?
                getUserDetails().getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        return !darkThemeEnabled ? activity.getColor(R.color.turkish_sea) : Color.WHITE;
    }

    public void logout(final @NonNull Activity activity) {
        LoginManager.getInstance().logOut();
        MyCustomVariables.getFirebaseAuth().signOut();
        MyCustomMethods.signOutWithFadeTransition(activity);
    }

    public void onClickHandler() {

    }

    public int getTimerCounter() {
        return timerCounter;
    }

    public void setTimerCounter(int timerCounter) {
        this.timerCounter = timerCounter;
    }
}