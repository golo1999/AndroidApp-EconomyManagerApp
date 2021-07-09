package com.example.economy_manager.main_part.dialog;

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

import com.example.economy_manager.R;

public class CustomDialogChangePassword extends AppCompatDialogFragment {
    private EditText oldPasswordField;
    private EditText newPasswordField;
    private CustomDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(final @Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getActivity() != null) {
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.linearlayout_custom_dialog_type_new_password, null);

            builder.setView(view).setPositiveButton(getResources().getString(R.string.proceed), (dialog, which) -> {
                final String typedOldPassword = String.valueOf(oldPasswordField.getText());
                final String typedNewPassword = String.valueOf(newPasswordField.getText());

                listener.changePassword(typedOldPassword, typedNewPassword);
            }).setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {

            });

            oldPasswordField = view.findViewById(R.id.changePasswordOldPassword);
            newPasswordField = view.findViewById(R.id.changePasswordNewPassword);
        }

        return builder.create();
    }

    @Override
    public void onAttach(final @NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (CustomDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement the listener");
        }
    }

    public interface CustomDialogListener {
        void changePassword(final String oldPass, final String newPass);
    }
}
