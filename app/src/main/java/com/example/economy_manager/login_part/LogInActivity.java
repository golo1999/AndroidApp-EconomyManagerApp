package com.example.economy_manager.login_part;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.main_part.view.activity.MainScreenActivity;
import com.example.economy_manager.model.ApplicationSettings;
import com.example.economy_manager.model.BirthDate;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.model.PersonalInformation;
import com.example.economy_manager.R;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;

import java.time.LocalDate;
import java.util.Arrays;

public class LogInActivity extends AppCompatActivity {
    private TextView signUp;
    private TextView forgotPassword;
    private TextView incorrectLogin;
    private Button logIn;
    private LoginButton facebookLogIn;
    private SignInButton googleLogIn;
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
        logIn = findViewById(R.id.login_button);
        emailField = findViewById(R.id.login_email);
        passwordField = findViewById(R.id.login_password);
        incorrectLogin = findViewById(R.id.login_incorrect_login);
        facebookLogIn = findViewById(R.id.login_button_facebook);
        googleLogIn = findViewById(R.id.login_button_google);
    }

    private void setOnClickListeners() {
        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // slide dinspre dreapta spre stanga
        });

        facebookLogIn.setOnClickListener(v -> buttonClickLoginFacebook());

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        googleLogIn.setOnClickListener(v -> {
            googleOrFacebookInsideOnActivityResult = 1;
            googleSignIn();
        });

        logIn.setOnClickListener(v -> validation(String.valueOf(emailField.getText()).trim(),
                String.valueOf(passwordField.getText())));
    }

    private void validation(final String _email, final String _password) {
        // in cazul in care email-ul este valid si parola are cel putin 7 caractere, incercam sa facem log in
        if (Patterns.EMAIL_ADDRESS.matcher(_email).matches() && _password.length() >= 7) {
            MyCustomVariables.getFirebaseAuth()
                    .signInWithEmailAndPassword(_email, _password).addOnCompleteListener(task -> {
                MyCustomMethods.closeTheKeyboard(this);
                // daca atat email-ul, cat si parola corespund, se face verifica daca emailul este verificat
                if (task.isSuccessful()) {
                    if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null) {
                        if (MyCustomVariables.getFirebaseAuth().getCurrentUser().isEmailVerified()) {
                            goToTheMainScreen();
                        } else {
                            Toast.makeText(LogInActivity.this, getResources().getString(R.string.login_verify_email),
                                    Toast.LENGTH_SHORT).show();
                            passwordField.setText("");
                        }
                    }
                } else {
                    // in cazul in care atat email-ul, cat si parola sunt valide,
                    // dar nu corespund informatiilor din baza de date Firebase
                    incorrectLogin.setText(R.string.login_incorrect_username_password);
                    passwordField.setText(""); // stergem parola
                }
            });
        }
        // in cazul in care nu se respecta conditia de log in
        else {
            MyCustomMethods.closeTheKeyboard(this);
            // daca atat email-ul, cat si parola nu contin niciun caracter
            if (_email.isEmpty() && _password.isEmpty())
                incorrectLogin.setText(R.string.signup_error2);
            else if (_email.isEmpty()) // daca email-ul nu contine niciun caracter
            {
                incorrectLogin.setText(R.string.signup_error3);
                passwordField.setText("");
            } else if (_password.isEmpty()) // daca parola nu contine niciun caracter
                incorrectLogin.setText(R.string.signup_error4);
            else if (!Patterns.EMAIL_ADDRESS.matcher(_email).matches() && _password.length() < 4) // daca email-ul nu are forma valida si parola este prea scurta
            {
                incorrectLogin.setText(R.string.signup_error5);
                passwordField.setText("");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(_email).matches()) // daca email-ul nu are forma valida, dar parola este in regula
            {
                incorrectLogin.setText(R.string.login_email_not_valid);
                passwordField.setText("");
            } else // daca parola este prea scurta, dar email-ul este in regula
            {
                incorrectLogin.setText(R.string.signup_error7);
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
        facebookLogIn.setReadPermissions(Arrays.asList("email", "public_profile"));
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
        googleLogIn.setSize(SignInButton.SIZE_STANDARD);
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
            } else
                Toast.makeText(getApplicationContext(), "Google authentication failed", Toast.LENGTH_SHORT).show();
        });
    }

    private void createPersonalInformationPath() {
        PersonalInformation information = new PersonalInformation("", "", "", "",
                "", "", new BirthDate(LocalDate.now()), "", "");
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("PersonalInformation")
                    .setValue(information);
        }
    }

    private void createApplicationSettingsPath() {
        ApplicationSettings settings = new ApplicationSettings("GBP");
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("ApplicationSettings")
                    .setValue(settings);
        }
    }

    private void goToTheMainScreen() {
        Intent intent = new Intent(LogInActivity.this, MainScreenActivity.class);
        Toast.makeText(LogInActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
        finishAffinity();
        startActivity(intent); // incepem activitatea urmatoare
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}