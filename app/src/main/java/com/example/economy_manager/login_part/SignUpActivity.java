package com.example.economy_manager.login_part;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.R;
import com.example.economy_manager.models.ApplicationSettings;
import com.example.economy_manager.models.BirthDate;
import com.example.economy_manager.models.PersonalInformation;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;

public class SignUpActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private TextView logIn;
    private EditText emailField;
    private EditText passwordField;
    private Button signUpButton;
    private UserDetails userDetails;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);
        setVariables();
        setOnClickListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setVariables() {
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        logIn = findViewById(R.id.signup_login);
        signUpButton = findViewById(R.id.signup_button);
        emailField = findViewById(R.id.signup_email);
        passwordField = findViewById(R.id.signup_password);
    }

    private void setOnClickListeners() {
        logIn.setOnClickListener(v -> onBackPressed());

        signUpButton.setOnClickListener(v -> {
            // saving the trimmed email's value
            final String enteredEmailValue = String.valueOf(emailField.getText()).trim();
            // saving the password's value
            final String enteredPasswordValue = String.valueOf(passwordField.getText());

            validation(enteredEmailValue, enteredPasswordValue);
        });
    }

    private void validation(final String emailValue, final String passwordValue) {
        MyCustomMethods.closeTheKeyboard(this);
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
                                userDetails = new UserDetails();
                                MyCustomMethods.showShortMessage(this,
                                        getResources().getString(R.string.verify_email));
                                createPersonalInformationPath();
                                createApplicationSettingsPath();
                                MyCustomSharedPreferences.saveUserDetailsToSharedPreferences(preferences, userDetails);
                                MyCustomVariables.getFirebaseAuth().signOut();
                                logIn.callOnClick();
                            }
                        });
                    }
                }
                // if the account can't be created
                else {
                    // showing the error: user already exists
                    MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error1));
                    // resetting the password field
                    emptyField(passwordField);
                }
            });
        }
        // if both email and password are empty
        else if (emailValue.isEmpty() && passwordValue.isEmpty()) {
            // showing the error
            MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error2));
        }
        // if only the email is empty
        else if (emailValue.isEmpty()) {
            // showing the error
            MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error3));
            // resetting the password field
            emptyField(passwordField);
        }
        // if only the password is empty
        else if (passwordValue.isEmpty()) {
            // showing the error
            MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error4));
        }
        // if the password is too short and the email isn't valid
        else if (passwordValue.length() < 7 && !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            // showing the error
            MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error5));
            // resetting the password field
            emptyField(passwordField);
        }
        // if the email isn't valid
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            // showing the error
            MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error6));
            // resetting the password field
            emptyField(passwordField);
        }
        // if the password is too short
        else {
            // showing the error
            MyCustomMethods.showShortMessage(this, getResources().getString(R.string.signup_error7));
            // resetting the password field
            emptyField(passwordField);
        }
    }

    private void createPersonalInformationPath() {
        final PersonalInformation information = new PersonalInformation("", "",
                "", "", "", "",
                new BirthDate(LocalDate.now()), "", "");

        userDetails.setPersonalInformation(information);

        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("PersonalInformation")
                    .setValue(information);
        }
    }

    private void createApplicationSettingsPath() {
        final ApplicationSettings settings = new ApplicationSettings("GBP");

        userDetails.setApplicationSettings(settings);

        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("ApplicationSettings")
                    .setValue(settings);
        }
    }

    private void emptyField(final EditText field) {
        field.setText("");
    }
}