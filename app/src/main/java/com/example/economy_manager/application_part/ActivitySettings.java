package com.example.economy_manager.application_part;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.economy_manager.CustomDialogChangePassword;
import com.example.economy_manager.CustomDialogDeleteAccount;
import com.example.economy_manager.login_part.LogIn;
import com.example.economy_manager.MyCustomMethods;
import com.example.economy_manager.MyCustomSharedPreferences;
import com.example.economy_manager.MyCustomVariables;
import com.example.economy_manager.R;
import com.example.economy_manager.UserDetails;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class ActivitySettings extends AppCompatActivity
        implements CustomDialogDeleteAccount.CustomDialogListener, CustomDialogChangePassword.CustomDialogListener {
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
        setContentView(R.layout.activity_settings);
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
        preferences = getSharedPreferences("ECONOMY_MANAGER_USER_DATA", MODE_PRIVATE);
        goBack = findViewById(R.id.settingsBack);
        resetDatabase = findViewById(R.id.settingsDeleteDatabase);
        deleteAccount = findViewById(R.id.settingsDeleteButton);
        changePassword = findViewById(R.id.settingsChangePasswordButton);
        currencySelector = findViewById(R.id.settingsCurrencySpinner);
        currencyText = findViewById(R.id.settingsCurrencyText);
        themeText = findViewById(R.id.settingsThemeText);
        darkThemeSwitch = findViewById(R.id.settingsDarkThemeSwitch);
    }

    private void setTheme() {
        if (userDetails != null) {
            final boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();

            getWindow().setBackgroundDrawableResource(!darkThemeEnabled ?
                    R.drawable.ic_white_gradient_tobacco_ad :
                    R.drawable.ic_black_gradient_night_shift);
        }
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
                                    CustomDialogChangePassword dialogChangePassword1 =
                                            new CustomDialogChangePassword();
                                    dialogChangePassword1.show(getSupportFragmentManager(),
                                            "example dialog");
                                }).setNegativeButton(getResources().getString(R.string.cancel).trim(),
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

                                    if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null)
                                        if (!MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData()
                                                .get(MyCustomVariables.getFirebaseAuth().getCurrentUser()
                                                        .getProviderData().size() - 1)
                                                .getProviderId().equals("password"))
                                            choice = 2;
                                    CustomDialogDeleteAccount dialogClass
                                            = new CustomDialogDeleteAccount(choice);
                                    dialogClass.show(getSupportFragmentManager(),
                                            "example dialog");
                                }).setNegativeButton(getResources().getString(R.string.cancel).trim(),
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
                                    if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null)
                                        if (!MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData()
                                                .get(MyCustomVariables.getFirebaseAuth().getCurrentUser()
                                                        .getProviderData().size() - 1)
                                                .getProviderId().equals("password"))
                                            choice = 3;
                                    CustomDialogDeleteAccount dialogClass =
                                            new CustomDialogDeleteAccount(choice);
                                    dialogClass.show(getSupportFragmentManager(),
                                            "example dialog");
                                }).setNegativeButton(getResources().getString(R.string.cancel).trim(),
                        (dialog, which) -> {

                        }).show();
            }
        });
    }

    private void setSpinners() {
        Log.d("userDetailsInSettings", userDetails.toString());

        if (userDetails != null) {
            final boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
            final String currency = userDetails.getApplicationSettings().getCurrency();
            final int color = !darkThemeEnabled ?
                    // turkish sea (albastru)
                    Color.parseColor("#195190") :
                    Color.WHITE;
            final int dropDownTheme = !darkThemeEnabled ?
                    R.drawable.ic_blue_gradient_unloved_teen :
                    R.drawable.ic_white_gradient_tobacco_ad;

            // setam culoarea sagetii
            currencySelector.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

            // setam culoarea elementelor
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
                    MyCustomVariables.getDatabaseReference().child(MyCustomVariables.getFirebaseAuth().getUid())
                            .child("ApplicationSettings").child("darkTheme").setValue(isChecked);
                    userDetails.getApplicationSettings().setDarkTheme(isChecked);
                    MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences,
                            userDetails);
                    setTheme();
                    setTexts();
                    //setCurrencySpinnerTheme();
                }
            });
        }
    }

    @Override
    public void changePassword(final String oldPassword, final String newPassword) {
        if (!oldPassword.trim().equals("") && !newPassword.trim().equals("")) {
            final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

            if (user != null && MyCustomVariables.getFirebaseAuth().getUid() != null) {
                final String email = user.getEmail();

                if (email != null) {
                    final AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(ActivitySettings.this,
                                            getResources().getString(R.string.settings_password_updated),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ActivitySettings.this,
                                    getResources().getString(R.string.settings_email_password_no_match),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } else if (oldPassword.trim().equals("") && newPassword.trim().equals("")) {
            Toast.makeText(this,
                    getResources().getString(R.string.settings_passwords_not_empty),
                    Toast.LENGTH_SHORT).show();
        } else if (oldPassword.trim().equals("")) {
            Toast.makeText(this,
                    getResources().getString(R.string.settings_old_password_not_empty),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    getResources().getString(R.string.settings_new_password_not_empty),
                    Toast.LENGTH_SHORT).show();
        }
        MyCustomMethods.closeTheKeyboard(this);
    }

    @Override
    public void getTypedPasswordAndDeleteTheAccount(final String password) {
        if (!password.trim().equals("")) {
            final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

            if (user != null && MyCustomVariables.getFirebaseAuth().getUid() != null) {
                final String email = user.getEmail();

                if (email != null) {
                    final AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .removeValue();

                            user.delete().addOnCompleteListener(task1 -> {
                                final Intent intent = new Intent(ActivitySettings.this,
                                        LogIn.class);
                                MyCustomVariables.getFirebaseAuth().signOut();
                                finishAffinity();
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.slide_out_left);
                                Toast.makeText(ActivitySettings.this,
                                        getResources().getString(R.string.settings_account_deleted),
                                        Toast.LENGTH_SHORT).show();
                            });
                        } else
                            Toast.makeText(ActivitySettings.this,
                                    getResources().getString(R.string.settings_email_password_no_match),
                                    Toast.LENGTH_SHORT).show();
                        MyCustomMethods.closeTheKeyboard(this);
                    });
                }
            }
        } else
            Toast.makeText(this, getResources().getString(R.string.signup_error4), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteAccount() {
        if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null) {
            // initializam acreditarea (credential) pentru autentificare cu facebook
            AuthCredential credential = FacebookAuthProvider
                    .getCredential(String.valueOf(AccessToken.getCurrentAccessToken()));

            // in cazul in care providerul de autentificare este google
            if (MyCustomVariables.getFirebaseAuth().getCurrentUser()
                    .getProviderData()
                    .get(MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData().size() - 1)
                    .getProviderId()
                    .equals("google.com")) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if (account != null)
                    credential = GoogleAuthProvider.getCredential(account.getIdToken(),
                            null);
            }

            MyCustomVariables.getFirebaseAuth().getCurrentUser().reauthenticate(credential).addOnCompleteListener(task -> {
                if (MyCustomVariables.getFirebaseAuth().getUid() != null)
                    MyCustomVariables.getDatabaseReference().child(MyCustomVariables.getFirebaseAuth().getUid()).child("PersonalTransactions").removeValue();
                MyCustomVariables.getFirebaseAuth().getCurrentUser().delete().addOnCompleteListener(task1 -> {
                    final Intent intent = new Intent(ActivitySettings.this, LogIn.class);

                    MyCustomVariables.getFirebaseAuth().signOut();
                    finishAffinity();
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    Toast.makeText(ActivitySettings.this,
                            getResources().getString(R.string.settings_account_deleted),
                            Toast.LENGTH_SHORT).show();
                });
            });
        }
    }

    @Override
    public void getTypedPasswordAndResetTheDatabase(final String password) {
        if (!password.trim().equals("")) {
            final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

            if (user != null && MyCustomVariables.getFirebaseAuth().getUid() != null) {
                final String email = user.getEmail();

                if (email != null) {
                    final AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .child("PersonalTransactions")
                                    .removeValue();

                            Toast.makeText(ActivitySettings.this,
                                    getResources().getString(R.string.settings_database_reset),
                                    Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(ActivitySettings.this,
                                    getResources().getString(R.string.settings_email_password_no_match),
                                    Toast.LENGTH_SHORT).show();
                        MyCustomMethods.closeTheKeyboard(this);
                    });
                }
            }
        } else
            Toast.makeText(this, getResources().getString(R.string.signup_error4), Toast.LENGTH_SHORT).show();
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

                if (account != null)
                    credential = GoogleAuthProvider.getCredential(account.getIdToken(),
                            null);
            }

            MyCustomVariables.getFirebaseAuth().getCurrentUser()
                    .reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    MyCustomVariables.getDatabaseReference()
                            .child(MyCustomVariables.getFirebaseAuth().getUid())
                            .child("PersonalTransactions")
                            .removeValue();

                    Toast.makeText(ActivitySettings.this,
                            getResources().getString(R.string.settings_database_reset),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ActivitySettings.this,
                            getResources().getString(R.string.settings_email_password_no_match),
                            Toast.LENGTH_SHORT).show();
                    MyCustomMethods.closeTheKeyboard(this);
                }
            });
        }
    }

    private void setTexts() {
        if (userDetails != null) {
            final boolean checked = userDetails.getApplicationSettings().getDarkTheme();
            final int color = !checked ?
                    Color.parseColor("#195190") : Color.WHITE;

            themeText.setTextColor(color);
            currencyText.setTextColor(color);
        }
    }

    private void createCurrencySpinner() {
        final String[] currencyList = getResources().getStringArray(R.array.currencies);

        final ArrayAdapter<String> currencyAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item, currencyList) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView,
                                        @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setGravity(Gravity.CENTER);
                setCurrencySpinnerTheme(v);

                return v;
            }
        };

        currencySelector.setAdapter(currencyAdapter);
    }

    private void setCurrencySpinnerTheme(final View v) {
        if (userDetails != null) {
            final boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
            final int itemsColor = !darkThemeEnabled ?
                    Color.WHITE : Color.BLACK;

            // setam culoarea textului elementelor in functie de tema selectata
            ((TextView) v).setTextColor(itemsColor);
        }
    }

    // stilizarea primului element al spinnerului
    private void setSelectedItemColorSpinner() {
        AdapterView.OnItemSelectedListener itemSelectedListener =
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(final AdapterView<?> parent, final View view, final int position,
                                               long id) {
                        if (userDetails != null && MyCustomVariables.getFirebaseAuth().getUid() != null) {
                            final boolean darkThemeEnabled = userDetails
                                    .getApplicationSettings().getDarkTheme();
                            final int color = !darkThemeEnabled ?
                                    Color.parseColor("#195190") : Color.WHITE;

                            // primul element va avea textul aliniat la stanga
                            // si culoarea in functie de tema selectata
                            ((TextView) parent.getChildAt(0)).setTextColor(color);
                            ((TextView) parent.getChildAt(0)).setGravity(Gravity.END);
                            ((TextView) parent.getChildAt(0)).setTypeface(null,
                                    Typeface.BOLD);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                };

        currencySelector.setOnItemSelectedListener(itemSelectedListener);
    }

    private void setDarkThemeSwitch(final SwitchCompat darkThemeSwitch) {
        if (userDetails != null) {
            final boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();

            darkThemeSwitch.setChecked(darkThemeEnabled);
        }
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
        String currencySymbol;

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

        if (MyCustomVariables.getFirebaseAuth().getUid() != null && userDetails != null &&
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