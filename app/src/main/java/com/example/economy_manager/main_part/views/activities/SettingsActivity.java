package com.example.economy_manager.main_part.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.economy_manager.R;
import com.example.economy_manager.login_part.LogInActivity;
import com.example.economy_manager.main_part.dialogs.ChangePasswordCustomDialog;
import com.example.economy_manager.main_part.dialogs.DeleteAccountCustomDialog;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SettingsActivity extends AppCompatActivity
        implements DeleteAccountCustomDialog.CustomDialogListener,
        ChangePasswordCustomDialog.CustomDialogListener {
    private UserDetails userDetails;
    private SharedPreferences preferences;
    private ImageView goBack;
    private Button resetDatabase;
    private Button deleteAccount;
    private Button changePassword;
    private Spinner currencySelector;
    private TextView themeText;
    private TextView currencyText;
    private SwitchCompat darkThemeSwitch;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        setVariables();
        setTheme();
        setDarkThemeSwitch(darkThemeSwitch);
        setTexts();
        setOnClickListeners();
        createCurrencySpinner();
        setSpinners();
        setSelectedItemColorSpinner();
        hideButtons();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveSelectedCurrency();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setVariables() {
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        goBack = findViewById(R.id.settings_back);
        resetDatabase = findViewById(R.id.settings_delete_database);
        deleteAccount = findViewById(R.id.settings_delete_button);
        changePassword = findViewById(R.id.settings_change_password_button);
        currencySelector = findViewById(R.id.settings_currency_spinner);
        currencyText = findViewById(R.id.settings_currency_text);
        themeText = findViewById(R.id.settings_theme_text);
        darkThemeSwitch = findViewById(R.id.settings_dark_theme_switch);
    }

    private void setTheme() {
        final boolean darkThemeEnabled = userDetails != null ?
                userDetails.getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        getWindow().setBackgroundDrawableResource(!darkThemeEnabled ?
                R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift);
    }

    private void setOnClickListeners() {
        changePassword.setOnClickListener(v -> {
            if (v.getContext() != null) {
                final Context context = v.getContext();

                new AlertDialog.Builder(context)
                        .setTitle(getResources().getString(R.string.settings_warning).trim())
                        .setMessage(getResources().getString(R.string.settings_change_password).trim())
                        .setPositiveButton(getResources().getString(R.string.settings_yes).trim(),
                                (dialog, which) -> {
                                    final ChangePasswordCustomDialog dialogChangePassword1 =
                                            new ChangePasswordCustomDialog();

                                    dialogChangePassword1.show(getSupportFragmentManager(), "example dialog");
                                })
                        .setNegativeButton(getResources().getString(R.string.cancel).trim(),
                                (dialog, which) -> {

                                }).show();
            }
        });

        deleteAccount.setOnClickListener(v -> {
            if (v.getContext() != null) {
                final Context context = v.getContext();

                new AlertDialog.Builder(context)
                        .setTitle(getResources().getString(R.string.settings_warning).trim())
                        .setMessage(getResources().getString(R.string.settings_delete_account).trim())
                        .setPositiveButton(getResources().getString(R.string.settings_yes).trim(),
                                (dialog, which) -> {
                                    int choice = 0;

                                    if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null &&
                                            !MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData()
                                                    .get(MyCustomVariables.getFirebaseAuth().getCurrentUser()
                                                            .getProviderData().size() - 1)
                                                    .getProviderId().equals("password")) {
                                        choice = 2;
                                    }
                                    final DeleteAccountCustomDialog dialogClass = new DeleteAccountCustomDialog(choice);

                                    dialogClass.show(getSupportFragmentManager(), "example dialog");
                                })
                        .setNegativeButton(getResources().getString(R.string.cancel).trim(),
                                (dialog, which) -> {

                                }).show();
            }
        });

        goBack.setOnClickListener(v -> onBackPressed());

        resetDatabase.setOnClickListener(v -> {
            if (v.getContext() != null) {
                final Context context = v.getContext();

                new AlertDialog.Builder(context)
                        .setTitle(getResources().getString(R.string.settings_warning).trim())
                        .setMessage(getResources().getString(R.string.settings_reset_database).trim())
                        .setPositiveButton(getResources().getString(R.string.settings_yes).trim(),
                                (dialog, which) -> {
                                    int choice = 1;

                                    if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null &&
                                            !MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData()
                                                    .get(MyCustomVariables.getFirebaseAuth().getCurrentUser()
                                                            .getProviderData().size() - 1)
                                                    .getProviderId().equals("password")) {
                                        choice = 3;
                                    }
                                    final DeleteAccountCustomDialog dialogClass = new DeleteAccountCustomDialog(choice);

                                    dialogClass.show(getSupportFragmentManager(), "example dialog");
                                })
                        .setNegativeButton(getResources().getString(R.string.cancel).trim(),
                                (dialog, which) -> {

                                }).show();
            }
        });
    }

    private void setSpinners() {
        Log.d("userDetailsInSettings", userDetails.toString());

        final boolean darkThemeEnabled = userDetails != null ?
                userDetails.getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        final String currency = userDetails != null ?
                userDetails.getApplicationSettings().getCurrency() : MyCustomVariables.getDefaultCurrency();
        // turkish sea (blue) or white
        final int color = !darkThemeEnabled ? Color.parseColor("#195190") : Color.WHITE;

        final int dropDownTheme = !darkThemeEnabled ?
                R.drawable.ic_blue_gradient_unloved_teen : R.drawable.ic_white_gradient_tobacco_ad;

        // setting arrow color
        currencySelector.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        // setting elements' color
        currencySelector.setPopupBackgroundResource(dropDownTheme);

        currencySelector.setSelection(currency.equals("AUD") ?
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

        darkThemeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                MyCustomVariables.getDatabaseReference()
                        .child(MyCustomVariables.getFirebaseAuth().getUid())
                        .child("ApplicationSettings")
                        .child("darkTheme")
                        .setValue(isChecked);

                userDetails.getApplicationSettings().setDarkTheme(isChecked);
                MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences, userDetails);

                MyCustomVariables.setUserDetails(userDetails);

                setTheme();
                setTexts();
                //setCurrencySpinnerTheme();
            }
        });
    }

    @Override
    public void changePassword(final String oldPassword,
                               final String newPassword) {
        if (!oldPassword.trim().isEmpty() &&
                !newPassword.trim().isEmpty()) {
            final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

            if (user != null &&
                    MyCustomVariables.getFirebaseAuth().getUid() != null) {
                final String email = user.getEmail();

                if (email != null) {
                    final AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    MyCustomMethods.showShortMessage(this,
                                            getResources().getString(R.string.settings_password_updated));
                                }
                            });
                        } else {
                            MyCustomMethods.showShortMessage(this,
                                    getResources().getString(R.string.settings_email_password_no_match));
                        }
                    });
                }
            }
        } else if (oldPassword.trim().isEmpty() &&
                newPassword.trim().isEmpty()) {
            MyCustomMethods.showShortMessage(this,
                    getResources().getString(R.string.settings_passwords_not_empty));
        } else if (oldPassword.trim().isEmpty()) {
            MyCustomMethods.showShortMessage(this,
                    getResources().getString(R.string.settings_old_password_not_empty));
        } else {
            MyCustomMethods.showShortMessage(this,
                    getResources().getString(R.string.settings_new_password_not_empty));
        }
        MyCustomMethods.closeTheKeyboard(this);
    }

    @Override
    public void getTypedPasswordAndDeleteTheAccount(final String password) {
        if (!password.trim().isEmpty()) {
            final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

            if (user != null &&
                    MyCustomVariables.getFirebaseAuth().getUid() != null) {
                final String email = user.getEmail();

                if (email != null) {
                    final AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .removeValue();

                            user.delete().addOnCompleteListener(task1 -> {
                                MyCustomVariables.getFirebaseAuth().signOut();
                                finishAffinity();
                                startActivity(new Intent(SettingsActivity.this, LogInActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                MyCustomMethods.showShortMessage(this,
                                        getResources().getString(R.string.settings_account_deleted));
                            });
                        } else
                            MyCustomMethods.showShortMessage(this,
                                    getResources().getString(R.string.settings_email_password_no_match));
                        MyCustomMethods.closeTheKeyboard(this);
                    });
                }
            }
        } else {
            MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error4));
        }
    }

    @Override
    public void deleteAccount() {
        if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null) {
            // initializam acreditarea (credential) pentru autentificare cu facebook
            AuthCredential credential = FacebookAuthProvider
                    .getCredential(String.valueOf(AccessToken.getCurrentAccessToken()));

            // in cazul in care providerul de autentificare este google
            if (MyCustomVariables.getFirebaseAuth()
                    .getCurrentUser()
                    .getProviderData()
                    .get(MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData().size() - 1)
                    .getProviderId()
                    .equals("google.com")) {
                final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

                if (account != null) {
                    credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                }
            }

            MyCustomVariables.getFirebaseAuth()
                    .getCurrentUser()
                    .reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .child("PersonalTransactions")
                                    .removeValue();
                        }

                        MyCustomVariables.getFirebaseAuth()
                                .getCurrentUser()
                                .delete()
                                .addOnCompleteListener(task1 -> {
                                    MyCustomVariables.getFirebaseAuth().signOut();
                                    finishAffinity();
                                    startActivity(new Intent(SettingsActivity.this, LogInActivity.class));
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    MyCustomMethods.showShortMessage(this,
                                            getResources().getString(R.string.settings_account_deleted));
                                });
                    });
        }
    }

    @Override
    public void getTypedPasswordAndResetTheDatabase(final String password) {
        if (!password.trim().isEmpty()) {
            final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

            if (user != null &&
                    MyCustomVariables.getFirebaseAuth().getUid() != null) {
                final String email = user.getEmail();

                if (email != null) {
                    final AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .child("PersonalTransactions")
                                    .removeValue();

                            MyCustomMethods.showShortMessage(this,
                                    getResources().getString(R.string.settings_database_reset));
                        } else {
                            MyCustomMethods.showShortMessage(this,
                                    getResources().getString(R.string.settings_email_password_no_match));
                        }

                        MyCustomMethods.closeTheKeyboard(this);
                    });
                }
            }
        } else {
            MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error4));
        }
    }

    @Override
    public void resetDatabase() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null &&
                MyCustomVariables.getFirebaseAuth().getCurrentUser() != null) {
            // initializam acreditarea (credential) pentru autentificare cu facebook
            AuthCredential credential = FacebookAuthProvider
                    .getCredential(String.valueOf(AccessToken.getCurrentAccessToken()));

            // in cazul in care providerul de autentificare este google
            if (MyCustomVariables.getFirebaseAuth().getCurrentUser()
                    .getProviderData()
                    .get(MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData().size() - 1)
                    .getProviderId()
                    .equals("google.com")) {
                final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

                if (account != null) {
                    credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                }
            }

            MyCustomVariables.getFirebaseAuth()
                    .getCurrentUser()
                    .reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .child("PersonalTransactions")
                                    .removeValue();

                            MyCustomMethods.showShortMessage(this,
                                    getResources().getString(R.string.settings_database_reset));
                        } else {
                            MyCustomMethods.showShortMessage(this,
                                    getResources().getString(R.string.settings_email_password_no_match));
                            MyCustomMethods.closeTheKeyboard(this);
                        }
                    });
        }
    }

    private void setTexts() {
        final boolean checked = userDetails != null ?
                userDetails.getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        final int color = !checked ? Color.parseColor("#195190") : Color.WHITE;

        themeText.setTextColor(color);
        currencyText.setTextColor(color);
    }

    private void createCurrencySpinner() {
        final String[] currencyList = getResources().getStringArray(R.array.currencies);

        final ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item, currencyList) {
            @Override
            public View getDropDownView(final int position,
                                        final @Nullable View convertView,
                                        final @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setGravity(Gravity.CENTER);
                setCurrencySpinnerTheme(v);

                return v;
            }
        };

        currencySelector.setAdapter(currencyAdapter);
    }

    private void setCurrencySpinnerTheme(final View v) {
        final boolean darkThemeEnabled = userDetails != null ?
                userDetails.getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        final int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;
        // setting elements' text color based on the selected theme
        ((TextView) v).setTextColor(itemsColor);
    }

    // styling spinner's first element
    private void setSelectedItemColorSpinner() {
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent,
                                       final View view,
                                       final int position,
                                       long id) {
                if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                    final boolean darkThemeEnabled = userDetails != null ?
                            userDetails.getApplicationSettings().getDarkTheme() :
                            MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

                    final int color = !darkThemeEnabled ? Color.parseColor("#195190") : Color.WHITE;
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

        currencySelector.setOnItemSelectedListener(itemSelectedListener);
    }

    private void setDarkThemeSwitch(final SwitchCompat darkThemeSwitch) {
        final boolean darkThemeEnabled = userDetails != null ?
                userDetails.getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        darkThemeSwitch.setChecked(darkThemeEnabled);
    }

    private void hideButtons() {
        final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

        if (user != null) {
            final String authProvider = user
                    .getProviderData()
                    .get(user.getProviderData().size() - 1)
                    .getProviderId();

            switch (authProvider) {
                case "google.com":
                case "facebook.com":
                    changePassword.setVisibility(View.GONE);
                    break;
                //case "password":
                //break;
            }
        }
    }

    private void saveSelectedCurrency() {
        final String selectedCurrency = String.valueOf(currencySelector.getSelectedItem());

        final String currencySymbol;

        currencySymbol = selectedCurrency.equals("AUD") ?
                "A$" : selectedCurrency.equals("BRL") ?
                "R$" : selectedCurrency.equals("CAD") ?
                "C$" : selectedCurrency.equals("CHF") ?
                "CHF" : selectedCurrency.equals("CNY") ?
                "元" : selectedCurrency.equals("EUR") ?
                "€" : selectedCurrency.equals("GBP") ?
                "£" : selectedCurrency.equals("INR") ?
                "₹" : selectedCurrency.equals("JPY") ?
                "¥" : selectedCurrency.equals("RON") ?
                "RON" : selectedCurrency.equals("RUB") ?
                "₽" : "$";

        if (MyCustomVariables.getFirebaseAuth().getUid() != null &&
                userDetails != null &&
                !userDetails.getApplicationSettings().getCurrency().equals(selectedCurrency)) {
            userDetails.getApplicationSettings().setCurrency(selectedCurrency);
            MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences, userDetails);

            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("ApplicationSettings")
                    .child("currency")
                    .setValue(selectedCurrency);

            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("ApplicationSettings")
                    .child("currencySymbol")
                    .setValue(currencySymbol);
        }
    }
}