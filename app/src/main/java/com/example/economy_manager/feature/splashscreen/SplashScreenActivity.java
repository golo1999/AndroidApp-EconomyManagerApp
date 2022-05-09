package com.example.economy_manager.feature.splashscreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.SplashScreenActivityBinding;
import com.example.economy_manager.feature.login.LoginActivity;
import com.example.economy_manager.feature.mainscreen.MainScreenActivity;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

    public class LogoLauncher extends Thread {
        public void run() {
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
                                    if (!snapshot.exists()) {
                                        return;
                                    }

                                    final UserDetails details = snapshot.getValue(UserDetails.class);

                                    if (details == null) {
                                        return;
                                    }

                                    MyCustomMethods
                                            .saveObjectToSharedPreferences(SplashScreenActivity.this,
                                                    details, "currentUserDetails");


                                    final UserDetails retrievedUserDetails = MyCustomMethods.
                                            retrieveObjectFromSharedPreferences(SplashScreenActivity.this,
                                                    "currentUserDetails", UserDetails.class);

                                    if (retrievedUserDetails == null) {
                                        return;
                                    }

                                    MyCustomVariables.setUserDetails(retrievedUserDetails);
                                }

                                @Override
                                public void onCancelled(final @NonNull DatabaseError error) {

                                }
                            });
                }

                MyCustomMethods.goToActivityWithoutTransition(SplashScreenActivity.this,
                        MyCustomVariables.getFirebaseAuth().getCurrentUser() != null ?
                                MainScreenActivity.class : LoginActivity.class);
            }
        }
    }
}