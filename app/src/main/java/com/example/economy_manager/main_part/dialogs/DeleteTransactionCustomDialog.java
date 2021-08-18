package com.example.economy_manager.main_part.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.adapters.EditTransactionsRecyclerViewAdapter;
import com.example.economy_manager.models.Transaction;

import java.util.ArrayList;

public class DeleteTransactionCustomDialog extends DialogFragment {
    private final ArrayList<Transaction> transactionsList;
    private final EditTransactionsRecyclerViewAdapter adapter;
    private final int positionInList;

    public DeleteTransactionCustomDialog(final ArrayList<Transaction> transactionsList,
                                         final EditTransactionsRecyclerViewAdapter adapter, final int positionInList) {
        this.transactionsList = transactionsList;
        this.adapter = adapter;
        this.positionInList = positionInList;
    }

    public interface DeleteDialogListener {
        void onDialogPositiveClick(final DialogFragment dialog, final ArrayList<Transaction> transactionsList,
                                   final EditTransactionsRecyclerViewAdapter adapter, final int positionInList);

        void onDialogNegativeClick(final DialogFragment dialog);
    }

    private DeleteDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.delete_transaction)
                .setPositiveButton(R.string.ok, (dialog, id) ->
                        listener.onDialogPositiveClick(DeleteTransactionCustomDialog.this, transactionsList,
                                adapter, positionInList))
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        listener.onDialogNegativeClick(DeleteTransactionCustomDialog.this));

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString() + " must implement NoticeDialogListener");
        }
    }
}