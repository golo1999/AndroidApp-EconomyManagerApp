package com.example.economy_manager.utility;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.economy_manager.R;
import com.example.economy_manager.feature.login.LoginActivity;
import com.example.economy_manager.model.MyCustomTime;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class MyCustomMethods {
    private MyCustomMethods() {

    }

    public static void closeTheKeyboard(final @NonNull Activity parentActivity) {
        final View view = parentActivity.getCurrentFocus();

        if (view == null) {
            return;
        }

        final InputMethodManager manager =
                (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean stringIsNotEmpty(final @NonNull String enteredString) {
        return !enteredString.trim().isEmpty();
    }

    public static boolean emailIsValid(final @NonNull String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    @NonNull
    public static String getCurrencySymbol() {
        final String displayLanguage = Locale.getDefault().getDisplayLanguage();

        return displayLanguage.equals(Languages.GERMAN_LANGUAGE) ||
                displayLanguage.equals(Languages.SPANISH_LANGUAGE) ||
                displayLanguage.equals(Languages.FRENCH_LANGUAGE) ||
                displayLanguage.equals(Languages.ITALIAN_LANGUAGE) ||
                displayLanguage.equals(Languages.PORTUGUESE_LANGUAGE) ?
                "€" : displayLanguage.equals(Languages.ROMANIAN_LANGUAGE) ?
                "RON" : "£";
    }

    @NonNull
    public static String getFormattedDate(@NonNull final LocalDate date) {
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

    @NonNull
    public static String getFormattedTime(final Context context,
                                          @NonNull final LocalTime time) {
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

    public static <T extends Comparable<? super T>> void sortListAscending(@NonNull final ArrayList<T> list) {
        if (!list.isEmpty()) {
            Collections.sort(list);
        }
    }

    public static void sortMapDescendingByValue(final @NonNull LinkedHashMap<Integer, Float> map) {
        final List<Map.Entry<Integer, Float>> sortedEntries = new ArrayList<>(map.entrySet());

        sortedEntries.sort((final Map.Entry<Integer, Float> firstEntry, final Map.Entry<Integer, Float> secondEntry) ->
                secondEntry.getValue().compareTo(firstEntry.getValue()));
        map.clear();
        sortedEntries.forEach((final Map.Entry<Integer, Float> entry) -> map.put(entry.getKey(), entry.getValue()));

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

    public static int nameIsValid(@NonNull final String name) {
        if (name.length() < 2) {
            return 0;
        } else {
            // if the first or the last character is not a letter
            if (!Character.isLetter(name.charAt(0)) || !Character.isLetter(name.charAt(name.length() - 1))) {
                return -1;
            }

            int index = -1;

            for (final char character : name.toCharArray()) {
                ++index;
                // if there are consecutive non-letter characters
                if (!Character.isLetter(character) && !Character.isLetter(name.charAt(index + 1))) {
                    return -1;
                }
                // if the character is a digit
                if (Character.isDigit(character)) {
                    return -1;
                }
            }
        }

        return 1;
    }

    public static <T> boolean objectExistsInSharedPreferences(@NonNull Activity parentActivity,
                                                              @NonNull String key,
                                                              Class<T> tClass,
                                                              @NonNull T givenObject) {
        return givenObject.equals(retrieveObjectFromSharedPreferences(parentActivity, key, tClass));
    }

    public static void restartCurrentActivity(@NonNull final Activity activity) {
        activity.startActivity(activity.getIntent());
        activity.finish();
        activity.overridePendingTransition(0, 0);
    }

    public static <T> T retrieveObjectFromSharedPreferences(@NonNull Activity parentActivity,
                                                            @NonNull String key,
                                                            Class<T> tClass) {
        SharedPreferences preferences =
                parentActivity.getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(key, "");

        return gson.fromJson(json, tClass);
    }

    public static <T> void saveObjectToSharedPreferences(@NonNull Activity parentActivity,
                                                         @NonNull T object,
                                                         @NonNull String key) {
        SharedPreferences preferences =
                parentActivity.getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);

        editor.putString(key, json);
        editor.apply();
    }

    public static void showShortMessage(final Context context,
                                        final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongMessage(final Context context,
                                       final String message) {
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
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public static boolean transactionWasMadeInTheLastWeek(final @NonNull MyCustomTime transactionTime) {
        final LocalDate oneWeekAgoDate = LocalDate.now().minusDays(8);
        final LocalDateTime oneWeekAgoDateTime =
                LocalDateTime.of(oneWeekAgoDate, LocalTime.of(23, 59, 59));

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