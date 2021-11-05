package com.example.economy_manager.main_part.views.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.SettingsActivityBinding;
import com.example.economy_manager.main_part.adapters.CurrenciesSpinnerAdapter;
import com.example.economy_manager.main_part.dialogs.ChangePasswordCustomDialog;
import com.example.economy_manager.main_part.dialogs.DeleteAccountCustomDialog;
import com.example.economy_manager.main_part.viewmodels.SettingsViewModel;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity
        implements DeleteAccountCustomDialog.CustomDialogListener,
        ChangePasswordCustomDialog.CustomDialogListener {
    private SettingsActivityBinding binding;
    private SettingsViewModel viewModel;
    private SharedPreferences preferences;

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
        setAllButtonsStyle();
        setDarkThemeSwitch(binding.themeSwitch);
        setTexts();
        createCurrencySelectorSpinner();
        setSpinners();
        setSelectedItemColorSpinner();
        hideButtons();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSelectedCurrency();
    }

    @Override
    public void changePassword(final String oldPassword,
                               final String newPassword) {
        viewModel.changePasswordHandler(this, oldPassword, newPassword);
    }

    @Override
    public void deleteAccount() {
        viewModel.deleteAccountHandler(this);
    }

    @Override
    public void getTypedPasswordAndDeleteTheAccount(final String password) {
        viewModel.deleteAccountWithPasswordHandler(this, password);
    }

    @Override
    public void getTypedPasswordAndResetTheDatabase(final String password) {
        viewModel.resetDatabaseWithPasswordHandler(this, password);
    }

    @Override
    public void resetDatabase() {
        viewModel.resetDatabaseHandler(this);
    }

    private void createCurrencySelectorSpinner() {
        final String[] currenciesList = getResources().getStringArray(R.array.currencies);

        final CurrenciesSpinnerAdapter adapter =
                new CurrenciesSpinnerAdapter(this, R.layout.custom_spinner_item, currenciesList);

        binding.currencySpinner.setAdapter(adapter);
    }

    private void hideButtons() {
        final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

        if (user != null) {
            final String authProvider = user
                    .getProviderData()
                    .get(user.getProviderData().size() - 1)
                    .getProviderId();

            if (authProvider.equals("google.com") || authProvider.equals("facebook.com")) {
                binding.changePasswordButton.setVisibility(View.GONE);
            }
        }
    }

    private void saveSelectedCurrency() {
        final String selectedCurrency = String.valueOf(binding.currencySpinner.getSelectedItem());

        viewModel.saveSelectedCurrencyHandler(selectedCurrency, preferences);
    }

    private void setAllButtonsStyle() {
        setButtonStyle(binding.rateButton, viewModel.getUserDetails() != null &&
                viewModel.getUserDetails().getApplicationSettings().getDarkTheme());
        setButtonStyle(binding.changePasswordButton, viewModel.getUserDetails() != null &&
                viewModel.getUserDetails().getApplicationSettings().getDarkTheme());
        setButtonStyle(binding.resetDatabaseButton, viewModel.getUserDetails() != null &&
                viewModel.getUserDetails().getApplicationSettings().getDarkTheme());
        setButtonStyle(binding.deleteAccountButton, viewModel.getUserDetails() != null &&
                viewModel.getUserDetails().getApplicationSettings().getDarkTheme());
    }

    private void setButtonStyle(final Button button,
                                final boolean darkThemeEnabled) {
        button.setBackgroundResource(!darkThemeEnabled ? R.drawable.button_blue_border : R.drawable.button_white_border);
        button.setTextColor(getColor(!darkThemeEnabled ? R.color.turkish_sea : R.color.white));
    }

    public void setCurrencySelectorSpinnerTheme(final View v) {
        final boolean darkThemeEnabled = viewModel.getUserDetails() != null ?
                viewModel.getUserDetails().getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        final int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;
        // setting elements' text color based on the selected theme
        ((TextView) v).setTextColor(itemsColor);
    }

    private void setDarkThemeSwitch(final SwitchCompat darkThemeSwitch) {
        final boolean darkThemeEnabled = viewModel.getUserDetails() != null ?
                viewModel.getUserDetails().getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        darkThemeSwitch.setChecked(darkThemeEnabled);
    }

    /**
     * Method for styling spinner's first item
     */
    private void setSelectedItemColorSpinner() {
        final AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent,
                                       final View view,
                                       final int position,
                                       long id) {
                if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                    final int color = viewModel.getTextColor(SettingsActivity.this);
                    // the first element will have the text aligned to its start and
                    // the color based on the selected theme
                    ((TextView) parent.getChildAt(0)).setTextColor(color);
                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.END);
                    ((TextView) parent.getChildAt(0)).setTypeface(null, Typeface.BOLD);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        binding.currencySpinner.setOnItemSelectedListener(itemSelectedListener);
    }

    private void setSpinners() {
        final boolean darkThemeEnabled = viewModel.getUserDetails() != null ?
                viewModel.getUserDetails().getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        final String currency = viewModel.getUserDetails() != null ?
                viewModel.getUserDetails().getApplicationSettings().getCurrency() : MyCustomVariables.getDefaultCurrency();
        // turkish sea (blue) or white
        final int color = viewModel.getTextColor(this);

        final int dropDownTheme = !darkThemeEnabled ?
                R.drawable.ic_blue_gradient_unloved_teen : R.drawable.ic_white_gradient_tobacco_ad;

        // setting arrow color
        binding.currencySpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        // setting elements' color
        binding.currencySpinner.setPopupBackgroundResource(dropDownTheme);

        binding.currencySpinner.setSelection(currency.equals("AUD") ?
                0 : currency.equals("BRL") ?
                1 : currency.equals("CAD") ?
                2 : currency.equals("CHF") ?
                3 : currency.equals("CNY") ?
                4 : currency.equals("EUR") ?
                5 : currency.equals("GBP") ?
                6 : currency.equals("INR") ?
                7 : currency.equals("JPY") ?
                8 : currency.equals("RON") ?
                9 : currency.equals("RUB") ?
                10 : 11);

        binding.themeSwitch.setOnCheckedChangeListener((final CompoundButton buttonView, final boolean isChecked) -> {
            if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                MyCustomVariables.getDatabaseReference()
                        .child(MyCustomVariables.getFirebaseAuth().getUid())
                        .child("ApplicationSettings")
                        .child("darkTheme")
                        .setValue(isChecked);

                viewModel.getUserDetails().getApplicationSettings().setDarkTheme(isChecked);
                MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences, viewModel.getUserDetails());

                MyCustomVariables.setUserDetails(viewModel.getUserDetails());
                MyCustomMethods.restartCurrentActivity(this);
            }
        });
    }

    private void setTexts() {
        final int color = viewModel.getTextColor(this);

        binding.themeText.setTextColor(color);
        binding.currencyText.setTextColor(color);
    }

    private void setTheme() {
        getWindow().setBackgroundDrawableResource(viewModel.getTheme());
    }

    private void setVariables() {
        final UserDetails userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);

        binding = DataBindingUtil.setContentView(this, R.layout.settings_activity);
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        viewModel = new SettingsViewModel(getApplication(), userDetails);

        binding.setActivity(this);
        binding.setFragmentManager(getSupportFragmentManager());
        binding.setViewModel(viewModel);
    }
}