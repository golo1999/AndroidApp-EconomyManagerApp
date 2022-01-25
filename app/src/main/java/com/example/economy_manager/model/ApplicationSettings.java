package com.example.economy_manager.model;

import com.example.economy_manager.utility.MyCustomVariables;

import lombok.Data;

@Data
public class ApplicationSettings {
    private String currency;
    private String currencySymbol;
    private boolean darkThemeEnabled;

    public ApplicationSettings() {
        // Required empty public constructor
        this(MyCustomVariables.getDefaultCurrency());
    }

    public ApplicationSettings(final String currency) {
        this.darkThemeEnabled = false;
        this.currency = currency;

        setCurrencySymbol();
    }

    public ApplicationSettings(String currency,
                               String currencySymbol,
                               boolean darkThemeEnabled) {
        this.currency = currency;
        this.currencySymbol = currencySymbol;
        this.darkThemeEnabled = darkThemeEnabled;
    }

    public void setCurrency(String currency) {
        this.currency = currency;

        setCurrencySymbol();
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
}