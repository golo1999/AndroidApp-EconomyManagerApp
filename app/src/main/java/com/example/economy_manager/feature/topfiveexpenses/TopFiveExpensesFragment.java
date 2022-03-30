package com.example.economy_manager.feature.topfiveexpenses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.TopFiveExpensesFragmentBinding;
import com.example.economy_manager.feature.mainscreen.MainScreenViewModel;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.utility.Languages;
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

public class TopFiveExpensesFragment extends Fragment {
    private TopFiveExpensesFragmentBinding binding;
    private MainScreenViewModel viewModel;

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
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        setVariables(inflater, container);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        createAndSetList();
    }

    private void setVariables(final @NonNull LayoutInflater inflater,
                              final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.top_five_expenses_fragment, container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MainScreenViewModel.class);
    }

    private void createAndSetList() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
                            final ArrayList<Transaction> expensesList = new ArrayList<>();
                            final String currencySymbol = viewModel.getUserDetails() != null ?
                                    viewModel.getUserDetails().getApplicationSettings().getCurrencySymbol() :
                                    MyCustomMethods.getCurrencySymbol();

                            if (snapshot.exists() &&
                                    snapshot.hasChild("personalTransactions") &&
                                    snapshot.child("personalTransactions").hasChildren()) {
                                for (DataSnapshot transactionIterator :
                                        snapshot.child("personalTransactions").getChildren()) {
                                    final Transaction transaction = transactionIterator.getValue(Transaction.class);

                                    if (transaction != null && transaction.getTime() != null &&
                                            transaction.getTime().getYear() == LocalDate.now().getYear() &&
                                            transaction.getTime().getMonth() == LocalDate.now().getMonthValue() &&
                                            transaction.getType() == 0) {
                                        expensesList.add(transaction);
                                    }
                                }
                            }

                            showTopExpenses(binding.mainLayout, expensesList, currencySymbol);
                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });
        } else {
            binding.centerText.setText(requireContext().getResources().getString(R.string.no_expenses_made_yet));
            binding.centerText.setVisibility(View.VISIBLE);
        }
    }

    private void showTopExpenses(final LinearLayout mainLayout,
                                 final ArrayList<Transaction> expensesList,
                                 final String currencySymbol) {
        mainLayout.removeAllViews();

        try {
            if (expensesList.size() == 0) {
                final String noExpensesThisMonthText =
                        requireContext().getResources().getString(R.string.no_expenses_made_this_month);

                binding.centerText.setText(noExpensesThisMonthText);
                binding.centerText.setVisibility(View.VISIBLE);
            }
            // if the expenses list isn't empty
            else {
                ArrayList<Transaction> limitedExpensesList = null;

                binding.centerText.setText("");
                binding.centerText.setVisibility(View.GONE);

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
                            childLayout.findViewById(R.id.typeText);

                    final TextView valueFromChildLayout =
                            childLayout.findViewById(R.id.valueText);

                    final String valueWithCurrency =
                            Locale.getDefault().getDisplayLanguage().equals(Languages.ENGLISH_LANGUAGE) ?
                                    currencySymbol + transaction.getValue() :
                                    transaction.getValue() + " " + currencySymbol;

                    final String translatedType = Types.getTranslatedType(requireContext(),
                            String.valueOf(Transaction.getTypeFromIndexInEnglish(transaction.getCategory())));

                    if (translatedType == null) {
                        return;
                    }

                    // displaying the views on the screen if the type was successfully translated
                    typeFromChildLayout.setText(translatedType);
                    valueFromChildLayout.setText(valueWithCurrency);
                    typeFromChildLayout.setTextColor(requireContext().getColor(R.color.quaternaryLight));
                    valueFromChildLayout.setTextColor(requireContext().getColor(R.color.quaternaryLight));
                    typeFromChildLayout.setTextSize(18);
                    valueFromChildLayout.setTextSize(18);

                    mainLayout.addView(childLayout);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}