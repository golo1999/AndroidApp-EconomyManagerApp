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

public class TopFiveExpensesFragment extends Fragment {
    private MainScreenViewModel viewModel;
    private LinearLayout mainLayout;
    private TextView centerText;

    public TopFiveExpensesFragment() {
        // Required empty public constructor
    }

    public static TopFiveExpensesFragment newInstance() {
        final TopFiveExpensesFragment fragment = new TopFiveExpensesFragment();
        final Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.top_five_expenses_fragment, container, false);

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
        mainLayout = v.findViewById(R.id.topFiveExpensesMainLayout);
        centerText = v.findViewById(R.id.topFiveExpensesCenterText);
    }

    private void createAndSetList() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            final ArrayList<Transaction> expensesList = new ArrayList<>();
                            final String currencySymbol = viewModel.getUserDetails() != null ?
                                    viewModel.getUserDetails().getApplicationSettings().getCurrencySymbol() :
                                    MyCustomMethods.getCurrencySymbol();

                            if (snapshot.exists() && snapshot.hasChild("PersonalTransactions") &&
                                    snapshot.child("PersonalTransactions").hasChildren()) {
                                for (DataSnapshot transactionIterator :
                                        snapshot.child("PersonalTransactions").getChildren()) {
                                    final Transaction transaction = transactionIterator.getValue(Transaction.class);

                                    if (transaction != null && transaction.getTime() != null &&
                                            transaction.getTime().getMonth() == LocalDate.now().getMonthValue() &&
                                            transaction.getType() == 0) {
                                        expensesList.add(transaction);
                                    }
                                }
                            }

                            showTopExpenses(mainLayout, expensesList, currencySymbol);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else {
            centerText.setText(getResources().getString(R.string.top_5_expenses_no_expenses_yet));
            centerText.setVisibility(View.VISIBLE);
            //showNoExpensesLayout(mainLayout, getResources().getString(R.string.top_5_expenses_no_expenses_yet));
        }
    }

    private void showTopExpenses(final LinearLayout mainLayout, final ArrayList<Transaction> expensesList,
                                 final String currencySymbol) {
        mainLayout.removeAllViews();

        try {
            if (expensesList.size() == 0) {
                centerText.setText(getResources().getString(R.string.top_5_expenses_no_expenses_this_month));
                centerText.setVisibility(View.VISIBLE);
                //showNoExpensesLayout(mainLayout, getResources().getString(R.string.top_5_expenses_no_expenses_this_month));
            }
            // if the expenses list isn't empty
            else {
                ArrayList<Transaction> limitedExpensesList = null;

                centerText.setText("");
                centerText.setVisibility(View.GONE);

                // sorting the list by value descending
                expensesList.sort((final Transaction transaction1, final Transaction transaction2) ->
                        Float.compare(Float.parseFloat(transaction2.getValue()),
                                Float.parseFloat(transaction1.getValue())));

                // creating a new list from the first 5 expenses only if there are more than than
                if (expensesList.size() > 5) {
                    limitedExpensesList = (ArrayList<Transaction>) expensesList.stream()
                            .limit(5).collect(Collectors.toList());
                }

                // iterating through the new list only if there are more than 5 expenses,
                // otherwise iterating through the old list
                for (final Transaction transaction :
                        (limitedExpensesList != null ? limitedExpensesList : expensesList)) {
                    final LinearLayout childLayout = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.top_five_expenses_linearlayout, mainLayout, false);

                    final TextView typeFromChildLayout =
                            childLayout.findViewById(R.id.topFiveExpensesConstraintLayoutType);

                    final TextView valueFromChildLayout =
                            childLayout.findViewById(R.id.topFiveExpensesConstraintLayoutValue);

                    final String valueWithCurrency = Locale.getDefault().getDisplayLanguage().equals("English") ?
                            currencySymbol + transaction.getValue() : transaction.getValue() + " " + currencySymbol;

                    final String translatedType = Types.getTranslatedType(requireContext(),
                            String.valueOf(Transaction.getTypeFromIndexInEnglish(transaction.getCategory())));

                    // displaying the views on the screen if the type was successfully translated
                    if (translatedType != null) {
                        typeFromChildLayout.setText(translatedType);
                        valueFromChildLayout.setText(valueWithCurrency);
                        typeFromChildLayout.setTextColor(Color.BLACK);
                        valueFromChildLayout.setTextColor(Color.BLACK);
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

    private void showNoExpensesLayout(final LinearLayout mainLayout, final String message) {
        final TextView noExpenses = new TextView(getContext());
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 20, 0, 20);

        noExpenses.setText(message);
        noExpenses.setTextColor(Color.BLACK);
        noExpenses.setTypeface(null, Typeface.BOLD);
        noExpenses.setTextSize(20);
        noExpenses.setGravity(Gravity.CENTER);
        noExpenses.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        noExpenses.setLayoutParams(params);

        mainLayout.addView(noExpenses);
    }
}