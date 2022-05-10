package com.example.economy_manager.model.currencyconversionresult;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CurrencyConversionResultInfo {

    private final long timestamp;
    @SerializedName("rate")
    private final double conversionRate;

    public CurrencyConversionResultInfo(final long timestamp,
                                        final double conversionRate) {
        this.timestamp = timestamp;
        this.conversionRate = conversionRate;
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrencyConversionResultInfo{" +
                "timestamp=" + timestamp +
                ", conversionRate=" + conversionRate +
                '}';
    }
}