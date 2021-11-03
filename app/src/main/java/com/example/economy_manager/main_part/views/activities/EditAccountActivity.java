package com.example.economy_manager.main_part.views.activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.viewmodels.EditAccountViewModel;
import com.example.economy_manager.main_part.views.fragments.DatePickerFragment;
import com.example.economy_manager.models.BirthDate;
import com.example.economy_manager.models.PersonalInformation;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.Languages;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAccountActivity
        extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {
    private SharedPreferences preferences;
    private EditAccountViewModel editAccountViewModel;
    private UserDetails userDetails;
    private ImageView goBack;
    private CircleImageView accountPhoto;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText phoneNumberInput;
    private EditText websiteInput;
    private TextView birthDateText;
    private EditText careerTitleInput;
    private Spinner countrySpinner;
    private Spinner genderSpinner;
    private Button updateProfileButton;
    private boolean darkThemeEnabled;
    private boolean personalInformationHasBeenModified;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyCustomMethods.finishActivityWithFadeTransition(this);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account_remastered_activity);
        setVariables();
        setTheme();
        setOnClickListeners();
        setCountrySpinner();
        setGenderSpinner();
        getAndSetPersonalInformation();
        setSelectedItemColorSpinner(countrySpinner);
        setSelectedItemColorSpinner(genderSpinner);
    }

    /**
     * Method for retrieving the birthdate from the fragment and setting it to the corresponding field
     */
    @Override
    public void onDateSet(final DatePicker datePicker,
                          final int year,
                          final int month,
                          final int dayOfMonth) {
        final LocalDate newTransactionDate = LocalDate.of(year, month + 1, dayOfMonth);

        setBirthDateText(newTransactionDate);
    }

    /**
     * Method for retrieving user's personal information from SharedPreferences and filling the fields with the info
     */
    private void getAndSetPersonalInformation() {
        final String accountPhotoImageURL = userDetails != null ?
                userDetails.getPersonalInformation().getPhotoURL() : "";

        final int accountPhotoPlaceholder = userDetails == null || !userDetails.getApplicationSettings().getDarkTheme() ?
                R.drawable.ic_person_blue : R.drawable.ic_person_white;

        final int accountPhotoBorderColor = userDetails == null || !userDetails.getApplicationSettings().getDarkTheme() ?
                getColor(R.color.turkish_sea) : Color.WHITE;

        final String firstNameHint = userDetails == null ||
                userDetails.getPersonalInformation().getFirstName().trim().isEmpty() ?
                getResources().getString(R.string.edit_account_first_name).trim() :
                userDetails.getPersonalInformation().getFirstName().trim();

        final String lastNameHint = userDetails == null ||
                userDetails.getPersonalInformation().getLastName().trim().isEmpty() ?
                getResources().getString(R.string.edit_account_last_name).trim() :
                userDetails.getPersonalInformation().getLastName().trim();

        final String phoneNumberHint = userDetails == null ||
                userDetails.getPersonalInformation().getPhoneNumber().trim().isEmpty() ?
                getResources().getString(R.string.edit_account_phone_number).trim() :
                userDetails.getPersonalInformation().getPhoneNumber().trim();

        final String websiteHint = userDetails == null ||
                userDetails.getPersonalInformation().getWebsite().trim().isEmpty() ?
                getResources().getString(R.string.edit_account_website).trim() :
                userDetails.getPersonalInformation().getWebsite().trim();

        final int countrySpinnerSelection = editAccountViewModel.getPositionInCountryList(getApplication(),
                userDetails != null ? userDetails.getPersonalInformation().getCountry().trim() : "");

        final int genderSpinnerSelection = editAccountViewModel.getPositionInGenderList(getApplication(),
                userDetails != null ? userDetails.getPersonalInformation().getGender().trim() : "");

        final String careerTitleHint = userDetails == null ||
                userDetails.getPersonalInformation().getCareerTitle().trim().isEmpty() ?
                getResources().getString(R.string.edit_account_career_title).trim() :
                userDetails.getPersonalInformation().getCareerTitle().trim();

        if (accountPhotoImageURL.trim().isEmpty()) {
            accountPhoto.setImageResource(accountPhotoPlaceholder);
        } else {
            Picasso.get()
                    .load(accountPhotoImageURL)
                    .placeholder(accountPhotoPlaceholder)
                    .fit()
                    .into(accountPhoto);
        }

        accountPhoto.setBorderColor(accountPhotoBorderColor);

        firstNameInput.setHint(firstNameHint);

        lastNameInput.setHint(lastNameHint);

        phoneNumberInput.setHint(phoneNumberHint);

        websiteInput.setHint(websiteHint);

        countrySpinner.setSelection(countrySpinnerSelection);

        genderSpinner.setSelection(genderSpinnerSelection);

        setBirthDateText(editAccountViewModel.getUserDetails() == null ||
                editAccountViewModel.getUserDetails().getPersonalInformation() == null ||
                editAccountViewModel.getUserDetails().getPersonalInformation().getBirthDate() == null ?
                LocalDate.now() :
                LocalDate.of(editAccountViewModel.getUserDetails().getPersonalInformation().getBirthDate().getYear(),
                        editAccountViewModel.getUserDetails().getPersonalInformation().getBirthDate().getMonth(),
                        editAccountViewModel.getUserDetails().getPersonalInformation().getBirthDate().getDay()));

        careerTitleInput.setHint(careerTitleHint);
    }

    private PersonalInformation personalInformationValidation(final PersonalInformation initialPersonalInformation) {
        final String enteredFirstName = !String.valueOf(firstNameInput.getText()).trim().isEmpty() ?
                String.valueOf(firstNameInput.getText()).trim() :
                !String.valueOf(firstNameInput.getHint()).trim().equals(getResources().getString(R.string.edit_account_first_name)) ?
                        String.valueOf(firstNameInput.getHint()).trim() : "";

        final String enteredLastName = !String.valueOf(lastNameInput.getText()).trim().isEmpty() ?
                String.valueOf(lastNameInput.getText()).trim() :
                !String.valueOf(lastNameInput.getHint()).trim().equals(getResources().getString(R.string.edit_account_last_name)) ?
                        String.valueOf(lastNameInput.getHint()).trim() : "";

        final String enteredPhoneNumber = !String.valueOf(phoneNumberInput.getText()).trim().isEmpty() ?
                String.valueOf(phoneNumberInput.getText()).trim() :
                !String.valueOf(phoneNumberInput.getHint()).trim().equals(getResources().getString(R.string.edit_account_phone_number)) ?
                        String.valueOf(phoneNumberInput.getHint()).trim() : "";

        final String enteredWebsite = !String.valueOf(websiteInput.getText()).trim().isEmpty() ?
                String.valueOf(websiteInput.getText()).trim() :
                !String.valueOf(websiteInput.getHint()).trim().equals(getResources().getString(R.string.edit_account_website)) ?
                        String.valueOf(websiteInput.getHint()).trim() : "";

        final String enteredCountry = Locale.getDefault().getDisplayLanguage().equals(Languages.getEnglishLanguage()) ?
                String.valueOf(countrySpinner.getSelectedItem()).trim() :
                String.valueOf(editAccountViewModel.getCountryNameInEnglish(getApplication(),
                        String.valueOf(countrySpinner.getSelectedItem()))).trim();

        final String enteredGender = Locale.getDefault().getDisplayLanguage().equals(Languages.getEnglishLanguage()) ?
                String.valueOf(genderSpinner.getSelectedItem()).trim() :
                String.valueOf(editAccountViewModel.getGenderInEnglish(getApplication(),
                        String.valueOf(genderSpinner.getSelectedItem()))).trim();

        final int birthDateYear = editAccountViewModel.getTransactionDate().getYear();

        final int birthDateMonth = editAccountViewModel.getTransactionDate().getMonthValue();

        final int birthDateDay = editAccountViewModel.getTransactionDate().getDayOfMonth();

        final BirthDate enteredBirthDate = new BirthDate(birthDateYear, birthDateMonth, birthDateDay);

        final String enteredCareerTitle = !String.valueOf(careerTitleInput.getText()).trim().isEmpty() ?
                String.valueOf(careerTitleInput.getText()).trim() :
                !String.valueOf(careerTitleInput.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_career_title)) ?
                        String.valueOf(careerTitleInput.getHint()).trim() : "";

        final String enteredPhotoURL = initialPersonalInformation.getPhotoURL();

        final PersonalInformation editedPersonalInformation =
                new PersonalInformation(enteredFirstName,
                        enteredLastName,
                        enteredPhoneNumber,
                        enteredWebsite,
                        enteredCountry,
                        enteredGender,
                        enteredBirthDate,
                        enteredCareerTitle,
                        enteredPhotoURL);

        final boolean firstNameIsOK = !enteredFirstName.isEmpty() ||
                !String.valueOf(firstNameInput.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_first_name).trim());

        final boolean lastNameIsOK = !enteredLastName.isEmpty() ||
                !String.valueOf(lastNameInput.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_last_name).trim());

        final boolean phoneNumberIsOK = !enteredPhoneNumber.isEmpty() ||
                !String.valueOf(phoneNumberInput.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_phone_number).trim());

        final boolean websiteIsOK = !enteredWebsite.isEmpty() ||
                !String.valueOf(websiteInput.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_website).trim());

        final boolean countryNameIsOK = !enteredCountry.isEmpty();

        final boolean genderIsOK = !enteredGender.isEmpty();

        final boolean careerTitleIsOK = !enteredCareerTitle.isEmpty() ||
                !String.valueOf(careerTitleInput.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_career_title).trim());

        final boolean firstNameHasBeenModified = !enteredFirstName.isEmpty() &&
                !enteredFirstName.equals(String.valueOf(firstNameInput.getHint()));

        final boolean lastNameHasBeenModified = !enteredLastName.isEmpty() &&
                !enteredLastName.equals(String.valueOf(lastNameInput.getHint()));

        final boolean phoneNumberHasBeenModified = !enteredPhoneNumber.isEmpty() &&
                !enteredPhoneNumber.equals(String.valueOf(phoneNumberInput.getHint()));

        final boolean websiteHasBeenModified = !enteredWebsite.isEmpty() &&
                !enteredWebsite.equals(String.valueOf(websiteInput.getHint()));

        final boolean countryHasBeenModified = !enteredCountry.isEmpty() &&
                !enteredCountry.equals(initialPersonalInformation.getCountry());

        final boolean genderHasBeenModified = !enteredGender.isEmpty() &&
                !enteredGender.equals(initialPersonalInformation.getGender());

        final boolean careerTitleHasBeenModified = !enteredCareerTitle.isEmpty() &&
                !enteredCareerTitle.equals(String.valueOf(careerTitleInput.getHint()));

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

    private void setBirthDateText(final LocalDate date) {
        final String formattedDate = MyCustomMethods.getFormattedDate(date);

        if (!editAccountViewModel.getTransactionDate().equals(date)) {
            editAccountViewModel.setTransactionDate(date);
        }

        if (!String.valueOf(birthDateText.getText()).trim().equals(formattedDate)) {
            birthDateText.setText(formattedDate);
        }
    }

    private void setBirthDateTextStyle(final int color) {
        final int drawableStartIcon = color == getColor(R.color.turkish_sea) ?
                R.drawable.ic_time_blue : R.drawable.ic_time_white;

        birthDateText.setTextColor(color);
        birthDateText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStartIcon, 0, 0, 0);
    }

    private void setCountrySpinner() {
        final ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item,
                editAccountViewModel.getCountryList()) {
            @Override
            public View getDropDownView(final int position,
                                        final @Nullable View convertView,
                                        final @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                // centering spinner's items' text
                ((TextView) v).setGravity(Gravity.CENTER);

                if (userDetails == null) {
                    userDetails = MyCustomVariables.getDefaultUserDetails();
                }

                if (userDetails.getApplicationSettings().getDarkTheme() != darkThemeEnabled) {
                    darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                }

                final int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;
                // setting elements' text color based on the selected theme
                ((TextView) v).setTextColor(itemsColor);

                return v;
            }
        };

        countrySpinner.setAdapter(countryAdapter);
    }

    private void setEditTextColor(final EditText editText,
                                  final int color) {
        editText.setHintTextColor(color);
        editText.setTextColor(color);
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void setGenderSpinner() {
        final ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item,
                editAccountViewModel.getGenderList()) {
            @Override
            public View getDropDownView(final int position,
                                        final @Nullable View convertView,
                                        final @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                // centering all spinner's items' text
                ((TextView) v).setGravity(Gravity.CENTER);

                if (userDetails == null) {
                    userDetails = MyCustomVariables.getDefaultUserDetails();
                }

                if (userDetails.getApplicationSettings().getDarkTheme() != darkThemeEnabled) {
                    darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                }

                final int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;
                // setting elements' text color based on the selected theme
                ((TextView) v).setTextColor(itemsColor);

                return v;
            }
        };

        genderSpinner.setAdapter(genderAdapter);
    }

    private void setOnClickListeners() {
        accountPhoto.setOnClickListener((final View view) ->
                MyCustomMethods.goToActivityWithFadeTransition(EditAccountActivity.this,
                        EditPhotoActivity.class));

        birthDateText.setOnClickListener((final View view) -> {
            final DialogFragment datePickerFragment = new DatePickerFragment(editAccountViewModel.getTransactionDate());

            datePickerFragment.show(getSupportFragmentManager(), "date_picker");
        });

        goBack.setOnClickListener((final View view) -> onBackPressed());

        updateProfileButton.setOnClickListener((final View view) -> {
            final PersonalInformation editedPersonalInformation =
                    personalInformationValidation(userDetails.getPersonalInformation());
            // updating user's new info into the database, into SharedPreferences and into utilities
            if (editedPersonalInformation != null &&
                    MyCustomVariables.getFirebaseAuth().getUid() != null) {
                MyCustomVariables.getDatabaseReference()
                        .child(MyCustomVariables.getFirebaseAuth().getUid())
                        .child("PersonalInformation")
                        .setValue(editedPersonalInformation)
                        .addOnCompleteListener((final Task<Void> task) -> MyCustomMethods.showShortMessage(this,
                                getResources().getString(R.string.profile_updated_successfully)));

                userDetails.setPersonalInformation(editedPersonalInformation);
                MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences, userDetails);

                MyCustomVariables.setUserDetails(userDetails);

                onBackPressed();
            } else if (!personalInformationHasBeenModified) {
                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.no_changes_have_been_made));
                onBackPressed();
            } else {
                MyCustomMethods.showShortMessage(this,
                        getResources().getString(R.string.please_complete_all_fields));
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * Method for styling spinner's first element
     */
    private void setSelectedItemColorSpinner(final Spinner spinner) {
        final AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent,
                                       final View view,
                                       final int position,
                                       final long id) {
                if (userDetails == null) {
                    userDetails = MyCustomVariables.getDefaultUserDetails();
                }

                if (userDetails.getApplicationSettings().getDarkTheme() != darkThemeEnabled) {
                    darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                }

                final int color = !darkThemeEnabled ? getColor(R.color.turkish_sea) : Color.WHITE;
                // centering spinner's first item's text and setting its color based on the selected theme
                ((TextView) parent.getChildAt(0)).setTextColor(color);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.START);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    private void setTheme() {
        darkThemeEnabled = userDetails != null ?
                userDetails.getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        final int theme = !darkThemeEnabled ?
                R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift;
        // turkish sea (blue) if dark theme is enabled or white if it's not
        final int color = !darkThemeEnabled ? getColor(R.color.turkish_sea) : Color.WHITE;

        final int dropDownTheme = !darkThemeEnabled ?
                R.drawable.ic_blue_gradient_unloved_teen : R.drawable.ic_white_gradient_tobacco_ad;

        getWindow().setBackgroundDrawableResource(theme);
        // setting arrow color
        countrySpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        // setting element's color
        countrySpinner.setPopupBackgroundResource(dropDownTheme);
        genderSpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        genderSpinner.setPopupBackgroundResource(dropDownTheme);

        setEditTextColor(firstNameInput, color);
        setEditTextColor(lastNameInput, color);
        setEditTextColor(phoneNumberInput, color);
        setEditTextColor(websiteInput, color);
        setEditTextColor(careerTitleInput, color);

        setBirthDateTextStyle(color);
        setUpdateProfileButtonStyle(darkThemeEnabled);
    }

    private void setUpdateProfileButtonStyle(final boolean darkThemeEnabled) {
        final int buttonBackground = darkThemeEnabled ? R.drawable.button_white_border : R.drawable.button_blue_border;

        final int buttonTextColor = darkThemeEnabled ? Color.WHITE : getColor(R.color.turkish_sea);

        updateProfileButton.setBackgroundResource(buttonBackground);
        updateProfileButton.setTextColor(buttonTextColor);
    }

    private void setVariables() {
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);
        editAccountViewModel = new EditAccountViewModel(getApplication(), userDetails);
        goBack = findViewById(R.id.edit_account_remastered_back);
        firstNameInput = findViewById(R.id.edit_account_remastered_first_name_field);
        lastNameInput = findViewById(R.id.edit_account_remastered_last_name_field);
        phoneNumberInput = findViewById(R.id.edit_account_remastered_phone_field);
        websiteInput = findViewById(R.id.edit_account_remastered_website_field);
        birthDateText = findViewById(R.id.edit_account_remastered_birth_date_text);
        careerTitleInput = findViewById(R.id.edit_account_remastered_career_title_field);
        countrySpinner = findViewById(R.id.edit_account_remastered_country_spinner);
        genderSpinner = findViewById(R.id.edit_account_remastered_gender_spinner);
        accountPhoto = findViewById(R.id.edit_account_remastered_photo);
        updateProfileButton = findViewById(R.id.edit_account_remastered_update_button);
    }
}