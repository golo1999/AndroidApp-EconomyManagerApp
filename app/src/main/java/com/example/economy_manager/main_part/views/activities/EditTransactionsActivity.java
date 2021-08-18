package com.example.economy_manager.main_part.views.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.adapters.EditTransactionsRecyclerViewAdapter;
import com.example.economy_manager.main_part.dialogs.DeleteTransactionCustomDialog;
import com.example.economy_manager.main_part.viewmodels.EditTransactionsViewModel;
import com.example.economy_manager.main_part.views.fragments.EditSpecificTransactionFragment;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.models.UserDetails;
import com.example.economy_manager.utilities.Months;
import com.example.economy_manager.utilities.MyCustomSharedPreferences;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.example.economy_manager.utilities.Types;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class EditTransactionsActivity extends AppCompatActivity
        implements DeleteTransactionCustomDialog.DeleteDialogListener {
    private UserDetails userDetails;
    private EditTransactionsViewModel viewModel;
    private ConstraintLayout activityLayout;
    private FrameLayout fragmentLayout;
    private ImageView goBack;
    private TextView activityTitle;
    private TextView centerText;
    private RecyclerView recyclerView;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private ConstraintLayout bottomLayout;
    private final ArrayList<Transaction> allTransactionsList = new ArrayList<>();
    private final ArrayList<Transaction> transactionsList = new ArrayList<>();
    private EditTransactionsRecyclerViewAdapter recyclerViewAdapter;

    public EditTransactionsViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_transactions_activity);
        setVariables();
        setUserDetailsInViewModel(userDetails != null ? userDetails : MyCustomVariables.getDefaultUserDetails());
        setTheme();
        setRecyclerView();
        setTitle();
        setBottomLayout();
        setOnClickListeners();
    }

    @Override
    public void onBackPressed() {
        if (viewModel.getEditSpecificTransactionFragment() != null) {
            removeEditSpecificTransactionFragment();
        } else {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void setVariables() {
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);
        viewModel = new ViewModelProvider(this).get(EditTransactionsViewModel.class);
        activityLayout = findViewById(R.id.edit_transactions_activity_layout);
        fragmentLayout = findViewById(R.id.edit_transactions_fragment_layout);
        goBack = findViewById(R.id.editTransactionsBack);
        recyclerView = findViewById(R.id.editTransactionsRecyclerView);
        activityTitle = findViewById(R.id.editTransactionsTitle);
        monthSpinner = findViewById(R.id.editTransactionBottomLayoutMonthSpinner);
        yearSpinner = findViewById(R.id.editTransactionBottomLayoutYearSpinner);
        centerText = findViewById(R.id.editTransactionsCenterText);
        bottomLayout = findViewById(R.id.editTransactionBottomLayout);
        recyclerViewAdapter = new EditTransactionsRecyclerViewAdapter(viewModel, transactionsList, this,
                userDetails, recyclerView, getSupportFragmentManager());
    }

    public void setUserDetailsInViewModel(final UserDetails details) {
        viewModel.setUserDetails(details);
    }

    private void setTitle() {
        activityTitle.setText(getResources().getString(R.string.edit_transactions_title).trim());
        activityTitle.setTextSize(20);
        activityTitle.setTextColor(Color.WHITE);
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void setOnClickListeners() {
        goBack.setOnClickListener(v -> onBackPressed());
    }

    private void setBottomLayout() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("PersonalTransactions")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChildren()) {
                                final ArrayList<String> yearsList = new ArrayList<>();
                                final ArrayList<String> monthsList = new ArrayList<>();

                                if (!allTransactionsList.isEmpty()) {
                                    allTransactionsList.clear();
                                }

                                for (final DataSnapshot transaction : snapshot.getChildren()) {
                                    final Transaction transactionFromDatabase = transaction.getValue(Transaction.class);

                                    if (transactionFromDatabase != null && transactionFromDatabase.getTime() != null) {
                                        final String transactionYear =
                                                String.valueOf(transactionFromDatabase.getTime().getYear());

                                        if (yearsList.isEmpty()) {
                                            yearsList.add(transactionYear);
                                        } else {
                                            boolean yearAlreadyExists = false;

                                            for (final String year : yearsList)
                                                if (year.equals(transactionYear)) {
                                                    yearAlreadyExists = true;
                                                    break;
                                                }

                                            if (!yearAlreadyExists) {
                                                yearsList.add(transactionYear);
                                            }
                                        }

                                        allTransactionsList.add(transactionFromDatabase);
                                    }
                                }

                                createYearSpinner(yearsList);

                                yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(final AdapterView<?> parent, final View view,
                                                               final int position, final long id) {
                                        final String selectedYear =
                                                String.valueOf(yearSpinner.getItemAtPosition(position));

                                        try {
                                            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                                            ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }

                                        if (!monthsList.isEmpty()) {
                                            monthsList.clear();
                                        }

                                        for (final Transaction transaction : allTransactionsList) {
                                            if (String.valueOf(transaction.getTime().getYear()).equals(selectedYear)) {
                                                final String transactionMonthParsed =
                                                        transaction.getTime().getMonthName().charAt(0) +
                                                                transaction.getTime().getMonthName().substring(1)
                                                                        .toLowerCase();

                                                if (monthsList.isEmpty()) {
                                                    monthsList.add(transactionMonthParsed);
                                                } else {
                                                    boolean monthAlreadyExists = false;

                                                    for (final String month : monthsList)
                                                        if (month.equals(transactionMonthParsed)) {
                                                            monthAlreadyExists = true;
                                                            break;
                                                        }

                                                    if (!monthAlreadyExists) {
                                                        monthsList.add(transactionMonthParsed);
                                                    }
                                                }
                                            }
                                        }

                                        createMonthSpinner(monthsList, selectedYear);

                                        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(final AdapterView<?> parent, final View view,
                                                                       final int position, final long id) {
                                                final String selectedMonth =
                                                        String.valueOf(monthSpinner.getItemAtPosition(position));

                                                try {
                                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                                                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                                                } catch (NullPointerException e) {
                                                    e.printStackTrace();
                                                }

                                                if (!monthsList.isEmpty()) {
                                                    centerText.setText("");
                                                    recyclerView.setEnabled(true);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    populateRecyclerView(selectedYear, selectedMonth);
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
                            } else {
                                monthSpinner.setEnabled(false);
                                yearSpinner.setEnabled(false);
                                bottomLayout.setVisibility(View.GONE);
                                monthSpinner.setVisibility(View.GONE);
                                yearSpinner.setVisibility(View.GONE);
                                centerText.setText(getResources()
                                        .getString(R.string.edit_transactions_center_text_no_transactions).trim());
                            }

                            if (userDetails != null) {
                                final boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();

                                centerText.setTextColor(!darkThemeEnabled ?
                                        Color.parseColor("#195190") : Color.WHITE);
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

    private void createMonthSpinner(final ArrayList<String> list,
                                    final String selectedYear) {
        // setting the array which contains the months translated in english
        final String[] months = getResources().getStringArray(R.array.months);
        // frequency array
        final int[] frequency = new int[13];
        final Calendar currentTime = Calendar.getInstance();
        final SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL", Locale.ENGLISH);
        int positionOfCurrentMonth = -1;

        // initializing array's elements with 0
        Arrays.fill(frequency, 0);

        // creating the frequency array
        for (final String listIterator : list) {
            int counter = 0;
            // finding out which months are also found into the selected year's months list
            for (String monthsListIterator : months) {
                counter++;

                if (listIterator.equals(monthsListIterator)) {
                    frequency[counter - 1] = 1;
                }
            }
        }

        // clearing the list and adding the months in their natural order
        list.clear();

        int counter = 0;

        // adding the month to the list if its frequency is 1
        for (final int frequencyIterator : frequency) {
            counter++;

            if (frequencyIterator == 1) {
                // translating the month name if it's not in english
                list.add(!Locale.getDefault().getDisplayLanguage().equals("English") ?
                        Months.getTranslatedMonth(EditTransactionsActivity.this, months[counter - 1]) :
                        months[counter - 1]);
            }
        }

        counter = 0;

        // saving current month's position if it has been found => for setting the default month spinner's selected
        // item as the current month
        for (String listIterator : list) {
            counter++;

            if (Months.getMonthInEnglish(EditTransactionsActivity.this, listIterator)
                    .equals(monthFormat.format(currentTime.getTime()))) {
                positionOfCurrentMonth = counter - 1;
                break;
            }
        }

        // creating and setting the month spinner adapter's styling
        final ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, list) {
            @Override
            public View getDropDownView(final int position, final @Nullable View convertView,
                                        final @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                // all spinner elements are aligned to center
                ((TextView) v).setGravity(Gravity.CENTER);

                if (userDetails != null) {
                    final boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                    final int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;

                    // setting text color based on the selected theme
                    ((TextView) v).setTextColor(itemsColor);
                }

                return v;
            }
        };

        monthSpinner.setAdapter(monthAdapter);

        // if the current month exists in the list and the selected year is the current one
        if (positionOfCurrentMonth >= 0 && selectedYear.equals(String.valueOf(currentTime.get(Calendar.YEAR)))) {
            // setting the current month as default when opening the activity or when selecting the current year
            monthSpinner.setSelection(positionOfCurrentMonth);
        }
    }

    private void createYearSpinner(final ArrayList<String> list) {
        // creating and setting the year spinner adapter's styling
        final ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, list) {
            @Override
            public View getDropDownView(final int position, final @Nullable View convertView,
                                        final @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                // all spinner elements are aligned to center
                ((TextView) v).setGravity(Gravity.CENTER);

                if (userDetails != null) {
                    final boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
                    final int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;

                    // setting text color based on the selected theme
                    ((TextView) v).setTextColor(itemsColor);
                }

                return v;
            }
        };

        yearSpinner.setAdapter(yearAdapter);

        final Calendar currentTime = Calendar.getInstance();
        int positionOfCurrentYear = -1;
        int secondCounter = -1;

        // searching the current year into the years list and saving its position if it has been found
        for (final String listIterator : list) {
            secondCounter++;

            if (listIterator.equals(String.valueOf(currentTime.get(Calendar.YEAR)))) {
                positionOfCurrentYear = secondCounter;
                break;
            }
        }

        // setting the current year as default when the activity opens if it has been found
        if (positionOfCurrentYear >= 0) {
            yearSpinner.setSelection(positionOfCurrentYear);
        }
    }

    private void populateRecyclerView(final String selectedYear,
                                      final String selectedMonth) {
        final int currentNumberOfTransactions = recyclerViewAdapter.getItemCount();

        if (!transactionsList.isEmpty()) {
            transactionsList.clear();
        }

        recyclerViewAdapter.notifyItemRangeRemoved(0, currentNumberOfTransactions);

        for (final Transaction transaction : allTransactionsList) {
            final String transactionMonthParsed = transaction.getTime().getMonthName().charAt(0) +
                    transaction.getTime().getMonthName().substring(1).toLowerCase();

            if (String.valueOf(transaction.getTime().getYear()).equals(selectedYear) &&
                    transactionMonthParsed.equals(Months.getMonthInEnglish(EditTransactionsActivity.this,
                            selectedMonth))) {
                transactionsList.add(transaction);
            }
        }

        transactionsList.sort((final Transaction o1, final Transaction o2) ->
                Float.compare(Float.parseFloat(o2.getValue()), Float.parseFloat(o1.getValue())));

        recyclerViewAdapter.notifyItemRangeChanged(0, transactionsList.size());
    }

    private void setTheme() {
        if (userDetails != null) {
            final boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
            final int theme = !darkThemeEnabled ?
                    R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift;
            final int dropDownTheme = !darkThemeEnabled ?
                    R.drawable.ic_blue_gradient_unloved_teen : R.drawable.ic_white_gradient_tobacco_ad;

            // setam culoarea dropdown
            getWindow().setBackgroundDrawableResource(theme);

            // setam culoarea sagetii
            monthSpinner.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            monthSpinner.setPopupBackgroundResource(dropDownTheme);
            // setam culoarea elementelor
            yearSpinner.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            yearSpinner.setPopupBackgroundResource(dropDownTheme);
        }
    }

    public void setEditSpecificTransactionFragment() {
        viewModel.setEditSpecificTransactionFragment(new EditSpecificTransactionFragment());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentLayout.getId(), viewModel.getEditSpecificTransactionFragment())
                .commit();

        activityLayout.setVisibility(View.INVISIBLE);
        fragmentLayout.setVisibility(View.VISIBLE);
    }

    public void removeEditSpecificTransactionFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(viewModel.getEditSpecificTransactionFragment())
                .commit();

        viewModel.setEditSpecificTransactionFragment(null);

        fragmentLayout.setVisibility(View.INVISIBLE);
        activityLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDialogPositiveClick(final DialogFragment dialog,
                                      final ArrayList<Transaction> transactionsList,
                                      final EditTransactionsRecyclerViewAdapter adapter,
                                      final int positionInList) {
        final Transaction transactionToDelete = transactionsList.get(positionInList);

        transactionsList.remove(positionInList);
        adapter.notifyItemRemoved(positionInList);

        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .child("PersonalTransactions")
                    .child(transactionToDelete.getId())
                    .removeValue();
        }
    }

    @Override
    public void onDialogNegativeClick(final DialogFragment dialog) {

    }

    class CustomAdaptor extends BaseAdapter {
        @Override
        public int getCount() {
            return allTransactionsList.size();
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
            final Transaction transaction = allTransactionsList.get(position);

            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater()
                    .inflate(R.layout.transaction_layout_cardview, null);
            final TextView transactionCategory = view.findViewById(R.id.transactionCategory);
            final TextView transactionPrice = view.findViewById(R.id.transactionPrice);
            final TextView transactionNote = view.findViewById(R.id.transactionNote);
            ImageView transactionDelete = view.findViewById(R.id.transactionDelete);
            ImageView transactionEdit = view.findViewById(R.id.transactionEdit);
            String translatedCategory = Types.getTranslatedType(EditTransactionsActivity.this,
                    Transaction.getTypeFromIndexInEnglish(transaction.getType()));
            final ConstraintLayout mainLayout = view.findViewById(R.id.transactionLayout);
            ConstraintSet set = new ConstraintSet();
            set.clone(mainLayout);

            transactionCategory.setText(translatedCategory);

            if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                MyCustomVariables.getDatabaseReference().child(MyCustomVariables.getFirebaseAuth().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
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
            }

            transactionNote.setText(String.valueOf(transaction.getNote()));

            if (String.valueOf(transactionNote.getText()).trim().equals("")) {
                mainLayout.removeView(transactionNote);
                set.connect(transactionPrice.getId(), ConstraintSet.BOTTOM, mainLayout.getId(), ConstraintSet.BOTTOM);
                set.applyTo(mainLayout);
            }

            // totul merge perfect, in afara de faptul ca nu se auto-actualizeaza lista la stergere si trebuie dat click din nou pe submit
            transactionDelete.setOnClickListener(v -> {
                if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
                    // translating the month if its name isn't in english
                    final String monthName = Locale.getDefault().getDisplayLanguage().equals("English") ?
                            String.valueOf(monthSpinner.getSelectedItem()) :
                            Months.getMonthInEnglish(EditTransactionsActivity.this,
                                    String.valueOf(monthSpinner.getSelectedItem()));

                    MyCustomVariables.getDatabaseReference()
                            .child(MyCustomVariables.getFirebaseAuth().getUid())
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

                                    if (snapshot.exists() && snapshot.hasChild(incomeOrExpense)) {
                                        if (snapshot.child(incomeOrExpense).hasChild("Overall")) {
                                            float oldOverall = Float.parseFloat(String
                                                    .valueOf(snapshot.child(incomeOrExpense)
                                                            .child("Overall").getValue()));
                                            oldOverall -= Float.parseFloat(transaction.getValue());

                                            if (oldOverall <= 0f) {
                                                MyCustomVariables.getDatabaseReference()
                                                        .child(MyCustomVariables.getFirebaseAuth().getUid())
                                                        .child("PersonalTransactions")
                                                        .child(String
                                                                .valueOf(yearSpinner.getSelectedItem()))
                                                        .child(monthName)
                                                        .child(incomeOrExpense)
                                                        .child("Overall").removeValue();
                                            } else {
                                                MyCustomVariables.getDatabaseReference()
                                                        .child(MyCustomVariables.getFirebaseAuth().getUid())
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
}