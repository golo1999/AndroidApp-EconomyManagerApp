package com.example.economy_manager.login_part;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.R;

public class ActivityForgotPassword extends AppCompatActivity implements View.OnClickListener {
    private TextView logIn;
    private TextView signUp;
    private TextView incorrectEmail;
    private Button forgotButton;
    private EditText emailField;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setVariables();
        setOnClickListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setVariables() {
        logIn = findViewById(R.id.forgot_login);
        signUp = findViewById(R.id.forgot_signup);
        forgotButton = findViewById(R.id.forgot_button);
        emailField = findViewById(R.id.forgot_email);
        incorrectEmail = findViewById(R.id.forgot_incorrect_email);
    }

    private void setOnClickListeners() {
        logIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        forgotButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final int clickedButtonId = view.getId();

        // if the clicked button is the one for authentication or for registration
        if (clickedButtonId == logIn.getId() || clickedButtonId == signUp.getId()) {
            final Intent intent = new Intent(ActivityForgotPassword.this,
                    clickedButtonId == logIn.getId() ?
                            ActivityLogIn.class : ActivitySignUp.class);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        // if the clicked button is the one for forgot password
        else if (clickedButtonId == forgotButton.getId()) {
            validation(String.valueOf(emailField.getText()));
        }
    }

    private void validation(final String emailText) {
        MyCustomMethods.closeTheKeyboard(this);
        if (Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            MyCustomVariables.getFirebaseAuth().sendPasswordResetEmail(emailText).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ActivityForgotPassword.this,
                            "Password reset sent",
                            Toast.LENGTH_LONG).show();

                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    incorrectEmail.setText(R.string.forgot_password_email_does_not_exist);
                }
            });
        } else {
            incorrectEmail.setText(emailText.isEmpty() ?
                    R.string.signup_error3 : R.string.login_email_not_valid);
        }
    }
}