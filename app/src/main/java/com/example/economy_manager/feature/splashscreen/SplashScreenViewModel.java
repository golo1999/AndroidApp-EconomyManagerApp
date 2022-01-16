package com.example.economy_manager.feature.splashscreen;

import androidx.lifecycle.ViewModel;

public class SplashScreenViewModel extends ViewModel {
    public void startSplashScreenHandler(final SplashScreenActivity.LogoLauncher launcher) {
        launcher.start();
    }
}