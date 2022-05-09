package com.example.economy_manager.utility;

import android.app.Activity;
import android.app.Application;

import com.example.economy_manager.R;

import java.util.ArrayList;
import java.util.Collections;

public final class Countries {

    private Countries() {

    }

    public static String getCountryNameInEnglish(final Activity activity,
                                                 final String countryName) {
        return countryName.equals(activity.getResources().getString(R.string.albania)) ?
                "Albania" : countryName.equals(activity.getResources()
                .getString(R.string.andorra)) ?
                "Andorra" : countryName.equals(activity.getResources()
                .getString(R.string.armenia)) ?
                "Armenia" : countryName.equals(activity.getResources()
                .getString(R.string.austria)) ?
                "Austria" : countryName.equals(activity.getResources()
                .getString(R.string.azerbaijan)) ?
                "Azerbaijan" : countryName.equals(activity.getResources()
                .getString(R.string.belarus)) ?
                "Belarus" : countryName.equals(activity.getResources()
                .getString(R.string.belgium)) ?
                "Belgium" : countryName.equals(activity.getResources()
                .getString(R.string.bosnia_and_herzegovina)) ?
                "Bosnia and Herzegovina" : countryName.equals(activity.getResources()
                .getString(R.string.bulgaria)) ?
                "Bulgaria" : countryName.equals(activity.getResources()
                .getString(R.string.croatia)) ?
                "Croatia" : countryName.equals(activity.getResources().getString(R.string.cyprus)) ?
                "Cyprus" : countryName.equals(activity.getResources()
                .getString(R.string.czech_republic)) ?
                "Czech Republic" : countryName.equals(activity.getResources()
                .getString(R.string.denmark)) ?
                "Denmark" : countryName.equals(activity.getResources()
                .getString(R.string.estonia)) ?
                "Estonia" : countryName.equals(activity.getResources()
                .getString(R.string.finland)) ?
                "Finland" : countryName.equals(activity.getResources().getString(R.string.france)) ?
                "France" : countryName.equals(activity.getResources().getString(R.string.georgia)) ?
                "Georgia" : countryName.equals(activity.getResources()
                .getString(R.string.germany)) ?
                "Germany" : countryName.equals(activity.getResources().getString(R.string.greece)) ?
                "Greece" : countryName.equals(activity.getResources().getString(R.string.hungary)) ?
                "Hungary" : countryName.equals(activity.getResources()
                .getString(R.string.iceland)) ?
                "Iceland" : countryName.equals(activity.getResources()
                .getString(R.string.ireland)) ?
                "Ireland" : countryName.equals(activity.getResources().getString(R.string.italy)) ?
                "Italy" : countryName.equals(activity.getResources()
                .getString(R.string.kazakhstan)) ?
                "Kazakhstan" : countryName.equals(activity.getResources()
                .getString(R.string.latvia)) ?
                "Latvia" : countryName.equals(activity.getResources()
                .getString(R.string.liechtenstein)) ?
                "Liechtenstein" : countryName.equals(activity.getResources()
                .getString(R.string.lithuania)) ?
                "Lithuania" : countryName.equals(activity.getResources()
                .getString(R.string.luxembourg)) ?
                "Luxembourg" : countryName.equals(activity.getResources()
                .getString(R.string.malta)) ?
                "Malta" : countryName.equals(activity.getResources().getString(R.string.moldova)) ?
                "Moldova" : countryName.equals(activity.getResources().getString(R.string.monaco)) ?
                "Monaco" : countryName.equals(activity.getResources()
                .getString(R.string.montenegro)) ?
                "Montenegro" : countryName.equals(activity.getResources()
                .getString(R.string.netherlands)) ?
                "Netherlands" : countryName.equals(activity.getResources()
                .getString(R.string.north_macedonia)) ?
                "North Macedonia" : countryName.equals(activity.getResources()
                .getString(R.string.norway)) ?
                "Norway" : countryName.equals(activity.getResources().getString(R.string.poland)) ?
                "Poland" : countryName.equals(activity.getResources()
                .getString(R.string.portugal)) ?
                "Portugal" : countryName.equals(activity.getResources()
                .getString(R.string.romania)) ?
                "Romania" : countryName.equals(activity.getResources().getString(R.string.russia)) ?
                "Russia" : countryName.equals(activity.getResources()
                .getString(R.string.san_marino)) ?
                "San Marino" : countryName.equals(activity.getResources()
                .getString(R.string.serbia)) ?
                "Serbia" : countryName.equals(activity.getResources()
                .getString(R.string.slovakia)) ?
                "Slovakia" : countryName.equals(activity.getResources()
                .getString(R.string.slovenia)) ?
                "Slovenia" : countryName.equals(activity.getResources().getString(R.string.spain)) ?
                "Spain" : countryName.equals(activity.getResources().getString(R.string.sweden)) ?
                "Sweden" : countryName.equals(activity.getResources()
                .getString(R.string.switzerland)) ?
                "Switzerland" : countryName.equals(activity.getResources()
                .getString(R.string.turkey)) ?
                "Turkey" : countryName.equals(activity.getResources().getString(R.string.ukraine)) ?
                "Ukraine" : countryName.equals(activity.getResources()
                .getString(R.string.united_kingdom)) ?
                "United Kingdom" : countryName.equals(activity.getResources()
                .getString(R.string.vatican_city)) ?
                "Vatican City" : "Unknown country";
    }

    public static int getCountryPositionInList(final Application app,
                                               final ArrayList<String> countriesList,
                                               final String countryName) {
        final String translatedCountryName = countryName.equals("Albania") ?
                app.getResources().getString(R.string.albania) : countryName.equals("Andorra") ?
                app.getResources().getString(R.string.andorra) : countryName.equals("Armenia") ?
                app.getResources().getString(R.string.armenia) : countryName.equals("Austria") ?
                app.getResources().getString(R.string.austria) : countryName.equals("Azerbaijan") ?
                app.getResources().getString(R.string.azerbaijan) : countryName.equals("Belarus") ?
                app.getResources().getString(R.string.belarus) : countryName.equals("Belgium") ?
                app.getResources().getString(R.string.belgium) : countryName.equals("Bosnia and Herzegovina") ?
                app.getResources().getString(R.string.bosnia_and_herzegovina) : countryName.equals("Bulgaria") ?
                app.getResources().getString(R.string.bulgaria) : countryName.equals("Croatia") ?
                app.getResources().getString(R.string.croatia) : countryName.equals("Cyprus") ?
                app.getResources().getString(R.string.cyprus) : countryName.equals("Czech Republic") ?
                app.getResources().getString(R.string.czech_republic) : countryName.equals("Denmark") ?
                app.getResources().getString(R.string.denmark) : countryName.equals("Estonia") ?
                app.getResources().getString(R.string.estonia) : countryName.equals("Finland") ?
                app.getResources().getString(R.string.finland) : countryName.equals("France") ?
                app.getResources().getString(R.string.france) : countryName.equals("Georgia") ?
                app.getResources().getString(R.string.georgia) : countryName.equals("Germany") ?
                app.getResources().getString(R.string.germany) : countryName.equals("Greece") ?
                app.getResources().getString(R.string.greece) : countryName.equals("Hungary") ?
                app.getResources().getString(R.string.hungary) : countryName.equals("Iceland") ?
                app.getResources().getString(R.string.iceland) : countryName.equals("Ireland") ?
                app.getResources().getString(R.string.ireland) : countryName.equals("Italy") ?
                app.getResources().getString(R.string.italy) : countryName.equals("Kazakhstan") ?
                app.getResources().getString(R.string.kazakhstan) : countryName.equals("Latvia") ?
                app.getResources().getString(R.string.latvia) : countryName.equals("Liechtenstein") ?
                app.getResources().getString(R.string.liechtenstein) : countryName.equals("Lithuania") ?
                app.getResources().getString(R.string.lithuania) : countryName.equals("Luxembourg") ?
                app.getResources().getString(R.string.luxembourg) : countryName.equals("Malta") ?
                app.getResources().getString(R.string.malta) : countryName.equals("Moldova") ?
                app.getResources().getString(R.string.moldova) : countryName.equals("Monaco") ?
                app.getResources().getString(R.string.monaco) : countryName.equals("Montenegro") ?
                app.getResources().getString(R.string.montenegro) : countryName.equals("Netherlands") ?
                app.getResources().getString(R.string.netherlands) : countryName.equals("North Macedonia") ?
                app.getResources().getString(R.string.north_macedonia) : countryName.equals("Norway") ?
                app.getResources().getString(R.string.norway) : countryName.equals("Poland") ?
                app.getResources().getString(R.string.poland) : countryName.equals("Portugal") ?
                app.getResources().getString(R.string.portugal) : countryName.equals("Romania") ?
                app.getResources().getString(R.string.romania) : countryName.equals("Russia") ?
                app.getResources().getString(R.string.russia) : countryName.equals("San Marino") ?
                app.getResources().getString(R.string.san_marino) : countryName.equals("Serbia") ?
                app.getResources().getString(R.string.serbia) : countryName.equals("Slovakia") ?
                app.getResources().getString(R.string.slovakia) : countryName.equals("Slovenia") ?
                app.getResources().getString(R.string.slovenia) : countryName.equals("Spain") ?
                app.getResources().getString(R.string.spain) : countryName.equals("Sweden") ?
                app.getResources().getString(R.string.sweden) : countryName.equals("Switzerland") ?
                app.getResources().getString(R.string.switzerland) : countryName.equals("Turkey") ?
                app.getResources().getString(R.string.turkey) : countryName.equals("Ukraine") ?
                app.getResources().getString(R.string.ukraine) : countryName.equals("United Kingdom") ?
                app.getResources().getString(R.string.united_kingdom) : countryName.equals("Vatican City") ?
                app.getResources().getString(R.string.vatican_city) :
                app.getResources().getString(R.string.unknown_country);

        return Collections.binarySearch(countriesList, translatedCountryName);
    }

    public static void populateList(final Application app,
                                    final ArrayList<String> countriesList) {
        countriesList.add(app.getResources().getString(R.string.albania));
        countriesList.add(app.getResources().getString(R.string.andorra));
        countriesList.add(app.getResources().getString(R.string.armenia));
        countriesList.add(app.getResources().getString(R.string.austria));
        countriesList.add(app.getResources().getString(R.string.azerbaijan));
        countriesList.add(app.getResources().getString(R.string.belarus));
        countriesList.add(app.getResources().getString(R.string.belgium));
        countriesList.add(app.getResources().getString(R.string.bosnia_and_herzegovina));
        countriesList.add(app.getResources().getString(R.string.bulgaria));
        countriesList.add(app.getResources().getString(R.string.croatia));
        countriesList.add(app.getResources().getString(R.string.cyprus));
        countriesList.add(app.getResources().getString(R.string.czech_republic));
        countriesList.add(app.getResources().getString(R.string.denmark));
        countriesList.add(app.getResources().getString(R.string.estonia));
        countriesList.add(app.getResources().getString(R.string.finland));
        countriesList.add(app.getResources().getString(R.string.france));
        countriesList.add(app.getResources().getString(R.string.georgia));
        countriesList.add(app.getResources().getString(R.string.germany));
        countriesList.add(app.getResources().getString(R.string.greece));
        countriesList.add(app.getResources().getString(R.string.hungary));
        countriesList.add(app.getResources().getString(R.string.iceland));
        countriesList.add(app.getResources().getString(R.string.ireland));
        countriesList.add(app.getResources().getString(R.string.italy));
        countriesList.add(app.getResources().getString(R.string.kazakhstan));
        countriesList.add(app.getResources().getString(R.string.latvia));
        countriesList.add(app.getResources().getString(R.string.liechtenstein));
        countriesList.add(app.getResources().getString(R.string.lithuania));
        countriesList.add(app.getResources().getString(R.string.luxembourg));
        countriesList.add(app.getResources().getString(R.string.malta));
        countriesList.add(app.getResources().getString(R.string.moldova));
        countriesList.add(app.getResources().getString(R.string.monaco));
        countriesList.add(app.getResources().getString(R.string.montenegro));
        countriesList.add(app.getResources().getString(R.string.netherlands));
        countriesList.add(app.getResources().getString(R.string.north_macedonia));
        countriesList.add(app.getResources().getString(R.string.norway));
        countriesList.add(app.getResources().getString(R.string.poland));
        countriesList.add(app.getResources().getString(R.string.portugal));
        countriesList.add(app.getResources().getString(R.string.romania));
        countriesList.add(app.getResources().getString(R.string.russia));
        countriesList.add(app.getResources().getString(R.string.san_marino));
        countriesList.add(app.getResources().getString(R.string.serbia));
        countriesList.add(app.getResources().getString(R.string.slovakia));
        countriesList.add(app.getResources().getString(R.string.slovenia));
        countriesList.add(app.getResources().getString(R.string.spain));
        countriesList.add(app.getResources().getString(R.string.sweden));
        countriesList.add(app.getResources().getString(R.string.switzerland));
        countriesList.add(app.getResources().getString(R.string.turkey));
        countriesList.add(app.getResources().getString(R.string.ukraine));
        countriesList.add(app.getResources().getString(R.string.united_kingdom));
        countriesList.add(app.getResources().getString(R.string.unknown_country));
        countriesList.add(app.getResources().getString(R.string.vatican_city));
    }
}