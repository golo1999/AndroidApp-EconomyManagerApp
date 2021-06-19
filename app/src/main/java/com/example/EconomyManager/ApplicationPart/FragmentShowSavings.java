package com.example.EconomyManager.ApplicationPart;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.EconomyManager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FragmentShowSavings extends Fragment
{
    private DatabaseReference myRef;
    private FirebaseAuth fbAuth;
    private TextView savings;

    public FragmentShowSavings()
    {

    }

    public static FragmentShowSavings newInstance()
    {
        FragmentShowSavings fragment = new FragmentShowSavings();
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
        View v=inflater.inflate(R.layout.fragment_show_savings, container, false);
        setVariables(v);
        return v;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        setSavings();
    }

    private void setVariables(View v)
    {
        myRef= FirebaseDatabase.getInstance().getReference();
        fbAuth=FirebaseAuth.getInstance();
        savings=v.findViewById(R.id.fragment2Savings);
    }

    private void setSavings()
    {
        final Calendar currentTime=Calendar.getInstance();
        final SimpleDateFormat monthFormat=new SimpleDateFormat("LLLL", Locale.ENGLISH);

        if(fbAuth.getUid()!=null)
        {
            myRef.child(fbAuth.getUid()).addValueEventListener(new ValueEventListener()
            {
                String savingsText=null, incomesDB="0", expensesDB="0";

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        String currency=String.valueOf(snapshot.child("ApplicationSettings").child("currencySymbol").getValue());

                        if(snapshot.hasChild("PersonalTransactions"))
                        {
                            if(snapshot.child("PersonalTransactions").hasChild(String.valueOf(currentTime.get(Calendar.YEAR))))
                            {
                                if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).hasChild(monthFormat.format(currentTime.getTime())))
                                {
                                    if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).hasChild("Incomes"))
                                    {
                                        if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).child("Incomes").hasChild("Overall"))
                                            incomesDB=String.valueOf(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).child("Incomes").child("Overall").getValue());
                                    }
                                    else incomesDB="0";

                                    if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).hasChild("Expenses"))
                                    {
                                        if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).child("Expenses").hasChild("Overall"))
                                            expensesDB=String.valueOf(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).child("Expenses").child("Overall").getValue());
                                    }
                                    else expensesDB="0";

                                    try
                                    {
                                        if(incomesDB.contains("."))
                                            if(incomesDB.length()-1-incomesDB.indexOf(".")>2)
                                                incomesDB=incomesDB.substring(0, incomesDB.indexOf(".")+3);

                                        if(expensesDB.contains("."))
                                            if(expensesDB.length()-1-expensesDB.indexOf(".")>2)
                                                expensesDB=expensesDB.substring(0, expensesDB.indexOf(".")+3);

                                        if(Float.parseFloat(incomesDB)-Float.parseFloat(expensesDB)<0f)
                                        {
                                            float absOfSaving=Math.abs(Float.parseFloat(incomesDB)-Float.parseFloat(expensesDB));

                                            if(String.valueOf(absOfSaving).contains("."))
                                                if(String.valueOf(absOfSaving).length()-1-String.valueOf(absOfSaving).indexOf(".")>2)
                                                    absOfSaving=Float.parseFloat(String.format(Locale.getDefault(), "%.2f", absOfSaving));

                                            if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                                savingsText=currency+absOfSaving;
                                            else savingsText=absOfSaving+" "+currency;
                                        }
                                        else
                                        {
                                            float saving=Float.parseFloat(incomesDB)-Float.parseFloat(expensesDB);

                                            if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                                savingsText=currency+Float.parseFloat(String.format(Locale.getDefault(), "%.2f", saving));
                                            else savingsText=String.format(Locale.getDefault(), "%.2f", saving)+" "+currency;
                                        }

                                        if(savingsText.contains("."))
                                            if(savingsText.length()-1-savingsText.indexOf(".")>2)
                                                savingsText=savingsText.substring(0, savingsText.indexOf(".")+3);

                                        if(savingsText.contains(","))
                                            savingsText=savingsText.replace(",", ".");

                                        savings.setText(savingsText);
                                    }
                                    catch (IllegalStateException e)
                                    {
                                        e.printStackTrace();
                                    }

                                    if(Float.parseFloat(incomesDB)-Float.parseFloat(expensesDB)<0f)
                                        savings.setTextColor(Color.RED);
                                    else savings.setTextColor(Color.BLUE);
                                }
                                else savings.setText(R.string.no_transactions_this_month);
                            }
                            else savings.setText(R.string.no_transactions_this_year);
                        }
                        else savings.setText(R.string.no_transactions_yet);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });




            //vechea
            /*myRef.child(fbAuth.getUid()).child("PersonalTransactions").addValueEventListener(new ValueEventListener()
            {
                String savingsText=null, incomesDB="0", expensesDB="0";

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        if(snapshot.hasChild(String.valueOf(currentTime.get(Calendar.YEAR))))
                        {
                            if(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).hasChild(monthFormat.format(currentTime.getTime())))
                            {
                                if(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).hasChild("Incomes"))
                                {
                                    if(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).child("Incomes").hasChild("Overall"))
                                        incomesDB=String.valueOf(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).child("Incomes").child("Overall").getValue());
                                }
                                else incomesDB="0";

                                if(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).hasChild("Expenses"))
                                {
                                    if(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).child("Expenses").hasChild("Overall"))
                                        expensesDB=String.valueOf(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(monthFormat.format(currentTime.getTime())).child("Expenses").child("Overall").getValue());
                                }
                                else expensesDB="0";

                                try
                                {
                                    String currency=getResources().getString(R.string.currency);
                                    if(Float.parseFloat(incomesDB)-Float.parseFloat(expensesDB)<0f)
                                    {
                                        String saving=String.valueOf(Float.parseFloat(incomesDB)-Float.parseFloat(expensesDB));
                                        saving=saving.substring(1);
                                        saving="-"+currency+saving;
                                        savingsText=saving;
                                    }
                                    else savingsText=currency+(Float.parseFloat(incomesDB)-Float.parseFloat(expensesDB));
                                    savings.setText(savingsText);
                                }
                                catch (IllegalStateException e)
                                {
                                    e.printStackTrace();
                                }

                                if(Float.parseFloat(incomesDB)-Float.parseFloat(expensesDB)<0f)
                                    savings.setTextColor(Color.parseColor("#FF0000"));
                                else if(Float.parseFloat(incomesDB)-Float.parseFloat(expensesDB)>0f)
                                    savings.setTextColor(Color.parseColor("#0000FF"));
                                else savings.setTextColor(Color.parseColor("#000000"));
                            }
                            else savings.setText(R.string.no_transactions_this_month);
                        }
                        else savings.setText(R.string.no_transactions_this_year);
                    }
                    else savings.setText(R.string.no_transactions_yet);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });*/
        }
    }
}