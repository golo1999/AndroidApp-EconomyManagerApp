package com.example.economy_manager.utilities;

import com.example.economy_manager.models.ApplicationSettings;
import com.example.economy_manager.models.PersonalInformation;
import com.example.economy_manager.models.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class MyCustomVariables {
    private MyCustomVariables() {

    }

    private static final String SHARED_PREFERENCES_FILE_NAME = "ECONOMY_MANAGER_USER_DATA";
    private static final FirebaseAuth FIREBASE_AUTH = FirebaseAuth.getInstance();
    private static final DatabaseReference DATABASE_REFERENCE = FirebaseDatabase.getInstance().getReference();
    private static UserDetails userDetails;
    private static final String defaultCurrency = "GBP";
    private static final UserDetails defaultUserDetails =
            new UserDetails(new ApplicationSettings(), new PersonalInformation());

    public static String getSharedPreferencesFileName() {
        return SHARED_PREFERENCES_FILE_NAME;
    }

    public static FirebaseAuth getFirebaseAuth() {
        return FIREBASE_AUTH;
    }

    public static DatabaseReference getDatabaseReference() {
        return DATABASE_REFERENCE;
    }

    public static UserDetails getUserDetails() {
        return userDetails;
    }

    public static void setUserDetails(UserDetails userDetails) {
        MyCustomVariables.userDetails = userDetails;
    }

    public static String getDefaultCurrency() {
        return defaultCurrency;
    }

    public static UserDetails getDefaultUserDetails() {
        return defaultUserDetails;
    }
}