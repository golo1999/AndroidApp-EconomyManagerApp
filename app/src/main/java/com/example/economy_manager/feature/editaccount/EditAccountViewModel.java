package com.example.economy_manager.feature.editaccount;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Countries;
import com.example.economy_manager.utility.DatePickerFragment;
import com.example.economy_manager.utility.Genders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class EditAccountViewModel extends AndroidViewModel {
    private UserDetails userDetails;
    private final ArrayList<String> countryList = new ArrayList<>();
    private final ArrayList<String> genderList = new ArrayList<>();
    private LocalDate transactionDate = LocalDate.now();

    public EditAccountViewModel(final @NonNull Application application,
                                final UserDetails userDetails) {
        super(application);

        this.userDetails = userDetails;

        Countries.populateList(application, countryList);
        Genders.populateList(application, genderList);
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

    public ArrayList<String> getGenderList() {
        return genderList;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public HashMap<String, Object> getPersonalInformation(final @NonNull Context context) {
        final HashMap<String, Object> personalInformationMap = new HashMap<>();

        final Object photoURL = getUserDetails() != null ?
                getUserDetails().getPersonalInformation().getPhotoURL() : "";

        final Object firstName = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getFirstName().trim().isEmpty() ?
                context.getResources().getString(R.string.edit_account_first_name).trim() :
                getUserDetails().getPersonalInformation().getFirstName().trim();

        final Object lastName = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getLastName().trim().isEmpty() ?
                context.getResources().getString(R.string.edit_account_last_name).trim() :
                getUserDetails().getPersonalInformation().getLastName().trim();

        final Object phoneNumber = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getPhoneNumber().trim().isEmpty() ?
                context.getResources().getString(R.string.edit_account_phone_number).trim() :
                getUserDetails().getPersonalInformation().getPhoneNumber().trim();

        final Object website = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getWebsite().trim().isEmpty() ?
                context.getResources().getString(R.string.edit_account_website).trim() :
                getUserDetails().getPersonalInformation().getWebsite().trim();

        final Object countrySpinnerSelection = Countries.getCountryPositionInList(getApplication(), countryList,
                getUserDetails() != null ? getUserDetails().getPersonalInformation().getCountry().trim() : "");

        final Object genderSpinnerSelection = Genders.getPositionInGenderList(getApplication(), genderList,
                getUserDetails() != null ? getUserDetails().getPersonalInformation().getGender().trim() : "");

        final Object birthDate = getUserDetails() == null ||
                getUserDetails().getPersonalInformation() == null ||
                getUserDetails().getPersonalInformation().getBirthDate() == null ?
                LocalDate.now() :
                LocalDate.of(getUserDetails().getPersonalInformation().getBirthDate().getYear(),
                        getUserDetails().getPersonalInformation().getBirthDate().getMonth(),
                        getUserDetails().getPersonalInformation().getBirthDate().getDay());

        final Object careerTitle = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getCareerTitle().trim().isEmpty() ?
                context.getResources().getString(R.string.edit_account_career_title).trim() :
                getUserDetails().getPersonalInformation().getCareerTitle().trim();


        personalInformationMap.put("photoURL", photoURL);
        personalInformationMap.put("firstName", firstName);
        personalInformationMap.put("lastName", lastName);
        personalInformationMap.put("phoneNumber", phoneNumber);
        personalInformationMap.put("website", website);
        personalInformationMap.put("countrySpinnerSelection", countrySpinnerSelection);
        personalInformationMap.put("genderSpinnerSelection", genderSpinnerSelection);
        personalInformationMap.put("birthDate", birthDate);
        personalInformationMap.put("careerTitle", careerTitle);

        return personalInformationMap;
    }

    public void onBirthDateTextClicked(final FragmentManager fragmentManager) {
        final DialogFragment datePickerFragment = new DatePickerFragment(getTransactionDate());

        datePickerFragment.show(fragmentManager, "date_picker");
    }

    private void sortListAscending(final ArrayList<String> list) {
        if (!list.isEmpty()) {
            Collections.sort(list);
        }
    }

    public void updateProfileHandler() {

    }
}