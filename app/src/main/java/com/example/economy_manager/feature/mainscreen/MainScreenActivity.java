package com.example.economy_manager.feature.mainscreen;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.MainScreenActivityBinding;
import com.example.economy_manager.feature.addexpense.AddExpenseActivity;
import com.example.economy_manager.feature.addincome.AddIncomeActivity;
import com.example.economy_manager.feature.editprofile.EditProfileActivity;
import com.example.economy_manager.feature.edittransactions.EditTransactionsActivity;
import com.example.economy_manager.feature.moneyspentpercentage.MoneySpentPercentageFragment;
import com.example.economy_manager.feature.monthlybalance.MonthlyBalanceActivity;
import com.example.economy_manager.feature.settings.SettingsActivity;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.model.currencyconversionresult.CurrencyConversionResult;
import com.example.economy_manager.utility.JsonPlaceholderAPI;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.MyCustomVariables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainScreenActivity
        extends AppCompatActivity
        implements MoneySpentPercentageFragment.MoneySpentPercentageListener {

    private MainScreenActivityBinding binding;
    private MainScreenViewModel viewModel;
    private ViewGroup.LayoutParams moneySpentPercentageLayoutParams;
    private long backPressedTime;
    private Toast backToast;
    private JsonPlaceholderAPI api;

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this,
                    getResources().getString(R.string.press_again_exit),
                    Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTimer();
        setActivityVariables();
        setLayoutVariables();
        setFragments();
//        getConversionRate("GBP", "RON", 10);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserDetails();
        setDates();
        setMoneySpentPercentage();
    }

    @Override
    public void onEmptyPieChart() {
        if (moneySpentPercentageLayoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            moneySpentPercentageLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        binding.expensesChartFragmentContainer.setLayoutParams(moneySpentPercentageLayoutParams);
    }

    @Override
    public void onNotEmptyPieChart() {
        final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        final int percentageScreenHeight = (int) (screenHeight * 0.4);

        if (moneySpentPercentageLayoutParams.height != percentageScreenHeight) {
            moneySpentPercentageLayoutParams.height = percentageScreenHeight;
        }

        binding.expensesChartFragmentContainer.setLayoutParams(moneySpentPercentageLayoutParams);
    }

    private void setDates() {
        if (!String.valueOf(binding.getGreetingText()).equals(viewModel.getGreetingMessage(this))) {
            binding.setGreetingText(viewModel.getGreetingMessage(this));
        }

        if (!String.valueOf(binding.getDateText()).equals(viewModel.getCurrentDateTranslated())) {
            binding.setDateText(viewModel.getCurrentDateTranslated());
        }
    }

    private void setFragments() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.showSavingsFragmentContainer, viewModel.getShowSavingsFragment())
                .replace(R.id.budgetReviewFragmentContainer, viewModel.getBudgetReviewFragment())
                .replace(R.id.moneySpentFragmentContainer, viewModel.getMoneySpentFragment())
                .replace(R.id.lastTenTransactionsFragmentContainer,
                        viewModel.getLastTenTransactionsFragment())
                .replace(R.id.topFiveExpensesFragmentContainer,
                        viewModel.getTopFiveExpensesFragment())
                .replace(R.id.favoriteExpensesCategoryFragmentContainer,
                        viewModel.getFavoriteExpensesCategoryFragment())
                .replace(R.id.expensesChartFragmentContainer,
                        viewModel.getMoneySpentPercentageFragment())
                .replace(R.id.overallProfitFragmentContainer, viewModel.getOverallProfitFragment())
                .commit();
    }

    private void setMoneySpentPercentage() {
        final String currentUserID = MyCustomVariables.getFirebaseAuth().getUid();

        if (currentUserID == null) {
            return;
        }

        MyCustomVariables.getDatabaseReference()
                .child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final @NonNull DataSnapshot snapshot) {
                        float totalMonthlyIncomes = 0f;
                        float totalMonthlyExpenses = 0f;
                        boolean currentMonthTransactionsExist = false;

                        if (!snapshot.exists()) {
                            return;
                        }

                        final UserDetails details = snapshot.getValue(UserDetails.class);

                        if (details == null || details.getPersonalTransactions() == null) {
                            binding.firebaseLoadingProgressBar.setVisibility(View.GONE);
                            binding.firebaseLoadingProgressBarLayout.setVisibility(View.GONE);
                            binding.setMoneySpentPercentageText(getResources().getString(R.string.no_money_records_yet));

                            return;
                        }

                        if (!details.getPersonalTransactions().isEmpty()) {
                            for (Map.Entry<String, Transaction> transactionEntry :
                                    details.getPersonalTransactions().entrySet()) {
                                final Transaction transaction = transactionEntry.getValue();

                                if (transaction != null &&
                                        transaction.getTime().getYear() ==
                                                LocalDate.now().getYear() &&
                                        transaction.getTime().getMonth() ==
                                                LocalDate.now().getMonthValue()) {
                                    if (!currentMonthTransactionsExist) {
                                        currentMonthTransactionsExist = true;
                                    }

                                    if (transaction.getCategory() > 0 &&
                                            transaction.getCategory() < 4) {
                                        totalMonthlyIncomes +=
                                                Float.parseFloat(transaction.getValue());
                                    } else {
                                        totalMonthlyExpenses +=
                                                Float.parseFloat(transaction.getValue());
                                    }
                                }
                            }

                            final int percentage =
                                    Float.valueOf(totalMonthlyExpenses / totalMonthlyIncomes * 100)
                                            .intValue();

                            final String percentageText = currentMonthTransactionsExist ?
                                    getResources().getString(R
                                                    .string.you_spent_percentage_of_your_incomes,
                                            Math.min(percentage, 100) + "%") :
                                    getResources().getString(R.string.no_money_records_this_month);

                            binding.setMoneySpentPercentageText(percentageText);
                        } else {
                            binding.setMoneySpentPercentageText(getResources()
                                    .getString(R.string.no_money_records_yet));
                        }

                        binding.firebaseLoadingProgressBar.setVisibility(View.GONE);
                        binding.firebaseLoadingProgressBarLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(final @NonNull DatabaseError error) {

                    }
                });
    }

    private void setTimer() {
        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    viewModel.setTimerCounter(viewModel.getTimerCounter() + 1);

                    // stopping the timer after one second
                    if (viewModel.getTimerCounter() == 1) {
                        timer.cancel();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void setUserDetails() {
        final String currentUserID = MyCustomVariables.getFirebaseAuth().getUid();

        if (currentUserID == null) {
            return;
        }

        if (MyCustomSharedPreferences.retrieveUserDetailsFromSharedPreferences(this)
                != null) {
            viewModel.setUserDetails(MyCustomSharedPreferences
                    .retrieveUserDetailsFromSharedPreferences(this));
        }

        if (viewModel.getUserDetails() != null) {
            binding.setIsDarkThemeEnabled(viewModel.getUserDetails().getApplicationSettings()
                    .isDarkThemeEnabled());
        }

        MyCustomVariables.getDatabaseReference()
                .child(currentUserID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final @NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            return;
                        }

                        final UserDetails details = snapshot.getValue(UserDetails.class);

                        if (details == null ||
                                details.getApplicationSettings() == null ||
                                details.getPersonalInformation() == null) {
                            return;
                        }

                        MyCustomMethods.
                                saveObjectToSharedPreferences(MainScreenActivity.this,
                                        details, "currentUserDetails");

                        if (MyCustomMethods.
                                retrieveObjectFromSharedPreferences(MainScreenActivity.this,
                                        "currentUserDetails", UserDetails.class) != null) {
                            final UserDetails userDetails = MyCustomMethods.
                                    retrieveObjectFromSharedPreferences(MainScreenActivity.this,
                                            "currentUserDetails", UserDetails.class);

                            MyCustomVariables.setUserDetails(userDetails);
                            viewModel.setUserDetails(userDetails);
                        }
                    }

                    @Override
                    public void onCancelled(final @NonNull DatabaseError error) {

                    }
                });
    }

    /**
     * Method for setting activity variables
     */
    private void setActivityVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.main_screen_activity);
        viewModel = new ViewModelProvider(this).get(MainScreenViewModel.class);
        moneySpentPercentageLayoutParams = binding.expensesChartFragmentContainer.getLayoutParams();

        api = new Retrofit.Builder().baseUrl(MyCustomVariables.getCurrencyConverterApiDomain())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(JsonPlaceholderAPI.class);
    }

    /**
     * Method for setting layout variables
     */
    private void setLayoutVariables() {
        binding.setActivity(this);
        binding.setAddExpenseActivity(AddExpenseActivity.class);
        binding.setAddIncomeActivity(AddIncomeActivity.class);
        binding.setEditProfileActivity(EditProfileActivity.class);
        binding.setEditTransactionsActivity(EditTransactionsActivity.class);
        binding.setMonthlyBalanceActivity(MonthlyBalanceActivity.class);
        binding.setSettingsActivity(SettingsActivity.class);
        binding.setViewModel(viewModel);
    }

    private void getConversionRate(final String from,
                                   final String to,
                                   final double amount) {
        try {
            final ApplicationInfo applicationInfo = getApplicationContext()
                    .getPackageManager()
                    .getApplicationInfo(getApplicationContext().getPackageName(),
                            PackageManager.GET_META_DATA);

            final String CURRENCY_CONVERTER_API_KEY =
                    String.valueOf(applicationInfo.metaData.get("CURRENCY_CONVERTER_API_KEY"));

            final Call<CurrencyConversionResult> currencyConversionResultCall =
                    api.getConversionRateBetween(from, to, amount, CURRENCY_CONVERTER_API_KEY);

            currencyConversionResultCall.enqueue(new Callback<CurrencyConversionResult>() {
                @Override
                public void onResponse(@NonNull Call<CurrencyConversionResult> call,
                                       @NonNull Response<CurrencyConversionResult> response) {
                    CurrencyConversionResult currencyConversionResult =
                            response.body();

                    if (currencyConversionResult == null) {
                        MyCustomMethods.showShortMessage(MainScreenActivity.this,
                                "conversion result null");
                        return;
                    }

                    Log.d("conversionResponse", currencyConversionResult.toString());
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
    }
}