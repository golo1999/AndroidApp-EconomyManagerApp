package com.example.economy_manager.feature.showsavings;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.ShowSavingsFragmentBinding;
import com.example.economy_manager.feature.mainscreen.MainScreenViewModel;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.utility.Languages;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Locale;

public class ShowSavingsFragment extends Fragment {
    private ShowSavingsFragmentBinding binding;
    private MainScreenViewModel viewModel;

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
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        setVariables(inflater, container);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setSavings();
    }

    private void setVariables(final @NonNull LayoutInflater inflater,
                              final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.show_savings_fragment, container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MainScreenViewModel.class);
    }

    private void setSavings() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference().child(MyCustomVariables.getFirebaseAuth().getUid())
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

                            float totalMonthlySavings = totalMonthlyIncomes - totalMonthlyExpenses;

                            totalMonthlySavings =
                                    MyCustomMethods.getRoundedNumberToNDecimalPlaces(totalMonthlySavings, 2);

                            final String savingsText =
                                    !Locale.getDefault().getDisplayLanguage().equals(Languages.getEnglishLanguage()) ?
                                            totalMonthlySavings + " " + currencySymbol : totalMonthlySavings < 0f ?
                                            "-" + currencySymbol + Math.abs(totalMonthlySavings) :
                                            currencySymbol + totalMonthlySavings;

                            binding.showSavingsText.setText(savingsText);

                            binding.showSavingsText.setTextColor(totalMonthlySavings > 0f ?
                                    Color.GREEN : totalMonthlySavings == 0f ? Color.BLUE : Color.RED);
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

            final String savingsText =
                    Locale.getDefault().getDisplayLanguage().equals(Languages.getEnglishLanguage()) ?
                            currencySymbol + totalMonthlySavings : totalMonthlySavings + " " + currencySymbol;

            binding.showSavingsText.setText(savingsText);
        }
    }
}