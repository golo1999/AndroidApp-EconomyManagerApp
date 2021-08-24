package com.example.economy_manager.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.economy_manager.R;

import java.util.Locale;

public final class MyCustomMethods {
    private MyCustomMethods() {

    }

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

    // direction : 0 -> left, 1 -> right
    public static void goToActivityInDirection(final @NonNull Activity currentActivity,
                                               final @NonNull Class<? extends Activity> nextActivity,
                                               final int direction) {
        currentActivity.startActivity(new Intent(currentActivity, nextActivity));
        currentActivity.overridePendingTransition(direction == 0 ?
                        R.anim.slide_in_left : R.anim.slide_in_right,
                direction == 0 ? R.anim.slide_out_right : R.anim.slide_out_left);
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
}