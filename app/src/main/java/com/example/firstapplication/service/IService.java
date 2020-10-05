package com.example.firstapplication.service;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Response;

public interface IService {
    void onSuccessResponse(Object response);

    void onFailedResponse(Object response);

    default String errorHandler(Response response) {
        try {
            JSONObject jsonObject = new JSONObject(response.errorBody().string());
            return jsonObject.getString("Message");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
