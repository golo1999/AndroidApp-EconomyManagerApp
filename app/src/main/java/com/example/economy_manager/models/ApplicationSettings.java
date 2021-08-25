package com.example.economy_manager.models;

import androidx.annotation.NonNull;

import com.example.economy_manager.utilities.MyCustomVariables;

import java.util.Objects;

public class ApplicationSettings {
    private String currency;
    private String currencySymbol;
    private boolean darkTheme;

    public ApplicationSettings() {
        // Required empty public constructor
        this(MyCustomVariables.getDefaultCurrency());
    }

    public ApplicationSettings(final String currency) {
        this.darkTheme = false;
        this.currency = currency;

        setCurrencySymbol();
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
        setCurrencySymbol();
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol() {
        final String currency = getCurrency();

        this.currencySymbol = currency.equals("AUD") ?
                "A$" : currency.equals("BRL") ?
                "R$" : currency.equals("CAD") ?
                "C$" : currency.equals("CHF") ?
                "CHF" : currency.equals("CNY") ?
                "元" : currency.equals("EUR") ?
                "€" : currency.equals("GBP") ?
                "£" : currency.equals("INR") ?
                "₹" : currency.equals("JPY") ?
                "¥" : currency.equals("RON") ?
                "RON" : currency.equals("RUB") ?
                "₽" : "$";
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
