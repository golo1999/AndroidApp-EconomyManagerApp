package com.example.economy_manager.feature.editspecifictransaction;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.EditSpecificTransactionFragmentBinding;
import com.example.economy_manager.feature.edittransactions.EditTransactionsActivity;
import com.example.economy_manager.feature.edittransactions.EditTransactionsViewModel;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.model.TransactionTypesSpinnerAdapter;
import com.example.economy_manager.utility.Types;

import java.time.LocalDate;
import java.time.LocalTime;

public class EditSpecificTransactionFragment extends Fragment {

    private EditSpecificTransactionFragmentBinding binding;
    private EditTransactionsViewModel viewModel;

    /**
     * The received date & time from the pickers are sent to the parent activity
     * Interfaces for passing the received date
     * and time from EditTransactionsActivity (parent) to this fragment (child)
     */
    public interface OnDateReceivedCallBack {
        void onDateReceived(final LocalDate newDate);
    }

    public interface OnTimeReceivedCallBack {
        void onTimeReceived(final LocalTime newTime);
    }

    public EditSpecificTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        setFragmentVariables(inflater, container);
        setLayoutVariables();
        viewModel.setBinding(binding);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(final @NonNull View view,
                              final @Nullable Bundle savedInstanceState) {
        setTheme();
        setDateText(viewModel.getUserDetails() != null ?
                LocalDate.of(viewModel.getSelectedTransaction().getTime().getYear(),
                        viewModel.getSelectedTransaction().getTime().getMonth(),
                        viewModel.getSelectedTransaction().getTime().getDay()) :
                viewModel.getTransactionDate());
        setTimeText(viewModel.getUserDetails() != null ?
                LocalTime.of(viewModel.getSelectedTransaction().getTime().getHour(),
                        viewModel.getSelectedTransaction().getTime().getMinute(),
                        viewModel.getSelectedTransaction().getTime().getSecond()) :
                viewModel.getTransactionTime());
        setOnFocusChangeListener(viewModel.getSelectedTransaction());
        viewModel.resetTransactionTypesList();
        viewModel.setTransactionTypesList(requireContext());
        setSpinner();
        setSpinnerOnSelectedItemListener();
        setFieldTexts();
    }

    public void setDateText(final LocalDate date) {
        final String formattedDate = MyCustomMethods.getFormattedDate(date);

        if (!viewModel.getTransactionDate().equals(date)) {
            viewModel.setTransactionDate(date);
        }

        binding.dateText.setText(formattedDate);
    }

    private void setFieldTexts() {
        if (viewModel.getSelectedTransaction() == null) {
            return;
        }

        final Transaction selectedTransaction = viewModel.getSelectedTransaction();
        final String translatedType = Types.getTranslatedType(requireContext(),
                String.valueOf(Transaction
                        .getTypeFromIndexInEnglish(selectedTransaction.getCategory())));
        int positionInTheTransactionTypesList =
                viewModel.getTransactionPositionInList(translatedType);

        if (selectedTransaction.getNote() != null) {
            binding.noteField.setText(selectedTransaction.getNote());
        }

        if (selectedTransaction.getValue() != null) {
            binding.valueField.setText(selectedTransaction.getValue());
        }

        if (positionInTheTransactionTypesList < 0) {
            return;
        }

        binding.typeSpinner.setSelection(positionInTheTransactionTypesList);
    }

    private void setFragmentVariables(final @NonNull LayoutInflater inflater,
                                      final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_specific_transaction_fragment,
                container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext())
                .get(EditTransactionsViewModel.class);
    }

    private void setLayoutVariables() {
        binding.setActivity((EditTransactionsActivity) requireActivity());
        binding.setFragmentManager(getChildFragmentManager());
        binding.setViewModel(viewModel);
    }

    private void setOnFocusChangeListener(final Transaction selectedTransaction) {
        final View.OnFocusChangeListener listener = (final View v, final boolean hasFocus) -> {
            if (hasFocus) {
                final String noteFieldText = selectedTransaction.getNote() != null ?
                        String.valueOf(((EditText) v).getHint()).trim() : "";

                ((EditText) v).setText(noteFieldText);
            }
        };

        binding.noteField.setOnFocusChangeListener(listener);
    }

    private void setSpinner() {
        final TransactionTypesSpinnerAdapter spinnerAdapter =
                new TransactionTypesSpinnerAdapter(requireContext(),
                        R.layout.custom_spinner_item,
                        viewModel.getTransactionTypesList());
        final int arrowColor = requireContext().getColor(binding.getIsDarkThemeEnabled() ?
                R.color.secondaryDark : R.color.quaternaryLight);

        binding.typeSpinner.setAdapter(spinnerAdapter);
        // setting arrow color
        binding.typeSpinner.getBackground().setColorFilter(arrowColor, PorterDuff.Mode.SRC_ATOP);
    }

    private void setSpinnerOnSelectedItemListener() {
        final AdapterView.OnItemSelectedListener listener = new AdapterView
                .OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull final AdapterView<?> parent,
                                       final View view,
                                       final int position,
                                       final long id) {
                final int textColor = requireContext().getColor(binding.getIsDarkThemeEnabled() ?
                        R.color.secondaryDark : R.color.quaternaryLight);

                ((TextView) parent.getChildAt(0)).setTextColor(textColor);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.START);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        };

        binding.typeSpinner.setOnItemSelectedListener(listener);
    }

    private void setTheme() {
        final boolean isDarkThemeEnabled = MyCustomVariables.getUserDetails() != null ?
                MyCustomVariables.getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings()
                        .isDarkThemeEnabled();

        binding.setIsDarkThemeEnabled(isDarkThemeEnabled);
    }

    public void setTimeText(final LocalTime time) {
        if (!viewModel.getTransactionTime().equals(time)) {
            viewModel.setTransactionTime(time);
        }

        binding.timeText.setText(MyCustomMethods.getFormattedTime(requireContext(), time));
    }
}