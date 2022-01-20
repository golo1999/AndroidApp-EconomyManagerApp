package com.example.economy_manager.utility;

import android.app.Application;

import androidx.annotation.Nullable;

import com.example.economy_manager.R;

import java.util.ArrayList;
import java.util.Collections;

public final class Genders {
    private Genders() {

    }

    @Nullable
    public static String getGenderInEnglish(final Application app,
                                            final String gender) {
        return gender.equals(app.getResources().getString(R.string.edit_account_gender_female)) ?
                "Female" : gender.equals(app.getResources().getString(R.string.edit_account_gender_male)) ?
                "Male" : gender.equals(app.getResources().getString(R.string.edit_account_gender_other)) ?
                "Other" : null;
    }

    public static int getPositionInGenderList(final Application app,
                                              ArrayList<String> gendersList,
                                              final String gender) {
        final String translatedGender = gender.equals("Female") ?
                app.getResources().getString(R.string.edit_account_gender_female) : gender.equals("Male") ?
                app.getResources().getString(R.string.edit_account_gender_male) : gender.equals("Other") ?
                app.getResources().getString(R.string.edit_account_gender_other) : " ";

        return Collections.binarySearch(gendersList, translatedGender);
    }

    public static void populateList(final Application app,
                                    final ArrayList<String> gendersList) {
        gendersList.add(app.getResources().getString(R.string.edit_account_gender_female));
        gendersList.add(app.getResources().getString(R.string.edit_account_gender_male));
        gendersList.add(app.getResources().getString(R.string.edit_account_gender_other));
        gendersList.add(app.getResources().getString(R.string.edit_account_gender_select));
    }
}