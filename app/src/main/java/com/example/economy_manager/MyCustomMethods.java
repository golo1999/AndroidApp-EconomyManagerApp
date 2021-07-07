package com.example.economy_manager;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import java.util.Locale;

public class MyCustomMethods {
    public static void closeTheKeyboard(final @NonNull Activity parentActivity) {
        final View v = parentActivity.getCurrentFocus();

        if (v != null) {
            final InputMethodManager manager = (InputMethodManager) parentActivity.
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static String getCurrencySymbol() {
        final String displayLanguage = Locale.getDefault().getDisplayLanguage();

        return displayLanguage.equals("Deutsch") ||
                displayLanguage.equals("español") ||
                displayLanguage.equals("français") ||
                displayLanguage.equals("italiano") ||
                displayLanguage.equals("português") ?
                "€" : displayLanguage.equals("română") ?
                "RON" : "£";
    }
}