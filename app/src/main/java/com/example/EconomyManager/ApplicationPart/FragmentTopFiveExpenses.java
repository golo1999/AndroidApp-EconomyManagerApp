package com.example.EconomyManager.ApplicationPart;

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

import com.example.EconomyManager.MoneyManager;
import com.example.EconomyManager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class FragmentTopFiveExpenses extends Fragment {
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private Calendar currentTime;
    private LinearLayout mainLayout;
    private Integer numberOfChildren = 0;

    public FragmentTopFiveExpenses() {
        // Required empty public constructor
    }

    public static FragmentTopFiveExpenses newInstance() {
        FragmentTopFiveExpenses fragment = new FragmentTopFiveExpenses();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_top_five_expenses, container,
                false);
        setVariables(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        createAndSetList();
    }

    private void setVariables(View v) {
        currentTime = Calendar.getInstance();
        mainLayout = v.findViewById(R.id.topFiveExpensesMainLayout);
    }

    private void createAndSetList() {
        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<MoneyManager> expensesList = new ArrayList<>();
                    SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL", Locale.ENGLISH);
                    String currency = String.valueOf(snapshot.child("ApplicationSettings").child("currencySymbol").getValue());
                    LinearLayout childLayout;

                    if (!expensesList.isEmpty())
                        expensesList.clear();
                    if (numberOfChildren > 0)
                        numberOfChildren = 0;

                    if (snapshot.hasChild("PersonalTransactions"))
                        if (snapshot.child("PersonalTransactions").hasChildren())
                            for (DataSnapshot yearIterator : snapshot.child("PersonalTransactions").getChildren())
                                if (String.valueOf(yearIterator.getKey()).equals(String.valueOf(currentTime.get(Calendar.YEAR)))) // daca a fost gasit anul curent
                                    for (DataSnapshot monthIterator : yearIterator.getChildren())
                                        if (String.valueOf(monthIterator.getKey()).equals(monthFormat.format(currentTime.getTime()))) // daca a fost gasita luna curenta
                                            if (monthIterator.hasChild("Expenses"))
                                                for (DataSnapshot expensesChild : monthIterator.child("Expenses").getChildren())
                                                    if (!String.valueOf(expensesChild.getKey()).equals("Overall"))
                                                        for (DataSnapshot expensesGrandChild : expensesChild.getChildren())
                                                            addTransactionToTheList(expensesGrandChild, expensesList);

                    mainLayout.removeAllViews();

                    try {
                        if (numberOfChildren == 0) {
                            TextView noTransactions = new TextView(getContext());
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 20, 0, 20);

                            noTransactions.setText(getResources().getString(R.string.top_5_expenses_no_expenses_this_month));
                            noTransactions.setTextColor(Color.BLACK);
                            noTransactions.setTypeface(null, Typeface.BOLD);
                            noTransactions.setTextSize(20);
                            noTransactions.setGravity(Gravity.CENTER);
                            noTransactions.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            noTransactions.setLayoutParams(params);
                            mainLayout.addView(noTransactions);
                        } else {
                            Collections.sort(expensesList, Collections.reverseOrder());

                            try {
                                if (expensesList.size() > 5)
                                    for (int x = 0; x < 5; x++) {
                                        childLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.linearlayout_top_five_expenses, mainLayout, false);
                                        TextView typeFromChildLayout = childLayout.findViewById(R.id.topFiveExpensesRelativeLayoutType), valueFromChildLayout = childLayout.findViewById(R.id.topFiveExpensesRelativeLayoutValue);
                                        String valueWithCurrency;
                                        float value = +expensesList.get(x).getValue();

                                        if (String.valueOf(value).contains("."))
                                            if (String.valueOf(value).length() - 1 - String.valueOf(value).indexOf(".") > 2)
                                                value = Float.parseFloat(String.format(Locale.getDefault(), "%.2f", value));

                                        valueWithCurrency = Locale.getDefault().getDisplayLanguage()
                                                .equals("English") ?
                                                currency + value : value + " " + currency;

                                        typeFromChildLayout.setText(getTranslatedExpenseType(expensesList.get(x).getType()));
                                        valueFromChildLayout.setText(valueWithCurrency);
                                        typeFromChildLayout.setTextColor(Color.BLACK);
                                        typeFromChildLayout.setTextSize(18);
                                        valueFromChildLayout.setTextSize(18);
                                        valueFromChildLayout.setTextColor(Color.BLACK);
                                        mainLayout.addView(childLayout);
                                    }
                                else for (int x = 0; x < expensesList.size(); x++) {
                                    childLayout = (LinearLayout) getLayoutInflater()
                                            .inflate(R.layout.linearlayout_top_five_expenses,
                                                    mainLayout, false);
                                    TextView typeFromChildLayout = childLayout
                                            .findViewById(R.id.topFiveExpensesRelativeLayoutType);
                                    TextView valueFromChildLayout = childLayout
                                            .findViewById(R.id.topFiveExpensesRelativeLayoutValue);
                                    String valueWithCurrency;
                                    float value = +expensesList.get(x).getValue();

                                    if (String.valueOf(value).contains("."))
                                        if (String.valueOf(value).length() - 1 - String.valueOf(value).indexOf(".") > 2)
                                            value = Float.parseFloat(String.format(Locale.getDefault(), "%.2f", value));

                                    valueWithCurrency = Locale.getDefault().getDisplayLanguage()
                                            .equals("English") ?
                                            currency + value : value + " " + currency;

                                    typeFromChildLayout.setText(getTranslatedExpenseType(expensesList.get(x).getType()));
                                    valueFromChildLayout.setText(valueWithCurrency);
                                    typeFromChildLayout.setTextColor(Color.BLACK);
                                    typeFromChildLayout.setTextSize(18);
                                    valueFromChildLayout.setTextSize(18);
                                    valueFromChildLayout.setTextColor(Color.BLACK);
                                    mainLayout.addView(childLayout);
                                }
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void addTransactionToTheList(DataSnapshot iterator, ArrayList<MoneyManager> list) {
        String note = String.valueOf(iterator.child("note").getValue());
        String date = String.valueOf(iterator.child("date").getValue());
        String type = String.valueOf(iterator.child("type").getValue());
        float value = Float.parseFloat(String.valueOf(iterator.child("value").getValue()));
        MoneyManager money = new MoneyManager(note, value, date, type);

        numberOfChildren++;
        list.add(money);
    }

    private String getTranslatedExpenseType(String transactionTypeInEnglish) {
        return transactionTypeInEnglish.equals("Bills") ?
                getResources().getString(R.string.subtract_money_bills) : transactionTypeInEnglish
                .equals("Car") ?
                getResources().getString(R.string.subtract_money_car) : transactionTypeInEnglish
                .equals("Clothes") ?
                getResources().getString(R.string.subtract_money_clothes) : transactionTypeInEnglish
                .equals("Communications") ?
                getResources().getString(R.string.subtract_money_communications) : transactionTypeInEnglish
                .equals("EatingOut") ?
                getResources().getString(R.string.subtract_money_eating_out) : transactionTypeInEnglish
                .equals("Entertainment") ?
                getResources().getString(R.string.subtract_money_entertainment) : transactionTypeInEnglish
                .equals("Food") ?
                getResources().getString(R.string.subtract_money_food) : transactionTypeInEnglish
                .equals("Gifts") ?
                getResources().getString(R.string.subtract_money_gifts) : transactionTypeInEnglish
                .equals("Health") ?
                getResources().getString(R.string.subtract_money_health) : transactionTypeInEnglish
                .equals("House") ?
                getResources().getString(R.string.subtract_money_house) : transactionTypeInEnglish
                .equals("Pets") ?
                getResources().getString(R.string.subtract_money_pets) : transactionTypeInEnglish
                .equals("Sports") ?
                getResources().getString(R.string.subtract_money_sports) : transactionTypeInEnglish
                .equals("Taxi") ?
                getResources().getString(R.string.subtract_money_taxi) : transactionTypeInEnglish
                .equals("Toiletry") ?
                getResources().getString(R.string.subtract_money_toiletry) : transactionTypeInEnglish
                .equals("Transport") ?
                getResources().getString(R.string.subtract_money_transport) : null;
    }
}