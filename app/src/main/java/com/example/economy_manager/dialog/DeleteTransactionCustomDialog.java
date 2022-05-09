package com.example.economy_manager.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.economy_manager.R;
import com.example.economy_manager.feature.edittransactions.EditTransactionsRecyclerViewAdapter;
import com.example.economy_manager.model.Transaction;

import java.util.ArrayList;

public class DeleteTransactionCustomDialog extends DialogFragment {

    private final ArrayList<Transaction> transactionsList;
    private final EditTransactionsRecyclerViewAdapter adapter;
    private final int positionInList;
    private DeleteDialogListener listener;

    public DeleteTransactionCustomDialog(final ArrayList<Transaction> transactionsList,
                                         final EditTransactionsRecyclerViewAdapter adapter,
                                         final int positionInList) {
        this.transactionsList = transactionsList;
        this.adapter = adapter;
        this.positionInList = positionInList;
    }

    public interface DeleteDialogListener {
        void onDialogPositiveClick(final DialogFragment dialog,
                                   final ArrayList<Transaction> transactionsList,
                                   final EditTransactionsRecyclerViewAdapter adapter,
                                   final int positionInList);

        void onDialogNegativeClick(final DialogFragment dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.are_you_sure_you_want_to_delete_this_transaction)
                .setPositiveButton(R.string.ok, (dialog, id) ->
                        listener.onDialogPositiveClick(DeleteTransactionCustomDialog.this,
                                transactionsList, adapter, positionInList))
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        listener.onDialogNegativeClick(DeleteTransactionCustomDialog.this));

        return builder.create();
    }

    @Override
    public void onAttach(final @NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getResources()
                    .getString(R.string.must_implement_the_listener, context,
                            context.getResources().getString(R.string.delete_dialog_listener)));
        }
    }
}