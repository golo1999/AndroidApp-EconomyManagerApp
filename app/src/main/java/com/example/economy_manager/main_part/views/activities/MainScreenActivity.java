package com.example.economy_manager.main_part.views.activities;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.MainScreenActivityBinding;
import com.example.economy_manager.main_part.viewmodels.MainScreenViewModel;
import com.example.economy_manager.main_part.views.fragments.MoneySpentPercentageFragment;
import com.example.economy_manager.models.ApplicationSettings;
import com.example.economy_manager.models.PersonalInformation;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
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
    private MainScreenActivityBinding binding;
    private MainScreenViewModel viewModel;
    private SharedPreferences preferences;
    private ViewGroup.LayoutParams moneySpentPercentageLayoutParams;
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
        setVariables();
        setFragments();
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
    public void onEmptyPieChart() {
        if (moneySpentPercentageLayoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            moneySpentPercentageLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        binding.expensesChartContainer.setLayoutParams(moneySpentPercentageLayoutParams);
    }

    @Override
    public void onNotEmptyPieChart() {
        final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        final int percentageScreenHeight = (int) (screenHeight * 0.4);

        if (moneySpentPercentageLayoutParams.height != percentageScreenHeight) {
            moneySpentPercentageLayoutParams.height = percentageScreenHeight;
        }

        binding.expensesChartContainer.setLayoutParams(moneySpentPercentageLayoutParams);
    }

    private UserDetails retrieveUserDetailsFromSharedPreferences() {
        SharedPreferences preferences =
                getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("currentUserDetails", "");

        return gson.fromJson(json, UserDetails.class);
    }

    private void saveUserDetailsToSharedPreferences(final UserDetails details) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(details);

        editor.putString("currentUserDetails", json);
        editor.apply();
    }

    private void setActivityTheme() {
        getWindow().setBackgroundDrawableResource(viewModel.getActivityTheme());
    }

    private void setDates() {
        if (!String.valueOf(binding.greetingText.getText()).trim().equals(viewModel.getGreetingMessage(this))) {
            binding.greetingText.setText(viewModel.getGreetingMessage(this));
        }

        if (!String.valueOf(binding.dateText.getText()).trim().equals(viewModel.getCurrentDateTranslated())) {
            binding.dateText.setText(viewModel.getCurrentDateTranslated());
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
                .replace(R.id.expensesChartContainer, viewModel.getFragmentMoneySpentPercentage())
                .commit();
    }

    private void setMoneySpentPercentage() {
        final String currentUserID = MyCustomVariables.getFirebaseAuth().getUid();

        if (currentUserID != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(currentUserID)
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

                                binding.moneySpentPercentageText.setText(percentageText);
                            } else {
                                binding.moneySpentPercentageText.setText(getResources().getString(R.string.no_money_records_yet));
                            }

                            binding.firebaseLoadingProgressBar.setVisibility(View.GONE);
                            binding.firebaseLoadingProgressBarLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setTextsBetweenFragments() {
        final int color = viewModel.getTextColor(this);

        binding.remainingMonthlyIncomeText.setTextColor(color);
        binding.monthlyBalanceText.setTextColor(color);
        binding.lastWeekExpensesText.setTextColor(color);
        binding.lastTenTransactionsText.setTextColor(color);
        binding.topFiveExpensesText.setTextColor(color);
        binding.expensesChartText.setTextColor(color);
    }

    private void setTimer() {
        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    viewModel.setTimerCounter(viewModel.getTimerCounter() + 1);

                    // stopping the timer after one second
                    if (viewModel.getTimerCounter() == 1) {
                        timer.cancel();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void setUserDetails() {
        final String currentUserID = MyCustomVariables.getFirebaseAuth().getUid();

        if (currentUserID != null) {
            if (MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this) == null) {
                MyCustomVariables.getDatabaseReference()
                        .child(currentUserID)
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
        binding = DataBindingUtil.setContentView(this, R.layout.main_screen_activity);
        viewModel = new ViewModelProvider(this).get(MainScreenViewModel.class);
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        moneySpentPercentageLayoutParams = binding.expensesChartContainer.getLayoutParams();

        binding.setActivity(this);
        binding.setAddExpenseActivity(AddExpenseActivity.class);
        binding.setAddIncomeActivity(AddIncomeActivity.class);
        binding.setEditAccountActivity(EditAccountActivity.class);
        binding.setEditTransactionsActivity(EditTransactionsActivity.class);
        binding.setMonthlyBalanceActivity(MonthlyBalanceActivity.class);
        binding.setSettingsActivity(SettingsActivity.class);
        binding.setViewModel(viewModel);
    }

    private boolean userDetailsAlreadyExistInSharedPreferences(final UserDetails details) {
        UserDetails userDetailsFromSharedPreferences = retrieveUserDetailsFromSharedPreferences();

        return details.equals(userDetailsFromSharedPreferences);
    }
}