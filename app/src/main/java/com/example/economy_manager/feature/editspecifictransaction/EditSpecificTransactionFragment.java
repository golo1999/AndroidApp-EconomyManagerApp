package com.example.economy_manager.feature.editspecifictransaction;

import android.content.res.ColorStateList;
import android.graphics.Color;
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
import com.example.economy_manager.utility.TransactionTypesSpinnerAdapter;
import com.example.economy_manager.utility.Types;

import java.time.LocalDate;
import java.time.LocalTime;

public class EditSpecificTransactionFragment extends Fragment {
    private EditSpecificTransactionFragmentBinding binding;
    private EditTransactionsViewModel viewModel;

    /**
     * The received date & time from the pickers are sent to the parent activity
     * Interfaces for passing the received date & time from EditTransactionsActivity (parent) to this fragment (child)
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
        setVariables(inflater, container);
        viewModel.setBinding(binding);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(final @NonNull View view,
                              final @Nullable Bundle savedInstanceState) {
        setFragmentTheme();
        setSaveChangesButtonStyle(viewModel.getUserDetails() != null &&
                viewModel.getUserDetails().getApplicationSettings().isDarkThemeEnabled());
        setDateText(viewModel.getUserDetails() != null ?
                LocalDate.of(viewModel.getSelectedTransaction().getTime().getYear(),
                        viewModel.getSelectedTransaction().getTime().getMonth(),
                        viewModel.getSelectedTransaction().getTime().getDay()) : viewModel.getTransactionDate());
        setTimeText(viewModel.getUserDetails() != null ?
                LocalTime.of(viewModel.getSelectedTransaction().getTime().getHour(),
                        viewModel.getSelectedTransaction().getTime().getMinute(),
                        viewModel.getSelectedTransaction().getTime().getSecond()) : viewModel.getTransactionTime());
        setOnFocusChangeListener(viewModel.getSelectedTransaction());
        setFragmentTitle();
        viewModel.resetTransactionTypesList();
        viewModel.setTransactionTypesList(requireContext());
        createTransactionTypesSpinner();
        setFieldHints();
    }

    private void createTransactionTypesSpinner() {
        final TransactionTypesSpinnerAdapter transactionTypesSpinnerAdapter =
                new TransactionTypesSpinnerAdapter(requireContext(),
                        R.layout.custom_spinner_item,
                        viewModel.getTransactionTypesList());

        final AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent,
                                       final View view,
                                       final int position,
                                       final long id) {
                final boolean darkTheme = MyCustomVariables.getUserDetails() != null ?
                        MyCustomVariables.getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                        MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

                final int textColor = !darkTheme ? requireContext().getColor(R.color.turkish_sea) : Color.WHITE;

                ((TextView) parent.getChildAt(0)).setTextColor(textColor);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.START);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        };

        binding.typeSpinner.setAdapter(transactionTypesSpinnerAdapter);
        binding.typeSpinner.setOnItemSelectedListener(listener);
    }

    public void setDateText(final LocalDate date) {
        final String formattedDate = MyCustomMethods.getFormattedDate(date);

        if (!viewModel.getTransactionDate().equals(date)) {
            viewModel.setTransactionDate(date);
        }

        binding.dateText.setText(formattedDate);
    }

    private void setDateTextColor(final int color) {
        final int drawableStartIcon = color == requireContext().getColor(R.color.turkish_sea) ?
                R.drawable.ic_calendar_blue : R.drawable.ic_calendar_white;

        binding.dateText.setTextColor(color);
        binding.dateText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStartIcon, 0, 0, 0);
    }

    private void setFieldHints() {
        if (viewModel.getSelectedTransaction() != null) {
            final Transaction selectedTransaction = viewModel.getSelectedTransaction();

            final String translatedType = Types.getTranslatedType(requireContext(),
                    String.valueOf(Transaction.getTypeFromIndexInEnglish(selectedTransaction.getCategory())));

            int positionInTheTransactionTypesList = viewModel.getTransactionPositionInList(translatedType);

            binding.noteField.setHint(selectedTransaction.getNote() != null ?
                    selectedTransaction.getNote() : requireContext().getResources().getString(R.string.note));

            binding.valueField.setHint(selectedTransaction.getValue() != null ?
                    selectedTransaction.getValue() : requireContext().getResources().getString(R.string.value));

            if (positionInTheTransactionTypesList != -1) {
                binding.typeSpinner.setSelection(positionInTheTransactionTypesList);
            }
        }
    }

    private void setFragmentTheme() {
        final boolean darkTheme = MyCustomVariables.getUserDetails() != null ?
                MyCustomVariables.getUserDetails().getApplicationSettings().isDarkThemeEnabled() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().isDarkThemeEnabled();

        final int color = !darkTheme ? requireContext().getColor(R.color.turkish_sea) : Color.WHITE;

        final int backgroundTheme = !darkTheme ?
                R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift;

        final int spinnerElementBackground = !darkTheme ?
                R.drawable.ic_blue_gradient_unloved_teen : R.drawable.ic_white_gradient_tobacco_ad;

        setTextStyleEditText(binding.noteField, color);
        setTextStyleEditText(binding.valueField, color);
        setDateTextColor(color);
        setTimeTextColor(color);

        requireActivity().getWindow().setBackgroundDrawableResource(backgroundTheme);
        // setting arrow's color
        binding.typeSpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        // setting items' color
        binding.typeSpinner.setPopupBackgroundResource(spinnerElementBackground);
    }

    private void setFragmentTitle() {
        final String editSpecificTransactionText =
                requireContext().getResources().getString(R.string.edit_specific_transaction_title).trim();

        if (viewModel.getActivityTitle() == null ||
                !viewModel.getActivityTitle().equals(editSpecificTransactionText)) {
            viewModel.setActivityTitle(editSpecificTransactionText);
        }

        binding.title.setText(viewModel.getActivityTitle());
        binding.title.setTextColor(Color.WHITE);
        binding.title.setTextSize(18);
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

    private void setSaveChangesButtonStyle(final boolean darkThemeEnabled) {
        final int background = !darkThemeEnabled ? R.drawable.button_blue_border : R.drawable.button_white_border;

        final int textColor = requireContext().getColor(!darkThemeEnabled ? R.color.turkish_sea : R.color.white);

        binding.saveChangesButton.setBackgroundResource(background);
        binding.saveChangesButton.setTextColor(textColor);
    }

    private void setTextStyleEditText(final @NonNull EditText editText,
                                      final int color) {
        editText.setTextColor(color);
        editText.setHintTextColor(color);
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public void setTimeText(final LocalTime time) {
        final String formattedTime = MyCustomMethods.getFormattedTime(requireContext(), time);

        if (!viewModel.getTransactionTime().equals(time)) {
            viewModel.setTransactionTime(time);
        }

        binding.timeText.setText(formattedTime);
    }

    private void setTimeTextColor(final int color) {
        final int drawableStartIcon = color == requireContext().getColor(R.color.turkish_sea) ?
                R.drawable.ic_time_blue : R.drawable.ic_time_white;

        binding.timeText.setTextColor(color);
        binding.timeText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStartIcon, 0, 0, 0);
    }

    private void setVariables(final @NonNull LayoutInflater inflater,
                              final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_specific_transaction_fragment, container, false);
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(EditTransactionsViewModel.class);

        binding.setActivity((EditTransactionsActivity) requireActivity());
        binding.setFragmentManager(getChildFragmentManager());
        binding.setViewModel(viewModel);
    }
}