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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FragmentBudgetReview extends Fragment
{
    private FirebaseAuth fbAuth;
    private DatabaseReference myRef;
    private TextView incomes, expenses;

    public FragmentBudgetReview()
    {

    }

    public static FragmentBudgetReview newInstance()
    {
        FragmentBudgetReview fragment = new FragmentBudgetReview();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v=inflater.inflate(R.layout.cardview_budget_review, container, false);
        setVariables(v);
        return v;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        updateMoneyBalance();
    }

    private void setVariables(View v)
    {
        fbAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        incomes=v.findViewById(R.id.budgetReviewIncomesText);
        expenses=v.findViewById(R.id.budgetReviewExpensesText);
    }

    private void updateMoneyBalance()
    {
        final Calendar currentTime=Calendar.getInstance();
        final SimpleDateFormat currentMonth=new SimpleDateFormat("LLLL", Locale.ENGLISH);

        if(fbAuth.getUid()!=null)
        {
            myRef.child(fbAuth.getUid()).addValueEventListener(new ValueEventListener()
            {
                String incomesDB, expensesDB;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(getContext()!=null)
                        if(snapshot.exists())
                        {
                            String currency=String.valueOf(snapshot.child("ApplicationSettings").child("currencySymbol").getValue());

                            if(snapshot.hasChild("PersonalTransactions"))
                            {
                                if(snapshot.child("PersonalTransactions").hasChild(String.valueOf(currentTime.get(Calendar.YEAR))))
                                {
                                    if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).hasChild(currentMonth.format(currentTime.getTime())))
                                    {
                                        if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).hasChild("Incomes"))
                                        {
                                            if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Incomes").hasChild("Overall"))
                                            {
                                                if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                                    incomesDB=currency+snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Incomes").child("Overall").getValue();
                                                else incomesDB=snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Incomes").child("Overall").getValue()+" "+currency;
                                                incomes.setText(incomesDB);
                                            }
                                            if(!snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).hasChild("Expenses"))
                                            {
                                                if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                                    expensesDB=currency+"0";
                                                else expensesDB="0 "+currency;
                                                expenses.setText(expensesDB);
                                            }
                                        }
                                        if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).hasChild("Expenses"))
                                        {
                                            if(snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Expenses").hasChild("Overall"))
                                            {
                                                if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                                    expensesDB=currency+snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Expenses").child("Overall").getValue();
                                                else expensesDB=snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Expenses").child("Overall").getValue()+" "+currency;
                                                expenses.setText(expensesDB);
                                            }
                                            if(!snapshot.child("PersonalTransactions").child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).hasChild("Incomes"))
                                            {
                                                if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                                    incomesDB=currency+"0";
                                                else incomesDB="0 "+currency;
                                                incomes.setText(incomesDB);
                                            }
                                        }
                                    }
                                    else if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                        incomesDB=expensesDB=currency+"0";
                                    else incomesDB=expensesDB="0 "+currency;
                                }
                                else if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                    incomesDB=expensesDB=currency+"0";
                                else incomesDB=expensesDB="0 "+currency;
                            }
                            else if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                incomesDB=expensesDB=currency+"0";
                            else incomesDB=expensesDB="0 "+currency;

                            incomes.setText(incomesDB);
                            expenses.setText(expensesDB);
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });

            /*myRef.child(fbAuth.getUid()).child("PersonalTransactions").addValueEventListener(new ValueEventListener()
            {
                String incomesDB, expensesDB;

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(getContext()!=null)
                    {
                        String currency=getResources().getString(R.string.currency);
                        if(snapshot.exists())
                        {
                            if(snapshot.hasChild(String.valueOf(currentTime.get(Calendar.YEAR))))
                            {
                                if(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).hasChild(currentMonth.format(currentTime.getTime())))
                                {
                                    if(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).hasChild("Incomes"))
                                    {
                                        if(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Incomes").hasChild("Overall"))
                                        {
                                            incomesDB=currency+snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Incomes").child("Overall").getValue();
                                            incomes.setText(incomesDB);
                                        }
                                        if(!snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).hasChild("Expenses"))
                                        {
                                            expensesDB=currency+"0";
                                            expenses.setText(expensesDB);
                                        }
                                    }
                                    if(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).hasChild("Expenses"))
                                    {
                                        if(snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Expenses").hasChild("Overall"))
                                        {
                                            expensesDB=currency+snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Expenses").child("Overall").getValue();
                                            expenses.setText(expensesDB);
                                        }
                                        if(!snapshot.child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).hasChild("Incomes"))
                                        {
                                            incomesDB=currency+"0";
                                            incomes.setText(incomesDB);
                                        }
                                    }
                                }
                                else incomesDB=expensesDB=currency+"0";
                            }
                            else incomesDB=expensesDB=currency+"0";
                        }
                        else incomesDB=expensesDB=currency+"0";
                        incomes.setText(incomesDB);
                        expenses.setText(expensesDB);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            }); -> pana aici era vechiul listener si era super */





            /*myRef.child(fbAuth.getUid()).child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Incomes").addListenerForSingleValueEvent(new ValueEventListener()
            {
                String incomesDatabase;

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("Overall"))
                    {
                        if(!snapshot.child("Overall").getValue().toString().isEmpty())
                            incomesDatabase=snapshot.child("Overall").getValue().toString();
                    }
                    else incomesDatabase="0";
                    incomes.setText(incomesDatabase);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });

            myRef.child(fbAuth.getUid()).child(String.valueOf(currentTime.get(Calendar.YEAR))).child(currentMonth.format(currentTime.getTime())).child("Expenses").addListenerForSingleValueEvent(new ValueEventListener()
            {
                String expensesDatabase;

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("Overall"))
                    {
                        if(!snapshot.child("Overall").getValue().toString().isEmpty())
                            expensesDatabase=snapshot.child("Overall").getValue().toString();
                    }
                    else expensesDatabase="0";
                    expenses.setText(expensesDatabase);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });*/

            /*if(!incomes.getText().toString().isEmpty() && !expenses.getText().toString().isEmpty())
                savings.setText(String.valueOf(Integer.parseInt(incomes.getText().toString())-Integer.parseInt(expenses.getText().toString())));
            else if(!expenses.getText().toString().isEmpty())
            {
                savings.setText("0");
                expenses.setText("0");
            }
            else if(!incomes.getText().toString().isEmpty())
            {
                incomes.setText("0");
                savings.setText("0");
            }*/



            /*try
            {
                //savings.setText(String.valueOf(Integer.parseInt(incomes.getText().toString())-Integer.parseInt(expenses.getText().toString())));
                savings.setText(incomes.getText().toString());
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Cannot set savings", Toast.LENGTH_SHORT).show();
            }*/







        }
    }
}