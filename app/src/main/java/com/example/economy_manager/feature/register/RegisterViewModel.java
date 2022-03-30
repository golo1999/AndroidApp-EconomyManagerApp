package com.example.economy_manager.feature.register;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.model.ApplicationSettings;
import com.example.economy_manager.model.PersonalInformation;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.auth.FirebaseUser;

public class RegisterViewModel extends ViewModel {
    private final ObservableField<String> enteredEmail = new ObservableField<>("");

    private final ObservableField<String> enteredPassword = new ObservableField<>("");

    private SharedPreferences preferences;

    private final UserDetails userDetails = new UserDetails();

    public ObservableField<String> getEnteredEmail() {
        return enteredEmail;
    }

    public ObservableField<String> getEnteredPassword() {
        return enteredPassword;
    }

    public void setEnteredPassword(String enteredPassword) {
        this.enteredPassword.set(enteredPassword);
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    private void createPersonalInformationPath() {
        final PersonalInformation information = new PersonalInformation();
        final String currentUserID = MyCustomVariables.getFirebaseAuth().getUid();

        userDetails.setPersonalInformation(information);

        if (currentUserID != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(currentUserID)
                    .child("personalInformation")
                    .setValue(information);
        }
    }

    private void createApplicationSettingsPath() {
        final ApplicationSettings settings = new ApplicationSettings();
        final String currentUserID = MyCustomVariables.getFirebaseAuth().getUid();

        userDetails.setApplicationSettings(settings);

        if (currentUserID != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(currentUserID)
                    .child("applicationSettings")
                    .setValue(settings);
        }
    }

    public void onSignUpButtonClicked(final @NonNull Activity activity,
                                      final String emailValue,
                                      final String passwordValue) {
        MyCustomMethods.closeTheKeyboard(activity);
        // if the password has got at least 7 characters and the email is valid
        if (passwordValue.length() > 6 && Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            // calling Firebase's register with email and password method
            MyCustomVariables.getFirebaseAuth()
                    .createUserWithEmailAndPassword(emailValue, passwordValue).addOnCompleteListener(task -> {
                // if the account can be created
                if (task.isSuccessful()) {
                    final FirebaseUser fbUser = MyCustomVariables.getFirebaseAuth().getCurrentUser();

                    if (fbUser != null) {
                        fbUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                MyCustomMethods.showShortMessage(activity,
                                        activity.getResources().getString(R.string.please_verify_your_email));
                                createPersonalInformationPath();
                                createApplicationSettingsPath();
                                MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(getPreferences(),
                                        getUserDetails());
                                MyCustomVariables.getFirebaseAuth().signOut();
                                activity.onBackPressed();
                            }
                        });
                    }
                }
                // showing the message & resetting the password field if the user already exists
                else {
                    MyCustomMethods.showShortMessage(activity, activity.getResources().getString(R.string.email_already_exists));

                    setEnteredPassword("");
                }
            });
        }
        // if both email and password are empty
        else if (emailValue.isEmpty() && passwordValue.isEmpty()) {
            // showing the error
            MyCustomMethods.showShortMessage(activity, activity.getResources().getString(R.string.email_and_password_should_not_be_empty));
        }
        // if only the email is empty
        else if (emailValue.isEmpty()) {
            // showing the error
            MyCustomMethods.showShortMessage(activity, activity.getResources().getString(R.string.should_not_be_empty));
            // resetting the password field
            setEnteredPassword("");
        }
        // if only the password is empty
        else if (passwordValue.isEmpty()) {
            // showing the error
            MyCustomMethods.showShortMessage(activity,
                    activity.getResources().getString(R.string.should_not_be_empty,
                            activity.getResources().getString(R.string.password)));
        }
        // if the password is too short and the email isn't valid
        else if (passwordValue.length() < 7 && !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            // showing the error
            MyCustomMethods.showShortMessage(activity, activity.getResources().getString(R.string.email_and_password_are_not_valid));
            // resetting the password field
            setEnteredPassword("");
        }
        // if the email isn't valid
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            // showing the error
            MyCustomMethods.showShortMessage(activity, activity.getResources().getString(R.string.please_enter_a_valid_email));
            // resetting the password field
            setEnteredPassword("");
        }
        // if the password is too short
        else {
            // showing the error
            MyCustomMethods.showShortMessage(activity,
                    activity.getResources().getString(R.string.should_have_at_least_characters,
                            activity.getResources().getString(R.string.password), 7));
            // resetting the password field
            setEnteredPassword("");
        }
    }
}