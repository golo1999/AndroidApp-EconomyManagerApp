package com.example.EconomyManager.LoginPart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.EconomyManager.BirthDate;
import com.example.EconomyManager.PersonalInformation;
import com.example.EconomyManager.ApplicationSettings;
import com.example.EconomyManager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;

public class SignUp extends AppCompatActivity {

    private TextView logIn, incorrectSignup;
    private EditText email, password;
    private Button signUp;
    private FirebaseAuth fbAuth;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        logIn = findViewById(R.id.signup_login);
        signUp = findViewById(R.id.signup_button);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        incorrectSignup = findViewById(R.id.signup_incorrect_signup);
        fbAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    private void setOnClickListeners() {
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LogIn.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verification();
            }
        });
    }

    private void verification() {
        String val_email = email.getText().toString().trim(); // salvam valoarea input-ului din email, fara spatii la inceput sau la final
        String val_pass = password.getText().toString(); // salvam valoarea inputului din parola

        closeTheKeyboard();
        if (val_pass.length() > 6 && Patterns.EMAIL_ADDRESS.matcher(val_email).matches()) // daca parola are cel putin 7 caractere si email-ul este valid
        {
            fbAuth.createUserWithEmailAndPassword(val_email, val_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() // metoda FireBase de sign up
            {
                @Override
                public void onComplete(@NonNull final Task<AuthResult> task) {
                    if (task.isSuccessful()) // daca se poate crea contul
                    {
                        final FirebaseUser fbUser = fbAuth.getCurrentUser();
                        if (fbUser != null)
                            fbUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                        createPersonalInformationPath();
                                        createApplicationSettingsPath();
                                        fbAuth.signOut();
                                        logIn.callOnClick();
                                    }
                                }
                            });
                    } else // daca nu se poate crea contul
                    {
                        incorrectSignup.setText(R.string.signup_error1); // afisam 'eroarea' ca email-ul deja exista
                        password.setText(""); // resetam parola
                    }
                }
            });
        } else if (val_email.isEmpty() && val_pass.isEmpty()) // daca atat emailul, cat si parola nu contin niciun caracter
            incorrectSignup.setText(R.string.signup_error2); // afisam 'eroarea'
        else if (val_email.isEmpty()) // daca doar emailul nu contine niciun caracter
        {
            incorrectSignup.setText(R.string.signup_error3); // afisam 'eroarea'
            password.setText(""); // resetam parola
        } else if (val_pass.isEmpty()) // daca doar parola nu contine caractere
            incorrectSignup.setText(R.string.signup_error4); // afisam 'eroarea'
        else if (val_pass.length() < 7 && !Patterns.EMAIL_ADDRESS.matcher(val_email).matches()) // daca parola este prea scurta si email-ul nu este valid
        {
            incorrectSignup.setText(R.string.signup_error5); // afisam 'eroarea'
            password.setText(""); // resetam parola
        } else if (!Patterns.EMAIL_ADDRESS.matcher(val_email).matches()) // daca email-ul nu este valid
        {
            incorrectSignup.setText(R.string.signup_error6); // afisam 'eroarea'
            password.setText(""); // resetam parola
        } else // daca parola este prea scurta
        {
            incorrectSignup.setText(R.string.signup_error7); // afisam 'eroarea'
            password.setText(""); // resetam parola
        }
    }

    private void createPersonalInformationPath() {
        PersonalInformation information = new PersonalInformation("", "", "", "", "", "", new BirthDate(LocalDate.now()), "", "");
        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).child("PersonalInformation").setValue(information);
    }

    private void createApplicationSettingsPath() {
        ApplicationSettings settings = new ApplicationSettings("GBP");
        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").setValue(settings);
    }

    private void closeTheKeyboard() {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}