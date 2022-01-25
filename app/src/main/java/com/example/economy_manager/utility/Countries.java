package com.example.economy_manager.utility;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.Nullable;

import com.example.economy_manager.R;

import java.util.ArrayList;
import java.util.Collections;

public final class Countries {
    private Countries() {

    }

    @Nullable
    public static String getCountryNameInEnglish(final Activity activity,
                                                 final String countryName) {
        return countryName.equals(activity.getResources().getString(R.string.edit_account_country_albania)) ?
                "Albania" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_andorra)) ?
                "Andorra" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_armenia)) ?
                "Armenia" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_austria)) ?
                "Austria" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_azerbaijan)) ?
                "Azerbaijan" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_belarus)) ?
                "Belarus" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_belgium)) ?
                "Belgium" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_bosnia_and_herzegovina)) ?
                "Bosnia and Herzegovina" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_bulgaria)) ?
                "Bulgaria" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_croatia)) ?
                "Croatia" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_cyprus)) ?
                "Cyprus" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_czech_republic)) ?
                "Czech Republic" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_denmark)) ?
                "Denmark" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_estonia)) ?
                "Estonia" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_finland)) ?
                "Finland" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_france)) ?
                "France" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_georgia)) ?
                "Georgia" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_germany)) ?
                "Germany" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_greece)) ?
                "Greece" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_hungary)) ?
                "Hungary" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_iceland)) ?
                "Iceland" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_ireland)) ?
                "Ireland" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_italy)) ?
                "Italy" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_kazakhstan)) ?
                "Kazakhstan" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_latvia)) ?
                "Latvia" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_liechtenstein)) ?
                "Liechtenstein" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_lithuania)) ?
                "Lithuania" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_luxembourg)) ?
                "Luxembourg" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_malta)) ?
                "Malta" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_moldova)) ?
                "Moldova" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_monaco)) ?
                "Monaco" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_montenegro)) ?
                "Montenegro" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_netherlands)) ?
                "Netherlands" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_north_macedonia)) ?
                "North Macedonia" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_norway)) ?
                "Norway" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_poland)) ?
                "Poland" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_portugal)) ?
                "Portugal" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_romania)) ?
                "Romania" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_russia)) ?
                "Russia" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_san_marino)) ?
                "San Marino" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_serbia)) ?
                "Serbia" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_slovakia)) ?
                "Slovakia" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_slovenia)) ?
                "Slovenia" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_spain)) ?
                "Spain" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_sweden)) ?
                "Sweden" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_switzerland)) ?
                "Switzerland" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_turkey)) ?
                "Turkey" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_ukraine)) ?
                "Ukraine" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_uk)) ?
                "United Kingdom" : countryName.equals(activity.getResources().getString(R.string.edit_account_country_vatican_city)) ?
                "Vatican City" : null;
    }

    public static int getCountryPositionInList(final Application app,
                                               final ArrayList<String> countriesList,
                                               final String countryName) {
        final String translatedCountryName = countryName.equals("Albania") ?
                app.getResources().getString(R.string.edit_account_country_albania) : countryName.equals("Andorra") ?
                app.getResources().getString(R.string.edit_account_country_andorra) : countryName.equals("Armenia") ?
                app.getResources().getString(R.string.edit_account_country_armenia) : countryName.equals("Austria") ?
                app.getResources().getString(R.string.edit_account_country_austria) : countryName.equals("Azerbaijan") ?
                app.getResources().getString(R.string.edit_account_country_azerbaijan) : countryName.equals("Belarus") ?
                app.getResources().getString(R.string.edit_account_country_belarus) : countryName.equals("Belgium") ?
                app.getResources().getString(R.string.edit_account_country_belgium) : countryName.equals("Bosnia and Herzegovina") ?
                app.getResources().getString(R.string.edit_account_country_bosnia_and_herzegovina) : countryName.equals("Bulgaria") ?
                app.getResources().getString(R.string.edit_account_country_bulgaria) : countryName.equals("Croatia") ?
                app.getResources().getString(R.string.edit_account_country_croatia) : countryName.equals("Cyprus") ?
                app.getResources().getString(R.string.edit_account_country_cyprus) : countryName.equals("Czech Republic") ?
                app.getResources().getString(R.string.edit_account_country_czech_republic) : countryName.equals("Denmark") ?
                app.getResources().getString(R.string.edit_account_country_denmark) : countryName.equals("Estonia") ?
                app.getResources().getString(R.string.edit_account_country_estonia) : countryName.equals("Finland") ?
                app.getResources().getString(R.string.edit_account_country_finland) : countryName.equals("France") ?
                app.getResources().getString(R.string.edit_account_country_france) : countryName.equals("Georgia") ?
                app.getResources().getString(R.string.edit_account_country_georgia) : countryName.equals("Germany") ?
                app.getResources().getString(R.string.edit_account_country_germany) : countryName.equals("Greece") ?
                app.getResources().getString(R.string.edit_account_country_greece) : countryName.equals("Hungary") ?
                app.getResources().getString(R.string.edit_account_country_hungary) : countryName.equals("Iceland") ?
                app.getResources().getString(R.string.edit_account_country_iceland) : countryName.equals("Ireland") ?
                app.getResources().getString(R.string.edit_account_country_ireland) : countryName.equals("Italy") ?
                app.getResources().getString(R.string.edit_account_country_italy) : countryName.equals("Kazakhstan") ?
                app.getResources().getString(R.string.edit_account_country_kazakhstan) : countryName.equals("Latvia") ?
                app.getResources().getString(R.string.edit_account_country_latvia) : countryName.equals("Liechtenstein") ?
                app.getResources().getString(R.string.edit_account_country_liechtenstein) : countryName.equals("Lithuania") ?
                app.getResources().getString(R.string.edit_account_country_lithuania) : countryName.equals("Luxembourg") ?
                app.getResources().getString(R.string.edit_account_country_luxembourg) : countryName.equals("Malta") ?
                app.getResources().getString(R.string.edit_account_country_malta) : countryName.equals("Moldova") ?
                app.getResources().getString(R.string.edit_account_country_moldova) : countryName.equals("Monaco") ?
                app.getResources().getString(R.string.edit_account_country_monaco) : countryName.equals("Montenegro") ?
                app.getResources().getString(R.string.edit_account_country_montenegro) : countryName.equals("Netherlands") ?
                app.getResources().getString(R.string.edit_account_country_netherlands) : countryName.equals("North Macedonia") ?
                app.getResources().getString(R.string.edit_account_country_north_macedonia) : countryName.equals("Norway") ?
                app.getResources().getString(R.string.edit_account_country_norway) : countryName.equals("Poland") ?
                app.getResources().getString(R.string.edit_account_country_poland) : countryName.equals("Portugal") ?
                app.getResources().getString(R.string.edit_account_country_portugal) : countryName.equals("Romania") ?
                app.getResources().getString(R.string.edit_account_country_romania) : countryName.equals("Russia") ?
                app.getResources().getString(R.string.edit_account_country_russia) : countryName.equals("San Marino") ?
                app.getResources().getString(R.string.edit_account_country_san_marino) : countryName.equals("Serbia") ?
                app.getResources().getString(R.string.edit_account_country_serbia) : countryName.equals("Slovakia") ?
                app.getResources().getString(R.string.edit_account_country_slovakia) : countryName.equals("Slovenia") ?
                app.getResources().getString(R.string.edit_account_country_slovenia) : countryName.equals("Spain") ?
                app.getResources().getString(R.string.edit_account_country_spain) : countryName.equals("Sweden") ?
                app.getResources().getString(R.string.edit_account_country_sweden) : countryName.equals("Switzerland") ?
                app.getResources().getString(R.string.edit_account_country_switzerland) : countryName.equals("Turkey") ?
                app.getResources().getString(R.string.edit_account_country_turkey) : countryName.equals("Ukraine") ?
                app.getResources().getString(R.string.edit_account_country_ukraine) : countryName.equals("United Kingdom") ?
                app.getResources().getString(R.string.edit_account_country_uk) : countryName.equals("Vatican City") ?
                app.getResources().getString(R.string.edit_account_country_vatican_city) : " ";

        return Collections.binarySearch(countriesList, translatedCountryName);
    }

    public static void populateList(final Application app,
                                    final ArrayList<String> countriesList) {
        countriesList.add(app.getResources().getString(R.string.edit_account_country_albania));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_andorra));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_armenia));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_austria));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_azerbaijan));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_belarus));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_belgium));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_bosnia_and_herzegovina));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_bulgaria));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_croatia));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_cyprus));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_czech_republic));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_denmark));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_estonia));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_finland));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_france));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_georgia));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_germany));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_greece));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_hungary));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_iceland));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_ireland));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_italy));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_kazakhstan));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_latvia));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_liechtenstein));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_lithuania));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_luxembourg));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_malta));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_moldova));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_monaco));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_montenegro));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_netherlands));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_north_macedonia));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_norway));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_poland));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_portugal));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_romania));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_russia));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_san_marino));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_serbia));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_slovakia));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_slovenia));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_spain));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_sweden));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_switzerland));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_turkey));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_ukraine));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_uk));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_vatican_city));
        countriesList.add(app.getResources().getString(R.string.edit_account_country_select_country));
    }
}