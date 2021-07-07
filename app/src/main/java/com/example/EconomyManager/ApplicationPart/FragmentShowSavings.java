package com.example.EconomyManager.ApplicationPart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.EconomyManager.MyCustomMethods;
import com.example.EconomyManager.MyCustomSharedPreferences;
import com.example.EconomyManager.MyCustomVariables;
import com.example.EconomyManager.R;
import com.example.EconomyManager.Transaction;
import com.example.EconomyManager.UserDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class FragmentShowSavings extends Fragment {
    private UserDetails userDetails;
    private TextView savingsText;

    public FragmentShowSavings() {

    }

    public static FragmentShowSavings newInstance() {
        final FragmentShowSavings fragment = new FragmentShowSavings();
        final Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_show_savings, container, false);

        setVariables(v);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUserDetails();
        setSavings();
    }

    private void setVariables(final View v) {
        savingsText = v.findViewById(R.id.fragment2Savings);
    }

    public void setUserDetails() {
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(requireContext());
    }

    private void setSavings() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid()).addValueEventListener(new ValueEventListener() {
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
                                if (transaction.getCategory() > 0 && transaction.getCategory() < 4) {
                                    totalMonthlyIncomes += Float.parseFloat(transaction.getValue());
                                } else {
                                    totalMonthlyExpenses += Float.parseFloat(transaction.getValue());
                                }
                            }
                        }
                    }

                    final float totalMonthlySavings = totalMonthlyIncomes - totalMonthlyExpenses;

                    savingsText.setText(Locale.getDefault()
                            .getDisplayLanguage().equals("English") ?
                            currencySymbol + totalMonthlySavings :
                            totalMonthlySavings + " " + currencySymbol);

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
            final float totalMonthlySavings = totalMonthlyIncomes - totalMonthlyExpenses;

            savingsText.setText(Locale.getDefault()
                    .getDisplayLanguage().equals("English") ?
                    currencySymbol + totalMonthlySavings :
                    totalMonthlySavings + " " + currencySymbol);
        }
    }
}