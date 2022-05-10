package com.example.economy_manager.utility;

import com.example.economy_manager.model.currencyconversionresult.CurrencyConversionResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceholderAPI {

    @GET("exchangerates_data/convert")
    Call<CurrencyConversionResult> getConversionRateBetween(@Query("from") String convertedFrom,
                                                            @Query("to") String convertedTo,
                                                            @Query("amount") double amount,
                                                            @Query("apikey") String apiKey);
}