package com.example.firstapplication.shared.comparator;

import com.example.firstapplication.model.CustomTask;

import java.util.Comparator;

public class TaskStatusComparator implements Comparator<CustomTask> {

    @Override
    public int compare(CustomTask customTask, CustomTask t1) {
        return Boolean.compare(t1.isStatus(),customTask.isStatus());
    }
}