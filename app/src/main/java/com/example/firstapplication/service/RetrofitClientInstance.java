package com.example.firstapplication.service;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.firstapplication.service.URLs.BASE_MESSIER_API;
import static com.example.firstapplication.shared.Constant.FEEDBACK_CONNECT_TIMEOUT;
import static com.example.firstapplication.shared.Constant.FEEDBACK_READ_TIMEOUT;

public abstract class RetrofitClientInstance {

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(FEEDBACK_READ_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(FEEDBACK_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_MESSIER_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

}
