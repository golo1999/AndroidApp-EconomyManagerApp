package com.example.economy_manager.main_part.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.economy_manager.model.MoneyManager;
import com.example.economy_manager.R;
import com.example.economy_manager.utility.Types;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class ActivityEditSpecificTransaction extends AppCompatActivity
{
    private TextView title, noteText, valueText, dateText, typeText;
    private EditText note, value, date;
    private Spinner type;
    private ImageView goBack;
    private Button saveChanges;
    private Bundle extras;
    private FirebaseAuth fbAuth;
    private DatabaseReference myRef;
    private static final int REQUEST_CODE=1;
    private char[] transactionModifiedQueue=new char[4];

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_specific_transaction);
        setVariables();
        setTheme();
        setOnClickListeners();
        setOnFocusChangeListener();
        setTitle();
        createTransactionTypesSpinner();
        setHints();
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
        extras=getIntent().getExtras();
        goBack=findViewById(R.id.editSpecificTransactionBack);
        title=findViewById(R.id.editSpecificTransactionTitle);
        note=findViewById(R.id.editSpecificTransactionNoteEdit);
        value=findViewById(R.id.editSpecificTransactionValueEdit);
        date=findViewById(R.id.editSpecificTransactionDateEdit);
        type=findViewById(R.id.editSpecificTransactionTypeSpinner);
        saveChanges=findViewById(R.id.editSpecificTransactionSave);
        fbAuth=FirebaseAuth.getInstance();
        myRef=FirebaseDatabase.getInstance().getReference();
        dateText=findViewById(R.id.editSpecificTransactionDateText);
        valueText=findViewById(R.id.editSpecificTransactionValueText);
        noteText=findViewById(R.id.editSpecificTransactionNoteText);
        typeText=findViewById(R.id.editSpecificTransactionTypeText);

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

        date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                Intent intent=new Intent(ActivityEditSpecificTransaction.this, ActivityTimeAndDatePicker.class);
                if(extras!=null)
                    intent.putExtra("initialDate", String.valueOf(extras.getString("date")));
                startActivityForResult(intent, REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        saveChanges.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeTheKeyboard();

                if(extras!=null)
                {
                    final String noteHint=String.valueOf(extras.getString("note")), valueHint=String.valueOf(extras.getString("value")), dateHint=String.valueOf(extras.getString("date")), typeHint=String.valueOf(extras.getString("type"));
                    float valueChanged=-1;
                    final MoneyManager money=new MoneyManager(noteHint, Float.parseFloat(valueHint), dateHint, typeHint);
                    boolean noteModified=false, valueModified=false, dateModified=false, typeModified=false;
                    String incomeOrExpense;

                    switch(typeHint)
                    {
                        case "Deposits":
                        case "IndependentSources":
                        case "Salary":
                        case "Saving":
                            incomeOrExpense="Incomes";
                            break;
                        default:
                            incomeOrExpense="Expenses";
                            break;
                    }

//                    if(!noteHint.trim().equals("") && !String.valueOf(note.getText()).trim().equals(noteHint))
//                    {
//
//                    }
//                    else if()
//                    {
//
//                    }
                    if(!String.valueOf(note.getText()).trim().equals(noteHint) && !String.valueOf(note.getText()).trim().equals(""))
                        noteModified=true;
                    if(!String.valueOf(value.getText()).trim().equals("") && !String.valueOf(Float.parseFloat(String.valueOf(value.getText()))).trim().equals(valueHint))
                    {
                        valueChanged=Float.parseFloat(String.valueOf(value.getText()).trim());
                        valueModified=true;
                    }
                    if(!Locale.getDefault().getDisplayLanguage().equals("English"))
                    {
                        if(!String.valueOf(date.getHint()).equals(getTranslatedDate(dateHint)))
                            dateModified=true;
                        if(!String.valueOf(type.getSelectedItem()).equals(extras.getString("type")))
                            typeModified=true;
                    }
                    else
                    {
                        if(!String.valueOf(date.getHint()).equals(dateHint))
                            dateModified=true;
                        if(!String.valueOf(Types.getTypeInEnglish(ActivityEditSpecificTransaction.this, String.valueOf(type.getSelectedItem()))).equals(extras.getString("type")))
                            typeModified=true;
                    }

                    if(noteModified || valueModified || dateModified || typeModified)
                    {
                        setQueue(transactionModifiedQueue, noteModified, valueModified, dateModified, typeModified);
                        //Toast.makeText(ActivityEditSpecificTransaction.this, String.valueOf(transactionModifiedQueue), Toast.LENGTH_SHORT).show();

                        if(fbAuth.getUid()!=null)
                        {
                            if(noteModified)
                            money.setNote(String.valueOf(note.getText()).trim());
                            if(valueModified)
                                money.setValue(valueChanged);
                            if(dateModified)
                                money.setDate(String.valueOf(date.getHint()));
                            if(typeModified)
                                if(!Locale.getDefault().getDisplayLanguage().equals("English"))
                                    money.setType(Types.getTypeInEnglish(ActivityEditSpecificTransaction.this, String.valueOf(type.getSelectedItem())));
                                else money.setType(String.valueOf(type.getSelectedItem()));

                            switch(String.valueOf(transactionModifiedQueue))
                            {
                                case "T000":
                                    break;
                                case "D000":
                                    break;
                                case "TD00":
                                    break;
                                case "V000":
                                    break;
                                case "TV00":
                                    break;
                                case "DV00":
                                    break;
                                case "TDV0":
                                    break;
                                case "N000":
                                    modifyNoteDatabase(money, incomeOrExpense);
                                    //myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(money.getDate())).child(incomeOrExpense).child(money.getType()).child(money.getDate()).child("note").setValue(money.getNote());
                                    break;
                                case "TN00":
                                    break;
                                case "DN00":
                                    break;
                                case "TDN0":
                                    break;
                                case "TDVN":
                                    break;
                            }

                            if(valueModified)
                            { // trebuie modificata valoarea tranzactiei, dar si overall (daca e cazul)
                                //money.setValue(Float.parseFloat(String.valueOf(value.getText()).trim()));
                                if(getIncomeOrExpense(typeHint).equals(getIncomeOrExpense(money.getType()))) // daca vechiul tip de tranzactie e la fel ca si noul (ex: income=income)
                                {

                                }
                                else
                                {

                                }
                                //myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(money.getDate())).child(incomeOrExpense).child(money.getType()).child(money.getDate()).child("note").setValue(money.getNote());
                            }
                        }

//                        if(noteModified)
//                            money.setNote(String.valueOf(note.getText()).trim());
//                        if(valueModified)
//                            money.setValue(valueChanged);
//                        if(dateModified)
//                            money.setDate(String.valueOf(date.getHint()));
//                        if(typeModified)
//                            if(!Locale.getDefault().getDisplayLanguage().equals("English"))
//                                money.setType(Types.getTypeInEnglish(ActivityEditSpecificTransaction.this, String.valueOf(type.getSelectedItem())));
//                            else money.setType(String.valueOf(type.getSelectedItem()));
//
//                        if(fbAuth.getUid()!=null)
//                        {
//                            //Toast.makeText(ActivityEditSpecificTransaction.this, noteHint+"\n"+valueHint+"\n"+dateHint+"\n"+typeHint+"\n\n"+money.getNote()+"\n"+money.getValue()+"\n"+money.getDate()+"\n"+money.getType(), Toast.LENGTH_LONG).show();
//                            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(getMonthFromDate(dateHint))).child(getIncomeOrExpense(typeHint)).child(typeHint).child(dateHint).removeValue(); // stergem din vechea locatie
//                            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(getMonthFromDate(money.getDate()))).child(getIncomeOrExpense(money.getType())).child(money.getType()).child(money.getDate()).setValue(money); // adaugam in noua locatie
//                            myRef.child(fbAuth.getUid()).child("PersonalTransactions").addListenerForSingleValueEvent(new ValueEventListener() // actualizam/stergem ovr din vechea locatie, precum si din noua locatie
//                            {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot)
//                                {
//                                    if(snapshot.hasChild(String.valueOf(getYearFromDate(dateHint))))
//                                        if(snapshot.child(String.valueOf(getYearFromDate(dateHint))).hasChild(getMonthFromDate(dateHint)))
//                                            if(snapshot.child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(dateHint)).hasChild(getIncomeOrExpense(typeHint)))
//                                                if(snapshot.child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(dateHint)).child(getIncomeOrExpense(typeHint)).hasChild("Overall"))
//                                                {
//                                                    float overall=Float.parseFloat(String.valueOf(snapshot.child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(dateHint)).child(getIncomeOrExpense(typeHint)).child("Overall").getValue()));
//                                                    overall-=Float.parseFloat(valueHint);
//                                                    if(overall>0f)
//                                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(dateHint)).child(getIncomeOrExpense(typeHint)).child("Overall").setValue(String.valueOf(overall));
//                                                    else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(dateHint)).child(getIncomeOrExpense(typeHint)).child("Overall").removeValue();
//                                                    Toast.makeText(ActivityEditSpecificTransaction.this, "overall in vechea locatie: "+overall, Toast.LENGTH_SHORT).show();
//                                                }
//
//                                    if(snapshot.hasChild(String.valueOf(getYearFromDate(money.getDate()))))
//                                        if(snapshot.child(String.valueOf(getYearFromDate(money.getDate()))).hasChild(getMonthFromDate(money.getDate())))
//                                            if(snapshot.child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(money.getDate())).hasChild(getIncomeOrExpense(money.getType())))
//                                            {
//                                                float overall;
//
//                                                if(snapshot.child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(money.getDate())).child(getIncomeOrExpense(money.getType())).hasChild("Overall"))
//                                                {
//                                                    overall=Float.parseFloat(String.valueOf(snapshot.child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(money.getDate())).child(getIncomeOrExpense(money.getType())).child("Overall").getValue()));
//                                                    Toast.makeText(ActivityEditSpecificTransaction.this, "overall in noua locatie inainte de adaugare: "+overall, Toast.LENGTH_SHORT).show();
//                                                    overall+=money.getValue();
//                                                }
//                                                else overall=money.getValue(); // daca nu exista overall in noua locatie
//                                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(money.getDate())).child(getIncomeOrExpense(money.getType())).child("Overall").setValue(String.valueOf(overall));
//                                                Toast.makeText(ActivityEditSpecificTransaction.this, "overall in noua locatie dupa adaugare: "+overall, Toast.LENGTH_SHORT).show();
//                                            }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error)
//                                {
//
//                                }
//                            });
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//                            /*if(getMonthFromDate(dateHint).equals(getMonthFromDate(money.getDate())) && getYearFromDate(dateHint)==getYearFromDate(money.getDate())) // daca luna e aceeasi si acelasi an
//                            {
//                                String oldType=getIncomeOrExpense(typeHint), newType=getIncomeOrExpense(money.getType());
//
//                                if(oldType.equals(newType)) // daca nu am schimbat income/expense: adica vechiul e income si noul e tot income -> merge perfect
//                                {
//                                    try
//                                    {
//                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(getMonthFromDate(dateHint))).child(oldType).child(typeHint).child(dateHint).removeValue(); // stergere
//                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(getMonthFromDate(money.getDate()))).child(newType).child(money.getType()).child(money.getDate()).setValue(money); // adaugare
//                                    }
//                                    catch(NullPointerException e)
//                                    {
//                                        Toast.makeText(ActivityEditSpecificTransaction.this, getResources().getString(R.string.delete_error), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                                else // daca am schimbat income/expense: adica vechiul e income si noul e expense
//                                {
//                                    myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(getMonthFromDate(dateHint))).child(getIncomeOrExpense(incomeOrExpense)).child(typeHint).child(dateHint).removeValue(); // stergere
//                                    myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(getMonthFromDate(dateHint))).child(getIncomeOrExpense(incomeOrExpense)).addListenerForSingleValueEvent(new ValueEventListener()
//                                    {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot)
//                                        {
//                                            if(snapshot.hasChild("Overall"))
//                                            {
//                                                float overall=Float.parseFloat(String.valueOf(snapshot.child("Overall").getValue()));
//                                                overall-=Float.parseFloat(valueHint);
//                                                if(overall>0f)
//                                                    myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(getMonthFromDate(dateHint))).child(getIncomeOrExpense(typeHint)).child("Overall").setValue(String.valueOf(Float.valueOf(overall)));
//                                                else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(getMonthFromDate(dateHint))).child(getIncomeOrExpense(typeHint)).child("Overall").removeValue();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error)
//                                        {
//
//                                        }
//                                    });
//
//                                    myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(getMonthFromDate(money.getDate()))).child(getIncomeOrExpense(money.getType())).child(money.getType()).child(money.getDate()).setValue(money); // adaugare
//                                    myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(getMonthFromDate(money.getDate()))).child(getIncomeOrExpense(money.getType())).addListenerForSingleValueEvent(new ValueEventListener()
//                                    {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot)
//                                        {
//                                            if(snapshot.hasChild("Overall"))
//                                            {
//                                                float overall=Float.parseFloat(String.valueOf(snapshot.child("Overall").getValue()));
//                                                overall+=money.getValue();
//                                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(getMonthFromDate(money.getDate()))).child(getIncomeOrExpense(money.getType())).child("Overall").setValue(String.valueOf(Float.valueOf(overall)));
//                                            }
//                                            else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(getMonthFromDate(money.getDate()))).child(getIncomeOrExpense(money.getType())).child("Overall").setValue(String.valueOf(money.getValue()));
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error)
//                                        {
//
//                                        }
//                                    });
//                                }
//                            }
//                            else // daca nu este aceeasi luna si acelasi an
//                            {
//                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(getMonthFromDate(dateHint))).child(getIncomeOrExpense(incomeOrExpense)).child(typeHint).child(dateHint).removeValue(); // stergere
//                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(getMonthFromDate(dateHint))).child(getIncomeOrExpense(incomeOrExpense)).addListenerForSingleValueEvent(new ValueEventListener()
//                                {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot)
//                                    {
//                                        if(snapshot.hasChild("Overall"))
//                                        {
//                                            float overall=Float.parseFloat(String.valueOf(snapshot.child("Overall").getValue()));
//                                            overall-=Float.parseFloat(valueHint);
//                                            if(overall>0f)
//                                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(getMonthFromDate(dateHint))).child(getIncomeOrExpense(typeHint)).child("Overall").setValue(String.valueOf(Float.valueOf(overall)));
//                                            else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(dateHint))).child(getMonthFromDate(getMonthFromDate(dateHint))).child(getIncomeOrExpense(typeHint)).child("Overall").removeValue();
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error)
//                                    {
//
//                                    }
//                                });
//
//                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(getMonthFromDate(money.getDate()))).child(getIncomeOrExpense(money.getType())).child(money.getType()).child(money.getDate()).setValue(money); // adaugare
//                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(getMonthFromDate(money.getDate()))).child(getIncomeOrExpense(money.getType())).addListenerForSingleValueEvent(new ValueEventListener()
//                                {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot)
//                                    {
//                                        if(snapshot.hasChild("Overall"))
//                                        {
//                                            float overall=Float.parseFloat(String.valueOf(snapshot.child("Overall").getValue()));
//                                            overall+=money.getValue();
//                                            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(getMonthFromDate(money.getDate()))).child(getIncomeOrExpense(money.getType())).child("Overall").setValue(String.valueOf(Float.valueOf(overall)));
//                                        }
//                                        else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(getMonthFromDate(money.getDate()))).child(getIncomeOrExpense(money.getType())).child("Overall").setValue(String.valueOf(money.getValue()));
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error)
//                                    {
//
//                                    }
//                                });
//                            }*/
//                        }
                    }
                    onBackPressed();










                    /*if(noteModified || valueModified || dateModified || typeModified) // daca s-au facut modificari
                    {
                        if(noteModified)
                            if(fbAuth.getUid()!=null)
                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(dateSplitIntoWords[3]).child(dateSplitIntoWords[0]).child(incomeOrExpense).child(typeHint).child(dateHint).child("note").setValue(noteChanged);
                        if(valueModified)
                            if(fbAuth.getUid()!=null)
                            {
                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(dateSplitIntoWords[3]).child(dateSplitIntoWords[0]).child(incomeOrExpense).child(typeHint).child(dateHint).child("value").setValue(valueChanged);
                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(dateSplitIntoWords[3]).child(dateSplitIntoWords[0]).child(incomeOrExpense).addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        if(snapshot.exists())
                                            if(snapshot.hasChild("Overall"))
                                            {
                                                float overall=Float.parseFloat(String.valueOf(snapshot.child("Overall").getValue()));
                                                overall+=getDifferenceBetweenValues(Float.parseFloat(copyOfValueHint), copyOfValueChanged);
                                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child("Overall").setValue(String.valueOf(overall));
                                            }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error)
                                    {

                                    }
                                });
                            }
                        if(dateModified) // trebuie luat pe cazuri: daca am schimbat anul, luna sau ziua
                        {
                            if(fbAuth.getUid()!=null)
                            {
                                String newDate=String.valueOf(date.getHint());
                                money.setDate(newDate);
                                final String[] newDateSplit=newDate.split("\\W");
                                if(!newDateSplit[3].equals(dateSplitIntoWords[3])) // daca am schimbat anul
                                {
                                    final String copyOfNewYear=newDateSplit[3];
                                    if(!newDateSplit[0].equals(dateSplitIntoWords[0])) // daca am schimbat luna
                                    {
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfNewYear).child(newDateSplit[0]).child(copyOfIncomeOrExpense).child(typeHint).child(newDate).setValue(money);
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfNewYear).child(newDateSplit[0]).child(copyOfIncomeOrExpense).addListenerForSingleValueEvent(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                if(snapshot.exists())
                                                    if(snapshot.hasChild("Overall"))
                                                    {
                                                        String oldOverall;
                                                        if(!String.valueOf(snapshot.child("Overall").getValue()).equals("")) // daca acest nod are o valoare
                                                        {
                                                            oldOverall=String.valueOf(snapshot.child("Overall").getValue());
                                                            oldOverall=String.valueOf(Float.parseFloat(oldOverall)+money.getValue());
                                                            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfNewYear).child(newDateSplit[0]).child(copyOfIncomeOrExpense).child("Overall").setValue(oldOverall);
                                                        }
                                                    }
                                                    else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfNewYear).child(newDateSplit[0]).child(copyOfIncomeOrExpense).child("Overall").setValue(String.valueOf(money.getValue()));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error)
                                            {

                                            }
                                        });
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child(typeHint).child(dateHint).removeValue();
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).addListenerForSingleValueEvent(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                if(snapshot.exists())
                                                    if(snapshot.hasChild("Overall"))
                                                    {
                                                        float overall=Float.parseFloat(String.valueOf(snapshot.child("Overall").getValue()));
                                                        overall-=money.getValue();
                                                        if(overall==0f || overall<0f)
                                                            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child("Overall").removeValue();
                                                        else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child("Overall").setValue(String.valueOf(overall));
                                                    }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error)
                                            {

                                            }
                                        });
                                    }
                                    else // daca nu am schimbat luna -> merge perfect
                                    {
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfNewYear).child(dateSplitIntoWords[0]).child(copyOfIncomeOrExpense).child(typeHint).child(newDate).setValue(money);
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfNewYear).child(dateSplitIntoWords[0]).child(copyOfIncomeOrExpense).addListenerForSingleValueEvent(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                if(snapshot.exists())
                                                    if(snapshot.hasChild("Overall"))
                                                    {
                                                        String oldOverall;
                                                        if(!String.valueOf(snapshot.child("Overall").getValue()).equals("")) // daca acest nod are o valoare
                                                        {
                                                            oldOverall=String.valueOf(snapshot.child("Overall").getValue());
                                                            oldOverall=String.valueOf(Float.parseFloat(oldOverall)+money.getValue());
                                                            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfNewYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child("Overall").setValue(oldOverall);
                                                        }
                                                    }
                                                    else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfNewYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child("Overall").setValue(String.valueOf(money.getValue()));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error)
                                            {

                                            }
                                        });
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child(typeHint).child(dateHint).removeValue();
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).addListenerForSingleValueEvent(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                if(snapshot.exists())
                                                    if(snapshot.hasChild("Overall"))
                                                    {
                                                        float overall=Float.parseFloat(String.valueOf(snapshot.child("Overall").getValue()));
                                                        overall-=money.getValue();
                                                        if(overall==0f || overall<0f)
                                                            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child("Overall").removeValue();
                                                        else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child("Overall").setValue(String.valueOf(overall));
                                                    }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error)
                                            {

                                            }
                                        });
                                    }
                                }
                                else // daca nu am schimbat anul -> cazul merge perfect
                                {
                                    final String copyOfNewMonth=newDateSplit[0];
                                    if(!newDateSplit[0].equals(dateSplitIntoWords[0])) // daca am schimbat luna
                                    {
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(dateSplitIntoWords[3]).child(newDateSplit[0]).child(copyOfIncomeOrExpense).child(typeHint).child(newDate).setValue(money);
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(dateSplitIntoWords[3]).child(newDateSplit[0]).child(copyOfIncomeOrExpense).addListenerForSingleValueEvent(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                if(snapshot.exists())
                                                    if(snapshot.hasChild("Overall"))
                                                    {
                                                        String oldOverall;
                                                        if(!String.valueOf(snapshot.child("Overall").getValue()).equals("")) // daca acest nod are o valoare
                                                        {
                                                            oldOverall=String.valueOf(snapshot.child("Overall").getValue());
                                                            oldOverall=String.valueOf(Float.parseFloat(oldOverall)+money.getValue());
                                                            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfNewMonth).child(copyOfIncomeOrExpense).child("Overall").setValue(oldOverall);
                                                        }
                                                    }
                                                    else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfNewMonth).child(copyOfIncomeOrExpense).child("Overall").setValue(String.valueOf(money.getValue()));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error)
                                            {

                                            }
                                        });
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(dateSplitIntoWords[3]).child(dateSplitIntoWords[0]).child(copyOfIncomeOrExpense).addListenerForSingleValueEvent(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                if(snapshot.exists())
                                                    if(snapshot.hasChild("Overall"))
                                                    {
                                                        {
                                                            float overall=Float.parseFloat(String.valueOf(snapshot.child("Overall").getValue()));
                                                            overall-=money.getValue();
                                                            if(overall==0f || overall<0f)
                                                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child("Overall").removeValue();
                                                            else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child("Overall").setValue(String.valueOf(overall));
                                                        }
                                                    }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error)
                                            {

                                            }
                                        });
                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(dateSplitIntoWords[3]).child(dateSplitIntoWords[0]).child(copyOfIncomeOrExpense).child(typeHint).child(dateHint).removeValue();
                                    }
                                    else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(dateSplitIntoWords[3]).child(dateSplitIntoWords[0]).child(copyOfIncomeOrExpense).child(typeHint).child(newDate).setValue(money); // daca nu am schimbat luna
                                    myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(dateSplitIntoWords[3]).child(dateSplitIntoWords[0]).child(copyOfIncomeOrExpense).child(typeHint).child(dateHint).removeValue();
                                }
                            }
                        }
                        if(typeModified) // merge perfect in cazul in care trecem de la incomes la expenses (sau invers); nu si de la expenses la expenses si de la incomes la incomes!
                        {
                            if(fbAuth.getUid()!=null)
                            {
                                String newIncomeOrExpense;
                                money.setType(typeChanged);

                                switch(typeChanged)
                                {
                                    case "Deposits":
                                    case "IndependentSources":
                                    case "Salary":
                                    case "Saving":
                                        newIncomeOrExpense="Incomes";
                                        break;
                                    default:
                                        newIncomeOrExpense="Expenses";
                                        break;
                                }

                                final String copyOfNewIncomeOrExpense=newIncomeOrExpense;

                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfNewIncomeOrExpense).child(typeChanged).child(dateHint).setValue(money); // adaugarea obiectului la noul type - merge perfect
                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfNewIncomeOrExpense).addListenerForSingleValueEvent(new ValueEventListener() // actualizarea overall la noul type
                                {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        if(snapshot.exists())
                                        {
                                            if(snapshot.hasChild("Overall"))
                                            {
                                                float overall=Float.parseFloat(String.valueOf(snapshot.child("Overall").getValue()));
                                                overall+=Float.parseFloat(copyOfValueHint);
                                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfNewIncomeOrExpense).child("Overall").setValue(String.valueOf(overall));
                                            }
                                            else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfNewIncomeOrExpense).child("Overall").setValue(String.valueOf(money.getValue()));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error)
                                    {

                                    }
                                });
                                if(copyOfNewIncomeOrExpense.equals(copyOfIncomeOrExpense)) // in cazul in care noul type si vechiul type sunt identice (conditia e buna), stergem doar obiectul si lasam overall asa cum este -> nu merge (adauga de 2 ori)
                                {
                                    //myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child(typeHint).child(dateHint).removeValue();
                                }
                                else // cazul merge perfect
                                {
                                    myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child(typeHint).child(dateHint).removeValue(); // aici stergem obiectul cu totul - merge perfect
                                    myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).addListenerForSingleValueEvent(new ValueEventListener() // aici modificam overall - merge perfect
                                    {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot)
                                        {
                                            if(snapshot.exists())
                                                if(snapshot.hasChild("Overall"))
                                                {
                                                    float overall=Float.parseFloat(String.valueOf(snapshot.child("Overall").getValue()));
                                                    overall-=Float.parseFloat(copyOfValueHint);
                                                    if(overall==0f || overall<0f)
                                                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child("Overall").removeValue();
                                                    else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(copyOfYear).child(copyOfMonth).child(copyOfIncomeOrExpense).child("Overall").setValue(String.valueOf(overall));
                                                }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error)
                                        {

                                        }
                                    });
                                }
                            }
                        }
                        Toast.makeText(ActivityEditSpecificTransaction.this, "Changes saved", Toast.LENGTH_SHORT).show();
                    }
                    onBackPressed();*/
                }
            }
        });

        //type.setOnItemSelectedListener(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE && resultCode==Activity.RESULT_OK)
        {
            if(data!=null)
            {
                Bundle extras=data.getExtras();
                if(extras!=null)
                {
                    String modifiedDate=extras.getString("modifiedDate");
                    if(modifiedDate!=null)
                        if(!getTranslatedDate(modifiedDate).equals(String.valueOf(date.getHint())))
                            if(!Locale.getDefault().getDisplayLanguage().equals("English"))
                                date.setHint(getTranslatedDate(modifiedDate));
                            else date.setHint(modifiedDate);
                }
            }
        }
    }

    private void setTitle()
    {
        String text=getResources().getString(R.string.edit_specific_transaction_title);
        title.setText(text.trim());
        title.setTextColor(Color.WHITE);
        title.setTextSize(18);
    }

    private void setHints()
    {
        if(extras!=null)
        {
            String noteText=extras.getString("note"), valueText=extras.getString("value"), dateText=extras.getString("date"), typeText=extras.getString("type");
            note.setHint(noteText);
            value.setHint(valueText);
            if(dateText!=null)
                if(!Locale.getDefault().getDisplayLanguage().equals("English"))
                    date.setHint(getTranslatedDate(dateText));
                else date.setHint(dateText);
            if(typeText!=null)
            {
                String translatedType=Types.getTranslatedType(ActivityEditSpecificTransaction.this, typeText);
                int positionInTheTransactionTypesList=-1;
                ArrayList<String> transactionTypesList=new ArrayList<>();

                if(!transactionTypesList.isEmpty())
                    transactionTypesList.clear();

                transactionTypesList.add(getResources().getString(R.string.add_money_deposits).trim());
                transactionTypesList.add(getResources().getString(R.string.add_money_independent_sources).trim());
                transactionTypesList.add(getResources().getString(R.string.salary).trim());
                transactionTypesList.add(getResources().getString(R.string.saving).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_bills).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_car).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_clothes).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_communications).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_eating_out).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_entertainment).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_food).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_gifts).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_health).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_house).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_pets).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_sports).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_taxi).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_toiletry).trim());
                transactionTypesList.add(getResources().getString(R.string.subtract_money_transport).trim());

                Collections.sort(transactionTypesList);

//                switch(typeText)
//                {
//                    case "Deposits":
//                        translatedType=getResources().getString(R.string.add_money_deposits);
//                        break;
//                    case "IndependentSources":
//                        translatedType=getResources().getString(R.string.add_money_independent_sources);
//                        break;
//                    case "Salary":
//                        translatedType=getResources().getString(R.string.salary);
//                        break;
//                    case "Saving":
//                        translatedType=getResources().getString(R.string.saving);
//                        break;
//                    case "Bills":
//                        translatedType=getResources().getString(R.string.subtract_money_bills);
//                        break;
//                    case "Car":
//                        translatedType=getResources().getString(R.string.subtract_money_car);
//                        break;
//                    case "Clothes":
//                        translatedType=getResources().getString(R.string.subtract_money_clothes);
//                        break;
//                    case "Communications":
//                        translatedType=getResources().getString(R.string.subtract_money_communications);
//                        break;
//                    case "EatingOut":
//                        translatedType=getResources().getString(R.string.subtract_money_eating_out);
//                        break;
//                    case "Entertainment":
//                        translatedType=getResources().getString(R.string.subtract_money_entertainment);
//                        break;
//                    case "Food":
//                        translatedType=getResources().getString(R.string.subtract_money_food);
//                        break;
//                    case "Gifts":
//                        translatedType=getResources().getString(R.string.subtract_money_gifts);
//                        break;
//                    case "Health":
//                        translatedType=getResources().getString(R.string.subtract_money_health);
//                        break;
//                    case "House":
//                        translatedType=getResources().getString(R.string.subtract_money_house);
//                        break;
//                    case "Pets":
//                        translatedType=getResources().getString(R.string.subtract_money_pets);
//                        break;
//                    case "Sports":
//                        translatedType=getResources().getString(R.string.subtract_money_sports);
//                        break;
//                    case "Taxi":
//                        translatedType=getResources().getString(R.string.subtract_money_taxi);
//                        break;
//                    case "Toiletry":
//                        translatedType=getResources().getString(R.string.subtract_money_toiletry);
//                        break;
//                    case "Transport":
//                        translatedType=getResources().getString(R.string.subtract_money_transport);
//                        break;
//                }

                //if(translatedType!=null)
                //{
                    for(int i=0; i<transactionTypesList.size(); i++)
                        if(transactionTypesList.get(i).equals(translatedType.trim()))
                        {
                            positionInTheTransactionTypesList=i;
                            break;
                        }
                    if(positionInTheTransactionTypesList!=-1) // aici e problema
                        type.setSelection(positionInTheTransactionTypesList); // nu merge: la independentsources apare pets
                //}
            }
        }
    }

    /*private float getDifferenceBetweenValues(float oldValue, float newValue) // metoda este perfecta
    {
        return newValue-oldValue;
    }*/

    private void closeTheKeyboard()
    {
        View v=this.getCurrentFocus();
        if(v!=null)
        {
            InputMethodManager manager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void createTransactionTypesSpinner()
    {
        ArrayList<String> types=new ArrayList<>();

        ArrayAdapter<String> typesAdapter=new ArrayAdapter<String>(this, R.layout.custom_spinner_item, types)
        {
            @Override
            public View getDropDownView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent)
            {
                final View v=super.getDropDownView(position, convertView, parent);
                ((TextView)v).setGravity(Gravity.CENTER);

                if(fbAuth.getUid()!=null)
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.hasChild("darkTheme"))
                            {
                                boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()).trim());
                                int itemsColor;

                                if(!darkThemeEnabled)
                                    itemsColor=Color.WHITE;
                                else itemsColor=Color.BLACK;

                                ((TextView)v).setTextColor(itemsColor); // setam culoarea textului elementelor in functie de tema selectata
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });

                return v;
            }
        };

        AdapterView.OnItemSelectedListener listener=new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id)
            {
                if(fbAuth.getUid()!=null)
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.hasChild("darkTheme"))
                            {
                                boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()).trim());
                                int textColor;

                                if(!darkThemeEnabled)
                                    textColor=Color.parseColor("#195190");
                                else textColor=Color.WHITE;

                                ((TextView)parent.getChildAt(0)).setTextColor(textColor);
                                ((TextView)parent.getChildAt(0)).setGravity(Gravity.START);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        };

        types.add(getResources().getString(R.string.subtract_money_bills).trim());
        types.add(getResources().getString(R.string.subtract_money_car).trim());
        types.add(getResources().getString(R.string.subtract_money_clothes).trim());
        types.add(getResources().getString(R.string.subtract_money_communications).trim());
        types.add(getResources().getString(R.string.add_money_deposits).trim());
        types.add(getResources().getString(R.string.subtract_money_eating_out).trim());
        types.add(getResources().getString(R.string.subtract_money_entertainment).trim());
        types.add(getResources().getString(R.string.subtract_money_food).trim());
        types.add(getResources().getString(R.string.subtract_money_gifts).trim());
        types.add(getResources().getString(R.string.subtract_money_health).trim());
        types.add(getResources().getString(R.string.subtract_money_house).trim());
        types.add(getResources().getString(R.string.subtract_money_pets).trim());
        types.add(getResources().getString(R.string.add_money_independent_sources).trim());
        types.add(getResources().getString(R.string.salary).trim());
        types.add(getResources().getString(R.string.saving).trim());
        types.add(getResources().getString(R.string.subtract_money_sports).trim());
        types.add(getResources().getString(R.string.subtract_money_taxi).trim());
        types.add(getResources().getString(R.string.subtract_money_toiletry).trim());
        types.add(getResources().getString(R.string.subtract_money_transport).trim());

        Collections.sort(types);

        type.setAdapter(typesAdapter);
        type.setOnItemSelectedListener(listener);
    }

    private void setTheme()
    {
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                            if(snapshot.hasChild("darkTheme"))
                            {
                                boolean darkTheme=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()).trim());
                                int color, backgroundTheme, spinnerElementBackground;

                                if(!darkTheme)
                                {
                                    color=Color.parseColor("#195190");
                                    backgroundTheme=R.drawable.ic_white_gradient_tobacco_ad;
                                    spinnerElementBackground=R.drawable.ic_blue_gradient_unloved_teen;
                                }
                                else
                                {
                                    color=Color.WHITE;
                                    spinnerElementBackground=R.drawable.ic_white_gradient_tobacco_ad;
                                    backgroundTheme=R.drawable.ic_black_gradient_night_shift;
                                }

                                setTextStyleTextView(noteText, color);
                                setTextStyleTextView(valueText, color);
                                setTextStyleTextView(dateText, color);
                                setTextStyleTextView(typeText, color);

                                setTextStyleEditText(note, color);
                                setTextStyleEditText(value, color);
                                setTextStyleEditText(date, color);

                                getWindow().setBackgroundDrawableResource(backgroundTheme);
                                type.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP); // setam culoarea sagetii
                                type.setPopupBackgroundResource(spinnerElementBackground); // setam culoarea elementelor
                            }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    private void setTextStyleTextView(TextView textView, int color)
    {
        textView.setTextColor(color);
        textView.setTextSize(18);
    }

    private void setTextStyleEditText(EditText editText, int color)
    {
        editText.setTextColor(color);
        editText.setHintTextColor(color);
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private String getTranslatedDate(String dateInEnglish)
    {
        String deviceLanguage=Locale.getDefault().getDisplayLanguage(), prefix, translatedDate;
        String[] dateInEnglishSplit=dateInEnglish.split("\\W");
        String month=getTranslatedMonth(dateInEnglishSplit[0].trim()), day=dateInEnglishSplit[1].trim(), year=dateInEnglishSplit[3].trim(), hour=dateInEnglishSplit[4].trim(), minute=dateInEnglishSplit[5].trim(), second=dateInEnglishSplit[6].trim();

        switch(deviceLanguage)
        {
            case "Deutsch":
                prefix="der";
                translatedDate=prefix+" "+day+". "+month+" "+year+", "+hour+":"+minute+":"+second;
                break;
            case "español":
                prefix="el";
                String prefix2="de";
                translatedDate=prefix+" "+day+" "+prefix2+" "+month.toLowerCase()+" "+prefix2+" "+year+", "+hour+":"+minute+":"+second;
                break;
            case "italiano":
                prefix="il";
                translatedDate=prefix+" "+day+" "+month.toLowerCase()+" "+year+", "+hour+":"+minute+":"+second;
                break;
            case "português":
                prefix="de";
                translatedDate=day+" "+prefix+" "+month.toLowerCase()+" "+prefix+" "+year+", "+hour+":"+minute+":"+second;
                break;
            case "français":
            case "română":
                translatedDate=day+" "+month.toLowerCase()+" "+year+", "+hour+":"+minute+":"+second;
                break;
            default:
                translatedDate="";
                break;
        }

        return translatedDate;
    }

    private String getTranslatedMonth(String monthNameInEnglish)
    {
        switch(monthNameInEnglish)
        {
            case "January":
                return getResources().getString(R.string.month_january);
            case "February":
                return getResources().getString(R.string.month_february);
            case "March":
                return getResources().getString(R.string.month_march);
            case "April":
                return getResources().getString(R.string.month_april);
            case "May":
                return getResources().getString(R.string.month_may);
            case "June":
                return getResources().getString(R.string.month_june);
            case "July":
                return getResources().getString(R.string.month_july);
            case "August":
                return getResources().getString(R.string.month_august);
            case "September":
                return getResources().getString(R.string.month_september);
            case "October":
                return getResources().getString(R.string.month_october);
            case "November":
                return getResources().getString(R.string.month_november);
            case "December":
                return getResources().getString(R.string.month_december);
            default:
                return "";
        }
    }

//    private String getTypeInEnglish(String translatedType)
//    {
//        if(translatedType.equals(getResources().getString(R.string.add_money_deposits)))
//            return "Deposits";
//        else if(translatedType.equals(getResources().getString(R.string.add_money_independent_sources)))
//            return "IndependentSources";
//        else if(translatedType.equals(getResources().getString(R.string.saving)))
//            return "Saving";
//        else if(translatedType.equals(getResources().getString(R.string.salary)))
//            return "Salary";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_bills)))
//            return "Bills";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_car)))
//            return "Car";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_clothes)))
//            return "Clothes";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_communications)))
//            return "Communications";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_eating_out)))
//            return "EatingOut";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_entertainment)))
//            return "Entertainment";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_food)))
//            return "Food";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_gifts)))
//            return "Gifts";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_health)))
//            return "Health";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_house)))
//            return "House";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_pets)))
//            return "Pets";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_sports)))
//            return "Sports";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_taxi)))
//            return "Taxi";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_toiletry)))
//            return "Toiletry";
//        else if(translatedType.equals(getResources().getString(R.string.subtract_money_transport)))
//            return "Transport";
//        else return null;
//    }

    private void setQueue(char[] queue, boolean noteModified, boolean valueModified, boolean dateModified, boolean typeModified)
    {
        Arrays.fill(queue, '0');

        if(typeModified)
        {
            int position=-1;
            for(char index:queue)
            {
                ++position;
                if(index=='0')
                {
                    queue[position]='T';
                    break;
                }
            }
        }
        if(dateModified)
        {
            int position=-1;
            for(char index:queue)
            {
                ++position;
                if(index=='0')
                {
                    queue[position]='D';
                    break;
                }
            }
        }
        if(valueModified)
        {
            int position=-1;
            for(char index:queue)
            {
                ++position;
                if(index=='0')
                {
                    queue[position]='V';
                    break;
                }
            }
        }
        if(noteModified)
        {
            int position=-1;
            for(char index:queue)
            {
                ++position;
                if(index=='0')
                {
                    queue[position]='N';
                    break;
                }
            }
        }
    }

    private String getFieldFromQueue(char field)
    {
        switch(field)
        {
            case 'T':
                return "type";
            case 'D':
                return "date";
            case 'V':
                return "value";
            case 'N':
                return "note";
            default:
                return null;
        }
    }

    private int getYearFromDate(String date)
    {
        String[] dateSplit=date.split("\\W");
        return Integer.parseInt(dateSplit[3].trim());
    }

    private String getMonthFromDate(String date)
    {
        String[] dateSplit=date.split("\\W");
        return dateSplit[0].trim();
    }

    private String getIncomeOrExpense(String type)
    {
        switch(type)
        {
            case "Deposits":
            case "IndependentSources":
            case "Salary":
            case "Saving":
                return "Incomes";
            default:
                return "Expenses";
        }
    }

    private void setOnFocusChangeListener()
    {
        View.OnFocusChangeListener listener=new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(hasFocus)
                    ((EditText)v).setText(((EditText) v).getHint());
            }
        };
        note.setOnFocusChangeListener(listener);
    }

    private void modifyNoteDatabase(MoneyManager money, String incomeOrExpense)
    {
        if(fbAuth.getUid()!=null)
            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(getYearFromDate(money.getDate()))).child(getMonthFromDate(money.getDate())).child(incomeOrExpense).child(money.getType()).child(money.getDate()).child("note").setValue(money.getNote());
    }
}