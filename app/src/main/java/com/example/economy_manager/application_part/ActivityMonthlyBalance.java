package com.example.economy_manager.application_part;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.Months;
import com.example.economy_manager.MyCustomSharedPreferences;
import com.example.economy_manager.MyCustomVariables;
import com.example.economy_manager.R;
import com.example.economy_manager.Transaction;
import com.example.economy_manager.Types;
import com.example.economy_manager.UserDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class ActivityMonthlyBalance extends AppCompatActivity {
    private UserDetails userDetails;
    private ImageView goBack;
    private TextView activityTitle;
    private TextView centerText;
    private SimpleDateFormat currentMonth;
    private Calendar currentTime;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_balance);
        setVariables();
        setActivityTheme();
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
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);
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
        if (userDetails != null) {
            final boolean checked = userDetails.getApplicationSettings().getDarkTheme();
            final int theme = !checked ?
                    R.drawable.ic_white_gradient_tobacco_ad :
                    R.drawable.ic_black_gradient_night_shift;

            getWindow().setBackgroundDrawableResource(theme);
        }
    }

    private void setIncomesAndExpensesInParent() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String currency = "£";
                            String darkTheme = null;
                            final ArrayList<Transaction> transactionsList = new ArrayList<>();
                            final ArrayList<Integer> daysList = new ArrayList<>();

                            if (snapshot.exists()) {
                                if (snapshot.hasChild("ApplicationSettings")) {
                                    if (snapshot.child("ApplicationSettings").hasChild("currency"))
                                        currency = String.valueOf(snapshot.child("ApplicationSettings")
                                                .child("currencySymbol").getValue());
                                    if (snapshot.child("ApplicationSettings").hasChild("darkTheme"))
                                        darkTheme = String.valueOf(snapshot.child("ApplicationSettings")
                                                .child("darkTheme").getValue());
                                    if (snapshot.hasChild("PersonalTransactions")) {
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
                                    transactionsList.sort((o1, o2) ->
                                            Float.compare(Float.parseFloat(String.valueOf(o2.getValue())),
                                                    Float.parseFloat(String.valueOf(o1.getValue()))));
                                    daysList.sort(Collections.reverseOrder());

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
                                                                .charAt(0) + String.valueOf(currentDate.getMonth())
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
                                                dayPrefix = dayFromDaysList % 10 == 1 ?
                                                        "st" : dayFromDaysList % 10 == 2 ?
                                                        "nd" : dayFromDaysList % 10 == 3 ?
                                                        "rd" : "th";

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
        if (userDetails != null && MyCustomVariables.getFirebaseAuth().getUid() != null) {
            boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();

            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("PersonalTransactions") &&
                                    snapshot.child("PersonalTransactions").hasChildren()) {
                                int numberOfCurrentMonthTransactions = 0;

                                for (DataSnapshot transaction :
                                        snapshot.child("PersonalTransactions").getChildren()) {
                                    Transaction databaseTransaction = transaction.getValue(Transaction.class);

                                    if (databaseTransaction != null &&
                                            databaseTransaction.getTime() != null &&
                                            databaseTransaction.getTime().getMonth() ==
                                                    LocalDate.now().getMonthValue()) {
                                        ++numberOfCurrentMonthTransactions;
                                        break;
                                    }
                                }

                                centerText.setText(numberOfCurrentMonthTransactions > 0 ?
                                        "" : getResources()
                                        .getString(R.string.no_transactions_this_month));
                            } else {
                                centerText.setText(R.string.no_transactions_yet);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            centerText.setTextColor(!darkThemeEnabled ?
                    Color.parseColor("#195190") :
                    Color.WHITE);
            centerText.setTextSize(20);
        }
    }

    private boolean checkIfDayCanBeAddedToList(final ArrayList<Integer> daysList, final int dayToBeAdded) {
        if (!daysList.isEmpty()) {
            for (int dayFromList : daysList) {
                if (dayFromList == dayToBeAdded) {
                    return false;
                }
            }
        }

        return true;
    }
}