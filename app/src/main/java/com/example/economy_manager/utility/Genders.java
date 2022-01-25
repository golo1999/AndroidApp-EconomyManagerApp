package com.example.economy_manager.utility;

import android.app.Activity;

import androidx.annotation.Nullable;

import com.example.economy_manager.R;

import java.util.ArrayList;
import java.util.Collections;

public final class Genders {
    private Genders() {

    }

    @Nullable
    public static String getGenderInEnglish(final Activity activity,
                                            final String gender) {
        return gender.equals(activity.getResources().getString(R.string.edit_account_gender_female)) ?
                "Female" : gender.equals(activity.getResources().getString(R.string.edit_account_gender_male)) ?
                "Male" : gender.equals(activity.getResources().getString(R.string.edit_account_gender_other)) ?
                "Other" : null;
    }

    public static int getPositionInGenderList(final Activity activity,
                                              ArrayList<String> gendersList,
                                              final String gender) {
        final String translatedGender = gender.equals("Female") ?
                activity.getResources().getString(R.string.edit_account_gender_female) : gender.equals("Male") ?
                activity.getResources().getString(R.string.edit_account_gender_male) : gender.equals("Other") ?
                activity.getResources().getString(R.string.edit_account_gender_other) : " ";

        return Collections.binarySearch(gendersList, translatedGender);
    }

    public static void populateList(final Activity activity,
                                    final ArrayList<String> gendersList) {
        gendersList.add(activity.getResources().getString(R.string.edit_account_gender_female));
        gendersList.add(activity.getResources().getString(R.string.edit_account_gender_male));
        gendersList.add(activity.getResources().getString(R.string.edit_account_gender_other));
        gendersList.add(activity.getResources().getString(R.string.edit_account_gender_select));
    }
}