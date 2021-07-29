package com.example.economy_manager.main_part.views.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.viewmodels.EditTransactionsViewModel;
import com.example.economy_manager.main_part.views.activities.EditTransactionsActivity;
import com.example.economy_manager.models.MyCustomTime;
import com.example.economy_manager.models.Transaction;
import com.example.economy_manager.utilities.MyCustomMethods;
import com.example.economy_manager.utilities.MyCustomVariables;
import com.example.economy_manager.utilities.Types;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class EditSpecificTransactionFragment extends Fragment {
    private EditTransactionsViewModel viewModel;
    private TextView titleText;
    private EditText noteField;
    private EditText valueField;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Spinner typeSpinner;
    private ImageView goBack;
    private Button saveChangesButton;

    public EditSpecificTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_specific_transaction_fragment, container, false);
    }

    @Override
    public void onViewCreated(final @NonNull View view, final @Nullable Bundle savedInstanceState) {
        setVariables(view);
        setTheme();
        setDatePicker(viewModel.getSelectedTransaction());
        setTimePicker(viewModel.getSelectedTransaction());
        setOnClickListeners();
        //setOnFocusChangeListener();
        setTitle();
        createTransactionTypesSpinner();
        setHints();
    }

    private void setVariables(@NonNull final View view) {
        viewModel = new ViewModelProvider((ViewModelStoreOwner) requireContext()).get(EditTransactionsViewModel.class);
        goBack = view.findViewById(R.id.editSpecificTransactionBack);
        titleText = view.findViewById(R.id.editSpecificTransactionTitle);
        noteField = view.findViewById(R.id.editSpecificTransactionNoteEdit);
        valueField = view.findViewById(R.id.editSpecificTransactionValueEdit);
        datePicker = view.findViewById(R.id.editSpecificTransactionDateEdit);
        timePicker = view.findViewById(R.id.editSpecificTransactionTimeEdit);
        typeSpinner = view.findViewById(R.id.editSpecificTransactionTypeSpinner);
        saveChangesButton = view.findViewById(R.id.editSpecificTransactionSave);
    }

    private void setOnClickListeners() {
        goBack.setOnClickListener(v -> requireActivity().onBackPressed());

        saveChangesButton.setOnClickListener(v -> {
            MyCustomMethods.closeTheKeyboard(requireActivity());

            if (MyCustomVariables.getUserDetails() != null) {
                Transaction selectedTransaction = viewModel.getSelectedTransaction();

                if (selectedTransaction != null) {
                    final String editedNote = !String.valueOf(noteField.getText()).trim().isEmpty() ?
                            String.valueOf(noteField.getText()).trim() : selectedTransaction.getNote();
                    final String editedValue = !String.valueOf(valueField.getText()).trim().isEmpty() ?
                            String.valueOf(valueField.getText()).trim() : selectedTransaction.getValue();
                    final int parsedCategoryIndex = Transaction.getIndexFromCategory(Types.
                            getTypeInEnglish(requireContext(), String.valueOf(typeSpinner.getSelectedItem()).trim()));
                    final int editedCategoryIndex = parsedCategoryIndex >= 0 && parsedCategoryIndex <= 18 &&
                            parsedCategoryIndex != selectedTransaction.getCategory() ?
                            parsedCategoryIndex : selectedTransaction.getCategory();
                    final int datePickerSelectedYear = datePicker.getYear();
                    final int datePickerSelectedMonth = datePicker.getMonth();
                    final int datePickerSelectedDay = datePicker.getDayOfMonth();
                    final int timePickerSelectedHour = timePicker.getHour();
                    final int timePickerSelectedMinute = timePicker.getMinute();
                    final int timePickerSelectedSecond =
                            (datePickerSelectedYear != selectedTransaction.getTime().getYear() &&
                                    datePickerSelectedMonth != selectedTransaction.getTime().getMonth() &&
                                    datePickerSelectedDay != selectedTransaction.getTime().getDay() &&
                                    timePickerSelectedHour != selectedTransaction.getTime().getHour() &&
                                    timePickerSelectedMinute != selectedTransaction.getTime().getMinute()) ?
                                    LocalDateTime.now().getSecond() : selectedTransaction.getTime().getSecond();
                    final MyCustomTime editedTime = new MyCustomTime(datePickerSelectedYear, datePickerSelectedMonth,
                            datePickerSelectedDay, timePickerSelectedHour, timePickerSelectedMinute,
                            timePickerSelectedSecond);
                    final int editedCategoryType = (editedCategoryIndex >= 0 && editedCategoryIndex <= 3) ? 1 : 0;
                    final Transaction editedTransaction = new Transaction(selectedTransaction.getId(),
                            editedCategoryIndex, editedTime, editedCategoryType, editedNote, editedValue);

                    Log.d("editedInitialTransaction", transactionHasBeenModified(selectedTransaction).toString());

//                    final Transaction editedTransaction = new Transaction("0ccf5d61-7a54-4132-a847-d9c4b762d2a1",
//                            11, new MyCustomTime(2021, 7, "JULY", 7,
//                            "WEDNESDAY", 17, 21, 9), 1, null, "33333");

                    //Toast.makeText(requireContext(), String.valueOf(transactionHasBeenModified(selectedTransaction)), Toast.LENGTH_SHORT).show();

                    Log.d("selectedTransaction", selectedTransaction.toString());

                    Log.d("editedTransaction", editedTransaction.toString());

                    //Toast.makeText(requireContext(), String.valueOf(selectedTransaction.equals(editedTransaction)), Toast.LENGTH_SHORT).show();

                    if (!selectedTransaction.equals(editedTransaction)) {
                        // nu e null => e ok
//                        Toast.makeText(requireContext(), String.valueOf(viewModel.getEditTransactionsRecyclerViewAdapter() == null), Toast.LENGTH_SHORT).show();


                        // nu merge
//                        selectedTransaction = editedTransaction;
//                        viewModel.getEditTransactionsRecyclerViewAdapter().notifyDataSetChanged();
//
//                        viewModel.setSelectedTransaction(editedTransaction);

                        final EditTransactionsViewModel editTransactionsViewModel =
                                ((EditTransactionsActivity) requireActivity()).getViewModel();

//                        editTransactionsViewModel.getSelectedTransaction().setValue("1500");

                        if (viewModel.getEditTransactionsRecyclerViewAdapter() != null) {
                            viewModel.getEditTransactionsRecyclerViewAdapter()
                                    .notifyItemChanged(viewModel.getSelectedTransactionListPosition());
                        }
                    }
                }
            }

            requireActivity().onBackPressed();
        });
    }

    private void setTitle() {
        if ((viewModel.getActivityTitle() == null) || (!viewModel.getActivityTitle()
                .equals(getResources().getString(R.string.edit_specific_transaction_title).trim()))) {
            viewModel.setActivityTitle(getResources().getString(R.string.edit_specific_transaction_title).trim());
        }

        titleText.setText(viewModel.getActivityTitle());
        titleText.setTextColor(Color.WHITE);
        titleText.setTextSize(18);
    }

    private void setHints() {
        if (viewModel.getSelectedTransaction() != null) {
            final Transaction selectedTransaction = viewModel.getSelectedTransaction();
            final String translatedType = Types.getTranslatedType(requireContext(),
                    String.valueOf(Transaction.getTypeFromIndexInEnglish(selectedTransaction.getCategory())));
            final ArrayList<String> transactionTypesList = new ArrayList<>();
            int positionInTheTransactionTypesList = -1;

            noteField.setHint(selectedTransaction.getNote() != null ?
                    selectedTransaction.getNote() : getResources().getString(R.string.note));
            valueField.setHint(selectedTransaction.getValue() != null ?
                    selectedTransaction.getValue() : getResources().getString(R.string.value));

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

    private void populateTransactionTypesList(@NonNull final ArrayList<String> list) {
        list.add(getResources().getString(R.string.add_money_deposits).trim());
        list.add(getResources().getString(R.string.add_money_independent_sources).trim());
        list.add(getResources().getString(R.string.salary).trim());
        list.add(getResources().getString(R.string.saving).trim());
        list.add(getResources().getString(R.string.subtract_money_bills).trim());
        list.add(getResources().getString(R.string.subtract_money_car).trim());
        list.add(getResources().getString(R.string.subtract_money_clothes).trim());
        list.add(getResources().getString(R.string.subtract_money_communications).trim());
        list.add(getResources().getString(R.string.subtract_money_eating_out).trim());
        list.add(getResources().getString(R.string.subtract_money_entertainment).trim());
        list.add(getResources().getString(R.string.subtract_money_food).trim());
        list.add(getResources().getString(R.string.subtract_money_gifts).trim());
        list.add(getResources().getString(R.string.subtract_money_health).trim());
        list.add(getResources().getString(R.string.subtract_money_house).trim());
        list.add(getResources().getString(R.string.subtract_money_pets).trim());
        list.add(getResources().getString(R.string.subtract_money_sports).trim());
        list.add(getResources().getString(R.string.subtract_money_taxi).trim());
        list.add(getResources().getString(R.string.subtract_money_toiletry).trim());
        list.add(getResources().getString(R.string.subtract_money_transport).trim());

        Collections.sort(list);
    }

    private void createTransactionTypesSpinner() {
        final ArrayList<String> types = new ArrayList<>();

        final ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(requireContext(),
                R.layout.custom_spinner_item, types) {
            @Override
            public View getDropDownView(final int position, final @Nullable View convertView,
                                        @NonNull final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setGravity(Gravity.CENTER);

                if (MyCustomVariables.getUserDetails() != null) {
                    final boolean darkTheme = MyCustomVariables.getUserDetails().getApplicationSettings().getDarkTheme();
                    final int itemsColor = !darkTheme ? Color.WHITE : Color.BLACK;

                    // setting items' text color based on the selected theme
                    ((TextView) v).setTextColor(itemsColor);
                }

                return v;
            }
        };

        final AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                if (MyCustomVariables.getUserDetails() != null) {
                    final boolean darkTheme = MyCustomVariables.getUserDetails().getApplicationSettings().getDarkTheme();
                    final int textColor = !darkTheme ? Color.parseColor("#195190") : Color.WHITE;

                    ((TextView) parent.getChildAt(0)).setTextColor(textColor);
                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.START);
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

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

        typeSpinner.setAdapter(typesAdapter);
        typeSpinner.setOnItemSelectedListener(listener);
    }

    private void setTheme() {
        if (MyCustomVariables.getUserDetails() != null) {
            final boolean darkTheme = MyCustomVariables.getUserDetails().getApplicationSettings().getDarkTheme();
            final int color = !darkTheme ? Color.parseColor("#195190") : Color.WHITE;
            final int backgroundTheme = !darkTheme ?
                    R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift;
            final int spinnerElementBackground = !darkTheme ?
                    R.drawable.ic_blue_gradient_unloved_teen : R.drawable.ic_white_gradient_tobacco_ad;

            setTextStyleEditText(noteField, color);
            setTextStyleEditText(valueField, color);

            requireActivity().getWindow().setBackgroundDrawableResource(backgroundTheme);

            // setting arrow's color
            typeSpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

            // setting items' color
            typeSpinner.setPopupBackgroundResource(spinnerElementBackground);
        }
    }

    private void setTextStyleEditText(@NonNull final EditText editText, final int color) {
        editText.setTextColor(color);
        editText.setHintTextColor(color);
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void setOnFocusChangeListener() {
        final View.OnFocusChangeListener listener = (final View v, final boolean hasFocus) -> {
            if (hasFocus) {
                ((EditText) v).setText(((EditText) v).getHint());
            }
        };

        noteField.setOnFocusChangeListener(listener);
    }

    private Transaction transactionHasBeenModified(@NonNull final Transaction initialTransaction) {
        final int datePickerSelectedYear = datePicker.getYear();
        final int datePickerSelectedMonth = datePicker.getMonth() + 1;
        final int datePickerSelectedDay = datePicker.getDayOfMonth();
        final int timePickerSelectedHour = timePicker.getHour();
        final int timePickerSelectedMinute = timePicker.getMinute();
        final int timePickerSelectedSecond =
                (datePickerSelectedYear != initialTransaction.getTime().getYear() &&
                        datePickerSelectedMonth != initialTransaction.getTime().getMonth() &&
                        datePickerSelectedDay != initialTransaction.getTime().getDay() &&
                        timePickerSelectedHour != initialTransaction.getTime().getHour() &&
                        timePickerSelectedMinute != initialTransaction.getTime().getMinute()) ?
                        LocalDateTime.now().getSecond() : initialTransaction.getTime().getSecond();
        final MyCustomTime editedTime = new MyCustomTime(datePickerSelectedYear, datePickerSelectedMonth,
                datePickerSelectedDay, timePickerSelectedHour, timePickerSelectedMinute,
                timePickerSelectedSecond);

        if ((initialTransaction.getNote() != null &&
                !String.valueOf(noteField.getText()).trim().equals(initialTransaction.getNote().trim())) ||
                (initialTransaction.getNote() == null && !String.valueOf(noteField.getText()).trim().isEmpty())) {
            Log.d("initialNote", initialTransaction.getNote() != null ? initialTransaction.getNote() : "null");
            Log.d("editedNote", String.valueOf(noteField.getText()).trim());

            final String editedNote = String.valueOf(noteField.getText()).trim();

            initialTransaction.setNote(editedNote);
        }

        if (!String.valueOf(valueField.getText()).trim().equals(String.valueOf(valueField.getHint()).trim()) &&
                !String.valueOf(valueField.getText()).trim().isEmpty()) {
            Log.d("initialValue", initialTransaction.getValue().trim());
            Log.d("editedValue", String.valueOf(valueField.getText()).trim());

            final String editedValue = String.valueOf(valueField.getText()).trim();

            initialTransaction.setValue(editedValue);
        }


        if (!editedTime.equals(initialTransaction.getTime())) {
            Log.d("initialTime", initialTransaction.getTime().toString());
            Log.d("editedTime", editedTime.toString());

            initialTransaction.setTime(editedTime);
        }

        final String initialCategoryName = Types.getTranslatedType(requireContext(),
                Transaction.getTypeFromIndexInEnglish(initialTransaction.getCategory()));
        final String selectedCategoryName = String.valueOf(typeSpinner.getSelectedItem()).trim();

        if (initialCategoryName != null && !initialCategoryName.equals(selectedCategoryName)) {
            Log.d("initialCategory", initialCategoryName);
            Log.d("editedCategory", selectedCategoryName);

            final int editedCategory =
                    Transaction.getIndexFromCategory(Types.getTypeInEnglish(requireContext(), selectedCategoryName));
            final int editedType = editedCategory >= 0 && editedCategory <= 3 ? 1 : 0;

            initialTransaction.setCategory(editedCategory);

            if (editedType != initialTransaction.getType()) {
                initialTransaction.setType(editedType);
            }
        }

        return initialTransaction;
    }

    private void setDatePicker(final @Nullable Transaction selectedTransaction) {
        datePicker.updateDate(selectedTransaction != null && selectedTransaction.getTime() != null ?
                        viewModel.getSelectedTransaction().getTime().getYear() : LocalDate.now().getYear(),
                selectedTransaction != null && selectedTransaction.getTime() != null ?
                        viewModel.getSelectedTransaction().getTime().getMonth() - 1 : LocalDate.now().getMonthValue() - 1,
                selectedTransaction != null && selectedTransaction.getTime() != null ?
                        viewModel.getSelectedTransaction().getTime().getDay() : LocalDate.now().getDayOfMonth());
    }

    private void setTimePicker(final @Nullable Transaction selectedTransaction) {
        timePicker.setHour(selectedTransaction != null && selectedTransaction.getTime() != null ?
                selectedTransaction.getTime().getHour() : LocalDateTime.now().getHour());
        timePicker.setMinute(selectedTransaction != null && selectedTransaction.getTime() != null ?
                selectedTransaction.getTime().getMinute() : LocalDateTime.now().getMinute());
    }
}