package com.example.EconomyManager.ApplicationPart;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class ActivityAddMoney extends AppCompatActivity
{

    private Button saveChanges, cancel;
    private FirebaseAuth fbAuth;
    private DatabaseReference myRef;
    private RadioGroup radioGroup;
    private EditText note, value;
    private RadioButton radioButton;
    private TextView error;
    private RadioButton[] radioButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        setVariables();
        createRadioButtons();
        setOnClickListeners();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setVariables()
    {
        saveChanges=findViewById(R.id.addMoneySaveButton);
        fbAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        radioGroup=findViewById(R.id.addMoneyRadioGroup);
        note=findViewById(R.id.addMoneyNote);
        value=findViewById(R.id.addMoneyValue);
        error=findViewById(R.id.addMoneyError);
        cancel=findViewById(R.id.addMoneyCancelButton);
        radioButton1=new RadioButton[4];
    }

    private void createRadioButtons()
    {
        ArrayList<String> buttonTextArray=new ArrayList<>();
        int ID;

        buttonTextArray.add(getResources().getString(R.string.add_money_deposits).trim());
        buttonTextArray.add(getResources().getString(R.string.add_money_independent_sources).trim());
        buttonTextArray.add(getResources().getString(R.string.salary).trim());
        buttonTextArray.add(getResources().getString(R.string.saving).trim());
        Collections.sort(buttonTextArray);

        for(int i=0; i<buttonTextArray.size(); i++)
        {
            if(buttonTextArray.get(i).equals(getResources().getString(R.string.add_money_deposits).trim()))
                ID=R.id.addMoneyRadioButtonDeposits;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.add_money_independent_sources).trim()))
                ID=R.id.addMoneyRadioButtonIndependentSources;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.salary).trim()))
                ID=R.id.addMoneyRadioButtonSalary;
            else ID=R.id.addMoneyRadioButtonSaving;

            radioButton1[i]=new RadioButton(this);
            radioButton1[i].setText(buttonTextArray.get(i));
            radioButton1[i].setId(ID);
            radioButton1[i].setTextSize(18);
            radioButton1[i].setTypeface(Typeface.DEFAULT_BOLD);
            radioButton1[i].setButtonTintList(ColorStateList.valueOf(Color.WHITE));
            radioButton1[i].setTextColor(Color.WHITE);
            radioGroup.addView(radioButton1[i]);
        }
    }

    private void setOnClickListeners()
    {
        saveChanges.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int selectedID=radioGroup.getCheckedRadioButtonId();

                closeTheKeyboard();
                if(selectedID!=-1 && fbAuth.getUid()!=null) // daca a fost selectat vreun buton radio
                {
                    if(!String.valueOf(value.getText()).trim().equals(""))
                    {
                        Calendar addTime=Calendar.getInstance();
                        SimpleDateFormat currentMonth=new SimpleDateFormat("LLLL", Locale.ENGLISH);
                        String addYear=String.valueOf(addTime.get(Calendar.YEAR)), addMonth=currentMonth.format(addTime.getTime()), addDay=String.valueOf(addTime.get(Calendar.DAY_OF_MONTH)), addHour=String.valueOf(addTime.get(Calendar.HOUR_OF_DAY)), addMinute=String.valueOf(addTime.get(Calendar.MINUTE)), addSecond=String.valueOf(addTime.get(Calendar.SECOND));
                        if(Integer.parseInt(addDay)<10)
                            addDay="0"+addDay;
                        if(Integer.parseInt(addHour)<10)
                            addHour="0"+addHour;
                        if(Integer.parseInt(addMinute)<10)
                            addMinute="0"+addMinute;
                        if(Integer.parseInt(addSecond)<10)
                            addSecond="0"+addSecond;
                        String addDate=addMonth+" "+addDay+", "+addYear+" "+addHour+":"+addMinute+":"+addSecond;
                        radioButton=findViewById(selectedID);
                        BigDecimal df=BigDecimal.valueOf(Float.parseFloat(String.valueOf(value.getText()))).setScale(2, RoundingMode.UP); // nu merge pentru numerele cu exact 2 zecimale ce nu sunt .00, .25, .50, .75

                        String textOfCheckedRadioButton=String.valueOf(radioButton.getText()), expenseToBeAddedToTheDatabase;
                        if(textOfCheckedRadioButton.equals(getResources().getString(R.string.add_money_deposits).trim()))
                            expenseToBeAddedToTheDatabase="Deposits";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.add_money_independent_sources).trim()))
                            expenseToBeAddedToTheDatabase="IndependentSources";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.salary).trim()))
                            expenseToBeAddedToTheDatabase="Salary";
                        else expenseToBeAddedToTheDatabase="Saving";

                        final MoneyManager addMoney=new MoneyManager(String.valueOf(note.getText()), Float.valueOf(String.valueOf(df)), addDate, expenseToBeAddedToTheDatabase);

                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(addYear).child(addMonth).child("Incomes").child(expenseToBeAddedToTheDatabase).child(addMoney.getDate()).setValue(addMoney);
                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(addYear).child(addMonth).child("Incomes").addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {

                                Calendar addTime1=Calendar.getInstance();
                                SimpleDateFormat currentMonth1=new SimpleDateFormat("LLLL", Locale.ENGLISH);

                                if(snapshot.hasChild("Overall")) // daca exista un nod cu numele Overall
                                {
                                    String oldOverall;
                                    if(!String.valueOf(snapshot.child("Overall").getValue()).trim().equals("")) // daca acest nod are o valoare
                                    {
                                        oldOverall=String.valueOf(snapshot.child("Overall").getValue());
                                        oldOverall=String.valueOf(Float.parseFloat(oldOverall)+addMoney.getValue());
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(addTime1.get(Calendar.YEAR))).child(currentMonth1.format(addTime1.getTime())).child("Incomes").child("Overall").setValue(oldOverall);
                                    }
                                }
                                else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(addTime1.get(Calendar.YEAR))).child(currentMonth1.format(addTime1.getTime())).child("Incomes").child("Overall").setValue(String.valueOf(addMoney.getValue()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {

                            }
                        });
                        Toast.makeText(ActivityAddMoney.this, getResources().getString(R.string.income)+" "+getResources().getString(R.string.add_money_added_successfully), Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                    else error.setText(R.string.money_error4);
                }
                else error.setText(R.string.money_error1);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }

    private void closeTheKeyboard()
    {
        View v=this.getCurrentFocus();
        if(v!=null)
        {
            InputMethodManager manager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}