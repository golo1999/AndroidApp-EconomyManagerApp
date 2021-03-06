package com.example.economy_manager.main_part.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.R;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.main_part.viewmodel.MainScreenViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ShowSavingsFragment extends Fragment {
    private MainScreenViewModel viewModel;
    private TextView savingsText;

    public ShowSavingsFragment() {
        // Required empty public constructor
    }

    public static ShowSavingsFragment newInstance() {
        final ShowSavingsFragment fragment = new ShowSavingsFragment();
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
        final View v = inflater.inflate(R.layout.show_savings_fragment, container, false);

        setVariables(v);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        setSavings();
    }

    private void setVariables(final View v) {
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MainScreenViewModel.class);
        savingsText = v.findViewById(R.id.fragment2Savings);
    }

    private void setSavings() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
                            final String currencySymbol = viewModel.getUserDetails() != null ?
                                    viewModel.getUserDetails().getApplicationSettings().getCurrencySymbol() :
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

                            final float totalMonthlySavings = totalMonthlyIncomes - totalMonthlyExpenses;

                            savingsText.setText(!Locale.getDefault().getDisplayLanguage().equals("English") ?
                                    totalMonthlySavings + " " + currencySymbol : totalMonthlySavings < 0f ?
                                    "-" + currencySymbol + Math.abs(totalMonthlySavings) :
                                    currencySymbol + totalMonthlySavings);

                            savingsText.setTextColor(totalMonthlySavings > 0f ? Color.GREEN : Color.RED);
                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });
        } else {
            final String currencySymbol = viewModel.getUserDetails() != null ?
                    viewModel.getUserDetails().getApplicationSettings().getCurrencySymbol() :
                    MyCustomMethods.getCurrencySymbol();
            final float totalMonthlyIncomes = 0f;
            final float totalMonthlyExpenses = 0f;
            final float totalMonthlySavings = totalMonthlyIncomes - totalMonthlyExpenses;

            savingsText.setText(Locale.getDefault().getDisplayLanguage().equals("English") ?
                    currencySymbol + totalMonthlySavings : totalMonthlySavings + " " + currencySymbol);
        }
    }
}