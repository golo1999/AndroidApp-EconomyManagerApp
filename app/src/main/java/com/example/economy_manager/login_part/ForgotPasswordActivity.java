package com.example.economy_manager.login_part;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.R;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.google.android.gms.tasks.Task;

public class ForgotPasswordActivity
        extends AppCompatActivity
        implements View.OnClickListener {
    private TextView logIn;
    private TextView signUp;
    private Button forgotButton;
    private EditText emailField;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);
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
    }

    private void setOnClickListeners() {
        logIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
        forgotButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final int clickedButtonId = view.getId();

        // if the clicked button is the one for authentication
        if (clickedButtonId == logIn.getId()) {
            onBackPressed();
        }
        // if the clicked button is the one for registration
        else if (clickedButtonId == signUp.getId()) {
            startActivity(new Intent(ForgotPasswordActivity.this, SignUpActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        // if the clicked button is the one for forgot password
        else if (clickedButtonId == forgotButton.getId()) {
            validation(String.valueOf(emailField.getText()).trim());
        }
    }

    private void validation(final String emailText) {
        MyCustomMethods.closeTheKeyboard(this);

        if (Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            MyCustomVariables.getFirebaseAuth()
                    .sendPasswordResetEmail(emailText)
                    .addOnCompleteListener((final Task<Void> task) -> {
                        if (task.isSuccessful()) {
                            MyCustomMethods.showLongMessage(this,
                                    getResources().getString(R.string.verify_email));
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            MyCustomMethods.showShortMessage(this,
                                    getResources().getString(R.string.forgot_password_email_does_not_exist));
                        }
                    });
        } else {
            MyCustomMethods.showShortMessage(this, emailText.isEmpty() ?
                    getResources().getString(R.string.signup_error3) :
                    getResources().getString(R.string.login_email_not_valid));
        }
    }
}