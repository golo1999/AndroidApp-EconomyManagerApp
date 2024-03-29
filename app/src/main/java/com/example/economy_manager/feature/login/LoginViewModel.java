package com.example.economy_manager.feature.login;

import android.app.Activity;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.LoginActivityBinding;
import com.example.economy_manager.feature.mainscreen.MainScreenActivity;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginViewModel extends ViewModel {

    private final ObservableField<String> enteredEmail = new ObservableField<>("");
    private final ObservableField<String> enteredPassword = new ObservableField<>("");

    private final LoginActivityBinding binding;

    public LoginViewModel(final LoginActivityBinding binding) {
        this.binding = binding;
    }

    public ObservableField<String> getEnteredEmail() {
        return enteredEmail;
    }

    public void setEnteredEmail(String enteredEmail) {
        this.enteredEmail.set(enteredEmail);
    }

    public ObservableField<String> getEnteredPassword() {
        return enteredPassword;
    }

    public void setEnteredPassword(String enteredPassword) {
        this.enteredPassword.set(enteredPassword);
    }

    public void goToActivityInDirection(final @NonNull Activity currentActivity,
                                        final @NonNull Class<? extends Activity> nextActivity,
                                        final int direction) {
        MyCustomMethods.goToActivityWithSlideTransition(currentActivity, nextActivity, direction);
    }

    public void goToTheMainScreen(final @NonNull Activity currentActivity) {
        MyCustomMethods.showShortMessage(currentActivity,
                currentActivity.getResources().getString(R.string.login_successful));
        MyCustomMethods.signInWithFadeTransition(currentActivity, MainScreenActivity.class);
    }

    public void onLogInButtonClicked(final @NonNull Activity activity,
                                     final String emailValue,
                                     final String passwordValue) {
        final boolean inputsAreValid =
                Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() && passwordValue.length() > 6;

        MyCustomMethods.closeTheKeyboard(activity);

        if (!inputsAreValid) {
            if (emailValue.isEmpty()) {
                final String error = activity.getResources().getString(R.string.should_not_be_empty,
                        activity.getResources().getString(R.string.the_email));

                binding.emailField.setError(error);
            } else if (!MyCustomMethods.emailIsValid(emailValue)) {
                binding.emailField.setError(activity.getResources()
                        .getString(R.string.email_is_not_valid));
            }

            if (passwordValue.isEmpty()) {
                final String error = activity.getResources().getString(R.string.should_not_be_empty,
                        activity.getResources().getString(R.string.the_password));

                binding.passwordField.setError(error);
            } else if (passwordValue.length() <= 6) {
                final String error =
                        activity.getResources().getString(R.string.should_have_at_least_characters,
                                activity.getResources().getString(R.string.the_password), 7);

                binding.passwordField.setError(error);
            }

            return;
        }

        MyCustomVariables.getFirebaseAuth()
                .signInWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener((final Task<AuthResult> signInTask) -> {
                    // verifying if user's email is verified if the credentials match
                    if (signInTask.isSuccessful()) {
                        if (MyCustomVariables.getFirebaseAuth().getCurrentUser() == null) {
                            return;
                        }

                        if (MyCustomVariables.getFirebaseAuth().getCurrentUser().isEmailVerified()) {
                            setUserDetailsToSharedPreferences(activity);
                            goToTheMainScreen(activity);
                        } else {
                            MyCustomMethods.showShortMessage(activity, activity.getResources().
                                    getString(R.string.please_verify_your_email));
                            setEnteredPassword("");
                        }
                    }
                    // removing the entered password
                    // and showing message if the credentials don't match
                    else {
                        MyCustomMethods.showShortMessage(activity, activity.getResources()
                                .getString(R.string.incorrect_username_or_password));
                        setEnteredPassword("");
                    }
                });
    }

    private void setUserDetailsToSharedPreferences(final @NonNull Activity parentActivity) {
        if (MyCustomVariables.getFirebaseAuth().getCurrentUser() == null ||
                MyCustomVariables.getFirebaseAuth().getUid() == null) {
            return;
        }

        MyCustomVariables.getDatabaseReference()
                .child(MyCustomVariables.getFirebaseAuth().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            return;
                        }

                        final UserDetails userDetails = snapshot.getValue(UserDetails.class);

                        if (userDetails == null) {
                            return;
                        }

                        MyCustomMethods.saveObjectToSharedPreferences(parentActivity,
                                userDetails, "currentUserDetails");
                        MyCustomVariables.setUserDetails(userDetails);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}