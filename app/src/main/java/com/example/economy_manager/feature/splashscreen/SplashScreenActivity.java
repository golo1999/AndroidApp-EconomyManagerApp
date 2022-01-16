package com.example.economy_manager.feature.splashscreen;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.SplashScreenActivityBinding;
import com.example.economy_manager.feature.login.LogInActivity;
import com.example.economy_manager.feature.mainscreen.MainScreenActivity;
import com.example.economy_manager.model.ApplicationSettings;
import com.example.economy_manager.model.PersonalInformation;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class SplashScreenActivity extends AppCompatActivity {
    private SplashScreenActivityBinding binding;
    private SplashScreenViewModel viewModel;
    private final LogoLauncher launcher = new LogoLauncher();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVariables();
        viewModel.startSplashScreenHandler(launcher);
    }

    private void setVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.splash_screen_activity);
        viewModel = new ViewModelProvider(this).get(SplashScreenViewModel.class);
    }

    private void startSplashScreen() {
        launcher.start();
    }

    public class LogoLauncher extends Thread {
        private SharedPreferences preferences;

        public void run() {
            preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);

            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (MyCustomVariables.getFirebaseAuth().getCurrentUser() != null &&
                        MyCustomVariables.getFirebaseAuth().getUid() != null) {
                    MyCustomVariables.getDatabaseReference()
                            .child(MyCustomVariables.getFirebaseAuth().getUid())
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
                                            final UserDetails details =
                                                    new UserDetails(applicationSettings, personalInformation);

                                            if (!userDetailsAlreadyExistInSharedPreferences(details)) {
                                                saveUserDetailsToSharedPreferences(details);
                                            }

                                            if (retrieveUserDetailsFromSharedPreferences() != null) {
                                                final UserDetails userDetails =
                                                        retrieveUserDetailsFromSharedPreferences();

                                                MyCustomVariables.setUserDetails(userDetails);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(final @NonNull DatabaseError error) {

                                }
                            });
                }

                MyCustomMethods.goToActivityWithoutTransition(SplashScreenActivity.this,
                        MyCustomVariables.getFirebaseAuth().getCurrentUser() != null ?
                                MainScreenActivity.class : LogInActivity.class);
            }
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

        private boolean userDetailsAlreadyExistInSharedPreferences(final UserDetails details) {
            UserDetails userDetailsFromSharedPreferences = retrieveUserDetailsFromSharedPreferences();

            return details.equals(userDetailsFromSharedPreferences);
        }
    }
}