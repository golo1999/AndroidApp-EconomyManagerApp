package com.example.economy_manager.main_part.views.activities;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.R;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.main_part.viewmodels.EditAccountViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EditAccountActivity extends AppCompatActivity {
    private EditAccountViewModel editAccountViewModel;
    private UserDetails userDetails;
    private final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private ImageView goBack;
    private ImageView accountPhoto;
    private ImageView editFirstName;
    private ImageView editLastName;
    private ImageView editPhone;
    private ImageView editWebsite;
    private ImageView editCountry;
    private ImageView editGender;
    private ImageView editBirthDate;
    private ImageView editCareerTitle;
    private TextView topText;
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private EditText website;
    private EditText birthDate;
    private EditText careerTitle;
    private Spinner country;
    private Spinner gender;
    private int firstNameClicked;
    private int lastNameClicked;
    private int phoneClicked;
    private int websiteClicked;
    private int countryClicked;
    private int genderClicked;
    private int birthDateClicked;
    private int careerTitleClicked;
    private boolean darkThemeEnabled;
    private Button updateProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account_remastered_activity);
        setVariables();
        setTheme();
        setOnClickListeners();
        setTopText();
        setCountrySpinner();
        setGenderSpinner();
        getPersonalInformation();
        //makeAllInformationNotClickable();
        setSelectedItemColorSpinner(country);
        setSelectedItemColorSpinner(gender);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setVariables() {
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);
        editAccountViewModel = new EditAccountViewModel(getApplication(), userDetails);
        goBack = findViewById(R.id.editAccountRemasteredBack);
        firstName = findViewById(R.id.editAccountRemasteredFirstNameField);
        lastName = findViewById(R.id.editAccountRemasteredLastNameField);
        phoneNumber = findViewById(R.id.editAccountRemasteredPhoneField);
        website = findViewById(R.id.editAccountRemasteredWebsiteField);
        birthDate = findViewById(R.id.editAccountRemasteredBirthDateField);
        careerTitle = findViewById(R.id.editAccountRemasteredCareerTitleField);
        country = findViewById(R.id.editAccountRemasteredCountrySpinner);
        gender = findViewById(R.id.editAccountRemasteredGenderSpinner);
        accountPhoto = findViewById(R.id.editAccountRemasteredPhoto);
//        editFirstName = findViewById(R.id.editAccountFirstNameEdit);
//        editLastName = findViewById(R.id.editAccountLastNameEdit);
//        editPhone = findViewById(R.id.editAccountPhoneEdit);
//        editWebsite = findViewById(R.id.editAccountWebsiteEdit);
//        editCountry = findViewById(R.id.editAccountCountryEdit);
//        editGender = findViewById(R.id.editAccountGenderEdit);
//        editBirthDate = findViewById(R.id.editAccountBirthDateEdit);
//        editCareerTitle = findViewById(R.id.editAccountTitleEdit);
        firstNameClicked = lastNameClicked = phoneClicked = websiteClicked = countryClicked =
                genderClicked = birthDateClicked = careerTitleClicked = 0;
        topText = findViewById(R.id.editAccountRemasteredTopText);
        updateProfileButton = findViewById(R.id.editAccountRemasteredUpdateButton);
    }

    private void setOnClickListeners() {
        accountPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(EditAccountActivity.this, EditPhotoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        goBack.setOnClickListener(v -> onBackPressed());

//        editFirstName.setOnClickListener(v -> {
//            firstNameClicked++;
//
//            if (firstNameClicked % 2 != 0)
//                ifClickedIsEven(firstName, editFirstName);
//            else if (firstNameClicked != 0) {
//                ifClickedIsOdd(firstName, editFirstName);
//                updateInformationEditText(firstName, "firstName");
//            }
//        });
//
//        editLastName.setOnClickListener(v -> {
//            lastNameClicked++;
//
//            if (lastNameClicked % 2 != 0)
//                ifClickedIsEven(lastName, editLastName);
//            else if (lastNameClicked != 0) {
//                ifClickedIsOdd(lastName, editLastName);
//                updateInformationEditText(lastName, "lastName");
//            }
//        });
//
//        editPhone.setOnClickListener(v -> {
//            phoneClicked++;
//
//            if (phoneClicked % 2 != 0)
//                ifClickedIsEven(phoneNumber, editPhone);
//            else if (phoneClicked != 0) {
//                ifClickedIsOdd(phoneNumber, editPhone);
//                updateInformationPhone();
//            }
//        });
//
//        editWebsite.setOnClickListener(v -> {
//            websiteClicked++;
//
//            if (websiteClicked % 2 != 0)
//                ifClickedIsEven(website, editWebsite);
//            else if (websiteClicked != 0) {
//                ifClickedIsOdd(website, editWebsite);
//                updateInformationEditText(website, "website");
//            }
//        });
//
//        editCountry.setOnClickListener(v -> {
//            countryClicked++;
//
//            if (countryClicked % 2 != 0)
//                ifClickedIsEven(country, editCountry);
//            else if (countryClicked != 0) {
//                ifClickedIsOdd(country, editCountry);
//
//                if (fbAuth.getUid() != null && !String.valueOf(country.getSelectedItem()).trim().isEmpty() && !String.valueOf(country.getSelectedItem()).trim().equals("") && country.getSelectedItem() != null)
//                    if (Locale.getDefault().getDisplayLanguage().equals("English"))
//                        myRef.child(fbAuth.getUid()).child("PersonalInformation").child("country").setValue(String.valueOf(country.getSelectedItem()).trim());
//                    else if (getCountryNameInEnglish(country.getSelectedItemPosition()) != null)
//                        myRef.child(fbAuth.getUid()).child("PersonalInformation").child("country").setValue(getCountryNameInEnglish(country.getSelectedItemPosition()));
//            }
//        });
//
//        editGender.setOnClickListener(v -> {
//            genderClicked++;
//
//            if (genderClicked % 2 != 0)
//                ifClickedIsEven(gender, editGender);
//            else if (genderClicked != 0) {
//                ifClickedIsOdd(gender, editGender);
//
//                if (fbAuth.getUid() != null && !String.valueOf(gender.getSelectedItem()).trim().isEmpty() && !String.valueOf(gender.getSelectedItem()).trim().equals("") && gender.getSelectedItem() != null)
//                    if (Locale.getDefault().getDisplayLanguage().equals("English"))
//                        myRef.child(fbAuth.getUid()).child("PersonalInformation").child("gender").setValue(String.valueOf(gender.getSelectedItem()).trim());
//                    else if (getGenderInEnglish(gender.getSelectedItemPosition()) != null)
//                        myRef.child(fbAuth.getUid()).child("PersonalInformation").child("gender").setValue(getGenderInEnglish(gender.getSelectedItemPosition()));
//            }
//        });
//
//        editBirthDate.setOnClickListener(v -> {
//            birthDateClicked++;
//
//            if (birthDateClicked % 2 != 0)
//                ifClickedIsEven(birthDate, editBirthDate);
//            else if (birthDateClicked != 0) {
//                ifClickedIsOdd(birthDate, editBirthDate);
//                if (fbAuth.getUid() != null && !String.valueOf(birthDate.getText()).isEmpty() && isValidDate(String.valueOf(birthDate.getText())))
//                    myRef.child(fbAuth.getUid()).child("PersonalInformation").child("birthDate").setValue(String.valueOf(birthDate.getText()));
//            }
//        });
//
//        editCareerTitle.setOnClickListener(v -> {
//            careerTitleClicked++;
//
//            if (careerTitleClicked % 2 != 0)
//                ifClickedIsEven(careerTitle, editCareerTitle);
//            else if (careerTitleClicked != 0) {
//                ifClickedIsOdd(careerTitle, editCareerTitle);
//                updateInformationEditText(careerTitle, "careerTitle");
//            }
//        });

        updateProfileButton.setOnClickListener(view -> {
            //PersonalInformation information = new PersonalInformation(1234);
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void getPersonalInformation() {
        if (userDetails != null) {
            if (userDetails.getPersonalInformation().getPhoneNumber().trim().equals(""))
                accountPhoto.setImageResource(R.drawable.ic_add_photo);
//            Picasso.get().load(!userDetails.getPersonalInformation().getPhotoURL().trim().equals("") ?
//                    userDetails.getPersonalInformation().getPhotoURL().trim() :
//                    "").placeholder(R.drawable.ic_add_photo).fit().into(accountPhoto);
            firstName.setHint(!userDetails.getPersonalInformation().getFirstName().trim().equals("") ?
                    userDetails.getPersonalInformation().getFirstName().trim() :
                    getResources().getString(R.string.edit_account_first_name).trim());
            lastName.setHint(!userDetails.getPersonalInformation().getLastName().trim().equals("") ?
                    userDetails.getPersonalInformation().getLastName().trim() :
                    getResources().getString(R.string.edit_account_last_name).trim());
            phoneNumber.setHint(!userDetails.getPersonalInformation().getPhoneNumber().trim().equals("") ?
                    userDetails.getPersonalInformation().getPhoneNumber().trim() :
                    getResources().getString(R.string.edit_account_phone_number).trim());
            website.setHint(!userDetails.getPersonalInformation().getWebsite().trim().equals("") ?
                    userDetails.getPersonalInformation().getWebsite().trim() :
                    getResources().getString(R.string.edit_account_website).trim());

            country.setSelection(editAccountViewModel.getPositionInCountryList(getApplication(),
                    userDetails.getPersonalInformation().getCountry().trim()));
            gender.setSelection(editAccountViewModel.getPositionInGenderList(getApplication(),
                    userDetails.getPersonalInformation().getGender().trim()));

            birthDate.setHint(!userDetails.getPersonalInformation().getBirthDate().toString().trim().equals("") ?
                    userDetails.getPersonalInformation().getBirthDate().toString().trim() :
                    getResources().getString(R.string.edit_account_birth_date).trim());
            careerTitle.setHint(!userDetails.getPersonalInformation().getCareerTitle().trim().equals("") ?
                    userDetails.getPersonalInformation().getCareerTitle().trim() :
                    getResources().getString(R.string.edit_account_career_title).trim());
        }

        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).child("PersonalInformation").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
//                        String firstname = String.valueOf(snapshot.child("firstName").getValue());
//                        String lastname = String.valueOf(snapshot.child("lastName").getValue());
//                        String phonenumber = String.valueOf(snapshot.child("phoneNumber").getValue());
//                        String web = String.valueOf(snapshot.child("website").getValue());
//                        String count = String.valueOf(snapshot.child("country").getValue());
//                        String gend = String.valueOf(snapshot.child("gender").getValue());
//                        String birthdate = String.valueOf(snapshot.child("birthDate").getValue());
//                        String careertitle = String.valueOf(snapshot.child("careerTitle").getValue());
//                        String photourl = String.valueOf(snapshot.child("photoURL").getValue());
//                        PersonalInformation information = new PersonalInformation(firstname, lastname, phonenumber, web, count, gend, birthdate, careertitle, photourl);
//
//                        if (!firstname.trim().equals(""))
//                            firstName.setHint(String.valueOf(information.getFirstName()));
//                        else
//                            firstName.setHint(getResources().getString(R.string.edit_account_first_name).trim());
//                        if (!lastname.trim().equals(""))
//                            lastName.setHint(String.valueOf(information.getLastName()));
//                        else
//                            lastName.setHint(getResources().getString(R.string.edit_account_last_name).trim());
//                        if (!phonenumber.trim().equals(""))
//                            phoneNumber.setHint(String.valueOf(information.getPhoneNumber()));
//                        else
//                            phoneNumber.setHint(getResources().getString(R.string.edit_account_phone_number).trim());
//                        if (!web.trim().equals(""))
//                            website.setHint(String.valueOf(information.getWebsite()));
//                        else
//                            website.setHint(getResources().getString(R.string.edit_account_website).trim());
//                        if (!birthdate.trim().equals("") && isValidDate(birthdate))
//                            birthDate.setHint(String.valueOf(information.getBirthDate()));
//                        else
//                            birthDate.setHint(getResources().getString(R.string.edit_account_birth_date).trim());
//                        if (!careertitle.trim().equals(""))
//                            careerTitle.setHint(String.valueOf(information.getCareerTitle()));
//                        else
//                            careerTitle.setHint(getResources().getString(R.string.edit_account_career_title).trim());
//
//                        country.setSelection(getPositionInCountryList(count));
//                        gender.setSelection(getPositionInGenderList(gend));
//
//                        if (photourl.trim().equals(""))
//                            //Picasso.get().load(R.drawable.ic_account).placeholder(R.drawable.ic_account).fit().into(accountPhoto);
//                            Picasso.get().load("https://www.pexels.com/photo/grayscale-photo-of-ferris-wheel-3673785/").placeholder(R.drawable.ic_add_photo).fit().into(accountPhoto);
//                        else
//                            Picasso.get().load(photourl).placeholder(R.drawable.ic_add_photo).fit().into(accountPhoto);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void makeAllInformationNotClickable() {
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

    private void ifClickedIsEven(View field, final ImageView edit) {
        field.setEnabled(true);

        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("darkTheme")) {
                        boolean darkThemeEnabled = Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()));
                        int color;

                        if (!darkThemeEnabled)
                            color = R.drawable.ic_save_edit_blue;
                        else color = R.drawable.ic_save_edit;

                        edit.setImageResource(color);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void ifClickedIsOdd(View field, final ImageView edit) {
        field.setEnabled(false);

        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("darkTheme")) {
                        boolean darkThemeEnabled = Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()));
                        int color = !darkThemeEnabled ?
                                R.drawable.ic_edit_blue : R.drawable.ic_edit;

                        edit.setImageResource(color);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void updateInformationEditText(EditText field, String childPath) {
        if (fbAuth.getUid() != null && !String.valueOf(field.getText()).isEmpty() && !String.valueOf(field.getText()).trim().equals(""))
            myRef.child(fbAuth.getUid()).child("PersonalInformation").child(childPath).setValue(String.valueOf(field.getText()));
    }

    private void updateInformationPhone() // de refacut
    {
        if (fbAuth.getUid() != null && !String.valueOf(phoneNumber.getText()).isEmpty() && !String.valueOf(phoneNumber.getText()).trim().equals("") && PhoneNumberUtils.isGlobalPhoneNumber(String.valueOf(phoneNumber.getText()).trim()))
            myRef.child(fbAuth.getUid()).child("PersonalInformation").child("phoneNumber").setValue(String.valueOf(phoneNumber.getText()));
        else if (!PhoneNumberUtils.isGlobalPhoneNumber(String.valueOf(phoneNumber.getText()).trim()))
            Toast.makeText(EditAccountActivity.this, getResources().getString(R.string.edit_account_incorrect_phone_number), Toast.LENGTH_SHORT).show();
    }

    private boolean isValidDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(EditAccountActivity.this, getResources().getString(R.string.edit_account_enter_valid_birth_date), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setTopText() {
        String text = getResources().getString(R.string.edit_account).trim();
        topText.setText(text);
        topText.setTextSize(20);
        topText.setTextColor(Color.WHITE);
    }

    private void setTheme() {
        if (userDetails != null) {
            darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
            int theme = !darkThemeEnabled ?
                    R.drawable.ic_white_gradient_tobacco_ad :
                    R.drawable.ic_black_gradient_night_shift;
            int color = !darkThemeEnabled ?
                    // turkish sea (albastru)
                    Color.parseColor("#195190") : Color.WHITE;
            int editResource = !darkThemeEnabled ? R.drawable.ic_edit_blue : R.drawable.ic_edit;
            int dropDownTheme = !darkThemeEnabled ?
                    R.drawable.ic_blue_gradient_unloved_teen :
                    R.drawable.ic_white_gradient_tobacco_ad;

            getWindow().setBackgroundDrawableResource(theme);

            // setam culoarea sagetii
            country.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            // setam culoarea elementelor
            country.setPopupBackgroundResource(dropDownTheme);
            gender.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            gender.setPopupBackgroundResource(dropDownTheme);

            setEditTextColor(firstName, color);
            setEditTextColor(lastName, color);
            setEditTextColor(phoneNumber, color);
            setEditTextColor(website, color);
            setEditTextColor(birthDate, color);
            setEditTextColor(careerTitle, color);
        }

//        if (fbAuth.getUid() != null)
//            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists())
//                        if (snapshot.hasChild("darkTheme")) {
//                            darkThemeEnabled = Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()));
//                            int color, theme, editResource, dropDownTheme;
//
//                            if (!darkThemeEnabled) {
//                                theme = R.drawable.ic_white_gradient_tobacco_ad;
//                                color = Color.parseColor("#195190"); // turkish sea (albastru)
//                                editResource = R.drawable.ic_edit_blue;
//                                dropDownTheme = R.drawable.ic_blue_gradient_unloved_teen;
//                            } else {
//                                theme = R.drawable.ic_black_gradient_night_shift;
//                                color = Color.WHITE;
//                                editResource = R.drawable.ic_edit;
//                                dropDownTheme = R.drawable.ic_white_gradient_tobacco_ad;
//                            }
//
//                            getWindow().setBackgroundDrawableResource(theme); // setam culoarea dropdown
//
//                            country.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP); // setam culoarea sagetii
//                            country.setPopupBackgroundResource(dropDownTheme); // setam culoarea elementelor
//                            gender.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
//                            gender.setPopupBackgroundResource(dropDownTheme);
//
////                            editFirstName.setImageResource(editResource);
////                            editLastName.setImageResource(editResource);
////                            editPhone.setImageResource(editResource);
////                            editWebsite.setImageResource(editResource);
////                            editCountry.setImageResource(editResource);
////                            editGender.setImageResource(editResource);
////                            editBirthDate.setImageResource(editResource);
////                            editCareerTitle.setImageResource(editResource);
//
//                            setEditTextColor(firstName, color);
//                            setEditTextColor(lastName, color);
//                            setEditTextColor(phoneNumber, color);
//                            setEditTextColor(website, color);
//                            setEditTextColor(birthDate, color);
//                            setEditTextColor(careerTitle, color);
//                        }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
    }

    private void setEditTextColor(EditText editText, int color) {
        editText.setHintTextColor(color);
        editText.setTextColor(color);
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void setCountrySpinner() {
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item, editAccountViewModel.getCountryList()) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView,
                                        @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                // toate elementele spinnerului sunt aliniate la centru
                ((TextView) v).setGravity(Gravity.CENTER);

                if (userDetails != null) {
                    if (userDetails.getApplicationSettings().getDarkTheme() != darkThemeEnabled) {
                        darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                    }

                    int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;

                    // setam culoarea textului elementelor in functie de tema selectata
                    ((TextView) v).setTextColor(itemsColor);
                }

                return v;
            }
        };

        country.setAdapter(countryAdapter);
    }

    // stilizarea primului element al spinnerului
    private void setSelectedItemColorSpinner(Spinner s) {
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position,
                                       long id) {
                if (userDetails != null) {
                    if (userDetails.getApplicationSettings().getDarkTheme() != darkThemeEnabled) {
                        darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                    }

                    int color = !darkThemeEnabled ?
                            Color.parseColor("#195190") : Color.WHITE;

                    // primul element va avea textul aliniat la stanga si
                    // culoarea in functie de tema selectata
                    ((TextView) parent.getChildAt(0)).setTextColor(color);
                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.START);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        s.setOnItemSelectedListener(itemSelectedListener);
    }

    private void setGenderSpinner() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item, editAccountViewModel.getGenderList()) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView,
                                        @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                // toate elementele spinnerului sunt aliniate la centru
                ((TextView) v).setGravity(Gravity.CENTER);

                if (userDetails != null) {
                    if (userDetails.getApplicationSettings().getDarkTheme() != darkThemeEnabled) {
                        darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                    }

                    int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;

                    // setam culoarea textului elementelor in functie de tema selectata
                    ((TextView) v).setTextColor(itemsColor);
                }

                return v;
            }
        };

        gender.setAdapter(genderAdapter);
    }
}