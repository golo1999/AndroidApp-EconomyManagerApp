package com.example.economy_manager.login_part;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.views.activities.MainScreenActivity;
import com.example.economy_manager.models.ApplicationSettings;
import com.example.economy_manager.models.BirthDate;
import com.example.economy_manager.models.PersonalInformation;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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

import java.time.LocalDate;
import java.util.Arrays;

public class LogInActivity extends AppCompatActivity {
    private TextView signUp;
    private TextView forgotPassword;
    private Button logInButton;
    private LoginButton facebookLogInButton;
    private SignInButton googleLogInButton;
    private EditText emailField;
    private EditText passwordField;
    private CallbackManager manager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 27;
    private int googleOrFacebookInsideOnActivityResult = 0; // 0 pentru facebook, 1 pentru google

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);
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
        signUp = findViewById(R.id.login_create_account);
        forgotPassword = findViewById(R.id.login_forgot_password);
        logInButton = findViewById(R.id.login_button);
        emailField = findViewById(R.id.login_email);
        passwordField = findViewById(R.id.login_password);
        facebookLogInButton = findViewById(R.id.login_button_facebook);
        googleLogInButton = findViewById(R.id.login_button_google);
    }

    private void setOnClickListeners() {
        signUp.setOnClickListener((final View v) -> {
            MyCustomMethods.goToActivityInDirection(LogInActivity.this, SignUpActivity.class, 1);
        });

        facebookLogInButton.setOnClickListener(v -> buttonClickLoginFacebook());

        forgotPassword.setOnClickListener((final View v) -> {
            MyCustomMethods.goToActivityInDirection(LogInActivity.this, ForgotPasswordActivity.class, 0);
        });

        googleLogInButton.setOnClickListener((final View v) -> {
            googleOrFacebookInsideOnActivityResult = 1;
            googleSignIn();
        });

        logInButton.setOnClickListener((final View v) -> validation(String.valueOf(emailField.getText()).trim(),
                String.valueOf(passwordField.getText())));
    }

    private void validation(final String emailValue,
                            final String passwordValue) {
        // in cazul in care email-ul este valid si parola are cel putin 7 caractere, incercam sa facem log in
        if (Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() &&
                passwordValue.length() >= 7) {
            MyCustomVariables.getFirebaseAuth()
                    .signInWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener((final Task<AuthResult> task) -> {
                        MyCustomMethods.closeTheKeyboard(this);
                        // daca atat email-ul, cat si parola corespund, se face verifica daca emailul este verificat
                        if (task.isSuccessful()) {
                            if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null) {
                                if (MyCustomVariables.getFirebaseAuth().getCurrentUser().isEmailVerified()) {
                                    goToTheMainScreen();
                                } else {
                                    Toast.makeText(LogInActivity.this, getResources().getString(R.string.verify_email),
                                            Toast.LENGTH_SHORT).show();

                                    passwordField.setText("");
                                }
                            }
                        } else {
                            // in cazul in care atat email-ul, cat si parola sunt valide,
                            // dar nu corespund informatiilor din baza de date Firebase
                            MyCustomMethods.showShortMessage(this,
                                    getResources().getString(R.string.login_incorrect_username_password));
                            passwordField.setText(""); // stergem parola
                        }
                    });
        }
        // in cazul in care nu se respecta conditia de log in
        else {
            MyCustomMethods.closeTheKeyboard(this);
            // daca atat email-ul, cat si parola nu contin niciun caracter
            if (emailValue.isEmpty() && passwordValue.isEmpty()) {
                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error2));
            }
            // daca email-ul nu contine niciun caracter
            else if (emailValue.isEmpty()) {
                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error3));
                passwordField.setText("");
            }
            // daca parola nu contine niciun caracter
            else if (passwordValue.isEmpty()) {
                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error4));
            }
            // daca email-ul nu are forma valida si parola este prea scurta
            else if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() && passwordValue.length() < 4) {
                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error5));
                passwordField.setText("");
            }
            // daca email-ul nu are forma valida, dar parola este in regula
            else if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.login_email_not_valid));
                passwordField.setText("");
            }
            // daca parola este prea scurta, dar email-ul este in regula
            else {
                MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error7));
                passwordField.setText("");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        facebookLogInButton.setReadPermissions(Arrays.asList("email", "public_profile"));
    }

    private void buttonClickLoginFacebook() {
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
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        MyCustomVariables.getFirebaseAuth().signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null) {
                    createPersonalInformationPath();
                    createApplicationSettingsPath();
                    goToTheMainScreen();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Facebook authentication failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setGoogleButtonSize() {
        googleLogInButton.setSize(SignInButton.SIZE_STANDARD);
    }

    private void setGoogleRequest() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        MyCustomVariables.getFirebaseAuth().signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null) {
                    createPersonalInformationPath();
                    createApplicationSettingsPath();
                    goToTheMainScreen();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Google authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPersonalInformationPath() {
        final PersonalInformation information = new PersonalInformation("", "", "",
                "", "", "", new BirthDate(LocalDate.now()), "", "");
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("PersonalInformation")
                    .setValue(information);
        }
    }

    private void createApplicationSettingsPath() {
        final ApplicationSettings settings = new ApplicationSettings("GBP");

        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("ApplicationSettings")
                    .setValue(settings);
        }
    }

    private void goToTheMainScreen() {
        final Intent intent = new Intent(LogInActivity.this, MainScreenActivity.class);

        Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
        finishAffinity();
        startActivity(intent); // incepem activitatea urmatoare
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}