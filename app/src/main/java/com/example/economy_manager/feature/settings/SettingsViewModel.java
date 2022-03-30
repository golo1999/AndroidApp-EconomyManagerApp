package com.example.economy_manager.feature.settings;

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
import com.example.economy_manager.dialog.ChangePasswordCustomDialog;
import com.example.economy_manager.dialog.DeleteAccountCustomDialog;
import com.example.economy_manager.feature.login.LoginActivity;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.utility.Theme;
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

                if (email == null) {
                    return;
                }

                user.reauthenticate(EmailAuthProvider.getCredential(email, oldPassword))
                        .addOnCompleteListener((final Task<Void> reAuthenticateTask) -> {
                            if (reAuthenticateTask.isSuccessful()) {
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener((final Task<Void> updatePasswordTask) -> {
                                            if (!updatePasswordTask.isSuccessful()) {
                                                return;
                                            }

                                            MyCustomMethods.showShortMessage(currentActivity,
                                                    currentActivity.getResources().getString(R.string.password_updated_successfully));
                                        });
                            } else {
                                MyCustomMethods.showShortMessage(currentActivity,
                                        currentActivity.getResources().getString(R.string.email_and_password_do_not_match));
                            }
                        });
            }
        } else if (oldPassword.trim().isEmpty() &&
                newPassword.trim().isEmpty()) {
            MyCustomMethods.showShortMessage(currentActivity,
                    currentActivity.getResources().getString(R.string.should_not_be_empty,
                            currentActivity.getResources().getString(R.string.passwords)));
        } else if (oldPassword.trim().isEmpty()) {
            MyCustomMethods.showShortMessage(currentActivity,
                    currentActivity.getResources().getString(R.string.should_not_be_empty,
                            currentActivity.getResources().getString(R.string.old_password)));
        } else {
            MyCustomMethods.showShortMessage(currentActivity,
                    currentActivity.getResources().getString(R.string.should_not_be_empty,
                            currentActivity.getResources().getString(R.string.new_password)));
        }

        MyCustomMethods.closeTheKeyboard(currentActivity);
    }

    public void deleteAccountHandler(final @NonNull Activity currentActivity) {
        if (MyCustomVariables.getFirebaseAuth().getCurrentUser() == null) {
            return;
        }

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
                .addOnCompleteListener((final Task<Void> reAuthenticateTask) -> {
                    if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                        MyCustomVariables.getDatabaseReference()
                                .child(MyCustomVariables.getFirebaseAuth().getUid())
                                .child("PersonalTransactions")
                                .removeValue();
                    }

                    MyCustomVariables.getFirebaseAuth()
                            .getCurrentUser()
                            .delete()
                            .addOnCompleteListener((final Task<Void> deleteTask) -> {
                                if (!deleteTask.isSuccessful()) {
                                    return;
                                }

                                MyCustomVariables.getFirebaseAuth().signOut();
                                currentActivity.finishAffinity();
                                currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class));
                                currentActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                MyCustomMethods.showShortMessage(currentActivity,
                                        currentActivity.getResources().getString(R.string.the_account_was_successfully_deleted));
                            });
                });
    }

    public void deleteAccountWithPasswordHandler(final @NonNull Activity currentActivity,
                                                 final String password) {
        if (!password.trim().isEmpty()) {
            final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

            if (user == null || MyCustomVariables.getFirebaseAuth().getUid() == null) {
                return;
            }

            final String email = user.getEmail();

            if (email == null) {
                return;
            }

            user.reauthenticate(EmailAuthProvider.getCredential(email, password))
                    .addOnCompleteListener((final Task<Void> reAuthenticateTask) -> {
                        if (reAuthenticateTask.isSuccessful()) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .removeValue();

                            user.delete().addOnCompleteListener((final Task<Void> deleteTask) -> {
                                if (!deleteTask.isSuccessful()) {
                                    return;
                                }

                                MyCustomVariables.getFirebaseAuth().signOut();
                                currentActivity.finishAffinity();
                                currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class));
                                currentActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                MyCustomMethods.showShortMessage(currentActivity,
                                        currentActivity.getResources().getString(R.string.the_account_was_successfully_deleted));
                            });
                        } else
                            MyCustomMethods.showShortMessage(currentActivity,
                                    currentActivity.getResources().getString(R.string.email_and_password_do_not_match));
                        MyCustomMethods.closeTheKeyboard(currentActivity);
                    });
        } else {
            MyCustomMethods.showShortMessage(currentActivity,
                    currentActivity.getResources().getString(R.string.should_not_be_empty,
                            currentActivity.getResources().getString(R.string.password)));
        }
    }

    public int getTextColor(final Context context) {
        final boolean checked = userDetails != null ?
                userDetails.getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

        return !checked ? context.getColor(R.color.turkish_sea) : Color.WHITE;
    }

    public int getTheme(final Context context) {
        final boolean isDarkThemeEnabled = userDetails != null ?
                userDetails.getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

        return Theme.getBackgroundColor(context, isDarkThemeEnabled);
    }

    public void onChangePasswordButtonClicked(final FragmentManager fragmentManager,
                                              final View view) {
        final Context context = view.getContext();

        if (context == null) {
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.warning).trim())
                .setMessage(context.getResources().getString(R.string.are_you_sure_you_want_to_change_your_password).trim())
                .setPositiveButton(context.getResources().getString(R.string.yes).trim(),
                        (final DialogInterface dialog, final int which) ->
                                new ChangePasswordCustomDialog().show(fragmentManager, "example dialog"))
                .setNegativeButton(context.getResources().getString(R.string.cancel).trim(),
                        (final DialogInterface dialog, final int which) -> {

                        })
                .show();
    }

    public void onDeleteAccountButtonClicked(final FragmentManager fragmentManager,
                                             final View view) {
        final Context context = view.getContext();

        if (context == null) {
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.warning).trim())
                .setMessage(context.getResources().getString(R.string.are_you_sure_you_want_to_delete_your_account).trim())
                .setPositiveButton(context.getResources().getString(R.string.yes).trim(),
                        (final DialogInterface dialog, final int which) -> {
                            int choice = 0;

                            if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null &&
                                    !MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData()
                                            .get(MyCustomVariables.getFirebaseAuth().getCurrentUser()
                                                    .getProviderData().size() - 1)
                                            .getProviderId().equals("password")) {
                                choice = 2;
                            }

                            new DeleteAccountCustomDialog(choice).show(fragmentManager, "example dialog");
                        })
                .setNegativeButton(context.getResources().getString(R.string.cancel).trim(),
                        (final DialogInterface dialog, final int which) -> {

                        })
                .show();
    }

    public void onResetDatabaseButtonClicked(final FragmentManager fragmentManager,
                                             final View view) {
        final Context context = view.getContext();

        if (context == null) {
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.warning).trim())
                .setMessage(context.getResources().getString(R.string.are_you_sure_you_want_to_reset_the_database).trim())
                .setPositiveButton(context.getResources().getString(R.string.yes).trim(),
                        (final DialogInterface dialog, final int which) -> {
                            int choice = 1;

                            if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null &&
                                    !MyCustomVariables.getFirebaseAuth().getCurrentUser().getProviderData()
                                            .get(MyCustomVariables.getFirebaseAuth().getCurrentUser()
                                                    .getProviderData().size() - 1)
                                            .getProviderId().equals("password")) {
                                choice = 3;
                            }

                            new DeleteAccountCustomDialog(choice).show(fragmentManager, "example dialog");
                        })
                .setNegativeButton(context.getResources().getString(R.string.cancel).trim(),
                        (final DialogInterface dialog, final int which) -> {

                        })
                .show();
    }

    public void resetDatabaseHandler(final @NonNull Activity currentActivity) {
        if (MyCustomVariables.getFirebaseAuth().getUid() == null ||
                MyCustomVariables.getFirebaseAuth().getCurrentUser() == null) {
            return;
        }

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
                .addOnCompleteListener((final Task<Void> reAuthenticateTask) -> {
                    if (reAuthenticateTask.isSuccessful()) {
                        MyCustomVariables.getDatabaseReference()
                                .child(MyCustomVariables.getFirebaseAuth().getUid())
                                .child("PersonalTransactions")
                                .removeValue();

                        MyCustomMethods.showShortMessage(currentActivity,
                                currentActivity.getResources().getString(R.string.the_database_was_reset_successfully));
                    } else {
                        MyCustomMethods.showShortMessage(currentActivity,
                                currentActivity.getResources().getString(R.string.email_and_password_do_not_match));
                        MyCustomMethods.closeTheKeyboard(currentActivity);
                    }
                });
    }

    public void resetDatabaseWithPasswordHandler(final @NonNull Activity currentActivity,
                                                 final String password) {
        if (!password.trim().isEmpty()) {
            final FirebaseUser user = MyCustomVariables.getFirebaseAuth().getCurrentUser();

            if (user == null || MyCustomVariables.getFirebaseAuth().getUid() == null) {
                return;
            }

            final String email = user.getEmail();

            if (email == null) {
                return;
            }

            user.reauthenticate(EmailAuthProvider.getCredential(email, password))
                    .addOnCompleteListener((final Task<Void> reAuthenticateTask) -> {
                        if (reAuthenticateTask.isSuccessful()) {
                            MyCustomVariables.getDatabaseReference()
                                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                                    .child("PersonalTransactions")
                                    .removeValue();

                            MyCustomMethods.showShortMessage(currentActivity,
                                    currentActivity.getResources().getString(R.string.the_database_was_reset_successfully));
                        } else {
                            MyCustomMethods.showShortMessage(currentActivity,
                                    currentActivity.getResources().getString(R.string.email_and_password_do_not_match));
                        }

                        MyCustomMethods.closeTheKeyboard(currentActivity);
                    });
        } else {
            MyCustomMethods.showShortMessage(currentActivity,
                    currentActivity.getResources().getString(R.string.should_not_be_empty,
                            currentActivity.getResources().getString(R.string.password)));
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

        if (userId == null ||
                userDetails == null ||
                userDetails.getApplicationSettings().getCurrency().equals(selectedCurrency)) {
            return;
        }

        userDetails.getApplicationSettings().setCurrency(selectedCurrency);

        MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences, userDetails);
        MyCustomVariables.setUserDetails(userDetails);

        MyCustomVariables.getDatabaseReference()
                .child(userId)
                .child("applicationSettings")
                .child("currency")
                .setValue(selectedCurrency);

        MyCustomVariables.getDatabaseReference()
                .child(userId)
                .child("applicationSettings")
                .child("currencySymbol")
                .setValue(currencySymbol);
    }
}