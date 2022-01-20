package com.example.economy_manager.feature.budgetreview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.BudgetReviewFragmentBinding;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Languages;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Locale;

public class BudgetReviewFragment extends Fragment {
    private BudgetReviewFragmentBinding binding;
    private UserDetails userDetails;

    public BudgetReviewFragment() {
        // Required empty public constructor
    }

    public static BudgetReviewFragment newInstance() {
        final BudgetReviewFragment fragment = new BudgetReviewFragment();

        final Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        setVariables(inflater, container);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setUserDetails();
        updateMoneyBalance();
    }

    private void setVariables(final @NonNull LayoutInflater inflater,
                              final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.budget_review_fragment, container, false);
    }

    public void setUserDetails() {
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(requireContext());
    }

    private void updateMoneyBalance() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
                            final String currencySymbol = userDetails != null ?
                                    userDetails.getApplicationSettings().getCurrencySymbol() :
                                    MyCustomMethods.getCurrencySymbol();

                            float totalMonthlyIncomes = 0f;

                            float totalMonthlyExpenses = 0f;

                            if (snapshot.exists() && snapshot.hasChild("PersonalTransactions") &&
                                    snapshot.child("PersonalTransactions").hasChildren()) {
                                for (final DataSnapshot databaseTransaction :
                                        snapshot.child("PersonalTransactions").getChildren()) {
                                    final Transaction transaction = databaseTransaction.getValue(Transaction.class);

                                    if (transaction != null && transaction.getTime() != null &&
                                            transaction.getTime().getYear() == LocalDate.now().getYear() &&
                                            transaction.getTime().getMonth() == LocalDate.now().getMonthValue()) {
                                        if (transaction.getType() == 1) {
                                            totalMonthlyIncomes += Float.parseFloat(transaction.getValue());
                                        } else {
                                            totalMonthlyExpenses += Float.parseFloat(transaction.getValue());
                                        }
                                    }
                                }
                            }

                            totalMonthlyIncomes =
                                    MyCustomMethods.getRoundedNumberToNDecimalPlaces(totalMonthlyIncomes, 2);

                            totalMonthlyExpenses =
                                    MyCustomMethods.getRoundedNumberToNDecimalPlaces(totalMonthlyExpenses, 2);

                            final String totalMonthlyIncomesText =
                                    Locale.getDefault().getDisplayLanguage().equals(Languages.ENGLISH_LANGUAGE) ?
                                            currencySymbol + totalMonthlyIncomes :
                                            totalMonthlyIncomes + " " + currencySymbol;

                            final String totalMonthlyExpensesText =
                                    Locale.getDefault().getDisplayLanguage().equals(Languages.ENGLISH_LANGUAGE) ?
                                            currencySymbol + totalMonthlyExpenses :
                                            totalMonthlyExpenses + " " + currencySymbol;

                            binding.incomesValue.setText(totalMonthlyIncomesText);

                            binding.expensesValue.setText(totalMonthlyExpensesText);

                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });
        } else {
            final String currencySymbol = userDetails != null ?
                    userDetails.getApplicationSettings().getCurrencySymbol() : MyCustomMethods.getCurrencySymbol();

            final float totalMonthlyIncomes = 0f;

            final float totalMonthlyExpenses = 0f;

            final String totalMonthlyIncomesText =
                    Locale.getDefault().getDisplayLanguage().equals(Languages.ENGLISH_LANGUAGE) ?
                            currencySymbol + totalMonthlyIncomes : totalMonthlyIncomes + " " + currencySymbol;

            final String totalMonthlyExpensesText =
                    Locale.getDefault().getDisplayLanguage().equals(Languages.ENGLISH_LANGUAGE) ?
                            currencySymbol + totalMonthlyExpenses : totalMonthlyExpenses + " " + currencySymbol;

            binding.incomesValue.setText(totalMonthlyIncomesText);
            binding.expensesValue.setText(totalMonthlyExpensesText);
        }
    }
}