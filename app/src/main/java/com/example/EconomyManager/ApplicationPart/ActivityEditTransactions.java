package com.example.EconomyManager.ApplicationPart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.EconomyManager.MoneyManager;
import com.example.EconomyManager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class ActivityEditTransactions extends AppCompatActivity
{
    private ImageView goBack;
    private FirebaseAuth fbAuth=FirebaseAuth.getInstance();
    private TextView activityTitle, centerText;
    private ListView listView;
    private DatabaseReference myRef=FirebaseDatabase.getInstance().getReference();
    private ArrayList<MoneyManager> arrayList=new ArrayList<>();
    private Spinner monthSpinner, yearSpinner;
    private ConstraintLayout bottomLayout;
    private CustomAdaptor adaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transactions);
        setTheme();
        setVariables();
        setTitle();
        setBottomLayout();
        setOnClickListeners();
        childListener();
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
        goBack=findViewById(R.id.editTransactionsBack);
        listView=findViewById(R.id.editTransactionsListView);
        activityTitle=findViewById(R.id.editTransactionsTitle);
        monthSpinner=findViewById(R.id.editTransactionBottomLayoutMonthSpinner);
        yearSpinner=findViewById(R.id.editTransactionBottomLayoutYearSpinner);
        centerText=findViewById(R.id.editTransactionsCenterText);
        bottomLayout=findViewById(R.id.editTransactionBottomLayout);
        adaptor=new CustomAdaptor();
        listView.setAdapter(adaptor);
    }

    private void setTitle()
    {
        activityTitle.setText(getResources().getString(R.string.edit_transactions_title).trim());
        activityTitle.setTextSize(20);
        activityTitle.setTextColor(Color.WHITE);
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

    private void setBottomLayout()
    {
        if(fbAuth.getUid()!=null)
        {
            myRef.child(fbAuth.getUid()).child("PersonalTransactions").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.exists())
                    {
                        if(snapshot.hasChildren())
                        {
                            final ArrayList<String> yearList=new ArrayList<>();

                            for(DataSnapshot yearIterator:snapshot.getChildren())
                                yearList.add(yearIterator.getKey());
                            createYearSpinner(yearList);
                            
                            yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                            {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                {
                                    final String selectedYear=String.valueOf(yearSpinner.getItemAtPosition(position));
                                    try
                                    {
                                        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                                        ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                                    }
                                    catch(NullPointerException e)
                                    {

                                    }

                                    myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(selectedYear).addListenerForSingleValueEvent(new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot)
                                        {
                                            if(snapshot.exists())
                                                if(snapshot.hasChildren())
                                                {
                                                    ArrayList<String> monthList=new ArrayList<>();

                                                    for(DataSnapshot monthIterator:snapshot.getChildren())
                                                        monthList.add(monthIterator.getKey());
                                                    createMonthSpinner(monthList, selectedYear);

                                                    monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                                                    {
                                                        @Override
                                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                                        {
                                                            String selectedMonth=String.valueOf(monthSpinner.getItemAtPosition(position));
                                                            final String copyOfSelectedMonth=getMonthInEnglish(selectedMonth);

                                                            try
                                                            {
                                                                ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                                                                ((TextView)parent.getChildAt(0)).setGravity(Gravity.CENTER);
                                                            }
                                                            catch(NullPointerException e)
                                                            {

                                                            }
                                                            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(selectedYear).child(getMonthInEnglish(selectedMonth)).addListenerForSingleValueEvent(new ValueEventListener()
                                                            {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                                                {
                                                                    if(snapshot.exists())
                                                                        if(snapshot.hasChildren())
                                                                        {
                                                                            centerText.setText("");
                                                                            listView.setEnabled(true);
                                                                            listView.setVisibility(View.VISIBLE);
                                                                            createListView(copyOfSelectedMonth, selectedYear);
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
                                                    });
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
                            });
                        }
                    }
                    else
                    {
                        monthSpinner.setEnabled(false);
                        yearSpinner.setEnabled(false);
                        bottomLayout.setVisibility(View.GONE);
                        monthSpinner.setVisibility(View.GONE);
                        yearSpinner.setVisibility(View.GONE);
                        centerText.setText(getResources().getString(R.string.edit_transactions_center_text_no_transactions).trim());
                    }

                    if(fbAuth.getUid()!=null)
                        myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if(snapshot.hasChild("darkTheme"))
                                {
                                    boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()));
                                    if(!darkThemeEnabled)
                                        centerText.setTextColor(Color.parseColor("#195190"));
                                    else centerText.setTextColor(Color.WHITE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {

                            }
                        });

                    centerText.setTextSize(20);
                    listView.setEnabled(false);
                    listView.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
        }
    }

    private void createMonthSpinner(ArrayList<String> list, String selectedYear)
    {
        String[] months=getResources().getStringArray(R.array.months); // setam vectorul ce contine lunile in engleza
        int[] frequency=new int[13]; // vectorul de frecventa
        Calendar currentTime=Calendar.getInstance();
        SimpleDateFormat monthFormat=new SimpleDateFormat("LLLL", Locale.ENGLISH);
        int positionOfCurrentMonth=-1;

        Arrays.fill(frequency, 0); // initializam vectorul de luni cu 0 pentru toate elementele

        for(String listIterator:list) // cream vectorul de frecventa
        {
            int j=0;
            for(String monthsListIterator:months) // vedem care dintre luni se regasesc si in ArrayList-ul cu lunile din anul selectat
            {
                j++;
                if(listIterator.equals(monthsListIterator))
                    frequency[j-1]=1;
            }
        }

        list.clear(); // golim lista si vom adauga lunile in ordinea lor naturala

        int j=0;

        for(int frequencyIterator:frequency) // daca luna are frecventa 1, o adaugam in lista
        {
            j++;
            if(frequencyIterator==1)
                if(!Locale.getDefault().getDisplayLanguage().equals("English")) // daca nu e in engleza
                    list.add(getTranslatedMonth(months[j-1])); // traducem numele lunii cu frecventa 1
                else list.add(months[j-1]); // daca e in engleza
        }

        j=0;

        for(String listIterator:list) // daca gasim luna curenta in lista, ii salvam pozitia (pentru a seta Spinnerul pe luna curenta, in cazul in care exista tranzactii efectuate in ea)
        {
            j++;
            if(getMonthInEnglish(listIterator).equals(monthFormat.format(currentTime.getTime())))
                positionOfCurrentMonth=j-1;
        }

        ArrayAdapter<String> monthAdapter=new ArrayAdapter<String>(this, R.layout.custom_spinner_item, list) // cream adaptorul ce afiseaza pe ecran lunile din Spinner
        {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                final View v=super.getDropDownView(position, convertView, parent);
                ((TextView)v).setGravity(Gravity.CENTER); // toate elementele spinnerului sunt aliniate la centru

                if(fbAuth.getUid()!=null)
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
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

        monthSpinner.setAdapter(monthAdapter);
        if(positionOfCurrentMonth>=0 && selectedYear.equals(String.valueOf(currentTime.get(Calendar.YEAR)))) // daca luna curenta exista in lista si anul este cel curent
            monthSpinner.setSelection(positionOfCurrentMonth); // o setam ca default atunci cand deschidem activitatea sau cand selectam anul curent
    }

    private void createYearSpinner(ArrayList<String> list)
    {
        ArrayAdapter<String> yearAdapter=new ArrayAdapter<String>(this, R.layout.custom_spinner_item, list)
        {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
            {
                final View v=super.getDropDownView(position, convertView, parent);
                ((TextView)v).setGravity(Gravity.CENTER); // toate elementele spinnerului sunt aliniate la centru

                if(fbAuth.getUid()!=null)
                    myRef.child(fbAuth.getUid()).child("ApplicationSettings").addListenerForSingleValueEvent(new ValueEventListener()
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

        yearSpinner.setAdapter(yearAdapter);
        Calendar currentTime=Calendar.getInstance();
        int positionOfCurrentYear=-1, j=-1;

        for(String listIterator:list) // cautam in lista anul curent si, in cazul in care este gasit, ii memoram pozitia
        {
            j++;
            if(listIterator.equals(String.valueOf(currentTime.get(Calendar.YEAR))))
                positionOfCurrentYear=j;
        }
        if(positionOfCurrentYear>=0) // daca am gasit anul curent in lista, il setam ca default atunci deschidem activitatea
            yearSpinner.setSelection(positionOfCurrentYear);
    }

    private void createListView(String month, String year)
    {
        if((month!=null && year!=null))
            if(!month.isEmpty() && !year.isEmpty())
                if(fbAuth.getUid()!=null)
                {
                    myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(year).child(month).addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(!arrayList.isEmpty())
                                arrayList.clear();
                            if(snapshot.hasChildren())
                            {
                                for(DataSnapshot monthChild:snapshot.getChildren())
                                    for(DataSnapshot monthGrandChild:monthChild.getChildren())
                                        if(!String.valueOf(monthGrandChild.getKey()).equals("Overall"))
                                            for(DataSnapshot monthGreatGrandChild:monthGrandChild.getChildren())
                                            {
                                                MoneyManager money=new MoneyManager(String.valueOf(monthGreatGrandChild.child("note").getValue()),Float.parseFloat(String.valueOf(monthGreatGrandChild.child("value").getValue())),String.valueOf(monthGreatGrandChild.child("date").getValue()),String.valueOf(monthGreatGrandChild.child("type").getValue()));
                                                arrayList.add(money);
                                            }
                                Collections.sort(arrayList, new Comparator<MoneyManager>() // sortare dupa valoare descendent
                                {
                                    @Override
                                    public int compare(MoneyManager o1, MoneyManager o2)
                                    {
                                        return Float.compare(o2.getValue(), o1.getValue());
                                    }
                                });
                                adaptor.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });
                }
    }

    private void childListener()
    {
        if(fbAuth.getUid()!=null)
        {
            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(yearSpinner.getSelectedItem())).child(String.valueOf(monthSpinner.getSelectedItem())).addChildEventListener(new ChildEventListener()
            {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                {
                    String note=String.valueOf(snapshot.child("note").getValue()), date = String.valueOf(snapshot.child("date").getValue()), type = String.valueOf(snapshot.child("type").getValue());
                    Float value=Float.parseFloat(String.valueOf(snapshot.child("value").getValue()));

                    MoneyManager money = new MoneyManager(note, value, date, type);
                    arrayList.add(money);
                    adaptor.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot)
                {
                    String date=String.valueOf(snapshot.child("date").getValue()), note=String.valueOf(snapshot.child("note").getValue()), type=String.valueOf(snapshot.child("type").getValue());
                    Float value=Float.parseFloat(String.valueOf(snapshot.child("value").getValue()));

                    MoneyManager money=new MoneyManager(note, value, date, type);
                    arrayList.remove(money);
                    adaptor.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
        }
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
                                boolean darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("darkTheme").getValue()).trim());
                                int theme, dropDownTheme;

                                if(!darkThemeEnabled)
                                {
                                    theme=R.drawable.ic_white_gradient_tobacco_ad;
                                    dropDownTheme=R.drawable.ic_blue_gradient_unloved_teen;
                                }
                                else
                                {
                                    theme=R.drawable.ic_black_gradient_night_shift;
                                    dropDownTheme=R.drawable.ic_white_gradient_tobacco_ad;
                                }

                                getWindow().setBackgroundDrawableResource(theme); // setam culoarea dropdown

                                monthSpinner.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP); // setam culoarea sagetii
                                monthSpinner.setPopupBackgroundResource(dropDownTheme); // setam culoarea elementelor
                                yearSpinner.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                                yearSpinner.setPopupBackgroundResource(dropDownTheme);
                            }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });
    }

    class CustomAdaptor extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) // cream fiecare element al listei sub forma unui cardview ce are o imagine, un titlu si o descriere
        {
            final MoneyManager money=arrayList.get(position);

            @SuppressLint({"ViewHolder", "InflateParams"}) View view=getLayoutInflater().inflate(R.layout.cardview_transaction_layout,null);
            final TextView transactionType=view.findViewById(R.id.transactionType);
            final TextView transactionPrice=view.findViewById(R.id.transactionPrice);
            final TextView transactionNote=view.findViewById(R.id.transactionNote);
            ImageView transactionDelete=view.findViewById(R.id.transactionDelete);
            ImageView transactionEdit=view.findViewById(R.id.transactionEdit);
            String translatedType;
            final ConstraintLayout mainLayout=view.findViewById(R.id.transactionLayout);
            ConstraintSet set=new ConstraintSet();
            set.clone(mainLayout);

            switch(money.getType())
            {
                case "Deposits":
                    translatedType=getResources().getString(R.string.add_money_deposits).trim();
                    break;
                case "IndependentSources":
                    translatedType=getResources().getString(R.string.add_money_independent_sources).trim();
                    break;
                case "Salary":
                    translatedType=getResources().getString(R.string.salary).trim();
                    break;
                case "Saving":
                    translatedType=getResources().getString(R.string.saving).trim();
                    break;
                case "Bills":
                    translatedType=getResources().getString(R.string.subtract_money_bills).trim();
                    break;
                case "Car":
                    translatedType=getResources().getString(R.string.subtract_money_car).trim();
                    break;
                case "Clothes":
                    translatedType=getResources().getString(R.string.subtract_money_clothes).trim();
                    break;
                case "Communications":
                    translatedType=getResources().getString(R.string.subtract_money_communications).trim();
                    break;
                case "EatingOut":
                    translatedType=getResources().getString(R.string.subtract_money_eating_out).trim();
                    break;
                case "Entertainment":
                    translatedType=getResources().getString(R.string.subtract_money_entertainment).trim();
                    break;
                case "Food":
                    translatedType=getResources().getString(R.string.subtract_money_food).trim();
                    break;
                case "Gifts":
                    translatedType=getResources().getString(R.string.subtract_money_gifts).trim();
                    break;
                case "Health":
                    translatedType=getResources().getString(R.string.subtract_money_health).trim();
                    break;
                case "House":
                    translatedType=getResources().getString(R.string.subtract_money_house).trim();
                    break;
                case "Pets":
                    translatedType=getResources().getString(R.string.subtract_money_pets).trim();
                    break;
                case "Sports":
                    translatedType=getResources().getString(R.string.subtract_money_sports).trim();
                    break;
                case "Taxi":
                    translatedType=getResources().getString(R.string.subtract_money_taxi).trim();
                    break;
                case "Toiletry":
                    translatedType=getResources().getString(R.string.subtract_money_toiletry).trim();
                    break;
                default:
                    translatedType=getResources().getString(R.string.subtract_money_transport).trim();
                    break;
            }

            transactionType.setText(translatedType);
            transactionType.setTextColor(Color.BLACK);
            transactionType.setTextSize(18);

            if(fbAuth.getUid()!=null)
                myRef.child(fbAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener()
                {
                    String currency, priceText;
                    boolean darkThemeEnabled;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                            if(snapshot.hasChild("ApplicationSettings"))
                            {
                                currency=String.valueOf(snapshot.child("ApplicationSettings").child("currencySymbol").getValue());
                                darkThemeEnabled=Boolean.parseBoolean(String.valueOf(snapshot.child("ApplicationSettings").child("darkTheme").getValue()));

                                if(Locale.getDefault().getDisplayLanguage().equals("English"))
                                    priceText=currency+money.getValue();
                                else priceText=money.getValue()+" "+currency;

                                if(!darkThemeEnabled)
                                    mainLayout.setBackgroundResource(R.drawable.ic_yellow_gradient_soda);
                                else mainLayout.setBackgroundResource(R.drawable.ic_white_gradient_tobacco_ad);
                                transactionPrice.setText(priceText);
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });

            transactionPrice.setTextColor(Color.BLACK);
            transactionPrice.setTextSize(18);
            transactionNote.setText(String.valueOf(money.getNote()));
            transactionNote.setTextColor(15);

            if(String.valueOf(transactionNote.getText()).trim().equals(""))
            {
                mainLayout.removeView(transactionNote);
                set.connect(transactionType.getId(), ConstraintSet.BOTTOM, mainLayout.getId(), ConstraintSet.BOTTOM);
                set.applyTo(mainLayout);
            }
            else
            {
                transactionNote.setTextColor(Color.BLACK);
                transactionNote.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            }

            transactionDelete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) // totul merge perfect, in afara de faptul ca nu se auto-actualizeaza lista la stergere si trebuie dat click din nou pe submit
                {
                    if(fbAuth.getUid()!=null)
                    {
                        final String monthName;
                        if(Locale.getDefault().getDisplayLanguage().equals("English"))
                            monthName=String.valueOf(monthSpinner.getSelectedItem());
                        else monthName=getMonthInEnglish(String.valueOf(monthSpinner.getSelectedItem()));

                        myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(yearSpinner.getSelectedItem())).child(monthName).addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            String incomeOrExpense=null;

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if(money.getType().equals("Salary") || money.getType().equals("Saving") || money.getType().equals("Deposits") || money.getType().equals("IndependentSources"))
                                    incomeOrExpense="Incomes";
                                else incomeOrExpense="Expenses";

                                if(snapshot.exists())
                                    if(snapshot.hasChild(incomeOrExpense))
                                        if(snapshot.child(incomeOrExpense).hasChild("Overall"))
                                        {
                                            float oldOverall=Float.parseFloat(String.valueOf(snapshot.child(incomeOrExpense).child("Overall").getValue()));
                                            oldOverall-=money.getValue();
                                            if(oldOverall<=0f)
                                                myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(yearSpinner.getSelectedItem())).child(monthName).child(incomeOrExpense).child("Overall").removeValue();
                                            else myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(yearSpinner.getSelectedItem())).child(monthName).child(incomeOrExpense).child("Overall").setValue(String.valueOf(oldOverall));
                                            myRef.child(fbAuth.getUid()).child("PersonalTransactions").child(String.valueOf(yearSpinner.getSelectedItem())).child(monthName).child(incomeOrExpense).child(money.getType()).child(money.getDate()).removeValue();
                                        }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {

                            }
                        });
                    }
                }
            });

            transactionEdit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent=new Intent(ActivityEditTransactions.this, ActivityEditSpecificTransaction.class);
                    intent.putExtra("note", money.getNote()).putExtra("value", String.valueOf(money.getValue())).putExtra("date", money.getDate()).putExtra("type", money.getType());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            return view;
        }
    }

    private String getTranslatedMonth(String monthName)
    {
        switch(monthName)
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
                return null;
        }
    }

    private String getMonthInEnglish(String monthName)
    {
        if(monthName.trim().equals(getResources().getString(R.string.month_january)))
            return "January";
        else if(monthName.trim().equals(getResources().getString(R.string.month_february)))
            return "February";
        else if(monthName.trim().equals(getResources().getString(R.string.month_march)))
            return "March";
        else if(monthName.trim().equals(getResources().getString(R.string.month_april)))
            return "April";
        else if(monthName.trim().equals(getResources().getString(R.string.month_may)))
            return "May";
        else if(monthName.trim().equals(getResources().getString(R.string.month_june)))
            return "June";
        else if(monthName.trim().equals(getResources().getString(R.string.month_july)))
            return "July";
        else if(monthName.trim().equals(getResources().getString(R.string.month_august)))
            return "August";
        else if(monthName.trim().equals(getResources().getString(R.string.month_september)))
            return "September";
        else if(monthName.trim().equals(getResources().getString(R.string.month_october)))
            return "October";
        else if(monthName.trim().equals(getResources().getString(R.string.month_november)))
            return "November";
        else if(monthName.trim().equals(getResources().getString(R.string.month_december)))
            return "December";
        else return "";
    }
}