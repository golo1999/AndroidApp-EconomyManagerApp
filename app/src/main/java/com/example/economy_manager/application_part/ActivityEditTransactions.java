package com.example.economy_manager.application_part;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economy_manager.Months;
import com.example.economy_manager.MyCustomSharedPreferences;
import com.example.economy_manager.R;
import com.example.economy_manager.Transaction;
import com.example.economy_manager.Types;
import com.example.economy_manager.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class ActivityEditTransactions extends AppCompatActivity {
    private UserDetails userDetails;
    private EditTransactionsViewModel viewModel;
    private ImageView goBack;
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private TextView activityTitle;
    private TextView centerText;
    private RecyclerView recyclerView;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private ArrayList<Transaction> arrayList = new ArrayList<>();
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private ConstraintLayout bottomLayout;
    private ArrayList<Transaction> transactionsList = new ArrayList<>();
    private ActivityEditTransactionsRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transactions);
        setVariables();
        setTheme();
        setRecyclerView();
        setTitle();
        setBottomLayout();
        setOnClickListeners();
        //childListener();

        Log.d("userDetails", userDetails.toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setVariables() {
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);
        viewModel = new ViewModelProvider(this).get(EditTransactionsViewModel.class);
        goBack = findViewById(R.id.editTransactionsBack);
        recyclerView = findViewById(R.id.editTransactionsRecyclerView);
        activityTitle = findViewById(R.id.editTransactionsTitle);
        monthSpinner = findViewById(R.id.editTransactionBottomLayoutMonthSpinner);
        yearSpinner = findViewById(R.id.editTransactionBottomLayoutYearSpinner);
        centerText = findViewById(R.id.editTransactionsCenterText);
        bottomLayout = findViewById(R.id.editTransactionBottomLayout);
        adapter = new ActivityEditTransactionsRecyclerViewAdapter(viewModel, transactionsList,
                this, userDetails);
    }

    private void setTitle() {
        activityTitle.setText(getResources().getString(R.string.edit_transactions_title).trim());
        activityTitle.setTextSize(20);
        activityTitle.setTextColor(Color.WHITE);
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListeners() {
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void setBottomLayout() {
        if (fbAuth.getUid() != null) {
            myRef.child(fbAuth.getUid())
                    .child("PersonalTransactions")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.hasChildren()) {
                                    final ArrayList<String> yearList = new ArrayList<>();
                                    ArrayList<String> monthList = new ArrayList<>();
                                    boolean yearAlreadyExists;

                                    for (DataSnapshot transaction : snapshot.getChildren()) {
                                        Transaction transactionFromDatabase = transaction
                                                .getValue(Transaction.class);

                                        if (transactionFromDatabase != null &&
                                                transactionFromDatabase.getTime() != null) {
                                            if (yearList.isEmpty()) {
                                                yearList.add(String.valueOf(transactionFromDatabase
                                                        .getTime().getYear()));
                                            } else {
                                                yearAlreadyExists = false;

                                                for (String year : yearList)
                                                    if (year.equals(String.
                                                            valueOf(transactionFromDatabase
                                                                    .getTime().getYear()))) {
                                                        yearAlreadyExists = true;
                                                        break;
                                                    }

                                                if (!yearAlreadyExists)
                                                    yearList.add(String
                                                            .valueOf(transactionFromDatabase
                                                                    .getTime().getYear()));
                                            }

                                            arrayList.add(transactionFromDatabase);
                                        }
                                    }

                                    createYearSpinner(yearList);

                                    yearSpinner.setOnItemSelectedListener(new AdapterView
                                            .OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view,
                                                                   int position, long id) {
                                            final String selectedYear = String.valueOf(yearSpinner
                                                    .getItemAtPosition(position));
                                            boolean monthAlreadyExists;

                                            try {
                                                ((TextView) parent.getChildAt(0))
                                                        .setTextColor(Color.WHITE);
                                                ((TextView) parent.getChildAt(0))
                                                        .setGravity(Gravity.CENTER);
                                            } catch (NullPointerException e) {
                                                e.printStackTrace();
                                            }

                                            for (Transaction transaction : arrayList)
                                                if (String.valueOf(transaction.getTime().getYear())
                                                        .equals(selectedYear)) {
                                                    String transactionMonthParsed = transaction
                                                            .getTime().getMonthName()
                                                            .substring(0, 1) +
                                                            transaction.getTime().getMonthName()
                                                                    .substring(1).toLowerCase();
                                                    if (monthList.isEmpty()) {
                                                        monthList.add(transactionMonthParsed);
                                                    } else {
                                                        monthAlreadyExists = false;

                                                        for (String month : monthList)
                                                            if (month.equals(transactionMonthParsed)) {
                                                                monthAlreadyExists = true;
                                                                break;
                                                            }

                                                        if (!monthAlreadyExists)
                                                            monthList.add(transactionMonthParsed);
                                                    }
                                                }

                                            createMonthSpinner(monthList, selectedYear);

                                            monthSpinner.setOnItemSelectedListener(new AdapterView
                                                    .OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent,
                                                                           View view, int position,
                                                                           long id) {
                                                    String selectedMonth = String
                                                            .valueOf(monthSpinner
                                                                    .getItemAtPosition(position));

                                                    try {
                                                        ((TextView) parent.getChildAt(0))
                                                                .setTextColor(Color.WHITE);
                                                        ((TextView) parent.getChildAt(0))
                                                                .setGravity(Gravity.CENTER);
                                                    } catch (NullPointerException e) {
                                                        e.printStackTrace();
                                                    }

                                                    if (!monthList.isEmpty()) {
                                                        centerText.setText("");
                                                        recyclerView.setEnabled(true);
                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        populateRecyclerView(selectedYear,
                                                                selectedMonth);
                                                    }
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                }
                            } else {
                                monthSpinner.setEnabled(false);
                                yearSpinner.setEnabled(false);
                                bottomLayout.setVisibility(View.GONE);
                                monthSpinner.setVisibility(View.GONE);
                                yearSpinner.setVisibility(View.GONE);
                                centerText.setText(getResources().getString(R.string.edit_transactions_center_text_no_transactions).trim());
                            }

                            if (userDetails != null) {
                                boolean darkThemeEnabled = userDetails.getApplicationSettings()
                                        .getDarkTheme();

                                centerText.setTextColor(!darkThemeEnabled ?
                                        Color.parseColor("#195190") :
                                        Color.WHITE);
                            }

                            centerText.setTextSize(20);
                            recyclerView.setEnabled(false);
                            recyclerView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void createMonthSpinner(ArrayList<String> list, String selectedYear) {
        // setam vectorul ce contine lunile in engleza
        String[] months = getResources().getStringArray(R.array.months);
        // vectorul de frecventa
        int[] frequency = new int[13];
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL", Locale.ENGLISH);
        int positionOfCurrentMonth = -1;

        // initializam vectorul de luni cu 0 pentru toate elementele
        Arrays.fill(frequency, 0);

        // cream vectorul de frecventa
        for (String listIterator : list) {
            int j = 0;
            // vedem care dintre luni se regasesc si in ArrayList-ul cu lunile din anul selectat
            for (String monthsListIterator : months) {
                j++;
                if (listIterator.equals(monthsListIterator))
                    frequency[j - 1] = 1;
            }
        }

        // golim lista si vom adauga lunile in ordinea lor naturala
        list.clear();

        int j = 0;

        // daca luna are frecventa 1, o adaugam in lista
        for (int frequencyIterator : frequency) {
            j++;
            if (frequencyIterator == 1)
                // daca nu e in engleza
                if (!Locale.getDefault().getDisplayLanguage().equals("English"))
                    // traducem numele lunii cu frecventa 1
                    list.add(Months.getTranslatedMonth(ActivityEditTransactions.this,
                            months[j - 1]));
                    // daca e in engleza
                else list.add(months[j - 1]);
        }

        j = 0;

        // daca gasim luna curenta in lista, ii salvam pozitia
        // (pentru a seta Spinnerul pe luna curenta, in cazul in care exista tranzactii
        // efectuate in ea)
        for (String listIterator : list) {
            j++;
            if (Months.getMonthInEnglish(ActivityEditTransactions.this, listIterator)
                    .equals(monthFormat.format(currentTime.getTime())))
                positionOfCurrentMonth = j - 1;
        }

        // cream adaptorul ce afiseaza pe ecran lunile din Spinner
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item, list) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView,
                                        @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                // toate elementele spinnerului sunt aliniate la centru
                ((TextView) v).setGravity(Gravity.CENTER);

                if (userDetails != null) {
                    boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                    int itemsColor = !darkThemeEnabled ?
                            Color.WHITE : Color.BLACK;

                    // setam culoarea textului elementelor
                    // in functie de tema selectata
                    ((TextView) v).setTextColor(itemsColor);
                }

                return v;
            }
        };

        monthSpinner.setAdapter(monthAdapter);

        // daca luna curenta exista in lista si anul este cel curent
        if (positionOfCurrentMonth >= 0 &&
                selectedYear.equals(String.valueOf(currentTime.get(Calendar.YEAR)))) {
            // o setam ca default atunci cand deschidem activitatea sau cand selectam anul curent
            monthSpinner.setSelection(positionOfCurrentMonth);
        }
    }

    private void createYearSpinner(ArrayList<String> list) {
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_item, list) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView,
                                        @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                // toate elementele spinnerului sunt aliniate la centru
                ((TextView) v).setGravity(Gravity.CENTER);

                if (userDetails != null) {
                    boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                    int itemsColor = !darkThemeEnabled ?
                            Color.WHITE : Color.BLACK;

                    // setam culoarea textului elementelor
                    // in functie de tema selectata
                    ((TextView) v).setTextColor(itemsColor);
                }

                return v;
            }
        };

        yearSpinner.setAdapter(yearAdapter);
        Calendar currentTime = Calendar.getInstance();
        int positionOfCurrentYear = -1, j = -1;

        // cautam in lista anul curent si, in cazul in care este gasit, ii memoram pozitia
        for (String listIterator : list) {
            j++;
            if (listIterator.equals(String.valueOf(currentTime.get(Calendar.YEAR))))
                positionOfCurrentYear = j;
        }

        // daca am gasit anul curent in lista, il setam ca default atunci deschidem activitatea
        if (positionOfCurrentYear >= 0)
            yearSpinner.setSelection(positionOfCurrentYear);
    }

    private void populateRecyclerView(String selectedYear, String selectedMonth) {
        if (!transactionsList.isEmpty())
            transactionsList.clear();

        for (Transaction transaction : arrayList) {
            String transactionMonthParsed = transaction.getTime().getMonthName().substring(0, 1) +
                    transaction.getTime().getMonthName().substring(1).toLowerCase();

            if (String.valueOf(transaction.getTime().getYear()).equals(selectedYear) &&
                    transactionMonthParsed.equals(Months
                            .getMonthInEnglish(ActivityEditTransactions.this,
                                    selectedMonth))) {
                transactionsList.add(transaction);
            }
        }

        Collections.sort(transactionsList, (o1, o2) -> Float
                .compare(Float.parseFloat(o2.getValue()),
                        Float.parseFloat(o1.getValue())));
        adapter.notifyDataSetChanged();
    }

//    private void childListener() {
//        if (fbAuth.getUid() != null) {
//            myRef.child(fbAuth.getUid())
//                    .child("PersonalTransactions")
//                    .child(String.valueOf(yearSpinner.getSelectedItem()))
//                    .child(String.valueOf(monthSpinner.getSelectedItem()))
//                    .addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(@NonNull DataSnapshot snapshot,
//                                                 @Nullable String previousChildName) {
//                            String note = String.valueOf(snapshot.child("note").getValue());
//                            String date = String.valueOf(snapshot.child("date").getValue());
//                            String type = String.valueOf(snapshot.child("type").getValue());
//                            Float value = Float
//                                    .parseFloat(String.valueOf(snapshot.child("value").getValue()));
//
//                            MoneyManager money = new MoneyManager(note, value, date, type);
//                            arrayList.add(money);
//                            adaptor.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onChildChanged(@NonNull DataSnapshot snapshot,
//                                                   @Nullable String previousChildName) {
//
//                        }
//
//                        @Override
//                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                            String date = String.valueOf(snapshot.child("date").getValue());
//                            String note = String.valueOf(snapshot.child("note").getValue());
//                            String type = String.valueOf(snapshot.child("type").getValue());
//                            Float value = Float
//                                    .parseFloat(String.valueOf(snapshot.child("value").getValue()));
//
//                            MoneyManager money = new MoneyManager(note, value, date, type);
//                            arrayList.remove(money);
//                            adaptor.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onChildMoved(@NonNull DataSnapshot snapshot,
//                                                 @Nullable String previousChildName) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//        }
//    }

    private void setTheme() {
        Log.d("userDetailsInSetTheme", userDetails.toString());
        if (userDetails != null) {

            boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
            int theme = !darkThemeEnabled ?
                    R.drawable.ic_white_gradient_tobacco_ad :
                    R.drawable.ic_black_gradient_night_shift;
            int dropDownTheme = !darkThemeEnabled ?
                    R.drawable.ic_blue_gradient_unloved_teen :
                    R.drawable.ic_white_gradient_tobacco_ad;

            // setam culoarea dropdown
            getWindow().setBackgroundDrawableResource(theme);

            // setam culoarea sagetii
            monthSpinner.getBackground().setColorFilter(Color.WHITE,
                    PorterDuff.Mode.SRC_ATOP);
            monthSpinner.setPopupBackgroundResource(dropDownTheme);
            // setam culoarea elementelor
            yearSpinner.getBackground().setColorFilter(Color.WHITE,
                    PorterDuff.Mode.SRC_ATOP);
            yearSpinner.setPopupBackgroundResource(dropDownTheme);
        }
    }

    private UserDetails retrieveUserDetailsFromSharedPreferences() {
        SharedPreferences preferences =
                getSharedPreferences("ECONOMY_MANAGER_USER_DATA", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("currentUserDetails", "");

        return gson.fromJson(json, UserDetails.class);
    }

    class CustomAdaptor extends BaseAdapter {
        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        // cream fiecare element al listei sub forma unui cardview ce are
        // o imagine, un titlu si o descriere
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Transaction transaction = arrayList.get(position);

            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater()
                    .inflate(R.layout.cardview_transaction_layout, null);
            final TextView transactionCategory = view.findViewById(R.id.transactionCategory);
            final TextView transactionPrice = view.findViewById(R.id.transactionPrice);
            final TextView transactionNote = view.findViewById(R.id.transactionNote);
            ImageView transactionDelete = view.findViewById(R.id.transactionDelete);
            ImageView transactionEdit = view.findViewById(R.id.transactionEdit);
            String translatedCategory = Types.getTranslatedType(ActivityEditTransactions.this,
                    Transaction.getTypeFromIndexInEnglish(transaction.getType()));
            final ConstraintLayout mainLayout = view.findViewById(R.id.transactionLayout);
            ConstraintSet set = new ConstraintSet();
            set.clone(mainLayout);

            transactionCategory.setText(translatedCategory);

            if (fbAuth.getUid() != null)
                myRef.child(fbAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    String currency, priceText;
                    boolean darkThemeEnabled;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                            if (snapshot.hasChild("ApplicationSettings")) {
                                currency = String.valueOf(snapshot.child("ApplicationSettings")
                                        .child("currencySymbol").getValue());
                                darkThemeEnabled = Boolean.parseBoolean(String.valueOf(snapshot
                                        .child("ApplicationSettings")
                                        .child("darkTheme").getValue()));
                                priceText = Locale.getDefault().getDisplayLanguage().equals("English") ?
                                        currency + transaction.getValue() :
                                        transaction.getValue() + " " + currency;
                                mainLayout.setBackgroundResource(!darkThemeEnabled ?
                                        R.drawable.ic_yellow_gradient_soda :
                                        R.drawable.ic_white_gradient_tobacco_ad);
                                transactionPrice.setText(priceText);
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            transactionNote.setText(String.valueOf(transaction.getNote()));

            if (String.valueOf(transactionNote.getText()).trim().equals("")) {
                mainLayout.removeView(transactionNote);
                set.connect(transactionPrice.getId(), ConstraintSet.BOTTOM, mainLayout.getId(), ConstraintSet.BOTTOM);
                set.applyTo(mainLayout);
            }

            // totul merge perfect, in afara de faptul ca nu se auto-actualizeaza lista la stergere si trebuie dat click din nou pe submit
            transactionDelete.setOnClickListener(v -> {
                if (fbAuth.getUid() != null) {
                    // in cazul in care limba dispozitivului este engleza
                    final String monthName = Locale.getDefault().getDisplayLanguage()
                            .equals("English") ?
                            String.valueOf(monthSpinner.getSelectedItem()) :
                            Months.getMonthInEnglish(ActivityEditTransactions.this,
                                    String.valueOf(monthSpinner.getSelectedItem()));

                    myRef.child(fbAuth.getUid())
                            .child("PersonalTransactions")
                            .child(String.valueOf(yearSpinner.getSelectedItem()))
                            .child(monthName)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                String incomeOrExpense = null;

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    incomeOrExpense = (transaction.getType() == 0 ||
                                            transaction.getType() == 1 ||
                                            transaction.getType() == 2 ||
                                            transaction.getType() == 3) ?
                                            "Incomes" : "Expenses";

                                    if (snapshot.exists())
                                        if (snapshot.hasChild(incomeOrExpense)) {
                                            if (snapshot.child(incomeOrExpense).hasChild("Overall")) {
                                                float oldOverall = Float.parseFloat(String
                                                        .valueOf(snapshot.child(incomeOrExpense)
                                                                .child("Overall").getValue()));
                                                oldOverall -= Float.parseFloat(transaction.getValue());
                                                if (oldOverall <= 0f) {
                                                    myRef.child(fbAuth.getUid())
                                                            .child("PersonalTransactions")
                                                            .child(String
                                                                    .valueOf(yearSpinner.getSelectedItem()))
                                                            .child(monthName)
                                                            .child(incomeOrExpense)
                                                            .child("Overall").removeValue();
                                                } else {
                                                    myRef.child(fbAuth.getUid())
                                                            .child("PersonalTransactions")
                                                            .child(String.valueOf(yearSpinner
                                                                    .getSelectedItem()))
                                                            .child(monthName)
                                                            .child(incomeOrExpense)
                                                            .child("Overall")
                                                            .setValue(String.valueOf(oldOverall));
                                                }
                                            }
//                                            myRef.child(fbAuth.getUid())
//                                                    .child("PersonalTransactions")
//                                                    .child(String.valueOf(yearSpinner.getSelectedItem()))
//                                                    .child(monthName)
//                                                    .child(incomeOrExpense)
//                                                    .child(money.getType())
//                                                    .child(money.getDate())
//                                                    .removeValue();
                                        }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            });

            transactionEdit.setOnClickListener(v -> {
//                Intent intent = new Intent(ActivityEditTransactions.this,
//                        ActivityEditSpecificTransaction.class);
//                intent.putExtra("note", money.getNote())
//                        .putExtra("value", String.valueOf(money.getValue()))
//                        .putExtra("date", money.getDate())
//                        .putExtra("type", money.getType());
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });

            return view;
        }
    }

//    private String getTranslatedMonth(String monthName)
//    {
//        switch(monthName)
//        {
//            case "January":
//                return getResources().getString(R.string.month_january);
//            case "February":
//                return getResources().getString(R.string.month_february);
//            case "March":
//                return getResources().getString(R.string.month_march);
//            case "April":
//                return getResources().getString(R.string.month_april);
//            case "May":
//                return getResources().getString(R.string.month_may);
//            case "June":
//                return getResources().getString(R.string.month_june);
//            case "July":
//                return getResources().getString(R.string.month_july);
//            case "August":
//                return getResources().getString(R.string.month_august);
//            case "September":
//                return getResources().getString(R.string.month_september);
//            case "October":
//                return getResources().getString(R.string.month_october);
//            case "November":
//                return getResources().getString(R.string.month_november);
//            case "December":
//                return getResources().getString(R.string.month_december);
//            default:
//                return null;
//        }
//    }

//    private String getMonthInEnglish(String monthName)
//    {
//        if(monthName.trim().equals(getResources().getString(R.string.month_january)))
//            return "January";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_february)))
//            return "February";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_march)))
//            return "March";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_april)))
//            return "April";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_may)))
//            return "May";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_june)))
//            return "June";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_july)))
//            return "July";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_august)))
//            return "August";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_september)))
//            return "September";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_october)))
//            return "October";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_november)))
//            return "November";
//        else if(monthName.trim().equals(getResources().getString(R.string.month_december)))
//            return "December";
//        else return "";
//    }
}