package com.example.economy_manager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class CustomDialogChangePassword extends AppCompatDialogFragment {
    private EditText oldPassword, newPassword;
    private CustomDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (getActivity() != null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater
                    .inflate(R.layout.linearlayout_custom_dialog_type_new_password, null);
            builder.setView(view).setPositiveButton(getResources().getString(R.string.proceed), (dialog, which) -> {
                String typedOldPassword = String.valueOf(oldPassword.getText());
                String typedNewPassword = String.valueOf(newPassword.getText());
                listener.changePassword(typedOldPassword, typedNewPassword);
            }).setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {

            });
            oldPassword = view.findViewById(R.id.changePasswordOldPassword);
            newPassword = view.findViewById(R.id.changePasswordNewPassword);
        }
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CustomDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement the listener");
        }
    }

    public interface CustomDialogListener {
        void changePassword(String oldPass, String newPass);
    }
}
