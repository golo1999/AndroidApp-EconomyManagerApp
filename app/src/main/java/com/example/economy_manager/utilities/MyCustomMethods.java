package com.example.economy_manager.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.economy_manager.R;
import com.example.economy_manager.login_part.views.LogInActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

public final class MyCustomMethods {
    private MyCustomMethods() {

    }

    public static void closeTheKeyboard(final @NonNull Activity parentActivity) {
        final View view = parentActivity.getCurrentFocus();

        if (view != null) {
            final InputMethodManager manager =
                    (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void finishActivityWithFadeTransition(final @NonNull Activity currentActivity) {
        currentActivity.finish();
        currentActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Method for finishing the current activity with sliding transition into a direction
     * Direction: 0 (left), 1 (right)
     */
    public static void finishActivityWithSlideTransition(final @NonNull Activity currentActivity,
                                                         final int direction) {
        currentActivity.finish();
        currentActivity.overridePendingTransition(direction == 0 ?
                        R.anim.slide_in_left : R.anim.slide_in_right,
                direction == 0 ? R.anim.slide_out_right : R.anim.slide_out_left);
    }

    public static String getCurrencySymbol() {
        final String displayLanguage = Locale.getDefault().getDisplayLanguage();

        return displayLanguage.equals(Languages.getGermanLanguage()) ||
                displayLanguage.equals(Languages.getSpanishLanguage()) ||
                displayLanguage.equals(Languages.getFrenchLanguage()) ||
                displayLanguage.equals(Languages.getItalianLanguage()) ||
                displayLanguage.equals(Languages.getPortugueseLanguage()) ?
                "€" : displayLanguage.equals(Languages.getRomanianLanguage()) ?
                "RON" : "£";
    }

    public static String getFormattedDate(final LocalDate date) {
        final String dayName = date.getDayOfWeek().name().charAt(0) +
                date.getDayOfWeek().name().substring(1).toLowerCase();

        final String monthName = String.valueOf(date.getMonth()).charAt(0) +
                String.valueOf(date.getMonth()).substring(1).toLowerCase();

        final int day = date.getDayOfMonth();

        final StringBuilder transactionDate = new StringBuilder(dayName)
                .append(", ")
                .append(monthName)
                .append(" ")
                .append(day);

        // displaying the year if it's not the current one
        if (date.getYear() != LocalDate.now().getYear()) {
            transactionDate.append(", ")
                    .append(date.getYear());
        }

        return String.valueOf(transactionDate);
    }

    public static String getFormattedTime(final Context context,
                                          final LocalTime time) {
        final String hour = time.getHour() < 10 ?
                "0" + time.getHour() : time.getHour() > 12 && !DateFormat.is24HourFormat(context) ?
                String.valueOf(time.getHour() - 12) : String.valueOf(time.getHour());

        final String minute = time.getMinute() < 10 ?
                "0" + time.getMinute() : String.valueOf(time.getMinute());

        final String second = time.getSecond() < 10 ?
                "0" + time.getSecond() : String.valueOf(time.getSecond());

        final StringBuilder transactionTime = new StringBuilder(hour)
                .append(":")
                .append(minute)
                .append(":")
                .append(second);

        // setting AM or PM if the time format is 12h
        if (!DateFormat.is24HourFormat(context)) {
            transactionTime.append(" ")
                    .append(time.getHour() < 12 ? "AM" : "PM");
        }

        return String.valueOf(transactionTime);
    }

    public static float getRoundedNumberToNDecimalPlaces(final float number,
                                                         final int scale) {
        int pow = 10;

        for (int i = 1; i < scale; i++) {
            pow *= 10;
        }

        float tmp = number * pow;

        return ((float) ((int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp))) / pow;
    }

    public static void goToActivityWithFadeTransition(final @NonNull Activity currentActivity,
                                                      final @NonNull Class<? extends Activity> nextActivity) {
        currentActivity.startActivity(new Intent(currentActivity, nextActivity));
        currentActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /**
     * Method for navigating to another activity with sliding transition into a direction
     * Direction: 0 (left), 1 (right)
     */
    public static void goToActivityWithSlideTransition(final @NonNull Activity currentActivity,
                                                       final @NonNull Class<? extends Activity> nextActivity,
                                                       final int direction) {
        currentActivity.startActivity(new Intent(currentActivity, nextActivity));
        currentActivity.overridePendingTransition(direction == 0 ?
                        R.anim.slide_in_left : R.anim.slide_in_right,
                direction == 0 ? R.anim.slide_out_right : R.anim.slide_out_left);
    }

    public static void goToActivityWithoutTransition(final @NonNull Activity currentActivity,
                                                     final @NonNull Class<? extends Activity> nextActivity) {
        currentActivity.startActivity(new Intent(currentActivity, nextActivity));
        currentActivity.finish();
    }

    public static void restartCurrentActivity(final Activity activity) {
        activity.startActivity(activity.getIntent());
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static void showShortMessage(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongMessage(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void signInWithFadeTransition(final @NonNull Activity currentActivity,
                                                final @NonNull Class<? extends Activity> nextActivity) {
        currentActivity.finishAffinity();
        currentActivity.startActivity(new Intent(currentActivity, nextActivity));
        currentActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static void signOutWithFadeTransition(final @NonNull Activity activity) {
        activity.finishAffinity();
        activity.startActivity(new Intent(activity, LogInActivity.class));
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}