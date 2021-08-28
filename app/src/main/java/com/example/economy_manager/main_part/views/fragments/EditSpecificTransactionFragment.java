package com.example.economy_manager.main_part.views.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.viewmodels.EditTransactionsViewModel;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.example.economy_manager.utilities.Types;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

public class EditSpecificTransactionFragment extends Fragment {
    private EditTransactionsViewModel viewModel;
    private TextView titleText;
    private EditText noteField;
    private EditText valueField;
    private TextView dateText;
    private TextView timeText;
    private Spinner typeSpinner;
    private ImageView goBack;
    private Button saveChangesButton;

    /**
     * Interfaces for passing the received date & time from EditTransactionsActivity (parent) to this fragment (child)
     * The received date & time from the pickers are sent to the parent activity, not this fragment
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
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_specific_transaction_fragment, container, false);
    }

    @Override
    public void onViewCreated(final @NonNull View view,
                              final @Nullable Bundle savedInstanceState) {
        setVariables(view);
        setFragmentTheme();
        setSaveChangesButtonStyle(viewModel.getUserDetails() != null &&
                viewModel.getUserDetails().getApplicationSettings().getDarkTheme());
        setDateText(viewModel.getUserDetails() != null ?
                LocalDate.of(viewModel.getSelectedTransaction().getTime().getYear(),
                        viewModel.getSelectedTransaction().getTime().getMonth(),
                        viewModel.getSelectedTransaction().getTime().getDay()) : viewModel.getTransactionDate());
        setTimeText(viewModel.getUserDetails() != null ?
                LocalTime.of(viewModel.getSelectedTransaction().getTime().getHour(),
                        viewModel.getSelectedTransaction().getTime().getMinute(),
                        viewModel.getSelectedTransaction().getTime().getSecond()) : viewModel.getTransactionTime());
        setOnClickListeners();
        setOnFocusChangeListener(viewModel.getSelectedTransaction());
        setTitle();
        createTransactionTypesSpinner();
        setFieldHints();
    }

    private void setVariables(final @NonNull View view) {
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(EditTransactionsViewModel.class);
        goBack = view.findViewById(R.id.edit_specific_transaction_back);
        titleText = view.findViewById(R.id.edit_specific_transaction_title);
        noteField = view.findViewById(R.id.edit_specific_transaction_note_field);
        valueField = view.findViewById(R.id.edit_specific_transaction_value_field);
        dateText = view.findViewById(R.id.edit_specific_transaction_date_text);
        timeText = view.findViewById(R.id.edit_specific_transaction_time_text);
        typeSpinner = view.findViewById(R.id.edit_specific_transaction_type_spinner);
        saveChangesButton = view.findViewById(R.id.edit_specific_transaction_save_button);
    }

    private void setOnClickListeners() {
        goBack.setOnClickListener((final View v) -> requireActivity().onBackPressed());

        dateText.setOnClickListener((final View view) -> {
            final int selectedTransactionYear = viewModel.getSelectedTransaction().getTime().getYear();

            final int selectedTransactionMonth = viewModel.getSelectedTransaction().getTime().getMonth();

            final int selectedTransactionDay = viewModel.getSelectedTransaction().getTime().getDay();

            final LocalDate selectedTransactionDate =
                    LocalDate.of(selectedTransactionYear, selectedTransactionMonth, selectedTransactionDay);

            final DialogFragment datePickerFragment = new DatePickerFragment(selectedTransactionDate);

            datePickerFragment.show(getChildFragmentManager(), "date_picker");
        });

        saveChangesButton.setOnClickListener((final View v) -> {
            MyCustomMethods.closeTheKeyboard(requireActivity());

            if (MyCustomVariables.getUserDetails() != null) {
                Transaction selectedTransaction = viewModel.getSelectedTransaction();

                if (selectedTransaction != null) {
                    final String editedNote = !String.valueOf(noteField.getText()).trim().isEmpty() ?
                            String.valueOf(noteField.getText()).trim() : selectedTransaction.getNote();

                    final String editedValue = !String.valueOf(valueField.getText()).trim().isEmpty() ?
                            String.valueOf(valueField.getText()).trim() : selectedTransaction.getValue();

                    final LocalDate editedDate = viewModel.getTransactionDate();

                    final int parsedCategoryIndex = Transaction.getIndexFromCategory(Types.
                            getTypeInEnglish(requireContext(), String.valueOf(typeSpinner.getSelectedItem()).trim()));

                    final int editedCategoryIndex = parsedCategoryIndex >= 0 && parsedCategoryIndex <= 18 &&
                            parsedCategoryIndex != selectedTransaction.getCategory() ?
                            parsedCategoryIndex : selectedTransaction.getCategory();

//                    final int datePickerSelectedYear = datePicker.getYear();
//
//                    final int datePickerSelectedMonth = datePicker.getMonth();
//
//                    final int datePickerSelectedDay = datePicker.getDayOfMonth();
//
//                    final int timePickerSelectedHour = timePicker.getHour();
//
//                    final int timePickerSelectedMinute = timePicker.getMinute();
//
//                    final int timePickerSelectedSecond =
//                            (datePickerSelectedYear != selectedTransaction.getTime().getYear() &&
//                                    datePickerSelectedMonth != selectedTransaction.getTime().getMonth() &&
//                                    datePickerSelectedDay != selectedTransaction.getTime().getDay() &&
//                                    timePickerSelectedHour != selectedTransaction.getTime().getHour() &&
//                                    timePickerSelectedMinute != selectedTransaction.getTime().getMinute()) ?
//                                    LocalDateTime.now().getSecond() : selectedTransaction.getTime().getSecond();
//
//                    final LocalDate selectedLocalDate =
//                            LocalDate.of(datePickerSelectedYear, datePickerSelectedMonth + 1, datePickerSelectedDay);
//
//                    final MyCustomTime editedTime = new MyCustomTime(datePickerSelectedYear, datePickerSelectedMonth,
//                            String.valueOf(selectedLocalDate.getMonth()), datePickerSelectedDay,
//                            String.valueOf(selectedLocalDate.getDayOfWeek()), timePickerSelectedHour,
//                            timePickerSelectedMinute, timePickerSelectedSecond);
//
//                    final int editedCategoryType = (editedCategoryIndex >= 0 && editedCategoryIndex <= 3) ? 1 : 0;
//
//                    final Transaction editedTransaction = new Transaction(selectedTransaction.getId(),
//                            editedCategoryIndex, editedTime, editedCategoryType, editedNote, editedValue);
//
//                    Log.d("editedInitialTransaction", transactionHasBeenModified(selectedTransaction).toString());
//
//                    Log.d("selectedTransaction", selectedTransaction.toString());
//
//                    Log.d("editedTransaction", editedTransaction.toString());
//
//                    //Toast.makeText(requireContext(), String.valueOf(selectedTransaction.equals(editedTransaction)), Toast.LENGTH_SHORT).show();
//
//                    if (!selectedTransaction.equals(editedTransaction) &&
//                            viewModel.getEditTransactionsRecyclerViewAdapter() != null) {
//                        viewModel.getEditTransactionsRecyclerViewAdapter()
//                                .notifyItemChanged(viewModel.getSelectedTransactionListPosition());
//                    }
                }
            }

            requireActivity().onBackPressed();
        });

        timeText.setOnClickListener((final View view) -> {
            final int selectedTransactionHour = viewModel.getSelectedTransaction().getTime().getHour();

            final int selectedTransactionMinute = viewModel.getSelectedTransaction().getTime().getMinute();

            final int selectedTransactionSecond = viewModel.getSelectedTransaction().getTime().getSecond();

            final LocalTime selectedTransactionTime =
                    LocalTime.of(selectedTransactionHour, selectedTransactionMinute, selectedTransactionSecond);

            final DialogFragment timePickerFragment = new TimePickerFragment(selectedTransactionTime);

            timePickerFragment.show(getChildFragmentManager(), "time_picker");
        });
    }

    private void setTitle() {
        final String editSpecificTransactionText =
                requireContext().getResources().getString(R.string.edit_specific_transaction_title).trim();

        if (viewModel.getActivityTitle() == null || !viewModel.getActivityTitle().equals(editSpecificTransactionText)) {
            viewModel.setActivityTitle(editSpecificTransactionText);
        }

        titleText.setText(viewModel.getActivityTitle());
        titleText.setTextColor(Color.WHITE);
        titleText.setTextSize(18);
    }

    private void setFieldHints() {
        if (viewModel.getSelectedTransaction() != null) {
            final Transaction selectedTransaction = viewModel.getSelectedTransaction();

            final String translatedType = Types.getTranslatedType(requireContext(),
                    String.valueOf(Transaction.getTypeFromIndexInEnglish(selectedTransaction.getCategory())));

            final ArrayList<String> transactionTypesList = new ArrayList<>();

            int positionInTheTransactionTypesList = -1;

            noteField.setHint(selectedTransaction.getNote() != null ?
                    selectedTransaction.getNote() : requireContext().getResources().getString(R.string.note));
            valueField.setHint(selectedTransaction.getValue() != null ?
                    selectedTransaction.getValue() : requireContext().getResources().getString(R.string.value));

            populateTransactionTypesList(transactionTypesList);

            for (final String type : transactionTypesList) {
                ++positionInTheTransactionTypesList;

                if (type.equals(translatedType)) {
                    break;
                }
            }

            if (positionInTheTransactionTypesList != -1) {
                typeSpinner.setSelection(positionInTheTransactionTypesList);
            }
        }
    }

    private void populateTransactionTypesList(final @NonNull ArrayList<String> list) {
        list.add(requireContext().getResources().getString(R.string.add_money_deposits).trim());
        list.add(requireContext().getResources().getString(R.string.add_money_independent_sources).trim());
        list.add(requireContext().getResources().getString(R.string.salary).trim());
        list.add(requireContext().getResources().getString(R.string.saving).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_bills).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_car).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_clothes).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_communications).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_eating_out).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_entertainment).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_food).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_gifts).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_health).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_house).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_pets).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_sports).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_taxi).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_toiletry).trim());
        list.add(requireContext().getResources().getString(R.string.subtract_money_transport).trim());

        Collections.sort(list);
    }

    private void createTransactionTypesSpinner() {
        final ArrayList<String> types = new ArrayList<>();

        final ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(requireContext(),
                R.layout.custom_spinner_item, types) {
            @Override
            public View getDropDownView(final int position,
                                        final @Nullable View convertView,
                                        final @NonNull ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);

                final boolean darkTheme = MyCustomVariables.getUserDetails() != null ?
                        MyCustomVariables.getUserDetails().getApplicationSettings().getDarkTheme() :
                        MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

                final int itemsColor = !darkTheme ? Color.WHITE : Color.BLACK;

                ((TextView) v).setGravity(Gravity.CENTER);
                // setting items' text color based on the selected theme
                ((TextView) v).setTextColor(itemsColor);

                return v;
            }
        };

        final AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent,
                                       final View view,
                                       final int position,
                                       final long id) {
                final boolean darkTheme = MyCustomVariables.getUserDetails() != null ?
                        MyCustomVariables.getUserDetails().getApplicationSettings().getDarkTheme() :
                        MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

                final int textColor = !darkTheme ? requireContext().getColor(R.color.turkish_sea) : Color.WHITE;

                ((TextView) parent.getChildAt(0)).setTextColor(textColor);
                ((TextView) parent.getChildAt(0)).setGravity(Gravity.START);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        };

        types.add(requireContext().getResources().getString(R.string.subtract_money_bills).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_car).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_clothes).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_communications).trim());
        types.add(requireContext().getResources().getString(R.string.add_money_deposits).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_eating_out).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_entertainment).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_food).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_gifts).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_health).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_house).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_pets).trim());
        types.add(requireContext().getResources().getString(R.string.add_money_independent_sources).trim());
        types.add(requireContext().getResources().getString(R.string.salary).trim());
        types.add(requireContext().getResources().getString(R.string.saving).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_sports).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_taxi).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_toiletry).trim());
        types.add(requireContext().getResources().getString(R.string.subtract_money_transport).trim());

        Collections.sort(types);

        typeSpinner.setAdapter(typesAdapter);
        typeSpinner.setOnItemSelectedListener(listener);
    }

    private void setFragmentTheme() {
        final boolean darkTheme = MyCustomVariables.getUserDetails() != null ?
                MyCustomVariables.getUserDetails().getApplicationSettings().getDarkTheme() :
                MyCustomVariables.getDefaultUserDetails().getApplicationSettings().getDarkTheme();

        final int color = !darkTheme ? requireContext().getColor(R.color.turkish_sea) : Color.WHITE;

        final int backgroundTheme = !darkTheme ?
                R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift;

        final int spinnerElementBackground = !darkTheme ?
                R.drawable.ic_blue_gradient_unloved_teen : R.drawable.ic_white_gradient_tobacco_ad;

        setTextStyleEditText(noteField, color);
        setTextStyleEditText(valueField, color);
        setDateTextColor(color);
        setTimeTextColor(color);

        requireActivity().getWindow().setBackgroundDrawableResource(backgroundTheme);
        // setting arrow's color
        typeSpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        // setting items' color
        typeSpinner.setPopupBackgroundResource(spinnerElementBackground);
    }

    private void setTextStyleEditText(final @NonNull EditText editText,
                                      final int color) {
        editText.setTextColor(color);
        editText.setHintTextColor(color);
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void setOnFocusChangeListener(final Transaction selectedTransaction) {
        final View.OnFocusChangeListener listener = (final View v, final boolean hasFocus) -> {
            if (hasFocus) {
                final String noteFieldText = selectedTransaction.getNote() != null ?
                        String.valueOf(((EditText) v).getHint()).trim() : "";

                ((EditText) v).setText(noteFieldText);
            }
        };

        noteField.setOnFocusChangeListener(listener);
    }

//    private Transaction transactionHasBeenModified(@NonNull final Transaction initialTransaction) {
//        final int initialMonthIndex = initialTransaction.getTime().getMonth();
//
//        final int datePickerSelectedYear = datePicker.getYear();
//
//        final int datePickerSelectedMonth = datePicker.getMonth() + 1;
//
//        final int datePickerSelectedDay = datePicker.getDayOfMonth();
//
//        final int timePickerSelectedHour = timePicker.getHour();
//
//        final int timePickerSelectedMinute = timePicker.getMinute();
//
//        final int timePickerSelectedSecond =
//                (datePickerSelectedYear != initialTransaction.getTime().getYear() &&
//                        datePickerSelectedMonth != initialTransaction.getTime().getMonth() &&
//                        datePickerSelectedDay != initialTransaction.getTime().getDay() &&
//                        timePickerSelectedHour != initialTransaction.getTime().getHour() &&
//                        timePickerSelectedMinute != initialTransaction.getTime().getMinute()) ?
//                        LocalDateTime.now().getSecond() : initialTransaction.getTime().getSecond();
//
//        final LocalDate selectedTimeLocalDate =
//                LocalDate.of(datePickerSelectedYear, datePickerSelectedMonth, datePickerSelectedDay);
//
//        final MyCustomTime editedTime = new MyCustomTime(datePickerSelectedYear, datePickerSelectedMonth,
//                String.valueOf(selectedTimeLocalDate.getMonth()), datePickerSelectedDay,
//                String.valueOf(selectedTimeLocalDate.getDayOfWeek()), timePickerSelectedHour, timePickerSelectedMinute,
//                timePickerSelectedSecond);
//
//        boolean hasBeenModified = false;
//
//        if ((initialTransaction.getNote() != null &&
//                !String.valueOf(noteField.getText()).trim().equals(initialTransaction.getNote().trim())) ||
//                (initialTransaction.getNote() == null && !String.valueOf(noteField.getText()).trim().isEmpty())) {
//            Log.d("initialNote", initialTransaction.getNote() != null ? initialTransaction.getNote() : "null");
//            Log.d("editedNote", String.valueOf(noteField.getText()).trim());
//
//            final String editedNote = !String.valueOf(noteField.getText()).trim().isEmpty() ?
//                    String.valueOf(noteField.getText()).trim() : null;
//
//            initialTransaction.setNote(editedNote);
//
//            hasBeenModified = true;
//        }
//
//        if (!String.valueOf(valueField.getText()).trim().equals(String.valueOf(valueField.getHint()).trim()) &&
//                !String.valueOf(valueField.getText()).trim().isEmpty()) {
//            Log.d("initialValue", initialTransaction.getValue().trim());
//            Log.d("editedValue", String.valueOf(valueField.getText()).trim());
//
//            final String editedValue = String.valueOf(valueField.getText()).trim();
//
//            initialTransaction.setValue(editedValue);
//
//            if (!hasBeenModified) {
//                hasBeenModified = true;
//            }
//        }
//
//        if (!editedTime.equals(initialTransaction.getTime())) {
//            Log.d("initialTime", initialTransaction.getTime().toString());
//            Log.d("editedTime", editedTime.toString());
//
//            initialTransaction.setTime(editedTime);
//
//            if (!hasBeenModified) {
//                hasBeenModified = true;
//            }
//        }
//
//        final String initialCategoryName = Types.getTranslatedType(requireContext(),
//                Transaction.getTypeFromIndexInEnglish(initialTransaction.getCategory()));
//
//        final String selectedCategoryName = String.valueOf(typeSpinner.getSelectedItem()).trim();
//
//        if (initialCategoryName != null && !initialCategoryName.equals(selectedCategoryName)) {
//            Log.d("initialCategory", initialCategoryName);
//            Log.d("editedCategory", selectedCategoryName);
//
//            final int editedCategory =
//                    Transaction.getIndexFromCategory(Types.getTypeInEnglish(requireContext(), selectedCategoryName));
//
//            final int editedType = editedCategory >= 0 && editedCategory <= 3 ? 1 : 0;
//
//            initialTransaction.setCategory(editedCategory);
//
//            if (editedType != initialTransaction.getType()) {
//                initialTransaction.setType(editedType);
//            }
//
//            if (!hasBeenModified) {
//                hasBeenModified = true;
//            }
//        }
//
//        // updating the transaction in the Firebase database
//        if (hasBeenModified && MyCustomVariables.getFirebaseAuth().getUid() != null) {
//            MyCustomVariables.getDatabaseReference()
//                    .child(MyCustomVariables.getFirebaseAuth().getUid())
//                    .child("PersonalTransactions")
//                    .child(initialTransaction.getId())
//                    .setValue(initialTransaction);
//        }
//
//        return initialTransaction;
//    }

    private void setSaveChangesButtonStyle(final boolean darkThemeEnabled) {
        final int background = !darkThemeEnabled ? R.drawable.button_blue_border : R.drawable.button_white_border;

        final int textColor = requireContext().getColor(!darkThemeEnabled ? R.color.turkish_sea : R.color.white);

        saveChangesButton.setBackgroundResource(background);
        saveChangesButton.setTextColor(textColor);
    }

    public void setDateText(final LocalDate date) {
        final String formattedDate = MyCustomMethods.getFormattedDate(date);

        if (!viewModel.getTransactionDate().equals(date)) {
            viewModel.setTransactionDate(date);
        }

        dateText.setText(formattedDate);
    }

    public void setTimeText(final LocalTime time) {
        final String formattedTime = MyCustomMethods.getFormattedTime(requireContext(), time);

        if (!viewModel.getTransactionTime().equals(time)) {
            viewModel.setTransactionTime(time);
        }

        timeText.setText(formattedTime);
    }

    private void setDateTextColor(final int color) {
        final int drawableStartIcon = color == requireContext().getColor(R.color.turkish_sea) ?
                R.drawable.ic_calendar_blue : R.drawable.ic_calendar_white;

        dateText.setTextColor(color);
        dateText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStartIcon, 0, 0, 0);
    }

    private void setTimeTextColor(final int color) {
        final int drawableStartIcon = color == requireContext().getColor(R.color.turkish_sea) ?
                R.drawable.ic_time_blue : R.drawable.ic_time_white;

        timeText.setTextColor(color);
        timeText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStartIcon, 0, 0, 0);
    }
}