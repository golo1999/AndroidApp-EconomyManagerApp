package com.example.economy_manager.feature.editprofile;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.EditProfileActivityBinding;
import com.example.economy_manager.model.PersonalInformation;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Countries;
import com.example.economy_manager.utility.Genders;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class EditProfileViewModel extends AndroidViewModel {

    private final ObservableField<String> firstName = new ObservableField<>("");
    private final ObservableField<String> lastName = new ObservableField<>("");
    private final ObservableField<String> phoneNumber = new ObservableField<>("");
    private final ObservableField<String> website = new ObservableField<>("");
    private final ObservableField<String> country = new ObservableField<>("");
    private final ObservableField<String> gender = new ObservableField<>("");
    private final ObservableField<String> careerTitle = new ObservableField<>("");

    private final EditProfileActivityBinding binding;
    private UserDetails userDetails;
    private final ArrayList<String> countriesList = new ArrayList<>();
    private final ArrayList<String> gendersList = new ArrayList<>();
    private LocalDate transactionDate = LocalDate.now();

    public EditProfileViewModel(final @NonNull Application application,
                                final @NonNull Activity activity,
                                final UserDetails userDetails,
                                final EditProfileActivityBinding binding) {
        super(application);

        this.userDetails = userDetails;
        this.binding = binding;

        Countries.populateList(application, countriesList);
        Genders.populateList(activity, gendersList);
        MyCustomMethods.sortListAscending(countriesList);
        MyCustomMethods.sortListAscending(gendersList);
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

    public void setCountry(String country) {
        this.country.set(country);
    }

    public void setGender(String gender) {
        this.gender.set(gender);
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

    public HashMap<String, Object> getPersonalInformation(final @NonNull Activity activity) {
        final HashMap<String, Object> personalInformationMap = new HashMap<>();

        final Object photoURL = getUserDetails() != null ?
                getUserDetails().getPersonalInformation().getPhotoURL() : "";

        final Object firstName = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getFirstName().trim().isEmpty() ?
                "" : getUserDetails().getPersonalInformation().getFirstName().trim();

        final Object lastName = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getLastName().trim().isEmpty() ?
                "" : getUserDetails().getPersonalInformation().getLastName().trim();

        final Object phoneNumber = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getPhoneNumber().trim().isEmpty() ?
                "" : getUserDetails().getPersonalInformation().getPhoneNumber().trim();

        final Object website = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getWebsite().trim().isEmpty() ?
                "" : getUserDetails().getPersonalInformation().getWebsite().trim();

        final Object countrySpinnerSelection =
                Countries.getCountryPositionInList(getApplication(), countriesList,
                        getUserDetails() != null ?
                                MyCustomMethods.decodeText(getUserDetails().getPersonalInformation().getCountry())
                                        .trim() : "");

        final Object genderSpinnerSelection =
                Genders.getPositionInGenderList(activity, gendersList,
                        getUserDetails() != null ?
                                MyCustomMethods.decodeText(getUserDetails().getPersonalInformation().getGender())
                                        .trim() : "");

        final Object careerTitle = getUserDetails() == null ||
                getUserDetails().getPersonalInformation().getCareerTitle().trim().isEmpty() ?
                "" : getUserDetails().getPersonalInformation().getCareerTitle().trim();


        personalInformationMap.put("photoURL", photoURL);
        personalInformationMap.put("firstName", firstName);
        personalInformationMap.put("lastName", lastName);
        personalInformationMap.put("phoneNumber", phoneNumber);
        personalInformationMap.put("website", website);
        personalInformationMap.put("countrySpinnerSelection", countrySpinnerSelection);
        personalInformationMap.put("genderSpinnerSelection", genderSpinnerSelection);
        personalInformationMap.put("careerTitle", careerTitle);

        return personalInformationMap;
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

        // if there are invalid fields
        if (MyCustomMethods.nameIsValid(enteredFirstName) != 1 ||
                MyCustomMethods.nameIsValid(enteredLastName) != 1) {
            // first name input errors
            if (enteredFirstName.trim().isEmpty()) {
                final String error = activity.getResources().getString(R.string.should_not_be_empty,
                        activity.getResources().getString(R.string.the_first_name));

                binding.firstNameField.setError(error);
            } else if (MyCustomMethods.nameIsValid(enteredFirstName.trim()) == 0) {
                final String error =
                        activity.getResources().getString(R.string.should_have_at_least_characters,
                                activity.getResources().getString(R.string.the_first_name), 2);

                binding.firstNameField.setError(error);
            } else if (MyCustomMethods.nameIsValid(enteredFirstName.trim()) == -1) {
                final String error =
                        activity.getResources().getString(R.string.contains_invalid_characters,
                                activity.getResources().getString(R.string.the_first_name));

                binding.firstNameField.setError(error);
            }
            // last name input errors
            if (enteredLastName.trim().isEmpty()) {
                final String error = activity.getResources().getString(R.string.should_not_be_empty,
                        activity.getResources().getString(R.string.the_last_name));

                binding.lastNameField.setError(error);
            } else if (MyCustomMethods.nameIsValid(enteredLastName.trim()) == 0) {
                final String error =
                        activity.getResources().getString(R.string.should_have_at_least_characters,
                                activity.getResources().getString(R.string.the_last_name), 2);

                binding.lastNameField.setError(error);
            } else if (MyCustomMethods.nameIsValid(enteredLastName.trim()) == -1) {
                final String error =
                        activity.getResources().getString(R.string.contains_invalid_characters,
                                activity.getResources().getString(R.string.the_last_name));

                binding.lastNameField.setError(error);
            }

            return;
        }

        final String currentUserID = MyCustomVariables.getFirebaseAuth().getUid();

        if (currentUserID == null) {
            return;
        }

        final String enteredCountryInEnglish =
                Countries.getCountryNameInEnglish(activity, enteredCountry);
        final String enteredGenderInEnglish = Genders.getGenderInEnglish(activity, enteredGender);

        final String encodedEnteredFirstName = MyCustomMethods.encodeText(enteredFirstName);
        final String encodedEnteredLastName = MyCustomMethods.encodeText(enteredLastName);
        final String encodedEnteredPhoneNumber = MyCustomMethods.encodeText(enteredPhoneNumber);
        final String encodedEnteredWebsite = MyCustomMethods.encodeText(enteredWebsite);
        final String encodedEnteredCountryInEnglish = MyCustomMethods.encodeText(enteredCountryInEnglish);
        final String encodedEnteredGenderInEnglish = MyCustomMethods.encodeText(enteredGenderInEnglish);
        final String encodedEnteredCareerTitle = MyCustomMethods.encodeText(enteredCareerTitle);

        MyCustomVariables.getDatabaseReference()
                .child(MyCustomVariables.getFirebaseAuth().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists() || !snapshot.hasChild("personalInformation")) {
                            return;
                        }

                        final PersonalInformation personalInformation =
                                snapshot.child("personalInformation")
                                        .getValue(PersonalInformation.class);

                        if (personalInformation == null) {
                            return;
                        }

                        final boolean enteredFirstNameIsTheSame =
                                encodedEnteredFirstName.trim().equals(personalInformation.getFirstName());
                        final boolean enteredLastNameIsTheSame =
                                encodedEnteredLastName.trim().equals(personalInformation.getLastName());
                        final boolean enteredPhoneNumberIsTheSame =
                                encodedEnteredPhoneNumber.trim().equals(personalInformation.getPhoneNumber());
                        final boolean enteredWebsiteIsTheSame =
                                encodedEnteredWebsite.trim().equals(personalInformation.getWebsite());
                        final boolean enteredCountryIsTheSame =
                                encodedEnteredCountryInEnglish.trim().equals(personalInformation.getCountry());
                        final boolean enteredGenderIsTheSame =
                                encodedEnteredGenderInEnglish.trim().equals(personalInformation.getGender());
                        final boolean enteredCareerTitleIsTheSame =
                                encodedEnteredCareerTitle.trim().equals(personalInformation.getCareerTitle());

                        boolean isProfileModified = false;

                        if (!enteredFirstNameIsTheSame) {
                            personalInformation.setFirstName(encodedEnteredFirstName);
                            isProfileModified = true;
                        }

                        if (!enteredLastNameIsTheSame) {
                            personalInformation.setLastName(encodedEnteredLastName);
                            isProfileModified = true;
                        }

                        if (!enteredPhoneNumberIsTheSame) {
                            personalInformation.setPhoneNumber(encodedEnteredPhoneNumber);
                            isProfileModified = true;
                        }

                        if (!enteredWebsiteIsTheSame) {
                            personalInformation.setWebsite(encodedEnteredWebsite);
                            isProfileModified = true;
                        }

                        if (!enteredCountryIsTheSame) {
                            personalInformation.setCountry(encodedEnteredCountryInEnglish);
                            isProfileModified = true;
                        }

                        if (!enteredGenderIsTheSame) {
                            personalInformation.setGender(encodedEnteredGenderInEnglish);
                            isProfileModified = true;
                        }

                        if (!enteredCareerTitleIsTheSame) {
                            personalInformation.setCareerTitle(encodedEnteredCareerTitle);
                            isProfileModified = true;
                        }

                        if (isProfileModified) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .child("personalInformation")
                                    .setValue(personalInformation)
                                    .addOnCompleteListener((Task<Void> updateProfileTask) ->
                                            MyCustomMethods.showShortMessage(activity,
                                                    activity.getResources().getString(R.string
                                                            .profile_updated_successfully)))
                                    .addOnFailureListener((Exception updateProfileException) ->
                                            MyCustomMethods.showShortMessage(activity,
                                                    activity.getResources().getString(R.string
                                                            .please_try_again)));
                        } else {
                            MyCustomMethods.showShortMessage(activity,
                                    activity.getResources().getString(R.string
                                            .no_changes_have_been_made));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}