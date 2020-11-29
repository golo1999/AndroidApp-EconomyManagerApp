package com.example.EconomyManager.ApplicationPart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.EconomyManager.LoginPart.LogIn;
import com.example.EconomyManager.R;
import com.google.firebase.auth.FirebaseAuth;

public class ActivitySplashScreen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        LogoLauncher launcher=new LogoLauncher();
        launcher.start();
    }

    public class LogoLauncher extends Thread
    {
        public void run()
        {
            try
            {
                sleep(2000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            FirebaseAuth fbAuth=FirebaseAuth.getInstance();
            Intent intent;
            if(fbAuth.getCurrentUser()!=null)
                intent=new Intent(ActivitySplashScreen.this, ActivityMainScreen.class);
            else intent=new Intent(ActivitySplashScreen.this, LogIn.class);
            startActivity(intent);
            finish();
        }
    }
}