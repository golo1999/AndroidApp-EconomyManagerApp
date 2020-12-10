package com.example.EconomyManager.ApplicationPart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.EconomyManager.CustomDialogChangePassword;
import com.example.EconomyManager.CustomDialogDeleteAccount;
import com.example.EconomyManager.LoginPart.LogIn;
import com.example.EconomyManager.R;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivitySettings extends AppCompatActivity implements CustomDialogDeleteAccount.CustomDialogListener, CustomDialogChangePassword.CustomDialogListener
{
    private ImageView goBack;
    private Button resetDatabase, deleteAccount, changePassword;
    private FirebaseAuth fbAuth=FirebaseAuth.getInstance();
    private DatabaseReference myRef=FirebaseDatabase.getInstance().getReference();
    private Spinner currencySelector;
    private TextView themeText, currencyText;
    private SwitchCompat darkThemeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setVariables();
        setDarkThemeSwitch(darkThemeSwitch);
        setTexts();
        setOnClickListeners();
        createCurrencySpinner();
        setSpinners();
        setSelectedItemColorSpinner();
        hideButtons();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setVariables()
    {
        goBack=findViewById(R.id.settingsBack);
        resetDatabase=findViewById(R.id.settingsDeleteDatabase);
        deleteAccount=findViewById(R.id.settingsDeleteButton);
        changePassword=findViewById(R.id.settingsChangePasswordButton);
        currencySelector=findViewById(R.id.settingsCurrencySpinner);
        currencyText=findViewById(R.id.settingsCurrencyText);
        themeText=findViewById(R.id.settingsThemeText);
        darkThemeSwitch=findViewById(R.id.settingsDarkThemeSwitch);
    }

    private void setOnClickListeners()
    {
        changePassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Context context;
                if(v.getContext()!=null)
                {
                    context=v.getContext();
                    new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.settings_warning).trim()).setMessage(getResources().getString(R.string.settings_change_password).trim()).setPositiveButton(getResources().getString(R.string.settings_yes).trim(), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            CustomDialogChangePassword dialogChangePassword1=new CustomDialogChangePassword();
                            dialogChangePassword1.show(getSupportFragmentManager(), "example dialog");
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel).trim(), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    }).show();
                }
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Context context;
                if(v.getContext()!=null)
                {
                    context=v.getContext();
                    new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.settings_warning).trim()).setMessage(getResources().getString(R.string.settings_delete_account).trim()).setPositiveButton(getResources().getString(R.string.settings_yes).trim(), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            int choice=0;
                            if(fbAuth.getCurrentUser()!=null)
                                if(!fbAuth.getCurrentUser().getProviderData().get(fbAuth.getCurrentUser().getProviderData().size()-1).getProviderId().equals("password"))
                                    choice=2;
                            CustomDialogDeleteAccount dialogClass=new CustomDialogDeleteAccount(choice);
                            dialogClass.show(getSupportFragmentManager(), "example dialog");
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel).trim(), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    }).show();
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        resetDatabase.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Context context;
                if(v.getContext()!=null)
                {
                    context=v.getContext();
                    new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.settings_warning).trim()).setMessage(getResources().getString(R.string.settings_reset_database).trim()).setPositiveButton(getResources().getString(R.string.settings_yes).trim(), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            int choice=1;
                            if(fbAuth.getCurrentUser()!=null)
                                if(!fbAuth.getCurrentUser().getProviderData().get(fbAuth.getCurrentUser().getProviderData().size()-1).getProviderId().equals("password"))
                                    choice=3;
                            CustomDialogDeleteAccount dialogClass=new CustomDialogDeleteAccount(choice);
                            dialogClass.show(getSupportFragmentManager(), "example dialog");
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel).trim(), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    }).show();
                }
            }
        });
    }

    private void setSpinners()
    {
        if(fbAuth.getUid()!=null)
        {
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        if(snapshot.hasChild("darkTheme"))
                        {
                            boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()));
                            int color, theme, dropDownTheme;

                            if(!darkThemeEnabled)
                            {
                                theme=R.drawable.ic_white_gradient_tobacco_ad;
                                color=Color.parseColor("#195190"); // turkish sea (albastru)
                                dropDownTheme=R.drawable.ic_blue_gradient_unloved_teen;
                            }
                            else
                            {
                                theme=R.drawable.ic_black_gradient_night_shift;
                                color=Color.WHITE;
                                dropDownTheme=R.drawable.ic_white_gradient_tobacco_ad;
                            }

                            getWindow().setBackgroundDrawableResource(theme); // setam culoarea dropdown

                            currencySelector.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP); // setam culoarea sagetii
                            currencySelector.setPopupBackgroundResource(dropDownTheme); // setam culoarea elementelor
                        }
                        if(snapshot.hasChild("currency"))
                        {
                            String databaseCurrency=String.valueOf(snapshot.child("currency").getValue());
                            switch(databaseCurrency)
                            {
                                case "AUD":
                                    currencySelector.setSelection(0);
                                    break;
                                case "BRL":
                                    currencySelector.setSelection(1);
                                    break;
                                case "CAD":
                                    currencySelector.setSelection(2);
                                    break;
                                case "CHF":
                                    currencySelector.setSelection(3);
                                    break;
                                case "CNY":
                                    currencySelector.setSelection(4);
                                    break;
                                case "EUR":
                                    currencySelector.setSelection(5);
                                    break;
                                case "GBP":
                                    currencySelector.setSelection(6);
                                    break;
                                case "INR":
                                    currencySelector.setSelection(7);
                                    break;
                                case "JPY":
                                    currencySelector.setSelection(8);
                                    break;
                                case "RON":
                                    currencySelector.setSelection(9);
                                    break;
                                case "RUB":
                                    currencySelector.setSelection(10);
                                    break;
                                default:
                                    currencySelector.setSelection(11);
                                    break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });

            darkThemeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").child("darkTheme").setValue(isChecked);
                }
            });
        }
    }

    @Override
    public void changePassword(String oldPass, final String newPass)
    {
        if(!oldPass.trim().equals("") && !newPass.trim().equals(""))
        {
            final FirebaseUser user=fbAuth.getCurrentUser();
            if(user!=null && fbAuth.getUid()!=null)
            {
                String email=user.getEmail();
                AuthCredential credential;
                if(email!=null)
                {
                    credential=EmailAuthProvider.getCredential(email, oldPass);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                            Toast.makeText(ActivitySettings.this, getResources().getString(R.string.settings_password_updated), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else Toast.makeText(ActivitySettings.this, getResources().getString(R.string.settings_email_password_no_match), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
        else if(oldPass.trim().equals("") && newPass.trim().equals(""))
            Toast.makeText(this, getResources().getString(R.string.settings_passwords_not_empty), Toast.LENGTH_SHORT).show();
        else if(oldPass.trim().equals(""))
            Toast.makeText(this, getResources().getString(R.string.settings_old_password_not_empty), Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, getResources().getString(R.string.settings_new_password_not_empty), Toast.LENGTH_SHORT).show();
        closeTheKeyboard();
    }

    @Override
    public void getTypedPasswordAndDeleteTheAccount(String pass)
    {
        if(!pass.trim().equals(""))
        {
            final FirebaseUser user=fbAuth.getCurrentUser();
            if(user!=null && fbAuth.getUid()!=null)
            {
                String email=user.getEmail();
                AuthCredential credential;
                if(email!=null)
                {
                    credential=EmailAuthProvider.getCredential(email, pass);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                myRef.child(fbAuth.getUid()).removeValue();
                                user.delete().addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        Intent intent=new Intent(ActivitySettings.this, LogIn.class);
                                        fbAuth.signOut();
                                        finishAffinity();
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        Toast.makeText(ActivitySettings.this, getResources().getString(R.string.settings_account_deleted), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else Toast.makeText(ActivitySettings.this, getResources().getString(R.string.settings_email_password_no_match), Toast.LENGTH_SHORT).show();
                            closeTheKeyboard();
                        }
                    });
                }
            }
        }
        else Toast.makeText(this, getResources().getString(R.string.signup_error4), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void deleteAccount()
    {
        if(fbAuth.getCurrentUser()!=null)
        {
            AuthCredential credential=FacebookAuthProvider.getCredential(String.valueOf(AccessToken.getCurrentAccessToken())); // initializam acreditarea (credential) pentru autentificare cu facebook

            if(fbAuth.getCurrentUser().getProviderData().get(fbAuth.getCurrentUser().getProviderData().size()-1).getProviderId().equals("google.com")) // in cazul in care providerul de autentificare este google
            {
                GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
                if(account!=null)
                    credential=GoogleAuthProvider.getCredential(account.getIdToken(), null);
            }

            fbAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(fbAuth.getUid()!=null)
                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").removeValue();
                    fbAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Intent intent=new Intent(ActivitySettings.this, LogIn.class);
                            fbAuth.signOut();
                            finishAffinity();
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            Toast.makeText(ActivitySettings.this, getResources().getString(R.string.settings_account_deleted), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void getTypedPasswordAndResetTheDatabase(String pass)
    {
        if(!pass.trim().equals(""))
        {
            final FirebaseUser user=fbAuth.getCurrentUser();
            if(user!=null && fbAuth.getUid()!=null)
            {
                String email=user.getEmail();
                AuthCredential credential;
                if(email!=null)
                {
                    credential= EmailAuthProvider.getCredential(email, pass);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").removeValue();
                                Toast.makeText(ActivitySettings.this, getResources().getString(R.string.settings_database_reset), Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(ActivitySettings.this, getResources().getString(R.string.settings_email_password_no_match), Toast.LENGTH_SHORT).show();
                            closeTheKeyboard();
                        }
                    });
                }
            }
        }
        else Toast.makeText(this, getResources().getString(R.string.signup_error4), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void resetDatabase()
    {
        if(fbAuth.getUid()!=null && fbAuth.getCurrentUser()!=null)
        {
            AuthCredential credential=FacebookAuthProvider.getCredential(String.valueOf(AccessToken.getCurrentAccessToken())); // initializam acreditarea (credential) pentru autentificare cu facebook

            if(fbAuth.getCurrentUser().getProviderData().get(fbAuth.getCurrentUser().getProviderData().size()-1).getProviderId().equals("google.com")) // in cazul in care providerul de autentificare este google
            {
                GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(this);
                if(account!=null)
                    credential=GoogleAuthProvider.getCredential(account.getIdToken(), null);
            }

            fbAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").removeValue();
                        Toast.makeText(ActivitySettings.this, getResources().getString(R.string.settings_database_reset), Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(ActivitySettings.this, getResources().getString(R.string.settings_email_password_no_match), Toast.LENGTH_SHORT).show();
                    closeTheKeyboard();
                }
            });
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

    private void setTexts()
    {
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("darkTheme"))
                    {
                        boolean checked=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()));
                        int color;

                        if(!checked)
                            color=Color.parseColor("#195190");
                        else color=Color.WHITE;

                        themeText.setTextColor(color);
                        currencyText.setTextColor(color);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void createCurrencySpinner()
    {
        String[] currencyList=getResources().getStringArray(R.array.currencies);

        ArrayAdapter<String> currencyAdapter=new ArrayAdapter<String>(this, R.layout.custom_spinner_item, currencyList)
        {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                final View v=super.getDropDownView(position, convertView, parent);
                ((TextView)v).setGravity(Gravity.CENTER);

                if(fbAuth.getUid()!=null)
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.hasChild("darkTheme"))
                            {
                                boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()).trim());
                                int itemsColor;

                                if(!darkThemeEnabled)
                                    itemsColor=Color.WHITE;
                                else itemsColor=Color.BLACK;

                                ((TextView)v).setTextColor(itemsColor); // setam culoarea textului elementelor in functie de tema selectata
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });

                return v;
            }
        };

        currencySelector.setAdapter(currencyAdapter);
    }

    private void setSelectedItemColorSpinner() // stilizarea primului element al spinnerului
    {
        AdapterView.OnItemSelectedListener itemSelectedListener=new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id)
            {
                String selectedCurrency=String.valueOf(currencySelector.getItemAtPosition(position)), currencySymbol;

                if(fbAuth.getUid()!=null)
                {
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.hasChild("darkTheme"))
                            {
                                boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()).trim());
                                int color;

                                if(!darkThemeEnabled)
                                    color=Color.parseColor("#195190");
                                else color=Color.WHITE;

                                ((TextView) parent.getChildAt(0)).setTextColor(color); // primul element va avea textul aliniat la stanga si culoarea in functie de tema selectata
                                ((TextView) parent.getChildAt(0)).setGravity(Gravity.END);
                                ((TextView) parent.getChildAt(0)).setTypeface(null, Typeface.BOLD);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });

                    switch(selectedCurrency)
                    {
                        case "AUD":
                            currencySymbol="A$";
                            break;
                        case "BRL":
                            currencySymbol="R$";
                            break;
                        case "CAD":
                            currencySymbol="C$";
                            break;
                        case "CHF":
                            currencySymbol="CHF";
                            break;
                        case "CNY":
                            currencySymbol="元";
                            break;
                        case "EUR":
                            currencySymbol="€";
                            break;
                        case "GBP":
                            currencySymbol="£";
                            break;
                        case "INR":
                            currencySymbol="₹";
                            break;
                        case "JPY":
                            currencySymbol="¥";
                            break;
                        case "RON":
                            currencySymbol="RON";
                            break;
                        case "RUB":
                            currencySymbol="₽";
                            break;
                        default:
                            currencySymbol="$";
                            break;
                    }
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").child("currency").setValue(selectedCurrency);
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").child("currencySymbol").setValue(currencySymbol);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        };

        currencySelector.setOnItemSelectedListener(itemSelectedListener);
    }

    private void setDarkThemeSwitch(final SwitchCompat darkThemeSwitch)
    {
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("darkTheme"))
                    {
                        boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()).trim());
                        darkThemeSwitch.setChecked(darkThemeEnabled);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void hideButtons()
    {
        FirebaseUser user=fbAuth.getCurrentUser();

        if(user!=null)
        {
            String authProvider=user.getProviderData().get(user.getProviderData().size()-1).getProviderId();

            switch(authProvider)
            {
                case "google.com":
                case "facebook.com":
                    changePassword.setVisibility(View.GONE);
                    break;
                //case "password":
                    //break;
            }
        }
    }
}