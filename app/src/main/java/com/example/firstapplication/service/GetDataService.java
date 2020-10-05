package com.example.firstapplication.service;

import com.example.firstapplication.model.Assistant;
import com.example.firstapplication.model.Jobs;
import com.example.firstapplication.model.Semester;
import com.example.firstapplication.model.Teaching;
import com.example.firstapplication.model.Training;
import com.example.firstapplication.model.notification.Sender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

import static com.example.firstapplication.service.URLs.AUTH_MESSIER;
import static com.example.firstapplication.service.URLs.CLOUD_MESSAGE_API;
import static com.example.firstapplication.service.URLs.JOBS_API;
import static com.example.firstapplication.service.URLs.SEMESTER_API;
import static com.example.firstapplication.service.URLs.TEACHING_API;
import static com.example.firstapplication.service.URLs.TRAINING_API;
import static com.example.firstapplication.service.URLs.USER_IDENTIFY_MESSIER;

public interface GetDataService {

    @GET(TEACHING_API)
    Observable<List<Teaching>> getAllTeachings(@Header("Authorization") String token);

    @GET(JOBS_API)
    Observable<List<Jobs>> getAllJobs(@Header("Authorization") String token, @QueryMap HashMap<String, String> params);

    @GET(TRAINING_API + "{initial}")
    Single<List<Training>> getAllTrainings(@Path(value = "initial") String initial);

    @GET(SEMESTER_API )
    Single<List<Semester>> getSemesters();

    @GET(USER_IDENTIFY_MESSIER )
    Single<Assistant> getUserIdentify(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST(AUTH_MESSIER)
    Call<String> authMessier(@FieldMap Map<String, String> params);

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAFeWTGVE:APA91bHwunzfc_R7XxvOZB9Oc2tbZbJUaBtV8cwD-NpyuA-o4VGXAkN0r_tieQfIenuIstEiuIPA1gr-avcEY2zlcSkHBO0La3OdZQkXv4b95GhFnU__SeXr5iZ_kH0dqnNzzoo13khI"
    })

    @POST(CLOUD_MESSAGE_API)
    Call<String> sendNotification(@Body Sender body);
}
