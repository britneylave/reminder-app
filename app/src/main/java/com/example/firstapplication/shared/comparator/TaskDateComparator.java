package com.example.firstapplication.shared.comparator;

import com.example.firstapplication.model.CustomTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class TaskDateComparator implements Comparator<CustomTask> {


    @Override
    public int compare(CustomTask customTask, CustomTask t1) {
        Date dateLeft = null;
        Date dateRight = null;
        try {
            dateLeft = new SimpleDateFormat("dd/MM/yyyy").parse(customTask.getDate());
            dateRight = new SimpleDateFormat("dd/MM/yyyy").parse(t1.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateLeft.compareTo(dateRight);
    }
}

