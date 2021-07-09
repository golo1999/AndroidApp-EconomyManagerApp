package com.example.economy_manager.main_part.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.model.ApplicationSettings;
import com.example.economy_manager.login_part.ActivityLogIn;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.model.PersonalInformation;
import com.example.economy_manager.R;
import com.example.economy_manager.model.UserDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class ActivitySplashScreen extends AppCompatActivity {
    private final LogoLauncher launcher = new LogoLauncher();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startSplashScreen();
    }

    private void startSplashScreen() {
        launcher.start();
    }

    public class LogoLauncher extends Thread {
        private SharedPreferences preferences;

        public void run() {
            preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
            final Intent intent = new Intent(ActivitySplashScreen.this,
                    MyCustomVariables.getFirebaseAuth().getCurrentUser() != null ?
                            ActivityMainScreen.class : ActivityLogIn.class);

            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null &&
                    MyCustomVariables.getFirebaseAuth().getUid() != null) {
                MyCustomVariables.getDatabaseReference().child(MyCustomVariables.getFirebaseAuth().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final @NonNull DataSnapshot snapshot) {
                                if (snapshot.exists() &&
                                        snapshot.hasChild("ApplicationSettings") &&
                                        snapshot.hasChild("PersonalInformation")) {
                                    final ApplicationSettings applicationSettings = snapshot
                                            .child("ApplicationSettings")
                                            .getValue(ApplicationSettings.class);
                                    final PersonalInformation personalInformation = snapshot
                                            .child("PersonalInformation")
                                            .getValue(PersonalInformation.class);

                                    if (applicationSettings != null &&
                                            personalInformation != null) {
                                        final UserDetails details = new UserDetails(applicationSettings,
                                                personalInformation);

                                        if (!checkIfUserDetailsAlreadyExistInSharedPreferences(details)) {
                                            saveUserDetailsToSharedPreferences(details);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(final @NonNull DatabaseError error) {

                            }
                        });
            }

            startActivity(intent);
            finish();
        }

        private void saveUserDetailsToSharedPreferences(final UserDetails details) {
            SharedPreferences.Editor editor = preferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(details);

            editor.putString("currentUserDetails", json);
            editor.apply();
        }

        private UserDetails retrieveUserDetailsFromSharedPreferences() {
            SharedPreferences preferences =
                    getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
            Gson gson = new Gson();
            String json = preferences.getString("currentUserDetails", "");

            return gson.fromJson(json, UserDetails.class);
        }

        private boolean checkIfUserDetailsAlreadyExistInSharedPreferences(final UserDetails details) {
            UserDetails userDetailsFromSharedPreferences = retrieveUserDetailsFromSharedPreferences();

            return details.equals(userDetailsFromSharedPreferences);
        }
    }
}