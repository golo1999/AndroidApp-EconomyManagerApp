package com.example.economy_manager.main_part.views.fragments;

import android.graphics.Color;
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
import com.example.economy_manager.main_part.viewmodels.MainScreenViewModel;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.example.economy_manager.utilities.Types;
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

    public static LastTenTransactionsFragment newInstance() {
        final LastTenTransactionsFragment fragment = new LastTenTransactionsFragment();
        final Bundle args = new Bundle();


        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.last_ten_transactions_fragment, container, false);

        setVariables(v);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        createAndSetList();
    }

    private void setVariables(final View v) {
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MainScreenViewModel.class);
        mainLayout = v.findViewById(R.id.fragmentLastTenTransactionsMainLayout);
    }

    // method for setting the transactions list and displaying it on the screen
    private void createAndSetList() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference().child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final ArrayList<Transaction> transactionsList = new ArrayList<>();
                            final String currencySymbol = viewModel.getUserDetails() != null ?
                                    viewModel.getUserDetails().getApplicationSettings().getCurrencySymbol() :
                                    MyCustomMethods.getCurrencySymbol();

                            if (snapshot.exists() && snapshot.hasChild("PersonalTransactions") &&
                                    snapshot.child("PersonalTransactions").hasChildren()) {
                                for (DataSnapshot transactionIterator :
                                        snapshot.child("PersonalTransactions").getChildren()) {
                                    final Transaction transaction = transactionIterator.getValue(Transaction.class);

                                    if (transaction != null && transaction.getTime() != null &&
                                            transaction.getTime().getMonth() == LocalDate.now().getMonthValue()) {
                                        transactionsList.add(transaction);
                                    }
                                }
                            }

                            // displaying the list on the screen
                            showLastTransactions(mainLayout, transactionsList, currencySymbol);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else {
            showNoTransactionsLayout(mainLayout, getResources().getString(R.string.no_transactions_yet));
        }
    }

    // method for displaying the list on the screen
    private void showLastTransactions(final LinearLayout mainLayout, final ArrayList<Transaction> transactionsList,
                                      final String currencySymbol) {
        // removing all the existing views from the main layout
        mainLayout.removeAllViews();

        try {
            if (transactionsList.size() == 0) {
                showNoTransactionsLayout(mainLayout, getResources().getString(R.string.no_transactions_this_month));
            }
            // if the transactions list isn't empty
            else {
                ArrayList<Transaction> limitedTransactionsList = null;

                // sorting the list by date descending
                transactionsList.sort((transaction1, transaction2) ->
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
                            .inflate(R.layout.last_ten_transactions_linearlayout, mainLayout, false);

                    final TextView typeFromChildLayout =
                            childLayout.findViewById(R.id.lastTenTransactionsRelativeLayoutTitle);

                    final TextView valueFromChildLayout =
                            childLayout.findViewById(R.id.lastTenTransactionsRelativeLayoutPrice);

                    final String valueWithCurrency = Locale.getDefault().getDisplayLanguage().equals("English") ?
                            currencySymbol + transaction.getValue() : transaction.getValue() + " " + currencySymbol;

                    final String translatedType = Types.getTranslatedType(requireContext(),
                            String.valueOf(Transaction.getTypeFromIndexInEnglish(transaction.getCategory())));

                    valueFromChildLayout.setTextColor(transaction.getType() == 1 ? Color.GREEN : Color.RED);

                    // displaying the views on the screen if the type was successfully translated
                    if (translatedType != null) {
                        typeFromChildLayout.setText(translatedType);
                        valueFromChildLayout.setText(valueWithCurrency);
                        typeFromChildLayout.setTextColor(Color.WHITE);
                        typeFromChildLayout.setTextSize(18);
                        valueFromChildLayout.setTextSize(18);

                        mainLayout.addView(childLayout);
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void showNoTransactionsLayout(final LinearLayout mainLayout, final String message) {
        final TextView noTransactions = new TextView(getContext());
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 20, 0, 20);

        noTransactions.setText(message);
        noTransactions.setTextColor(Color.parseColor("#FAEBEF"));
        noTransactions.setTypeface(null, Typeface.BOLD);
        noTransactions.setTextSize(20);
        noTransactions.setGravity(Gravity.CENTER);
        noTransactions.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        noTransactions.setLayoutParams(params);

        mainLayout.addView(noTransactions);
    }
}