package com.example.economy_manager.feature.overallprofit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.OverallProfitFragmentBinding;
import com.example.economy_manager.feature.mainscreen.MainScreenViewModel;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Languages;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Map;

public class OverallProfitFragment extends Fragment {

    private OverallProfitFragmentBinding binding;
    private MainScreenViewModel viewModel;

    public OverallProfitFragment() {
        // Required empty public constructor
    }

    public static OverallProfitFragment newInstance() {
        final OverallProfitFragment fragment = new OverallProfitFragment();
        final Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        setFragmentVariables(inflater, container);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setOverallProfit();
    }

    private void setFragmentVariables(final @NonNull LayoutInflater inflater,
                                      final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.overall_profit_fragment,
                container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext())
                .get(MainScreenViewModel.class);
    }

    private void setOverallProfit() {
        if (MyCustomVariables.getFirebaseAuth().getUid() == null) {
            return;
        }

        MyCustomVariables.getDatabaseReference()
                .child(MyCustomVariables.getFirebaseAuth().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            return;
                        }

                        final UserDetails details = snapshot.getValue(UserDetails.class);

                        if (details == null || details.getPersonalTransactions() == null) {
                            binding.text.setText(requireContext().getResources()
                                    .getString(R.string.no_transactions_made_yet));
                            return;
                        }

                        final String currencySymbol = viewModel.getUserDetails() != null ?
                                viewModel.getUserDetails().getApplicationSettings()
                                        .getCurrencySymbol() :
                                MyCustomMethods.getCurrencySymbol();
                        float totalIncomes = 0f;
                        float totalExpenses = 0f;
                        final String fragmentText;

                        if (!details.getPersonalTransactions().isEmpty()) {
                            for (Map.Entry<String, Transaction> transactionEntry :
                                    details.getPersonalTransactions().entrySet()) {
                                final Transaction transaction = transactionEntry.getValue();

                                if (transaction != null) {
                                    if (transaction.getType() == 1) {
                                        totalIncomes += Float.parseFloat(transaction.getValue());
                                    } else {
                                        totalExpenses += Float.parseFloat(transaction.getValue());
                                    }
                                }
                            }
                        }

                        float overallProfit = totalIncomes - totalExpenses;

                        overallProfit = MyCustomMethods
                                .getRoundedNumberToNDecimalPlaces(overallProfit, 2);
                        fragmentText = !Locale.getDefault().getDisplayLanguage()
                                .equals(Languages.ENGLISH_LANGUAGE) ?
                                overallProfit + " " + currencySymbol :
                                overallProfit < 0f ?
                                        "-" + currencySymbol + Math.abs(overallProfit) :
                                        currencySymbol + overallProfit;

                        binding.text.setText(fragmentText);
                        binding.text.setTextColor(overallProfit > 0f ?
                                requireContext().getColor(R.color.secondaryLight) :
                                overallProfit == 0f ?
                                        requireContext().getColor(R.color.quaternaryLight) :
                                        requireContext().getColor(R.color.crimson));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}