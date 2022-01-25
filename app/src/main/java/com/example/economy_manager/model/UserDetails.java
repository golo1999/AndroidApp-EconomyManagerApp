package com.example.economy_manager.model;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.Data;

@Data
public class UserDetails {
    private ApplicationSettings applicationSettings;
    private PersonalInformation personalInformation;
    private HashMap<String, Transaction> personalTransactions;

    public UserDetails() {
        // Required empty public constructor
    }

    public UserDetails(ApplicationSettings applicationSettings) {
        this.applicationSettings = applicationSettings;
    }

    public UserDetails(PersonalInformation personalInformation) {
        this.personalInformation = personalInformation;
    }

    public UserDetails(HashMap<String, Transaction> personalTransactions) {
        this.personalTransactions = personalTransactions;
    }

    public UserDetails(ApplicationSettings applicationSettings,
                       PersonalInformation personalInformation) {
        this.applicationSettings = applicationSettings;
        this.personalInformation = personalInformation;
    }

    public UserDetails(PersonalInformation personalInformation,
                       HashMap<String, Transaction> personalTransactions) {
        this.personalInformation = personalInformation;
        this.personalTransactions = personalTransactions;
    }

    public UserDetails(ApplicationSettings applicationSettings,
                       HashMap<String, Transaction> personalTransactions) {
        this.applicationSettings = applicationSettings;
        this.personalTransactions = personalTransactions;
    }

    public UserDetails(ApplicationSettings applicationSettings,
                       PersonalInformation personalInformation,
                       HashMap<String, Transaction> personalTransactions) {
        this.applicationSettings = applicationSettings;
        this.personalInformation = personalInformation;
        this.personalTransactions = personalTransactions;
    }
}