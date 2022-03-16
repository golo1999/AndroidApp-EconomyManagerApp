package com.example.economy_manager.feature.moneyspentpercentage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.MoneySpentPercentageFragmentBinding;
import com.example.economy_manager.feature.mainscreen.MainScreenViewModel;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.models.PieModel;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MoneySpentPercentageFragment extends Fragment {

    private MoneySpentPercentageFragmentBinding binding;
    private MainScreenViewModel viewModel;
    private UserDetails userDetails;
    private final LinkedHashMap<Integer, Float> transactionTypesMap = new LinkedHashMap<>();
    private MoneySpentPercentageListener listener;

    public MoneySpentPercentageFragment() {
        // Required empty public constructor
    }

    public static MoneySpentPercentageFragment newInstance() {
        final MoneySpentPercentageFragment fragment = new MoneySpentPercentageFragment();

        final Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    public interface MoneySpentPercentageListener {
        void onEmptyPieChart();

        void onNotEmptyPieChart();
    }

    @Override
    public void onAttach(final @NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (MoneySpentPercentageListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " " + "must implement MoneySpentPercentageListener");
        }
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
        setUserDetails();
        populateMap();
    }

    private void setUserDetails() {
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(requireContext());
    }

    private void setVariables(final @NonNull LayoutInflater inflater,
                              final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.money_spent_percentage_fragment, container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MainScreenViewModel.class);
    }

    /**
     * Method for calculating each expense's percentage from the total expenses
     */
    private void populateMap() {
        if (MyCustomVariables.getFirebaseAuth().getUid() == null) {
            return;
        }

        MyCustomVariables.getDatabaseReference()
                .child(MyCustomVariables.getFirebaseAuth().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final @NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists() ||
                                !snapshot.hasChild("personalTransactions") ||
                                !snapshot.child("personalTransactions").hasChildren()) {
                            return;
                        }

                        final Iterable<DataSnapshot> personalTransactions =
                                snapshot.child("personalTransactions").getChildren();

                        final int currentMonthIndex = LocalDate.now().getMonthValue();

                        final int currentYear = LocalDate.now().getYear();

                        final AtomicReference<Float> currentMonthExpensesTotal =
                                new AtomicReference<>(0f);

                        if (!transactionTypesMap.isEmpty()) {
                            transactionTypesMap.clear();
                        }

                        personalTransactions.forEach((final DataSnapshot personalTransactionIterator) -> {
                            final Transaction personalTransaction =
                                    personalTransactionIterator.getValue(Transaction.class);

                            // if the transaction is a current month & year expense
                            if (personalTransaction != null &&
                                    personalTransaction.getType() == 0 &&
                                    personalTransaction.getTime().getYear() == currentYear &&
                                    personalTransaction.getTime().getMonth() == currentMonthIndex) {
                                final int personalTransactionCategory = personalTransaction.getCategory();

                                currentMonthExpensesTotal.updateAndGet((final Float value) ->
                                        value + Float.parseFloat(personalTransaction.getValue()));

                                // updating current category's total value if it already exists
                                if (!transactionTypesMap.containsKey(personalTransactionCategory)) {
                                    final float personalTransactionValue =
                                            Float.parseFloat(String.format(Locale.getDefault(),
                                                    "%.0f",
                                                    Float.parseFloat(personalTransaction.getValue())));

                                    transactionTypesMap.put(personalTransactionCategory,
                                            personalTransactionValue);
                                }
                                // if the current category doesn't exist yet
                                else {
                                    final float personalTransactionCategoryCurrentSum =
                                            Float.parseFloat(String.valueOf(transactionTypesMap
                                                    .get(personalTransactionCategory)));

                                    final float personalTransactionValue =
                                            Float.parseFloat(String.format(Locale.getDefault(),
                                                    "%.0f",
                                                    Float.parseFloat(personalTransaction.getValue())));

                                    transactionTypesMap.put(personalTransactionCategory,
                                            personalTransactionCategoryCurrentSum + personalTransactionValue);
                                }
                            }
                        });

                        if (transactionTypesMap.isEmpty()) {
                            listener.onEmptyPieChart();
                            binding.pieChart.setVisibility(View.GONE);
                            binding.noExpensesMadeText.setVisibility(View.VISIBLE);

                            return;
                        }

                        listener.onNotEmptyPieChart();
                        binding.noExpensesMadeText.setVisibility(View.GONE);
                        binding.pieChart.setVisibility(View.VISIBLE);

                        MyCustomMethods.sortMapDescendingByValue(transactionTypesMap);
                        setPieChartData(transactionTypesMap, currentMonthExpensesTotal.get());
                    }

                    @Override
                    public void onCancelled(final @NonNull DatabaseError error) {

                    }
                });
    }

    private void setPieChartData(final LinkedHashMap<Integer, Float> transactionTypesMap,
                                 final float totalMonthlyExpenses) {
        final AtomicInteger transactionTypesIndex = new AtomicInteger(0);

        binding.pieChart.clearChart();

        if (binding.detailsLayout.getChildCount() > 0) {
            binding.detailsLayout.removeAllViews();
        }

        transactionTypesMap.forEach((final Integer key, final Float value) -> {
            final int categoryPercentage = (int) (100 * (value / totalMonthlyExpenses));

            final int categoryColor = viewModel.getPieChartCategoryColor(requireContext(), key);

            final PieModel pieSlice = new PieModel("category" + key, categoryPercentage, categoryColor);

            binding.pieChart.addPieSlice(pieSlice);

            transactionTypesIndex.getAndIncrement();

            if (transactionTypesIndex.get() <= 5) {
                addPieChartDetail(key, categoryPercentage, categoryColor);
            }
        });
    }

    private void addPieChartDetail(final int categoryIndex,
                                   final int categoryPercentage,
                                   final int categoryColor) {
        final LinearLayout categoryLayout =
                viewModel.getPieChartDetail(requireContext(), categoryIndex, categoryPercentage, categoryColor);

        binding.detailsLayout.addView(categoryLayout);
    }
}