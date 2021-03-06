package com.example.economy_manager.main_part.view.activity;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.viewmodel.EditSpecificTransactionViewModel;
import com.example.economy_manager.model.MyCustomTime;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.Months;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.utility.Types;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class EditSpecificTransactionActivity extends AppCompatActivity {
    private EditSpecificTransactionViewModel viewModel;
    private SharedPreferences preferences;
    private TextView titleText;
    private TextView noteText;
    private TextView valueText;
    private TextView dateText;
    private TextView typeText;
    private EditText noteField;
    private EditText valueField;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Spinner typeSpinner;
    private ImageView goBack;
    private Button saveChangesButton;
    private Bundle extras;
    private static final int REQUEST_CODE = 1;
    private final char[] transactionModifiedQueue = new char[4];

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_specific_transaction_activity);
        setVariables();
        setUserDetails();
        setTheme();
        setTransactionDetails();
        setDatePicker(viewModel.getSelectedTransaction());
        setTimePicker(viewModel.getSelectedTransaction());
        setOnClickListeners();
        setOnFocusChangeListener();
        setTitle();
        createTransactionTypesSpinner();
        setHints();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void setVariables() {
        viewModel = new ViewModelProvider(this).get(EditSpecificTransactionViewModel.class);
        preferences = getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
        extras = getIntent().getExtras();
        goBack = findViewById(R.id.editSpecificTransactionBack);
        titleText = findViewById(R.id.editSpecificTransactionTitle);
        noteField = findViewById(R.id.editSpecificTransactionNoteEdit);
        valueField = findViewById(R.id.editSpecificTransactionValueEdit);
        datePicker = findViewById(R.id.editSpecificTransactionDateEdit);
        timePicker = findViewById(R.id.editSpecificTransactionTimeEdit);
        typeSpinner = findViewById(R.id.editSpecificTransactionTypeSpinner);
        saveChangesButton = findViewById(R.id.editSpecificTransactionSave);
        dateText = findViewById(R.id.editSpecificTransactionDateText);
        valueText = findViewById(R.id.editSpecificTransactionValueText);
        noteText = findViewById(R.id.editSpecificTransactionNoteText);
        typeText = findViewById(R.id.editSpecificTransactionTypeText);

    }

    private void setUserDetails() {
        final UserDetails userDetails = MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this);

        if (userDetails != null) {
            viewModel.setUserDetails(userDetails);
        }
    }

    private void setTransactionDetails() {
        final Transaction selectedTransaction =
                MyCustomSharedPreferences.retrieveTransactionFromSharedPreferences(this);

        if (selectedTransaction != null) {
            viewModel.setSelectedTransaction(selectedTransaction);
        }
    }

    private void setOnClickListeners() {
        goBack.setOnClickListener(v -> onBackPressed());

        saveChangesButton.setOnClickListener(v -> {
            MyCustomMethods.closeTheKeyboard(EditSpecificTransactionActivity.this);

            if (viewModel.getUserDetails() != null) {
                final Transaction selectedTransaction = viewModel.getSelectedTransaction();

                if (selectedTransaction != null) {
                    final String editedNote = !String.valueOf(noteField.getText()).trim().isEmpty() ?
                            String.valueOf(noteField.getText()).trim() : selectedTransaction.getNote();
                    final String editedValue = !String.valueOf(valueField.getText()).trim().isEmpty() ?
                            String.valueOf(valueField.getText()).trim() : selectedTransaction.getValue();
                    final int parsedCategoryIndex = Transaction.getIndexFromCategory(Types.
                            getTypeInEnglish(this, String.valueOf(typeSpinner.getSelectedItem()).trim()));
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

//                    final Transaction editedTransaction = new Transaction("0ccf5d61-7a54-4132-a847-d9c4b762d2a1",
//                            11, new MyCustomTime(2021, 7, "JULY", 7,
//                            "WEDNESDAY", 17, 21, 9), 1, null, "33333");

                    if (!selectedTransaction.equals(editedTransaction)) {

                    }

                    Log.d("selectedTransaction", selectedTransaction.toString());

                    Log.d("editedTransaction", editedTransaction.toString());

                    Toast.makeText(EditSpecificTransactionActivity.this, String.valueOf(selectedTransaction.equals(editedTransaction)), Toast.LENGTH_SHORT).show();
                }
            }

            onBackPressed();

//            if (extras != null) {
//                final String noteHint = String.valueOf(extras.getString("note"));
//                final String valueHint = String.valueOf(extras.getString("value"));
//                final String dateHint = String.valueOf(extras.getString("time"));
//                final String typeHint = String.valueOf(extras.getString("type"));
//                float valueChanged = -1;
//                final MoneyManager money = new MoneyManager(noteHint, Float.parseFloat(valueHint), dateHint, typeHint);
//                boolean noteModified = false, valueModified = false, dateModified = false, typeModified = false;
//
//                if (!String.valueOf(noteField.getText()).trim().equals(noteHint) && !String.valueOf(noteField.getText()).trim().equals(""))
//                    noteModified = true;
//                if (!String.valueOf(valueField.getText()).trim().equals("") &&
//                        !String.valueOf(Float.parseFloat(String.valueOf(valueField.getText()))).trim().equals(valueHint)) {
//                    valueChanged = Float.parseFloat(String.valueOf(valueField.getText()).trim());
//                    valueModified = true;
//                }
//                if (!Locale.getDefault().getDisplayLanguage().equals("English")) {
//                    if (!String.valueOf(dateField.getHint()).equals(getTranslatedDate(dateHint)))
//                        dateModified = true;
//                    if (!String.valueOf(typeSpinner.getSelectedItem()).equals(extras.getString("type")))
//                        typeModified = true;
//                } else {
//                    if (!String.valueOf(dateField.getHint()).equals(dateHint))
//                        dateModified = true;
//                    if (!String.valueOf(Types.getTypeInEnglish(ActivityEditSpecificTransaction.this,
//                            String.valueOf(typeSpinner.getSelectedItem()))).equals(extras.getString("type")))
//                        typeModified = true;
//                }
//
//                if (noteModified || valueModified || dateModified || typeModified) {
//                    setQueue(transactionModifiedQueue, noteModified, valueModified, dateModified, typeModified);
//
//                    if (MyCustomVariables.getFirebaseAuth().getUid() != null) {
//                        if (noteModified)
//                            money.setNote(String.valueOf(noteField.getText()).trim());
//                        if (valueModified)
//                            money.setValue(valueChanged);
//                        if (dateModified)
//                            money.setDate(String.valueOf(dateField.getHint()));
//                        if (typeModified)
//                            if (!Locale.getDefault().getDisplayLanguage().equals("English"))
//                                money.setType(Types.getTypeInEnglish(ActivityEditSpecificTransaction.this, String.valueOf(typeSpinner.getSelectedItem())));
//                            else money.setType(String.valueOf(typeSpinner.getSelectedItem()));
//                    }
//                }
//                onBackPressed();
//            }
        });

        //type.setOnItemSelectedListener(listener);
    }

//    @Override
//    protected void onActivityResult(final int requestCode, final int resultCode, final @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                final Bundle extras = data.getExtras();
//
//                if (extras != null) {
//                    final String modifiedDate = extras.getString("modifiedDate");
//
//                    if (modifiedDate != null &&
//                            !getTranslatedDate(modifiedDate).equals(String.valueOf(datePicker.getHint()))) {
//                        datePicker.setHint(!Locale.getDefault().getDisplayLanguage().equals("English") ?
//                                getTranslatedDate(modifiedDate) : modifiedDate);
//                    }
//                }
//            }
//        }
//    }

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
            final String translatedType = Types.getTranslatedType(this,
                    String.valueOf(Transaction.getTypeFromIndexInEnglish(selectedTransaction.getCategory())));
            final ArrayList<String> transactionTypesList = new ArrayList<>();
            int positionInTheTransactionTypesList = -1;

            noteField.setHint(selectedTransaction.getNote() != null ? selectedTransaction.getNote() : "");
            valueField.setHint(selectedTransaction.getValue());

//            if (selectedTransaction.getTime() != null) {
//                datePicker.setHint(selectedTransaction.getTime().toString());
//            }

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

    private void populateTransactionTypesList(final ArrayList<String> list) {
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

    /*private float getDifferenceBetweenValues(float oldValue, float newValue) // metoda este perfecta
    {
        return newValue-oldValue;
    }*/

    private void createTransactionTypesSpinner() {
        final ArrayList<String> types = new ArrayList<>();

        final ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item,
                types) {
            @Override
            public View getDropDownView(final int position, final @Nullable View convertView,
                                        @NonNull final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);

                if (viewModel.getUserDetails() != null) {
                    final boolean darkTheme = viewModel.getUserDetails().getApplicationSettings().getDarkTheme();
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
                if (viewModel.getUserDetails() != null) {
                    final boolean darkTheme = viewModel.getUserDetails().getApplicationSettings().getDarkTheme();
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
        if (viewModel.getUserDetails() != null) {
            final boolean darkTheme = viewModel.getUserDetails().getApplicationSettings().getDarkTheme();
            final int color = !darkTheme ? Color.parseColor("#195190") : Color.WHITE;
            final int backgroundTheme = !darkTheme ?
                    R.drawable.ic_white_gradient_tobacco_ad : R.drawable.ic_black_gradient_night_shift;
            final int spinnerElementBackground = !darkTheme ?
                    R.drawable.ic_blue_gradient_unloved_teen : R.drawable.ic_white_gradient_tobacco_ad;

            setTextStyleTextView(noteText, color);
            setTextStyleTextView(valueText, color);
            setTextStyleTextView(dateText, color);
            setTextStyleTextView(typeText, color);

            setTextStyleEditText(noteField, color);
            setTextStyleEditText(valueField, color);
//            setTextStyleEditText(datePicker, color);

            getWindow().setBackgroundDrawableResource(backgroundTheme);

            // setting arrow's color
            typeSpinner.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

            // setting items' color
            typeSpinner.setPopupBackgroundResource(spinnerElementBackground);
        }
    }

    private void setTextStyleTextView(final TextView textView, final int color) {
        textView.setTextColor(color);
        textView.setTextSize(18);
    }

    private void setTextStyleEditText(final EditText editText, final int color) {
        editText.setTextColor(color);
        editText.setHintTextColor(color);
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private String getTranslatedDate(final String dateInEnglish) {
        String deviceLanguage = Locale.getDefault().getDisplayLanguage(), prefix, translatedDate;
        String[] dateInEnglishSplit = dateInEnglish.split("\\W");
        String month = Months.getTranslatedMonth(this, dateInEnglishSplit[0].trim());
        String day = dateInEnglishSplit[1].trim();
        String year = dateInEnglishSplit[3].trim();
        String hour = dateInEnglishSplit[4].trim();
        String minute = dateInEnglishSplit[5].trim();
        String second = dateInEnglishSplit[6].trim();

        switch (deviceLanguage) {
            case "Deutsch":
                prefix = "der";
                translatedDate = prefix + " " + day + ". " + month + " " + year + ", " + hour + ":" + minute
                        + ":" + second;
                break;
            case "español":
                prefix = "el";
                String prefix2 = "de";
                translatedDate = prefix + " " + day + " " + prefix2 + " " + month.toLowerCase() + " " + prefix2
                        + " " + year + ", " + hour + ":" + minute + ":" + second;
                break;
            case "italiano":
                prefix = "il";
                translatedDate = prefix + " " + day + " " + month.toLowerCase() + " " + year + ", " + hour
                        + ":" + minute + ":" + second;
                break;
            case "português":
                prefix = "de";
                translatedDate = day + " " + prefix + " " + month.toLowerCase() + " " + prefix + " " + year
                        + ", " + hour + ":" + minute + ":" + second;
                break;
            case "français":
            case "română":
                translatedDate = day + " " + month.toLowerCase() + " " + year + ", " + hour + ":" + minute
                        + ":" + second;
                break;
            default:
                translatedDate = "";
                break;
        }

        return translatedDate;
    }

    private void setQueue(final char[] queue, final boolean noteModified, final boolean valueModified,
                          final boolean dateModified, final boolean typeModified) {
        Arrays.fill(queue, '0');

        if (typeModified) {
            int position = -1;

            for (final char index : queue) {
                ++position;

                if (index == '0') {
                    queue[position] = 'T';
                    break;
                }
            }
        }

        if (dateModified) {
            int position = -1;

            for (final char index : queue) {
                ++position;

                if (index == '0') {
                    queue[position] = 'D';
                    break;
                }
            }
        }

        if (valueModified) {
            int position = -1;

            for (final char index : queue) {
                ++position;

                if (index == '0') {
                    queue[position] = 'V';
                    break;
                }
            }
        }

        if (noteModified) {
            int position = -1;

            for (final char index : queue) {
                ++position;

                if (index == '0') {
                    queue[position] = 'N';
                    break;
                }
            }
        }
    }

    private void setOnFocusChangeListener() {
        final View.OnFocusChangeListener listener = (final View v, final boolean hasFocus) -> {
            if (hasFocus) {
                ((EditText) v).setText(((EditText) v).getHint());
            }
        };

        noteField.setOnFocusChangeListener(listener);
    }

    private MyCustomTime getTimeFromString(final String timeString) {
        final String[] timeStringParsed = timeString.split("-");
        final int year = Integer.parseInt(timeStringParsed[0]);
        final int month = Integer.parseInt(timeStringParsed[1]);
        final int day = Integer.parseInt(timeStringParsed[2]);
        final int hour = Integer.parseInt(timeStringParsed[3]);
        final int minute = Integer.parseInt(timeStringParsed[4]);
        final int second = Integer.parseInt(timeStringParsed[5]);

        return new MyCustomTime(year, month, day, hour, minute, second);
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