package com.example.economy_manager.main_part.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.model.UserDetails;

import java.util.ArrayList;
import java.util.Collections;

public class EditAccountViewModel extends AndroidViewModel {
    private UserDetails userDetails;
    private ArrayList<String> countryList = new ArrayList<>();
    private ArrayList<String> genderList = new ArrayList<>();

    public EditAccountViewModel(@NonNull Application application, UserDetails userDetails) {
        super(application);

        this.userDetails = userDetails;

        addCountriesToList(application);
        addGendersToList(application);
        sortListAscending(countryList);
        sortListAscending(genderList);
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public ArrayList<String> getCountryList() {
        return countryList;
    }

    public void setCountryList(ArrayList<String> countryList) {
        this.countryList = countryList;
    }

    public ArrayList<String> getGenderList() {
        return genderList;
    }

    public void setGenderList(ArrayList<String> genderList) {
        this.genderList = genderList;
    }

    private void addCountriesToList(Application app) {
        countryList.add(app.getResources().getString(R.string.edit_account_country_albania));
        countryList.add(app.getResources().getString(R.string.edit_account_country_andorra));
        countryList.add(app.getResources().getString(R.string.edit_account_country_armenia));
        countryList.add(app.getResources().getString(R.string.edit_account_country_austria));
        countryList.add(app.getResources().getString(R.string.edit_account_country_azerbaijan));
        countryList.add(app.getResources().getString(R.string.edit_account_country_belarus));
        countryList.add(app.getResources().getString(R.string.edit_account_country_belgium));
        countryList.add(app.getResources().getString(R.string.edit_account_country_bosnia_and_herzegovina));
        countryList.add(app.getResources().getString(R.string.edit_account_country_bulgaria));
        countryList.add(app.getResources().getString(R.string.edit_account_country_croatia));
        countryList.add(app.getResources().getString(R.string.edit_account_country_cyprus));
        countryList.add(app.getResources().getString(R.string.edit_account_country_czech_republic));
        countryList.add(app.getResources().getString(R.string.edit_account_country_denmark));
        countryList.add(app.getResources().getString(R.string.edit_account_country_estonia));
        countryList.add(app.getResources().getString(R.string.edit_account_country_finland));
        countryList.add(app.getResources().getString(R.string.edit_account_country_france));
        countryList.add(app.getResources().getString(R.string.edit_account_country_georgia));
        countryList.add(app.getResources().getString(R.string.edit_account_country_germany));
        countryList.add(app.getResources().getString(R.string.edit_account_country_greece));
        countryList.add(app.getResources().getString(R.string.edit_account_country_hungary));
        countryList.add(app.getResources().getString(R.string.edit_account_country_iceland));
        countryList.add(app.getResources().getString(R.string.edit_account_country_ireland));
        countryList.add(app.getResources().getString(R.string.edit_account_country_italy));
        countryList.add(app.getResources().getString(R.string.edit_account_country_kazakhstan));
        countryList.add(app.getResources().getString(R.string.edit_account_country_latvia));
        countryList.add(app.getResources().getString(R.string.edit_account_country_liechtenstein));
        countryList.add(app.getResources().getString(R.string.edit_account_country_lithuania));
        countryList.add(app.getResources().getString(R.string.edit_account_country_luxembourg));
        countryList.add(app.getResources().getString(R.string.edit_account_country_malta));
        countryList.add(app.getResources().getString(R.string.edit_account_country_moldova));
        countryList.add(app.getResources().getString(R.string.edit_account_country_monaco));
        countryList.add(app.getResources().getString(R.string.edit_account_country_montenegro));
        countryList.add(app.getResources().getString(R.string.edit_account_country_netherlands));
        countryList.add(app.getResources().getString(R.string.edit_account_country_north_macedonia));
        countryList.add(app.getResources().getString(R.string.edit_account_country_norway));
        countryList.add(app.getResources().getString(R.string.edit_account_country_poland));
        countryList.add(app.getResources().getString(R.string.edit_account_country_portugal));
        countryList.add(app.getResources().getString(R.string.edit_account_country_romania));
        countryList.add(app.getResources().getString(R.string.edit_account_country_russia));
        countryList.add(app.getResources().getString(R.string.edit_account_country_san_marino));
        countryList.add(app.getResources().getString(R.string.edit_account_country_serbia));
        countryList.add(app.getResources().getString(R.string.edit_account_country_slovakia));
        countryList.add(app.getResources().getString(R.string.edit_account_country_slovenia));
        countryList.add(app.getResources().getString(R.string.edit_account_country_spain));
        countryList.add(app.getResources().getString(R.string.edit_account_country_sweden));
        countryList.add(app.getResources().getString(R.string.edit_account_country_switzerland));
        countryList.add(app.getResources().getString(R.string.edit_account_country_turkey));
        countryList.add(app.getResources().getString(R.string.edit_account_country_ukraine));
        countryList.add(app.getResources().getString(R.string.edit_account_country_uk));
        countryList.add(app.getResources().getString(R.string.edit_account_country_vatican_city));
        countryList.add(app.getResources().getString(R.string.edit_account_country_select_country));
    }

    private void addGendersToList(Application app) {
        genderList.add(app.getResources().getString(R.string.edit_account_gender_female));
        genderList.add(app.getResources().getString(R.string.edit_account_gender_male));
        genderList.add(app.getResources().getString(R.string.edit_account_gender_other));
        genderList.add(app.getResources().getString(R.string.edit_account_gender_select));

        Collections.sort(genderList);
    }

    private void sortListAscending(ArrayList<String> list) {
        if (!list.isEmpty()) {
            Collections.sort(list);
        }
    }

    public int getPositionInCountryList(Application app, String countryName) {
        String translatedCountryName;

        switch (countryName) {
            case "Albania":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_albania);
                break;
            case "Andorra":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_andorra);
                break;
            case "Armenia":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_armenia);
                break;
            case "Austria":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_austria);
                break;
            case "Azerbaijan":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_azerbaijan);
                break;
            case "Belarus":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_belarus);
                break;
            case "Belgium":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_belgium);
                break;
            case "Bosnia and Herzegovina":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_bosnia_and_herzegovina);
                break;
            case "Bulgaria":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_bulgaria);
                break;
            case "Croatia":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_croatia);
                break;
            case "Cyprus":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_cyprus);
                break;
            case "Czech Republic":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_czech_republic);
                break;
            case "Denmark":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_denmark);
                break;
            case "Estonia":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_estonia);
                break;
            case "Finland":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_finland);
                break;
            case "France":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_france);
                break;
            case "Georgia":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_georgia);
                break;
            case "Germany":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_germany);
                break;
            case "Greece":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_greece);
                break;
            case "Hungary":
                translatedCountryName = app.getResources()

                        .getString(R.string.edit_account_country_hungary);
                break;
            case "Iceland":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_iceland);
                break;
            case "Ireland":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_ireland);
                break;
            case "Italy":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_italy);
                break;
            case "Kazakhstan":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_kazakhstan);
                break;
            case "Latvia":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_latvia);
                break;
            case "Liechtenstein":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_liechtenstein);
                break;
            case "Lithuania":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_lithuania);
                break;
            case "Luxembourg":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_luxembourg);
                break;
            case "Malta":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_malta);
                break;
            case "Moldova":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_moldova);
                break;
            case "Monaco":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_monaco);
                break;
            case "Montenegro":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_montenegro);
                break;
            case "Netherlands":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_netherlands);
                break;
            case "North Macedonia":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_north_macedonia);
                break;
            case "Norway":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_norway);
                break;
            case "Poland":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_poland);
                break;
            case "Portugal":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_portugal);
                break;
            case "Romania":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_romania);
                break;
            case "Russia":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_russia);
                break;
            case "San Marino":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_san_marino);
                break;
            case "Serbia":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_serbia);
                break;
            case "Slovakia":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_slovakia);
                break;
            case "Slovenia":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_slovenia);
                break;
            case "Spain":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_spain);
                break;
            case "Sweden":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_sweden);
                break;
            case "Switzerland":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_switzerland);
                break;
            case "Turkey":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_turkey);
                break;
            case "Ukraine":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_ukraine);
                break;
            case "United Kingdom":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_uk);
                break;
            case "Vatican City":
                translatedCountryName = app.getResources()
                        .getString(R.string.edit_account_country_vatican_city);
                break;
            default:
                translatedCountryName = " ";
                break;
        }

        return Collections.binarySearch(getCountryList(), translatedCountryName);
    }

    public String getCountryNameInEnglish(Application app, int positionInCountryList) {
        String countryName = getCountryList().get(positionInCountryList);

        if (countryName.equals(app.getResources().getString(R.string.edit_account_country_albania)))
            return "Albania";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_andorra)))
            return "Andorra";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_armenia)))
            return "Armenia";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_austria)))
            return "Austria";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_azerbaijan)))
            return "Azerbaijan";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_belarus)))
            return "Belarus";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_belgium)))
            return "Belgium";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_bosnia_and_herzegovina)))
            return "Bosnia and Herzegovina";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_bulgaria)))
            return "Bulgaria";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_croatia)))
            return "Croatia";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_cyprus)))
            return "Cyprus";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_czech_republic)))
            return "Czech Republic";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_denmark)))
            return "Denmark";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_estonia)))
            return "Estonia";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_finland)))
            return "Finland";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_france)))
            return "France";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_georgia)))
            return "Georgia";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_germany)))
            return "Germany";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_greece)))
            return "Greece";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_hungary)))
            return "Hungary";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_iceland)))
            return "Iceland";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_ireland)))
            return "Ireland";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_italy)))
            return "Italy";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_kazakhstan)))
            return "Kazakhstan";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_latvia)))
            return "Latvia";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_liechtenstein)))
            return "Liechtenstein";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_lithuania)))
            return "Lithuania";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_luxembourg)))
            return "Luxembourg";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_malta)))
            return "Malta";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_moldova)))
            return "Moldova";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_monaco)))
            return "Monaco";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_montenegro)))
            return "Montenegro";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_netherlands)))
            return "Netherlands";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_north_macedonia)))
            return "North Macedonia";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_norway)))
            return "Norway";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_poland)))
            return "Poland";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_portugal)))
            return "Portugal";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_romania)))
            return "Romania";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_russia)))
            return "Russia";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_san_marino)))
            return "San Marino";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_serbia)))
            return "Serbia";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_slovakia)))
            return "Slovakia";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_slovenia)))
            return "Slovenia";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_spain)))
            return "Spain";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_sweden)))
            return "Sweden";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_switzerland)))
            return "Switzerland";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_turkey)))
            return "Turkey";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_ukraine)))
            return "Ukraine";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_uk)))
            return "United Kingdom";
        else if (countryName.equals(app.getResources().getString(R.string.edit_account_country_vatican_city)))
            return "Vatican City";
        else return null;
    }

    public int getPositionInGenderList(Application app, String gender) {
        String translatedGender = gender.equals("Female") ?
                app.getResources().getString(R.string.edit_account_gender_female) :
                gender.equals("Male") ?
                        app.getResources().getString(R.string.edit_account_gender_male) :
                        gender.equals("Other") ?
                                app.getResources().getString(R.string.edit_account_gender_other) : " ";

        return Collections.binarySearch(getGenderList(), translatedGender);
    }

    public String getGenderInEnglish(Application app, int positionInGenderList) {
        String gender = getGenderList().get(positionInGenderList);

        return gender.equals(app.getResources().getString(R.string.edit_account_gender_female)) ?
                "Female" : gender.equals(app.getResources().getString(R.string.edit_account_gender_male)) ?
                "Male" : gender.equals(app.getResources().getString(R.string.edit_account_gender_other)) ?
                "Other" : null;
    }
}