package com.example.economy_manager.model.currencyconversionresult;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CurrencyConversionResult {

    private final boolean success;
    @SerializedName("query")
    private final CurrencyConversionResultQuery conversionQuery;
    @SerializedName("info")
    private final CurrencyConversionResultInfo conversionInfo;
    @SerializedName("date")
    private final String conversionDate;
    @SerializedName("result")
    private final String conversionResult;

    public CurrencyConversionResult(final boolean success,
                                    final CurrencyConversionResultQuery conversionQuery,
                                    final CurrencyConversionResultInfo conversionInfo,
                                    final String conversionDate,
                                    final String conversionResult) {
        this.success = success;
        this.conversionQuery = conversionQuery;
        this.conversionInfo = conversionInfo;
        this.conversionDate = conversionDate;
        this.conversionResult = conversionResult;
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrencyConversionResult{" +
                "success=" + success +
                ", conversionQuery=" + conversionQuery +
                ", conversionInfo=" + conversionInfo +
                ", conversionDate='" + conversionDate + '\'' +
                ", conversionResult='" + conversionResult + '\'' +
                '}';
    }
}