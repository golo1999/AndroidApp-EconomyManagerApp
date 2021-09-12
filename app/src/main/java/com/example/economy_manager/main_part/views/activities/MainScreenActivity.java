package com.example.economy_manager.main_part.views.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.login_part.LogInActivity;
import com.example.economy_manager.main_part.viewmodels.MainScreenViewModel;
import com.example.economy_manager.main_part.views.fragments.MoneySpentPercentageFragment;
import com.example.economy_manager.models.ApplicationSettings;
import com.example.economy_manager.models.PersonalInformation;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

public class MainScreenActivity
        extends AppCompatActivity
        implements MoneySpentPercentageFragment.MoneySpentPercentageListener {
    private MainScreenViewModel viewModel;
    private SharedPreferences preferences;
    private ConstraintLayout firebaseDatabaseLoadingProgressBarLayout;
    private FrameLayout moneySpentPercentageLayout;
    private ViewGroup.LayoutParams moneySpentPercentageLayoutParams;
    private ProgressBar firebaseDatabaseLoadingProgressBar;
    private TextView greeting;
    private TextView date;
    private FloatingActionButton addButton;
    private FloatingActionButton subtractButton;
    private ImageView signOut;
    private ImageView edit;
    private ImageView balance;
    private TextView moneySpentPercentageText;
    private ImageView account;
    private ImageView settings;
    private TextView remainingMonthlyIncomeText;
    private TextView monthlyBalanceText;
    private TextView lastWeekExpensesText;
    private TextView lastTenTransactionsText;
    private TextView topFiveExpensesText;
    private TextView pieChartSpentPercentageText;
    private int timerCounter = 0;
    private long backPressedTime;
    private Toast backToast;

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this,
                    getResources().getString(R.string.press_again_exit),
                    Toast.LENGTH_SHORT);

            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTimer();
        setContentView(R.layout.main_screen_activity);
        setVariables();
        setFragments();
        setOnClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserDetails();
        setActivityTheme();
        setTextsBetweenFragments();
        setDates();
        setMoneySpentPercentage();

        if (MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this) != null) {
            Log.d("userDetailsMainScreen", MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this).toString());
        }
    }

    @Override
    public void onEmptyPieChart() {
        if (moneySpentPercentageLayoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            moneySpentPercentageLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        moneySpentPercentageLayout.setLayoutParams(moneySpentPercentageLayoutParams);
    }

    @Override
    public void onNotEmptyPieChart() {
        final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        final int percentageScreenHeight = (int) (screenHeight * 0.4);

        if (moneySpentPercentageLayoutParams.height != percentageScreenHeight) {
            moneySpentPercentageLayoutParams.height = percentageScreenHeight;
        }

        moneySpentPercentageLayout.setLayoutParams(moneySpentPercentageLayoutParams);
    }

    private void setActivityTheme() {
        final boolean darkThemeEnabled = viewModel.getUserDetails() != null ?
                viewModel.getUserDetails().getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        getWindow().setBackgroundDrawableResource(!darkThemeEnabled ?
                R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift);
    }

    private void setDates() {
        if (!String.valueOf(greeting.getText()).trim().equals(viewModel.getGreetingMessage(this))) {
            greeting.setText(viewModel.getGreetingMessage(this));
        }

        if (!String.valueOf(date.getText()).trim().equals(viewModel.getCurrentDateTranslated())) {
            date.setText(viewModel.getCurrentDateTranslated());
        }
    }

    private void setFragments() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_show_savings_container, viewModel.getFragmentShowSavings())
                .replace(R.id.fragment_budget_review_container, viewModel.getFragmentBudgetReview())
                .replace(R.id.fragment_money_spent_container, viewModel.getFragmentMoneySpent())
                .replace(R.id.fragment_last_ten_transactions_container, viewModel.getFragmentLastTenTransactions())
                .replace(R.id.fragment_top_five_expenses_container, viewModel.getFragmentTopFiveExpenses())
                .replace(R.id.fragment_money_spent_percentage_container, viewModel.getFragmentMoneySpentPercentage())
                .commit();
    }

    private void setMoneySpentPercentage() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
                            float totalMonthlyIncomes = 0f;
                            float totalMonthlyExpenses = 0f;
                            boolean currentMonthTransactionsExist = false;

                            if (snapshot.exists() && snapshot.hasChild("PersonalTransactions") &&
                                    snapshot.child("PersonalTransactions").hasChildren()) {
                                for (final DataSnapshot databaseTransaction :
                                        snapshot.child("PersonalTransactions").getChildren()) {
                                    final Transaction transaction = databaseTransaction.getValue(Transaction.class);

                                    if (transaction != null &&
                                            transaction.getTime().getYear() == LocalDate.now().getYear() &&
                                            transaction.getTime().getMonth() == LocalDate.now().getMonthValue()) {
                                        if (!currentMonthTransactionsExist) {
                                            currentMonthTransactionsExist = true;
                                        }

                                        if (transaction.getCategory() > 0 && transaction.getCategory() < 4) {
                                            totalMonthlyIncomes += Float.parseFloat(transaction.getValue());
                                        } else {
                                            totalMonthlyExpenses += Float.parseFloat(transaction.getValue());
                                        }
                                    }
                                }

                                final int percentage =
                                        Float.valueOf(totalMonthlyExpenses / totalMonthlyIncomes * 100).intValue();

                                final String percentageText = currentMonthTransactionsExist ?
                                        percentage <= 100 ?
                                                getResources().getString(R.string.money_spent_you_spent) + " " + percentage
                                                        + getResources().getString(R.string.money_spent_percentage) :
                                                getResources().getString(R.string.money_spent_you_spent) + " " + 100
                                                        + getResources().getString(R.string.money_spent_percentage) :
                                        getResources().getString(R.string.no_money_records_month);

                                moneySpentPercentageText.setText(percentageText);
                            } else {
                                moneySpentPercentageText.setText(getResources().getString(R.string.no_money_records_yet));
                            }

                            firebaseDatabaseLoadingProgressBar.setVisibility(View.GONE);
                            firebaseDatabaseLoadingProgressBarLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setOnClickListeners() {
        account.setOnClickListener((final View v) ->
                MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                        EditAccountActivity.class, 1));

        addButton.setOnClickListener((final View v) ->
                MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                        AddMoneyActivity.class, 1));

        balance.setOnClickListener((final View v) ->
                MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                        MonthlyBalanceActivity.class, 0));

        edit.setOnClickListener((final View v) ->
                MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                        EditTransactionsActivity.class, 0));

        settings.setOnClickListener((final View v) ->
                MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                        SettingsActivity.class, 1));

        subtractButton.setOnClickListener((final View v) ->
                MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                        SubtractMoneyActivity.class, 1));

        signOut.setOnClickListener((final View v) -> {
            LoginManager.getInstance().logOut();
            MyCustomVariables.getFirebaseAuth().signOut();
            finishAffinity();
            startActivity(new Intent(MainScreenActivity.this, LogInActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void setTextsBetweenFragments() {
        final boolean darkThemeEnabled = viewModel.getUserDetails() != null ?
                viewModel.getUserDetails().getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        final int color = !darkThemeEnabled ? getColor(R.color.turkish_sea) : Color.WHITE;

        remainingMonthlyIncomeText.setTextColor(color);
        monthlyBalanceText.setTextColor(color);
        lastWeekExpensesText.setTextColor(color);
        lastTenTransactionsText.setTextColor(color);
        topFiveExpensesText.setTextColor(color);
        pieChartSpentPercentageText.setTextColor(color);
    }

    private void setTimer() {
        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    ++timerCounter;

                    // stopping the timer after one second
                    if (timerCounter == 1) {
                        timer.cancel();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void setUserDetails() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            if (MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this) == null) {
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
                                            viewModel.setUserDetails(userDetails);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(final @NonNull DatabaseError error) {

                            }
                        });
            } else {
                final UserDetails userDetails =
                        MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);

                viewModel.setUserDetails(userDetails);
            }
        }
    }

    private void setVariables() {
        viewModel = new ViewModelProvider(this).get(MainScreenViewModel.class);
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        firebaseDatabaseLoadingProgressBarLayout = findViewById(R.id.main_screen_progress_bar_layout);
        moneySpentPercentageLayout = findViewById(R.id.fragment_money_spent_percentage_container);
        moneySpentPercentageLayoutParams = moneySpentPercentageLayout.getLayoutParams();
        firebaseDatabaseLoadingProgressBar = findViewById(R.id.main_screen_progress_bar);
        greeting = findViewById(R.id.main_screen_greeting_text);
        date = findViewById(R.id.main_screen_date_text);
        addButton = findViewById(R.id.main_screen_fab_add);
        subtractButton = findViewById(R.id.main_screen_fab_subtract);
        signOut = findViewById(R.id.main_screen_sign_out);
        edit = findViewById(R.id.main_screen_edit);
        balance = findViewById(R.id.main_screen_balance);
        moneySpentPercentageText = findViewById(R.id.main_screen_current_money_text);
        account = findViewById(R.id.main_screen_account);
        settings = findViewById(R.id.main_screen_settings);
        remainingMonthlyIncomeText = findViewById(R.id.main_screen_remaining_income_text);
        monthlyBalanceText = findViewById(R.id.main_screen_monthly_balance_text);
        lastWeekExpensesText = findViewById(R.id.main_screen_last_week_spent_text);
        lastTenTransactionsText = findViewById(R.id.main_screen_last_ten_transactions_text);
        topFiveExpensesText = findViewById(R.id.main_screen_top_five_expenses_text);
        pieChartSpentPercentageText = findViewById(R.id.main_screen_money_spent_percentage_text);
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