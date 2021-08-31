package com.example.economy_manager.main_part.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.viewmodels.MainScreenViewModel;
import com.example.economy_manager.models.MyCustomTime;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

public class MoneySpentFragment extends Fragment {
    private MainScreenViewModel viewModel;
    private TextView moneySpentText;

    public MoneySpentFragment() {
        // Required empty public constructor
    }

    public static MoneySpentFragment newInstance() {
        final MoneySpentFragment fragment = new MoneySpentFragment();
        final Bundle args = new Bundle();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.money_spent_fragment, container, false);

        setVariables(v);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        setLastWeekExpenses();
    }

    private void setVariables(final View v) {
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(MainScreenViewModel.class);
        moneySpentText = v.findViewById(R.id.money_spent_week);
    }

    private void setLastWeekExpenses() {
        if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
            MyCustomVariables.getDatabaseReference()
                    .child(MyCustomVariables.getFirebaseAuth().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final @NonNull DataSnapshot snapshot) {
                            final String currencySymbol = viewModel.getUserDetails() != null ?
                                    viewModel.getUserDetails().getApplicationSettings().getCurrencySymbol() :
                                    MyCustomMethods.getCurrencySymbol();

                            float moneySpentLastWeek = 0f;

                            if (snapshot.exists() &&
                                    snapshot.hasChild("PersonalTransactions") &&
                                    snapshot.child("PersonalTransactions").hasChildren()) {
                                for (final DataSnapshot transactionIterator :
                                        snapshot.child("PersonalTransactions").getChildren()) {
                                    final Transaction transaction = transactionIterator.getValue(Transaction.class);

                                    if (transaction != null &&
                                            transaction.getType() == 0 &&
                                            wasMadeInTheLastWeek(transaction.getTime())) {
                                        moneySpentLastWeek += Float.parseFloat(transaction.getValue());
                                    }
                                }
                            }

                            // if the user hasn't spent money in the last week
                            if (moneySpentLastWeek <= 0f) {
                                moneySpentText.setText(R.string.no_money_last_week);
                            }
                            // if the user spent money in the last week
                            else {
                                if (String.valueOf(moneySpentLastWeek).contains(".") &&
                                        String.valueOf(moneySpentLastWeek).length() -
                                                String.valueOf(moneySpentLastWeek).indexOf(".") > 3) {
                                    moneySpentLastWeek = Float.parseFloat(String.format(Locale.getDefault(),
                                            "%.2f", moneySpentLastWeek));
                                }

                                final boolean languageIsEnglish =
                                        Locale.getDefault().getDisplayLanguage().equals("English");

                                final String youSpentText = languageIsEnglish ?
                                        // english
                                        requireContext().getResources().getString(R.string.money_spent_you_spent) +
                                                " " + currencySymbol + moneySpentLastWeek + " " +
                                                requireContext().getResources().getString(R.string.money_spent_last_week) :
                                        // everything else
                                        requireContext().getResources().getString(R.string.money_spent_you_spent) +
                                                " " + moneySpentLastWeek + " " + currencySymbol + " " +
                                                requireContext().getResources().getString(R.string.money_spent_last_week);

                                moneySpentText.setText(youSpentText.trim());
                            }
                        }

                        @Override
                        public void onCancelled(final @NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private boolean wasMadeInTheLastWeek(final @NonNull MyCustomTime transactionTime) {
        final LocalDate oneWeekAgoDate = LocalDate.now().minusDays(8);

        final LocalDateTime oneWeekAgoDateTime = LocalDateTime.of(oneWeekAgoDate, LocalTime.of(23, 59, 59));

        final LocalDate nextDayDate = LocalDate.now().plusDays(1);

        final LocalDateTime nextDayDateTime = LocalDateTime.of(nextDayDate, LocalTime.of(0, 0));

        final LocalDateTime transactionTimeParsed = LocalDateTime.of(transactionTime.getYear(),
                transactionTime.getMonth(), transactionTime.getDay(), transactionTime.getHour(),
                transactionTime.getMinute(), transactionTime.getSecond());

        final boolean isAfterOneWeekAgoDate = transactionTimeParsed.isAfter(oneWeekAgoDateTime);

        final boolean isBeforeCurrentDate = transactionTimeParsed.isBefore(nextDayDateTime);

        return isAfterOneWeekAgoDate && isBeforeCurrentDate;
    }
}