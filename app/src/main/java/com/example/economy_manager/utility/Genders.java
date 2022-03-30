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
        return gender.equals(activity.getResources().getString(R.string.female)) ?
                "Female" : gender.equals(activity.getResources().getString(R.string.male)) ?
                "Male" : gender.equals(activity.getResources().getString(R.string.other)) ?
                "Other" : null;
    }

    public static int getPositionInGenderList(final Activity activity,
                                              ArrayList<String> gendersList,
                                              final String gender) {
        final String translatedGender = gender.equals("Female") ?
                activity.getResources().getString(R.string.female) : gender.equals("Male") ?
                activity.getResources().getString(R.string.male) : gender.equals("Other") ?
                activity.getResources().getString(R.string.other) : " ";

        return Collections.binarySearch(gendersList, translatedGender);
    }

    public static void populateList(final Activity activity,
                                    final ArrayList<String> gendersList) {
        gendersList.add(activity.getResources().getString(R.string.female));
        gendersList.add(activity.getResources().getString(R.string.male));
        gendersList.add(activity.getResources().getString(R.string.other));
        gendersList.add(activity.getResources().getString(R.string.space_character));
    }
}