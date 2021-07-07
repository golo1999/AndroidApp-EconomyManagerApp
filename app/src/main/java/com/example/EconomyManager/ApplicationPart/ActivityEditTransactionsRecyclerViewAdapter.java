package com.example.EconomyManager.ApplicationPart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.EconomyManager.R;
import com.example.EconomyManager.Transaction;
import com.example.EconomyManager.Types;
import com.example.EconomyManager.UserDetails;

import java.util.ArrayList;
import java.util.Locale;

public class ActivityEditTransactionsRecyclerViewAdapter extends RecyclerView
        .Adapter<ActivityEditTransactionsRecyclerViewAdapter
        .ActivityEditTransactionsRecyclerViewAdapterViewHolder> {
    private final EditTransactionsViewModel viewModel;
    private final ArrayList<Transaction> transactionsList;
    private final Context context;
    private Transaction transaction;
    private final UserDetails userDetails;

    public ActivityEditTransactionsRecyclerViewAdapter(final EditTransactionsViewModel viewModel,
                                                       final ArrayList<Transaction> transactionsList,
                                                       final Context context, final UserDetails userDetails/*, Transaction transaction */) {
        this.viewModel = viewModel;
        this.transactionsList = transactionsList;
        this.context = context;
        this.userDetails = userDetails;
        //this.transaction = transaction;
    }

    @NonNull
    @Override
    public ActivityEditTransactionsRecyclerViewAdapterViewHolder onCreateViewHolder(final @NonNull ViewGroup parent,
                                                                                    final int viewType) {
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.cardview_transaction_layout, parent, false);

        return new ActivityEditTransactionsRecyclerViewAdapterViewHolder(view, viewModel, context,
                transactionsList);
    }

    @Override
    public void onBindViewHolder(final @NonNull ActivityEditTransactionsRecyclerViewAdapterViewHolder holder,
                                 final int position) {
        transaction = transactionsList.get(position);
        final String translatedCategory = Types.getTranslatedType(context,
                String.valueOf(Transaction.getTypeFromIndexInEnglish(transaction.getCategory())));

        if (userDetails != null) {
            final String currencySymbol = userDetails.getApplicationSettings().getCurrencySymbol();
            final boolean darkThemeEnabled = userDetails.getApplicationSettings().getDarkTheme();
            final String transactionPriceText = Locale.getDefault().getDisplayLanguage().equals("English") ?
                    currencySymbol + transaction.getValue() :
                    transaction.getValue() + " " + currencySymbol;

            holder.mainLayout.setBackgroundResource(!darkThemeEnabled ?
                    R.drawable.ic_yellow_gradient_soda :
                    R.drawable.ic_white_gradient_tobacco_ad);
            holder.transactionPrice.setText(transactionPriceText);
        } else {
            holder.transactionPrice.setText(transaction.getValue());
        }

        holder.transactionCategory.setText(translatedCategory);
        holder.transactionNote.setText(transaction.getNote() != null ?
                String.valueOf(transaction.getNote()) : "");
        holder.transactionEdit.setImageResource(R.drawable.ic_edit_black);
        holder.transactionDelete.setImageResource(R.drawable.ic_delete);
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    public static class ActivityEditTransactionsRecyclerViewAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView transactionCategory;
        private TextView transactionPrice;
        private TextView transactionNote;
        private ImageView transactionEdit;
        private ImageView transactionDelete;
        private EditTransactionsViewModel viewModel;
        private Context context;
        private Transaction selectedTransaction;
        private ArrayList<Transaction> transactionsList;
        private ConstraintLayout mainLayout;

        public ActivityEditTransactionsRecyclerViewAdapterViewHolder(final @NonNull View itemView,
                                                                     final EditTransactionsViewModel viewModel,
                                                                     final Context context,
                                                                     final ArrayList<Transaction> transactionsList) {
            super(itemView);

            this.viewModel = viewModel;
            this.context = context;
            this.transactionsList = transactionsList;

            setVariables(itemView);
            setOnClickListeners();
        }

        private void setVariables(final View v) {
            transactionCategory = v.findViewById(R.id.transactionCategory);
            transactionPrice = v.findViewById(R.id.transactionPrice);
            transactionNote = v.findViewById(R.id.transactionNote);
            transactionEdit = v.findViewById(R.id.transactionEdit);
            transactionDelete = v.findViewById(R.id.transactionDelete);
            mainLayout = v.findViewById(R.id.transactionLayout);
        }

        private void setOnClickListeners() {
            transactionEdit.setOnClickListener(view -> {
                if (getBindingAdapterPosition() != -1) {
                    selectedTransaction = transactionsList.get(getBindingAdapterPosition());

                    viewModel.setSelectedTransaction(selectedTransaction);
                }
            });

            transactionDelete.setOnClickListener(view -> {

            });
        }
    }
}
