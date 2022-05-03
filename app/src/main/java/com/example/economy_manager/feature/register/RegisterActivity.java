package com.example.economy_manager.feature.register;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.RegisterActivityBinding;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;

public class RegisterActivity extends AppCompatActivity {
    private RegisterActivityBinding binding;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityVariables();
        setLayoutVariables();
        setPreferences();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyCustomMethods.finishActivityWithSlideTransition(this, 0);
    }

    private void setActivityVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.register_activity);
        viewModel = new RegisterViewModel(binding);
    }

    private void setLayoutVariables() {
        binding.setActivity(this);
        binding.setViewModel(viewModel);
    }

    public void setPreferences() {
        final SharedPreferences preferences =
                getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);

        viewModel.setPreferences(preferences);
    }
}