package com.example.economy_manager.feature.monthlyexpensespiechart;

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
import com.example.economy_manager.databinding.MonthlyExpensesPieChartFragmentBinding;
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

public class MonthlyExpensesPieChartFragment extends Fragment {

    private MonthlyExpensesPieChartFragmentBinding binding;
    private MainScreenViewModel viewModel;
    private UserDetails userDetails;
    private final LinkedHashMap<Integer, Float> transactionTypesMap = new LinkedHashMap<>();
    private MonthlyExpensesPieChartListener listener;

    public MonthlyExpensesPieChartFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static MonthlyExpensesPieChartFragment newInstance() {
        final MonthlyExpensesPieChartFragment fragment = new MonthlyExpensesPieChartFragment();
        final Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    public interface MonthlyExpensesPieChartListener {
        void onEmptyPieChart();

        void onNotEmptyPieChart();
    }

    @Override
    public void onAttach(final @NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (MonthlyExpensesPieChartListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getResources()
                    .getString(R.string.must_implement_the_listener, context, context.getResources()
                            .getString(R.string.monthly_expenses_pie_chart_listener)));
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
        setActivityVariables(inflater, container);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setUserDetails();
        populateMap();
    }

    private void addPieChartDetail(final int categoryIndex,
                                   final int categoryPercentage,
                                   final int categoryColor) {
        final LinearLayout categoryLayout =
                viewModel.getPieChartDetail(requireContext(), categoryIndex, categoryPercentage, categoryColor);

        binding.detailsLayout.addView(categoryLayout);
    }

    private void clearPieChart() {
        binding.detailsLayout.removeAllViews();
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
                        final AtomicReference<Float> currentMonthExpensesTotal = new AtomicReference<>(0f);

                        if (!transactionTypesMap.isEmpty()) {
                            transactionTypesMap.clear();
                        }

                        personalTransactions
                                .forEach((final DataSnapshot transactionsIterator) -> {
                                    final Transaction transaction = transactionsIterator.getValue(Transaction.class);

                                    // if the transaction is a current month & year expense
                                    if (transaction != null && transaction.getType() == 0 &&
                                            transaction.getTime().getYear() == currentYear &&
                                            transaction.getTime().getMonth() == currentMonthIndex) {
                                        final int transactionCategory = transaction.getCategory();

                                        currentMonthExpensesTotal.updateAndGet((final Float value) ->
                                                value + Float.parseFloat(transaction.getValue()));

                                        // updating current category's total value
                                        // if it already exists
                                        if (!transactionTypesMap.containsKey(transactionCategory)) {
                                            final float transactionValue =
                                                    Float.parseFloat(String.format(Locale.getDefault(), "%.0f",
                                                            Float.parseFloat(transaction.getValue())));

                                            transactionTypesMap.put(transactionCategory, transactionValue);
                                        }
                                        // if the current category doesn't exist yet
                                        else {
                                            final float currentSumOfCurrentCategory =
                                                    Float.parseFloat(String.valueOf(transactionTypesMap
                                                            .get(transactionCategory)));

                                            final float transactionValue =
                                                    Float.parseFloat(String.format(Locale.getDefault(), "%.0f",
                                                            Float.parseFloat(transaction.getValue())));

                                            transactionTypesMap.put(transactionCategory,
                                                    currentSumOfCurrentCategory + transactionValue);
                                        }
                                    }
                                });

                        if (transactionTypesMap.isEmpty()) {
                            listener.onEmptyPieChart();
                            binding.pieChart.setVisibility(View.GONE);
                            binding.noExpensesMadeText.setVisibility(View.VISIBLE);

                            if (binding.detailsLayout.getChildCount() > 0) {
                                clearPieChart();
                            }

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

    private void setActivityVariables(final @NonNull LayoutInflater inflater,
                                      final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.monthly_expenses_pie_chart_fragment, container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MainScreenViewModel.class);
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

    private void setUserDetails() {
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(requireContext());
    }
}