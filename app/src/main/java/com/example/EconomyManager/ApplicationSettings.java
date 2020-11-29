package com.example.EconomyManager;

public class ApplicationSettings
{
    private String currency, currencySymbol;
    private Boolean darkTheme;

    public ApplicationSettings(Boolean darkTheme, String currency)
    {
        this.darkTheme = darkTheme;
        this.currency = currency;

        switch(this.currency)
        {
            case "EUR":
                this.currencySymbol="€";
                break;
            case "GBP":
                this.currencySymbol="£";
                break;
            default:
                this.currencySymbol="RON";
                break;
        }
    }

    public Boolean getDarkTheme()
    {
        return darkTheme;
    }

    public void setDarkTheme(Boolean theme)
    {
        this.darkTheme = theme;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getCurrencySymbol()
    {
        return currencySymbol;
    }
}
