package com.example.economy_manager.feature.mainscreen;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.feature.budgetreview.BudgetReviewFragment;
import com.example.economy_manager.feature.favoriteexpenses.FavoriteExpensesCategoryFragment;
import com.example.economy_manager.feature.lasttentransactions.LastTenTransactionsFragment;
import com.example.economy_manager.feature.lastweekexpenses.LastWeekExpensesFragment;
import com.example.economy_manager.feature.moneyspentpercentage.MoneySpentPercentageFragment;
import com.example.economy_manager.feature.monthlysavings.MonthlySavingsFragment;
import com.example.economy_manager.feature.topfiveexpenses.TopFiveExpensesFragment;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Languages;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.utility.Theme;
import com.example.economy_manager.utility.Types;
import com.facebook.login.LoginManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainScreenViewModel extends ViewModel {
    private UserDetails userDetails;

    private final MonthlySavingsFragment monthlySavingsFragment = MonthlySavingsFragment.newInstance();

    private final BudgetReviewFragment budgetReviewFragment = BudgetReviewFragment.newInstance();

    private final LastWeekExpensesFragment lastWeekExpensesFragment = LastWeekExpensesFragment.newInstance();

    private final LastTenTransactionsFragment lastTenTransactionsFragment = LastTenTransactionsFragment.newInstance();

    private final TopFiveExpensesFragment topFiveExpensesFragment = TopFiveExpensesFragment.newInstance();

    private final FavoriteExpensesCategoryFragment favoriteExpensesCategoryFragment = FavoriteExpensesCategoryFragment.newInstance();

    private final MoneySpentPercentageFragment moneySpentPercentageFragment = MoneySpentPercentageFragment.newInstance();

    private final Calendar currentTime = Calendar.getInstance();

    private final int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);

    private final String datePrefix = Locale.getDefault().getDisplayLanguage().equals(Languages.GERMAN_LANGUAGE) ?
            // german
            "der" : Locale.getDefault().getDisplayLanguage().equals(Languages.ITALIAN_LANGUAGE) ?
            // italian
            "il" : Locale.getDefault().getDisplayLanguage().equals(Languages.PORTUGUESE_LANGUAGE) ?
            // portuguese
            "de" :
            // every other language
            "";

    private final String daySuffix = !Locale.getDefault().getDisplayLanguage().equals(Languages.ENGLISH_LANGUAGE) ?
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

    private final String separator = Locale.getDefault().getDisplayLanguage().equals(Languages.SPANISH_LANGUAGE) ?
            // spanish
            "de" :
            // every other language but spanish
            "";

    private final SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL",
            Locale.getDefault().getDisplayLanguage().equals(Languages.GERMAN_LANGUAGE) ?
                    //german
                    Locale.GERMAN : Locale.getDefault().getDisplayLanguage().equals(Languages.SPANISH_LANGUAGE) ?
                    // spanish
                    Locale.forLanguageTag("es-ES") : Locale.getDefault().getDisplayLanguage().equals(Languages.FRENCH_LANGUAGE) ?
                    // french
                    Locale.FRENCH : Locale.getDefault().getDisplayLanguage().equals(Languages.ITALIAN_LANGUAGE) ?
                    // italian
                    Locale.ITALIAN : Locale.getDefault().getDisplayLanguage().equals(Languages.PORTUGUESE_LANGUAGE) ?
                    // portuguese
                    Locale.forLanguageTag("pt-PT") : Locale.getDefault().getDisplayLanguage().equals(Languages.ROMANIAN_LANGUAGE) ?
                    // romanian
                    Locale.forLanguageTag("ro-RO") :
                    // every other language
                    Locale.ENGLISH);

    private final String currentDateTranslated = Locale.getDefault().getDisplayLanguage().equals(Languages.GERMAN_LANGUAGE) ||
            Locale.getDefault().getDisplayLanguage().equals(Languages.ITALIAN_LANGUAGE) ||
            Locale.getDefault().getDisplayLanguage().equals(Languages.ROMANIAN_LANGUAGE) ?
            // german, italian or romanian
            getDatePrefix() + " " + getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " +
                    getMonthFormat().format(getCurrentTime().getTime()) + " " + getCurrentTime().get(Calendar.YEAR) :
            Locale.getDefault().getDisplayLanguage().equals(Languages.SPANISH_LANGUAGE) ?
                    // spanish
                    getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " + getSeparator() + " " +
                            getMonthFormat().format(getCurrentTime().getTime()) + " " + getSeparator() + " " +
                            getCurrentTime().get(Calendar.YEAR) :
                    Locale.getDefault().getDisplayLanguage().equals(Languages.FRENCH_LANGUAGE) ?
                            // french
                            getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " +
                                    getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                    getCurrentTime().get(Calendar.YEAR) :
                            Locale.getDefault().getDisplayLanguage().equals(Languages.PORTUGUESE_LANGUAGE) ?
                                    // portuguese
                                    getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " + getDatePrefix() + " " +
                                            getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                            getDatePrefix() + " " + getCurrentTime().get(Calendar.YEAR) :
                                    // every other language
                                    getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                            getCurrentTime().get(Calendar.DAY_OF_MONTH) + getDaySuffix() + ", "
                                            + getCurrentTime().get(Calendar.YEAR);

    private int timerCounter = 0;

    public int getActivityTheme(final Context context) {
        final boolean isDarkThemeEnabled = getUserDetails() != null ?
                getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

        return Theme.getBackgroundColor(context, isDarkThemeEnabled);
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public MonthlySavingsFragment getFragmentShowSavings() {
        return monthlySavingsFragment;
    }

    public BudgetReviewFragment getFragmentBudgetReview() {
        return budgetReviewFragment;
    }

    public LastWeekExpensesFragment getFragmentMoneySpent() {
        return lastWeekExpensesFragment;
    }

    public LastTenTransactionsFragment getFragmentLastTenTransactions() {
        return lastTenTransactionsFragment;
    }

    public TopFiveExpensesFragment getFragmentTopFiveExpenses() {
        return topFiveExpensesFragment;
    }

    public FavoriteExpensesCategoryFragment getFavoriteExpensesCategoryFragment() {
        return favoriteExpensesCategoryFragment;
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
                context.getResources().getString(R.string.good_morning).trim() : getCurrentHour() < 18 ?
                context.getResources().getString(R.string.good_afternoon).trim() :
                context.getResources().getString(R.string.good_evening).trim();
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

    public int getPieChartCategoryColor(final @NonNull Context context,
                                        final int key) {
        return context.getColor(key == 4 ?
                R.color.expenses_bills : key == 5 ?
                R.color.expenses_car : key == 6 ?
                R.color.expenses_clothes : key == 7 ?
                R.color.expenses_communications : key == 8 ?
                R.color.expenses_eating_out : key == 9 ?
                R.color.expenses_entertainment : key == 10 ?
                R.color.expenses_food : key == 11 ?
                R.color.expenses_gifts : key == 12 ?
                R.color.expenses_health : key == 13 ?
                R.color.expenses_house : key == 14 ?
                R.color.expenses_pets : key == 15 ?
                R.color.expenses_sports : key == 16 ?
                R.color.expenses_taxi : key == 17 ?
                R.color.expenses_toiletry : R.color.expenses_transport);
    }

    public int getTextColor(final @NonNull Activity activity) {
        final boolean darkThemeEnabled = getUserDetails() != null ?
                getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

        return darkThemeEnabled ? activity.getColor(R.color.tertiaryLight) : activity.getColor(R.color.quaternaryLight);
    }

    public void goToActivity(final @NonNull Activity currentActivity,
                             final @NonNull Class<? extends Activity> nextActivity) {
        MyCustomMethods.goToActivityWithFadeTransition(currentActivity, nextActivity);
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

    public LinearLayout getPieChartDetail(final @NonNull Context context,
                                          final int categoryIndex,
                                          final int categoryPercentage,
                                          final int categoryColor) {
        final LinearLayout categoryLayout = new LinearLayout(context);

        final LinearLayout.LayoutParams categoryLayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        categoryLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        categoryLayout.setLayoutParams(categoryLayoutParams);

        final View categoryView = new View(context);

        final ViewGroup.LayoutParams categoryViewParams = new ViewGroup.LayoutParams(30, 30);

        categoryView.setBackgroundColor(categoryColor);
        categoryView.setLayoutParams(categoryViewParams);
        categoryLayout.addView(categoryView);

        final TextView categoryTextView = new TextView(context);

        final ViewGroup.LayoutParams categoryTextViewParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final String categoryTextViewText = Types.getTranslatedType(context,
                String.valueOf(Transaction.getTypeFromIndexInEnglish(categoryIndex))) +
                " " + "(" + categoryPercentage + "%)";

        categoryTextView.setPaddingRelative(10, 0, 0, 0);
        categoryTextView.setText(categoryTextViewText);
        categoryTextView.setTextSize(16);
        categoryTextView.setLayoutParams(categoryTextViewParams);
        categoryLayout.addView(categoryTextView);

        return categoryLayout;
    }

//    public void setPieChartData() {
//        transactionTypesMap.forEach((final Integer key, final Float value) -> {
//            final int categoryPercentage = (int) (100 * (value / totalMonthlyExpenses));
//
//            final int categoryColor = viewModel.getPieChartCategoryColor(requireContext(), key);
//
//            final PieModel pieSlice = new PieModel("category" + key, categoryPercentage, categoryColor);
//
//            binding.pieChart.addPieSlice(pieSlice);
//
//            transactionTypesIndex.getAndIncrement();
//
//            if (transactionTypesIndex.get() <= 5) {
//                addPieChartDetail(key, categoryPercentage, categoryColor);
//            }
//        });
//    }
}