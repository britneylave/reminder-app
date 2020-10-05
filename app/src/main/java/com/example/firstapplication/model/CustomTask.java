package com.example.firstapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class CustomTask extends Tasks implements Parcelable {

    private String initial, name, location, date, time, key, filterTask;
    private Assistant sharedBy;
    private boolean notify, status;

    public CustomTask(String initial, String name, String location, String date, String time, boolean notify, boolean status) {
        this.initial = initial;
        this.name = name;
        this.location = location;
        this.date = date;
        this.time = time;
        this.notify = notify;
        this.status = status;
    }

    private CustomTask(Parcel in) {

        this.date = in.readString();
        this.initial = in.readString();
        this.location = in.readString();
        this.name = in.readString();
        this.time = in.readString();
        this.notify = Boolean.parseBoolean(in.readString());
        this.status = Boolean.parseBoolean(in.readString());

    }

    public CustomTask() {
    }

    public Assistant getSharedBy() {
        return sharedBy;
    }

    public String getFilterTask() {
        if (sharedBy == null) return "";
        return String.format("%s-%s-%s", sharedBy.getInitial(), initial, key);
    }

    public void setSharedBy(Assistant sharedBy) {
        this.sharedBy = sharedBy;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getName() {
        return name;
    }

    @Exclude
    public String getTitleName() {
        if (sharedBy != null)
            return String.format("%s\nby %s", name, sharedBy.getInitial());
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(initial);
        parcel.writeString(location);
        parcel.writeString(name);
        parcel.writeString(time);
        parcel.writeString(String.valueOf(notify));
        parcel.writeString(String.valueOf(status));
    }

    public static final Creator<CustomTask> CREATOR = new Creator<CustomTask>() {
        @Override
        public CustomTask createFromParcel(Parcel in) {
            return new CustomTask(in);
        }

        @Override
        public CustomTask[] newArray(int size) {
            return new CustomTask[size];
        }
    };
}
