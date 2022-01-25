package com.example.economy_manager.feature.editaccount;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Countries;
import com.example.economy_manager.utility.DatePickerFragment;
import com.example.economy_manager.utility.Genders;
import com.example.economy_manager.utility.MyCustomMethods;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class EditAccountViewModel extends AndroidViewModel {
    private final ObservableField<String> firstName = new ObservableField<>("");
    private final ObservableField<String> lastName = new ObservableField<>("");
    private final ObservableField<String> phoneNumber = new ObservableField<>("");
    private final ObservableField<String> website = new ObservableField<>("");
    private final ObservableField<String> country = new ObservableField<>("");
    private final ObservableField<String> gender = new ObservableField<>("");
    private LocalDate birthDate = LocalDate.now();
    private final ObservableField<String> careerTitle = new ObservableField<>("");

    private UserDetails userDetails;
    private final ArrayList<String> countriesList = new ArrayList<>();
    private final ArrayList<String> gendersList = new ArrayList<>();
    private LocalDate transactionDate = LocalDate.now();

    public EditAccountViewModel(final @NonNull Application application,
                                final @NonNull Activity activity,
                                final UserDetails userDetails) {
        super(application);

        this.userDetails = userDetails;

        Countries.populateList(application, countriesList);
        Genders.populateList(activity, gendersList);
        sortListAscending(countriesList);
        sortListAscending(gendersList);
    }

    public ObservableField<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public ObservableField<String> getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public ObservableField<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public ObservableField<String> getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website.set(website);
    }

    public ObservableField<String> getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    public ObservableField<String> getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public ObservableField<String> getCareerTitle() {
        return careerTitle;
    }

    public void setCareerTitle(String careerTitle) {
        this.careerTitle.set(careerTitle);
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public ArrayList<String> getCountriesList() {
        return countriesList;
    }

    public ArrayList<String> getGendersList() {
        return gendersList;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    private boolean personalInformationIsValid(String enteredFirstName,
                                               String enteredLastName,
                                               String enteredPhoneNumber,
                                               String enteredWebsite,
                                               String enteredCountry,
                                               String enteredGender,
                                               String enteredCareerTitle) {
        return MyCustomMethods.nameIsValid(enteredFirstName) == 1 &&
                MyCustomMethods.nameIsValid(enteredLastName) == 1 &&
                MyCustomMethods.stringIsNotEmpty(enteredCountry) &&
                MyCustomMethods.stringIsNotEmpty(enteredGender) &&
                MyCustomMethods.stringIsNotEmpty(enteredCareerTitle);
    }

    public HashMap<String, Object> getPersonalInformation(final @NonNull Activity activity) {
        final HashMap<String, Object> personalInformationMap = new HashMap<>();

        final Object photoURL = getUserDetails() != null ?
                getUserDetails().getPersonalInformation().getPhotoURL() : "";

        final Object firstName = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getFirstName().trim().isEmpty() ?
                activity.getResources().getString(R.string.edit_account_first_name).trim() :
                getUserDetails().getPersonalInformation().getFirstName().trim();

        final Object lastName = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getLastName().trim().isEmpty() ?
                activity.getResources().getString(R.string.edit_account_last_name).trim() :
                getUserDetails().getPersonalInformation().getLastName().trim();

        final Object phoneNumber = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getPhoneNumber().trim().isEmpty() ?
                activity.getResources().getString(R.string.edit_account_phone_number).trim() :
                getUserDetails().getPersonalInformation().getPhoneNumber().trim();

        final Object website = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getWebsite().trim().isEmpty() ?
                activity.getResources().getString(R.string.edit_account_website).trim() :
                getUserDetails().getPersonalInformation().getWebsite().trim();

        final Object countrySpinnerSelection = Countries.getCountryPositionInList(getApplication(), countriesList,
                getUserDetails() != null ? getUserDetails().getPersonalInformation().getCountry().trim() : "");

        final Object genderSpinnerSelection = Genders.getPositionInGenderList(activity, gendersList,
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
                activity.getResources().getString(R.string.edit_account_career_title).trim() :
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
        new DatePickerFragment(getTransactionDate()).show(fragmentManager, "date_picker");
    }

    private void sortListAscending(@NonNull final ArrayList<String> list) {
        if (!list.isEmpty()) {
            Collections.sort(list);
        }
    }

    public void updateProfileHandler(@NonNull Activity activity) {
        final String enteredFirstName = firstName.get();
        final String enteredLastName = lastName.get();
        final String enteredPhoneNumber = phoneNumber.get();
        final String enteredWebsite = website.get();
        final String enteredCountry = country.get();
        final String enteredGender = gender.get();
        final String enteredCareerTitle = careerTitle.get();

        if (enteredFirstName == null ||
                enteredLastName == null ||
                enteredPhoneNumber == null ||
                enteredWebsite == null ||
                enteredCountry == null ||
                enteredGender == null ||
                enteredCareerTitle == null) {
            return;
        }

        final String enteredCountryInEnglish = Countries.getCountryNameInEnglish(activity, enteredCountry);
        final String enteredGenderInEnglish = Genders.getGenderInEnglish(activity, enteredGender);

        MyCustomMethods.showShortMessage(activity, enteredFirstName + " " +
                enteredCountryInEnglish + " " +
                enteredGenderInEnglish);

        if (enteredFirstName.trim().isEmpty() ||
                enteredLastName.trim().isEmpty() ||
                enteredPhoneNumber.trim().isEmpty() ||
                enteredWebsite.trim().isEmpty() ||
                enteredCountry.trim().isEmpty() ||
                enteredGender.trim().isEmpty() ||
                enteredCareerTitle.trim().isEmpty()
        ) {
            MyCustomMethods.showShortMessage(activity, "empty");
            return;
        }
//        else if () {
//
//        }

        MyCustomMethods.showShortMessage(activity, "ez");
    }
}