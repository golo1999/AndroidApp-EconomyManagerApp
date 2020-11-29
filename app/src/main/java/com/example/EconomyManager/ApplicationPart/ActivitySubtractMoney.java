package com.example.EconomyManager.ApplicationPart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class ActivitySubtractMoney extends AppCompatActivity
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
        setContentView(R.layout.activity_subtract_money);
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
        saveChanges=findViewById(R.id.subtractMoneySaveButton);
        fbAuth=FirebaseAuth.getInstance();
        myRef= FirebaseDatabase.getInstance().getReference();
        radioGroup=findViewById(R.id.subtractMoneyRadioGroup);
        note=findViewById(R.id.subtractMoneyNote);
        value=findViewById(R.id.subtractMoneyValue);
        error=findViewById(R.id.subtractMoneyError);
        cancel=findViewById(R.id.subtractMoneyCancelButton);
        radioButton1=new RadioButton[15];
    }

    private void createRadioButtons()
    {
        ArrayList<String> buttonTextArray=new ArrayList<>();
        int ID;
        buttonTextArray.add(getResources().getString(R.string.subtract_money_bills).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_car).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_clothes).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_communications).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_eating_out).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_entertainment).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_food).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_gifts).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_health).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_house).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_pets).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_sports).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_taxi).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_toiletry).trim());
        buttonTextArray.add(getResources().getString(R.string.subtract_money_transport).trim());
        Collections.sort(buttonTextArray);
        for(int i=0; i<buttonTextArray.size(); i++)
        {
            if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_bills).trim()))
                ID=R.id.subtractMoneyRadioButtonBills;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_car).trim()))
                ID=R.id.subtractMoneyRadioButtonCar;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_clothes).trim()))
                ID=R.id.subtractMoneyRadioButtonClothes;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_communications).trim()))
                ID=R.id.subtractMoneyRadioButtonCommunications;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_eating_out).trim()))
                ID=R.id.subtractMoneyRadioButtonEatingOut;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_entertainment).trim()))
                ID=R.id.subtractMoneyRadioButtonEntertainment;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_food).trim()))
                ID=R.id.subtractMoneyRadioButtonFood;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_gifts).trim()))
                ID=R.id.subtractMoneyRadioButtonGifts;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_health).trim()))
                ID=R.id.subtractMoneyRadioButtonHealth;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_house).trim()))
                ID=R.id.subtractMoneyRadioButtonHouse;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_pets).trim()))
                ID=R.id.subtractMoneyRadioButtonPets;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_sports).trim()))
                ID=R.id.subtractMoneyRadioButtonSports;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_taxi).trim()))
                ID=R.id.subtractMoneyRadioButtonTaxi;
            else if(buttonTextArray.get(i).equals(getResources().getString(R.string.subtract_money_toiletry).trim()))
                ID=R.id.subtractMoneyRadioButtonToiletry;
            else ID=R.id.subtractMoneyRadioButtonTransport;

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
                        if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_bills).trim()))
                            expenseToBeAddedToTheDatabase="Bills";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_car).trim()))
                            expenseToBeAddedToTheDatabase="Car";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_clothes).trim()))
                            expenseToBeAddedToTheDatabase="Clothes";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_communications).trim()))
                            expenseToBeAddedToTheDatabase="Communications";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_eating_out).trim()))
                            expenseToBeAddedToTheDatabase="EatingOut";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_entertainment).trim()))
                            expenseToBeAddedToTheDatabase="Entertainment";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_food).trim()))
                            expenseToBeAddedToTheDatabase="Food";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_gifts).trim()))
                            expenseToBeAddedToTheDatabase="Gifts";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_health).trim()))
                            expenseToBeAddedToTheDatabase="Health";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_house).trim()))
                            expenseToBeAddedToTheDatabase="House";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_pets).trim()))
                            expenseToBeAddedToTheDatabase="Pets";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_sports).trim()))
                            expenseToBeAddedToTheDatabase="Sports";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_taxi).trim()))
                            expenseToBeAddedToTheDatabase="Taxi";
                        else if(textOfCheckedRadioButton.equals(getResources().getString(R.string.subtract_money_toiletry).trim()))
                            expenseToBeAddedToTheDatabase="Toiletry";
                        else expenseToBeAddedToTheDatabase="Transport";
                        final MoneyManager addMoney=new MoneyManager(String.valueOf(note.getText()), Float.valueOf(String.valueOf(df)), addDate, expenseToBeAddedToTheDatabase);

                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(addYear).child(addMonth).child("Expenses").child(expenseToBeAddedToTheDatabase).child(addMoney.getDate()).setValue(addMoney);
                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(addYear).child(addMonth).child("Expenses").addListenerForSingleValueEvent(new ValueEventListener()
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
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(addTime1.get(Calendar.YEAR))).child(currentMonth1.format(addTime1.getTime())).child("Expenses").child("Overall").setValue(oldOverall);
                                    }
                                }
                                else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(addTime1.get(Calendar.YEAR))).child(currentMonth1.format(addTime1.getTime())).child("Expenses").child("Overall").setValue(String.valueOf(addMoney.getValue()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {

                            }
                        });
                        Toast.makeText(ActivitySubtractMoney.this, getResources().getString(R.string.expense)+" "+getResources().getString(R.string.add_money_added_successfully), Toast.LENGTH_SHORT).show();
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