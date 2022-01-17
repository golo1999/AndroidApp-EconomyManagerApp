package com.example.economy_manager.feature.edittransactions;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economy_manager.R;
import com.example.economy_manager.dialog.DeleteTransactionCustomDialog;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.model.UserDetails;
import com.example.economy_manager.utility.MyCustomMethods;
import com.example.economy_manager.utility.MyCustomVariables;
import com.example.economy_manager.utility.Types;

import java.util.ArrayList;
import java.util.Locale;

public class EditTransactionsRecyclerViewAdapter
        extends RecyclerView.Adapter<EditTransactionsRecyclerViewAdapter.EditTransactionsViewHolder> {
    private final EditTransactionsViewModel viewModel;
    private final ArrayList<Transaction> transactionsList;
    private final Context context;
    private Transaction transaction;
    private final UserDetails userDetails;
    private final RecyclerView recyclerView;
    private final FragmentManager fragmentManager;

    public EditTransactionsRecyclerViewAdapter(final EditTransactionsViewModel viewModel,
                                               final ArrayList<Transaction> transactionsList,
                                               final Context context,
                                               final UserDetails userDetails,
                                               final RecyclerView recyclerView,
                                               final FragmentManager fragmentManager/*, Transaction transaction */) {
        this.viewModel = viewModel;
        this.transactionsList = transactionsList;
        this.context = context;
        this.userDetails = userDetails;
        this.recyclerView = recyclerView;
        this.fragmentManager = fragmentManager;
        //this.transaction = transaction;
    }

    @NonNull
    @Override
    public EditTransactionsViewHolder onCreateViewHolder(final @NonNull ViewGroup parent,
                                                         final int viewType) {
        final View view =
                LayoutInflater.from(context).inflate(R.layout.transaction_layout_cardview, parent, false);

        return new EditTransactionsViewHolder(view, viewModel, context, transactionsList, recyclerView, fragmentManager);
    }

    @Override
    public void onBindViewHolder(final @NonNull EditTransactionsViewHolder holder,
                                 final int position) {
        transaction = transactionsList.get(position);

        final String translatedCategory = Types.getTranslatedType(context,
                String.valueOf(Transaction.getTypeFromIndexInEnglish(transaction.getCategory())));
        final String currencySymbol = userDetails != null ?
                userDetails.getApplicationSettings().getCurrencySymbol() : MyCustomMethods.getCurrencySymbol();
        final boolean darkThemeEnabled = userDetails != null && userDetails.getApplicationSettings().isDarkThemeEnabled();
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
        private final FragmentManager fragmentManager;

        public EditTransactionsViewHolder(final @NonNull View itemView,
                                          final EditTransactionsViewModel viewModel,
                                          final Context context,
                                          final ArrayList<Transaction> transactionsList,
                                          final RecyclerView recyclerView,
                                          final FragmentManager fragmentManager) {
            super(itemView);

            this.viewModel = viewModel;
            this.context = context;
            this.transactionsList = transactionsList;
            this.recyclerView = recyclerView;
            this.fragmentManager = fragmentManager;

            setVariables(itemView);
            setOnClickListeners();
        }

        private void setVariables(final View v) {
            preferences = context.getSharedPreferences(MyCustomVariables.getSharedPreferencesFileName(), MODE_PRIVATE);
            transactionCategory = v.findViewById(R.id.transaction_category);
            transactionPrice = v.findViewById(R.id.transaction_price);
            transactionNote = v.findViewById(R.id.transaction_note);
            transactionEdit = v.findViewById(R.id.transaction_edit);
            transactionDelete = v.findViewById(R.id.transaction_delete);
            mainLayout = v.findViewById(R.id.transaction_layout);
        }

        private void setOnClickListeners() {
            transactionEdit.setOnClickListener(view -> {
                if (getBindingAdapterPosition() > -1) {
                    // retrieving the selected transaction from the list
                    selectedTransaction = transactionsList.get(getBindingAdapterPosition());

                    if (selectedTransaction != null) {
                        viewModel.setSelectedTransaction(selectedTransaction);
                        viewModel.setSelectedTransactionListPosition(getBindingAdapterPosition());

                        if (viewModel.getEditTransactionsRecyclerViewAdapter() == null) {
                            final EditTransactionsRecyclerViewAdapter adapter =
                                    (EditTransactionsRecyclerViewAdapter) recyclerView.getAdapter();

                            viewModel.setEditTransactionsRecyclerViewAdapter(adapter);
                        }

                        ((EditTransactionsActivity) context).setEditSpecificTransactionFragment();
                    }
                }
            });

            transactionDelete.setOnClickListener(view -> {
                final EditTransactionsRecyclerViewAdapter adapter =
                        (EditTransactionsRecyclerViewAdapter) recyclerView.getAdapter();

                final int positionInList = getBindingAdapterPosition();

                if (adapter != null && positionInList > -1) {
                    showTransactionDeleteDialog(transactionsList, adapter, positionInList);
                }
            });
        }

        private void showTransactionDeleteDialog(final ArrayList<Transaction> transactionsList,
                                                 final EditTransactionsRecyclerViewAdapter adapter,
                                                 final int positionInList) {
            DeleteTransactionCustomDialog deleteTransactionCustomDialog =
                    new DeleteTransactionCustomDialog(transactionsList, adapter, positionInList);

            deleteTransactionCustomDialog.show(fragmentManager, "deleteDialogFragment");
        }
    }
}
