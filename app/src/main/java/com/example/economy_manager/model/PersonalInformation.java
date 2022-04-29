package com.example.economy_manager.model;

import lombok.Data;

@Data
public class PersonalInformation {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String website;
    private String country;
    private String gender;
    //    private BirthDate birthDate;
    private String careerTitle;
    private String photoURL;

    public PersonalInformation() {
        // Required empty public constructor
        this("", "", "", "", "", "",
//                new BirthDate(),
                "",
                "");
    }

    public PersonalInformation(final String firstName,
                               final String lastName,
                               final String phoneNumber,
                               final String website,
                               final String country,
                               final String gender,
//                               final BirthDate birthDate,
                               final String careerTitle,
                               final String photoURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.country = country;
        this.gender = gender;
//        this.birthDate = birthDate;
        this.careerTitle = careerTitle;
        this.photoURL = photoURL;
    }
}
