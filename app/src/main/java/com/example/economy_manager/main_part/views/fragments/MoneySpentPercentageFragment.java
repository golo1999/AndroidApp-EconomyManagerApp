package com.example.economy_manager.main_part.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.economy_manager.R;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MoneySpentPercentageFragment extends Fragment {
    private UserDetails userDetails;
    private LinkedHashMap<Integer, Float> transactionTypesMap;
    private TextView noExpensesMadeThisMonthText;
    private PieChart pieChart;
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
            throw new ClassCastException(context.toString() + " must implement MoneySpentPercentageListener");
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.money_spent_percentage_fragment, container, false);

        setVariables(v);

        return v;
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

    private void setVariables(final View v) {
        transactionTypesMap = new LinkedHashMap<>();
        noExpensesMadeThisMonthText = v.findViewById(R.id.money_spent_percentage_no_expenses_text);
        pieChart = v.findViewById(R.id.money_spent_pie_chart);
    }

    // method for calculating each expense's percentage (i.e: house 5%, food 35%, bills 50%)
    private void populateMap() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() &&
                                    snapshot.hasChild("PersonalTransactions") &&
                                    snapshot.child("PersonalTransactions").hasChildren()) {
                                final Iterable<DataSnapshot> personalTransactions =
                                        snapshot.child("PersonalTransactions").getChildren();

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

                                if (!transactionTypesMap.isEmpty()) {
                                    listener.onNotEmptyPieChart();
                                    noExpensesMadeThisMonthText.setVisibility(View.GONE);
                                    pieChart.setVisibility(View.VISIBLE);

                                    sortMapDescendingByValue(transactionTypesMap);
                                    setPieChartData(transactionTypesMap, currentMonthExpensesTotal.get());
                                } else {
                                    listener.onEmptyPieChart();
                                    pieChart.setVisibility(View.GONE);
                                    noExpensesMadeThisMonthText.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setPieChartData(final LinkedHashMap<Integer, Float> transactionTypesMap,
                                 final float totalMonthlyExpenses) {
        pieChart.clearChart();

        transactionTypesMap.forEach((final Integer key, final Float value) -> {
            final int categoryPercentage = (int) (100 * (value / totalMonthlyExpenses));

            final int categoryColor = requireContext().getColor(key == 4 ?
                    R.color.expenses_bills : key == 5 ?
                    R.color.expenses_car : key == 6 ?
                    R.color.expenses_clothes : key == 7 ?
                    R.color.expenses_communications : key == 8 ?
                    R.color.expenses_eating_out : key == 9 ?
                    R.color.expenses_entertainment : key == 10 ?
                    R.color.expenses_food : key == 11 ?
                    R.color.expenses_gifts : key == 12 ?
                    R.color.expenses_health : key == 13 ?
                    R.color.expenses_house : key == 14 ?
                    R.color.expenses_pets : key == 15 ?
                    R.color.expenses_sports : key == 16 ?
                    R.color.expenses_taxi : key == 17 ?
                    R.color.expenses_toiletry : R.color.expenses_transport);

            final PieModel pieSlice = new PieModel("category" + key, categoryPercentage, categoryColor);

            pieChart.addPieSlice(pieSlice);
        });
    }

    private void sortMapDescendingByValue(final LinkedHashMap<Integer, Float> map) {
        final List<Map.Entry<Integer, Float>> sortedEntries = new ArrayList<>(map.entrySet());

        sortedEntries.sort((final Map.Entry<Integer, Float> entry1, final Map.Entry<Integer, Float> entry2) ->
                entry2.getValue().compareTo(entry1.getValue()));

        map.clear();

        sortedEntries.forEach((final Map.Entry<Integer, Float> entry) -> map.put(entry.getKey(), entry.getValue()));
    }
}