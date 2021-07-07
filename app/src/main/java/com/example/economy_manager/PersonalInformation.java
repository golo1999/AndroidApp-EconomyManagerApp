package com.example.economy_manager;

import androidx.annotation.NonNull;

import java.util.Objects;

public class PersonalInformation {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String website;
    private String country;
    private String gender;
    private BirthDate birthDate;
    private String careerTitle;
    private String photoURL;

    public PersonalInformation() {

    }

    public PersonalInformation(String firstName, String lastName, String phoneNumber,
                               String website, String country, String gender, BirthDate birthDate,
                               String careerTitle, String photoURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.country = country;
        this.gender = gender;
        this.birthDate = birthDate;
        this.careerTitle = careerTitle;
        this.photoURL = photoURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public BirthDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(BirthDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getCareerTitle() {
        return careerTitle;
    }

    public void setCareerTitle(String careerTitle) {
        this.careerTitle = careerTitle;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalInformation that = (PersonalInformation) o;
        return firstName.equals(that.firstName) &&
                lastName.equals(that.lastName) &&
                phoneNumber.equals(that.phoneNumber) &&
                website.equals(that.website) &&
                country.equals(that.country) &&
                gender.equals(that.gender) &&
                birthDate.equals(that.birthDate) &&
                careerTitle.equals(that.careerTitle) &&
                photoURL.equals(that.photoURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, phoneNumber, website, country, gender, birthDate, careerTitle, photoURL);
    }

    @NonNull
    @Override
    public String toString() {
        return "PersonalInformation{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", website='" + website + '\'' +
                ", country='" + country + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", careerTitle='" + careerTitle + '\'' +
                ", photoURL='" + photoURL + '\'' +
                '}';
    }
}
