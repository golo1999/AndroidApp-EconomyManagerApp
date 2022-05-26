package com.example.economy_manager.feature.barchart;

import android.os.Bundle;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
        displayData();
    }

    /**
     * Method for populating the filtered list and displaying the data based on it
     */
    private void displayData() {
        final String[] typesList = requireContext().getResources().getStringArray(R.array.barChartFragmentTypes);
        // if the SELECTED_TYPE is not contained into the typesList or there is no authenticated user
        if (Arrays.stream(typesList).noneMatch(SELECTED_TYPE::equals) ||
                MyCustomVariables.getFirebaseAuth().getUid() == null) {
            MyCustomMethods.showShortMessage(requireContext(), SELECTED_TYPE);
            showNoDataText();
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

    private void populateBarChart(final @NonNull LinkedHashMap<String, Float> map) {
        // iterating through the filtered list and updating the sum for each one of map's keys
        // e.g. updating the current sum for january's expenses if the selected param type is 'CURRENT_YEAR_EXPENSES'
        filteredTransactionsList.forEach((final Transaction filteredTransaction) -> {
            final String parsedName;

            switch (SELECTED_TYPE) {
                case "CURRENT_YEAR_ECONOMIES":
                case "CURRENT_YEAR_EXPENSES":
                case "CURRENT_YEAR_INCOMES":
                    parsedName = Months.getTranslatedMonth(requireContext(),
                            filteredTransaction.getTime().getMonthName().substring(0, 1)
                                    .concat(filteredTransaction.getTime().getMonthName().substring(1).toLowerCase()));
                    break;
                case "LAST_FIVE_YEARS_ECONOMIES":
                    final String transactionYear = String.valueOf(filteredTransaction.getTime().getYear());

                    parsedName = transactionYear.substring(transactionYear.length() - 1);
                    break;
                case "LAST_WEEK_EXPENSES":
                    parsedName = Days.getTranslatedDayName(requireContext(),
                            filteredTransaction.getTime().getDayName().substring(0, 1)
                                    .concat(filteredTransaction.getTime().getDayName().substring(1).toLowerCase()));
                    break;
                default:
                    parsedName = null;
                    break;
            }

            if (parsedName == null) {
                return;
            }

            final float currentSum = Float.parseFloat(String.valueOf(map.get(parsedName)));
            final float transactionValue =
                    Float.parseFloat(String.format(Locale.getDefault(), "%.0f",
                            Float.parseFloat(filteredTransaction.getValue())));
            final float newSum;

            switch (SELECTED_TYPE) {
                case "CURRENT_YEAR_ECONOMIES":
                case "LAST_FIVE_YEARS_ECONOMIES":
                    newSum = filteredTransaction.getType() == 1 ?
                            currentSum + transactionValue : currentSum - transactionValue;
                    break;
                default:
                    newSum = currentSum + transactionValue;
                    break;
            }

            map.put(parsedName, newSum);
        });

        // iterating the map (e.g. the week days map) and displaying a bar chart for each key (e.g. each week day)
        map.forEach((final String key, final Float value) -> {
            final String firstLetter = key.substring(0, 1);
            final int color;
            final float barValue = SELECTED_TYPE.equals("CURRENT_YEAR_ECONOMIES") && value < 0f ? 0f : value;

            switch (SELECTED_TYPE) {
                case "CURRENT_YEAR_ECONOMIES":
                case "CURRENT_YEAR_INCOMES":
                case "LAST_FIVE_YEARS_ECONOMIES":
                    color = requireContext().getColor(R.color.secondaryLight);
                    break;
                default:
                    color = requireContext().getColor(R.color.crimson);
                    break;
            }

            binding.barChart.addBar(new BarModel(firstLetter, barValue, color));
        });
    }

    private void populateFilteredTransactionsList(final @NonNull DataSnapshot snapshot) {
        final Iterable<DataSnapshot> personalTransactions = snapshot.child("personalTransactions").getChildren();
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

            final boolean isCurrentYearExpense = transaction.getType() == 0 &&
                    transaction.getTime().getYear() == currentTime.getYear();
            final boolean isCurrentYearIncome = transaction.getType() == 1 &&
                    transaction.getTime().getYear() == currentTime.getYear();
            final boolean isCurrentYearEconomy = isCurrentYearExpense || isCurrentYearIncome;
            final boolean isLastFiveYearsEconomy = transaction.getTime().getYear() >= currentTime.getYear() - 4;
            final boolean isLastWeekExpense = transaction.getType() == 0 &&
                    MyCustomMethods.transactionWasMadeInTheLastWeek(transaction.getTime());

            if ((SELECTED_TYPE.equals("CURRENT_YEAR_ECONOMIES") && !isCurrentYearEconomy) ||
                    (SELECTED_TYPE.equals("CURRENT_YEAR_EXPENSES") && !isCurrentYearExpense) ||
                    (SELECTED_TYPE.equals("CURRENT_YEAR_INCOMES") && !isCurrentYearIncome) ||
                    (SELECTED_TYPE.equals("LAST_FIVE_YEARS_ECONOMIES") && !isLastFiveYearsEconomy) ||
                    (SELECTED_TYPE.equals("LAST_WEEK_EXPENSES") && !isLastWeekExpense)) {
                return;
            }

            filteredTransactionsList.add(transaction);
        });
    }

    private void populateMap() {
        // displaying a message if there is not data found for the selected param type
        if (filteredTransactionsList.isEmpty()) {
            showNoDataText();
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
            switch (SELECTED_TYPE) {
                case "CURRENT_YEAR_ECONOMIES":
                case "CURRENT_YEAR_EXPENSES":
                case "CURRENT_YEAR_INCOMES":
                    final String[] monthNamesList = {requireContext().getString(R.string.january),
                            requireContext().getString(R.string.february), requireContext().getString(R.string.march),
                            requireContext().getString(R.string.april), requireContext().getString(R.string.may),
                            requireContext().getString(R.string.june), requireContext().getString(R.string.july),
                            requireContext().getString(R.string.august), requireContext().getString(R.string.september),
                            requireContext().getString(R.string.october), requireContext().getString(R.string.november),
                            requireContext().getString(R.string.december)};

                    for (final String monthName : monthNamesList) {
                        put(monthName, 0f);
                    }
                    break;
                case "LAST_FIVE_YEARS_ECONOMIES":
                    for (int counter = 4; counter >= 0; --counter) {
                        final String year = String.valueOf(LocalDate.now().getYear() - counter);

                        put(year.substring(year.length() - 1), 0f);
                    }
                    break;
                case "LAST_WEEK_EXPENSES":
                    final String[] weekDayNamesList = {requireContext().getString(R.string.monday),
                            requireContext().getString(R.string.tuesday), requireContext().getString(R.string.wednesday),
                            requireContext().getString(R.string.thursday), requireContext().getString(R.string.friday),
                            requireContext().getString(R.string.saturday), requireContext().getString(R.string.sunday)};

                    for (final String dayName : weekDayNamesList) {
                        put(dayName, 0f);
                    }
                    break;
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
     * Method for displaying a text if there is no transaction matching the selected type found
     */
    private void showNoDataText() {
        final String noDataText = SELECTED_TYPE.equals("CURRENT_YEAR_ECONOMIES") ?
                requireContext().getString(R.string.no_transactions_made_this_year) :
                SELECTED_TYPE.equals("CURRENT_YEAR_INCOMES") ?
                        requireContext().getString(R.string.no_incomes_made_this_year) :
                        SELECTED_TYPE.equals("CURRENT_YEAR_EXPENSES") ?
                                requireContext().getString(R.string.no_expenses_made_this_year) :
                                SELECTED_TYPE.equals("LAST_FIVE_YEARS_ECONOMIES") ?
                                        requireContext().getString(R.string.no_transactions_made_in_the_last_years, 5) :
                                        SELECTED_TYPE.equals("LAST_WEEK_EXPENSES") ?
                                                requireContext().getString(R.string.no_money_spent_last_week) :
                                                requireContext().getString(R.string.no_data);

        binding.noDataText.setText(noDataText);
        binding.barChart.setVisibility(View.GONE);
        binding.noDataText.setVisibility(View.VISIBLE);
    }
}