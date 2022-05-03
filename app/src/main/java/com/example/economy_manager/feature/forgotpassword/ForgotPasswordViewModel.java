package com.example.economy_manager.feature.forgotpassword;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.ForgotPasswordActivityBinding;
import com.example.economy_manager.feature.register.RegisterActivity;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.android.gms.tasks.Task;

public class ForgotPasswordViewModel extends ViewModel {

    private String enteredEmail = "";
    private final ForgotPasswordActivityBinding binding;

    public ForgotPasswordViewModel(final ForgotPasswordActivityBinding binding) {
        this.binding = binding;
    }

    public String getEnteredEmail() {
        return enteredEmail;
    }

    public void setEnteredEmail(String enteredEmail) {
        this.enteredEmail = enteredEmail;
    }

    public void onResetPasswordButtonClicked(final @NonNull Activity currentActivity,
                                             final String emailText) {
        MyCustomMethods.closeTheKeyboard(currentActivity);

        if (MyCustomVariables.getFirebaseAuth() == null) {
            return;
        }

        if (!MyCustomMethods.emailIsValid(emailText)) {
            final String error = emailText.isEmpty() ?
                    currentActivity.getResources().getString(R.string.should_not_be_empty,
                            currentActivity.getResources().getString(R.string.email)) :
                    currentActivity.getResources().getString(R.string.email_address_is_not_valid);

            binding.emailField.setError(error);

            return;
        }

        MyCustomVariables.getFirebaseAuth()
                .sendPasswordResetEmail(emailText)
                .addOnCompleteListener((final Task<Void> sendPasswordResetEmailTask) -> {
                    if (sendPasswordResetEmailTask.isSuccessful()) {
                        MyCustomMethods.showLongMessage(currentActivity,
                                currentActivity.getResources().getString(R.string.please_verify_your_email));
                        MyCustomMethods.finishActivityWithSlideTransition(currentActivity,
                                1);
                    } else {
                        binding.emailField.setError(currentActivity.getResources()
                                .getString(R.string.email_does_not_exist_in_the_database));
                    }
                });
    }

    public void onSignUpButtonClicked(final @NonNull Activity currentActivity) {
        MyCustomMethods.goToActivityWithSlideTransition(currentActivity, RegisterActivity.class, 1);
    }
}