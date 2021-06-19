package com.example.EconomyManager.ApplicationPart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.EconomyManager.Months;
import com.example.EconomyManager.R;
import com.example.EconomyManager.Transaction;
import com.example.EconomyManager.Types;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class ActivityMonthlyBalance extends AppCompatActivity {
    private ImageView goBack;
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private TextView activityTitle;
    private TextView centerText;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private SimpleDateFormat currentMonth;
    private Calendar currentTime;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityTheme();
        setContentView(R.layout.activity_monthly_balance);
        setVariables();
        setOnClickListeners();
        setTitle();
        setCenterText();
        setIncomesAndExpensesInParent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setVariables() {
        goBack = findViewById(R.id.monthlyBalanceBack);
        activityTitle = findViewById(R.id.monthlyBalanceTitle);
        currentMonth = new SimpleDateFormat("LLLL", Locale.ENGLISH);
        centerText = findViewById(R.id.monthlyBalanceCenterText);
        currentTime = Calendar.getInstance();
        mainLayout = findViewById(R.id.monthlyBalanceMainLayout);
    }

    private void setOnClickListeners() {
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void setActivityTheme() {
        if (fbAuth.getUid() != null) {
            myRef.child(fbAuth.getUid())
                    .child("ApplicationSettings")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                                if (snapshot.hasChild("darkTheme")) {
                                    boolean checked = Boolean.parseBoolean(String.
                                            valueOf(snapshot.child("darkTheme").getValue()));
                                    int theme = !checked ?
                                            R.drawable.ic_white_gradient_tobacco_ad :
                                            R.drawable.ic_black_gradient_night_shift;

                                    getWindow().setBackgroundDrawableResource(theme);
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void setIncomesAndExpensesInParent() {
        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String currency = "£";
                    String darkTheme = null;
                    ArrayList<Transaction> transactionsList = new ArrayList<>();
                    ArrayList<Integer> daysList = new ArrayList<>();

                    if (snapshot.exists()) {
                        if (snapshot.hasChild("ApplicationSettings")) {
                            if (snapshot.child("ApplicationSettings").hasChild("currency"))
                                currency = String.valueOf(snapshot.child("ApplicationSettings")
                                        .child("currencySymbol").getValue());
                            if (snapshot.child("ApplicationSettings").hasChild("darkTheme"))
                                darkTheme = String.valueOf(snapshot.child("ApplicationSettings")
                                        .child("darkTheme").getValue());
                            if (snapshot.hasChild("PersonalTransactions")) {
                                if (!transactionsList.isEmpty())
                                    transactionsList.clear();
                                if (!daysList.isEmpty())
                                    daysList.clear();

                                if (snapshot.child("PersonalTransactions").hasChildren())
                                    for (DataSnapshot transaction :
                                            snapshot.child("PersonalTransactions").getChildren())
                                        if (transaction.hasChild("time") &&
                                                transaction.child("time").hasChild("month") &&
                                                Integer.parseInt(String.valueOf(transaction
                                                        .child("time").child("month")
                                                        .getValue())) ==
                                                        LocalDate.now().getMonthValue()) {
                                            Transaction userTransaction = transaction
                                                    .getValue(Transaction.class);

                                            if (userTransaction != null) {
                                                transactionsList.add(userTransaction);

                                                if (checkIfDayCanBeAddedToList(daysList,
                                                        userTransaction.getTime().getDay())) {
                                                    daysList.add(userTransaction.getTime().getDay());
                                                }
                                            }
                                        }
                            }

                            // sortarea listei dupa valoare descendent
                            Collections.sort(transactionsList, (o1, o2) ->
                                    Float.compare(Float.parseFloat(String.valueOf(o2.getValue())),
                                            Float.parseFloat(String.valueOf(o1.getValue()))));
                            Collections.sort(daysList, Collections.reverseOrder());

                            // pentru fiecare data descendenta afisam toate datele ascendent
                            for (int dayFromDaysList : daysList) {
                                LocalDate currentDate = LocalDate.now();
                                // calculam suma pentru fiecare zi
                                float dateTotalIncome = 0f;
                                float dateTotalExpense = 0f;
                                String textForTotalSum;
                                String translatedMonth = Months
                                        .getTranslatedMonth(ActivityMonthlyBalance.this,
                                                String.valueOf(currentDate.getMonth())
                                                        .substring(0, 1) +
                                                        String.valueOf(currentDate.getMonth())
                                                                .substring(1).toLowerCase());
                                String dateTranslated;
                                String dayPrefix;
                                LinearLayout dayAndSumLayout;
                                LinearLayout transactionLayout;
                                TextView typeText;
                                TextView valueText;
                                TextView dayText;
                                TextView totalSumText;

                                switch (Locale.getDefault().getDisplayLanguage()) {
                                    case "Deutsch":
                                        dayPrefix = "der";
                                        dateTranslated = dayPrefix + " " + dayFromDaysList + " " +
                                                translatedMonth.trim();
                                        break;
                                    case "español":
                                    case "português":
                                        dayPrefix = "de";
                                        dateTranslated = dayFromDaysList + " " + dayPrefix + " " +
                                                translatedMonth.trim().toLowerCase();
                                        break;
                                    case "français":
                                    case "română":
                                        dateTranslated = dayFromDaysList + " " +
                                                translatedMonth.trim().toLowerCase();
                                        break;
                                    case "italiano":
                                        dayPrefix = "il";
                                        dateTranslated = dayPrefix + " " + dayFromDaysList + " " +
                                                translatedMonth.trim().toLowerCase();
                                        break;
                                    default:
                                        dayPrefix = (dayFromDaysList % 10 == 1 ||
                                                dayFromDaysList % 10 == 2 ||
                                                dayFromDaysList % 10 == 3) ?
                                                (dayFromDaysList % 10 == 1 ?
                                                        "st" :
                                                        (dayFromDaysList % 10 == 2) ?
                                                                "nd" : "rd") :
                                                "th";

                                        dateTranslated = translatedMonth.trim() + " " +
                                                dayFromDaysList + dayPrefix;
                                        break;
                                }

                                dayAndSumLayout = (LinearLayout) View
                                        .inflate(ActivityMonthlyBalance.this,
                                                R.layout.linearlayout_monthly_balance_title,
                                                null);
                                dayText = dayAndSumLayout
                                        .findViewById(R.id.monthlyBalanceRelativeLayoutDay);
                                totalSumText = dayAndSumLayout
                                        .findViewById(R.id.monthlyBalanceRelativeLayoutDayTotalSum);

                                mainLayout.addView(dayAndSumLayout);
                                dayText.setText(dateTranslated);
                                dayText.setTextSize(25);

                                if (darkTheme != null)
                                    dayText.setTextColor(darkTheme.equals("true") ?
                                            Color.YELLOW : Color.BLUE);

                                for (Transaction transactionsListIterator : transactionsList) {
                                    if (dayFromDaysList == transactionsListIterator
                                            .getTime().getDay()) {
                                        if (transactionsListIterator.getCategory() == 0 ||
                                                transactionsListIterator.getCategory() == 1 ||
                                                transactionsListIterator.getCategory() == 2 ||
                                                transactionsListIterator.getCategory() == 3) {
                                            dateTotalIncome += Float
                                                    .parseFloat(transactionsListIterator.getValue());
                                        } else {
                                            dateTotalExpense += Float
                                                    .parseFloat(transactionsListIterator.getValue());
                                        }

                                        transactionLayout = (LinearLayout) View
                                                .inflate(ActivityMonthlyBalance.this,
                                                        R.layout.linearlayout_monthly_balance,
                                                        null);
                                        typeText = transactionLayout
                                                .findViewById(R.id.monthlyBalanceRelativeLayoutType);
                                        valueText = transactionLayout
                                                .findViewById(R.id.monthlyBalanceRelativeLayoutValue);

                                        typeText.setText(Types
                                                .getTranslatedType(ActivityMonthlyBalance
                                                        .this, String.valueOf(Transaction
                                                        .getTypeFromIndexInEnglish(transactionsListIterator
                                                                .getCategory()))));
                                        if (darkTheme != null) {
                                            typeText.setTextColor(darkTheme.equals("true") ?
                                                    Color.WHITE : Color.BLACK);
                                            valueText.setTextColor(darkTheme.equals("true") ?
                                                    Color.WHITE : Color.BLACK);
                                        }
                                        typeText.setTextSize(19);

                                        String text = Locale.getDefault().getDisplayLanguage()
                                                .equals("English") ?
                                                currency + transactionsListIterator.getValue() :
                                                transactionsListIterator.getValue() + " " + currency;

                                        text = (transactionsListIterator.getCategory() == 0 ||
                                                transactionsListIterator.getCategory() == 1 ||
                                                transactionsListIterator.getCategory() == 2 ||
                                                transactionsListIterator.getCategory() == 3) ?
                                                "+" + text :
                                                "-" + text;

                                        valueText.setText(text);
                                        valueText.setTextSize(19);

                                        mainLayout.addView(transactionLayout);
                                    }
                                }

                                textForTotalSum = Locale.getDefault().getDisplayLanguage()
                                        .equals("English") ?
                                        currency + Math.abs(dateTotalIncome - dateTotalExpense) :
                                        Math.abs(dateTotalIncome - dateTotalExpense) + " " + currency;

                                totalSumText.setText(textForTotalSum);
                                totalSumText.setTextSize(25);

                                totalSumText.setTextColor(dateTotalIncome - dateTotalExpense < 0f ?
                                        Color.RED : Color.GREEN);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private void setTitle() {
        String year = String.valueOf(currentTime.get(Calendar.YEAR));
        String month = Months.getTranslatedMonth(ActivityMonthlyBalance.this,
                currentMonth.format(currentTime.getTime()));
        String currentMonthYear = month.trim() + " " + year;

        activityTitle.setText(currentMonthYear);
        activityTitle.setTextSize(20);
        activityTitle.setTextColor(Color.WHITE);
    }

    private void setCenterText() {
        final String year = String.valueOf(currentTime.get(Calendar.YEAR));
        final String month = currentMonth.format(currentTime.getTime());

        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).addValueEventListener(new ValueEventListener() {
                boolean darkThemeEnabled;

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                        if (snapshot.hasChild("ApplicationSettings")) {
                            if (snapshot.child("ApplicationSettings").hasChild("darkTheme"))
                                darkThemeEnabled = Boolean.parseBoolean(String
                                        .valueOf(snapshot.child("ApplicationSettings")
                                                .child("darkTheme").getValue()));
                            if (snapshot.hasChild("PersonalTransactions")) {
                                if (snapshot.child("PersonalTransactions").hasChild(year))
                                    centerText.setText(snapshot.child("PersonalTransactions")
                                            .child(year).hasChild(month) ?
                                            "" : getResources()
                                            .getString(R.string.no_transactions_this_month));
                            } else centerText.setText(R.string.no_transactions_yet);

                            centerText.setTextColor(!darkThemeEnabled ?
                                    Color.parseColor("#195190") :
                                    Color.WHITE);
                            centerText.setTextSize(20);
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    private boolean checkIfDayCanBeAddedToList(ArrayList<Integer> daysList, int dayToBeAdded) {
        if (!daysList.isEmpty())
            for (int dayFromList : daysList)
                if (dayFromList == dayToBeAdded)
                    return false;

        return true;
    }
}

/*

// metoda initiala

private void setIncomesAndExpensesInParent() {
        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL", Locale.ENGLISH);
                    String currency = "£";
                    String darkTheme = null;
                    ArrayList<MoneyManager> transactionsList = new ArrayList<>();
                    ArrayList<String> datesList = new ArrayList<>();

                    if (snapshot.exists()) {
                        if (snapshot.hasChild("ApplicationSettings")) {
                            if (snapshot.child("ApplicationSettings").hasChild("currency"))
                                currency = String.valueOf(snapshot.child("ApplicationSettings").child("currencySymbol").getValue());
                            if (snapshot.child("ApplicationSettings").hasChild("darkTheme"))
                                darkTheme = String.valueOf(snapshot.child("ApplicationSettings").child("darkTheme").getValue());
                            if (snapshot.hasChild("PersonalTransactions")) {
                                if (!transactionsList.isEmpty())
                                    transactionsList.clear();
                                if (!datesList.isEmpty())
                                    datesList.clear();

                                if (snapshot.child("PersonalTransactions").hasChildren())
                                    for (DataSnapshot yearIterator : snapshot.child("PersonalTransactions").getChildren())
                                        if (String.valueOf(yearIterator.getKey()).equals(String.valueOf(currentTime.get(Calendar.YEAR)))) // daca am gasit anul curent
                                            for (DataSnapshot monthIterator : yearIterator.getChildren())
                                                if (String.valueOf(monthIterator.getKey()).equals(monthFormat.format(currentTime.getTime()))) // daca am gasit luna curenta
                                                    for (DataSnapshot monthChild : monthIterator.getChildren())
                                                        for (DataSnapshot monthGrandChild : monthChild.getChildren())
                                                            if (!String.valueOf(monthGrandChild.getKey()).equals("Overall")) // daca nu este 'Overall'
                                                                for (DataSnapshot monthGreatGrandChild : monthGrandChild.getChildren())
                                                                    checkIfTheTransactionCanBeAddedToTheDatesList(monthGreatGrandChild, transactionsList, datesList);
                            }
                            Collections.sort(transactionsList, (o1, o2) -> Float.compare(o2.getValue(), o1.getValue())); // sortarea listei dupa valoare descendent
                            Collections.sort(datesList, Collections.reverseOrder());

                            for (String datesListIterator : datesList) // pentru fiecare data descendenta afisam toate datele ascendent
                            {
                                String[] dateFromTransactionListSplit, dateParsed = datesListIterator.split(" ");
                                float dateTotalIncome = 0f, dateTotalExpense = 0f; // calculam suma pentru fiecare zi
                                String textForTotalSum, monthParsed = Months.getTranslatedMonth(ActivityMonthlyBalance.this, dateParsed[0]), dateTranslated, dayPrefix;
                                LinearLayout dayAndSumLayout, transactionLayout;
                                TextView typeText, valueText, dayText, totalSumText;

//                                switch(dateParsed[0])
//                                {
//                                    case "January":
//                                        monthParsed=getResources().getString(R.string.month_january).trim();
//                                        break;
//                                    case "February":
//                                        monthParsed=getResources().getString(R.string.month_february).trim();
//                                        break;
//                                    case "March":
//                                        monthParsed=getResources().getString(R.string.month_march).trim();
//                                        break;
//                                    case "April":
//                                        monthParsed=getResources().getString(R.string.month_april).trim();
//                                        break;
//                                    case "May":
//                                        monthParsed=getResources().getString(R.string.month_may).trim();
//                                        break;
//                                    case "June":
//                                        monthParsed=getResources().getString(R.string.month_june).trim();
//                                        break;
//                                    case "July":
//                                        monthParsed=getResources().getString(R.string.month_july).trim();
//                                        break;
//                                    case "August":
//                                        monthParsed=getResources().getString(R.string.month_august).trim();
//                                        break;
//                                    case "September":
//                                        monthParsed=getResources().getString(R.string.month_september).trim();
//                                        break;
//                                    case "October":
//                                        monthParsed=getResources().getString(R.string.month_october).trim();
//                                        break;
//                                    case "November":
//                                        monthParsed=getResources().getString(R.string.month_november).trim();
//                                        break;
//                                    default:
//                                        monthParsed=getResources().getString(R.string.month_december).trim();
//                                        break;
//                                }

                                switch (Locale.getDefault().getDisplayLanguage()) {
                                    case "Deutsch":
                                        dayPrefix = "der";
                                        dateTranslated = dayPrefix + " " + Integer.parseInt(dateParsed[1]) + " " + monthParsed.trim();
                                        break;
                                    case "español":
                                    case "português":
                                        dayPrefix = "de";
                                        dateTranslated = Integer.parseInt(dateParsed[1]) + " " + dayPrefix + " " + monthParsed.trim().toLowerCase();
                                        break;
                                    case "français":
                                    case "română":
                                        dateTranslated = Integer.parseInt(dateParsed[1]) + " " + monthParsed.trim().toLowerCase();
                                        break;
                                    case "italiano":
                                        dayPrefix = "il";
                                        dateTranslated = dayPrefix + " " + Integer.parseInt(dateParsed[1]) + " " + monthParsed.trim().toLowerCase();
                                        break;
                                    default:
                                        dayPrefix = "th";
                                        if (Integer.parseInt(dateParsed[1]) % 10 == 1)
                                            dayPrefix = "st";
                                        else if (Integer.parseInt(dateParsed[1]) % 10 == 2)
                                            dayPrefix = "nd";
                                        else if (Integer.parseInt(dateParsed[1]) % 10 == 3)
                                            dayPrefix = "rd";
                                        dateTranslated = monthParsed.trim() + " " + Integer.parseInt(dateParsed[1]) + dayPrefix;
                                        break;
                                }

                                dayAndSumLayout = (LinearLayout) View.inflate(ActivityMonthlyBalance.this, R.layout.linearlayout_monthly_balance_title, null);
                                dayText = dayAndSumLayout.findViewById(R.id.monthlyBalanceRelativeLayoutDay);
                                totalSumText = dayAndSumLayout.findViewById(R.id.monthlyBalanceRelativeLayoutDayTotalSum);

                                mainLayout.addView(dayAndSumLayout);
                                dayText.setText(dateTranslated);
                                dayText.setTextSize(25);
                                if (darkTheme != null)
                                    if (darkTheme.equals("true"))
                                        dayText.setTextColor(Color.YELLOW);
                                    else dayText.setTextColor(Color.BLUE);

                                for (MoneyManager transactionsListIterator : transactionsList) {
                                    dateFromTransactionListSplit = transactionsListIterator.getDate().split("\\W");
                                    if (datesListIterator.equals(dateFromTransactionListSplit[0] + " " + dateFromTransactionListSplit[1])) {
                                        if (transactionsListIterator.getType().equals("Deposits") || transactionsListIterator.getType().equals("IndependentSources") || transactionsListIterator.getType().equals("Salary") || transactionsListIterator.getType().equals("Saving"))
                                            dateTotalIncome += transactionsListIterator.getValue();
                                        else
                                            dateTotalExpense += transactionsListIterator.getValue();

                                        transactionLayout = (LinearLayout) View.inflate(ActivityMonthlyBalance.this, R.layout.linearlayout_monthly_balance, null);
                                        typeText = transactionLayout.findViewById(R.id.monthlyBalanceRelativeLayoutType);
                                        valueText = transactionLayout.findViewById(R.id.monthlyBalanceRelativeLayoutValue);

                                        typeText.setText(Types.getTranslatedType(ActivityMonthlyBalance.this, transactionsListIterator.getType()));
                                        if (darkTheme != null)
                                            if (darkTheme.equals("true")) {
                                                typeText.setTextColor(Color.WHITE);
                                                valueText.setTextColor(Color.WHITE);
                                            } else {
                                                typeText.setTextColor(Color.BLACK);
                                                valueText.setTextColor(Color.BLACK);
                                            }
                                        typeText.setTextSize(19);

                                        String text;
                                        if (Locale.getDefault().getDisplayLanguage().equals("English"))
                                            text = currency + transactionsListIterator.getValue();
                                        else
                                            text = transactionsListIterator.getValue() + " " + currency;

                                        if (transactionsListIterator.getType().equals("Deposits") || transactionsListIterator.getType().equals("IndependentSources") || transactionsListIterator.getType().equals("Salary") || transactionsListIterator.getType().equals("Saving"))
                                            text = "+" + text;
                                        else text = "-" + text;

                                        valueText.setText(text);
                                        valueText.setTextSize(19);

                                        mainLayout.addView(transactionLayout);
                                    }
                                }

                                if (Locale.getDefault().getDisplayLanguage().equals("English"))
                                    textForTotalSum = currency + Math.abs(dateTotalIncome - dateTotalExpense);
                                else
                                    textForTotalSum = Math.abs(dateTotalIncome - dateTotalExpense) + " " + currency;
                                totalSumText.setText(textForTotalSum);
                                totalSumText.setTextSize(25);
                                if (dateTotalIncome - dateTotalExpense < 0f)
                                    totalSumText.setTextColor(Color.RED);
                                else totalSumText.setTextColor(Color.GREEN);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

// metoda cu cateva ternary operator

private void setIncomesAndExpensesInParent() {
        if (fbAuth.getUid() != null)
            myRef.child(fbAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL", Locale.ENGLISH);
                    String currency = "£";
                    String darkTheme = null;
                    ArrayList<MoneyManager> transactionsList = new ArrayList<>();
                    ArrayList<String> datesList = new ArrayList<>();

                    if (snapshot.exists()) {
                        if (snapshot.hasChild("ApplicationSettings")) {
                            if (snapshot.child("ApplicationSettings").hasChild("currency"))
                                currency = String.valueOf(snapshot.child("ApplicationSettings").child("currencySymbol").getValue());
                            if (snapshot.child("ApplicationSettings").hasChild("darkTheme"))
                                darkTheme = String.valueOf(snapshot.child("ApplicationSettings").child("darkTheme").getValue());
                            if (snapshot.hasChild("PersonalTransactions")) {
                                if (!transactionsList.isEmpty())
                                    transactionsList.clear();
                                if (!datesList.isEmpty())
                                    datesList.clear();

                                if (snapshot.child("PersonalTransactions").hasChildren())
                                    for (DataSnapshot yearIterator : snapshot.child("PersonalTransactions").getChildren())
                                        if (String.valueOf(yearIterator.getKey()).equals(String.valueOf(currentTime.get(Calendar.YEAR)))) // daca am gasit anul curent
                                            for (DataSnapshot monthIterator : yearIterator.getChildren())
                                                if (String.valueOf(monthIterator.getKey()).equals(monthFormat.format(currentTime.getTime()))) // daca am gasit luna curenta
                                                    for (DataSnapshot monthChild : monthIterator.getChildren())
                                                        for (DataSnapshot monthGrandChild : monthChild.getChildren())
                                                            if (!String.valueOf(monthGrandChild.getKey()).equals("Overall")) // daca nu este 'Overall'
                                                                for (DataSnapshot monthGreatGrandChild : monthGrandChild.getChildren())
                                                                    checkIfTheTransactionCanBeAddedToTheDatesList(monthGreatGrandChild, transactionsList, datesList);
                            }
                            // sortarea listei dupa valoare descendent
                            Collections.sort(transactionsList, (o1, o2) ->
                                    Float.compare(o2.getValue(), o1.getValue()));
                            Collections.sort(datesList, Collections.reverseOrder());

                            // pentru fiecare data descendenta afisam toate datele ascendent
                            for (String datesListIterator : datesList) {
                                String[] dateFromTransactionListSplit, dateParsed = datesListIterator.split(" ");
                                // calculam suma pentru fiecare zi
                                float dateTotalIncome = 0f;
                                float dateTotalExpense = 0f;
                                String textForTotalSum, monthParsed = Months.getTranslatedMonth(ActivityMonthlyBalance.this, dateParsed[0]), dateTranslated, dayPrefix;
                                LinearLayout dayAndSumLayout, transactionLayout;
                                TextView typeText, valueText, dayText, totalSumText;

                                switch (Locale.getDefault().getDisplayLanguage()) {
                                    case "Deutsch":
                                        dayPrefix = "der";
                                        dateTranslated = dayPrefix + " " + Integer.parseInt(dateParsed[1]) + " " + monthParsed.trim();
                                        break;
                                    case "español":
                                    case "português":
                                        dayPrefix = "de";
                                        dateTranslated = Integer.parseInt(dateParsed[1]) + " " + dayPrefix + " " + monthParsed.trim().toLowerCase();
                                        break;
                                    case "français":
                                    case "română":
                                        dateTranslated = Integer.parseInt(dateParsed[1]) + " " + monthParsed.trim().toLowerCase();
                                        break;
                                    case "italiano":
                                        dayPrefix = "il";
                                        dateTranslated = dayPrefix + " " + Integer.parseInt(dateParsed[1]) + " " + monthParsed.trim().toLowerCase();
                                        break;
                                    default:
                                        dayPrefix = (Integer.parseInt(dateParsed[1]) % 10 == 1 ||
                                                Integer.parseInt(dateParsed[1]) % 10 == 2 ||
                                                Integer.parseInt(dateParsed[1]) % 10 == 3) ?
                                                (Integer.parseInt(dateParsed[1]) % 10 == 1 ?
                                                        "st" :
                                                        (Integer.parseInt(dateParsed[1]) % 10 == 2) ?
                                                                "nd" : "rd") :
                                                "th";

                                        dateTranslated = monthParsed.trim() + " " + Integer.parseInt(dateParsed[1]) + dayPrefix;
                                        break;
                                }

                                dayAndSumLayout = (LinearLayout) View.inflate(ActivityMonthlyBalance.this, R.layout.linearlayout_monthly_balance_title, null);
                                dayText = dayAndSumLayout.findViewById(R.id.monthlyBalanceRelativeLayoutDay);
                                totalSumText = dayAndSumLayout.findViewById(R.id.monthlyBalanceRelativeLayoutDayTotalSum);

                                mainLayout.addView(dayAndSumLayout);
                                dayText.setText(dateTranslated);
                                dayText.setTextSize(25);

                                if (darkTheme != null)
                                    dayText.setTextColor(darkTheme.equals("true") ?
                                            Color.YELLOW : Color.BLUE);

                                for (MoneyManager transactionsListIterator : transactionsList) {
                                    dateFromTransactionListSplit = transactionsListIterator.getDate().split("\\W");
                                    if (datesListIterator.equals(dateFromTransactionListSplit[0] + " " + dateFromTransactionListSplit[1])) {
                                        if (transactionsListIterator.getType().equals("Deposits") || transactionsListIterator.getType().equals("IndependentSources") || transactionsListIterator.getType().equals("Salary") || transactionsListIterator.getType().equals("Saving"))
                                            dateTotalIncome += transactionsListIterator.getValue();
                                        else
                                            dateTotalExpense += transactionsListIterator.getValue();

                                        transactionLayout = (LinearLayout) View.inflate(ActivityMonthlyBalance.this, R.layout.linearlayout_monthly_balance, null);
                                        typeText = transactionLayout.findViewById(R.id.monthlyBalanceRelativeLayoutType);
                                        valueText = transactionLayout.findViewById(R.id.monthlyBalanceRelativeLayoutValue);

                                        typeText.setText(Types.getTranslatedType(ActivityMonthlyBalance.this, transactionsListIterator.getType()));
                                        if (darkTheme != null)
                                            if (darkTheme.equals("true")) {
                                                typeText.setTextColor(Color.WHITE);
                                                valueText.setTextColor(Color.WHITE);
                                            } else {
                                                typeText.setTextColor(Color.BLACK);
                                                valueText.setTextColor(Color.BLACK);
                                            }
                                        typeText.setTextSize(19);

                                        String text = Locale.getDefault().getDisplayLanguage()
                                                .equals("English") ?
                                                currency + transactionsListIterator.getValue() :
                                                transactionsListIterator.getValue() + " " + currency;

                                        if (transactionsListIterator.getType().equals("Deposits") || transactionsListIterator.getType().equals("IndependentSources") || transactionsListIterator.getType().equals("Salary") || transactionsListIterator.getType().equals("Saving"))
                                            text = "+" + text;
                                        else text = "-" + text;

                                        valueText.setText(text);
                                        valueText.setTextSize(19);

                                        mainLayout.addView(transactionLayout);
                                    }
                                }

                                textForTotalSum = Locale.getDefault().getDisplayLanguage()
                                        .equals("English") ?
                                        currency + Math.abs(dateTotalIncome - dateTotalExpense) :
                                        Math.abs(dateTotalIncome - dateTotalExpense) + " " + currency;

                                totalSumText.setText(textForTotalSum);
                                totalSumText.setTextSize(25);

                                totalSumText.setTextColor(dateTotalIncome - dateTotalExpense < 0f ?
                                        Color.RED : Color.GREEN);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

 */