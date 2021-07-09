package com.example.economy_manager.utility;

import android.content.Context;

import com.example.economy_manager.R;

public final class Types {
    private Types() {

    }

    public static String getTranslatedType(final Context context, final String typeInEnglish) {
        return typeInEnglish.equals("Deposits") ?
                context.getString(R.string.add_money_deposits) : typeInEnglish.equals("Independent sources") ?
                context.getString(R.string.add_money_independent_sources) : typeInEnglish.equals("Salary") ?
                context.getString(R.string.salary) : typeInEnglish.equals("Saving") ?
                context.getString(R.string.saving) : typeInEnglish.equals("Bills") ?
                context.getString(R.string.subtract_money_bills) : typeInEnglish.equals("Car") ?
                context.getString(R.string.subtract_money_car) : typeInEnglish.equals("Clothes") ?
                context.getString(R.string.subtract_money_clothes) : typeInEnglish.equals("Communications") ?
                context.getString(R.string.subtract_money_communications) : typeInEnglish.equals("Eating out") ?
                context.getString(R.string.subtract_money_eating_out) : typeInEnglish.equals("Entertainment") ?
                context.getString(R.string.subtract_money_entertainment) : typeInEnglish.equals("Food") ?
                context.getString(R.string.subtract_money_food) : typeInEnglish.equals("Gifts") ?
                context.getString(R.string.subtract_money_gifts) : typeInEnglish.equals("Health") ?
                context.getString(R.string.subtract_money_health) : typeInEnglish.equals("House") ?
                context.getString(R.string.subtract_money_house) : typeInEnglish.equals("Pets") ?
                context.getString(R.string.subtract_money_pets) : typeInEnglish.equals("Sports") ?
                context.getString(R.string.subtract_money_sports) : typeInEnglish.equals("Taxi") ?
                context.getString(R.string.subtract_money_taxi) : typeInEnglish.equals("Toiletry") ?
                context.getString(R.string.subtract_money_toiletry) : typeInEnglish.equals("Transport") ?
                context.getString(R.string.subtract_money_transport) : null;
    }

    public static String getTypeInEnglish(final Context context, final String translatedType) {
        return translatedType.equals(context.getString(R.string.add_money_deposits)) ?
                "Deposits" : translatedType.equals(context.getString(R.string.add_money_independent_sources)) ?
                "Independent sources" : translatedType.equals(context.getString(R.string.saving)) ?
                "Saving" : translatedType.equals(context.getString(R.string.salary)) ?
                "Salary" : translatedType.equals(context.getString(R.string.subtract_money_bills)) ?
                "Bills" : translatedType.equals(context.getString(R.string.subtract_money_car)) ?
                "Car" : translatedType.equals(context.getString(R.string.subtract_money_clothes)) ?
                "Clothes" : translatedType.equals(context.getString(R.string.subtract_money_communications)) ?
                "Communications" : translatedType.equals(context.getString(R.string.subtract_money_eating_out)) ?
                "Eating out" : translatedType.equals(context.getString(R.string.subtract_money_entertainment)) ?
                "Entertainment" : translatedType.equals(context.getString(R.string.subtract_money_food)) ?
                "Food" : translatedType.equals(context.getString(R.string.subtract_money_gifts)) ?
                "Gifts" : translatedType.equals(context.getString(R.string.subtract_money_health)) ?
                "Health" : translatedType.equals(context.getString(R.string.subtract_money_house)) ?
                "House" : translatedType.equals(context.getString(R.string.subtract_money_pets)) ?
                "Pets" : translatedType.equals(context.getString(R.string.subtract_money_sports)) ?
                "Sports" : translatedType.equals(context.getString(R.string.subtract_money_taxi)) ?
                "Taxi" : translatedType.equals(context.getString(R.string.subtract_money_toiletry)) ?
                "Toiletry" : translatedType.equals(context.getString(R.string.subtract_money_transport)) ?
                "Transport" : null;
    }
}
