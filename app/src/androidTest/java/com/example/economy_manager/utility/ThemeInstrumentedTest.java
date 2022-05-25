package com.example.economy_manager.utility;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.economy_manager.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ThemeInstrumentedTest {

    private Context context;

    @Before
    public void initializeVariables() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void getBackgroundColor() {
        assertEquals(Theme.getBackgroundColor(context, true), context.getColor(R.color.primaryDark));
        assertEquals(Theme.getBackgroundColor(context, false), context.getColor(R.color.primaryLight));
    }

    @Test
    public void getDropdownBackgroundColor() {
        assertEquals(Theme.getDropdownBackgroundColor(context, true), context.getColor(R.color.tertiaryDark));
        assertEquals(Theme.getDropdownBackgroundColor(context, false), context.getColor(R.color.quaternaryLight));
    }
}