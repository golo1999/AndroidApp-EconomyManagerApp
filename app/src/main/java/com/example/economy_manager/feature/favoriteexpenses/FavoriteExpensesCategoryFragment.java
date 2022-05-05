package com.example.economy_manager.feature.favoriteexpenses;

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
import com.example.economy_manager.databinding.FavoriteExpensesCategoryFragmentBinding;
import com.example.economy_manager.feature.mainscreen.MainScreenViewModel;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Languages;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.utility.Types;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class FavoriteExpensesCategoryFragment extends Fragment {

    private FavoriteExpensesCategoryFragmentBinding binding;
    private MainScreenViewModel viewModel;

    public FavoriteExpensesCategoryFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static FavoriteExpensesCategoryFragment newInstance() {
        final FavoriteExpensesCategoryFragment fragment = new FavoriteExpensesCategoryFragment();
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
        setExpensesMap();
    }

    private void setExpensesMap() {
        if (MyCustomVariables.getFirebaseAuth().getUid() == null) {
            return;
        }

        MyCustomVariables.getDatabaseReference()
                .child(MyCustomVariables.getFirebaseAuth().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final @NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            return;
                        }

                        final UserDetails details = snapshot.getValue(UserDetails.class);

                        if (details == null || details.getPersonalTransactions() == null) {
                            binding.text.setText(requireContext().getResources()
                                    .getString(R.string.no_expenses_made_yet));
                            return;
                        }

                        final LinkedHashMap<Integer, Float> expensesMap = new LinkedHashMap<>();
                        final String fragmentText;

                        if (!details.getPersonalTransactions().isEmpty()) {
                            for (Map.Entry<String, Transaction> transactionEntry :
                                    details.getPersonalTransactions().entrySet()) {
                                final Transaction transaction = transactionEntry.getValue();

                                if (transaction != null && transaction.getType() == 0) {
                                    final Integer transactionCategory = transaction.getCategory();

                                    final Float transactionValue =
                                            Float.parseFloat(transaction.getValue());

                                    if (expensesMap.isEmpty() ||
                                            !expensesMap.containsKey(transactionCategory)) {
                                        expensesMap.put(transactionCategory, transactionValue);
                                    } else if (expensesMap.containsKey(transactionCategory)) {
                                        final Float currentCategoryTotalValue =
                                                expensesMap.get(transactionCategory);

                                        if (currentCategoryTotalValue != null) {
                                            final Float newCategoryTotalValue =
                                                    currentCategoryTotalValue + transactionValue;

                                            expensesMap.replace(transactionCategory,
                                                    newCategoryTotalValue);
                                        }
                                    }
                                }
                            }
                        }

                        if (!expensesMap.isEmpty()) {
                            MyCustomMethods.sortMapDescendingByValue(expensesMap);

                            final String favoriteExpenseCategoryName =
                                    Types.getTranslatedType(requireContext(),
                                            Transaction.getTypeFromIndexInEnglish((Integer)
                                                    expensesMap.keySet().toArray()[0]));
                            final float favoriteExpenseAmount =
                                    (float) expensesMap.values().toArray()[0];
                            final String currencySymbol = viewModel.getUserDetails() != null ?
                                    viewModel.getUserDetails().getApplicationSettings()
                                            .getCurrencySymbol() :
                                    MyCustomMethods.getCurrencySymbol();
                            final String favoriteExpenseAmountWithCurrency =
                                    Locale.getDefault().getDisplayLanguage()
                                            .equals(Languages.ENGLISH_LANGUAGE) ?
                                            currencySymbol + favoriteExpenseAmount :
                                            favoriteExpenseAmount + " " + currencySymbol;
                            fragmentText = favoriteExpenseCategoryName +
                                    ": " + favoriteExpenseAmountWithCurrency;
                        } else {
                            fragmentText = requireContext().getResources()
                                    .getString(R.string.no_expenses_made_yet);
                        }

                        binding.text.setText(fragmentText);
                    }

                    @Override
                    public void onCancelled(final @NonNull DatabaseError error) {

                    }
                });
    }

    private void setVariables(final @NonNull LayoutInflater inflater,
                              final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.favorite_expenses_category_fragment,
                container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext())
                .get(MainScreenViewModel.class);
    }
}