package com.example.economy_manager.main_part.views.activities;

import android.content.Intent;
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

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.viewmodels.EditAccountViewModel;
import com.example.economy_manager.models.BirthDate;
import com.example.economy_manager.models.PersonalInformation;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAccountActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private EditAccountViewModel editAccountViewModel;
    private UserDetails userDetails;
    private ImageView goBack;
    private CircleImageView accountPhoto;
    private TextView topText;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText phoneNumberInput;
    private EditText websiteInput;
    private DatePicker birthDatePicker;
    private EditText careerTitleInput;
    private Spinner countrySpinner;
    private Spinner genderSpinner;
    private Button updateProfileButton;
    private boolean darkThemeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account_remastered_activity);
        setVariables();
        setTheme();
        setOnClickListeners();
        setTopText();
        setCountrySpinner();
        setGenderSpinner();
        getPersonalInformation();
        setSelectedItemColorSpinner(countrySpinner);
        setSelectedItemColorSpinner(genderSpinner);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setVariables() {
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);
        editAccountViewModel = new EditAccountViewModel(getApplication(), userDetails);
        goBack = findViewById(R.id.editAccountRemasteredBack);
        firstNameInput = findViewById(R.id.editAccountRemasteredFirstNameField);
        lastNameInput = findViewById(R.id.editAccountRemasteredLastNameField);
        phoneNumberInput = findViewById(R.id.editAccountRemasteredPhoneField);
        websiteInput = findViewById(R.id.editAccountRemasteredWebsiteField);
        birthDatePicker = findViewById(R.id.editAccountRemasteredBirthDatePicker);
        careerTitleInput = findViewById(R.id.editAccountRemasteredCareerTitleField);
        countrySpinner = findViewById(R.id.editAccountRemasteredCountrySpinner);
        genderSpinner = findViewById(R.id.editAccountRemasteredGenderSpinner);
        accountPhoto = findViewById(R.id.editAccountRemasteredPhoto);
        topText = findViewById(R.id.editAccountRemasteredTopText);
        updateProfileButton = findViewById(R.id.editAccountRemasteredUpdateButton);
    }

    private void setOnClickListeners() {
        accountPhoto.setOnClickListener(v -> {
            startActivity(new Intent(EditAccountActivity.this, EditPhotoActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        goBack.setOnClickListener(v -> onBackPressed());

        updateProfileButton.setOnClickListener(view -> {
            final PersonalInformation editedPersonalInformation = validation(userDetails.getPersonalInformation());
            // updating user's new info into the database, into SharedPreferences and into utilities
            if (editedPersonalInformation != null &&
                    MyCustomVariables.getFirebaseAuth().getUid() != null) {
                MyCustomVariables.getDatabaseReference()
                        .child(MyCustomVariables.getFirebaseAuth().getUid())
                        .child("PersonalInformation")
                        .setValue(editedPersonalInformation)
                        .addOnCompleteListener(task ->
                                MyCustomMethods.showShortMessage(this, "Profile updated successfully"));

                userDetails.setPersonalInformation(editedPersonalInformation);
                MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences, userDetails);

                MyCustomVariables.setUserDetails(userDetails);

                onBackPressed();
            } else {
                MyCustomMethods.showShortMessage(this, "Please complete all fields");
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void getPersonalInformation() {
        final String accountPhotoImageURL = userDetails != null ?
                userDetails.getPersonalInformation().getPhotoURL() : "";

        final int accountPhotoPlaceholder = userDetails == null || !userDetails.getApplicationSettings().getDarkTheme() ?
                R.drawable.ic_person_blue : R.drawable.ic_person_white;

        final int accountPhotoBorderColor = userDetails == null || !userDetails.getApplicationSettings().getDarkTheme() ?
                getColor(R.color.turkish_sea) : Color.WHITE;

        final String firstNameHint =
                userDetails.getPersonalInformation().getFirstName().trim().isEmpty() || userDetails == null ?
                        getResources().getString(R.string.edit_account_first_name).trim() :
                        userDetails.getPersonalInformation().getFirstName().trim();

        final String lastNameHint =
                userDetails.getPersonalInformation().getLastName().trim().isEmpty() || userDetails == null ?
                        getResources().getString(R.string.edit_account_last_name).trim() :
                        userDetails.getPersonalInformation().getLastName().trim();

        final String phoneNumberHint =
                userDetails.getPersonalInformation().getPhoneNumber().trim().isEmpty() || userDetails == null ?
                        getResources().getString(R.string.edit_account_phone_number).trim() :
                        userDetails.getPersonalInformation().getPhoneNumber().trim();

        final String websiteHint =
                userDetails.getPersonalInformation().getWebsite().trim().isEmpty() || userDetails == null ?
                        getResources().getString(R.string.edit_account_website).trim() :
                        userDetails.getPersonalInformation().getWebsite().trim();

        final int countrySpinnerSelection = editAccountViewModel.getPositionInCountryList(getApplication(),
                userDetails != null ? userDetails.getPersonalInformation().getCountry().trim() : "");

        final int genderSpinnerSelection = editAccountViewModel.getPositionInGenderList(getApplication(),
                userDetails != null ? userDetails.getPersonalInformation().getGender().trim() : "");

        final BirthDate birthDateHint =
                userDetails.getPersonalInformation().getBirthDate().toString().trim().isEmpty() || userDetails == null ?
                        new BirthDate() : userDetails.getPersonalInformation().getBirthDate();

        final String careerTitleHint =
                userDetails.getPersonalInformation().getCareerTitle().trim().isEmpty() || userDetails == null ?
                        getResources().getString(R.string.edit_account_career_title).trim() :
                        userDetails.getPersonalInformation().getCareerTitle().trim();

        Picasso.get()
                .load(accountPhotoImageURL)
                .placeholder(accountPhotoPlaceholder)
                .fit()
                .into(accountPhoto);

        accountPhoto.setBorderColor(accountPhotoBorderColor);

        firstNameInput.setHint(firstNameHint);

        lastNameInput.setHint(lastNameHint);

        phoneNumberInput.setHint(phoneNumberHint);

        websiteInput.setHint(websiteHint);

        countrySpinner.setSelection(countrySpinnerSelection);

        genderSpinner.setSelection(genderSpinnerSelection);

        // if the birth date is not selected as today (today is the default date for DatePicker)
        if (!birthDateHint.equals(new BirthDate())) {
            final int birthDateYear = birthDateHint.getYear();
            final int birthDateMonth = birthDateHint.getMonth() - 1;
            final int birthDateDay = birthDateHint.getDay();

            birthDatePicker.updateDate(birthDateYear, birthDateMonth, birthDateDay);
        }

        careerTitleInput.setHint(careerTitleHint);
    }

    private void setTopText() {
        final String text = getResources().getString(R.string.edit_account).trim();

        topText.setText(text);
        topText.setTextSize(20);
        topText.setTextColor(Color.WHITE);
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
//        setEditTextColor(birthDatePicker, color);
        setEditTextColor(careerTitleInput, color);

        setUpdateProfileButtonStyle(darkThemeEnabled);
    }

    private void setEditTextColor(final EditText editText,
                                  final int color) {
        editText.setHintTextColor(color);
        editText.setTextColor(color);
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
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

    // styling spinner's first item
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

    private void setUpdateProfileButtonStyle(final boolean darkThemeEnabled) {
        final int buttonBackground = darkThemeEnabled ? R.drawable.button_white_border : R.drawable.button_blue_border;

        final int buttonTextColor = darkThemeEnabled ? Color.WHITE : getColor(R.color.turkish_sea);

        updateProfileButton.setBackgroundResource(buttonBackground);
        updateProfileButton.setTextColor(buttonTextColor);
    }

    private PersonalInformation validation(final PersonalInformation initialPersonalInformation) {
        final String enteredFirstName = String.valueOf(firstNameInput.getText()).trim();

        final String enteredLastName = String.valueOf(lastNameInput.getText()).trim();

        final String enteredPhoneNumber = String.valueOf(phoneNumberInput.getText()).trim();

        final String enteredWebsite = String.valueOf(websiteInput.getText()).trim();

        final String enteredCountry = Locale.getDefault().getDisplayLanguage().equals("English") ?
                String.valueOf(countrySpinner.getSelectedItem()).trim() :
                String.valueOf(editAccountViewModel.getCountryNameInEnglish(getApplication(),
                        String.valueOf(countrySpinner.getSelectedItem()))).trim();

        final String enteredGender = Locale.getDefault().getDisplayLanguage().equals("English") ?
                String.valueOf(genderSpinner.getSelectedItem()).trim() :
                String.valueOf(editAccountViewModel.getGenderInEnglish(getApplication(),
                        String.valueOf(genderSpinner.getSelectedItem()))).trim();

        final int birthDateYear = birthDatePicker.getYear();

        final int birthDateMonth = birthDatePicker.getMonth() + 1;

        final int birthDateDay = birthDatePicker.getDayOfMonth();

        final BirthDate enteredBirthDate = new BirthDate(birthDateYear, birthDateMonth, birthDateDay);

        final String enteredCareerTitle = String.valueOf(careerTitleInput.getText()).trim();

        final String enteredPhotoURL = initialPersonalInformation.getPhotoURL();

        final PersonalInformation editedPersonalInformation = new PersonalInformation(enteredFirstName, enteredLastName,
                enteredPhoneNumber, enteredWebsite, enteredCountry, enteredGender, enteredBirthDate, enteredCareerTitle,
                enteredPhotoURL);

        return !initialPersonalInformation.equals(editedPersonalInformation) &&
                !enteredFirstName.isEmpty() &&
                !enteredLastName.isEmpty() &&
                !enteredPhoneNumber.isEmpty() &&
                !enteredWebsite.isEmpty() &&
                !enteredCountry.isEmpty() &&
                !enteredGender.isEmpty() &&
                !enteredCareerTitle.isEmpty() ?
                editedPersonalInformation : null;
    }
}