package com.example.economy_manager.main_part.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economy_manager.R;
import com.example.economy_manager.main_part.view.activity.EditSpecificTransactionActivity;
import com.example.economy_manager.main_part.viewmodel.EditTransactionsViewModel;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomSharedPreferences;
import com.example.economy_manager.utility.Types;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class EditTransactionsRecyclerViewAdapter extends RecyclerView
        .Adapter<EditTransactionsRecyclerViewAdapter.EditTransactionsViewHolder> {
    private final EditTransactionsViewModel viewModel;
    private final ArrayList<Transaction> transactionsList;
    private final Context context;
    private Transaction transaction;
    private final UserDetails userDetails;
    private final RecyclerView recyclerView;

    public EditTransactionsRecyclerViewAdapter(final EditTransactionsViewModel viewModel,
                                               final ArrayList<Transaction> transactionsList,
                                               final Context context, final UserDetails userDetails,
                                               final RecyclerView recyclerView/*, Transaction transaction */) {
        this.viewModel = viewModel;
        this.transactionsList = transactionsList;
        this.context = context;
        this.userDetails = userDetails;
        this.recyclerView = recyclerView;
        //this.transaction = transaction;
    }

    @NonNull
    @Override
    public EditTransactionsViewHolder onCreateViewHolder(final @NonNull ViewGroup parent, final int viewType) {
        final View view =
                LayoutInflater.from(context).inflate(R.layout.transaction_layout_cardview, parent, false);

        return new EditTransactionsViewHolder(view, viewModel, context, transactionsList, recyclerView);
    }

    @Override
    public void onBindViewHolder(final @NonNull EditTransactionsViewHolder holder, final int position) {
        transaction = transactionsList.get(position);
        final String translatedCategory = Types.getTranslatedType(context,
                String.valueOf(Transaction.getTypeFromIndexInEnglish(transaction.getCategory())));
        final String currencySymbol = userDetails != null ?
                userDetails.getApplicationSettings().getCurrencySymbol() : MyCustomMethods.getCurrencySymbol();
        final boolean darkThemeEnabled = userDetails != null && userDetails.getApplicationSettings().getDarkTheme();
        final String transactionPriceText = Locale.getDefault().getDisplayLanguage().equals("English") ?
                currencySymbol + transaction.getValue() : transaction.getValue() + " " + currencySymbol;

        holder.mainLayout.setBackgroundResource(!darkThemeEnabled ?
                R.drawable.ic_yellow_gradient_soda : R.drawable.ic_white_gradient_tobacco_ad);

        holder.transactionPrice.setText(transactionPriceText);
        holder.transactionCategory.setText(translatedCategory);
        holder.transactionNote.setText(transaction.getNote() != null ? String.valueOf(transaction.getNote()) : "");
        holder.transactionEdit.setImageResource(R.drawable.ic_edit_black);
        holder.transactionDelete.setImageResource(R.drawable.ic_delete);
    }

    @Override
    public int getItemCount() {
        return transactionsList.size();
    }

    public static class EditTransactionsViewHolder extends RecyclerView.ViewHolder {
        private TextView transactionCategory;
        private TextView transactionPrice;
        private TextView transactionNote;
        private ImageView transactionEdit;
        private ImageView transactionDelete;
        private final EditTransactionsViewModel viewModel;
        private final Context context;
        private final ArrayList<Transaction> transactionsList;
        private final RecyclerView recyclerView;
        private Transaction selectedTransaction;
        private ConstraintLayout mainLayout;
        private SharedPreferences preferences;

        public EditTransactionsViewHolder(final @NonNull View itemView,
                                          final EditTransactionsViewModel viewModel,
                                          final Context context,
                                          final ArrayList<Transaction> transactionsList,
                                          final RecyclerView recyclerView) {
            super(itemView);

            this.viewModel = viewModel;
            this.context = context;
            this.transactionsList = transactionsList;
            this.recyclerView = recyclerView;

            setVariables(itemView);
            setOnClickListeners();
        }

        private void setVariables(final View v) {
            preferences = context.getSharedPreferences("ECONOMY_MANAGER_USER_DATA", MODE_PRIVATE);
            transactionCategory = v.findViewById(R.id.transactionCategory);
            transactionPrice = v.findViewById(R.id.transactionPrice);
            transactionNote = v.findViewById(R.id.transactionNote);
            transactionEdit = v.findViewById(R.id.transactionEdit);
            transactionDelete = v.findViewById(R.id.transactionDelete);
            mainLayout = v.findViewById(R.id.transactionLayout);
        }

        private void setOnClickListeners() {
            transactionEdit.setOnClickListener(view -> {
                if (getBindingAdapterPosition() > -1) {
                    // retrieving the selected transaction from the list
                    selectedTransaction = transactionsList.get(getBindingAdapterPosition());

                    // saving it to SharedPreferences
                    MyCustomSharedPreferences.saveTransactionToSharedPreferences(preferences, selectedTransaction);

                    // redirecting to the edit specific transaction activity
                    context.startActivity(new Intent(context, EditSpecificTransactionActivity.class));
                    //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            transactionDelete.setOnClickListener(view -> {
                final EditTransactionsRecyclerViewAdapter adapter =
                        (EditTransactionsRecyclerViewAdapter) recyclerView.getAdapter();
                final int positionInList = getBindingAdapterPosition();

                if (adapter != null && positionInList > -1) {
                    transactionsList.remove(positionInList);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
