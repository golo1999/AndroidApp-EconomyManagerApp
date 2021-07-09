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

public class CustomDialogDeleteAccount extends AppCompatDialogFragment {
    private EditText password;
    private CustomDialogListener listener;
    private final int choice;

    public CustomDialogDeleteAccount(final int choice) {
        this.choice = choice;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final @Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (getActivity() != null) {
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.linearlayout_custom_dialog, null);

            if (choice == 0 || choice == 1) {
                builder.setView(view).setPositiveButton(getResources().getString(R.string.proceed), (dialog, which) -> {
                    final String typedPassword = String.valueOf(password.getText());

                    if (choice == 0) {
                        listener.getTypedPasswordAndDeleteTheAccount(typedPassword);
                    } else {
                        listener.getTypedPasswordAndResetTheDatabase(typedPassword);
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {

                });
            } else if (choice == 2) {
                listener.deleteAccount();
            } else if (choice == 3) {
                listener.resetDatabase();
            }

            password = view.findViewById(R.id.deleteAccountPassword);
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
        void getTypedPasswordAndDeleteTheAccount(final String pass);

        void deleteAccount();

        void getTypedPasswordAndResetTheDatabase(final String pass);

        void resetDatabase();
    }
}
