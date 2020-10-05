package com.example.firstapplication.ui.model;

import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.firstapplication.model.Tasks;
import com.example.firstapplication.model.Training;
import com.example.firstapplication.service.messier.MessierService;
import com.example.firstapplication.shared.Utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

import static com.example.firstapplication.shared.Constant.JOB_MODE_CURRENT;
import static com.example.firstapplication.shared.Constant.JOB_MODE_FUTURE;
import static com.example.firstapplication.shared.Constant.JOB_MODE_HISTORY;

public class InboxViewModel extends ViewModel {
    private static final String TAG = "InboxViewModel";

    private MutableLiveData<List<Tasks>> taskList;
    private List<Tasks> tempTask;
    private HashMap<String, String> jobParams;
    private int type;

    public void setJobOptions(int type) {
        if (jobParams == null) jobParams = new HashMap<>();

        this.type = type;

        switch (type) {
            case JOB_MODE_HISTORY:
                jobParams.put("mode", "history");
                break;
            case JOB_MODE_CURRENT:
                jobParams.put("mode", "current");
                break;
            case JOB_MODE_FUTURE:
                jobParams.put("mode", "future");
                break;
        }

    }

    public void setSemester(String semesterId) {
        if (jobParams == null) jobParams = new HashMap<>();
        jobParams.put("semesterId", semesterId);
        Log.d(TAG, "setSemester: " + semesterId);
    }


    public LiveData<List<Tasks>> getTaskList() {
        if (taskList == null) {
            taskList = new MutableLiveData<>();
            loadTask();
        }
        return taskList;
    }

    public void refreshTaskList() {
        loadTask();
    }

    private void loadTask() {
        /*
          load job and teachings
         */
        if (tempTask == null)
            tempTask = new ArrayList<>();

        tempTask.clear();

        MessierService service = MessierService.getInstance();

        Log.d(TAG, "loadTask: " + jobParams.toString());

        service.getTaskSchedule(jobParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<List<Tasks>>() {
                    @Override
                    public void onNext(List<Tasks> tasks) {
                        taskList.setValue(tasks);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        if (Utilities.isInBinusAddress()) {
            service.getTrainingSchedule()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<List<Training>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(List<Training> value) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Date now = new Date();

                                switch (type) {
                                    case JOB_MODE_HISTORY:
                                        tempTask = value.stream()
                                                .filter(training -> new Date(training.getStart()).getTime() < now.getTime())
                                                .collect(Collectors.toList());
                                        break;
                                    case JOB_MODE_CURRENT:
                                        tempTask = value.stream()
                                                .filter(training -> new Date(training.getStart()).getDay() == now.getDay())
                                                .collect(Collectors.toList());
                                        break;
                                    case JOB_MODE_FUTURE:
                                        tempTask = value.stream()
                                                .filter(training -> new Date(training.getStart()).getTime() > now.getTime())
                                                .collect(Collectors.toList());
                                        break;
                                }

                            }
                            taskList.setValue(tempTask);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "Throw : " + e.getMessage());
                        }
                    });
        }
    }
}
