package com.example.economy_manager.utility;

import android.content.Context;

import com.example.economy_manager.R;

public final class Theme {

    private Theme() {

    }

    public static int getBackgroundColor(final Context context,
                                         final boolean isDarkThemeEnabled) {
        return isDarkThemeEnabled ?
                context.getColor(R.color.primaryDark) : context.getColor(R.color.primaryLight);
    }

    public static int getDropdownBackgroundColor(final Context context,
                                                 final boolean isDarkThemeEnabled) {
        return isDarkThemeEnabled ?
                context.getColor(R.color.tertiaryDark) : context.getColor(R.color.quaternaryLight);
    }
}