package com.example.EconomyManager.ApplicationPart;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.EconomyManager.PersonalInformation;
import com.example.EconomyManager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ActivityEditAccount extends AppCompatActivity
{
    private ImageView goBack, accountPhoto, editFirstName, editLastName, editPhone, editWebsite, editCountry, editGender, editBirthDate, editCareerTitle;
    private TextView topText;
    private EditText firstName, lastName, phoneNumber, website, birthDate, careerTitle;
    private Spinner country, gender;
    private FirebaseAuth fbAuth=FirebaseAuth.getInstance();
    private DatabaseReference myRef=FirebaseDatabase.getInstance().getReference();
    private int firstNameClicked, lastNameClicked, phoneClicked, websiteClicked, countryClicked, genderClicked, birthDateClicked, careerTitleClicked;
    private boolean darkThemeEnabled;
    private ArrayList<String> countryList=new ArrayList<>(), genderList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        setVariables();
        setTheme();
        setOnClickListeners();
        setTopText();
        setCountrySpinner();
        setGenderSpinner();
        getPersonalInformation();
        makeAllInformationNotClickable();
        setSelectedItemColorSpinner(country);
        setSelectedItemColorSpinner(gender);
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
        goBack=findViewById(R.id.editAccountBack);
        firstName=findViewById(R.id.editAccountFirstName);
        lastName=findViewById(R.id.editAccountLastName);
        phoneNumber=findViewById(R.id.editAccountPhone);
        website=findViewById(R.id.editAccountWebsite);
        birthDate=findViewById(R.id.editAccountBirthDate);
        careerTitle=findViewById(R.id.editAccountTitle);
        country=findViewById(R.id.editAccountCountrySpinner);
        gender=findViewById(R.id.editAccountGenderSpinner);
        accountPhoto=findViewById(R.id.editAccountPhoto);
        editFirstName=findViewById(R.id.editAccountFirstNameEdit);
        editLastName=findViewById(R.id.editAccountLastNameEdit);
        editPhone=findViewById(R.id.editAccountPhoneEdit);
        editWebsite=findViewById(R.id.editAccountWebsiteEdit);
        editCountry=findViewById(R.id.editAccountCountryEdit);
        editGender=findViewById(R.id.editAccountGenderEdit);
        editBirthDate=findViewById(R.id.editAccountBirthDateEdit);
        editCareerTitle=findViewById(R.id.editAccountTitleEdit);
        firstNameClicked=lastNameClicked=phoneClicked=websiteClicked=countryClicked=genderClicked=birthDateClicked=careerTitleClicked=0;
        topText=findViewById(R.id.editAccountTopText);
    }

    private void setOnClickListeners()
    {
        goBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        editFirstName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                firstNameClicked++;

                if(firstNameClicked%2!=0)
                    ifClickedIsEven(firstName, editFirstName);
                else if(firstNameClicked!=0)
                {
                    ifClickedIsOdd(firstName, editFirstName);
                    updateInformationEditText(firstName, "firstName");
                }
            }
        });

        editLastName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lastNameClicked++;

                if(lastNameClicked%2!=0)
                    ifClickedIsEven(lastName, editLastName);
                else if(lastNameClicked!=0)
                {
                    ifClickedIsOdd(lastName, editLastName);
                    updateInformationEditText(lastName, "lastName");
                }
            }
        });

        editPhone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                phoneClicked++;

                if(phoneClicked%2!=0)
                    ifClickedIsEven(phoneNumber, editPhone);
                else if(phoneClicked!=0)
                {
                    ifClickedIsOdd(phoneNumber, editPhone);
                    updateInformationPhone();
                }
            }
        });

        editWebsite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                websiteClicked++;

                if(websiteClicked%2!=0)
                    ifClickedIsEven(website, editWebsite);
                else if(websiteClicked!=0)
                {
                    ifClickedIsOdd(website, editWebsite);
                    updateInformationEditText(website, "website");
                }
            }
        });

        editCountry.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                countryClicked++;

                if(countryClicked%2!=0)
                    ifClickedIsEven(country, editCountry);
                else if(countryClicked!=0)
                {
                    ifClickedIsOdd(country, editCountry);

                    if(fbAuth.getUid()!=null && !String.valueOf(country.getSelectedItem()).trim().isEmpty() && !String.valueOf(country.getSelectedItem()).trim().equals("") && country.getSelectedItem()!=null)
                        if(Locale.getDefault().getDisplayLanguage().equals("English"))
                            myRef.child(fbAuth.getUid()).child("PersonalInformation").child("country").setValue(String.valueOf(country.getSelectedItem()).trim());
                        else if(getCountryNameInEnglish(country.getSelectedItemPosition())!=null)
                                myRef.child(fbAuth.getUid()).child("PersonalInformation").child("country").setValue(getCountryNameInEnglish(country.getSelectedItemPosition()));
                }
            }
        });

        editGender.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                genderClicked++;

                if(genderClicked%2!=0)
                    ifClickedIsEven(gender, editGender);
                else if(genderClicked!=0)
                {
                    ifClickedIsOdd(gender, editGender);

                    if(fbAuth.getUid()!=null && !String.valueOf(gender.getSelectedItem()).trim().isEmpty() && !String.valueOf(gender.getSelectedItem()).trim().equals("") && gender.getSelectedItem()!=null)
                        if(Locale.getDefault().getDisplayLanguage().equals("English"))
                            myRef.child(fbAuth.getUid()).child("PersonalInformation").child("gender").setValue(String.valueOf(gender.getSelectedItem()).trim());
                        else if(getGenderInEnglish(gender.getSelectedItemPosition())!=null)
                            myRef.child(fbAuth.getUid()).child("PersonalInformation").child("gender").setValue(getGenderInEnglish(gender.getSelectedItemPosition()));
                }
            }
        });

        editBirthDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                birthDateClicked++;

                if(birthDateClicked%2!=0)
                    ifClickedIsEven(birthDate, editBirthDate);
                else if(birthDateClicked!=0)
                {
                    ifClickedIsOdd(birthDate, editBirthDate);
                    if(fbAuth.getUid()!=null && !String.valueOf(birthDate.getText()).isEmpty() && isValidDate(String.valueOf(birthDate.getText())))
                        myRef.child(fbAuth.getUid()).child("PersonalInformation").child("birthDate").setValue(String.valueOf(birthDate.getText()));
                }
            }
        });

        editCareerTitle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                careerTitleClicked++;
                
                if(careerTitleClicked%2!=0)
                    ifClickedIsEven(careerTitle, editCareerTitle);
                else if(careerTitleClicked!=0)
                {
                    ifClickedIsOdd(careerTitle, editCareerTitle);
                    updateInformationEditText(careerTitle, "careerTitle");
                }
            }
        });

        accountPhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(ActivityEditAccount.this, ActivityEditPhoto.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void getPersonalInformation()
    {
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("PersonalInformation").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        String firstname=String.valueOf(snapshot.child("firstName").getValue()), lastname=String.valueOf(snapshot.child("lastName").getValue()), phonenumber=String.valueOf(snapshot.child("phoneNumber").getValue()), web=String.valueOf(snapshot.child("website").getValue()), count=String.valueOf(snapshot.child("country").getValue()), gend=String.valueOf(snapshot.child("gender").getValue()), birthdate=String.valueOf(snapshot.child("birthDate").getValue()), careertitle=String.valueOf(snapshot.child("careerTitle").getValue()), photourl=String.valueOf(snapshot.child("photoURL").getValue());
                        PersonalInformation information=new PersonalInformation(firstname, lastname, phonenumber, web, count, gend, birthdate, careertitle, photourl);

                        if(!firstname.trim().equals(""))
                            firstName.setHint(String.valueOf(information.getFirstName()));
                        else firstName.setHint(getResources().getString(R.string.edit_account_first_name).trim());
                        if(!lastname.trim().equals(""))
                            lastName.setHint(String.valueOf(information.getLastName()));
                        else lastName.setHint(getResources().getString(R.string.edit_account_last_name).trim());
                        if(!phonenumber.trim().equals(""))
                            phoneNumber.setHint(String.valueOf(information.getPhoneNumber()));
                        else phoneNumber.setHint(getResources().getString(R.string.edit_account_phone_number).trim());
                        if(!web.trim().equals(""))
                            website.setHint(String.valueOf(information.getWebsite()));
                        else website.setHint(getResources().getString(R.string.edit_account_website).trim());
                        if(!birthdate.trim().equals("") && isValidDate(birthdate))
                            birthDate.setHint(String.valueOf(information.getBirthDate()));
                        else birthDate.setHint(getResources().getString(R.string.edit_account_birth_date).trim());
                        if(!careertitle.trim().equals(""))
                            careerTitle.setHint(String.valueOf(information.getCareerTitle()));
                        else careerTitle.setHint(getResources().getString(R.string.edit_account_career_title).trim());

                        country.setSelection(getPositionInCountryList(count));
                        gender.setSelection(getPositionInGenderList(gend));

                        if(photourl.trim().equals(""))
                            //Picasso.get().load(R.drawable.ic_account).placeholder(R.drawable.ic_account).fit().into(accountPhoto);
                            Picasso.get().load("https://www.pexels.com/photo/grayscale-photo-of-ferris-wheel-3673785/").placeholder(R.drawable.ic_add_photo).fit().into(accountPhoto);
                        else Picasso.get().load(photourl).placeholder(R.drawable.ic_add_photo).fit().into(accountPhoto);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void makeAllInformationNotClickable()
    {
        firstName.setEnabled(false);
        lastName.setEnabled(false);
        phoneNumber.setEnabled(false);
        website.setEnabled(false);
        birthDate.setEnabled(false);
        careerTitle.setEnabled(false);
        country.setEnabled(false);
        gender.setEnabled(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void ifClickedIsEven(View field, final ImageView edit)
    {
        field.setEnabled(true);

        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("darkTheme"))
                    {
                        boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()));
                        int color;

                        if(!darkThemeEnabled)
                            color=R.drawable.ic_save_edit_blue;
                        else color=R.drawable.ic_save_edit;

                        edit.setImageResource(color);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void ifClickedIsOdd(View field, final ImageView edit)
    {
        field.setEnabled(false);

        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("darkTheme"))
                    {
                        boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()));
                        int color;

                        if(!darkThemeEnabled)
                            color=R.drawable.ic_edit_blue;
                        else color=R.drawable.ic_edit;

                        edit.setImageResource(color);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void updateInformationEditText(EditText field, String childPath)
    {
        if(fbAuth.getUid()!=null && !String.valueOf(field.getText()).isEmpty() && !String.valueOf(field.getText()).trim().equals(""))
            myRef.child(fbAuth.getUid()).child("PersonalInformation").child(childPath).setValue(String.valueOf(field.getText()));
    }

    private void updateInformationPhone() // de refacut
    {
        if(fbAuth.getUid()!=null && !String.valueOf(phoneNumber.getText()).isEmpty() && !String.valueOf(phoneNumber.getText()).trim().equals("") && PhoneNumberUtils.isGlobalPhoneNumber(String.valueOf(phoneNumber.getText()).trim()))
            myRef.child(fbAuth.getUid()).child("PersonalInformation").child("phoneNumber").setValue(String.valueOf(phoneNumber.getText()));
        else if(!PhoneNumberUtils.isGlobalPhoneNumber(String.valueOf(phoneNumber.getText()).trim()))
            Toast.makeText(ActivityEditAccount.this, getResources().getString(R.string.edit_account_incorrect_phone_number), Toast.LENGTH_SHORT).show();
    }

    private boolean isValidDate(String date)
    {
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        dateFormat.setLenient(false);
        try
        {
            dateFormat.parse(date);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
            Toast.makeText(ActivityEditAccount.this, getResources().getString(R.string.edit_account_enter_valid_birth_date), Toast.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }

    private void setTopText()
    {
        String text=getResources().getString(R.string.edit_account).trim();
        topText.setText(text);
        topText.setTextSize(20);
        topText.setTextColor(Color.WHITE);
    }

    private void setTheme()
    {
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                        if(snapshot.hasChild("darkTheme"))
                        {
                            darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()));
                            int color, theme, editResource, dropDownTheme;

                            if(!darkThemeEnabled)
                            {
                                theme=R.drawable.ic_white_gradient_tobacco_ad;
                                color=Color.parseColor("#195190"); // turkish sea (albastru)
                                editResource=R.drawable.ic_edit_blue;
                                dropDownTheme=R.drawable.ic_blue_gradient_unloved_teen;
                            }
                            else
                            {
                                theme=R.drawable.ic_black_gradient_night_shift;
                                color=Color.WHITE;
                                editResource=R.drawable.ic_edit;
                                dropDownTheme=R.drawable.ic_white_gradient_tobacco_ad;
                            }

                            getWindow().setBackgroundDrawableResource(theme); // setam culoarea dropdown

                            country.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP); // setam culoarea sagetii
                            country.setPopupBackgroundResource(dropDownTheme); // setam culoarea elementelor
                            gender.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                            gender.setPopupBackgroundResource(dropDownTheme);

                            editFirstName.setImageResource(editResource);
                            editLastName.setImageResource(editResource);
                            editPhone.setImageResource(editResource);
                            editWebsite.setImageResource(editResource);
                            editCountry.setImageResource(editResource);
                            editGender.setImageResource(editResource);
                            editBirthDate.setImageResource(editResource);
                            editCareerTitle.setImageResource(editResource);

                            setEditTextColor(firstName, color);
                            setEditTextColor(lastName, color);
                            setEditTextColor(phoneNumber, color);
                            setEditTextColor(website, color);
                            setEditTextColor(birthDate, color);
                            setEditTextColor(careerTitle, color);
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void setEditTextColor(EditText editText, int color)
    {
        editText.setHintTextColor(color);
        editText.setTextColor(color);
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void setCountrySpinner()
    {
        ArrayAdapter<String> countryAdapter=new ArrayAdapter<String >(this, R.layout.custom_spinner_item, countryList)
        {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                final View v=super.getDropDownView(position, convertView, parent);
                ((TextView)v).setGravity(Gravity.CENTER); // toate elementele spinnerului sunt aliniate la centru

                if(fbAuth.getUid()!=null)
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
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

        countryList.add(getResources().getString(R.string.edit_account_country_albania));
        countryList.add(getResources().getString(R.string.edit_account_country_andorra));
        countryList.add(getResources().getString(R.string.edit_account_country_armenia));
        countryList.add(getResources().getString(R.string.edit_account_country_austria));
        countryList.add(getResources().getString(R.string.edit_account_country_azerbaijan));
        countryList.add(getResources().getString(R.string.edit_account_country_belarus));
        countryList.add(getResources().getString(R.string.edit_account_country_belgium));
        countryList.add(getResources().getString(R.string.edit_account_country_bosnia_and_herzegovina));
        countryList.add(getResources().getString(R.string.edit_account_country_bulgaria));
        countryList.add(getResources().getString(R.string.edit_account_country_croatia));
        countryList.add(getResources().getString(R.string.edit_account_country_cyprus));
        countryList.add(getResources().getString(R.string.edit_account_country_czech_republic));
        countryList.add(getResources().getString(R.string.edit_account_country_denmark));
        countryList.add(getResources().getString(R.string.edit_account_country_estonia));
        countryList.add(getResources().getString(R.string.edit_account_country_finland));
        countryList.add(getResources().getString(R.string.edit_account_country_france));
        countryList.add(getResources().getString(R.string.edit_account_country_georgia));
        countryList.add(getResources().getString(R.string.edit_account_country_germany));
        countryList.add(getResources().getString(R.string.edit_account_country_greece));
        countryList.add(getResources().getString(R.string.edit_account_country_hungary));
        countryList.add(getResources().getString(R.string.edit_account_country_iceland));
        countryList.add(getResources().getString(R.string.edit_account_country_ireland));
        countryList.add(getResources().getString(R.string.edit_account_country_italy));
        countryList.add(getResources().getString(R.string.edit_account_country_kazakhstan));
        countryList.add(getResources().getString(R.string.edit_account_country_latvia));
        countryList.add(getResources().getString(R.string.edit_account_country_liechtenstein));
        countryList.add(getResources().getString(R.string.edit_account_country_lithuania));
        countryList.add(getResources().getString(R.string.edit_account_country_luxembourg));
        countryList.add(getResources().getString(R.string.edit_account_country_malta));
        countryList.add(getResources().getString(R.string.edit_account_country_moldova));
        countryList.add(getResources().getString(R.string.edit_account_country_monaco));
        countryList.add(getResources().getString(R.string.edit_account_country_montenegro));
        countryList.add(getResources().getString(R.string.edit_account_country_netherlands));
        countryList.add(getResources().getString(R.string.edit_account_country_north_macedonia));
        countryList.add(getResources().getString(R.string.edit_account_country_norway));
        countryList.add(getResources().getString(R.string.edit_account_country_poland));
        countryList.add(getResources().getString(R.string.edit_account_country_portugal));
        countryList.add(getResources().getString(R.string.edit_account_country_romania));
        countryList.add(getResources().getString(R.string.edit_account_country_russia));
        countryList.add(getResources().getString(R.string.edit_account_country_san_marino));
        countryList.add(getResources().getString(R.string.edit_account_country_serbia));
        countryList.add(getResources().getString(R.string.edit_account_country_slovakia));
        countryList.add(getResources().getString(R.string.edit_account_country_slovenia));
        countryList.add(getResources().getString(R.string.edit_account_country_spain));
        countryList.add(getResources().getString(R.string.edit_account_country_sweden));
        countryList.add(getResources().getString(R.string.edit_account_country_switzerland));
        countryList.add(getResources().getString(R.string.edit_account_country_turkey));
        countryList.add(getResources().getString(R.string.edit_account_country_ukraine));
        countryList.add(getResources().getString(R.string.edit_account_country_uk));
        countryList.add(getResources().getString(R.string.edit_account_country_vatican_city));
        countryList.add(getResources().getString(R.string.edit_account_country_select_country));

        Collections.sort(countryList);
        country.setAdapter(countryAdapter);
    }

    private void setSelectedItemColorSpinner(Spinner s) // stilizarea primului element al spinnerului
    {
        AdapterView.OnItemSelectedListener itemSelectedListener=new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id)
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
                                int color;

                                if(!darkThemeEnabled)
                                    color=Color.parseColor("#195190");
                                else color=Color.WHITE;

                                ((TextView) parent.getChildAt(0)).setTextColor(color); // primul element va avea textul aliniat la stanga si culoarea in functie de tema selectata
                                ((TextView) parent.getChildAt(0)).setGravity(Gravity.START);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        };

        s.setOnItemSelectedListener(itemSelectedListener);
    }

    private void setGenderSpinner()
    {
        ArrayAdapter<String> genderAdapter=new ArrayAdapter<String>(this, R.layout.custom_spinner_item, genderList)
        {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                final View v=super.getDropDownView(position, convertView, parent);
                ((TextView)v).setGravity(Gravity.CENTER); // toate elementele spinnerului sunt aliniate la centru

                if(fbAuth.getUid()!=null)
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
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

        genderList.add(getResources().getString(R.string.edit_account_gender_female));
        genderList.add(getResources().getString(R.string.edit_account_gender_male));
        genderList.add(getResources().getString(R.string.edit_account_gender_other));
        genderList.add(getResources().getString(R.string.edit_account_gender_select));

        Collections.sort(genderList);
        gender.setAdapter(genderAdapter);
    }

    private String getGenderInEnglish(int positionInGenderList)
    {
        String gender=genderList.get(positionInGenderList);

        if(gender.equals(getResources().getString(R.string.edit_account_gender_female)))
            return "Female";
        if(gender.equals(getResources().getString(R.string.edit_account_gender_male)))
            return "Male";
        if(gender.equals(getResources().getString(R.string.edit_account_gender_other)))
            return "Other";
        else return null;
    }

    private String getCountryNameInEnglish(int positionInCountryList)
    {
        String countryName=countryList.get(positionInCountryList);

        if(countryName.equals(getResources().getString(R.string.edit_account_country_albania)))
            return "Albania";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_andorra)))
            return "Andorra";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_armenia)))
            return "Armenia";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_austria)))
            return "Austria";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_azerbaijan)))
            return "Azerbaijan";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_belarus)))
            return "Belarus";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_belgium)))
            return "Belgium";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_bosnia_and_herzegovina)))
            return "Bosnia and Herzegovina";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_bulgaria)))
            return "Bulgaria";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_croatia)))
            return "Croatia";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_cyprus)))
            return "Cyprus";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_czech_republic)))
            return "Czech Republic";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_denmark)))
            return "Denmark";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_estonia)))
            return "Estonia";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_finland)))
            return "Finland";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_france)))
            return "France";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_georgia)))
            return "Georgia";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_germany)))
            return "Germany";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_greece)))
            return "Greece";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_hungary)))
            return "Hungary";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_iceland)))
            return "Iceland";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_ireland)))
            return "Ireland";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_italy)))
            return "Italy";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_kazakhstan)))
            return "Kazakhstan";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_latvia)))
            return "Latvia";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_liechtenstein)))
            return "Liechtenstein";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_lithuania)))
            return "Lithuania";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_luxembourg)))
            return "Luxembourg";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_malta)))
            return "Malta";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_moldova)))
            return "Moldova";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_monaco)))
            return "Monaco";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_montenegro)))
            return "Montenegro";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_netherlands)))
            return "Netherlands";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_north_macedonia)))
            return "North Macedonia";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_norway)))
            return "Norway";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_poland)))
            return "Poland";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_portugal)))
            return "Portugal";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_romania)))
            return "Romania";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_russia)))
            return "Russia";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_san_marino)))
            return "San Marino";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_serbia)))
            return "Serbia";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_slovakia)))
            return "Slovakia";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_slovenia)))
            return "Slovenia";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_spain)))
            return "Spain";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_sweden)))
            return "Sweden";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_switzerland)))
            return "Switzerland";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_turkey)))
            return "Turkey";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_ukraine)))
            return "Ukraine";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_uk)))
            return "United Kingdom";
        else if(countryName.equals(getResources().getString(R.string.edit_account_country_vatican_city)))
            return "Vatican City";
        else return null;

    }

    private int getPositionInGenderList(String gender)
    {
        String translatedGender;

        switch(gender)
        {
            case "Female":
                translatedGender=getResources().getString(R.string.edit_account_gender_female);
                break;
            case "Male":
                translatedGender=getResources().getString(R.string.edit_account_gender_male);
                break;
            case "Other":
                translatedGender=getResources().getString(R.string.edit_account_gender_other);
                break;
            default:
                translatedGender=" ";
                break;
        }

        return Collections.binarySearch(genderList, translatedGender);
    }

    private int getPositionInCountryList(String countryName)
    {
        String translatedCountryName;

        switch(countryName)
        {
            case "Albania":
                translatedCountryName=getResources().getString(R.string.edit_account_country_albania);
                break;
            case "Andorra":
                translatedCountryName=getResources().getString(R.string.edit_account_country_andorra);
                break;
            case "Armenia":
                translatedCountryName=getResources().getString(R.string.edit_account_country_armenia);
                break;
            case "Austria":
                translatedCountryName=getResources().getString(R.string.edit_account_country_austria);
                break;
            case "Azerbaijan":
                translatedCountryName=getResources().getString(R.string.edit_account_country_azerbaijan);
                break;
            case "Belarus":
                translatedCountryName=getResources().getString(R.string.edit_account_country_belarus);
                break;
            case "Belgium":
                translatedCountryName=getResources().getString(R.string.edit_account_country_belgium);
                break;
            case "Bosnia and Herzegovina":
                translatedCountryName=getResources().getString(R.string.edit_account_country_bosnia_and_herzegovina);
                break;
            case "Bulgaria":
                translatedCountryName=getResources().getString(R.string.edit_account_country_bulgaria);
                break;
            case "Croatia":
                translatedCountryName=getResources().getString(R.string.edit_account_country_croatia);
                break;
            case "Cyprus":
                translatedCountryName=getResources().getString(R.string.edit_account_country_cyprus);
                break;
            case "Czech Republic":
                translatedCountryName=getResources().getString(R.string.edit_account_country_czech_republic);
                break;
            case "Denmark":
                translatedCountryName=getResources().getString(R.string.edit_account_country_denmark);
                break;
            case "Estonia":
                translatedCountryName=getResources().getString(R.string.edit_account_country_estonia);
                break;
            case "Finland":
                translatedCountryName=getResources().getString(R.string.edit_account_country_finland);
                break;
            case "France":
                translatedCountryName=getResources().getString(R.string.edit_account_country_france);
                break;
            case "Georgia":
                translatedCountryName=getResources().getString(R.string.edit_account_country_georgia);
                break;
            case "Germany":
                translatedCountryName=getResources().getString(R.string.edit_account_country_germany);
                break;
            case "Greece":
                translatedCountryName=getResources().getString(R.string.edit_account_country_greece);
                break;
            case "Hungary":
                translatedCountryName=getResources().getString(R.string.edit_account_country_hungary);
                break;
            case "Iceland":
                translatedCountryName=getResources().getString(R.string.edit_account_country_iceland);
                break;
            case "Ireland":
                translatedCountryName=getResources().getString(R.string.edit_account_country_ireland);
                break;
            case "Italy":
                translatedCountryName=getResources().getString(R.string.edit_account_country_italy);
                break;
            case "Kazakhstan":
                translatedCountryName=getResources().getString(R.string.edit_account_country_kazakhstan);
                break;
            case "Latvia":
                translatedCountryName=getResources().getString(R.string.edit_account_country_latvia);
                break;
            case "Liechtenstein":
                translatedCountryName=getResources().getString(R.string.edit_account_country_liechtenstein);
                break;
            case "Lithuania":
                translatedCountryName=getResources().getString(R.string.edit_account_country_lithuania);
                break;
            case "Luxembourg":
                translatedCountryName=getResources().getString(R.string.edit_account_country_luxembourg);
                break;
            case "Malta":
                translatedCountryName=getResources().getString(R.string.edit_account_country_malta);
                break;
            case "Moldova":
                translatedCountryName=getResources().getString(R.string.edit_account_country_moldova);
                break;
            case "Monaco":
                translatedCountryName=getResources().getString(R.string.edit_account_country_monaco);
                break;
            case "Montenegro":
                translatedCountryName=getResources().getString(R.string.edit_account_country_montenegro);
                break;
            case "Netherlands":
                translatedCountryName=getResources().getString(R.string.edit_account_country_netherlands);
                break;
            case "North Macedonia":
                translatedCountryName=getResources().getString(R.string.edit_account_country_north_macedonia);
                break;
            case "Norway":
                translatedCountryName=getResources().getString(R.string.edit_account_country_norway);
                break;
            case "Poland":
                translatedCountryName=getResources().getString(R.string.edit_account_country_poland);
                break;
            case "Portugal":
                translatedCountryName=getResources().getString(R.string.edit_account_country_portugal);
                break;
            case "Romania":
                translatedCountryName=getResources().getString(R.string.edit_account_country_romania);
                break;
            case "Russia":
                translatedCountryName=getResources().getString(R.string.edit_account_country_russia);
                break;
            case "San Marino":
                translatedCountryName=getResources().getString(R.string.edit_account_country_san_marino);
                break;
            case "Serbia":
                translatedCountryName=getResources().getString(R.string.edit_account_country_serbia);
                break;
            case "Slovakia":
                translatedCountryName=getResources().getString(R.string.edit_account_country_slovakia);
                break;
            case "Slovenia":
                translatedCountryName=getResources().getString(R.string.edit_account_country_slovenia);
                break;
            case "Spain":
                translatedCountryName=getResources().getString(R.string.edit_account_country_spain);
                break;
            case "Sweden":
                translatedCountryName=getResources().getString(R.string.edit_account_country_sweden);
                break;
            case "Switzerland":
                translatedCountryName=getResources().getString(R.string.edit_account_country_switzerland);
                break;
            case "Turkey":
                translatedCountryName=getResources().getString(R.string.edit_account_country_turkey);
                break;
            case "Ukraine":
                translatedCountryName=getResources().getString(R.string.edit_account_country_ukraine);
                break;
            case "United Kingdom":
                translatedCountryName=getResources().getString(R.string.edit_account_country_uk);
                break;
            case "Vatican City":
                translatedCountryName=getResources().getString(R.string.edit_account_country_vatican_city);
                break;
            default:
                translatedCountryName=" ";
                break;
        }

        return Collections.binarySearch(countryList, translatedCountryName);
    }
}