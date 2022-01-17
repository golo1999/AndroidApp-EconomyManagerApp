package com.example.economy_manager.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MyCustomTime implements Serializable, Comparable<MyCustomTime> {
    private int year;
    private int month;
    private String monthName;
    private int day;
    private String dayName;
    private int hour;
    private int minute;
    private int second;

    public MyCustomTime() {
        // Required empty public constructor
    }

    public MyCustomTime(int year,
                        int month,
                        String monthName,
                        int day,
                        String dayName,
                        int hour,
                        int minute,
                        int second) {
        this.year = year;
        this.month = month;
        this.monthName = monthName;
        this.day = day;
        this.dayName = dayName;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public MyCustomTime(int year,
                        int month,
                        int day,
                        int hour,
                        int minute,
                        int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    @NonNull
    @Override
    public String toString() {
        final String monthParsed = month < 10 ? "0" + month : String.valueOf(month);
        final String dayParsed = day < 10 ? "0" + day : String.valueOf(day);
        final String hourParsed = hour < 10 ? "0" + hour : String.valueOf(hour);
        final String minuteParsed = minute < 10 ? "0" + minute : String.valueOf(minute);
        final String secondParsed = second < 10 ? "0" + second : String.valueOf(second);

        return year + "-" + monthParsed + "-" + dayParsed + " " + hourParsed + ":" + minuteParsed + ":" + secondParsed;
    }

    @Override
    public int compareTo(MyCustomTime myCustomTime) {
        final LocalDateTime localDateTime1 =
                LocalDateTime.of(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond());
        final LocalDateTime localDateTime2 =
                LocalDateTime.of(myCustomTime.getYear(), myCustomTime.getMonth(), myCustomTime.getDay(),
                        myCustomTime.getHour(), myCustomTime.getMinute(), myCustomTime.getSecond());

        return localDateTime1.isBefore(localDateTime2) ?
                -1 : localDateTime1.isEqual(localDateTime2) ?
                0 : 1;
    }
}
