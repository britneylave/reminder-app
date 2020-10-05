package com.example.firstapplication.model;

import androidx.annotation.NonNull;

public class Semester {
    private String Description;
    private String SemesterId;

    public Semester(String description, String semesterId) {
        Description = description;
        SemesterId = semesterId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSemesterId() {
        return SemesterId;
    }

    public void setSemesterId(String semesterId) {
        SemesterId = semesterId;
    }

    @NonNull
    @Override
    public String toString() {
        String[] divided = getDescription().split(" ");

        /**
         * return semester + year
         * ex : Odd 2019/2020
         */
        return divided[0] + " " + divided[divided.length - 1];
    }
}
