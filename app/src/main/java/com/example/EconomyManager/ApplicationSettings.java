package com.example.EconomyManager;

public class ApplicationSettings {
    private String currency;
    private String currencySymbol;
    private boolean darkTheme;

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
}
