package com.example.EconomyManager;

import android.content.Context;

public class Types
{
    public static String getTranslatedType(Context context, String typeInEnglish)
    {
        switch(typeInEnglish)
        {
            case "Deposits":
                return context.getString(R.string.add_money_deposits);
            case "IndependentSources":
                return context.getString(R.string.add_money_independent_sources);
            case "Salary":
                return context.getString(R.string.salary);
            case "Saving":
                return context.getString(R.string.saving);
            case "Bills":
                return context.getString(R.string.subtract_money_bills);
            case "Car":
                return context.getString(R.string.subtract_money_car);
            case "Clothes":
                return context.getString(R.string.subtract_money_clothes);
            case "Communications":
                return context.getString(R.string.subtract_money_communications);
            case "EatingOut":
                return context.getString(R.string.subtract_money_eating_out);
            case "Entertainment":
                return context.getString(R.string.subtract_money_entertainment);
            case "Food":
                return context.getString(R.string.subtract_money_food);
            case "Gifts":
                return context.getString(R.string.subtract_money_gifts);
            case "Health":
                return context.getString(R.string.subtract_money_health);
            case "House":
                return context.getString(R.string.subtract_money_house);
            case "Pets":
                return context.getString(R.string.subtract_money_pets);
            case "Sports":
                return context.getString(R.string.subtract_money_sports);
            case "Taxi":
                return context.getString(R.string.subtract_money_taxi);
            case "Toiletry":
                return context.getString(R.string.subtract_money_toiletry);
            case "Transport":
                return context.getString(R.string.subtract_money_transport);
            default:
                return "";
        }
    }

    public static String getTypeInEnglish(Context context, String translatedType)
    {
        if(translatedType.equals(context.getString(R.string.add_money_deposits)))
            return "Deposits";
        else if(translatedType.equals(context.getString(R.string.add_money_independent_sources)))
            return "IndependentSources";
        else if(translatedType.equals(context.getString(R.string.saving)))
            return "Saving";
        else if(translatedType.equals(context.getString(R.string.salary)))
            return "Salary";
        else if(translatedType.equals(context.getString(R.string.subtract_money_bills)))
            return "Bills";
        else if(translatedType.equals(context.getString(R.string.subtract_money_car)))
            return "Car";
        else if(translatedType.equals(context.getString(R.string.subtract_money_clothes)))
            return "Clothes";
        else if(translatedType.equals(context.getString(R.string.subtract_money_communications)))
            return "Communications";
        else if(translatedType.equals(context.getString(R.string.subtract_money_eating_out)))
            return "EatingOut";
        else if(translatedType.equals(context.getString(R.string.subtract_money_entertainment)))
            return "Entertainment";
        else if(translatedType.equals(context.getString(R.string.subtract_money_food)))
            return "Food";
        else if(translatedType.equals(context.getString(R.string.subtract_money_gifts)))
            return "Gifts";
        else if(translatedType.equals(context.getString(R.string.subtract_money_health)))
            return "Health";
        else if(translatedType.equals(context.getString(R.string.subtract_money_house)))
            return "House";
        else if(translatedType.equals(context.getString(R.string.subtract_money_pets)))
            return "Pets";
        else if(translatedType.equals(context.getString(R.string.subtract_money_sports)))
            return "Sports";
        else if(translatedType.equals(context.getString(R.string.subtract_money_taxi)))
            return "Taxi";
        else if(translatedType.equals(context.getString(R.string.subtract_money_toiletry)))
            return "Toiletry";
        else if(translatedType.equals(context.getString(R.string.subtract_money_transport)))
            return "Transport";
        else return null;
    }
}
