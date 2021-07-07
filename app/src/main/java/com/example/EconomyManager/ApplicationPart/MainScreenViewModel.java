package com.example.EconomyManager.ApplicationPart;

import androidx.lifecycle.ViewModel;

import com.example.EconomyManager.UserDetails;

public class MainScreenViewModel extends ViewModel {
    private UserDetails userDetails;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
}