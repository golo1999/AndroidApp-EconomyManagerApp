package com.example.EconomyManager;

import androidx.annotation.NonNull;

import java.util.Objects;

public class UserDetails {
    private ApplicationSettings applicationSettings;
    private PersonalInformation personalInformation;

    public UserDetails() {

    }

    public UserDetails(ApplicationSettings applicationSettings,
                       PersonalInformation personalInformation) {
        this.applicationSettings = applicationSettings;
        this.personalInformation = personalInformation;
    }

    public ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }

    public void setApplicationSettings(ApplicationSettings applicationSettings) {
        this.applicationSettings = applicationSettings;
    }

    public PersonalInformation getPersonalInformation() {
        return personalInformation;
    }

    public void setPersonalInformation(PersonalInformation personalInformation) {
        this.personalInformation = personalInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetails details = (UserDetails) o;
        return applicationSettings.equals(details.applicationSettings) &&
                personalInformation.equals(details.personalInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationSettings, personalInformation);
    }

    @NonNull
    @Override
    public String toString() {
        return "UserDetails{" +
                "applicationSettings=" + applicationSettings +
                ", personalInformation=" + personalInformation +
                '}';
    }
}