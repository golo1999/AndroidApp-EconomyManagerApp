package com.example.economy_manager.feature.moneyspent;

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
import com.example.economy_manager.databinding.MoneySpentFragmentBinding;
import com.example.economy_manager.feature.mainscreen.MainScreenViewModel;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.utility.Languages;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MoneySpentFragment extends Fragment {
    private MoneySpentFragmentBinding binding;
    private MainScreenViewModel viewModel;

    public MoneySpentFragment() {
        // Required empty public constructor
    }

    public static MoneySpentFragment newInstance() {
        final MoneySpentFragment fragment = new MoneySpentFragment();
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
        binding = DataBindingUtil.inflate(inflater, R.layout.money_spent_fragment, container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MainScreenViewModel.class);
    }

    private void setLastWeekExpenses() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
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
                                    snapshot.hasChild("PersonalTransactions") &&
                                    snapshot.child("PersonalTransactions").hasChildren()) {
                                for (final DataSnapshot transactionIterator :
                                        snapshot.child("PersonalTransactions").getChildren()) {
                                    final Transaction transaction = transactionIterator.getValue(Transaction.class);

                                    if (transaction != null &&
                                            transaction.getType() == 0 &&
                                            viewModel.transactionWasMadeInTheLastWeek(transaction.getTime())) {
                                        moneySpentLastWeek += Float.parseFloat(transaction.getValue());
                                    }
                                }
                            }

                            // if the user hasn't spent money in the last week
                            if (moneySpentLastWeek <= 0f) {
                                binding.moneySpentText.setText(R.string.no_money_last_week);
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
                                        Locale.getDefault().getDisplayLanguage().equals(Languages.getENGLISH_LANGUAGE());

                                final String youSpentText = languageIsEnglish ?
                                        // english
                                        requireContext().getResources().getString(R.string.money_spent_you_spent) +
                                                " " + currencySymbol + moneySpentLastWeek + " " +
                                                requireContext().getResources().getString(R.string.money_spent_last_week) :
                                        // everything else
                                        requireContext().getResources().getString(R.string.money_spent_you_spent) +
                                                " " + moneySpentLastWeek + " " + currencySymbol + " " +
                                                requireContext().getResources().getString(R.string.money_spent_last_week);

                                binding.moneySpentText.setText(youSpentText.trim());
                            }
                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });
        }
    }
}