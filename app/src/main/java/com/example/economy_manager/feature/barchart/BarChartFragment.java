package com.example.economy_manager.feature.barchart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.BarChartFragmentBinding;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.utility.Days;
import com.example.economy_manager.utility.Months;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.models.BarModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

public class BarChartFragment extends Fragment {

    private BarChartFragmentBinding binding;
    private final ArrayList<Transaction> filteredTransactionsList = new ArrayList<>();

    private static final String TYPE = "TYPE";
    private String SELECTED_TYPE;

    public BarChartFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static BarChartFragment newInstance(String type) {
        BarChartFragment fragment = new BarChartFragment();
        Bundle args = new Bundle();

        args.putString(TYPE, type);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            SELECTED_TYPE = getArguments().getString(TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        setFragmentVariables(inflater, container);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        showData();
    }

    private void populateBarChart(@NonNull final LinkedHashMap<String, Float> map) {
        // iterating through the filtered list and updating the sum for each one of map's keys
        // e.g. updating the current sum for january's expenses if the selected param type is 'CURRENT_YEAR_EXPENSES'
        filteredTransactionsList.forEach((final Transaction filteredTransaction) -> {
            String parsedName = null;

            if (SELECTED_TYPE.equals("LAST_WEEK_EXPENSES")) {
                parsedName = Days.getTranslatedDayName(requireContext(),
                        filteredTransaction.getTime().getDayName().substring(0, 1)
                                .concat(filteredTransaction.getTime().getDayName().substring(1).toLowerCase()));
            } else if (SELECTED_TYPE.equals("CURRENT_YEAR_ECONOMIES") ||
                    SELECTED_TYPE.equals("CURRENT_YEAR_INCOMES") ||
                    SELECTED_TYPE.equals("CURRENT_YEAR_EXPENSES")) {
                parsedName = Months.getTranslatedMonth(requireContext(),
                        filteredTransaction.getTime().getMonthName().substring(0, 1)
                                .concat(filteredTransaction.getTime().getMonthName().substring(1).toLowerCase()));
            }

            if (parsedName == null) {
                return;
            }

            final float currentSum = Float.parseFloat(String.valueOf(map.get(parsedName)));
            final float transactionValue =
                    Float.parseFloat(String.format(Locale.getDefault(), "%.0f",
                            Float.parseFloat(filteredTransaction.getValue())));
            final float newSum;

            if (SELECTED_TYPE.equals("CURRENT_YEAR_ECONOMIES")) {
                newSum = filteredTransaction.getType() == 1 ?
                        currentSum + transactionValue : currentSum - transactionValue;
            } else {
                newSum = currentSum + transactionValue;
            }

            map.put(parsedName, newSum);
        });

        // iterating the map (e.g. the week days map) and displaying a bar chart for each key (e.g. each week day)
        map.forEach((final String key, final Float value) -> {
            final String firstLetter = key.substring(0, 1);
            final int color = requireContext().getColor(SELECTED_TYPE.equals("CURRENT_YEAR_ECONOMIES") ||
                    SELECTED_TYPE.equals("CURRENT_YEAR_INCOMES") ?
                    R.color.secondaryLight : R.color.crimson);
            final float barValue = SELECTED_TYPE.equals("CURRENT_YEAR_ECONOMIES") && value < 0f ? 0f : value;

            binding.barChart.addBar(new BarModel(firstLetter, barValue, color));
        });
    }

    private void populateFilteredTransactionsList(final @NonNull DataSnapshot snapshot) {
        final Iterable<DataSnapshot> personalTransactions =
                snapshot.child("personalTransactions").getChildren();
        final LocalDateTime currentTime = LocalDateTime.now();

        if (!filteredTransactionsList.isEmpty()) {
            filteredTransactionsList.clear();
        }

        // iterating the transactions list and filtering it based on the selected bar chart param type
        personalTransactions.forEach((final DataSnapshot transactionsIterator) -> {
            final Transaction transaction = transactionsIterator.getValue(Transaction.class);

            if (transaction == null) {
                return;
            }

            final boolean isLastWeekExpense = transaction.getType() == 0 &&
                    MyCustomMethods.transactionWasMadeInTheLastWeek(transaction.getTime());
            final boolean isCurrentYearIncome = transaction.getType() == 1 &&
                    transaction.getTime().getYear() == currentTime.getYear();
            final boolean isCurrentYearExpense = transaction.getType() == 0 &&
                    transaction.getTime().getYear() == currentTime.getYear();

            if ((SELECTED_TYPE.equals("LAST_WEEK_EXPENSES") && !isLastWeekExpense) ||
                    (SELECTED_TYPE.equals("CURRENT_YEAR_ECONOMIES") && !isCurrentYearIncome && !isCurrentYearExpense) ||
                    (SELECTED_TYPE.equals("CURRENT_YEAR_INCOMES") && !isCurrentYearIncome) ||
                    (SELECTED_TYPE.equals("CURRENT_YEAR_EXPENSES") && !isCurrentYearExpense)) {
                return;
            }

            filteredTransactionsList.add(transaction);
        });
    }

    private void populateMap() {
        // displaying a message if there is not data found for the selected param type
        if (filteredTransactionsList.isEmpty()) {
            String noDataText = null;

            switch (SELECTED_TYPE) {
                case "LAST_WEEK_EXPENSES":
                    noDataText = requireActivity().getResources().getString(R.string.no_money_spent_last_week);
                    break;
                case "CURRENT_YEAR_ECONOMIES":
                    noDataText = requireActivity().getResources().getString(R.string.no_transactions_made_this_year);
                    break;
                case "CURRENT_YEAR_INCOMES":
                    noDataText = requireActivity().getResources().getString(R.string.no_incomes_made_this_year);
                    break;
                case "CURRENT_YEAR_EXPENSES":
                    noDataText = requireActivity().getResources().getString(R.string.no_expenses_made_this_year);
                    break;
            }

            // not showing the 'no data' text if the param type is something else
            if (noDataText == null) {
                return;
            }

            binding.noDataText.setText(noDataText);
            binding.barChart.setVisibility(View.GONE);
            binding.noDataText.setVisibility(View.VISIBLE);

            return;
        }

        // if there is data found for the selected param type
        binding.noDataText.setVisibility(View.GONE);
        binding.barChart.setVisibility(View.VISIBLE);
        binding.barChart.clearChart();
        binding.barChart.setShowValues(false);

        // creating a map and pre-populating it based on the selected param type
        // e.g. initializing the week days or the year months
        final LinkedHashMap<String, Float> map = new LinkedHashMap<String, Float>() {{
            if (SELECTED_TYPE.equals("LAST_WEEK_EXPENSES")) {
                put(requireContext().getString(R.string.monday), 0f);
                put(requireContext().getString(R.string.tuesday), 0f);
                put(requireContext().getString(R.string.wednesday), 0f);
                put(requireContext().getString(R.string.thursday), 0f);
                put(requireContext().getString(R.string.friday), 0f);
                put(requireContext().getString(R.string.saturday), 0f);
                put(requireContext().getString(R.string.sunday), 0f);
            } else if (SELECTED_TYPE.equals("CURRENT_YEAR_ECONOMIES") ||
                    SELECTED_TYPE.equals("CURRENT_YEAR_INCOMES") ||
                    SELECTED_TYPE.equals("CURRENT_YEAR_EXPENSES")) {
                put(requireContext().getString(R.string.january), 0f);
                put(requireContext().getString(R.string.february), 0f);
                put(requireContext().getString(R.string.march), 0f);
                put(requireContext().getString(R.string.april), 0f);
                put(requireContext().getString(R.string.may), 0f);
                put(requireContext().getString(R.string.june), 0f);
                put(requireContext().getString(R.string.july), 0f);
                put(requireContext().getString(R.string.august), 0f);
                put(requireContext().getString(R.string.september), 0f);
                put(requireContext().getString(R.string.october), 0f);
                put(requireContext().getString(R.string.november), 0f);
                put(requireContext().getString(R.string.december), 0f);
            }
        }};

        populateBarChart(map);
        binding.barChart.startAnimation();
    }

    private void setFragmentVariables(final @NonNull LayoutInflater inflater,
                                      final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.bar_chart_fragment, container, false);
    }

    /**
     * Method for populating the filtered list and displaying the data based on it
     */
    private void showData() {
        if (MyCustomVariables.getFirebaseAuth().getUid() == null) {
            return;
        }

        MyCustomVariables.getDatabaseReference()
                .child(MyCustomVariables.getFirebaseAuth().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final @NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists() || !snapshot.hasChild("personalTransactions")) {
                            return;
                        }

                        populateFilteredTransactionsList(snapshot);
                        populateMap();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}