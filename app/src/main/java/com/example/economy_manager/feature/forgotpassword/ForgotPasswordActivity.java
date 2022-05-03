package com.example.economy_manager.feature.forgotpassword;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.ForgotPasswordActivityBinding;
import com.example.economy_manager.utility.MyCustomMethods;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ForgotPasswordActivityBinding binding;
    private ForgotPasswordViewModel viewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityVariables();
        setLayoutVariables();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyCustomMethods.finishActivityWithSlideTransition(this, 1);
    }

    private void setActivityVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.forgot_password_activity);
        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);
    }

    private void setLayoutVariables() {
        binding.setActivity(this);
        binding.setViewModel(viewModel);
    }
}