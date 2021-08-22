package com.example.economy_manager.main_part.views.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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

import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

public class MainScreenActivity extends AppCompatActivity {
    private MainScreenViewModel viewModel;
    private ConstraintLayout firebaseDatabaseLoadingProgressBarLayout;
    private ProgressBar firebaseDatabaseLoadingProgressBar;
    private TextView greeting;
    private TextView date;
    private FloatingActionButton addButton;
    private FloatingActionButton subtractButton;
    private ImageView signOut;
    private ImageView edit;
    private ImageView balance;
    private TextView moneySpentPercentage;
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
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this,
                    getResources().getString(R.string.press_again_exit), Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    private void setVariables() {
        viewModel = new ViewModelProvider(this).get(MainScreenViewModel.class);
        firebaseDatabaseLoadingProgressBarLayout = findViewById(R.id.main_screen_progress_bar_layout);
        firebaseDatabaseLoadingProgressBar = findViewById(R.id.main_screen_progress_bar);
        greeting = findViewById(R.id.greetingText);
        date = findViewById(R.id.dateText);
        addButton = findViewById(R.id.fabAdd);
        subtractButton = findViewById(R.id.fabSubtract);
        signOut = findViewById(R.id.main_screen_sign_out);
        edit = findViewById(R.id.main_screen_edit);
        balance = findViewById(R.id.main_screen_balance);
        moneySpentPercentage = findViewById(R.id.currentMoneyText);
        account = findViewById(R.id.main_screen_account);
        settings = findViewById(R.id.main_screen_settings);
        remainingMonthlyIncomeText = findViewById(R.id.mainScreenRemainingIncome);
        monthlyBalanceText = findViewById(R.id.mainScreenMonthlyBalance);
        lastWeekExpensesText = findViewById(R.id.mainScreenLastWeekSpent);
        lastTenTransactionsText = findViewById(R.id.mainScreenLastTenTransactions);
        topFiveExpensesText = findViewById(R.id.mainScreenTopFiveExpenses);
        pieChartSpentPercentageText = findViewById(R.id.mainScreenMoneySpentPercentage);
    }

    private void setOnClickListeners() {
        account.setOnClickListener(v -> MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                EditAccountActivity.class, 1));

        addButton.setOnClickListener(v -> MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                AddMoneyActivity.class, 1));

        balance.setOnClickListener(v -> MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                MonthlyBalanceActivity.class, 0));

        edit.setOnClickListener(v -> MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                EditTransactionsActivity.class, 0));

        settings.setOnClickListener(v -> MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                SettingsActivity.class, 1));

        subtractButton.setOnClickListener(v -> MyCustomMethods.goToActivityInDirection(MainScreenActivity.this,
                SubtractMoneyActivity.class, 1));

        signOut.setOnClickListener(v -> {
            LoginManager.getInstance().logOut();
            MyCustomVariables.getFirebaseAuth().signOut();
            finishAffinity();
            startActivity(new Intent(MainScreenActivity.this, LogInActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
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

    private void setUserDetails() {
        final UserDetails userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);

        viewModel.setUserDetails(userDetails);
    }

    private void setActivityTheme() {
        final boolean darkThemeEnabled = viewModel.getUserDetails() != null ?
                viewModel.getUserDetails().getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        getWindow().setBackgroundDrawableResource(!darkThemeEnabled ?
                R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift);
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

    private void setDates() {
        greeting.setText(viewModel.getGreetingMessage(this));
        date.setText(viewModel.getCurrentDateTranslated());
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

                                final int percentage = Float
                                        .valueOf(totalMonthlyExpenses / totalMonthlyIncomes * 100).intValue();
                                final String percentageText = currentMonthTransactionsExist ?
                                        percentage <= 100 ?
                                                getResources().getString(R.string.money_spent_you_spent) + " " + percentage
                                                        + getResources().getString(R.string.money_spent_percentage) :
                                                getResources().getString(R.string.money_spent_you_spent) + " " + 100
                                                        + getResources().getString(R.string.money_spent_percentage) :
                                        getResources().getString(R.string.no_money_records_month);

                                moneySpentPercentage.setText(percentageText);
                            } else {
                                moneySpentPercentage.setText(getResources().getString(R.string.no_money_records_yet));
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
}