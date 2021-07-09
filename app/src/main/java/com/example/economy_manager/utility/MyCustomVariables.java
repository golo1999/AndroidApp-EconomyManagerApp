package com.example.economy_manager.utility;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class MyCustomVariables {
    private MyCustomVariables() {

    }

    private static final String SHARED_PREFERENCES_FILE_NAME = "ECONOMY_MANAGER_USER_DATA";
    private static final FirebaseAuth FIREBASE_AUTH = FirebaseAuth.getInstance();
    private static final DatabaseReference DATABASE_REFERENCE = FirebaseDatabase.getInstance().getReference();

    public static String getSharedPreferencesFileName() {
        return SHARED_PREFERENCES_FILE_NAME;
    }

    public static FirebaseAuth getFirebaseAuth() {
        return FIREBASE_AUTH;
    }

    public static DatabaseReference getDatabaseReference() {
        return DATABASE_REFERENCE;
    }
}