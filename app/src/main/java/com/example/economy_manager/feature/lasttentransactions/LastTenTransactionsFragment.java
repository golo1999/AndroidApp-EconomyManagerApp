package com.example.economy_manager.feature.lasttentransactions;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.economy_manager.R;
import com.example.economy_manager.feature.mainscreen.MainScreenViewModel;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.utility.Types;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

public class LastTenTransactionsFragment extends Fragment {

    private MainScreenViewModel viewModel;
    private LinearLayout mainLayout;

    public LastTenTransactionsFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static LastTenTransactionsFragment newInstance() {
        final LastTenTransactionsFragment fragment = new LastTenTransactionsFragment();
        final Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.last_ten_transactions_fragment, container,
                false);

        setVariables(v);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        createAndSetList();
    }

    private void setVariables(final @NonNull View v) {
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MainScreenViewModel.class);
        mainLayout = v.findViewById(R.id.mainLayout);
    }

    // method for setting the transactions list and displaying it on the screen
    private void createAndSetList() {
        if (MyCustomVariables.getFirebaseAuth().getUid() == null) {
            showNoTransactionsLayout(mainLayout,
                    requireContext().getResources().getString(R.string.no_transactions_made_yet));

            return;
        }

        MyCustomVariables.getDatabaseReference().child(MyCustomVariables.getFirebaseAuth().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final @NonNull DataSnapshot snapshot) {
                        final ArrayList<Transaction> transactionsList = new ArrayList<>();
                        final String currencySymbol = viewModel.getUserDetails() != null ?
                                viewModel.getUserDetails().getApplicationSettings()
                                        .getCurrencySymbol() :
                                MyCustomMethods.getCurrencySymbol();

                        if (snapshot.exists() &&
                                snapshot.hasChild("personalTransactions") &&
                                snapshot.child("personalTransactions").hasChildren()) {
                            for (DataSnapshot transactionIterator :
                                    snapshot.child("personalTransactions").getChildren()) {
                                final Transaction transaction =
                                        transactionIterator.getValue(Transaction.class);

                                if (transaction != null && transaction.getTime() != null &&
                                        transaction.getTime().getYear() ==
                                                LocalDate.now().getYear() &&
                                        transaction.getTime().getMonth() ==
                                                LocalDate.now().getMonthValue()) {
                                    transactionsList.add(transaction);
                                }
                            }
                        }
                        // displaying the list on the screen
                        showLastTransactions(mainLayout, transactionsList, currencySymbol);
                    }

                    @Override
                    public void onCancelled(final @NonNull DatabaseError error) {

                    }
                });
    }

    // method for displaying the list on the screen
    private void showLastTransactions(final @NonNull LinearLayout mainLayout,
                                      final ArrayList<Transaction> transactionsList,
                                      final String currencySymbol) {
        // removing all the existing views from the main layout
        mainLayout.removeAllViews();

        if (transactionsList.size() == 0) {
            showNoTransactionsLayout(mainLayout, requireContext().getResources()
                    .getString(R.string.no_transactions_made_this_month));

            return;
        }

        ArrayList<Transaction> limitedTransactionsList = null;

        // sorting the list by date descending
        transactionsList.sort((final Transaction transaction1, final Transaction transaction2) ->
                transaction2.getTime().compareTo(transaction1.getTime()));

        // creating a new list from the first 10 transactions only if there are more than that
        if (transactionsList.size() > 10) {
            limitedTransactionsList = (ArrayList<Transaction>) transactionsList.stream()
                    .limit(10).collect(Collectors.toList());
        }

        // iterating through the new list only if there are more than 10 transactions,
        // otherwise iterating through the old list
        for (final Transaction transaction :
                (limitedTransactionsList != null ? limitedTransactionsList : transactionsList)) {
            final LinearLayout childLayout = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.last_ten_transactions_item_layout, mainLayout, false);

            final TextView typeFromChildLayout =
                    childLayout.findViewById(R.id.transactionTitle);

            final TextView valueFromChildLayout =
                    childLayout.findViewById(R.id.transactionValue);

            final String valueWithCurrency =
                    Locale.getDefault().getDisplayLanguage().equals("English") ?
                            currencySymbol + transaction.getValue() :
                            transaction.getValue() + " " + currencySymbol;

            final String translatedType = Types.getTranslatedType(requireContext(),
                    String.valueOf(Transaction.getTypeFromIndexInEnglish(transaction
                            .getCategory())));

            valueFromChildLayout.setTextColor(transaction.getType() == 1 ?
                    requireContext().getColor(R.color.secondaryLight) :
                    requireContext().getColor(R.color.crimson));

            if (translatedType == null) {
                return;
            }

            // displaying the views on the screen if the type was successfully translated
            typeFromChildLayout.setText(translatedType);
            valueFromChildLayout.setText(valueWithCurrency);
            typeFromChildLayout.setTextColor(requireContext().getColor(R.color.quaternaryLight));
            typeFromChildLayout.setTextSize(18);
            valueFromChildLayout.setTextSize(18);

            mainLayout.addView(childLayout);
        }
    }

    private void showNoTransactionsLayout(final LinearLayout mainLayout,
                                          final String message) {
        final TextView noTransactions = new TextView(getContext());
        final LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 20, 0, 20);

        noTransactions.setText(message);
        noTransactions.setTextColor(requireContext().getColor(R.color.quaternaryLight));
        noTransactions.setTypeface(null, Typeface.BOLD);
        noTransactions.setTextSize(20);
        noTransactions.setGravity(Gravity.CENTER);
        noTransactions.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        noTransactions.setLayoutParams(params);

        mainLayout.addView(noTransactions);
    }
}