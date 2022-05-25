package com.example.economy_manager.utility;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.res.Resources;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.economy_manager.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DaysInstrumentedTest {

    private Context context;

    @Before
    public void initializeVariables() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void getTranslatedDayName() {
        final Resources resources = context.getResources();
        final String[] dayNamesInEnglish =
                {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        final String[] translatedDayNames = {resources.getString(R.string.monday), resources.getString(R.string.tuesday),
                resources.getString(R.string.wednesday), resources.getString(R.string.thursday),
                resources.getString(R.string.friday), resources.getString(R.string.saturday),
                resources.getString(R.string.sunday)};
        int counter = -1;

        for (final String dayName : dayNamesInEnglish) {
            ++counter;
            assertEquals(Days.getTranslatedDayName(context, dayName), translatedDayNames[counter]);
        }
    }
}