package com.example.EconomyManager.ApplicationPart;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.EconomyManager.LoginPart.LogIn;
import com.example.EconomyManager.R;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
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
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private ConstraintLayout firebaseDatabaseLoadingProgressBarLayout;
    private ProgressBar firebaseDatabaseLoadingProgressBar;
    private int timerCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTimer();
        setActivityTheme();
        setContentView(R.layout.activity_main_screen);
        setFragments();
        setVariables();
        setOnClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTextsBetweenFragments();
        setDates();
        setMoneySpentPercentage();
    }

    private void setFragments() {
        FragmentBudgetReview budgetReview = FragmentBudgetReview.newInstance();
        FragmentMoneySpent moneySpent = FragmentMoneySpent.newInstance();
        FragmentLastTenTransactions lastTenTransactions = FragmentLastTenTransactions.newInstance();
        FragmentShowSavings savings = FragmentShowSavings.newInstance();
        FragmentTopFiveExpenses expenses = FragmentTopFiveExpenses.newInstance();
        FragmentMoneySpentPercentage moneySpentPercentage =
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

    private void setActivityTheme() {
        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                        if (snapshot.hasChild("darkTheme")) {
                            boolean checked = Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()));

                            getWindow().setBackgroundDrawableResource(!checked ?
                                    R.drawable.ic_white_gradient_tobacco_ad :
                                    R.drawable.ic_black_gradient_night_shift);
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void setOnClickListeners() {
        account.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMainScreen.this, ActivityEditAccount.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMainScreen.this, ActivityAddMoney.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        balance.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMainScreen.this, ActivityMonthlyBalance.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMainScreen.this, ActivityEditTransactions.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        settings.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMainScreen.this, ActivitySettings.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        subtractButton.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMainScreen.this, ActivitySubtractMoney.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        signOut.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMainScreen.this, LogIn.class);
            LoginManager.getInstance().logOut();
            fbAuth.signOut();
            finishAffinity();
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
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
        String greetingMessage;
        String currentDate;
        String datePrefix;
        SimpleDateFormat monthFormat;

        if (currentHour < 12)
            greetingMessage = getResources().getString(R.string.greet_good_morning);
        else if (currentHour < 18)
            greetingMessage = getResources().getString(R.string.greet_good_afternoon);
        else greetingMessage = getResources().getString(R.string.greet_good_evening);

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
                monthFormat = new SimpleDateFormat("LLLL", Locale.forLanguageTag("es-ES"));
                String separator = "de";
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
                String daySuffix = currentTime.get(Calendar.DAY_OF_MONTH) % 10 == 1 ?
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
        final Calendar currentTime = Calendar.getInstance();
        final SimpleDateFormat currentMonth = new SimpleDateFormat("LLLL", Locale.ENGLISH);

        if (fbAuth.getUid() != null) {
            myRef.child(fbAuth.getUid()).child("PersonalTransactions").addValueEventListener(new ValueEventListener() {
                String incomesDB, expensesDB, text;
                Float percentage;

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR)))
                                .child(currentMonth.format(currentTime.getTime()))
                                .hasChild("Incomes")) {
                            if (snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR)))
                                    .child(currentMonth.format(currentTime.getTime()))
                                    .child("Incomes").hasChild("Overall")) {
                                incomesDB = String.valueOf(snapshot
                                        .child(String.valueOf(currentTime.get(Calendar.YEAR)))
                                        .child(currentMonth.format(currentTime.getTime()))
                                        .child("Incomes")
                                        .child("Overall")
                                        .getValue());
                            }
                            if (!snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR)))
                                    .child(currentMonth.format(currentTime.getTime()))
                                    .hasChild("Expenses")) {
                                expensesDB = "0";
                            }
                        } else incomesDB = "0";
                        if (snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR)))
                                .child(currentMonth.format(currentTime.getTime()))
                                .hasChild("Expenses")) {
                            if (snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR)))
                                    .child(currentMonth.format(currentTime.getTime()))
                                    .child("Expenses")
                                    .hasChild("Overall")) {
                                expensesDB = String.valueOf(snapshot
                                        .child(String.valueOf(currentTime.get(Calendar.YEAR)))
                                        .child(currentMonth.format(currentTime.getTime()))
                                        .child("Expenses")
                                        .child("Overall")
                                        .getValue());
                            }
                            if (!snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR)))
                                    .child(currentMonth.format(currentTime.getTime()))
                                    .hasChild("Incomes")) {
                                incomesDB = "0";
                            }
                        } else {
                            expensesDB = "0";
                        }
                        try {
                            // aici este tratat si cazul in care trecem de la o luna la alta:
                            // va aparea ca nu sunt bani
                            if ((incomesDB == null || expensesDB == null) ||
                                    (Float.parseFloat(incomesDB) == 0 &&
                                            Float.parseFloat(expensesDB) == 0)) {
                                text = getResources().getString(R.string.no_money_records_month);
                            } else {
                                if (Float.parseFloat(incomesDB) == 0 && Float.parseFloat(expensesDB) != 0)
                                    percentage = 100f;
                                else if ((Float.parseFloat(incomesDB) != 0 &&
                                        Float.parseFloat(expensesDB) != 0)
                                        || Float.parseFloat(expensesDB) == 0)
                                    percentage = Float.parseFloat(expensesDB) /
                                            Float.parseFloat(incomesDB) * 100;
                                if (percentage > 100f)
                                    percentage = 100f;
                                text = getResources().getString(R.string.money_spent_you_spent) + " " + percentage.intValue() + getResources().getString(R.string.money_spent_percentage);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else text = getResources().getString(R.string.no_money_records_yet);
                    moneySpentPercentage.setText(text.trim());
                    firebaseDatabaseLoadingProgressBar.setVisibility(View.GONE);
                    firebaseDatabaseLoadingProgressBarLayout.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void setTextsBetweenFragments() {
        if (fbAuth.getUid() != null) {
            myRef.child(fbAuth.getUid()).addValueEventListener(new ValueEventListener() {
                boolean darkThemeEnabled;
                int color;

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                        if (snapshot.hasChild("ApplicationSettings"))
                            if (snapshot.child("ApplicationSettings").hasChild("darkTheme")) {
                                darkThemeEnabled = Boolean.parseBoolean(String.valueOf(snapshot
                                        .child("ApplicationSettings")
                                        .child("darkTheme").getValue()));
                                color = !darkThemeEnabled ?
                                        Color.parseColor("#195190") : Color.WHITE;

                                remainingMonthlyIncomeText.setTextColor(color);
                                monthlyBalanceText.setTextColor(color);
                                lastWeekExpensesText.setTextColor(color);
                                lastTenTransactionsText.setTextColor(color);
                                topFiveExpensesText.setTextColor(color);
                            }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}