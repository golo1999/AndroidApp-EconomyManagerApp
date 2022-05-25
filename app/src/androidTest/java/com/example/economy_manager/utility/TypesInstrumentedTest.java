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
public class TypesInstrumentedTest {

    private Context context;

    private int counter;
    private String[] typeNamesInEnglish;
    private String[] translatedTypeNames;

    @Before
    public void initializeVariables() {
        context = ApplicationProvider.getApplicationContext();
        final Resources resources = context.getResources();

        counter = -1;
        typeNamesInEnglish =
                new String[]{"Deposits", "Independent sources", "Salary", "Saving", "Bills", "Car", "Clothes",
                        "Communications", "Eating out", "Entertainment", "Food", "Gifts", "Health", "House", "Pets",
                        "Sports", "Taxi", "Toiletry", "Transport"};
        translatedTypeNames =
                new String[]{resources.getString(R.string.deposits), resources.getString(R.string.independent_sources),
                        resources.getString(R.string.salary), resources.getString(R.string.saving),
                        resources.getString(R.string.bills), resources.getString(R.string.car),
                        resources.getString(R.string.clothes), resources.getString(R.string.communications),
                        resources.getString(R.string.eating_out), resources.getString(R.string.entertainment),
                        resources.getString(R.string.food), resources.getString(R.string.gifts),
                        resources.getString(R.string.health), resources.getString(R.string.house),
                        resources.getString(R.string.pets), resources.getString(R.string.sports),
                        resources.getString(R.string.taxi), resources.getString(R.string.toiletry),
                        resources.getString(R.string.transport)};
    }

    @Test
    public void getTranslatedType() {
        for (final String typeName : typeNamesInEnglish) {
            ++counter;
            assertEquals(Types.getTranslatedType(context, typeName), translatedTypeNames[counter]);
        }

        assertNull(Types.getTranslatedType(context, "SOME_OTHER_TYPE"));
        assertNull(Types.getTranslatedType(context, null));
    }

    @Test
    public void getTypeInEnglish() {
        for (final String typeName : translatedTypeNames) {
            ++counter;
            assertEquals(Types.getTypeInEnglish(context, typeName), typeNamesInEnglish[counter]);
        }

        assertNull(Types.getTypeInEnglish(context, "SOME_OTHER_TYPE"));
        assertNull(Types.getTypeInEnglish(context, null));
    }
}