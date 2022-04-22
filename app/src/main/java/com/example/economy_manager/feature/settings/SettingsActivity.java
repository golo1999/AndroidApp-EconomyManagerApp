package com.example.economy_manager.feature.settings;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.DataBindingUtil;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.SettingsActivityBinding;
import com.example.economy_manager.dialog.ChangePasswordCustomDialog;
import com.example.economy_manager.dialog.DeleteAccountCustomDialog;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.CurrenciesSpinnerAdapter;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
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
        setActivityVariables();
        setLayoutVariables();
        setDarkThemeSwitch(binding.themeSwitch);
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

        if (user == null) {
            return;
        }

        final String authProvider = user
                .getProviderData()
                .get(user.getProviderData().size() - 1)
                .getProviderId();

        if (!authProvider.equals("google.com") && !authProvider.equals("facebook.com")) {
            return;
        }

        binding.changePasswordButton.setVisibility(View.GONE);
    }

    private void saveSelectedCurrency() {
        viewModel.saveSelectedCurrencyHandler(String.valueOf(binding.currencySpinner.getSelectedItem()), preferences);
    }

    private void setActivityVariables() {
        final UserDetails userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);

        binding = DataBindingUtil.setContentView(this, R.layout.settings_activity);
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        viewModel = new SettingsViewModel(getApplication(), userDetails);
    }

    public void setCurrencySelectorSpinnerTheme(final View v) {
        final boolean isDarkThemeEnabled = viewModel.getUserDetails() != null ?
                viewModel.getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();
        final int itemBackgroundColor = getColor(isDarkThemeEnabled ? R.color.primaryLight : R.color.primaryDark);
        final int itemTextColor = getColor(isDarkThemeEnabled ? R.color.quaternaryLight : R.color.secondaryDark);

        ((TextView) v).setTextColor(itemTextColor);
        v.setBackgroundColor(itemBackgroundColor);
    }

    private void setDarkThemeSwitch(final SwitchCompat darkThemeSwitch) {
        final boolean isDarkThemeEnabled = viewModel.getUserDetails() != null ?
                viewModel.getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

        darkThemeSwitch.setChecked(isDarkThemeEnabled);
    }

    private void setLayoutVariables() {
        binding.setActivity(this);
        binding.setFragmentManager(getSupportFragmentManager());
        binding.setIsDarkThemeEnabled(viewModel.getUserDetails().getApplicationSettings().isDarkThemeEnabled());
        binding.setViewModel(viewModel);
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
                if (MyCustomVariables.getFirebaseAuth().getUid() == null) {
                    return;
                }

                final int itemTextColor = getColor(viewModel.getUserDetails().getApplicationSettings().isDarkThemeEnabled() ?
                        R.color.secondaryDark : R.color.quaternaryLight);

                // the first element will have the text aligned to its start and
                // the color based on the selected theme
                ((TextView) parent.getChildAt(0)).setTextColor(itemTextColor);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.END);
                ((TextView) parent.getChildAt(0)).setTypeface(null, Typeface.BOLD);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        binding.currencySpinner.setOnItemSelectedListener(itemSelectedListener);
    }

    private void setSpinners() {
        final boolean isDarkThemeEnabled = viewModel.getUserDetails() != null ?
                viewModel.getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();
        final String currency = viewModel.getUserDetails() != null ?
                viewModel.getUserDetails().getApplicationSettings().getCurrency() :
                MyCustomVariables.getDefaultCurrency();
        final int arrowColor = getColor(isDarkThemeEnabled ? R.color.secondaryDark : R.color.quaternaryLight);

        // setting arrow color
        binding.currencySpinner.getBackground().setColorFilter(arrowColor, PorterDuff.Mode.SRC_ATOP);

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
            if (MyCustomVariables.getFirebaseAuth().getUid() == null) {
                return;
            }

            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("applicationSettings")
                    .child("darkThemeEnabled")
                    .setValue(isChecked);

            viewModel.getUserDetails().getApplicationSettings().setDarkThemeEnabled(isChecked);
            MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences, viewModel.getUserDetails());

            MyCustomVariables.setUserDetails(viewModel.getUserDetails());
            MyCustomMethods.restartCurrentActivity(this);
        });
    }
}