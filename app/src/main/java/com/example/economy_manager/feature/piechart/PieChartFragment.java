package com.example.economy_manager.feature.piechart;

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
import com.example.economy_manager.databinding.PieChartFragmentBinding;
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

public class PieChartFragment extends Fragment {

    private PieChartFragmentBinding binding;
    private MainScreenViewModel viewModel;
    private UserDetails userDetails;
    private final LinkedHashMap<Integer, Float> transactionTypesMap = new LinkedHashMap<>();
    private PieChartListener listener;

    private static final String TYPE = "TYPE";
    private String SELECTED_TYPE;

    public PieChartFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static PieChartFragment newInstance(String type) {
        final PieChartFragment fragment = new PieChartFragment();
        final Bundle args = new Bundle();

        args.putString(TYPE, type);
        fragment.setArguments(args);

        return fragment;
    }

    public interface PieChartListener {
        void onEmptyPieChart();

        void onNotEmptyPieChart();
    }

    @Override
    public void onAttach(final @NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (PieChartListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getResources().getString(R.string.must_implement_the_listener, context,
                    context.getResources().getString(R.string.pie_chart_listener)));
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            SELECTED_TYPE = getArguments().getString(TYPE);
        }
    }

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        setFragmentVariables(inflater, container);

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
     * Method for populating the map and displaying the data based on it
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
                        final AtomicReference<Float> currentMonthTotalExpenses = new AtomicReference<>(0f);
                        final AtomicReference<Float> currentMonthTotalIncomes = new AtomicReference<>(0f);

                        if (!transactionTypesMap.isEmpty()) {
                            transactionTypesMap.clear();
                        }

                        personalTransactions.forEach((final DataSnapshot transactionsIterator) -> {
                            final Transaction transaction = transactionsIterator.getValue(Transaction.class);

                            if (transaction == null) {
                                return;
                            }

                            final boolean isCurrentMonthExpense = transaction.getType() == 0 &&
                                    transaction.getTime().getYear() == LocalDate.now().getYear() &&
                                    transaction.getTime().getMonth() == LocalDate.now().getMonthValue();
                            final boolean isCurrentMonthIncome = transaction.getType() == 1 &&
                                    transaction.getTime().getYear() == LocalDate.now().getYear() &&
                                    transaction.getTime().getMonth() == LocalDate.now().getMonthValue();

                            if ((SELECTED_TYPE.equals("MONTHLY_EXPENSES") && !isCurrentMonthExpense) ||
                                    (SELECTED_TYPE.equals("MONTHLY_INCOMES") && !isCurrentMonthIncome)) {
                                return;
                            }

                            final int transactionCategory = transaction.getCategory();

                            if (SELECTED_TYPE.equals("MONTHLY_EXPENSES")) {
                                currentMonthTotalExpenses.updateAndGet((final Float value) ->
                                        value + Float.parseFloat(transaction.getValue()));
                            } else if (SELECTED_TYPE.equals("MONTHLY_INCOMES")) {
                                currentMonthTotalIncomes.updateAndGet((final Float value) ->
                                        value + Float.parseFloat(transaction.getValue()));
                            }

                            // updating current category's total value if it already exists
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
                        });

                        if (transactionTypesMap.isEmpty()) {
                            listener.onEmptyPieChart();
                            binding.noDataText.setText(requireActivity().getResources()
                                    .getString(SELECTED_TYPE.equals("MONTHLY_EXPENSES") ?
                                            R.string.no_expenses_made_this_month : R.string.no_incomes_made_this_month));
                            binding.pieChart.setVisibility(View.GONE);
                            binding.noDataText.setVisibility(View.VISIBLE);

                            if (binding.detailsLayout.getChildCount() > 0) {
                                clearPieChart();
                            }

                            return;
                        }

                        listener.onNotEmptyPieChart();
                        binding.noDataText.setVisibility(View.GONE);
                        binding.pieChart.setVisibility(View.VISIBLE);

                        MyCustomMethods.sortMapDescendingByValue(transactionTypesMap);
                        setPieChartData(transactionTypesMap, SELECTED_TYPE.equals("MONTHLY_EXPENSES") ?
                                currentMonthTotalExpenses.get() : currentMonthTotalIncomes.get());
                    }

                    @Override
                    public void onCancelled(final @NonNull DatabaseError error) {

                    }
                });
    }

    private void setFragmentVariables(final @NonNull LayoutInflater inflater,
                                      final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.pie_chart_fragment, container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MainScreenViewModel.class);
    }

    private void setPieChartData(final LinkedHashMap<Integer, Float> transactionTypesMap,
                                 final float total) {
        final AtomicInteger transactionTypesIndex = new AtomicInteger(0);

        binding.pieChart.clearChart();

        if (binding.detailsLayout.getChildCount() > 0) {
            binding.detailsLayout.removeAllViews();
        }

        // showing only the first five categories descending by value
        transactionTypesMap.forEach((final Integer key, final Float value) -> {
            if (transactionTypesIndex.get() >= 5) {
                return;
            }

            final int categoryPercentage = (int) (100 * (value / total));
            final int categoryColor = viewModel.getPieChartCategoryColor(requireContext(), key);
            final PieModel pieSlice = new PieModel("category" + key, categoryPercentage, categoryColor);

            binding.pieChart.addPieSlice(pieSlice);
            transactionTypesIndex.getAndIncrement();
            addPieChartDetail(key, categoryPercentage, categoryColor);
        });
    }

    private void setUserDetails() {
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(requireContext());
    }
}