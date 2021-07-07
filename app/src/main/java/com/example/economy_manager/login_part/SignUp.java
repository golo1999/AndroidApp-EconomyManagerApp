package com.example.economy_manager.login_part;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.ApplicationSettings;
import com.example.economy_manager.BirthDate;
import com.example.economy_manager.MyCustomMethods;
import com.example.economy_manager.MyCustomSharedPreferences;
import com.example.economy_manager.MyCustomVariables;
import com.example.economy_manager.PersonalInformation;
import com.example.economy_manager.R;
import com.example.economy_manager.UserDetails;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;

public class SignUp extends AppCompatActivity {
    private SharedPreferences preferences;
    private TextView logIn;
    private TextView incorrectSignUp;
    private EditText emailField;
    private EditText passwordField;
    private Button signUpButton;
    private UserDetails userDetails;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
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
        preferences = getSharedPreferences("ECONOMY_MANAGER_USER_DATA", MODE_PRIVATE);
        logIn = findViewById(R.id.signup_login);
        signUpButton = findViewById(R.id.signup_button);
        emailField = findViewById(R.id.signup_email);
        passwordField = findViewById(R.id.signup_password);
        incorrectSignUp = findViewById(R.id.signup_incorrect_signup);
    }

    private void setOnClickListeners() {
        logIn.setOnClickListener(v -> {
            final Intent intent = new Intent(SignUp.this, LogIn.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        signUpButton.setOnClickListener(v -> verification());
    }

    private void verification() {
        // salvam valoarea input-ului din email, fara spatii la inceput sau la final
        final String val_email = emailField.getText().toString().trim();
        // salvam valoarea inputului din parola
        final String val_pass = passwordField.getText().toString();

        MyCustomMethods.closeTheKeyboard(this);

        // daca parola are cel putin 7 caractere si email-ul este valid
        if (val_pass.length() > 6 && Patterns.EMAIL_ADDRESS.matcher(val_email).matches()) {
            // metoda de inregistrare a Firebase
            MyCustomVariables.getFirebaseAuth()
                    .createUserWithEmailAndPassword(val_email, val_pass).addOnCompleteListener(task -> {
                // daca se poate crea contul
                if (task.isSuccessful()) {
                    final FirebaseUser fbUser = MyCustomVariables.getFirebaseAuth().getCurrentUser();
                    if (fbUser != null)
                        fbUser.sendEmailVerification().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                userDetails = new UserDetails();
                                Toast.makeText(SignUp.this,
                                        "Please verify your email",
                                        Toast.LENGTH_SHORT).show();
                                createPersonalInformationPath();
                                createApplicationSettingsPath();
                                MyCustomSharedPreferences
                                        .saveUserDetailsToSharedPreferences(preferences,
                                                userDetails);
                                MyCustomVariables.getFirebaseAuth().signOut();
                                logIn.callOnClick();
                            }
                        });
                }
                // daca nu se poate crea contul
                else {
                    // afisam 'eroarea' ca email-ul deja exista
                    incorrectSignUp.setText(R.string.signup_error1);
                    passwordField.setText(""); // resetam parola
                }
            });
        }
        // daca atat emailul, cat si parola nu contin niciun caracter
        else if (val_email.isEmpty() && val_pass.isEmpty()) {
            // afisam 'eroarea'
            incorrectSignUp.setText(R.string.signup_error2);
        }
        // daca doar emailul nu contine niciun caracter
        else if (val_email.isEmpty()) {
            // afisam 'eroarea'
            incorrectSignUp.setText(R.string.signup_error3);
            passwordField.setText(""); // resetam parola
        }
        // daca doar parola nu contine caractere
        else if (val_pass.isEmpty()) {
            // afisam 'eroarea'
            incorrectSignUp.setText(R.string.signup_error4);
        }
        // daca parola este prea scurta si email-ul nu este valid
        else if (val_pass.length() < 7 && !Patterns.EMAIL_ADDRESS.matcher(val_email).matches()) {
            // afisam 'eroarea'
            incorrectSignUp.setText(R.string.signup_error5);
            passwordField.setText(""); // resetam parola
        }
        // daca email-ul nu este valid
        else if (!Patterns.EMAIL_ADDRESS.matcher(val_email).matches()) {
            // afisam 'eroarea'
            incorrectSignUp.setText(R.string.signup_error6);
            passwordField.setText(""); // resetam parola
        }
        // daca parola este prea scurta
        else {
            // afisam 'eroarea'
            incorrectSignUp.setText(R.string.signup_error7);
            passwordField.setText(""); // resetam parola
        }
    }

    private void createPersonalInformationPath() {
        PersonalInformation information = new PersonalInformation("", "",
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
        ApplicationSettings settings = new ApplicationSettings("GBP");

        userDetails.setApplicationSettings(settings);

        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("ApplicationSettings")
                    .setValue(settings);
        }
    }
}