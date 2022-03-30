package com.example.economy_manager.feature.edittransactions;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.EditTransactionsActivityBinding;
import com.example.economy_manager.dialog.DeleteTransactionCustomDialog;
import com.example.economy_manager.feature.editspecifictransaction.EditSpecificTransactionFragment;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Months;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.utility.Theme;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class EditTransactionsActivity
        extends AppCompatActivity
        implements DeleteTransactionCustomDialog.DeleteDialogListener,
        DatePickerDialog.OnDateSetListener,
        EditSpecificTransactionFragment.OnDateReceivedCallBack,
        EditSpecificTransactionFragment.OnTimeReceivedCallBack,
        TimePickerDialog.OnTimeSetListener {

    private EditTransactionsActivityBinding binding;
    private EditTransactionsViewModel viewModel;
    private final ArrayList<Transaction> allTransactionsList = new ArrayList<>();
    private final ArrayList<Transaction> transactionsList = new ArrayList<>();
    private EditTransactionsRecyclerViewAdapter recyclerViewAdapter;
    private UserDetails userDetails;

    public EditTransactionsViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onBackPressed() {
        if (viewModel.getEditSpecificTransactionFragment() != null) {
            removeEditSpecificTransactionFragment();
        } else {
            super.onBackPressed();
            MyCustomMethods.finishActivityWithFadeTransition(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVariables();
        setUserDetailsInViewModel(userDetails != null ? userDetails : MyCustomVariables.getDefaultUserDetails());
        setTheme();
        setRecyclerView();
        setBottomLayout();
    }

    @Override
    public void onDateReceived(final LocalDate newDate) {
        final Fragment currentDisplayedFragment =
                viewModel.getCurrentDisplayedFragment(getSupportFragmentManager().getFragments());

        if (!(currentDisplayedFragment instanceof EditSpecificTransactionFragment)) {
            return;
        }

        ((EditSpecificTransactionFragment) currentDisplayedFragment).setDateText(newDate);
    }

    @Override
    public void onDateSet(final DatePicker datePicker,
                          final int year,
                          final int month,
                          final int dayOfMonth) {
        onDateReceived(LocalDate.of(year, month + 1, dayOfMonth));
    }

    @Override
    public void onTimeReceived(final LocalTime newTime) {
        final Fragment currentDisplayedFragment =
                viewModel.getCurrentDisplayedFragment(getSupportFragmentManager().getFragments());

        if (!(currentDisplayedFragment instanceof EditSpecificTransactionFragment)) {
            return;
        }

        ((EditSpecificTransactionFragment) currentDisplayedFragment).setTimeText(newTime);
    }

    @Override
    public void onTimeSet(final TimePicker timePicker,
                          final int hourOfDay,
                          final int minute) {
        onTimeReceived(LocalTime.of(hourOfDay, minute));
    }

    @Override
    public void onDialogNegativeClick(final DialogFragment dialog) {

    }

    @Override
    public void onDialogPositiveClick(final DialogFragment dialog,
                                      final @NonNull ArrayList<Transaction> transactionsList,
                                      final @NonNull EditTransactionsRecyclerViewAdapter adapter,
                                      final int positionInList) {
        final String currentUserID = MyCustomVariables.getFirebaseAuth().getUid();

        transactionsList.remove(positionInList);
        adapter.notifyItemRemoved(positionInList);

        if (currentUserID == null) {
            return;
        }

        MyCustomVariables.getDatabaseReference()
                .child(currentUserID)
                .child("personalTransactions")
                .child(transactionsList.get(positionInList).getId())
                .removeValue();
    }

    private void createMonthSpinner(final @NonNull ArrayList<String> list,
                                    final String selectedYear) {
        // setting the array which contains the months translated in english
        final String[] months = getResources().getStringArray(R.array.months);
        // frequency array
        final int[] frequency = new int[12];

        final Calendar currentTime = Calendar.getInstance();

        final SimpleDateFormat monthFormat = new SimpleDateFormat("LLLL", Locale.ENGLISH);

        int positionOfCurrentMonth = -1;

        // initializing frequency array's elements with 0
        Arrays.fill(frequency, 0);

        // creating the frequency array
        list.forEach((final String listIterator) -> {
            int counter = -1;
            // finding out which months are also found into the selected year's months list
            for (String monthsListIterator : months) {
                counter++;

                if (listIterator.equals(monthsListIterator)) {
                    frequency[counter - 1] = 1;
                }
            }
        });

        // clearing the list and adding the months in their natural order
        list.clear();

        int counter = 0;

        // adding the month to the list if its frequency is 1
        for (final int frequencyIterator : frequency) {
            counter++;

            if (frequencyIterator == 1) {
                // translating the month name if it's not in english
                list.add(!Locale.getDefault().getDisplayLanguage().equals("English") ?
                        Months.getTranslatedMonth(EditTransactionsActivity.this, months[counter]) :
                        months[counter]);
            }
        }

        counter = -1;

        // saving current month's position if it has been found => for setting the default month spinner's selected
        // item as the current month
        for (final String listIterator : list) {
            final String listIteratorInEnglish =
                    Months.getMonthInEnglish(EditTransactionsActivity.this, listIterator);

            final String currentMonthNameFormatted = monthFormat.format(currentTime.getTime());

            counter++;

            if (listIteratorInEnglish.equals(currentMonthNameFormatted)) {
                positionOfCurrentMonth = counter;
                break;
            }
        }

        // creating and setting the month spinner adapter's styling
        final ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, list) {
            @Override
            public View getDropDownView(final int position,
                                        final @Nullable View convertView,
                                        final @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);

                final boolean darkThemeEnabled = userDetails != null ?
                        userDetails.getApplicationSettings().isDarkThemeEnabled() :
                        MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

                final int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;
                // all spinner elements are aligned to center
                ((TextView) v).setGravity(Gravity.CENTER);

                // setting text color based on the selected theme
                ((TextView) v).setTextColor(itemsColor);

                return v;
            }
        };

        binding.monthsSpinner.setAdapter(monthAdapter);

        // if the current month exists in the list and the selected year is the current one
        if (positionOfCurrentMonth >= 0 &&
                selectedYear.equals(String.valueOf(currentTime.get(Calendar.YEAR)))) {
            // setting the current month as default when opening the activity or when selecting the current year
            binding.monthsSpinner.setSelection(positionOfCurrentMonth);
        } else {
            // subtracting 1 because we start from index 0
            final int currentMonthIndex = LocalDate.now().getMonthValue() - 1;
            boolean nearbyMonthFound = false;

            // if the current month is at least February
            if (currentMonthIndex > 0) {
                for (int monthCounter = currentMonthIndex - 1; monthCounter >= 0; --monthCounter) {
                    if (frequency[monthCounter] == 1) {
                        final int nearbyMonthIndexFromSpinner =
                                viewModel.getNearestMonthPositionFromSpinner(this, list, monthCounter);

                        nearbyMonthFound = true;

                        if (nearbyMonthIndexFromSpinner >= -1) {
                            binding.monthsSpinner.setSelection(nearbyMonthIndexFromSpinner);
                        }

                        break;
                    }
                }

                if (!nearbyMonthFound) {
                    for (int monthCounter = currentMonthIndex; monthCounter < frequency.length; ++monthCounter) {
                        if (frequency[monthCounter] == 1) {
                            final int nearbyMonthIndexFromSpinner =
                                    viewModel.getNearestMonthPositionFromSpinner(this, list, monthCounter);

                            if (nearbyMonthIndexFromSpinner >= -1) {
                                binding.monthsSpinner.setSelection(nearbyMonthIndexFromSpinner);
                            }

                            break;
                        }
                    }
                }
            }
            // if the current month is January
            else {
                for (int monthCounter = currentMonthIndex; monthCounter < frequency.length; ++monthCounter) {
                    if (frequency[monthCounter] == 1) {
                        final int nearbyMonthIndexFromSpinner =
                                viewModel.getNearestMonthPositionFromSpinner(this, list, monthCounter);

                        if (nearbyMonthIndexFromSpinner >= -1) {
                            binding.monthsSpinner.setSelection(nearbyMonthIndexFromSpinner);
                        }

                        break;
                    }
                }
            }
        }
    }

    private void createYearSpinner(final ArrayList<String> list) {
        // creating and setting the year spinner adapter's styling
        final ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, list) {
            @Override
            public View getDropDownView(final int position,
                                        final @Nullable View convertView,
                                        final @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);

                final boolean darkThemeEnabled = userDetails != null ?
                        userDetails.getApplicationSettings().isDarkThemeEnabled() :
                        MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

                final int itemsColor = !darkThemeEnabled ? Color.WHITE : Color.BLACK;
                // all spinner elements are aligned to center
                ((TextView) v).setGravity(Gravity.CENTER);
                // setting text color based on the selected theme
                ((TextView) v).setTextColor(itemsColor);

                return v;
            }
        };

        binding.yearsSpinner.setAdapter(yearAdapter);

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
            binding.yearsSpinner.setSelection(positionOfCurrentYear);
        }
    }

    private void populateRecyclerView(final String selectedYear,
                                      final String selectedMonth) {
        if (!transactionsList.isEmpty()) {
            transactionsList.clear();
            recyclerViewAdapter.notifyItemRangeRemoved(0, recyclerViewAdapter.getItemCount());
        }

        allTransactionsList.forEach((final Transaction transaction) -> {
            final String transactionMonthParsed = transaction.getTime().getMonthName().charAt(0) +
                    transaction.getTime().getMonthName().substring(1).toLowerCase();

            final String transactionYear = String.valueOf(transaction.getTime().getYear());

            final String selectedMonthNameInEnglish = Months.getMonthInEnglish(this, selectedMonth);

            if (transactionYear.equals(selectedYear) &&
                    transactionMonthParsed.equals(selectedMonthNameInEnglish)) {
                transactionsList.add(transaction);
            }
        });

        transactionsList.sort((final Transaction transaction1, final Transaction transaction2) ->
                Float.compare(Float.parseFloat(transaction2.getValue()), Float.parseFloat(transaction1.getValue())));

        recyclerViewAdapter.notifyItemRangeChanged(0, transactionsList.size());
    }

    public void removeEditSpecificTransactionFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(viewModel.getEditSpecificTransactionFragment())
                .commit();

        viewModel.setEditSpecificTransactionFragment(null);

        binding.editSpecificTransactionFragmentLayout.setVisibility(View.INVISIBLE);
        binding.mainLayout.setVisibility(View.VISIBLE);
    }

    private void setBottomLayout() {
        if (MyCustomVariables.getFirebaseAuth().getUid() == null) {
            return;
        }

        MyCustomVariables.getDatabaseReference()
                .child(MyCustomVariables.getFirebaseAuth().getUid())
                .child("personalTransactions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final @NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() &&
                                snapshot.hasChildren()) {
                            final ArrayList<String> yearsList = new ArrayList<>();

                            final ArrayList<String> monthsList = new ArrayList<>();

                            if (!allTransactionsList.isEmpty()) {
                                allTransactionsList.clear();
                            }

                            snapshot.getChildren().forEach((final DataSnapshot transaction) -> {
                                final Transaction databaseTransaction = transaction.getValue(Transaction.class);

                                if (databaseTransaction != null && databaseTransaction.getTime() != null) {
                                    final String transactionYear =
                                            String.valueOf(databaseTransaction.getTime().getYear());

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

                                    allTransactionsList.add(databaseTransaction);
                                }
                            });

                            createYearSpinner(yearsList);

                            binding.yearsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(final AdapterView<?> parent,
                                                           final View view,
                                                           final int position,
                                                           final long id) {
                                    final String selectedYear =
                                            String.valueOf(binding.yearsSpinner.getItemAtPosition(position));

                                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);

                                    if (!monthsList.isEmpty()) {
                                        monthsList.clear();
                                    }

                                    allTransactionsList.forEach((final Transaction transaction) -> {
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
                                    });

                                    createMonthSpinner(monthsList, selectedYear);

                                    binding.monthsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(final AdapterView<?> parent,
                                                                   final View view,
                                                                   final int position,
                                                                   final long id) {
                                            final String selectedMonth =
                                                    String.valueOf(binding.monthsSpinner.getItemAtPosition(position));

                                            ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                                            ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);

                                            if (!monthsList.isEmpty()) {
                                                binding.centerText.setText("");
                                                binding.recyclerView.setEnabled(true);
                                                binding.recyclerView.setVisibility(View.VISIBLE);
                                                populateRecyclerView(selectedYear, selectedMonth);
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(final AdapterView<?> parent) {

                                        }
                                    });
                                }

                                @Override
                                public void onNothingSelected(final AdapterView<?> parent) {

                                }
                            });
                        } else {
                            binding.monthsSpinner.setEnabled(false);
                            binding.yearsSpinner.setEnabled(false);
                            binding.bottomLayout.setVisibility(View.GONE);
                            binding.monthsSpinner.setVisibility(View.GONE);
                            binding.yearsSpinner.setVisibility(View.GONE);
                            binding.centerText.setText(getResources()
                                    .getString(R.string.no_transactions_made_yet).trim());
                        }

                        final boolean darkThemeEnabled = userDetails != null ?
                                userDetails.getApplicationSettings().isDarkThemeEnabled() :
                                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

                        binding.centerText.setTextColor(!darkThemeEnabled ? getColor(R.color.turkish_sea) : Color.WHITE);
                        binding.centerText.setTextSize(20);
                        binding.recyclerView.setEnabled(false);
                        binding.recyclerView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void setEditSpecificTransactionFragment() {
        viewModel.setEditSpecificTransactionFragment(new EditSpecificTransactionFragment());

        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.editSpecificTransactionFragmentLayout.getId(),
                        viewModel.getEditSpecificTransactionFragment())
                .commit();

        binding.mainLayout.setVisibility(View.INVISIBLE);
        binding.editSpecificTransactionFragmentLayout.setVisibility(View.VISIBLE);
    }

    private void setRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void setTheme() {
        final boolean isDarkThemeEnabled = userDetails != null ?
                userDetails.getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

        binding.setActivityBackgroundColor(Theme.getBackgroundColor(this, isDarkThemeEnabled));
        binding.setDropdownBackgroundColor(Theme.getDropdownBackgroundColor(this, isDarkThemeEnabled));

        // setting arrow color
        binding.monthsSpinner.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        // setting elements' color
        binding.yearsSpinner.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }

    private void setVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.edit_transactions_activity);
        userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);
        viewModel = new ViewModelProvider(this).get(EditTransactionsViewModel.class);
        recyclerViewAdapter = new EditTransactionsRecyclerViewAdapter(viewModel,
                transactionsList,
                this,
                userDetails,
                binding.recyclerView,
                getSupportFragmentManager());

        binding.setActivity(this);
    }

    public void setUserDetailsInViewModel(final UserDetails details) {
        viewModel.setUserDetails(details);
    }
}