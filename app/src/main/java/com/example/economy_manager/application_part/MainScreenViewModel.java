package com.example.economy_manager.application_part;

import androidx.lifecycle.ViewModel;

import com.example.economy_manager.UserDetails;

public class MainScreenViewModel extends ViewModel {
    private UserDetails userDetails;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
}