package com.example.economy_manager.main_part.viewmodels;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.login_part.views.LogInActivity;
import com.example.economy_manager.main_part.dialogs.ChangePasswordCustomDialog;
import com.example.economy_manager.main_part.dialogs.DeleteAccountCustomDialog;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SettingsViewModel extends AndroidViewModel {
    private UserDetails userDetails;

    public SettingsViewModel(final @NonNull Application application,
                             final UserDetails userDetails) {
        super(application);

        this.userDetails = userDetails;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public void changePasswordHandler(final @NonNull Activity currentActivity,
                                      final String oldPassword,
                                      final String newPassword) {
        if (!oldPassword.trim().isEmpty() && !newPassword.trim().isEmpty()) {
            final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

            if (user != null && MyCustomVariables.getFirebaseAuth().getUid() != null) {
                final String email = user.getEmail();

                if (email != null) {
                    final AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

                    user.reauthenticate(credential).addOnCompleteListener((final Task<Void> task) -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener((final Task<Void> task1) -> {
                                if (task1.isSuccessful()) {
                                    MyCustomMethods.showShortMessage(currentActivity,
                                            currentActivity.getResources().getString(R.string.settings_password_updated));
                                }
                            });
                        } else {
                            MyCustomMethods.showShortMessage(currentActivity,
                                    currentActivity.getResources().getString(R.string.settings_email_password_no_match));
                        }
                    });
                }
            }
        } else if (oldPassword.trim().isEmpty() &&
                newPassword.trim().isEmpty()) {
            MyCustomMethods.showShortMessage(currentActivity,
                    currentActivity.getResources().getString(R.string.settings_passwords_not_empty));
        } else if (oldPassword.trim().isEmpty()) {
            MyCustomMethods.showShortMessage(currentActivity,
                    currentActivity.getResources().getString(R.string.settings_old_password_not_empty));
        } else {
            MyCustomMethods.showShortMessage(currentActivity,
                    currentActivity.getResources().getString(R.string.settings_new_password_not_empty));
        }

        MyCustomMethods.closeTheKeyboard(currentActivity);
    }

    public void deleteAccountHandler(final @NonNull Activity currentActivity) {
        if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null) {
            // initializing credentials for Facebook authentication
            AuthCredential credential = FacebookAuthProvider
                    .getCredential(String.valueOf(AccessToken.getCurrentAccessToken()));

            // if the authentication provider is Google
            if (MyCustomVariables.getFirebaseAuth()
                    .getCurrentUser()
                    .getProviderData()
                    .get(MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData().size() - 1)
                    .getProviderId()
                    .equals("google.com")) {
                final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(currentActivity);

                if (account != null) {
                    credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                }
            }

            MyCustomVariables.getFirebaseAuth()
                    .getCurrentUser()
                    .reauthenticate(credential)
                    .addOnCompleteListener((final Task<Void> task) -> {
                        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .child("PersonalTransactions")
                                    .removeValue();
                        }

                        MyCustomVariables.getFirebaseAuth()
                                .getCurrentUser()
                                .delete()
                                .addOnCompleteListener((final Task<Void> task1) -> {
                                    MyCustomVariables.getFirebaseAuth().signOut();
                                    currentActivity.finishAffinity();
                                    currentActivity.startActivity(new Intent(currentActivity, LogInActivity.class));
                                    currentActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    MyCustomMethods.showShortMessage(currentActivity,
                                            currentActivity.getResources().getString(R.string.settings_account_deleted));
                                });
                    });
        }
    }

    public void deleteAccountWithPasswordHandler(final @NonNull Activity currentActivity,
                                                 final String password) {
        if (!password.trim().isEmpty()) {
            final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

            if (user != null &&
                    MyCustomVariables.getFirebaseAuth().getUid() != null) {
                final String email = user.getEmail();

                if (email != null) {
                    final AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    user.reauthenticate(credential).addOnCompleteListener((final Task<Void> task) -> {
                        if (task.isSuccessful()) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .removeValue();

                            user.delete().addOnCompleteListener((final Task<Void> task1) -> {
                                MyCustomVariables.getFirebaseAuth().signOut();
                                currentActivity.finishAffinity();
                                currentActivity.startActivity(new Intent(currentActivity, LogInActivity.class));
                                currentActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                MyCustomMethods.showShortMessage(currentActivity,
                                        currentActivity.getResources().getString(R.string.settings_account_deleted));
                            });
                        } else
                            MyCustomMethods.showShortMessage(currentActivity,
                                    currentActivity.getResources().getString(R.string.settings_email_password_no_match));
                        MyCustomMethods.closeTheKeyboard(currentActivity);
                    });
                }
            }
        } else {
            MyCustomMethods.showShortMessage(currentActivity, currentActivity.getResources().getString(R.string.signup_error4));
        }
    }

    public int getTextColor(final Context context) {
        final boolean checked = getUserDetails() != null ?
                getUserDetails().getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        return !checked ? context.getColor(R.color.turkish_sea) : Color.WHITE;
    }

    public int getTheme() {
        final boolean darkThemeEnabled = getUserDetails() != null ?
                getUserDetails().getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        return !darkThemeEnabled ? R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift;
    }

    public void onChangePasswordButtonClicked(final FragmentManager fragmentManager,
                                              final View view) {
        final Context context = view.getContext();

        if (context != null) {
            new AlertDialog.Builder(context)
                    .setTitle(context.getResources().getString(R.string.settings_warning).trim())
                    .setMessage(context.getResources().getString(R.string.settings_change_password).trim())
                    .setPositiveButton(context.getResources().getString(R.string.settings_yes).trim(),
                            (final DialogInterface dialog, final int which) -> {
                                final ChangePasswordCustomDialog changePasswordCustomDialog =
                                        new ChangePasswordCustomDialog();

                                changePasswordCustomDialog.show(fragmentManager, "example dialog");
                            })
                    .setNegativeButton(context.getResources().getString(R.string.cancel).trim(),
                            (final DialogInterface dialog, final int which) -> {

                            })
                    .show();
        }
    }

    public void onDeleteAccountButtonClicked(final FragmentManager fragmentManager,
                                             final View view) {
        final Context context = view.getContext();

        if (context != null) {
            new AlertDialog.Builder(context)
                    .setTitle(context.getResources().getString(R.string.settings_warning).trim())
                    .setMessage(context.getResources().getString(R.string.settings_delete_account).trim())
                    .setPositiveButton(context.getResources().getString(R.string.settings_yes).trim(),
                            (final DialogInterface dialog, final int which) -> {
                                int choice = 0;

                                if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null &&
                                        !MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData()
                                                .get(MyCustomVariables.getFirebaseAuth().getCurrentUser()
                                                        .getProviderData().size() - 1)
                                                .getProviderId().equals("password")) {
                                    choice = 2;
                                }
                                final DeleteAccountCustomDialog dialogClass = new DeleteAccountCustomDialog(choice);

                                dialogClass.show(fragmentManager, "example dialog");
                            })
                    .setNegativeButton(context.getResources().getString(R.string.cancel).trim(),
                            (final DialogInterface dialog, final int which) -> {

                            })
                    .show();
        }
    }

    public void onResetDatabaseButtonClicked(final FragmentManager fragmentManager,
                                             final View view) {
        final Context context = view.getContext();

        if (context != null) {
            new AlertDialog.Builder(context)
                    .setTitle(context.getResources().getString(R.string.settings_warning).trim())
                    .setMessage(context.getResources().getString(R.string.settings_reset_database).trim())
                    .setPositiveButton(context.getResources().getString(R.string.settings_yes).trim(),
                            (final DialogInterface dialog, final int which) -> {
                                int choice = 1;

                                if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null &&
                                        !MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData()
                                                .get(MyCustomVariables.getFirebaseAuth().getCurrentUser()
                                                        .getProviderData().size() - 1)
                                                .getProviderId().equals("password")) {
                                    choice = 3;
                                }
                                final DeleteAccountCustomDialog dialogClass = new DeleteAccountCustomDialog(choice);

                                dialogClass.show(fragmentManager, "example dialog");
                            })
                    .setNegativeButton(context.getResources().getString(R.string.cancel).trim(),
                            (final DialogInterface dialog, final int which) -> {

                            })
                    .show();
        }
    }

    public void resetDatabaseHandler(final @NonNull Activity currentActivity) {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null &&
                MyCustomVariables.getFirebaseAuth().getCurrentUser() != null) {
            // initializing credentials for Facebook authentication
            AuthCredential credential = FacebookAuthProvider
                    .getCredential(String.valueOf(AccessToken.getCurrentAccessToken()));

            // if the authentication provider is Google
            if (MyCustomVariables.getFirebaseAuth().getCurrentUser()
                    .getProviderData()
                    .get(MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData().size() - 1)
                    .getProviderId()
                    .equals("google.com")) {
                final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(currentActivity);

                if (account != null) {
                    credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                }
            }

            MyCustomVariables.getFirebaseAuth()
                    .getCurrentUser()
                    .reauthenticate(credential)
                    .addOnCompleteListener((final Task<Void> task) -> {
                        if (task.isSuccessful()) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .child("PersonalTransactions")
                                    .removeValue();

                            MyCustomMethods.showShortMessage(currentActivity,
                                    currentActivity.getResources().getString(R.string.settings_database_reset));
                        } else {
                            MyCustomMethods.showShortMessage(currentActivity,
                                    currentActivity.getResources().getString(R.string.settings_email_password_no_match));
                            MyCustomMethods.closeTheKeyboard(currentActivity);
                        }
                    });
        }
    }

    public void resetDatabaseWithPasswordHandler(final @NonNull Activity currentActivity,
                                                 final String password) {
        if (!password.trim().isEmpty()) {
            final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

            if (user != null &&
                    MyCustomVariables.getFirebaseAuth().getUid() != null) {
                final String email = user.getEmail();

                if (email != null) {
                    final AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    user.reauthenticate(credential).addOnCompleteListener((final Task<Void> task) -> {
                        if (task.isSuccessful()) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .child("PersonalTransactions")
                                    .removeValue();

                            MyCustomMethods.showShortMessage(currentActivity,
                                    currentActivity.getResources().getString(R.string.settings_database_reset));
                        } else {
                            MyCustomMethods.showShortMessage(currentActivity,
                                    currentActivity.getResources().getString(R.string.settings_email_password_no_match));
                        }

                        MyCustomMethods.closeTheKeyboard(currentActivity);
                    });
                }
            }
        } else {
            MyCustomMethods.showShortMessage(currentActivity, currentActivity.getResources().getString(R.string.signup_error4));
        }
    }

    public void saveSelectedCurrencyHandler(final String selectedCurrency,
                                            final SharedPreferences preferences) {
        final String currencySymbol = selectedCurrency.equals("AUD") ?
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

        final String userId = MyCustomVariables.getFirebaseAuth().getUid();

        final String previousCurrency = getUserDetails().getApplicationSettings().getCurrency();

        if (userId != null && getUserDetails() != null && !previousCurrency.equals(selectedCurrency)) {
            getUserDetails().getApplicationSettings().setCurrency(selectedCurrency);

            MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences, getUserDetails());
            MyCustomVariables.setUserDetails(getUserDetails());

            MyCustomVariables.getDatabaseReference()
                    .child(userId)
                    .child("ApplicationSettings")
                    .child("currency")
                    .setValue(selectedCurrency);

            MyCustomVariables.getDatabaseReference()
                    .child(userId)
                    .child("ApplicationSettings")
                    .child("currencySymbol")
                    .setValue(currencySymbol);
        }
    }
}