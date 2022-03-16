package com.example.economy_manager.feature.favoriteexpenses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.economy_manager.R;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedHashMap;
import java.util.Map;

public class FavoriteExpensesCategoryFragment extends Fragment {

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
        final View v = inflater.inflate(R.layout.favorite_expenses_category_fragment, container, false);

        setVariables(v);

        return v;
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
                        final LinkedHashMap<Integer, Float> expensesMap = new LinkedHashMap<>();

                        if (!snapshot.exists()) {
                            return;
                        }

                        final UserDetails details = snapshot.getValue(UserDetails.class);

                        if (details == null || details.getPersonalTransactions() == null) {
                            return;
                        }

                        if (!details.getPersonalTransactions().isEmpty()) {
                            for (Map.Entry<String, Transaction> transactionEntry :
                                    details.getPersonalTransactions().entrySet()) {
                                final Transaction transaction = transactionEntry.getValue();

                                if (transaction != null && transaction.getType() == 0) {
                                    final Integer transactionCategory = transaction.getCategory();

                                    final Float transactionValue = Float.parseFloat(transaction.getValue());

                                    if (expensesMap.isEmpty() || !expensesMap.containsKey(transactionCategory)) {
                                        expensesMap.put(transactionCategory, transactionValue);
                                    } else if (expensesMap.containsKey(transactionCategory)) {
                                        final Float currentCategoryTotalValue = expensesMap.get(transactionCategory);

                                        if (currentCategoryTotalValue != null) {
                                            final Float newCategoryTotalValue =
                                                    currentCategoryTotalValue + transactionValue;

                                            expensesMap.replace(transactionCategory, newCategoryTotalValue);
                                        }
                                    }
                                }
                            }
                        }

                        if (!expensesMap.isEmpty()) {
                            MyCustomMethods.sortMapDescendingByValue(expensesMap);
                        } else {

                        }

                        Log.d("expensesMap", expensesMap.toString());
                    }

                    @Override
                    public void onCancelled(final @NonNull DatabaseError error) {

                    }
                });
    }

    private void setVariables(final View v) {

    }
}