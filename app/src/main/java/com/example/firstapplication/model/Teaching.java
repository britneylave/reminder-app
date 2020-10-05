package com.example.firstapplication.model;

import com.example.firstapplication.shared.Utilities;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Teaching extends Tasks {

    @SerializedName("Subject")
    @Expose
    private String subject;
    @SerializedName("Class")
    @Expose
    private String _class;
    @SerializedName("Day")
    @Expose
    private int day;
    @SerializedName("Shift")
    @Expose
    private String shift;
    @SerializedName("Room")
    @Expose
    private String room;
    @SerializedName("Assistant")
    @Expose
    private String assistant;
    @SerializedName("Realization")
    @Expose
    private String realization;


    public Teaching() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getAssistant() {
        return assistant;
    }

    public void setAssistant(String assistant) {
        this.assistant = assistant;
    }

    public String getRealization() {
        return realization;
    }

    public void setRealization(String realization) {
        this.realization = realization;
    }

    public Utilities.Weekday getDay() {
        return Utilities.Weekday.values()[day];
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
