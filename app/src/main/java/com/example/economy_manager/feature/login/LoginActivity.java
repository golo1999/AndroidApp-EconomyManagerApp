package com.example.economy_manager.feature.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityBinding binding;
    private LoginViewModel viewModel;
    private CallbackManager manager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 27;

    // 0 for Facebook and 1 for Google
    private int googleOrFacebookInsideOnActivityResult = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVariables();
        setGoogleButtonSize();
        setGoogleRequest();
        initializeFacebookLogin();
        setOnClickListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.setActivity(this);
        binding.setForgotPasswordActivity(ForgotPasswordActivity.class);
        binding.setRegisterActivity(RegisterActivity.class);
        binding.setViewModel(viewModel);
    }

    private void setOnClickListeners() {
        binding.facebookLogInButton.setOnClickListener((final View view) -> onFacebookLogInButtonClicked());

        binding.googleLogInButton.setOnClickListener((final View v) -> {
            googleOrFacebookInsideOnActivityResult = 1;
            googleSignIn();
        });
    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode,
                                    final @Nullable Intent data) {
        manager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //if(task.getResult(ApiException.class)==null)
                //Toast.makeText(this, "task null", Toast.LENGTH_SHORT).show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account); // in documentatia de la google e account.getIdToken()
                    //Toast.makeText(this, "account!=null", Toast.LENGTH_SHORT).show();
                }
                //else Toast.makeText(this, "account==null", Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                //Toast.makeText(this, "Error in onActivityResult", Toast.LENGTH_SHORT).show();
                //e.printStackTrace();
                //Toast.makeText(LogIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
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

    private void setGoogleButtonSize() {
        binding.googleLogInButton.setSize(SignInButton.SIZE_STANDARD);
    }

    private void setGoogleRequest() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("236665611938-1kbke01jbeuq0pfa5n5n4ggt279qrkaj.apps.googleusercontent.com").requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        MyCustomVariables.getFirebaseAuth()
                .signInWithCredential(credential)
                .addOnCompleteListener((final Task<AuthResult> task) -> {
                    if (!task.isSuccessful()) {
                        MyCustomMethods.showShortMessage(this,
                                getResources().getString(R.string.google_authentication_failed));

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
        MyCustomMethods.showShortMessage(this, getResources().getString(R.string.login_successful));
        MyCustomMethods.signInWithFadeTransition(LoginActivity.this, MainScreenActivity.class);
    }
}