package com.example.EconomyManager.ApplicationPart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.EconomyManager.ApplicationSettings;
import com.example.EconomyManager.LoginPart.LogIn;
import com.example.EconomyManager.PersonalInformation;
import com.example.EconomyManager.R;
import com.example.EconomyManager.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class ActivitySplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        LogoLauncher launcher = new LogoLauncher();
        launcher.start();
    }

    public class LogoLauncher extends Thread {
        private SharedPreferences preferences =
                getSharedPreferences("ECONOMY_MANAGER_USER_DATA", MODE_PRIVATE);

        public void run() {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            FirebaseAuth fbAuth = FirebaseAuth.getInstance();
            Intent intent = fbAuth.getCurrentUser() != null ?
                    new Intent(ActivitySplashScreen.this,
                            ActivityMainScreen.class) :
                    new Intent(ActivitySplashScreen.this, LogIn.class);

            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (fbAuth.getCurrentUser() != null && fbAuth.getUid() != null) {
                myRef.child(fbAuth.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists() &&
                                        snapshot.hasChild("ApplicationSettings") &&
                                        snapshot.hasChild("PersonalInformation")) {
                                    ApplicationSettings applicationSettings = snapshot
                                            .child("ApplicationSettings")
                                            .getValue(ApplicationSettings.class);
                                    PersonalInformation personalInformation = snapshot
                                            .child("PersonalInformation")
                                            .getValue(PersonalInformation.class);

                                    if (applicationSettings != null &&
                                            personalInformation != null) {
                                        UserDetails details = new UserDetails(applicationSettings,
                                                personalInformation);

                                        if (!checkIfUserDetailsAlreadyExistInSharedPreferences(details)) {
                                            saveUserDetailsToSharedPreferences(details);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            startActivity(intent);
            finish();
        }

        private void saveUserDetailsToSharedPreferences(UserDetails details) {
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(details);

            editor.putString("currentUserDetails", json);
            editor.apply();
        }

        private UserDetails retrieveUserDetailsFromSharedPreferences() {
            SharedPreferences preferences =
                    getSharedPreferences("ECONOMY_MANAGER_USER_DATA", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = preferences.getString("currentUserDetails", "");

            return gson.fromJson(json, UserDetails.class);
        }

        private boolean checkIfUserDetailsAlreadyExistInSharedPreferences(UserDetails details) {
            UserDetails userDetailsFromSharedPreferences = retrieveUserDetailsFromSharedPreferences();

//            Log.d("userDetailsAreEqual", String.valueOf(details.equals(userDetailsFromSharedPreferences)));
//
//            Log.d("userDetails1", details.toString());
//            Log.d("userDetails2", userDetailsFromSharedPreferences.toString());

            return details.equals(userDetailsFromSharedPreferences);
        }
    }
}