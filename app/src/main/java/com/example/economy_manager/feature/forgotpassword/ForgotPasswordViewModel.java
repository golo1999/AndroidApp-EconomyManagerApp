package com.example.economy_manager.feature.forgotpassword;

import android.app.Activity;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.feature.register.SignUpActivity;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.android.gms.tasks.Task;

public class ForgotPasswordViewModel extends ViewModel {
    private String enteredEmail = "";

    public String getEnteredEmail() {
        return enteredEmail;
    }

    public void setEnteredEmail(String enteredEmail) {
        this.enteredEmail = enteredEmail;
    }

    public void onResetPasswordButtonClicked(final @NonNull Activity currentActivity,
                                             final String emailText) {
        MyCustomMethods.closeTheKeyboard(currentActivity);

        if (Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            MyCustomVariables.getFirebaseAuth()
                    .sendPasswordResetEmail(emailText)
                    .addOnCompleteListener((final Task<Void> task) -> {
                        if (task.isSuccessful()) {
                            MyCustomMethods.showLongMessage(currentActivity,
                                    currentActivity.getResources().getString(R.string.verify_email));
                            MyCustomMethods.finishActivityWithSlideTransition(currentActivity,
                                    1);
                        } else {
                            MyCustomMethods.showShortMessage(currentActivity,
                                    currentActivity.getResources().getString(R.string.forgot_password_email_does_not_exist));
                        }
                    });
        } else {
            MyCustomMethods.showShortMessage(currentActivity, emailText.isEmpty() ?
                    currentActivity.getResources().getString(R.string.signup_error3) :
                    currentActivity.getResources().getString(R.string.email_not_valid));
        }
    }

    public void onSignUpButtonClicked(final @NonNull Activity currentActivity) {
        MyCustomMethods.goToActivityWithSlideTransition(currentActivity, SignUpActivity.class, 1);
    }
}