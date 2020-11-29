package com.example.EconomyManager.LoginPart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.EconomyManager.ApplicationPart.ActivityMainScreen;
import com.example.EconomyManager.ApplicationSettings;
import com.example.EconomyManager.PersonalInformation;
import com.example.EconomyManager.R;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class LogIn extends AppCompatActivity
{

    private TextView signUp, forgotPassword, incorrectLogin;
    private Button logIn;
    private LoginButton facebookLogIn;
    private SignInButton googleLogIn;
    private EditText email, password;
    private FirebaseAuth fbAuth=FirebaseAuth.getInstance();
    private CallbackManager manager;
    private DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN=27;
    private int googleOrFacebookInsideOnActivityResult;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setVariables();
        setGoogleButtonSize();
        setGoogleRequest();
        initializeFacebookLogin();
        setOnClickListeners();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    private void setVariables()
    {
        signUp=findViewById(R.id.login_create_account);
        forgotPassword=findViewById(R.id.login_forgot_password);
        logIn=findViewById(R.id.login_button);
        email=findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        incorrectLogin=findViewById(R.id.login_incorrect_login);
        facebookLogIn=findViewById(R.id.login_button_facebook);
        googleLogIn=findViewById(R.id.login_button_google);
    }

    private void setOnClickListeners()
    {
        signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(LogIn.this, SignUp.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // slide dinspre dreapta spre stanga
            }
        });

        facebookLogIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                buttonClickLoginFacebook(v);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(LogIn.this, ForgotPassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        googleLogIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                googleSignIn();
            }
        });

        logIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validation(email.getText().toString().trim(), password.getText().toString());
            }
        });
    }

    private void validation(final String _email, final String _password)
    {
        if(Patterns.EMAIL_ADDRESS.matcher(_email).matches() && _password.length()>=7) // in cazul in care email-ul este valid si parola are cel putin 7 caractere, incercam sa facem log in
        {
            fbAuth.signInWithEmailAndPassword(_email,_password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    closeTheKeyboard();
                    if(task.isSuccessful()) // daca atat email-ul, cat si parola corespund, se face verifica daca emailul este verificat
                    {
                        if(fbAuth.getCurrentUser()!=null)
                            if(fbAuth.getCurrentUser().isEmailVerified())
                                goToTheMainScreen();
                        else
                        {
                            Toast.makeText(LogIn.this, getResources().getString(R.string.login_verify_email), Toast.LENGTH_SHORT).show();
                            password.setText("");
                        }
                    }
                    else
                    {
                        incorrectLogin.setText(R.string.login_incorrect_username_password); // in cazul in care atat email-ul, cat si parola sunt valide, dar nu corespund informatiilor din baza de date Firebase
                        password.setText(""); // stergem parola
                    }
                }
            });
        }
        else // in cazul in care nu se respecta conditia de log in
        {
            closeTheKeyboard();
            if(_email.isEmpty() && _password.isEmpty()) // daca atat email-ul, cat si parola nu contin niciun caracter
                incorrectLogin.setText(R.string.signup_error2);
            else if(_email.isEmpty()) // daca email-ul nu contine niciun caracter
            {
                incorrectLogin.setText(R.string.signup_error3);
                password.setText("");
            }
            else if(_password.isEmpty()) // daca parola nu contine niciun caracter
                incorrectLogin.setText(R.string.signup_error4);
            else if(!Patterns.EMAIL_ADDRESS.matcher(_email).matches() && _password.length()<4) // daca email-ul nu are forma valida si parola este prea scurta
            {
                incorrectLogin.setText(R.string.signup_error5);
                password.setText("");
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(_email).matches()) // daca email-ul nu are forma valida, dar parola este in regula
            {
                incorrectLogin.setText(R.string.login_email_not_valid);
                password.setText("");
            }
            else // daca parola este prea scurta, dar email-ul este in regula
            {
                incorrectLogin.setText(R.string.signup_error7);
                password.setText("");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        manager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                //if(task.getResult(ApiException.class)==null)
                    //Toast.makeText(this, "task null", Toast.LENGTH_SHORT).show();
                GoogleSignInAccount account=task.getResult(ApiException.class);
                if(account!=null)
                {
                    firebaseAuthWithGoogle(account); // in documentatia de la google e account.getIdToken()
                    //Toast.makeText(this, "account!=null", Toast.LENGTH_SHORT).show();
                }
                //else Toast.makeText(this, "account==null", Toast.LENGTH_SHORT).show();
            }
            catch(ApiException e)
            {
                //Toast.makeText(this, "Error in onActivityResult", Toast.LENGTH_SHORT).show();
                //e.printStackTrace();
                //Toast.makeText(LogIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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

    private void initializeFacebookLogin()
    {
        FacebookSdk.sdkInitialize(getApplicationContext());
        manager=CallbackManager.Factory.create();
        facebookLogIn.setReadPermissions(Arrays.asList("email", "public_profile"));
    }

    private void buttonClickLoginFacebook(View v)
    {
        LoginManager.getInstance().registerCallback(manager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel()
            {

            }

            @Override
            public void onError(FacebookException error)
            {

            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken)
    {
        AuthCredential credential=FacebookAuthProvider.getCredential(accessToken.getToken());
        fbAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    if(fbAuth.getCurrentUser()!=null)
                    {
                        createPersonalInformationPath();
                        createApplicationSettingsPath();
                        goToTheMainScreen();
                    }
                }
                //else Toast.makeText(getApplicationContext(), "Facebook authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setGoogleButtonSize()
    {
        googleLogIn.setSize(SignInButton.SIZE_STANDARD);
    }

    private void setGoogleRequest()
    {
        GoogleSignInOptions signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient=GoogleSignIn.getClient(this, signInOptions);
    }

    private void googleSignIn()
    {
        Intent signInIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account)
    {
        AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fbAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    if(fbAuth.getCurrentUser()!=null)
                    {
                        createPersonalInformationPath();
                        createApplicationSettingsPath();
                        goToTheMainScreen();
                    }
                }
                //else Toast.makeText(getApplicationContext(), "Google authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createPersonalInformationPath()
    {
        PersonalInformation information=new PersonalInformation("","","","","","","","","");
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("PersonalInformation").setValue(information);
    }

    private void createApplicationSettingsPath()
    {
        ApplicationSettings settings=new ApplicationSettings(false, "GBP");
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").setValue(settings);
    }

    private void goToTheMainScreen()
    {
        Intent intent=new Intent(LogIn.this, ActivityMainScreen.class);
        Toast.makeText(LogIn.this, "Login successful", Toast.LENGTH_SHORT).show();
        finishAffinity();
        startActivity(intent); // incepem activitatea urmatoare
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}