package com.example.economy_manager.utility;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.economy_manager.R;

import java.util.ArrayList;
import java.util.Collections;

public final class Genders {

    private Genders() {

    }

    @NonNull
    public static String getGenderInEnglish(@NonNull final Activity activity,
                                            @NonNull final String gender) {
        return gender.equals(activity.getResources().getString(R.string.female)) ?
                "Female" : gender.equals(activity.getResources().getString(R.string.male)) ?
                "Male" : gender.equals(activity.getResources().getString(R.string.other)) ?
                "Other" : "Unknown gender";
    }

    public static int getPositionInGenderList(final Activity activity,
                                              ArrayList<String> gendersList,
                                              @NonNull final String gender) {
        final String translatedGender = gender.equals("Female") ?
                activity.getResources().getString(R.string.female) : gender.equals("Male") ?
                activity.getResources().getString(R.string.male) : gender.equals("Other") ?
                activity.getResources().getString(R.string.other) :
                activity.getResources().getString(R.string.unknown_gender);

        return Collections.binarySearch(gendersList, translatedGender);
    }

    public static void populateList(final Activity activity,
                                    final ArrayList<String> gendersList) {
        gendersList.add(activity.getResources().getString(R.string.female));
        gendersList.add(activity.getResources().getString(R.string.male));
        gendersList.add(activity.getResources().getString(R.string.other));
        gendersList.add(activity.getResources().getString(R.string.unknown_gender));
    }
}