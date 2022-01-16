package com.example.economy_manager.feature.register;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.SignUpActivityBinding;
import com.example.economy_manager.feature.register.SignUpViewModel;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;

public class SignUpActivity extends AppCompatActivity {
    private SignUpActivityBinding binding;
    private SignUpViewModel viewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVariables();
        setPreferences();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyCustomMethods.finishActivityWithSlideTransition(this, 0);
    }

    private void setVariables() {
        binding = DataBindingUtil.setContentView(this, R.layout.sign_up_activity);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        binding.setActivity(this);
        binding.setViewModel(viewModel);
    }

    public void setPreferences() {
        final SharedPreferences preferences =
                getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);

        viewModel.setPreferences(preferences);
    }
}