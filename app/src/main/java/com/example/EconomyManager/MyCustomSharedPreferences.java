package com.example.EconomyManager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class MyCustomSharedPreferences {
    public static void saveUserDetailsToSharedPreferences(SharedPreferences preferences,
                                                          UserDetails details) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(details);

        editor.putString("currentUserDetails", json);
        editor.apply();
    }

    public static UserDetails retrieveUserDetailsFromSharedPreferences(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences("ECONOMY_MANAGER_USER_DATA", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("currentUserDetails", "");

        return gson.fromJson(json, UserDetails.class);
    }
}