package com.example.economy_manager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.databinding.DataBindingUtil;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.ChangePasswordCustomDialogBinding;

public class ChangePasswordCustomDialog extends AppCompatDialogFragment {

    private ChangePasswordCustomDialogBinding binding;
    private CustomDialogListener listener;

    public interface CustomDialogListener {
        void changePassword(final String oldPass, final String newPass);
    }

    @Override
    public void onAttach(final @NonNull Context context) {
        super.onAttach(context);
        setBinding();

        try {
            listener = (CustomDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getResources()
                    .getString(R.string.must_implement_the_listener, context,
                            context.getResources().getString(R.string.custom_dialog_listener)));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final @Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getActivity() != null) {
            builder.setView(binding.getRoot())
                    .setPositiveButton(getResources().getString(R.string.proceed),
                            (dialog, which) -> {
                                final String typedOldPassword =
                                        String.valueOf(binding.oldPasswordField.getText());
                                final String typedNewPassword =
                                        String.valueOf(binding.newPasswordField.getText());

                                listener.changePassword(typedOldPassword, typedNewPassword);
                            })
                    .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {

                    });
        }

        return builder.create();
    }

    public void setBinding() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.change_password_custom_dialog, null, false);
    }
}