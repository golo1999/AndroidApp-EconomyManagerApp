package com.example.economy_manager.model.currencyconversionresult;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CurrencyConversionResultQuery {

    @SerializedName("from")
    private final String convertedFrom;
    @SerializedName("to")
    private final String convertedTo;
    private final double amount;

    public CurrencyConversionResultQuery(final String convertedFrom,
                                         final String convertedTo,
                                         final double amount) {
        this.convertedFrom = convertedFrom;
        this.convertedTo = convertedTo;
        this.amount = amount;
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrencyConversionResultQuery{" +
                "convertedFrom='" + convertedFrom + '\'' +
                ", convertedTo='" + convertedTo + '\'' +
                ", amount=" + amount +
                '}';
    }
}