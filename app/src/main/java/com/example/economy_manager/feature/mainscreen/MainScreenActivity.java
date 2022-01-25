package com.example.economy_manager.feature.mainscreen;

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
import com.example.economy_manager.feature.addexpense.AddExpenseActivity;
import com.example.economy_manager.feature.addincome.AddIncomeActivity;
import com.example.economy_manager.feature.editaccount.EditAccountActivity;
import com.example.economy_manager.feature.edittransactions.EditTransactionsActivity;
import com.example.economy_manager.feature.moneyspentpercentage.MoneySpentPercentageFragment;
import com.example.economy_manager.feature.monthlybalance.MonthlyBalanceActivity;
import com.example.economy_manager.feature.settings.SettingsActivity;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

public class MainScreenActivity
        extends AppCompatActivity
        implements MoneySpentPercentageFragment.MoneySpentPercentageListener {
    private MainScreenActivityBinding binding;
    private MainScreenViewModel viewModel;
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

        binding.expensesChartFragmentContainer.setLayoutParams(moneySpentPercentageLayoutParams);
    }

    @Override
    public void onNotEmptyPieChart() {
        final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        final int percentageScreenHeight = (int) (screenHeight * 0.4);

        if (moneySpentPercentageLayoutParams.height != percentageScreenHeight) {
            moneySpentPercentageLayoutParams.height = percentageScreenHeight;
        }

        binding.expensesChartFragmentContainer.setLayoutParams(moneySpentPercentageLayoutParams);
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
                .replace(R.id.showSavingsFragmentContainer, viewModel.getFragmentShowSavings())
                .replace(R.id.budgetReviewFragmentContainer, viewModel.getFragmentBudgetReview())
                .replace(R.id.moneySpentFragmentContainer, viewModel.getFragmentMoneySpent())
                .replace(R.id.lastTenTransactionsFragmentContainer, viewModel.getFragmentLastTenTransactions())
                .replace(R.id.topFiveExpensesFragmentContainer, viewModel.getFragmentTopFiveExpenses())
                .replace(R.id.favoriteExpensesCategoryFragmentContainer, viewModel.getFavoriteExpensesCategoryFragment())
                .replace(R.id.expensesChartFragmentContainer, viewModel.getFragmentMoneySpentPercentage())
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

                            if (snapshot.exists() && snapshot.hasChild("personalTransactions") &&
                                    snapshot.child("personalTransactions").hasChildren()) {
                                for (final DataSnapshot databaseTransaction :
                                        snapshot.child("personalTransactions").getChildren()) {
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
        binding.favoriteExpensesCategoryText.setTextColor(color);
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
                                if (snapshot.exists()) {
                                    final UserDetails details = snapshot.getValue(UserDetails.class);

                                    if (details != null &&
                                            details.getApplicationSettings() != null &&
                                            details.getPersonalInformation() != null) {
                                        if (MyCustomMethods.
                                                objectExistsInSharedPreferences(MainScreenActivity.this,
                                                        "currentUserDetails", UserDetails.class, details)) {
                                            MyCustomMethods.
                                                    saveObjectToSharedPreferences(MainScreenActivity.this,
                                                            details, "currentUserDetails");
                                        }

                                        if (MyCustomMethods.
                                                retrieveObjectFromSharedPreferences(MainScreenActivity.this,
                                                        "currentUserDetails", UserDetails.class) != null) {
                                            final UserDetails userDetails = MyCustomMethods.
                                                    retrieveObjectFromSharedPreferences(MainScreenActivity.this,
                                                            "currentUserDetails", UserDetails.class);

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

    /**
     * Method for setting activity & layout variables
     */
    private void setVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.main_screen_activity);
        viewModel = new ViewModelProvider(this).get(MainScreenViewModel.class);
        moneySpentPercentageLayoutParams = binding.expensesChartFragmentContainer.getLayoutParams();

        binding.setActivity(this);
        binding.setAddExpenseActivity(AddExpenseActivity.class);
        binding.setAddIncomeActivity(AddIncomeActivity.class);
        binding.setEditAccountActivity(EditAccountActivity.class);
        binding.setEditTransactionsActivity(EditTransactionsActivity.class);
        binding.setMonthlyBalanceActivity(MonthlyBalanceActivity.class);
        binding.setSettingsActivity(SettingsActivity.class);
        binding.setViewModel(viewModel);
    }
}