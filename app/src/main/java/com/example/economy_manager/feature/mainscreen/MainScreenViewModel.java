package com.example.economy_manager.feature.mainscreen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.feature.budgetreview.BudgetReviewFragment;
import com.example.economy_manager.feature.lasttentransactions.LastTenTransactionsFragment;
import com.example.economy_manager.feature.moneyspent.MoneySpentFragment;
import com.example.economy_manager.feature.moneyspentpercentage.MoneySpentPercentageFragment;
import com.example.economy_manager.feature.showsavings.ShowSavingsFragment;
import com.example.economy_manager.feature.topfiveexpenses.TopFiveExpensesFragment;
import com.example.economy_manager.model.MyCustomTime;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Languages;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.utility.Types;
import com.facebook.login.LoginManager;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private final String datePrefix = Locale.getDefault().getDisplayLanguage().equals(Languages.getGERMAN_LANGUAGE()) ?
            // german
            "der" : Locale.getDefault().getDisplayLanguage().equals(Languages.getITALIAN_LANGUAGE()) ?
            // italian
            "il" : Locale.getDefault().getDisplayLanguage().equals(Languages.getPORTUGUESE_LANGUAGE()) ?
            // portuguese
            "de" :
            // every other language
            "";

    private final String daySuffix = !Locale.getDefault().getDisplayLanguage().equals(Languages.getENGLISH_LANGUAGE()) ?
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

    private final String separator = Locale.getDefault().getDisplayLanguage().equals(Languages.getSPANISH_LANGUAGE()) ?
            // spanish
            "de" :
            // every other language but spanish
            "";

    private final SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL",
            Locale.getDefault().getDisplayLanguage().equals(Languages.getGERMAN_LANGUAGE()) ?
                    //german
                    Locale.GERMAN : Locale.getDefault().getDisplayLanguage().equals(Languages.getSPANISH_LANGUAGE()) ?
                    // spanish
                    Locale.forLanguageTag("es-ES") : Locale.getDefault().getDisplayLanguage().equals(Languages.getFRENCH_LANGUAGE()) ?
                    // french
                    Locale.FRENCH : Locale.getDefault().getDisplayLanguage().equals(Languages.getITALIAN_LANGUAGE()) ?
                    // italian
                    Locale.ITALIAN : Locale.getDefault().getDisplayLanguage().equals(Languages.getPORTUGUESE_LANGUAGE()) ?
                    // portuguese
                    Locale.forLanguageTag("pt-PT") : Locale.getDefault().getDisplayLanguage().equals(Languages.getROMANIAN_LANGUAGE()) ?
                    // romanian
                    Locale.forLanguageTag("ro-RO") :
                    // every other language
                    Locale.ENGLISH);

    private final String currentDateTranslated = Locale.getDefault().getDisplayLanguage().equals(Languages.getGERMAN_LANGUAGE()) ||
            Locale.getDefault().getDisplayLanguage().equals(Languages.getITALIAN_LANGUAGE()) ||
            Locale.getDefault().getDisplayLanguage().equals(Languages.getROMANIAN_LANGUAGE()) ?
            // german, italian or romanian
            getDatePrefix() + " " + getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " +
                    getMonthFormat().format(getCurrentTime().getTime()) + " " + getCurrentTime().get(Calendar.YEAR) :
            Locale.getDefault().getDisplayLanguage().equals(Languages.getSPANISH_LANGUAGE()) ?
                    // spanish
                    getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " + getSeparator() + " " +
                            getMonthFormat().format(getCurrentTime().getTime()) + " " + getSeparator() + " " +
                            getCurrentTime().get(Calendar.YEAR) :
                    Locale.getDefault().getDisplayLanguage().equals(Languages.getFRENCH_LANGUAGE()) ?
                            // french
                            getCurrentTime().get(Calendar.DAY_OF_MONTH) + " " +
                                    getMonthFormat().format(getCurrentTime().getTime()) + " " +
                                    getCurrentTime().get(Calendar.YEAR) :
                            Locale.getDefault().getDisplayLanguage().equals(Languages.getPORTUGUESE_LANGUAGE()) ?
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
                getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

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

        return !darkThemeEnabled ? activity.getColor(R.color.turkish_sea) : Color.WHITE;
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

    public LinkedHashMap<Integer, Float> getSortedMapDescendingByValue(final LinkedHashMap<Integer, Float> map) {
        final List<Map.Entry<Integer, Float>> sortedEntries = new ArrayList<>(map.entrySet());

        sortedEntries.sort((final Map.Entry<Integer, Float> entry1, final Map.Entry<Integer, Float> entry2) ->
                entry2.getValue().compareTo(entry1.getValue()));

        map.clear();

        sortedEntries.forEach((final Map.Entry<Integer, Float> entry) -> map.put(entry.getKey(), entry.getValue()));

        return map;
    }

    public boolean transactionWasMadeInTheLastWeek(final @NonNull MyCustomTime transactionTime) {
        final LocalDate oneWeekAgoDate = LocalDate.now().minusDays(8);

        final LocalDateTime oneWeekAgoDateTime = LocalDateTime.of(oneWeekAgoDate, LocalTime.of(23, 59, 59));

        final LocalDate nextDayDate = LocalDate.now().plusDays(1);

        final LocalDateTime nextDayDateTime = LocalDateTime.of(nextDayDate, LocalTime.of(0, 0));

        final LocalDateTime transactionTimeParsed = LocalDateTime.of(transactionTime.getYear(),
                transactionTime.getMonth(), transactionTime.getDay(), transactionTime.getHour(),
                transactionTime.getMinute(), transactionTime.getSecond());

        final boolean isAfterOneWeekAgoDate = transactionTimeParsed.isAfter(oneWeekAgoDateTime);

        final boolean isBeforeCurrentDate = transactionTimeParsed.isBefore(nextDayDateTime);

        return isAfterOneWeekAgoDate && isBeforeCurrentDate;
    }
}