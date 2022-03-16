package com.example.economy_manager.feature.login;

import android.app.Activity;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.feature.mainscreen.MainScreenActivity;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LogInViewModel extends ViewModel {

    private final ObservableField<String> enteredEmail = new ObservableField<>("");

    private final ObservableField<String> enteredPassword = new ObservableField<>("");

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
        MyCustomMethods.showShortMessage(currentActivity, currentActivity.getResources().getString(R.string.login_successful));
        MyCustomMethods.signInWithFadeTransition(currentActivity, MainScreenActivity.class);
    }

    public void onLogInButtonClicked(final @NonNull Activity activity,
                                     final String emailValue,
                                     final String passwordValue) {
        // proceeding to login if the email is valid & the password has got at least 7 characters
        if (Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() &&
                passwordValue.length() >= 7) {
            MyCustomVariables.getFirebaseAuth()
                    .signInWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener((final Task<AuthResult> task) -> {
                        MyCustomMethods.closeTheKeyboard(activity);
                        // verifying if user's email is verified if the credentials match
                        if (task.isSuccessful()) {
                            if (MyCustomVariables.getFirebaseAuth().getCurrentUser() == null) {
                                return;
                            }

                            if (MyCustomVariables.getFirebaseAuth().getCurrentUser().isEmailVerified()) {
                                setUserDetailsToSharedPreferences(activity);
                                goToTheMainScreen(activity);
                            } else {
                                MyCustomMethods.showShortMessage(activity,
                                        activity.getResources().getString(R.string.verify_email));
                                setEnteredPassword("");
                            }
                        }
                        // removing the entered password & showing message if the credentials don't match
                        else {
                            MyCustomMethods.showShortMessage(activity,
                                    activity.getResources().getString(R.string.login_incorrect_username_password));
                            setEnteredPassword("");
                        }
                    });
        }
        // showing error messages if the login condition isn't respected
        else {
            MyCustomMethods.closeTheKeyboard(activity);
            // if both the email & the password are empty
            if (emailValue.isEmpty() && passwordValue.isEmpty()) {
                MyCustomMethods.showShortMessage(activity, activity.getResources().getString(R.string.signup_error2));
            }
            // if the email is empty
            else if (emailValue.isEmpty()) {
                MyCustomMethods.showShortMessage(activity, activity.getResources().getString(R.string.signup_error3));
                setEnteredPassword("");
            }
            // if the password is empty
            else if (passwordValue.isEmpty()) {
                MyCustomMethods.showShortMessage(activity, activity.getResources().getString(R.string.signup_error4));
            }
            // if the email isn't valid & the password is too short
            else if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() && passwordValue.length() < 4) {
                MyCustomMethods.showShortMessage(activity, activity.getResources().getString(R.string.signup_error5));
                setEnteredPassword("");
            }
            // if the email isn't valid & the password is
            else if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
                MyCustomMethods.showShortMessage(activity, activity.getResources().getString(R.string.email_not_valid));
                setEnteredPassword("");
            }
            // if the email is valid & the password is too short
            else {
                MyCustomMethods.showShortMessage(activity, activity.getResources().getString(R.string.signup_error7));
                setEnteredPassword("");
            }
        }
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