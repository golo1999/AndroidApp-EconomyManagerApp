package com.example.economy_manager.feature.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.LoginActivityBinding;
import com.example.economy_manager.feature.forgotpassword.ForgotPasswordActivity;
import com.example.economy_manager.feature.mainscreen.MainScreenActivity;
import com.example.economy_manager.feature.register.RegisterActivity;
import com.example.economy_manager.model.ApplicationSettings;
import com.example.economy_manager.model.PersonalInformation;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityBinding binding;
    private LoginViewModel viewModel;
    private CallbackManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityVariables();
        setLayoutVariables();
        initializeFacebookLogin();
        setOnClickListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setActivityVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        viewModel = new LoginViewModel(binding);
    }

    private void setLayoutVariables() {
        binding.setActivity(this);
        binding.setForgotPasswordActivity(ForgotPasswordActivity.class);
        binding.setRegisterActivity(RegisterActivity.class);
        binding.setViewModel(viewModel);
    }

    private void setOnClickListeners() {
        binding.facebookLogInButton.setOnClickListener((final View view) -> onFacebookLogInButtonClicked());
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode,
                                    final @Nullable Intent data) {
        manager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initializeFacebookLogin() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        manager = CallbackManager.Factory.create();
        binding.facebookLogInButton.setReadPermissions(Arrays.asList("email", "public_profile"));
    }

    private void onFacebookLogInButtonClicked() {
        LoginManager.getInstance().registerCallback(manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());

        MyCustomVariables.getFirebaseAuth()
                .signInWithCredential(credential)
                .addOnCompleteListener((final Task<AuthResult> task) -> {
                    if (!task.isSuccessful()) {
                        MyCustomMethods.showShortMessage(this,
                                getResources().getString(R.string.facebook_authentication_failed));

                        return;
                    }

                    if (MyCustomVariables.getFirebaseAuth().getCurrentUser() == null) {
                        return;
                    }

                    createPersonalInformationPath();
                    createApplicationSettingsPath();
                    goToTheMainScreen();
                });
    }

    private void createPersonalInformationPath() {
        final PersonalInformation information = new PersonalInformation();
        final String currentUserID = MyCustomVariables.getFirebaseAuth().getUid();

        if (currentUserID == null) {
            return;
        }

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

        MyCustomVariables.getDatabaseReference()
                .child(currentUserID)
                .child("applicationSettings")
                .setValue(settings);
    }

    private void goToTheMainScreen() {
        MyCustomMethods.showShortMessage(this,
                getResources().getString(R.string.login_successful));
        MyCustomMethods.signInWithFadeTransition(LoginActivity.this,
                MainScreenActivity.class);
    }
}