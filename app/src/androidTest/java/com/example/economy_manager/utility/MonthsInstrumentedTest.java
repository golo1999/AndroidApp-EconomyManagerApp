package com.example.economy_manager.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.content.res.Resources;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.economy_manager.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MonthsInstrumentedTest {

    private Context context;

    private String[] monthNamesInEnglish;
    private String[] translatedMonthNames;

    @Before
    public void initializeVariables() {
        context = ApplicationProvider.getApplicationContext();
        final Resources resources = context.getResources();

        monthNamesInEnglish = new String[]{"January", "February", "March", "April", "May", "June", "July", "August",
                "September", "October", "November", "December"};
        translatedMonthNames =
                new String[]{resources.getString(R.string.january), resources.getString(R.string.february),
                        resources.getString(R.string.march), resources.getString(R.string.april),
                        resources.getString(R.string.may), resources.getString(R.string.june),
                        resources.getString(R.string.july), resources.getString(R.string.august),
                        resources.getString(R.string.september), resources.getString(R.string.october),
                        resources.getString(R.string.november), resources.getString(R.string.december)};
    }

    @Test
    public void getIndexFromMonth() {
        for (int counter = 0; counter < translatedMonthNames.length; ++counter) {
            assertEquals(Months.getIndexFromMonth(context, translatedMonthNames[counter]), counter);
        }

        assertEquals(Months.getIndexFromMonth(context, "SOME_OTHER_MONTH_NAME"), -1);
        assertEquals(Months.getIndexFromMonth(context, null), -1);
    }

    @Test
    public void getMonthFromIndex() {
        for (int counter = 0; counter < translatedMonthNames.length; ++counter) {
            assertEquals(Months.getMonthFromIndex(context, counter), translatedMonthNames[counter]);
        }

        assertNull(Months.getMonthFromIndex(context, -1));
        assertNull(Months.getMonthFromIndex(context, 12));
    }

    @Test
    public void getMonthInEnglish() {
        int counter = -1;

        for (final String translatedMonthName : translatedMonthNames) {
            ++counter;
            assertEquals(Months.getMonthInEnglish(context, translatedMonthName), monthNamesInEnglish[counter]);
        }

        assertEquals(Months.getMonthInEnglish(context, "SOME_OTHER_MONTH_NAME"), "");
        assertEquals(Months.getMonthInEnglish(context, null), "");
    }

    @Test
    public void getTranslatedMonth() {
        int counter = -1;

        for (final String monthName : monthNamesInEnglish) {
            ++counter;
            assertEquals(Months.getTranslatedMonth(context, monthName), translatedMonthNames[counter]);
        }

        assertEquals(Months.getTranslatedMonth(context, "SOME_OTHER_MONTH_NAME"), "");
        assertEquals(Months.getTranslatedMonth(context, null), "");
    }
}