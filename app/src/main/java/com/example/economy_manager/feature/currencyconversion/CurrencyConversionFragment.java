package com.example.economy_manager.feature.currencyconversion;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.CurrencyConversionFragmentBinding;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.model.currencyconversionresult.CurrencyConversionResult;
import com.example.economy_manager.utility.JsonPlaceholderAPI;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyConversionFragment extends Fragment {

    private CurrencyConversionFragmentBinding binding;
    private JsonPlaceholderAPI api;

    private static final String TYPE = "TYPE";
    private String SELECTED_TYPE;

    public CurrencyConversionFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static CurrencyConversionFragment newInstance(String type) {
        CurrencyConversionFragment fragment = new CurrencyConversionFragment();
        Bundle args = new Bundle();

        args.putString(TYPE, type);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            SELECTED_TYPE = getArguments().getString(TYPE);
        }
    }

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        setFragmentVariables(inflater, container);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
//        createAndSetList();
    }

    /**
     * Method for iterating through the transactions, calculating the sum for the selected type
     * (e.g. the sum of monthly incomes or expenses) and
     * calling the method for dynamically adding the items into the main layout
     */
    private void createAndSetList() {
        if (MyCustomVariables.getFirebaseAuth().getUid() == null) {
            showNoTransactionsLayout(binding.mainLayout,
                    requireContext().getResources().getString(R.string.no_transactions_made_yet));

            return;
        }

        MyCustomVariables.getDatabaseReference().child(MyCustomVariables.getFirebaseAuth().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            return;
                        }

                        // saving current user's database node details
                        final UserDetails details = snapshot.getValue(UserDetails.class);

                        if (details == null || details.getPersonalTransactions() == null) {
                            binding.noRecordsText.setText(requireContext().getResources()
                                    .getString(R.string.no_transactions_made_yet));
                            return;
                        }

                        final String currencyName = details.getApplicationSettings().getCurrency();
                        final LocalDate currentDate = LocalDate.now();

                        float totalIncomes = 0f;
                        float totalExpenses = 0f;

                        // iterating through the transactions list and
                        // calculating the sum of current month's expenses and incomes
                        if (!details.getPersonalTransactions().isEmpty()) {
                            for (Map.Entry<String, Transaction> transactionEntry :
                                    details.getPersonalTransactions().entrySet()) {
                                final Transaction transaction = transactionEntry.getValue();

                                if (transaction != null &&
                                        transaction.getTime().getYear() == currentDate.getYear() &&
                                        transaction.getTime().getMonth() == currentDate.getMonthValue()) {
                                    if (transaction.getType() == 1) {
                                        totalIncomes += Float.parseFloat(transaction.getValue());
                                    } else {
                                        totalExpenses += Float.parseFloat(transaction.getValue());
                                    }
                                }
                            }
                        }

                        final float valueToConvert =
                                SELECTED_TYPE.equals("MONTHLY_EXPENSES") ? totalExpenses : totalIncomes;

                        showConvertedValue(binding.mainLayout, valueToConvert, currencyName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    /**
     * Method for setting fragment variables
     */
    private void setFragmentVariables(final @NonNull LayoutInflater inflater,
                                      final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.currency_conversion_fragment, container, false);
        api = new Retrofit.Builder().baseUrl(MyCustomVariables.getCurrencyConverterApiDomain())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(JsonPlaceholderAPI.class);
    }

    /**
     * Method for dynamically adding the child layouts into the main one
     * after converting the value to every other currency using an API
     */
    private void showConvertedValue(final @NonNull LinearLayout mainLayout,
                                    final float value,
                                    final String currentCurrencyName) {
        // removing all the existing views from the main layout
        mainLayout.removeAllViews();

        if (value == 0f) {
            showNoTransactionsLayout(mainLayout, requireContext().getResources()
                    .getString(R.string.no_transactions_made_this_month));

            return;
        }

        final String[] currenciesArray = requireContext().getResources().getStringArray(R.array.currencies);
        final List<String> currenciesList = Arrays.stream(currenciesArray).collect(Collectors.toList());

        // filtering the currencies array with all of its values but the current currency
        currenciesList.stream().filter(currency -> !currency.equals(currentCurrencyName)).forEach(currency -> {
            final LinearLayout childLayout = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.currency_conversion_item_layout, mainLayout, false);

            if (childLayout == null) {
                return;
            }

            // trying to convert the value from the current currency to the other ones
            try {
                final ApplicationInfo applicationInfo = getApplicationContext()
                        .getPackageManager()
                        .getApplicationInfo(getApplicationContext().getPackageName(),
                                PackageManager.GET_META_DATA);

                final String CURRENCY_CONVERTER_API_KEY =
                        String.valueOf(applicationInfo.metaData.get("CURRENCY_CONVERTER_API_KEY"));

                final Call<CurrencyConversionResult> currencyConversionResultCall =
                        api.getConversionRateBetween(currentCurrencyName, currency, value, CURRENCY_CONVERTER_API_KEY);

                currencyConversionResultCall.enqueue(new Callback<CurrencyConversionResult>() {
                    @Override
                    public void onResponse(@NonNull Call<CurrencyConversionResult> call,
                                           @NonNull Response<CurrencyConversionResult> response) {
                        // getting the conversion result and displaying it on the child layout which will be appended
                        // to the main layout's vertical LinearLayout
                        CurrencyConversionResult currencyConversionResult = response.body();

                        if (currencyConversionResult == null) {
                            return;
                        }

                        final TextView currencyNameTextView = childLayout.findViewById(R.id.currencyName);
                        final TextView convertedValueTextView = childLayout.findViewById(R.id.convertedValue);
                        final String currencySymbol = MyCustomMethods.getCurrencySymbolFromCurrencyName(currency);
                        final float convertedValue = Float.parseFloat(currencyConversionResult.getConversionResult());
                        final String convertedValueWithCurrency =
                                Locale.getDefault().getDisplayLanguage().equals("English") ?
                                        currencySymbol + convertedValue : convertedValue + " " + currencySymbol;

                        currencyNameTextView.setText(currency);
                        convertedValueTextView.setText(convertedValueWithCurrency);

                        mainLayout.addView(childLayout);
                    }

                    @Override
                    public void onFailure(@NonNull Call<CurrencyConversionResult> call,
                                          @NonNull Throwable t) {

                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("currencyConverterApiKey", "COULDN'T BE FETCHED FROM BUILD CONFIG");
                e.printStackTrace();
            }
        });
    }

    /**
     * Method for displaying the message if there are no transactions in the database or in the current month
     */
    private void showNoTransactionsLayout(final @NonNull LinearLayout mainLayout,
                                          final String message) {
        final TextView noTransactions = new TextView(getContext());
        final LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 20, 0, 20);

        noTransactions.setText(message);
        noTransactions.setTextColor(requireContext().getColor(R.color.quaternaryLight));
        noTransactions.setTypeface(null, Typeface.BOLD);
        noTransactions.setTextSize(20);
        noTransactions.setGravity(Gravity.CENTER);
        noTransactions.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        noTransactions.setLayoutParams(params);

        mainLayout.addView(noTransactions);
    }
}