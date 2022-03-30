package com.example.economy_manager.feature.lastweekexpenses;

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
import com.example.economy_manager.databinding.LastWeekExpensesFragmentBinding;
import com.example.economy_manager.feature.mainscreen.MainScreenViewModel;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.utility.Languages;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class LastWeekExpensesFragment extends Fragment {

    private LastWeekExpensesFragmentBinding binding;
    private MainScreenViewModel viewModel;

    public LastWeekExpensesFragment() {
        // Required empty public constructor
    }

    public static LastWeekExpensesFragment newInstance() {
        final LastWeekExpensesFragment fragment = new LastWeekExpensesFragment();
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
        setLastWeekExpenses();
    }

    private void setVariables(final @NonNull LayoutInflater inflater,
                              final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.last_week_expenses_fragment, container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MainScreenViewModel.class);
    }

    private void setLastWeekExpenses() {
        if (MyCustomVariables.getFirebaseAuth().getUid() == null) {
            return;
        }

        MyCustomVariables.getDatabaseReference()
                .child(MyCustomVariables.getFirebaseAuth().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final @NonNull DataSnapshot snapshot) {
                        final String currencySymbol = viewModel.getUserDetails() != null ?
                                viewModel.getUserDetails().getApplicationSettings().getCurrencySymbol() :
                                MyCustomMethods.getCurrencySymbol();

                        float moneySpentLastWeek = 0f;

                        if (snapshot.exists() &&
                                snapshot.hasChild("personalTransactions") &&
                                snapshot.child("personalTransactions").hasChildren()) {
                            for (final DataSnapshot transactionIterator :
                                    snapshot.child("personalTransactions").getChildren()) {
                                final Transaction transaction = transactionIterator.getValue(Transaction.class);

                                if (transaction != null &&
                                        transaction.getType() == 0 &&
                                        MyCustomMethods.transactionWasMadeInTheLastWeek(transaction.getTime())) {
                                    moneySpentLastWeek += Float.parseFloat(transaction.getValue());
                                }
                            }
                        }

                        // if the user hasn't spent money in the last week
                        if (moneySpentLastWeek <= 0f) {
                            binding.moneySpentText.setText(R.string.no_money_spent_last_week);
                        }
                        // if the user spent money in the last week
                        else {
                            if (String.valueOf(moneySpentLastWeek).contains(".") &&
                                    String.valueOf(moneySpentLastWeek).length() -
                                            String.valueOf(moneySpentLastWeek).indexOf(".") > 3) {
                                moneySpentLastWeek = Float.parseFloat(String.format(Locale.getDefault(),
                                        "%.2f", moneySpentLastWeek));
                            }

                            final boolean languageIsEnglish =
                                    Locale.getDefault().getDisplayLanguage().equals(Languages.ENGLISH_LANGUAGE);

                            final String moneyPlusCurrencySymbol = languageIsEnglish ?
                                    currencySymbol + moneySpentLastWeek : moneySpentLastWeek + currencySymbol;

                            final String youSpentText =
                                    requireContext().getResources().getString(R.string.you_spent_last_week,
                                            moneyPlusCurrencySymbol);

                            binding.moneySpentText.setText(youSpentText.trim());
                        }
                    }

                    @Override
                    public void onCancelled(final @NonNull DatabaseError error) {

                    }
                });
    }
}