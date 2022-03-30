package com.example.economy_manager.utility;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.economy_manager.R;

public final class Types {
    private Types() {

    }

    @Nullable
    public static String getTranslatedType(final Context context, @NonNull final String typeInEnglish) {
        return typeInEnglish.equals("Deposits") ?
                context.getString(R.string.deposits) : typeInEnglish.equals("Independent sources") ?
                context.getString(R.string.independent_sources) : typeInEnglish.equals("Salary") ?
                context.getString(R.string.salary) : typeInEnglish.equals("Saving") ?
                context.getString(R.string.saving) : typeInEnglish.equals("Bills") ?
                context.getString(R.string.bills) : typeInEnglish.equals("Car") ?
                context.getString(R.string.car) : typeInEnglish.equals("Clothes") ?
                context.getString(R.string.clothes) : typeInEnglish.equals("Communications") ?
                context.getString(R.string.communications) : typeInEnglish.equals("Eating out") ?
                context.getString(R.string.eating_out) : typeInEnglish.equals("Entertainment") ?
                context.getString(R.string.entertainment) : typeInEnglish.equals("Food") ?
                context.getString(R.string.food) : typeInEnglish.equals("Gifts") ?
                context.getString(R.string.gifts) : typeInEnglish.equals("Health") ?
                context.getString(R.string.health) : typeInEnglish.equals("House") ?
                context.getString(R.string.house) : typeInEnglish.equals("Pets") ?
                context.getString(R.string.pets) : typeInEnglish.equals("Sports") ?
                context.getString(R.string.sports) : typeInEnglish.equals("Taxi") ?
                context.getString(R.string.taxi) : typeInEnglish.equals("Toiletry") ?
                context.getString(R.string.toiletry) : typeInEnglish.equals("Transport") ?
                context.getString(R.string.transport) : null;
    }

    @Nullable
    public static String getTypeInEnglish(@NonNull final Context context, @NonNull final String translatedType) {
        return translatedType.equals(context.getString(R.string.deposits)) ?
                "Deposits" : translatedType.equals(context.getString(R.string.independent_sources)) ?
                "Independent sources" : translatedType.equals(context.getString(R.string.saving)) ?
                "Saving" : translatedType.equals(context.getString(R.string.salary)) ?
                "Salary" : translatedType.equals(context.getString(R.string.bills)) ?
                "Bills" : translatedType.equals(context.getString(R.string.car)) ?
                "Car" : translatedType.equals(context.getString(R.string.clothes)) ?
                "Clothes" : translatedType.equals(context.getString(R.string.communications)) ?
                "Communications" : translatedType.equals(context.getString(R.string.eating_out)) ?
                "Eating out" : translatedType.equals(context.getString(R.string.entertainment)) ?
                "Entertainment" : translatedType.equals(context.getString(R.string.food)) ?
                "Food" : translatedType.equals(context.getString(R.string.gifts)) ?
                "Gifts" : translatedType.equals(context.getString(R.string.health)) ?
                "Health" : translatedType.equals(context.getString(R.string.house)) ?
                "House" : translatedType.equals(context.getString(R.string.pets)) ?
                "Pets" : translatedType.equals(context.getString(R.string.sports)) ?
                "Sports" : translatedType.equals(context.getString(R.string.taxi)) ?
                "Taxi" : translatedType.equals(context.getString(R.string.toiletry)) ?
                "Toiletry" : translatedType.equals(context.getString(R.string.transport)) ?
                "Transport" : null;
    }
}
