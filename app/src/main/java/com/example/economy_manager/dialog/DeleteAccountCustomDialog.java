package com.example.economy_manager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.databinding.DataBindingUtil;

import com.example.economy_manager.R;
import com.example.economy_manager.databinding.CustomDialogBinding;

public class DeleteAccountCustomDialog extends AppCompatDialogFragment {

    private CustomDialogBinding binding;
    private CustomDialogListener listener;
    private final int choice;

    public interface CustomDialogListener {
        void getTypedPasswordAndDeleteTheAccount(final String password);

        void deleteAccount();

        void getTypedPasswordAndResetTheDatabase(final String password);

        void resetDatabase();
    }

    public DeleteAccountCustomDialog(final int choice) {
        this.choice = choice;
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
            switch (choice) {
                case 0:
                case 1:
                    builder.setView(binding.getRoot())
                            .setPositiveButton(getResources().getString(R.string.proceed), (DialogInterface dialog, int which) -> {
                                final String typedPassword =
                                        String.valueOf(binding.passwordField.getText());

                                if (choice == 0) {
                                    listener.getTypedPasswordAndDeleteTheAccount(typedPassword);
                                } else {
                                    listener.getTypedPasswordAndResetTheDatabase(typedPassword);
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {

                            });
                    break;
                case 2:
                    listener.deleteAccount();
                    break;
                case 3:
                    listener.resetDatabase();
                    break;
            }
        }

        return builder.create();
    }

    public void setBinding() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.custom_dialog, null, false);
    }
}
