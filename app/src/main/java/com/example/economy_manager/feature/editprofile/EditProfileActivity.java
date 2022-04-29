package com.example.economy_manager.feature.editprofile;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.EditProfileActivityBinding;
import com.example.economy_manager.feature.editphoto.EditPhotoActivity;
import com.example.economy_manager.model.PersonalInformation;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Countries;
import com.example.economy_manager.utility.Genders;
import com.example.economy_manager.utility.Languages;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;

public class EditProfileActivity
        extends AppCompatActivity {

    private EditProfileActivityBinding binding;
    private EditProfileViewModel viewModel;
    private SharedPreferences preferences;
    private UserDetails userDetails;
    private boolean isDarkThemeEnabled;
    private boolean personalInformationHasBeenModified;
    private static boolean isPhotoUrlModified;

    public static boolean isPhotoUrlModified() {
        return isPhotoUrlModified;
    }

    public static void setIsPhotoUrlModified(boolean isPhotoUrlModified) {
        EditProfileActivity.isPhotoUrlModified = isPhotoUrlModified;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyCustomMethods.finishActivityWithFadeTransition(this);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityVariables();
        setLayoutVariables();
        setTheme();
        setOnClickListeners();
        setSpinner("COUNTRY_SPINNER");
        setSpinner("GENDER_SPINNER");
        getAndSetPersonalInformation();
        setSpinnerOnSelectedItemListener(binding.countrySpinner, "COUNTRY_SPINNER");
        setSpinnerOnSelectedItemListener(binding.genderSpinner, "GENDER_SPINNER");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isPhotoUrlModified) {
            setIsPhotoUrlModified(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setProfilePhoto();
    }

    /**
     * Method for retrieving user's personal information from SharedPreferences
     * and filling the fields with the info
     */
    private void getAndSetPersonalInformation() {
        final HashMap<String, Object> retrievedPersonalInformation =
                viewModel.getPersonalInformation(this);
        final int accountPhotoBorderColor = binding.getIsDarkThemeEnabled() ?
                getColor(R.color.secondaryDark) : getColor(R.color.quaternaryLight);

        final String retrievedFirstName =
                String.valueOf(retrievedPersonalInformation.get("firstName"));
        final String retrievedLastName =
                String.valueOf(retrievedPersonalInformation.get("lastName"));
        final String retrievedPhoneNumber =
                String.valueOf(retrievedPersonalInformation.get("phoneNumber"));
        final String retrievedWebsite = String.valueOf(retrievedPersonalInformation.get("website"));
        final int countrySpinnerSelection = Integer.parseInt(String.
                valueOf(retrievedPersonalInformation.get("countrySpinnerSelection")));
        final int genderSpinnerSelection = Integer.parseInt(String.
                valueOf(retrievedPersonalInformation.get("genderSpinnerSelection")));
        final String retrievedCareerTitle =
                String.valueOf(retrievedPersonalInformation.get("careerTitle"));

        binding.photo.setBorderColor(accountPhotoBorderColor);
        viewModel.setFirstName(retrievedFirstName);
        viewModel.setLastName(retrievedLastName);
        viewModel.setPhoneNumber(retrievedPhoneNumber);
        viewModel.setWebsite(retrievedWebsite);
        binding.countrySpinner.setSelection(countrySpinnerSelection);
        binding.genderSpinner.setSelection(genderSpinnerSelection);
        viewModel.setCareerTitle(retrievedCareerTitle);
    }

    @Nullable
    private PersonalInformation personalInformationValidation(@NonNull final PersonalInformation initialPersonalInformation) {
        final String enteredFirstName = !String.valueOf(binding.firstNameField.getText()).trim().isEmpty() ?
                String.valueOf(binding.firstNameField.getText()).trim() :
                !String.valueOf(binding.firstNameField.getHint()).trim().equals(getResources().getString(R.string.first_name)) ?
                        String.valueOf(binding.firstNameField.getHint()).trim() : "";

        final String enteredLastName = !String.valueOf(binding.lastNameField.getText()).trim().isEmpty() ?
                String.valueOf(binding.lastNameField.getText()).trim() :
                !String.valueOf(binding.lastNameField.getHint()).trim().equals(getResources().getString(R.string.last_name)) ?
                        String.valueOf(binding.lastNameField.getHint()).trim() : "";

        final String enteredPhoneNumber = !String.valueOf(binding.phoneField.getText()).trim().isEmpty() ?
                String.valueOf(binding.phoneField.getText()).trim() :
                !String.valueOf(binding.phoneField.getHint()).trim().equals(getResources().getString(R.string.phone_number)) ?
                        String.valueOf(binding.phoneField.getHint()).trim() : "";

        final String enteredWebsite = !String.valueOf(binding.websiteField.getText()).trim().isEmpty() ?
                String.valueOf(binding.websiteField.getText()).trim() :
                !String.valueOf(binding.websiteField.getHint()).trim().equals(getResources().getString(R.string.website)) ?
                        String.valueOf(binding.websiteField.getHint()).trim() : "";

        final String enteredCountry = Locale.getDefault().getDisplayLanguage().equals(Languages.ENGLISH_LANGUAGE) ?
                String.valueOf(binding.countrySpinner.getSelectedItem()).trim() :
                String.valueOf(Countries.getCountryNameInEnglish(this,
                        String.valueOf(binding.countrySpinner.getSelectedItem()))).trim();

        final String enteredGender = Locale.getDefault().getDisplayLanguage().equals(Languages.ENGLISH_LANGUAGE) ?
                String.valueOf(binding.genderSpinner.getSelectedItem()).trim() :
                String.valueOf(Genders.getGenderInEnglish(this,
                        String.valueOf(binding.genderSpinner.getSelectedItem()))).trim();

        final String enteredCareerTitle = !String.valueOf(binding.careerTitleField.getText()).trim().isEmpty() ?
                String.valueOf(binding.careerTitleField.getText()).trim() :
                !String.valueOf(binding.careerTitleField.getHint()).trim()
                        .equals(getResources().getString(R.string.career_title)) ?
                        String.valueOf(binding.careerTitleField.getHint()).trim() : "";

        final String enteredPhotoURL = initialPersonalInformation.getPhotoURL();

        final PersonalInformation editedPersonalInformation =
                new PersonalInformation(enteredFirstName,
                        enteredLastName,
                        enteredPhoneNumber,
                        enteredWebsite,
                        enteredCountry,
                        enteredGender,
                        enteredCareerTitle,
                        enteredPhotoURL);

        final boolean firstNameIsOK = !enteredFirstName.isEmpty() ||
                !String.valueOf(binding.firstNameField.getHint()).trim()
                        .equals(getResources().getString(R.string.first_name).trim());

        final boolean lastNameIsOK = !enteredLastName.isEmpty() ||
                !String.valueOf(binding.lastNameField.getHint()).trim()
                        .equals(getResources().getString(R.string.last_name).trim());

        final boolean phoneNumberIsOK = !enteredPhoneNumber.isEmpty() ||
                !String.valueOf(binding.phoneField.getHint()).trim()
                        .equals(getResources().getString(R.string.phone_number).trim());

        final boolean websiteIsOK = !enteredWebsite.isEmpty() ||
                !String.valueOf(binding.websiteField.getHint()).trim()
                        .equals(getResources().getString(R.string.website).trim());

        final boolean countryNameIsOK = !enteredCountry.isEmpty();

        final boolean genderIsOK = !enteredGender.isEmpty();

        final boolean careerTitleIsOK = !enteredCareerTitle.isEmpty() ||
                !String.valueOf(binding.careerTitleField.getHint()).trim()
                        .equals(getResources().getString(R.string.career_title).trim());

        final boolean firstNameHasBeenModified = !enteredFirstName.isEmpty() &&
                !enteredFirstName.equals(String.valueOf(binding.firstNameField.getHint()));

        final boolean lastNameHasBeenModified = !enteredLastName.isEmpty() &&
                !enteredLastName.equals(String.valueOf(binding.lastNameField.getHint()));

        final boolean phoneNumberHasBeenModified = !enteredPhoneNumber.isEmpty() &&
                !enteredPhoneNumber.equals(String.valueOf(binding.phoneField.getHint()));

        final boolean websiteHasBeenModified = !enteredWebsite.isEmpty() &&
                !enteredWebsite.equals(String.valueOf(binding.websiteField.getHint()));

        final boolean countryHasBeenModified = !enteredCountry.isEmpty() &&
                !enteredCountry.equals(initialPersonalInformation.getCountry());

        final boolean genderHasBeenModified = !enteredGender.isEmpty() &&
                !enteredGender.equals(initialPersonalInformation.getGender());

        final boolean careerTitleHasBeenModified = !enteredCareerTitle.isEmpty() &&
                !enteredCareerTitle.equals(String.valueOf(binding.careerTitleField.getHint()));

        personalInformationHasBeenModified = firstNameHasBeenModified ||
                lastNameHasBeenModified ||
                phoneNumberHasBeenModified ||
                websiteHasBeenModified ||
                countryHasBeenModified ||
                genderHasBeenModified ||
                careerTitleHasBeenModified;

        return !initialPersonalInformation.equals(editedPersonalInformation) &&
                firstNameIsOK &&
                lastNameIsOK &&
                phoneNumberIsOK &&
                websiteIsOK &&
                countryNameIsOK &&
                genderIsOK &&
                careerTitleIsOK ?
                editedPersonalInformation : null;
    }

    private void setActivityVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.edit_profile_activity);
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);
        viewModel = new EditProfileViewModel(getApplication(), this, userDetails, binding);
    }

    private void setLayoutVariables() {
        binding.setActivity(this);
        binding.setFragmentManager(getSupportFragmentManager());
        binding.setViewModel(viewModel);
    }

    private void setOnClickListeners() {
        binding.photo.setOnClickListener((final View view) ->
                MyCustomMethods.goToActivityWithFadeTransition(EditProfileActivity.this,
                        EditPhotoActivity.class));

//        binding.updateProfileButton.setOnClickListener((final View view) -> {
//            final PersonalInformation editedPersonalInformation =
//                    personalInformationValidation(userDetails.getPersonalInformation());
//
//            if (editedPersonalInformation != null && MyCustomVariables.getFirebaseAuth().getUid() != null) {
//                final boolean firstNameIsEmpty = editedPersonalInformation.getFirstName().trim().isEmpty();
//
//                final boolean lastNameIsEmpty = editedPersonalInformation.getLastName().trim().isEmpty();
//
//                final boolean phoneNumberIsEmpty = editedPersonalInformation.getPhoneNumber().trim().isEmpty();
//
//                final boolean websiteIsEmpty = editedPersonalInformation.getWebsite().trim().isEmpty();
//
//                final boolean countryIsEmpty = editedPersonalInformation.getCountry().trim().isEmpty();
//
//                final boolean genderIsEmpty = editedPersonalInformation.getGender().trim().isEmpty();
//
//                final boolean careerTitleIsEmpty = editedPersonalInformation.getCareerTitle().trim().isEmpty();
//
//                final boolean allInformationIsCompleted =
//                        !firstNameIsEmpty && !lastNameIsEmpty && !phoneNumberIsEmpty && !websiteIsEmpty &&
//                                !countryIsEmpty && !genderIsEmpty && !careerTitleIsEmpty;
//
//                // updating user's new info into the database, into SharedPreferences and into utilities
//                MyCustomVariables.getDatabaseReference()
//                        .child(MyCustomVariables.getFirebaseAuth().getUid())
//                        .child("PersonalInformation")
//                        .setValue(editedPersonalInformation)
//                        .addOnCompleteListener((final Task<Void> task) -> MyCustomMethods.showShortMessage(this,
//                                getResources().getString(R.string.profile_updated_successfully)));
//
//                userDetails.setPersonalInformation(editedPersonalInformation);
//                MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences, userDetails);
//
//                MyCustomVariables.setUserDetails(userDetails);
//
//                onBackPressed();
//
//            }
//            // here we need to set the case when all information is completed
//            else if (!personalInformationHasBeenModified) {
//                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.no_changes_have_been_made));
//                onBackPressed();
//            } else {
//                MyCustomMethods.showShortMessage(this,
//                        getResources().getString(R.string.please_complete_all_fields));
//            }
//        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void setProfilePhoto() {
        final String photoURL =
                String.valueOf(viewModel.getPersonalInformation(this).get("photoURL"));
        final int photoPlaceholder = binding.getIsDarkThemeEnabled() ?
                R.drawable.ic_person_dark : R.drawable.ic_person_light;

        if (photoURL.trim().isEmpty()) {
            binding.photo.setImageResource(photoPlaceholder);
        } else {
            if (isPhotoUrlModified) {
                Picasso.get()
                        .invalidate(photoURL);
            }

            Picasso.get()
                    .load(photoURL)
                    .placeholder(photoPlaceholder)
                    .fit()
                    .into(binding.photo);
        }
    }

    private void setSpinner(@NonNull final String spinnerType) {
        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                        R.layout.custom_spinner_item,
                        spinnerType.equals("COUNTRY_SPINNER") ?
                                viewModel.getCountriesList() : viewModel.getGendersList()) {
                    @NonNull
                    @Override
                    public View getDropDownView(final int position,
                                                final @Nullable View convertView,
                                                final @NonNull ViewGroup parent) {
                        final View v = super.getDropDownView(position, convertView, parent);
                        final boolean isDarkThemeEnabledInUserDetails =
                                userDetails.getApplicationSettings().isDarkThemeEnabled();

                        // centering all spinner's items' text
                        ((TextView) v).setGravity(Gravity.CENTER);

                        if (userDetails == null) {
                            userDetails = MyCustomVariables.getDefaultUserDetails();
                        }

                        if (isDarkThemeEnabledInUserDetails != isDarkThemeEnabled) {
                            isDarkThemeEnabled = isDarkThemeEnabledInUserDetails;
                        }

                        // setting elements' text color based on the selected theme
                        v.setBackgroundColor(getColor(isDarkThemeEnabled ?
                                R.color.primaryLight : R.color.primaryDark));
                        ((TextView) v).setTextColor(isDarkThemeEnabled ?
                                getColor(R.color.quaternaryLight) : getColor(R.color.secondaryDark));

                        return v;
                    }
                };

        if (spinnerType.equals("COUNTRY_SPINNER")) {
            binding.countrySpinner.setAdapter(adapter);
        } else if (spinnerType.equals("GENDER_SPINNER")) {
            binding.genderSpinner.setAdapter(adapter);
        }
    }

    /**
     * Method for styling spinner's first element
     */
    private void setSpinnerOnSelectedItemListener(@NonNull final Spinner spinner,
                                                  final String spinnerType) {
        final AdapterView.OnItemSelectedListener itemSelectedListener =
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(final AdapterView<?> parent,
                                               final View view,
                                               final int position,
                                               final long id) {
                        if (userDetails == null) {
                            userDetails = MyCustomVariables.getDefaultUserDetails();
                        }

                        if (userDetails.getApplicationSettings().isDarkThemeEnabled()
                                != isDarkThemeEnabled) {
                            isDarkThemeEnabled =
                                    userDetails.getApplicationSettings().isDarkThemeEnabled();
                        }

                        final int color = isDarkThemeEnabled ?
                                getColor(R.color.secondaryDark) : getColor(R.color.quaternaryLight);
                        // centering spinner's first item's text
                        // and setting its color based on the selected theme
                        ((TextView) parent.getChildAt(0)).setTextColor(color);
                        ((TextView) parent.getChildAt(0)).setGravity(Gravity.START);

                        if (spinnerType.trim().equals("COUNTRY_SPINNER")) {
                            viewModel.setCountry(String.valueOf(parent.getItemAtPosition(position)));
                        } else if (spinnerType.trim().equals("GENDER_SPINNER")) {
                            viewModel.setGender(String.valueOf(parent.getItemAtPosition(position)));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                };

        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    private void setTheme() {
        isDarkThemeEnabled = userDetails != null ?
                userDetails.getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

        binding.setIsDarkThemeEnabled(isDarkThemeEnabled);

        final int color = getColor(isDarkThemeEnabled ?
                R.color.secondaryDark : R.color.quaternaryLight);

        // setting arrow color
        binding.countrySpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        binding.genderSpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
}