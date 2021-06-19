package com.example.EconomyManager.ApplicationPart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.EconomyManager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentMoneySpent extends Fragment
{
    private FirebaseAuth fbAuth;
    private DatabaseReference myRef;
    private TextView moneySpent;

    public FragmentMoneySpent()
    {
        // Required empty public constructor
    }
    public static FragmentMoneySpent newInstance()
    {
        FragmentMoneySpent fragment = new FragmentMoneySpent();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.fragment_money_spent, container, false);
        setVariables(v);
        return v;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setLastWeekExpenses();
    }

    private void setVariables(View v)
    {
        fbAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        moneySpent=v.findViewById(R.id.money_spent_week);
    }

    private void setLastWeekExpenses()
    {
        final Calendar currentTime=Calendar.getInstance();
        final DateFormat format1=new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        final DateFormat format2=new SimpleDateFormat("LLLL", Locale.ENGLISH);
        final DateFormat format3=new SimpleDateFormat("yyyy", Locale.ENGLISH);
        final DateFormat format4=new SimpleDateFormat("MMMM d, yyyy H:m:s", Locale.ENGLISH);
        long DAY_IN_MS=1000*60*60*24;
        String currentHour=String.valueOf(currentTime.get(Calendar.HOUR_OF_DAY)), currentMinute=String.valueOf(currentTime.get(Calendar.MINUTE)), currentSecond=String.valueOf(currentTime.get(Calendar.SECOND));
        final String sevenDaysAgoMonth, sevenDaysAgoFullDate;
        final String sevenDaysAgoYear;

        if(Integer.parseInt(currentHour)<10)
            currentHour="0"+currentHour;
        if(Integer.parseInt(currentMinute)<10)
            currentMinute="0"+currentMinute;
        if(Integer.parseInt(currentSecond)<10)
            currentSecond="0"+currentSecond;

        final Date sevenDaysBack=new Date(System.currentTimeMillis()-7*DAY_IN_MS);
        sevenDaysAgoFullDate=format1.format(sevenDaysBack.getTime())+" "+currentHour+":"+currentMinute+":"+currentSecond;
        sevenDaysAgoMonth=format2.format(sevenDaysBack.getTime());
        sevenDaysAgoYear=format3.format(sevenDaysBack.getTime());

        if(fbAuth.getUid()!=null)
        {
            myRef.child(fbAuth.getUid()).addValueEventListener(new ValueEventListener()
            {
                float moneySpentLastWeek=0f;

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        String currency=String.valueOf(snapshot.child("ApplicationSettings").child("currencySymbol").getValue());

                        if(snapshot.hasChild("PersonalTransactions"))
                            if(!sevenDaysAgoYear.equals(String.valueOf(currentTime.get(Calendar.YEAR)))) // daca acum o saptamana a fost an diferit
                            {
                                if(snapshot.child("PersonalTransactions").hasChild(String.valueOf(currentTime.get(Calendar.YEAR)))) // daca exista tranzactii in ultima saptamana, dar in anul curent
                                    if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).hasChild(format2.format(currentTime.getTime())))
                                        if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(format2.format(currentTime.getTime())).hasChild("Expenses"))
                                            for(DataSnapshot transactionTypesIterator:snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(format2.format(currentTime.getTime())).child("Expenses").getChildren())
                                                if(!String.valueOf(transactionTypesIterator.getKey()).equals("Overall"))
                                                    for(DataSnapshot transactionsIterator:transactionTypesIterator.getChildren())
                                                        if(checkIfTheTransactionWasMadeInTheLastWeek(transactionsIterator, format4, sevenDaysAgoFullDate)==1)
                                                            moneySpentLastWeek+=Float.parseFloat(String.valueOf(transactionsIterator.child("value").getValue()));

                                if(snapshot.child("PersonalTransactions").hasChild(sevenDaysAgoYear)) // daca exista tranzactii in ultima saptamana, dar in anul trecut
                                    if(snapshot.child("PersonalTransactions").child(sevenDaysAgoYear).hasChild(sevenDaysAgoMonth))
                                        if(snapshot.child("PersonalTransactions").child(sevenDaysAgoYear).child(sevenDaysAgoMonth).hasChild("Expenses"))
                                            for(DataSnapshot transactionTypesIterator:snapshot.child("PersonalTransactions").child(sevenDaysAgoYear).child(sevenDaysAgoMonth).child("Expenses").getChildren())
                                                if(!String.valueOf(transactionTypesIterator.getKey()).equals("Overall"))
                                                    for(DataSnapshot transactionsIterator:transactionTypesIterator.getChildren())
                                                        if(checkIfTheTransactionWasMadeInTheLastWeek(transactionsIterator, format4, sevenDaysAgoFullDate)==1)
                                                            moneySpentLastWeek+=Float.parseFloat(String.valueOf(transactionsIterator.child("value").getValue()));
                            }
                            else // daca acum o saptamana a fost acelasi an
                            {
                                if(snapshot.child("PersonalTransactions").hasChild(String.valueOf(currentTime.get(Calendar.YEAR))))
                                    if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).hasChildren())
                                    {
                                        if(sevenDaysAgoMonth.compareTo(format2.format(currentTime.getTime()))==0) // daca acum o saptamana a fost aceeasi luna
                                        {
                                            if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).hasChildren())
                                                if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).hasChild(format2.format(currentTime.getTime()))) // daca are child luna curenta
                                                    if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(format2.format(currentTime.getTime())).hasChild("Expenses"))
                                                        for(DataSnapshot transactionTypesIterator:snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(format2.format(currentTime.getTime())).child("Expenses").getChildren())
                                                            if(!String.valueOf(transactionTypesIterator.getKey()).equals("Overall"))
                                                                for(DataSnapshot transactionsIterator:transactionTypesIterator.getChildren())
                                                                    if(checkIfTheTransactionWasMadeInTheLastWeek(transactionsIterator, format4, sevenDaysAgoFullDate)==1)
                                                                        moneySpentLastWeek+=Float.parseFloat(String.valueOf(transactionsIterator.child("value").getValue()));
                                        }
                                        else // daca acum o saptamana a fost alta luna, vom face 2 queries: una pt luna curenta, iar cealalta pentru luna precedenta
                                        {
                                            if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).hasChildren())
                                            {
                                                if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).hasChild(format2.format(currentTime.getTime()))) // daca are child luna curenta
                                                    if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(format2.format(currentTime.getTime())).hasChild("Expenses"))
                                                        for(DataSnapshot transactionTypesIterator:snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(format2.format(currentTime.getTime())).child("Expenses").getChildren())
                                                            if(!String.valueOf(transactionTypesIterator.getKey()).equals("Overall"))
                                                                for(DataSnapshot transactionsIterator:transactionTypesIterator.getChildren())
                                                                    if(checkIfTheTransactionWasMadeInTheLastWeek(transactionsIterator, format4, sevenDaysAgoFullDate)==1)
                                                                        moneySpentLastWeek+=Float.parseFloat(String.valueOf(transactionsIterator.child("value").getValue()));

                                                if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).hasChild(format2.format(sevenDaysBack.getTime()))) // daca are child luna precedenta
                                                    if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(format2.format(sevenDaysBack.getTime())).hasChild("Expenses"))
                                                        for(DataSnapshot transactionTypesIterator:snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(format2.format(sevenDaysBack.getTime())).child("Expenses").getChildren())
                                                            if(!String.valueOf(transactionTypesIterator.getKey()).equals("Overall"))
                                                                for(DataSnapshot transactionsIterator:transactionTypesIterator.getChildren())
                                                                    if(checkIfTheTransactionWasMadeInTheLastWeek(transactionsIterator, format4, sevenDaysAgoFullDate)==1)
                                                                        moneySpentLastWeek+=Float.parseFloat(String.valueOf(transactionsIterator.child("value").getValue()));
                                            }
                                        }
                                    }
                            }

                        try
                        {
                            if(moneySpentLastWeek<=0f)
                                moneySpent.setText(R.string.no_money_last_week);
                            else
                            {
                                String moneySpentText;

                                if(String.valueOf(moneySpentLastWeek).contains("."))
                                    if(String.valueOf(moneySpentLastWeek).length()-1-String.valueOf(moneySpentLastWeek).indexOf(".")>2)
                                        moneySpentLastWeek=Float.parseFloat(String.format(Locale.getDefault(), "%.2f", moneySpentLastWeek));

                                if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                    moneySpentText=getResources().getString(R.string.money_spent_you_spent)+" "+currency+moneySpentLastWeek+" "+getResources().getString(R.string.money_spent_last_week);
                                else moneySpentText=getResources().getString(R.string.money_spent_you_spent)+" "+moneySpentLastWeek+" "+currency+" "+getResources().getString(R.string.money_spent_last_week);
                                moneySpent.setText(moneySpentText.trim());
                            }
                        }
                        catch (IllegalStateException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
        }
    }

    private int checkIfTheTransactionWasMadeInTheLastWeek(DataSnapshot iterator, DateFormat dateFormat, String sevenDaysAgo)
    {
        Date dateFromDatabaseParsed=null, sevenDaysAgoParsed=null;

        try
        {
            if(iterator.getKey()!=null)
                dateFromDatabaseParsed=dateFormat.parse(iterator.getKey());
            sevenDaysAgoParsed=dateFormat.parse(sevenDaysAgo);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        if(dateFromDatabaseParsed!=null && sevenDaysAgoParsed!=null && getActivity()!=null)
        {
            if(Integer.parseInt(String.valueOf(dateFromDatabaseParsed.compareTo(sevenDaysAgoParsed)))>0) // conditia ca data din baza de date sa fie in ultima saptamana
                if(iterator.hasChild("value"))
                    return 1;
        }
        return 0;
    }
}