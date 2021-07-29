package com.example.economy_manager.main_part.views.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.example.economy_manager.R;
import com.example.economy_manager.models.UserDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MoneySpentPercentageFragment extends Fragment {
    private UserDetails userDetails;
    private LinkedHashMap<String, Float> transactionTypesList;
    private Calendar currentTime;
    private AnyChartView pieChart;

    public MoneySpentPercentageFragment() {
        // Required empty public constructor
    }

    public static MoneySpentPercentageFragment newInstance() {
        final MoneySpentPercentageFragment fragment = new MoneySpentPercentageFragment();
        final Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.money_spent_percentage_fragment, container, false);

        setVariables(v);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUserDetails();
        populateMap();
        setPieChart();
    }

    private void setUserDetails() {
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(requireContext());
    }

    private void setVariables(final View v) {
        transactionTypesList = new LinkedHashMap<>();
        currentTime = Calendar.getInstance();
        pieChart = v.findViewById(R.id.moneySpentPieChart);
    }

    // metoda pentru calcularea procentajelor fiecarui tip de cheltuiala (ex: house 5%, food 35%, bills 50%)
    private void populateMap() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
//                            final String currencySymbol = userDetails != null ?
//                                    userDetails.getApplicationSettings().getCurrencySymbol() :
//                                    MyCustomMethods.getCurrencySymbol();
//                            float totalMonthlyIncomes = 0f;
//                            float totalMonthlyExpenses = 0f;
//
//                            if (snapshot.exists() && snapshot.hasChild("PersonalTransactions") &&
//                                    snapshot.child("PersonalTransactions").hasChildren()) {
//                                for (final DataSnapshot databaseTransaction :
//                                        snapshot.child("PersonalTransactions").getChildren()) {
//                                    final Transaction transaction = databaseTransaction.getValue(Transaction.class);
//
//                                    if (transaction != null) {
//                                        if (transaction.getCategory() > 0 && transaction.getCategory() < 4) {
//                                            totalMonthlyIncomes += Float.parseFloat(transaction.getValue());
//                                        } else {
//                                            totalMonthlyExpenses += Float.parseFloat(transaction.getValue());
//                                        }
//                                    }
//                                }
//                            }

                            //

                            if (snapshot.hasChild("PersonalTransactions")) {
                                if (snapshot.child("PersonalTransactions").hasChildren()) {
                                    SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL", Locale.ENGLISH);
                                    float transactionTypeSum, overall;

                                    if (transactionTypesList.size() > 0)
                                        transactionTypesList.clear();

                                    for (DataSnapshot yearIterator : snapshot.child("PersonalTransactions").getChildren())
                                        if (String.valueOf(yearIterator.getKey()).equals(String.valueOf(currentTime.get(Calendar.YEAR)))) // daca a fost gasit anul curent
                                            for (DataSnapshot monthIterator : yearIterator.getChildren())
                                                if (String.valueOf(monthIterator.getKey()).equals(monthFormat.format(currentTime.getTime()))) // daca a fost gasita luna curenta
                                                    for (DataSnapshot monthChild : monthIterator.getChildren())
                                                        if (String.valueOf(monthChild.getKey()).equals("Expenses")) // daca este cheltuiala
                                                            if (monthChild.hasChild("Overall")) {
                                                                overall = Float.parseFloat(String.valueOf(monthChild.child("Overall").getValue()));

                                                                for (DataSnapshot monthGrandChild : monthChild.getChildren())
                                                                    if (!String.valueOf(monthGrandChild.getKey()).equals("Overall")) // daca fiul nu este 'Overall'
                                                                    {
                                                                        transactionTypeSum = 0f;
                                                                        for (DataSnapshot monthGreatGrandChild : monthGrandChild.getChildren())
                                                                            transactionTypeSum += Float.parseFloat(String.valueOf(monthGreatGrandChild.child("value").getValue())); // adunam valoarea tranzactiilor de un tip
                                                                        transactionTypesList.put(String.valueOf(monthGrandChild.getKey()), Float.parseFloat(String.format(Locale.getDefault(), "%.0f", 100f * transactionTypeSum / overall))); // punem in linkedhashmap (ex: house, 10) => 10 = 10%
                                                                    }
                                                            }
                                }
                            }

                            if (transactionTypesList.size() > 0) {
                                //Toast.makeText(getContext(), ">0", Toast.LENGTH_SHORT).show();
                                float sum = 0f;
                                for (Map.Entry<String, Float> transactionType : transactionTypesList.entrySet())
                                    sum += transactionType.getValue();
                                transactionTypesList.entrySet().stream().sorted(Map.Entry.<String, Float>comparingByValue().reversed()).forEach(entry -> Toast.makeText(getContext(), entry.getKey() + " " + entry.getValue(), Toast.LENGTH_SHORT).show()); // aici apare exceptia java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String android.content.Context.getPackageName()' on a null object reference
                                //transactionTypesList.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> Toast.makeText(getContext(), entry.getKey()+" "+entry.getValue(), Toast.LENGTH_SHORT).show());
                                //Toast.makeText(getContext(), "total: "+sum, Toast.LENGTH_SHORT).show();
                                Pie pie = AnyChart.pie();
                                List<DataEntry> dataEntryList = new ArrayList<>();

                                for (Map.Entry<String, Float> iterator : transactionTypesList.entrySet()) {
                                    dataEntryList.add(new ValueDataEntry(iterator.getKey(), iterator.getValue()));
                                }
                                pie.data(dataEntryList);
                                pieChart.setChart(pie);
                            }
                            // daca nu exista nicio tranzactie in lista
                            else {
                                //Toast.makeText(getContext(), "=0", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setPieChart() {


//        if(transactionTypesList.size()>0) // daca avem tranzactii in lista
//        {
//            for(Map.Entry<String, Float> transactionType: transactionTypesList.entrySet())
//            {
//                //Toast.makeText(getContext(), transactionType.getKey()+": "+transactionType.getValue()+"%", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else // daca nu avem nicio tranzactie in lista
//        {
//
//        }
    }
}