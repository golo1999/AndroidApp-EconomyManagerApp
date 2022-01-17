package com.example.economy_manager.model;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class Transaction {
    private static final String[] categoryList = new String[]{"Deposits", "Independent sources", "Salary", "Saving",
            "Bills", "Car", "Clothes", "Communications", "Eating out", "Entertainment", "Food", "Gifts", "Health",
            "House", "Pets", "Sports", "Taxi", "Toiletry", "Transport"};
    private String id;
    private int category;
    private MyCustomTime time;
    private int type;
    private String note;
    private String value;

    public Transaction() {
        // Required empty public constructor
    }

    public Transaction(String id, int category, MyCustomTime time, int type, String note, String value) {
        this.id = id;
        this.category = category;
        this.time = time;
        this.type = type;
        this.note = note;
        this.value = value;
    }

    public Transaction(int category, int type, String note, String value) {
        LocalDateTime now = LocalDateTime.now();

        this.id = generateRandomID();
        this.category = category;
        this.time = new MyCustomTime(now.getYear(),
                now.getMonthValue(),
                String.valueOf(now.getMonth()),
                now.getDayOfMonth(),
                String.valueOf(now.getDayOfWeek()),
                now.getHour(),
                now.getMinute(),
                now.getSecond());
        this.type = type;
        this.note = note;
        this.value = value;
    }

    public Transaction(int category, int type, String value) {
        LocalDateTime now = LocalDateTime.now();

        this.id = generateRandomID();
        this.category = category;
        this.time = new MyCustomTime(now.getYear(),
                now.getMonthValue(),
                String.valueOf(now.getMonth()),
                now.getDayOfMonth(),
                String.valueOf(now.getDayOfWeek()),
                now.getHour(),
                now.getMinute(),
                now.getSecond());
        this.type = type;
        this.value = value;
    }

    public static int getIndexFromCategory(final String categoryName) {
        int index = -1;

        for (final String category : categoryList) {
            ++index;

            if (category.equals(categoryName.trim())) {
                break;
            }
        }

        return index;
    }

    public static String getTypeFromIndexInEnglish(final int index) {
        return index <= categoryList.length ? categoryList[index] : "";
    }

    public static int getIndexFromType(@NonNull final String typeName) {
        return typeName.equals("Income") ? 1 : 0;
    }

    @NonNull
    private String generateRandomID() {
        return String.valueOf(UUID.randomUUID());
    }
}