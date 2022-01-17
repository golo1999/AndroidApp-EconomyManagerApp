package com.example.economy_manager.model;

import lombok.Data;

@Data
public class UserDetails {
    private ApplicationSettings applicationSettings;
    private PersonalInformation personalInformation;

    public UserDetails() {
        // Required empty public constructor
    }

    public UserDetails(ApplicationSettings applicationSettings,
                       PersonalInformation personalInformation) {
        this.applicationSettings = applicationSettings;
        this.personalInformation = personalInformation;
    }
}