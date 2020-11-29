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

import com.example.EconomyManager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity
{

    private TextView logIn, signUp, incorrectEmail;
    private Button forgotButton;
    private EditText email;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setVariables();
        setOnClickListeners();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setVariables()
    {
        logIn=findViewById(R.id.forgot_login);
        signUp=findViewById(R.id.forgot_signup);
        forgotButton=findViewById(R.id.forgot_button);
        email=findViewById(R.id.forgot_email);
        incorrectEmail=findViewById(R.id.forgot_incorrect_email);
        fbAuth=FirebaseAuth.getInstance();
    }

    private void setOnClickListeners()
    {
        logIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(ForgotPassword.this, LogIn.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(ForgotPassword.this, SignUp.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validation(email.getText().toString());
            }
        });
    }

    private void validation(final String emailText)
    {
        closeTheKeyboard();
        if(Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
        {
            fbAuth.sendPasswordResetEmail(emailText).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ForgotPassword.this, "Password reset sent", Toast.LENGTH_LONG).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    else incorrectEmail.setText(R.string.forgot_password_email_does_not_exist);
                }
            });
        }
        else
        {
            if(emailText.isEmpty())
                incorrectEmail.setText(R.string.signup_error3);
            else if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
                incorrectEmail.setText(R.string.login_email_not_valid);
        }
    }

    private void closeTheKeyboard()
    {
        View v=this.getCurrentFocus();
        if(v!=null)
        {
            InputMethodManager manager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}