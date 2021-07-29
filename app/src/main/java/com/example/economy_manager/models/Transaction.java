package com.example.economy_manager.models;

import android.util.Log;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

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

        for (String category : categoryList) {
            ++index;

            if (category.equals(categoryName.trim()))
                break;

            Log.d("category123", categoryName + " " + category + " " + index);
        }

        return index;
    }

    public static String getTypeFromIndexInEnglish(int index) {
        return index <= categoryList.length ? categoryList[index] : "";
    }

    public static int getIndexFromType(@NonNull final String typeName) {
        return typeName.equals("Income") ? 1 : 0;
    }

    public String getId() {
        return id;
    }

    @NonNull
    private String generateRandomID() {
        return String.valueOf(UUID.randomUUID());
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public MyCustomTime getTime() {
        return time;
    }

    public void setTime(MyCustomTime time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return category == that.category &&
                type == that.type &&
                id.equals(that.id) &&
                time.equals(that.time) &&
                Objects.equals(note, that.note) &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, time, type, note, value);
    }

    @NonNull
    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", category=" + category +
                ", time=" + time +
                ", type=" + type +
                ", note='" + note + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}