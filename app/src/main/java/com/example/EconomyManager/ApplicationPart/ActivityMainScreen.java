package com.example.EconomyManager.ApplicationPart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.EconomyManager.LoginPart.LogIn;
import com.example.EconomyManager.MyCustomSharedPreferences;
import com.example.EconomyManager.MyCustomVariables;
import com.example.EconomyManager.R;
import com.example.EconomyManager.Transaction;
import com.example.EconomyManager.UserDetails;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityMainScreen extends AppCompatActivity {
    private MainScreenViewModel viewModel;
    private TextView greeting;
    private TextView date;
    private TextView moneySpentPercentage;
    private TextView remainingMonthlyIncomeText;
    private TextView monthlyBalanceText;
    private TextView lastWeekExpensesText;
    private TextView lastTenTransactionsText;
    private TextView topFiveExpensesText;
    private FloatingActionButton addButton;
    private FloatingActionButton subtractButton;
    private ImageView signOut;
    private ImageView edit;
    private ImageView balance;
    private ImageView account;
    private ImageView settings;
    private ConstraintLayout firebaseDatabaseLoadingProgressBarLayout;
    private ProgressBar firebaseDatabaseLoadingProgressBar;
    private int timerCounter = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTimer();
        setContentView(R.layout.activity_main_screen);
        setFragments();
        setVariables();
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

        Log.d("userDetailsInMainScreen", viewModel.getUserDetails().toString());
    }

    private void setVariables() {
        viewModel = new ViewModelProvider(this).get(MainScreenViewModel.class);
        firebaseDatabaseLoadingProgressBarLayout = findViewById(R.id.mainScreenProgressBarLayout);
        firebaseDatabaseLoadingProgressBar = findViewById(R.id.mainScreenProgressBar);
        greeting = findViewById(R.id.greetingText);
        date = findViewById(R.id.dateText);
        addButton = findViewById(R.id.fabAdd);
        subtractButton = findViewById(R.id.fabSubtract);
        signOut = findViewById(R.id.mainSignout);
        edit = findViewById(R.id.mainEdit);
        balance = findViewById(R.id.mainBalance);
        moneySpentPercentage = findViewById(R.id.currentMoneyText);
        account = findViewById(R.id.mainAccount);
        settings = findViewById(R.id.mainSettings);
        remainingMonthlyIncomeText = findViewById(R.id.mainScreenRemainingIncome);
        monthlyBalanceText = findViewById(R.id.mainScreenMonthlyBalance);
        lastWeekExpensesText = findViewById(R.id.mainScreenLastWeekSpent);
        lastTenTransactionsText = findViewById(R.id.mainScreenLastTenTransactions);
        topFiveExpensesText = findViewById(R.id.mainScreenTopFiveExpenses);
    }

    private void setOnClickListeners() {
        account.setOnClickListener(v -> {
            final Intent intent = new Intent(ActivityMainScreen.this, ActivityEditAccount.class);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        addButton.setOnClickListener(v -> {
            final Intent intent = new Intent(ActivityMainScreen.this, ActivityAddMoney.class);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        balance.setOnClickListener(v -> {
            final Intent intent = new Intent(ActivityMainScreen.this, ActivityMonthlyBalance.class);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        edit.setOnClickListener(v -> {
            final Intent intent = new Intent(ActivityMainScreen.this, ActivityEditTransactions.class);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        settings.setOnClickListener(v -> {
            final Intent intent = new Intent(ActivityMainScreen.this, ActivitySettings.class);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        subtractButton.setOnClickListener(v -> {
            final Intent intent = new Intent(ActivityMainScreen.this, ActivitySubtractMoney.class);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        signOut.setOnClickListener(v -> {
            final Intent intent = new Intent(ActivityMainScreen.this, LogIn.class);

            LoginManager.getInstance().logOut();
            MyCustomVariables.getFirebaseAuth().signOut();
            finishAffinity();
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void setFragments() {
        final FragmentBudgetReview budgetReview = FragmentBudgetReview.newInstance();
        final FragmentMoneySpent moneySpent = FragmentMoneySpent.newInstance();
        final FragmentLastTenTransactions lastTenTransactions = FragmentLastTenTransactions.newInstance();
        final FragmentShowSavings savings = FragmentShowSavings.newInstance();
        final FragmentTopFiveExpenses expenses = FragmentTopFiveExpenses.newInstance();
        final FragmentMoneySpentPercentage moneySpentPercentage =
                FragmentMoneySpentPercentage.newInstance();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.scroll_container0, savings)
                .replace(R.id.scroll_container1, budgetReview)
                .replace(R.id.scroll_container2, moneySpent)
                .replace(R.id.scroll_container3, lastTenTransactions)
                .replace(R.id.scroll_container4, expenses)
                .replace(R.id.scroll_container5, moneySpentPercentage)
                .commit();
    }

    private void setUserDetails() {
        final UserDetails userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);

        viewModel.setUserDetails(userDetails);
    }

    private void setActivityTheme() {
        if (viewModel.getUserDetails() != null) {
            final boolean darkThemeEnabled = viewModel.getUserDetails().getApplicationSettings().getDarkTheme();

            getWindow().setBackgroundDrawableResource(!darkThemeEnabled ?
                    R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift);
        }
    }

    private void setTimer() {
        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    ++timerCounter;

                    // oprim timerul dupa o secunda
                    if (timerCounter == 1) {
                        timer.cancel();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void setDates() {
        final Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        final String greetingMessage = currentHour < 12 ?
                getResources().getString(R.string.greet_good_morning) : currentHour < 18 ?
                getResources().getString(R.string.greet_good_afternoon) : getResources().getString(R.string.greet_good_evening);
        String currentDate;
        String datePrefix;
        SimpleDateFormat monthFormat;

//        Toast.makeText(ActivityMainScreen.this,
//                Locale.getDefault().getDisplayLanguage(),
//                Toast.LENGTH_SHORT).show(); // -> afiseaza limba curenta a dispozitivului

        switch (Locale.getDefault().getDisplayLanguage()) {
            case "Deutsch":
                datePrefix = "der";
                monthFormat = new SimpleDateFormat("LLLL", Locale.GERMAN);
                currentDate = datePrefix + " " + currentTime.get(Calendar.DAY_OF_MONTH) + " " +
                        monthFormat.format(currentTime.getTime()) + " " +
                        currentTime.get(Calendar.YEAR);
                break;
            case "español":
                final String separator = "de";

                monthFormat = new SimpleDateFormat("LLLL", Locale.forLanguageTag("es-ES"));
                currentDate = currentTime.get(Calendar.DAY_OF_MONTH) + " " + separator + " " +
                        monthFormat.format(currentTime.getTime()) + " " + separator + " " +
                        currentTime.get(Calendar.YEAR);
                break;
            case "français":
                monthFormat = new SimpleDateFormat("LLLL", Locale.FRENCH);
                currentDate = currentTime.get(Calendar.DAY_OF_MONTH) + " " +
                        monthFormat.format(currentTime.getTime()) + " " +
                        currentTime.get(Calendar.YEAR);
                break;
            case "italiano":
                datePrefix = "il";
                monthFormat = new SimpleDateFormat("LLLL", Locale.ITALIAN);
                currentDate = datePrefix + " " + currentTime.get(Calendar.DAY_OF_MONTH) + " " +
                        monthFormat.format(currentTime.getTime()) + " " +
                        currentTime.get(Calendar.YEAR);
                break;
            case "português":
                datePrefix = "de";
                monthFormat = new SimpleDateFormat("LLLL", Locale.forLanguageTag("pt-PT"));
                currentDate = currentTime.get(Calendar.DAY_OF_MONTH) + " " + datePrefix + " " +
                        monthFormat.format(currentTime.getTime()) + " " + datePrefix + " " +
                        currentTime.get(Calendar.YEAR);
                break;
            case "română":
                monthFormat = new SimpleDateFormat("LLLL", Locale.forLanguageTag("ro-RO"));
                currentDate = currentTime.get(Calendar.DAY_OF_MONTH) + " " +
                        monthFormat.format(currentTime.getTime()) + " " +
                        currentTime.get(Calendar.YEAR);
                break;
            default:
                final String daySuffix = currentTime.get(Calendar.DAY_OF_MONTH) % 10 == 1 ?
                        "st" : currentTime.get(Calendar.DAY_OF_MONTH) % 10 == 2 ?
                        "nd" : currentTime.get(Calendar.DAY_OF_MONTH) % 10 == 3 ?
                        "rd" : "th";
                monthFormat = new SimpleDateFormat("LLLL", Locale.ENGLISH);
                currentDate = monthFormat.format(currentTime.getTime()) + " " +
                        currentTime.get(Calendar.DAY_OF_MONTH) + daySuffix + ", " +
                        currentTime.get(Calendar.YEAR);
                break;
        }

        date.setText(currentDate);
        greeting.setText(greetingMessage.trim());
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
        if (viewModel.getUserDetails() != null) {
            final boolean darkThemeEnabled = viewModel.getUserDetails().getApplicationSettings().getDarkTheme();
            final int color = !darkThemeEnabled ? Color.parseColor("#195190") : Color.WHITE;

            remainingMonthlyIncomeText.setTextColor(color);
            monthlyBalanceText.setTextColor(color);
            lastWeekExpensesText.setTextColor(color);
            lastTenTransactionsText.setTextColor(color);
            topFiveExpensesText.setTextColor(color);
        }
    }
}