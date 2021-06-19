package com.example.EconomyManager.ApplicationPart;

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
import com.example.EconomyManager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FragmentMoneySpentPercentage extends Fragment {
    private FirebaseAuth fbAuth;
    private DatabaseReference myRef;
    private LinkedHashMap<String, Float> transactionTypesList;
    private Calendar currentTime;
    private AnyChartView pieChart;

    public FragmentMoneySpentPercentage() {
        // Required empty public constructor
    }

    public static FragmentMoneySpentPercentage newInstance() {
        FragmentMoneySpentPercentage fragment = new FragmentMoneySpentPercentage();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_money_spent_percentage, container, false);
        setVariables(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        populateMap();
        setPieChart();
    }

    private void setVariables(View v) {
        fbAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();
        transactionTypesList = new LinkedHashMap<>();
        currentTime = Calendar.getInstance();
        pieChart = v.findViewById(R.id.moneySpentPieChart);
    }

    private void populateMap() // metoda pentru calcularea procentajelor fiecarui tip de cheltuiala (ex: house 5%, food 35%, bills 50%)
    {
        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("PersonalTransactions"))
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
                    } else // daca nu exista nicio tranzactie in lista
                    {
                        //Toast.makeText(getContext(), "=0", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
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