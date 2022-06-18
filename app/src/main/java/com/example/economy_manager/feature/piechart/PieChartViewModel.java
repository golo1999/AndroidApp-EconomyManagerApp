package com.example.economy_manager.feature.piechart;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.economy_manager.R;
import com.example.economy_manager.model.Transaction;
import com.example.economy_manager.utility.Types;

public class PieChartViewModel extends ViewModel {

    public int getPieChartCategoryColor(final @NonNull Context context,
                                        final int key) {
        return context.getColor(key == 0 ?
                R.color.incomes_deposits : key == 1 ?
                R.color.incomes_independent_sources : key == 2 ?
                R.color.incomes_salary : key == 3 ?
                R.color.incomes_saving : key == 4 ?
                R.color.expenses_bills : key == 5 ?
                R.color.expenses_car : key == 6 ?
                R.color.expenses_clothes : key == 7 ?
                R.color.expenses_communications : key == 8 ?
                R.color.expenses_eating_out : key == 9 ?
                R.color.expenses_entertainment : key == 10 ?
                R.color.expenses_food : key == 11 ?
                R.color.expenses_gifts : key == 12 ?
                R.color.expenses_health : key == 13 ?
                R.color.expenses_house : key == 14 ?
                R.color.expenses_pets : key == 15 ?
                R.color.expenses_sports : key == 16 ?
                R.color.expenses_taxi : key == 17 ?
                R.color.expenses_toiletry : R.color.expenses_transport);
    }

    public LinearLayout getPieChartDetail(final @NonNull Context context,
                                          final int categoryIndex,
                                          final int categoryPercentage,
                                          final int categoryColor) {
        final LinearLayout categoryLayout = new LinearLayout(context);
        final LinearLayout.LayoutParams categoryLayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        categoryLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        categoryLayout.setLayoutParams(categoryLayoutParams);

        final View categoryView = new View(context);
        final ViewGroup.LayoutParams categoryViewParams = new ViewGroup.LayoutParams(30, 30);

        categoryView.setBackgroundColor(categoryColor);
        categoryView.setLayoutParams(categoryViewParams);
        categoryLayout.addView(categoryView);

        final TextView categoryTextView = new TextView(context);
        final ViewGroup.LayoutParams categoryTextViewParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final String categoryTextViewText = Types.getTranslatedType(context,
                String.valueOf(Transaction.getTypeFromIndexInEnglish(categoryIndex))) +
                " " + "(" + categoryPercentage + "%)";

        categoryTextView.setPaddingRelative(10, 0, 0, 0);
        categoryTextView.setText(categoryTextViewText);
        categoryTextView.setTextSize(16);
        categoryTextView.setLayoutParams(categoryTextViewParams);
        categoryLayout.addView(categoryTextView);

        return categoryLayout;
    }
}