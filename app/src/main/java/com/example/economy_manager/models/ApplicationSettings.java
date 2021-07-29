package com.example.economy_manager.models;

import androidx.annotation.NonNull;

import java.util.Objects;

public class ApplicationSettings {
    private String currency;
    private String currencySymbol;
    private boolean darkTheme;

    public ApplicationSettings() {
        // Required empty public constructor
    }

    public ApplicationSettings(String currency) {
        this.darkTheme = false;
        this.currency = currency;

        switch (this.currency) {
            case "EUR":
                this.currencySymbol = "€";
                break;
            case "GBP":
                this.currencySymbol = "£";
                break;
            default:
                this.currencySymbol = "RON";
                break;
        }
    }

    public boolean getDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean theme) {
        this.darkTheme = theme;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationSettings that = (ApplicationSettings) o;
        return darkTheme == that.darkTheme &&
                Objects.equals(currency, that.currency) &&
                currencySymbol.equals(that.currencySymbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, currencySymbol, darkTheme);
    }

    @NonNull
    @Override
    public String toString() {
        return "ApplicationSettings{" +
                "currency='" + currency + '\'' +
                ", currencySymbol='" + currencySymbol + '\'' +
                ", darkTheme=" + darkTheme +
                '}';
    }
}
