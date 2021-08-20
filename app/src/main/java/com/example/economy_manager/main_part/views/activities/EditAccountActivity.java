package com.example.economy_manager.main_part.views.activities;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.viewmodels.EditAccountViewModel;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditAccountActivity extends AppCompatActivity {
    private EditAccountViewModel editAccountViewModel;

    private UserDetails userDetails;

    private ImageView goBack;

    private CircleImageView accountPhoto;

    private TextView topText;

    private EditText firstName;

    private EditText lastName;

    private EditText phoneNumber;

    private EditText website;

    private EditText birthDate;

    private EditText careerTitle;

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
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);
        editAccountViewModel = new EditAccountViewModel(getApplication(), userDetails);
        goBack = findViewById(R.id.editAccountRemasteredBack);
        firstName = findViewById(R.id.editAccountRemasteredFirstNameField);
        lastName = findViewById(R.id.editAccountRemasteredLastNameField);
        phoneNumber = findViewById(R.id.editAccountRemasteredPhoneField);
        website = findViewById(R.id.editAccountRemasteredWebsiteField);
        birthDate = findViewById(R.id.editAccountRemasteredBirthDateField);
        careerTitle = findViewById(R.id.editAccountRemasteredCareerTitleField);
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

        final String birthDateHint =
                userDetails.getPersonalInformation().getBirthDate().toString().trim().isEmpty() || userDetails == null ?
                        getResources().getString(R.string.edit_account_birth_date).trim() :
                        userDetails.getPersonalInformation().getBirthDate().toString().trim();

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

        firstName.setHint(firstNameHint);

        lastName.setHint(lastNameHint);

        phoneNumber.setHint(phoneNumberHint);

        website.setHint(websiteHint);

        countrySpinner.setSelection(countrySpinnerSelection);

        genderSpinner.setSelection(genderSpinnerSelection);

        birthDate.setHint(birthDateHint);

        careerTitle.setHint(careerTitleHint);
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

        setEditTextColor(firstName, color);
        setEditTextColor(lastName, color);
        setEditTextColor(phoneNumber, color);
        setEditTextColor(website, color);
        setEditTextColor(birthDate, color);
        setEditTextColor(careerTitle, color);

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

                if (userDetails != null) {
                    if (userDetails.getApplicationSettings().getDarkTheme() != darkThemeEnabled) {
                        darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                    }

                    final int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;
                    // setting elements' text color based on the selected theme
                    ((TextView) v).setTextColor(itemsColor);
                }

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
                if (userDetails != null) {
                    if (userDetails.getApplicationSettings().getDarkTheme() != darkThemeEnabled) {
                        darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                    }

                    final int color = !darkThemeEnabled ? getColor(R.color.turkish_sea) : Color.WHITE;
                    // centering spinner's first item's text and setting its color based on the selected theme
                    ((TextView) parent.getChildAt(0)).setTextColor(color);
                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.START);
                }
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

                if (userDetails != null) {
                    if (userDetails.getApplicationSettings().getDarkTheme() != darkThemeEnabled) {
                        darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                    }

                    final int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;
                    // setting elements' text color based on the selected theme
                    ((TextView) v).setTextColor(itemsColor);
                }

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
}