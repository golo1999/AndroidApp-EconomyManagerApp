package com.example.economy_manager.login_part.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.ForgotPasswordActivityBinding;
import com.example.economy_manager.login_part.viewmodels.ForgotPasswordViewModel;
import com.example.economy_manager.utilities.MyCustomMethods;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ForgotPasswordActivityBinding binding;
    private ForgotPasswordViewModel viewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVariables();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyCustomMethods.finishActivityWithSlideTransition(this, 1);
    }

    private void setVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.forgot_password_activity);
        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        binding.setActivity(this);
        binding.setViewModel(viewModel);
    }
}