package com.example.EconomyManager;

public class PersonalInformation
{
    private String firstName, lastName, phoneNumber, website, country, gender, birthDate, careerTitle, photoURL;

    public PersonalInformation(String fName, String lName, String pNumber, String website, String country, String gender, String bDate, String cTitle, String pURL)
    {
        this.firstName=fName;
        this.lastName=lName;
        this.phoneNumber=pNumber;
        this.website=website;
        this.country=country;
        this.gender=gender;
        this.birthDate=bDate;
        this.careerTitle=cTitle;
        this.photoURL=pURL;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry(String country)
    {
        this.country = country;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(String birthDate)
    {
        this.birthDate = birthDate;
    }

    public String getCareerTitle()
    {
        return careerTitle;
    }

    public void setCareerTitle(String careerTitle)
    {
        this.careerTitle = careerTitle;
    }

    public String getPhotoURL()
    {
        return photoURL;
    }

    public void setPhotoURL(String photoURL)
    {
        this.photoURL = photoURL;
    }
}
