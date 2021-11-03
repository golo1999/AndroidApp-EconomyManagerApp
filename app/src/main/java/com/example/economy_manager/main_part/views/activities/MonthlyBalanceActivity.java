package com.example.economy_manager.main_part.views.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.MonthlyBalanceActivityBinding;
import com.example.economy_manager.main_part.viewmodels.MonthlyBalanceViewModel;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.example.economy_manager.utilities.Types;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class MonthlyBalanceActivity extends AppCompatActivity {
    private MonthlyBalanceActivityBinding binding;
    private MonthlyBalanceViewModel viewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVariables();
        setUserDetails();
        setActivityTheme();
        setCenterText();
        setIncomesAndExpensesInParent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyCustomMethods.finishActivityWithFadeTransition(this);
    }

    private void setVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.monthly_balance_activity);
        viewModel = new ViewModelProvider(this).get(MonthlyBalanceViewModel.class);

        binding.setActivity(this);
        binding.setActivityTitle(viewModel.getActivityTitle(this));
        binding.setContext(this);
        binding.setViewModel(viewModel);
    }

    private void setUserDetails() {
        final UserDetails userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);

        viewModel.setUserDetails(userDetails);
    }

    private void setActivityTheme() {
        getWindow().setBackgroundDrawableResource(viewModel.getActivityTheme());
    }

    private void setIncomesAndExpensesInParent() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
                            final String currencySymbol = viewModel.getUserDetails() != null ?
                                    viewModel.getUserDetails().getApplicationSettings().getCurrencySymbol() :
                                    MyCustomMethods.getCurrencySymbol();
                            final ArrayList<Transaction> transactionsList = new ArrayList<>();

                            final ArrayList<Integer> daysList = new ArrayList<>();

                            if (snapshot.exists() && snapshot.hasChild("PersonalTransactions") &&
                                    snapshot.child("PersonalTransactions").hasChildren()) {
                                for (final DataSnapshot databaseTransaction :
                                        snapshot.child("PersonalTransactions").getChildren()) {
                                    final Transaction transaction = databaseTransaction.getValue(Transaction.class);

                                    if (transaction != null && transaction.getTime() != null &&
                                            transaction.getTime().getYear() == LocalDate.now().getYear() &&
                                            transaction.getTime().getMonth() == LocalDate.now().getMonthValue()) {
                                        transactionsList.add(transaction);

                                        if (viewModel.checkIfDayCanBeAddedToList(daysList, transaction.getTime().getDay())) {
                                            daysList.add(transaction.getTime().getDay());
                                        }
                                    }
                                }

                                // sorting the list by value descending
                                transactionsList.sort((final Transaction transaction1, final Transaction transaction2) ->
                                        Float.compare(Float.parseFloat(String.valueOf(transaction2.getValue())),
                                                Float.parseFloat(String.valueOf(transaction1.getValue()))));
                                daysList.sort(Collections.reverseOrder());

                                // setting the balance for each day from the list
                                for (final int dayFromDaysList : daysList) {
                                    // calculating the total incomes and expenses for each day
                                    float dateTotalIncome = 0f;

                                    float dateTotalExpense = 0f;

                                    final String dateTranslated = viewModel
                                            .getDateTranslated(MonthlyBalanceActivity.this, dayFromDaysList);

                                    final ConstraintLayout dayAndSumLayout =
                                            (ConstraintLayout) View.inflate(MonthlyBalanceActivity.this,
                                                    R.layout.monthly_balance_title_layout, null);

                                    final TextView dayText = dayAndSumLayout
                                            .findViewById(R.id.monthly_balance_relative_layout_day);

                                    final TextView totalSumText = dayAndSumLayout
                                            .findViewById(R.id.monthly_balance_relative_layout_day_total_sum);

                                    binding.mainLayout.addView(dayAndSumLayout);
                                    dayText.setText(dateTranslated);
                                    dayText.setTextSize(25);

                                    dayText.setTextColor(viewModel.getUserDetails() == null ||
                                            !viewModel.getUserDetails().getApplicationSettings().getDarkTheme()
                                            ? Color.BLUE : Color.YELLOW);

                                    for (final Transaction transactionsListIterator : transactionsList) {
                                        if (dayFromDaysList == transactionsListIterator.getTime().getDay()) {
                                            if (transactionsListIterator.getType() == 1) {
                                                dateTotalIncome += Float
                                                        .parseFloat(transactionsListIterator.getValue());
                                            } else {
                                                dateTotalExpense += Float
                                                        .parseFloat(transactionsListIterator.getValue());
                                            }

                                            final LinearLayout transactionLayout = (LinearLayout) View
                                                    .inflate(MonthlyBalanceActivity.this,
                                                            R.layout.monthly_balance_linearlayout,
                                                            null);
                                            final TextView typeText = transactionLayout
                                                    .findViewById(R.id.monthly_balance_relative_layout_type);

                                            final TextView valueText = transactionLayout
                                                    .findViewById(R.id.monthly_balance_relative_layout_value);

                                            typeText.setText(Types.getTranslatedType(MonthlyBalanceActivity.this,
                                                    String.valueOf(Transaction
                                                            .getTypeFromIndexInEnglish(transactionsListIterator
                                                                    .getCategory()))));

                                            typeText.setTextColor(viewModel.getUserDetails() == null ||
                                                    !viewModel.getUserDetails().getApplicationSettings().getDarkTheme() ?
                                                    Color.BLACK : Color.WHITE);
                                            valueText.setTextColor(viewModel.getUserDetails() == null ||
                                                    !viewModel.getUserDetails().getApplicationSettings().getDarkTheme() ?
                                                    Color.BLACK : Color.WHITE);

                                            typeText.setTextSize(19);

                                            String text = Locale.getDefault().getDisplayLanguage().equals("English") ?
                                                    currencySymbol + transactionsListIterator.getValue() :
                                                    transactionsListIterator.getValue() + " " + currencySymbol;

                                            text = transactionsListIterator.getType() == 1 ?
                                                    "+" + text : "-" + text;

                                            valueText.setText(text);
                                            valueText.setTextSize(19);

                                            binding.mainLayout.addView(transactionLayout);
                                        }
                                    }

                                    final String textForTotalSum = Locale.getDefault().getDisplayLanguage()
                                            .equals("English") ?
                                            currencySymbol + Math.abs(dateTotalIncome - dateTotalExpense) :
                                            Math.abs(dateTotalIncome - dateTotalExpense) + " " + currencySymbol;

                                    totalSumText.setText(textForTotalSum);
                                    totalSumText.setTextSize(25);

                                    totalSumText.setTextColor(dateTotalIncome - dateTotalExpense < 0f ?
                                            Color.RED : Color.GREEN);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setCenterText() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            final boolean darkThemeEnabled = viewModel.getUserDetails() != null ?
                    viewModel.getUserDetails().getApplicationSettings().getDarkTheme() :
                    MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() &&
                                    snapshot.hasChild("PersonalTransactions") &&
                                    snapshot.child("PersonalTransactions").hasChildren()) {
                                int numberOfCurrentMonthTransactions = 0;

                                for (final DataSnapshot transaction :
                                        snapshot.child("PersonalTransactions").getChildren()) {
                                    final Transaction databaseTransaction = transaction.getValue(Transaction.class);

                                    if (databaseTransaction != null && databaseTransaction.getTime() != null &&
                                            databaseTransaction.getTime().getYear() == LocalDate.now().getYear() &&
                                            databaseTransaction.getTime().getMonth() == LocalDate.now().getMonthValue()) {
                                        ++numberOfCurrentMonthTransactions;
                                        break;
                                    }
                                }

                                binding.centerText.setText(numberOfCurrentMonthTransactions > 0 ?
                                        "" : getResources()
                                        .getString(R.string.no_transactions_this_month));
                            } else {
                                binding.centerText.setText(R.string.no_transactions_yet);
                            }
                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });

            binding.centerText.setTextColor(!darkThemeEnabled ? getColor(R.color.turkish_sea) : Color.WHITE);
            binding.centerText.setTextSize(20);
        }
    }
}