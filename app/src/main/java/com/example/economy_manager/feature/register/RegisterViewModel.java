package com.example.economy_manager.feature.register;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.RegisterActivityBinding;
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

    private final RegisterActivityBinding binding;
    private SharedPreferences preferences;
    private final UserDetails userDetails = new UserDetails();

    public RegisterViewModel(final RegisterActivityBinding binding) {
        this.binding = binding;
    }

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

        if (currentUserID == null) {
            return;
        }

        userDetails.setPersonalInformation(information);

        MyCustomVariables.getDatabaseReference()
                .child(currentUserID)
                .child("personalInformation")
                .setValue(information);
    }

    private void createApplicationSettingsPath() {
        final ApplicationSettings settings = new ApplicationSettings();
        final String currentUserID = MyCustomVariables.getFirebaseAuth().getUid();

        if (currentUserID == null) {
            return;
        }

        userDetails.setApplicationSettings(settings);

        MyCustomVariables.getDatabaseReference()
                .child(currentUserID)
                .child("applicationSettings")
                .setValue(settings);
    }

    public void onSignUpButtonClicked(final @NonNull Activity activity,
                                      final String emailValue,
                                      final String passwordValue) {
        final boolean inputsAreValid = passwordValue.length() > 6 && MyCustomMethods.emailIsValid(emailValue);

        MyCustomMethods.closeTheKeyboard(activity);

        if (!inputsAreValid) {
            if (emailValue.isEmpty()) {
                final String error = activity.getResources().getString(R.string.should_not_be_empty,
                        activity.getResources().getString(R.string.email));

                binding.emailField.setError(error);
            } else if (!MyCustomMethods.emailIsValid(emailValue)) {
                binding.emailField.setError(activity.getResources().getString(R.string.email_address_is_not_valid));
            }

            if (passwordValue.isEmpty()) {
                final String error = activity.getResources().getString(R.string.should_not_be_empty,
                        activity.getResources().getString(R.string.password));

                binding.passwordField.setError(error);
            } else if (passwordValue.length() <= 6) {
                final String error = activity.getResources().getString(R.string.should_have_at_least_characters,
                        activity.getResources().getString(R.string.password), 7);

                binding.passwordField.setError(error);
            }

            return;
        }

        // calling Firebase's register with email and password method
        MyCustomVariables.getFirebaseAuth()
                .createUserWithEmailAndPassword(emailValue, passwordValue).addOnCompleteListener(createUserTask -> {
            // if the account can be created
            if (createUserTask.isSuccessful()) {
                final FirebaseUser firebaseUser = MyCustomVariables.getFirebaseAuth().getCurrentUser();

                if (firebaseUser == null) {
                    return;
                }

                firebaseUser.sendEmailVerification().addOnCompleteListener(sendEmailVerificationTask -> {
                    if (sendEmailVerificationTask.isSuccessful()) {
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
            // if the email address already exists into the database
            else {
                binding.emailField.setError(activity.getResources().getString(R.string.email_already_exists));
            }
        });
    }
}