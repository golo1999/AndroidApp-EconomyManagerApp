package com.example.economy_manager.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.economy_manager.model.UserDetails;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public final class MyCustomSharedPreferences {
    private MyCustomSharedPreferences() {

    }

    public static void saveUserDetailsToSharedPreferences(final SharedPreferences preferences,
                                                          final UserDetails details) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(details);

        editor.putString("currentUserDetails", json);
        editor.apply();
    }

    public static UserDetails retrieveUserDetailsFromSharedPreferences(final Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences("ECONOMY_MANAGER_USER_DATA", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("currentUserDetails", "");

        return gson.fromJson(json, UserDetails.class);
    }
}