package com.example.firstapplication.service.messier;

import android.util.Log;

import com.example.firstapplication.model.Assistant;
import com.example.firstapplication.model.Jobs;
import com.example.firstapplication.model.Semester;
import com.example.firstapplication.model.Tasks;
import com.example.firstapplication.model.Teaching;
import com.example.firstapplication.model.Training;
import com.example.firstapplication.service.GetDataService;
import com.example.firstapplication.service.IService;
import com.example.firstapplication.service.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessierService {

    private static final String TAG = "MessierService";

    private static MessierService instance;
    private GetDataService service;

    private MessierService() {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
    }

    public static MessierService getInstance() {
        if (instance == null)
            return instance = new MessierService();
        return instance;
    }

    public void loginAuth(String username, String password, final IService iService) {

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<String> call = service.authMessier(params);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful())
                    iService.onSuccessResponse(response.body());
                else
                    iService.onFailedResponse(iService.errorHandler(response));
                Log.d(TAG, "onResponse: " + response.code() + "#" + username + "#" + password);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                iService.onFailedResponse(t.getMessage());
            }
        });
    }

    public Single<List<Training>> getTrainingSchedule() {
        Assistant auth = Assistant.getInstance();
        Single<List<Training>> trainings = service.getAllTrainings(auth.getInitial());
        return trainings;
    }

    public Observable<List<Tasks>> getTaskSchedule(HashMap<String, String> paramsJob ) {
        Assistant auth = Assistant.getInstance();
        final String token = "Bearer " + auth.getToken();
        Log.d(TAG, "getTaskSchedule: " + token);

        List<Tasks> tasksList = new ArrayList<>();

        Observable<List<Teaching>> teachingList = service.getAllTeachings(token);
        Observable<List<Jobs>> jobList = service.getAllJobs(token, paramsJob);

        Log.d(TAG, "getTaskSchedule: (paramsJob) "  + paramsJob.toString());

        return Observable.zip(teachingList, jobList, (teachings, jobs) -> {
            Log.d(TAG, "getTaskSchedule: (jobs) " + jobs.size());
            Log.d(TAG, "getTaskSchedule: (teaching) " + teachings.size());
            tasksList.addAll(teachings);
            tasksList.addAll(jobs);
            return tasksList;
        });
    }

    public Single<List<Semester>> getSemesters() {
        return service.getSemesters();
    }

    public Single<Assistant> getUserIdentify() {
        Assistant auth = Assistant.getInstance();
        final String token = "Bearer " + auth.getToken();
        return service.getUserIdentify(token);
    }
}
