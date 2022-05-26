package com.example.economy_manager.feature.editprofile;

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
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProfileActivity
        extends AppCompatActivity {

    private EditProfileActivityBinding binding;
    private EditProfileViewModel viewModel;
    private UserDetails userDetails;
    private boolean isDarkThemeEnabled;
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

        final String retrievedFirstName = String.valueOf(retrievedPersonalInformation.get("firstName"));
        final String retrievedLastName = String.valueOf(retrievedPersonalInformation.get("lastName"));
        final String retrievedPhoneNumber = String.valueOf(retrievedPersonalInformation.get("phoneNumber"));
        final String retrievedWebsite = String.valueOf(retrievedPersonalInformation.get("website"));
        final int countrySpinnerSelection = Integer.parseInt(String.
                valueOf(retrievedPersonalInformation.get("countrySpinnerSelection")));
        final int genderSpinnerSelection = Integer.parseInt(String.
                valueOf(retrievedPersonalInformation.get("genderSpinnerSelection")));
        final String retrievedCareerTitle = String.valueOf(retrievedPersonalInformation.get("careerTitle"));

        binding.photo.setBorderColor(accountPhotoBorderColor);
        viewModel.setFirstName(retrievedFirstName);
        viewModel.setLastName(retrievedLastName);
        viewModel.setPhoneNumber(retrievedPhoneNumber);
        viewModel.setWebsite(retrievedWebsite);
        binding.countrySpinner.setSelection(countrySpinnerSelection);
        binding.genderSpinner.setSelection(genderSpinnerSelection);
        viewModel.setCareerTitle(retrievedCareerTitle);
    }

    private void setActivityVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.edit_profile_activity);
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

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void setProfilePhoto() {
        final String photoURL = String.valueOf(viewModel.getPersonalInformation(this).get("photoURL"));
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
                new ArrayAdapter<String>(this, R.layout.custom_spinner_item, spinnerType.equals("COUNTRY_SPINNER") ?
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
                        v.setBackgroundColor(getColor(isDarkThemeEnabled ? R.color.primaryLight : R.color.primaryDark));
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
        final AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent,
                                       final View view,
                                       final int position,
                                       final long id) {
                if (userDetails == null) {
                    userDetails = MyCustomVariables.getDefaultUserDetails();
                }

                if (userDetails.getApplicationSettings().isDarkThemeEnabled() != isDarkThemeEnabled) {
                    isDarkThemeEnabled = userDetails.getApplicationSettings().isDarkThemeEnabled();
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

        final int color = getColor(isDarkThemeEnabled ? R.color.secondaryDark : R.color.quaternaryLight);

        // setting arrow color
        binding.countrySpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        binding.genderSpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }
}