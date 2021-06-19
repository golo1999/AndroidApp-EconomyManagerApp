package com.example.EconomyManager;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class MyCustomTime implements Serializable {
    private int year;
    private int month;
    private String monthName;
    private int day;
    private String dayName;
    private int hour;
    private int minute;
    private int second;

    public MyCustomTime() {

    }

    public MyCustomTime(int year, int month, String monthName, int day, String dayName, int hour,
                        int minute, int second) {
        this.year = year;
        this.month = month;
        this.monthName = monthName;
        this.day = day;
        this.dayName = dayName;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    @NonNull
    @Override
    public String toString() {
        return "MyTime{" +
                "year=" + year +
                ", month=" + month +
                ", monthName='" + monthName + '\'' +
                ", day=" + day +
                ", dayName='" + dayName + '\'' +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                '}';
    }
}
