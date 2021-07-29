package com.example.economy_manager.models;

public class MoneyManager implements Comparable<MoneyManager> {
    private static MoneyManager lastTransaction = null;
    private static float lastOverall = 0f;
    private String type, date, note;
    private Float value;

    public MoneyManager(String note, Float value, String date, String type) {
        this.note = note;
        this.value = value;
        this.date = date;
        this.type = type;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String newNote) {
        this.note = newNote;
    }

    public Float getValue() {
        return this.value;
    }

    public void setValue(Float newValue) {
        this.value = newValue;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String newDate) {
        this.date = newDate;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String newType) {
        this.type = newType;
    }

    public static MoneyManager getLastTransaction() {
        return lastTransaction;
    }

    public static void setLastTransaction(MoneyManager transaction) {
        MoneyManager.lastTransaction = transaction;
    }

    public static float getLastOverall() {
        return lastOverall;
    }

    public static void setLastOverall(float lastOverall) {
        MoneyManager.lastOverall = lastOverall;
    }

    public int compareTo(MoneyManager o) {
        return this.getValue().compareTo(o.getValue());
    }
}
