package com.example.economy_manager.feature.editaccount;

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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.EditAccountRemasteredActivityBinding;
import com.example.economy_manager.feature.editphoto.EditPhotoActivity;
import com.example.economy_manager.model.BirthDate;
import com.example.economy_manager.model.PersonalInformation;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Languages;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;

public class EditAccountActivity
        extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {
    private EditAccountRemasteredActivityBinding binding;
    private EditAccountViewModel viewModel;
    private SharedPreferences preferences;
    private UserDetails userDetails;
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
        setVariables();
        setTheme();
        setOnClickListeners();
        setCountrySpinner();
        setGenderSpinner();
        getAndSetPersonalInformation();
        setSelectedItemColorSpinner(binding.countrySpinner);
        setSelectedItemColorSpinner(binding.genderSpinner);
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
        final HashMap<String, Object> retrievedPersonalInformation = viewModel.getPersonalInformation(this);

        final String accountPhotoImageURL = String.valueOf(retrievedPersonalInformation.get("photoURL"));

        final int accountPhotoPlaceholder = userDetails == null || !userDetails.getApplicationSettings().getDarkTheme() ?
                R.drawable.ic_person_blue : R.drawable.ic_person_white;

        final int accountPhotoBorderColor = userDetails == null || !userDetails.getApplicationSettings().getDarkTheme() ?
                getColor(R.color.turkish_sea) : Color.WHITE;

        final String firstNameHint = String.valueOf(retrievedPersonalInformation.get("firstName"));

        final String lastNameHint = String.valueOf(retrievedPersonalInformation.get("lastName"));

        final String phoneNumberHint = String.valueOf(retrievedPersonalInformation.get("phoneNumber"));

        final String websiteHint = String.valueOf(retrievedPersonalInformation.get("website"));

        final int countrySpinnerSelection =
                Integer.parseInt(String.valueOf(retrievedPersonalInformation.get("countrySpinnerSelection")));

        final int genderSpinnerSelection =
                Integer.parseInt(String.valueOf(retrievedPersonalInformation.get("genderSpinnerSelection")));

        final LocalDate birthDateHint = (LocalDate) retrievedPersonalInformation.get("birthDate");

        final String careerTitleHint = String.valueOf(retrievedPersonalInformation.get("careerTitle"));

        if (accountPhotoImageURL.trim().isEmpty()) {
            binding.photo.setImageResource(accountPhotoPlaceholder);
        } else {
            Picasso.get()
                    .load(accountPhotoImageURL)
                    .placeholder(accountPhotoPlaceholder)
                    .fit()
                    .into(binding.photo);
        }

        binding.photo.setBorderColor(accountPhotoBorderColor);

        binding.firstNameField.setHint(firstNameHint);

        binding.lastNameField.setHint(lastNameHint);

        binding.phoneField.setHint(phoneNumberHint);

        binding.websiteField.setHint(websiteHint);

        binding.countrySpinner.setSelection(countrySpinnerSelection);

        binding.genderSpinner.setSelection(genderSpinnerSelection);

        setBirthDateText(birthDateHint);

        binding.careerTitleField.setHint(careerTitleHint);
    }

    private PersonalInformation personalInformationValidation(final PersonalInformation initialPersonalInformation) {
        final String enteredFirstName = !String.valueOf(binding.firstNameField.getText()).trim().isEmpty() ?
                String.valueOf(binding.firstNameField.getText()).trim() :
                !String.valueOf(binding.firstNameField.getHint()).trim().equals(getResources().getString(R.string.edit_account_first_name)) ?
                        String.valueOf(binding.firstNameField.getHint()).trim() : "";

        final String enteredLastName = !String.valueOf(binding.lastNameField.getText()).trim().isEmpty() ?
                String.valueOf(binding.lastNameField.getText()).trim() :
                !String.valueOf(binding.lastNameField.getHint()).trim().equals(getResources().getString(R.string.edit_account_last_name)) ?
                        String.valueOf(binding.lastNameField.getHint()).trim() : "";

        final String enteredPhoneNumber = !String.valueOf(binding.phoneField.getText()).trim().isEmpty() ?
                String.valueOf(binding.phoneField.getText()).trim() :
                !String.valueOf(binding.phoneField.getHint()).trim().equals(getResources().getString(R.string.edit_account_phone_number)) ?
                        String.valueOf(binding.phoneField.getHint()).trim() : "";

        final String enteredWebsite = !String.valueOf(binding.websiteField.getText()).trim().isEmpty() ?
                String.valueOf(binding.websiteField.getText()).trim() :
                !String.valueOf(binding.websiteField.getHint()).trim().equals(getResources().getString(R.string.edit_account_website)) ?
                        String.valueOf(binding.websiteField.getHint()).trim() : "";

        final String enteredCountry = Locale.getDefault().getDisplayLanguage().equals(Languages.getEnglishLanguage()) ?
                String.valueOf(binding.countrySpinner.getSelectedItem()).trim() :
                String.valueOf(viewModel.getCountryNameInEnglish(getApplication(),
                        String.valueOf(binding.countrySpinner.getSelectedItem()))).trim();

        final String enteredGender = Locale.getDefault().getDisplayLanguage().equals(Languages.getEnglishLanguage()) ?
                String.valueOf(binding.genderSpinner.getSelectedItem()).trim() :
                String.valueOf(viewModel.getGenderInEnglish(getApplication(),
                        String.valueOf(binding.genderSpinner.getSelectedItem()))).trim();

        final int birthDateYear = viewModel.getTransactionDate().getYear();

        final int birthDateMonth = viewModel.getTransactionDate().getMonthValue();

        final int birthDateDay = viewModel.getTransactionDate().getDayOfMonth();

        final BirthDate enteredBirthDate = new BirthDate(birthDateYear, birthDateMonth, birthDateDay);

        final String enteredCareerTitle = !String.valueOf(binding.careerTitleField.getText()).trim().isEmpty() ?
                String.valueOf(binding.careerTitleField.getText()).trim() :
                !String.valueOf(binding.careerTitleField.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_career_title)) ?
                        String.valueOf(binding.careerTitleField.getHint()).trim() : "";

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
                !String.valueOf(binding.firstNameField.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_first_name).trim());

        final boolean lastNameIsOK = !enteredLastName.isEmpty() ||
                !String.valueOf(binding.lastNameField.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_last_name).trim());

        final boolean phoneNumberIsOK = !enteredPhoneNumber.isEmpty() ||
                !String.valueOf(binding.phoneField.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_phone_number).trim());

        final boolean websiteIsOK = !enteredWebsite.isEmpty() ||
                !String.valueOf(binding.websiteField.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_website).trim());

        final boolean countryNameIsOK = !enteredCountry.isEmpty();

        final boolean genderIsOK = !enteredGender.isEmpty();

        final boolean careerTitleIsOK = !enteredCareerTitle.isEmpty() ||
                !String.valueOf(binding.careerTitleField.getHint()).trim()
                        .equals(getResources().getString(R.string.edit_account_career_title).trim());

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

    private void setBirthDateText(final LocalDate date) {
        final String formattedDate = MyCustomMethods.getFormattedDate(date);

        if (!viewModel.getTransactionDate().equals(date)) {
            viewModel.setTransactionDate(date);
        }

        if (!String.valueOf(binding.birthDateText.getText()).trim().equals(formattedDate)) {
            binding.birthDateText.setText(formattedDate);
        }
    }

    private void setBirthDateTextStyle(final int color) {
        final int drawableStartIcon = color == getColor(R.color.turkish_sea) ?
                R.drawable.ic_time_blue : R.drawable.ic_time_white;

        binding.birthDateText.setTextColor(color);
        binding.birthDateText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStartIcon, 0, 0, 0);
    }

    private void setCountrySpinner() {
        final ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item,
                viewModel.getCountryList()) {
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

        binding.countrySpinner.setAdapter(countryAdapter);
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
                viewModel.getGenderList()) {
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

        binding.genderSpinner.setAdapter(genderAdapter);
    }

    private void setOnClickListeners() {
        binding.photo.setOnClickListener((final View view) ->
                MyCustomMethods.goToActivityWithFadeTransition(EditAccountActivity.this,
                        EditPhotoActivity.class));

        binding.updateProfileButton.setOnClickListener((final View view) -> {
            final PersonalInformation editedPersonalInformation =
                    personalInformationValidation(userDetails.getPersonalInformation());

            if (editedPersonalInformation != null && MyCustomVariables.getFirebaseAuth().getUid() != null) {
                final boolean firstNameIsEmpty = editedPersonalInformation.getFirstName().trim().isEmpty();

                final boolean lastNameIsEmpty = editedPersonalInformation.getLastName().trim().isEmpty();

                final boolean phoneNumberIsEmpty = editedPersonalInformation.getPhoneNumber().trim().isEmpty();

                final boolean websiteIsEmpty = editedPersonalInformation.getWebsite().trim().isEmpty();

                final boolean countryIsEmpty = editedPersonalInformation.getCountry().trim().isEmpty();

                final boolean genderIsEmpty = editedPersonalInformation.getGender().trim().isEmpty();

                final boolean careerTitleIsEmpty = editedPersonalInformation.getCareerTitle().trim().isEmpty();

                final boolean allInformationIsCompleted =
                        !firstNameIsEmpty && !lastNameIsEmpty && !phoneNumberIsEmpty && !websiteIsEmpty &&
                                !countryIsEmpty && !genderIsEmpty && !careerTitleIsEmpty;

                // updating user's new info into the database, into SharedPreferences and into utilities
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

            }
            // here we need to set the case when all information is completed
            else if (!personalInformationHasBeenModified) {
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
        binding.countrySpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        // setting element's color
        binding.countrySpinner.setPopupBackgroundResource(dropDownTheme);
        binding.genderSpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        binding.genderSpinner.setPopupBackgroundResource(dropDownTheme);

        setEditTextColor(binding.firstNameField, color);
        setEditTextColor(binding.lastNameField, color);
        setEditTextColor(binding.phoneField, color);
        setEditTextColor(binding.websiteField, color);
        setEditTextColor(binding.careerTitleField, color);

        setBirthDateTextStyle(color);
        setUpdateProfileButtonStyle(darkThemeEnabled);
    }

    private void setUpdateProfileButtonStyle(final boolean darkThemeEnabled) {
        final int buttonBackground = darkThemeEnabled ? R.drawable.button_white_border : R.drawable.button_blue_border;

        final int buttonTextColor = darkThemeEnabled ? Color.WHITE : getColor(R.color.turkish_sea);

        binding.updateProfileButton.setBackgroundResource(buttonBackground);
        binding.updateProfileButton.setTextColor(buttonTextColor);
    }

    private void setVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.edit_account_remastered_activity);
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);
        viewModel = new EditAccountViewModel(getApplication(), userDetails);

        binding.setActivity(this);
        binding.setFragmentManager(getSupportFragmentManager());
        binding.setViewModel(viewModel);
    }
}