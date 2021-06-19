package com.example.EconomyManager;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private static String[] categoryList = new String[]{"Deposits", "Independent sources", "Salary",
            "Saving", "Bills", "Car", "Clothes", "Communications", "Eating out", "Entertainment",
            "Food", "Gifts", "Health", "House", "Pets", "Sports", "Taxi", "Toiletry", "Transport"};
    private String id;
    private int category;
    private MyCustomTime time;
    private int type;
    private String note;
    private String value;

    public Transaction() {

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

    public static int getIndexFromCategory(String categoryName) {
        int index = -1;

        for (String category : categoryList) {
            ++index;

            if (category.equals(categoryName.trim()))
                break;
        }

        return index;
    }

    public static String getTypeFromIndexInEnglish(int index) {
        return index <= categoryList.length ? categoryList[index] : "";
    }

    public static int getIndexFromType(String typeName) {
        return typeName.equals("Income") ? 1 : 0;
    }

    public String getId() {
        return id;
    }

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
}