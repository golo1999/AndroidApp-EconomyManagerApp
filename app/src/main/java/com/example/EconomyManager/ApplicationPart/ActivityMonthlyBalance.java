package com.example.EconomyManager.ApplicationPart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Comparator;
import java.util.Locale;

public class ActivityMonthlyBalance extends AppCompatActivity
{
    private ImageView goBack;
    private FirebaseAuth fbAuth=FirebaseAuth.getInstance();
    private TextView activityTitle, centerText;
    private DatabaseReference myRef=FirebaseDatabase.getInstance().getReference();
    private SimpleDateFormat currentMonth;
    private Calendar currentTime;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setVariables()
    {
        goBack=findViewById(R.id.monthlyBalanceBack);
        activityTitle=findViewById(R.id.monthlyBalanceTitle);
        currentMonth=new SimpleDateFormat("LLLL", Locale.ENGLISH);
        centerText=findViewById(R.id.monthlyBalanceCenterText);
        currentTime=Calendar.getInstance();
        mainLayout=findViewById(R.id.monthlyBalanceMainLayout);
    }

    private void setOnClickListeners()
    {
        goBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }

    private void setActivityTheme()
    {
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                        if(snapshot.hasChild("darkTheme"))
                        {
                            boolean checked=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()));
                            int theme;

                            if(!checked)
                                theme=R.drawable.ic_white_gradient_tobacco_ad;
                            else theme=R.drawable.ic_black_gradient_night_shift;

                            getWindow().setBackgroundDrawableResource(theme);
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void setIncomesAndExpensesInParent()
    {
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    SimpleDateFormat monthFormat=new SimpleDateFormat("LLLL", Locale.ENGLISH);
                    String currency="£", darkTheme=null;
                    ArrayList<MoneyManager> transactionsList=new ArrayList<>();
                    ArrayList<String> datesList=new ArrayList<>();

                    if(snapshot.exists())
                    {
                        if(snapshot.hasChild("ApplicationSettings"))
                        {
                            if(snapshot.child("ApplicationSettings").hasChild("currency"))
                                currency=String.valueOf(snapshot.child("ApplicationSettings").child("currencySymbol").getValue());
                            if(snapshot.child("ApplicationSettings").hasChild("darkTheme"))
                                darkTheme=String.valueOf(snapshot.child("ApplicationSettings").child("darkTheme").getValue());
                            if(snapshot.hasChild("PersonalTransactions"))
                            {
                                if(!transactionsList.isEmpty())
                                    transactionsList.clear();
                                if(!datesList.isEmpty())
                                    datesList.clear();

                                if(snapshot.child("PersonalTransactions").hasChildren())
                                    for(DataSnapshot yearIterator:snapshot.child("PersonalTransactions").getChildren())
                                        if(String.valueOf(yearIterator.getKey()).equals(String.valueOf(currentTime.get(Calendar.YEAR)))) // daca am gasit anul curent
                                            for(DataSnapshot monthIterator:yearIterator.getChildren())
                                                if(String.valueOf(monthIterator.getKey()).equals(monthFormat.format(currentTime.getTime()))) // daca am gasit luna curenta
                                                    for(DataSnapshot monthChild:monthIterator.getChildren())
                                                        for(DataSnapshot monthGrandChild:monthChild.getChildren())
                                                            if(!String.valueOf(monthGrandChild.getKey()).equals("Overall")) // daca nu este 'Overall'
                                                                for(DataSnapshot monthGreatGrandChild:monthGrandChild.getChildren())
                                                                    checkIfTheTransactionCanBeAddedToTheDatesList(monthGreatGrandChild, transactionsList, datesList);
                            }
                            Collections.sort(transactionsList, new Comparator<MoneyManager>()
                            {
                                @Override
                                public int compare(MoneyManager o1, MoneyManager o2)
                                {
                                    return Float.compare(o2.getValue(), o1.getValue());
                                }
                            }); // sortarea listei dupa valoare descendent
                            Collections.sort(datesList, Collections.<String>reverseOrder());

                            for(String datesListIterator:datesList) // pentru fiecare data descendenta afisam toate datele ascendent
                            {
                                String[] dateFromTransactionListSplit, dateParsed;
                                float dateTotalIncome=0f, dateTotalExpense=0f; // calculam suma pentru fiecare zi
                                String textForTotalSum, monthParsed, dateTranslated, dayPrefix;
                                LinearLayout dayAndSumLayout, transactionLayout;
                                TextView typeText, valueText, dayText, totalSumText;

                                dateParsed=datesListIterator.split(" ");
                                switch(dateParsed[0])
                                {
                                    case "January":
                                        monthParsed=getResources().getString(R.string.month_january).trim();
                                        break;
                                    case "February":
                                        monthParsed=getResources().getString(R.string.month_february).trim();
                                        break;
                                    case "March":
                                        monthParsed=getResources().getString(R.string.month_march).trim();
                                        break;
                                    case "April":
                                        monthParsed=getResources().getString(R.string.month_april).trim();
                                        break;
                                    case "May":
                                        monthParsed=getResources().getString(R.string.month_may).trim();
                                        break;
                                    case "June":
                                        monthParsed=getResources().getString(R.string.month_june).trim();
                                        break;
                                    case "July":
                                        monthParsed=getResources().getString(R.string.month_july).trim();
                                        break;
                                    case "August":
                                        monthParsed=getResources().getString(R.string.month_august).trim();
                                        break;
                                    case "September":
                                        monthParsed=getResources().getString(R.string.month_september).trim();
                                        break;
                                    case "October":
                                        monthParsed=getResources().getString(R.string.month_october).trim();
                                        break;
                                    case "November":
                                        monthParsed=getResources().getString(R.string.month_november).trim();
                                        break;
                                    default:
                                        monthParsed=getResources().getString(R.string.month_december).trim();
                                        break;
                                }

                                switch(Locale.getDefault().getDisplayLanguage())
                                {
                                    case "Deutsch":
                                        dayPrefix="der";
                                        dateTranslated=dayPrefix+" "+Integer.parseInt(dateParsed[1])+" "+monthParsed.trim();
                                        break;
                                    case "español":
                                    case "português":
                                        dayPrefix="de";
                                        dateTranslated=Integer.parseInt(dateParsed[1])+" "+dayPrefix+" "+monthParsed.trim().toLowerCase();
                                        break;
                                    case "français":
                                    case "română":
                                        dateTranslated=Integer.parseInt(dateParsed[1])+" "+monthParsed.trim().toLowerCase();
                                        break;
                                    case "italiano":
                                        dayPrefix="il";
                                        dateTranslated=dayPrefix+" "+Integer.parseInt(dateParsed[1])+" "+monthParsed.trim().toLowerCase();
                                        break;
                                    default:
                                        dayPrefix="th";
                                        if(Integer.parseInt(dateParsed[1])%10==1)
                                            dayPrefix="st";
                                        else if(Integer.parseInt(dateParsed[1])%10==2)
                                            dayPrefix="nd";
                                        else if(Integer.parseInt(dateParsed[1])%10==3)
                                            dayPrefix="rd";
                                        dateTranslated=monthParsed.trim()+" "+Integer.parseInt(dateParsed[1])+dayPrefix;
                                        break;
                                }

                                dayAndSumLayout=(LinearLayout)getLayoutInflater().inflate(R.layout.linearlayout_monthly_balance_title, null);
                                dayText=dayAndSumLayout.findViewById(R.id.monthlyBalanceRelativeLayoutDay);
                                totalSumText=dayAndSumLayout.findViewById(R.id.monthlyBalanceRelativeLayoutDayTotalSum);

                                mainLayout.addView(dayAndSumLayout);
                                dayText.setText(dateTranslated);
                                dayText.setTextSize(25);
                                if(darkTheme!=null)
                                    if(darkTheme.equals("true"))
                                        dayText.setTextColor(Color.YELLOW);
                                    else dayText.setTextColor(Color.BLUE);

                                for(MoneyManager transactionsListIterator:transactionsList)
                                {
                                    dateFromTransactionListSplit=transactionsListIterator.getDate().split("\\W");
                                    if(datesListIterator.equals(dateFromTransactionListSplit[0]+" "+dateFromTransactionListSplit[1]))
                                    {
                                        if(transactionsListIterator.getType().equals("Deposits") || transactionsListIterator.getType().equals("IndependentSources") || transactionsListIterator.getType().equals("Salary") || transactionsListIterator.getType().equals("Saving"))
                                            dateTotalIncome+=transactionsListIterator.getValue();
                                        else dateTotalExpense+=transactionsListIterator.getValue();

                                        transactionLayout=(LinearLayout)getLayoutInflater().inflate(R.layout.linearlayout_monthly_balance, null);
                                        typeText=transactionLayout.findViewById(R.id.monthlyBalanceRelativeLayoutType);
                                        valueText=transactionLayout.findViewById(R.id.monthlyBalanceRelativeLayoutValue);

                                        typeText.setText(getTranslatedTransactionType(transactionsListIterator.getType()));
                                        if(darkTheme!=null)
                                            if(darkTheme.equals("true"))
                                            {
                                                typeText.setTextColor(Color.WHITE);
                                                valueText.setTextColor(Color.WHITE);
                                            }
                                            else
                                            {
                                                typeText.setTextColor(Color.BLACK);
                                                valueText.setTextColor(Color.BLACK);
                                            }
                                        typeText.setTextSize(19);

                                        String text;
                                        if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                            text=currency+transactionsListIterator.getValue();
                                        else
                                            text=transactionsListIterator.getValue()+" "+currency;

                                        if(transactionsListIterator.getType().equals("Deposits") || transactionsListIterator.getType().equals("IndependentSources") || transactionsListIterator.getType().equals("Salary") || transactionsListIterator.getType().equals("Saving"))
                                            text="+"+text;
                                        else text="-"+text;

                                        valueText.setText(text);
                                        valueText.setTextSize(19);

                                        mainLayout.addView(transactionLayout);
                                    }
                                }

                                if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                    textForTotalSum=currency+Math.abs(dateTotalIncome-dateTotalExpense);
                                else textForTotalSum=Math.abs(dateTotalIncome-dateTotalExpense)+" "+currency;
                                totalSumText.setText(textForTotalSum);
                                totalSumText.setTextSize(25);
                                if(dateTotalIncome-dateTotalExpense<0f)
                                    totalSumText.setTextColor(Color.RED);
                                else totalSumText.setTextColor(Color.GREEN);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void setTitle()
    {
        String year=String.valueOf(currentTime.get(Calendar.YEAR)), month=currentMonth.format(currentTime.getTime());

        switch(month)
        {
            case "January":
                month=getResources().getString(R.string.month_january);
                break;
            case "February":
                month=getResources().getString(R.string.month_february);
                break;
            case "March":
                month=getResources().getString(R.string.month_march);
                break;
            case "April":
                month=getResources().getString(R.string.month_april);
                break;
            case "May":
                month=getResources().getString(R.string.month_may);
                break;
            case "June":
                month=getResources().getString(R.string.month_june);
                break;
            case "July":
                month=getResources().getString(R.string.month_july);
                break;
            case "August":
                month=getResources().getString(R.string.month_august);
                break;
            case "September":
                month=getResources().getString(R.string.month_september);
                break;
            case "October":
                month=getResources().getString(R.string.month_october);
                break;
            case "November":
                month=getResources().getString(R.string.month_november);
                break;
            case "December":
                month=getResources().getString(R.string.month_december);
                break;
        }

        String currentMonthYear=month.trim()+" "+year;
        activityTitle.setText(currentMonthYear);
        activityTitle.setTextSize(20);
        activityTitle.setTextColor(Color.WHITE);
    }

    private void setCenterText()
    {
        final String year=String.valueOf(currentTime.get(Calendar.YEAR)), month=currentMonth.format(currentTime.getTime());

        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).addValueEventListener(new ValueEventListener()
            {
                boolean darkThemeEnabled;

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                        if(snapshot.hasChild("ApplicationSettings"))
                        {
                            if(snapshot.child("ApplicationSettings").hasChild("darkTheme"))
                                darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("ApplicationSettings").child("darkTheme").getValue()));
                            if(snapshot.hasChild("PersonalTransactions"))
                            {
                                if(snapshot.child("PersonalTransactions").hasChild(year))
                                    if(snapshot.child("PersonalTransactions").child(year).hasChild(month))
                                        centerText.setText("");
                                    else centerText.setText(R.string.no_transactions_this_month);
                            }
                            else centerText.setText(R.string.no_transactions_yet);

                            if(!darkThemeEnabled)
                                centerText.setTextColor(Color.parseColor("#195190"));
                            else centerText.setTextColor(Color.WHITE);
                            centerText.setTextSize(20);
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void checkIfTheTransactionCanBeAddedToTheDatesList(DataSnapshot iterator, ArrayList<MoneyManager> transactionsList, ArrayList<String> datesList)
    {
        String note, date, type;
        float value;
        MoneyManager money;
        boolean containedIntoDatesList=false;
        String[] dateSplitIntoWords;

        note=String.valueOf(iterator.child("note").getValue());
        date=String.valueOf(iterator.child("date").getValue());
        type=String.valueOf(iterator.child("type").getValue());
        value=Float.parseFloat(String.valueOf(iterator.child("value").getValue()));
        money=new MoneyManager(note, value, date, type);
        transactionsList.add(money);
        dateSplitIntoWords=date.split("\\W");

        if(datesList.isEmpty())
            datesList.add(dateSplitIntoWords[0]+" "+dateSplitIntoWords[1]);
        else
        {
            for(int j=0; j<datesList.size(); j++)
                if(datesList.get(j).equals(dateSplitIntoWords[0]+" "+dateSplitIntoWords[1]))
                {
                    containedIntoDatesList=true;
                    break;
                }
            if(!containedIntoDatesList)
                datesList.add(dateSplitIntoWords[0]+" "+dateSplitIntoWords[1]);
        }
    }

    private String getTranslatedTransactionType(String type)
    {
        switch(type)
        {
            case "Deposits":
                return getResources().getString(R.string.add_money_deposits);
            case "IndependentSources":
                return getResources().getString(R.string.add_money_independent_sources);
            case "Salary":
                return getResources().getString(R.string.salary);
            case "Saving":
                return getResources().getString(R.string.saving);
            case "Bills":
                return getResources().getString(R.string.subtract_money_bills);
            case "Car":
                return getResources().getString(R.string.subtract_money_car);
            case "Clothes":
                return getResources().getString(R.string.subtract_money_clothes);
            case "Communications":
                return getResources().getString(R.string.subtract_money_communications);
            case "EatingOut":
                return getResources().getString(R.string.subtract_money_eating_out);
            case "Entertainment":
                return getResources().getString(R.string.subtract_money_entertainment);
            case "Food":
                return getResources().getString(R.string.subtract_money_food);
            case "Gifts":
                return getResources().getString(R.string.subtract_money_gifts);
            case "Health":
                return getResources().getString(R.string.subtract_money_health);
            case "House":
                return getResources().getString(R.string.subtract_money_house);
            case "Pets":
                return getResources().getString(R.string.subtract_money_pets);
            case "Sports":
                return getResources().getString(R.string.subtract_money_sports);
            case "Taxi":
                return getResources().getString(R.string.subtract_money_taxi);
            case "Toiletry":
                return getResources().getString(R.string.subtract_money_toiletry);
            case "Transport":
                return getResources().getString(R.string.subtract_money_transport);
            default:
                return null;
        }
    }
}