package com.example.economy_manager.main_part.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.economy_manager.R;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class FavoriteExpensesCategoryFragment extends Fragment {

    public FavoriteExpensesCategoryFragment() {
        // Required empty public constructor
    }

    public static FavoriteExpensesCategoryFragment newInstance() {
        final FavoriteExpensesCategoryFragment fragment = new FavoriteExpensesCategoryFragment();
        final Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.favorite_expenses_category_fragment, container, false);

        setVariables(v);

        return v;
    }

    private void createAndSetExpensesList() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
                            final HashMap<Integer, Float> expensesMap = new HashMap<>();

                            if (snapshot.exists() &&
                                    snapshot.hasChild("PersonalTransactions") &&
                                    snapshot.child("PersonalTransactions").hasChildren()) {
                                for (DataSnapshot transactionIterator :
                                        snapshot.child("PersonalTransactions").getChildren()) {
                                    final Transaction transaction = transactionIterator.getValue(Transaction.class);

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
                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setVariables(final View v) {

    }
}