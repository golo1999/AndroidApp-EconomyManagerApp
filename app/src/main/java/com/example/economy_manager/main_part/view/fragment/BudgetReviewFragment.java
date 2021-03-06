package com.example.economy_manager.main_part.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.R;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class BudgetReviewFragment extends Fragment {
    private UserDetails userDetails;
    private TextView incomesText;
    private TextView expensesText;

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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.budget_review_cardview, container, false);

        setVariables(v);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUserDetails();
        updateMoneyBalance();
    }

    private void setVariables(final View v) {
        incomesText = v.findViewById(R.id.budget_review_incomes_value);
        expensesText = v.findViewById(R.id.budget_review_expenses_value);
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

                                    if (transaction != null) {
                                        if (transaction.getCategory() >= 0 && transaction.getCategory() < 4) {
                                            totalMonthlyIncomes += Float.parseFloat(transaction.getValue());
                                        } else {
                                            totalMonthlyExpenses += Float.parseFloat(transaction.getValue());
                                        }
                                    }
                                }
                            }

                            incomesText.setText(Locale.getDefault()
                                    .getDisplayLanguage().equals("English") ?
                                    currencySymbol + totalMonthlyIncomes :
                                    totalMonthlyIncomes + " " + currencySymbol);
                            expensesText.setText(Locale.getDefault()
                                    .getDisplayLanguage().equals("English") ?
                                    currencySymbol + totalMonthlyExpenses :
                                    totalMonthlyExpenses + " " + currencySymbol);

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

            incomesText.setText(Locale.getDefault()
                    .getDisplayLanguage().equals("English") ?
                    currencySymbol + totalMonthlyIncomes :
                    totalMonthlyIncomes + " " + currencySymbol);
            expensesText.setText(Locale.getDefault()
                    .getDisplayLanguage().equals("English") ?
                    currencySymbol + totalMonthlyExpenses :
                    totalMonthlyExpenses + " " + currencySymbol);
        }
    }
}