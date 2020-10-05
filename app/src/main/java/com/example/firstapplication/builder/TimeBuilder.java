package com.example.firstapplication.builder;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class TimeBuilder {

    private int second = 0;       // no. of ms in a second
    private int minute = 0; // no. of ms in a minute
    private int hour = 0;// no. of ms in an hour
    private int day = 0;// no. of ms in a day
    private int week = 0;       // no. of ms in a week
    private int month = 0;       // no. of ms in a month

    public TimeBuilder() {
    }

    public TimeBuilder setSecond(int second) {
        this.second = second;
        return this;
    }

    public TimeBuilder setMinute(int minute) {
        this.minute = minute;
        return this;
    }

    public TimeBuilder setHour(int hour) {
        this.hour = hour;
        return this;
    }

    public TimeBuilder setDay(int day) {
        this.day = day;
        return this;
    }

    public TimeBuilder setWeek(int week) {
        this.week = week;
        return this;
    }

    @Override
    public String toString() {
        if (month > 0)
            return month + "m";
        if (week > 0)
            return week + "w";
        if (day > 0)
            return day + "d";
        if (hour > 0)
            return hour + "h";
        if (minute > 0)
            return minute + "m";
        if (second > 0)
            return second + "s";

        return "";
    }

    public TimeBuilder setMonth(int month) {
        this.month = month;
        return this;
    }
}
