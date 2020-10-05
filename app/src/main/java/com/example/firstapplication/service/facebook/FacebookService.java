package com.example.firstapplication.service.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.firstapplication.MainActivity;
import com.example.firstapplication.model.Assistant;
import com.example.firstapplication.presenter.AuthenticationPresenter;
import com.example.firstapplication.service.IService;
import com.example.firstapplication.service.firebase.FirebaseService;
import com.example.firstapplication.service.messier.MessierService;
import com.example.firstapplication.shared.SharedPreferencesManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;

import java.util.Arrays;
import java.util.Date;

import static com.example.firstapplication.shared.Constant.SP_AUTH_INITIAL;
import static com.example.firstapplication.shared.Constant.SP_AUTH_TOKEN;
import static com.example.firstapplication.shared.Constant.SP_AUTH_TOKEN_LIFETIME;

public class FacebookService {
    private Context context;
    private CallbackManager callbackManager;
    private AuthenticationPresenter.View view;

    public FacebookService(Context context) {
        this.context = context;
        view = (AuthenticationPresenter.View) context;

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loadUserProfile(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                view.failAuthentication("Failed Login");
                view.showLoading(false);
            }

            @Override
            public void onError(FacebookException error) {
                view.failAuthentication("Error Login");
                view.showLoading(false);
            }
        });
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void logInWithReadPermissions() {
        LoginManager.getInstance().logInWithReadPermissions((Activity) context, Arrays.asList("public_profile", "email"));
    }

    private void loadUserProfile(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            try {
                final String email = object.getString("email");
                FirebaseService.getInstance()
                        .isSocialMediaAuthRegistered(email, new IService() {
                            @Override
                            public void onSuccessResponse(Object response) {
                                Assistant assistant = (Assistant) response;

                                if(assistant.getPassword() != null)
                                    MessierService.getInstance()
                                            .loginAuth(assistant.getInitial(), assistant.getPlainPassword(), new IService() {
                                                @Override
                                                public void onSuccessResponse(Object response) {

                                                    SharedPreferencesManager sharedPreferencesManager =
                                                            SharedPreferencesManager.getInstance(context);

                                                    Assistant astMessierAuth = Assistant.getInstance();
                                                    astMessierAuth.setToken((String) response);
                                                    astMessierAuth.setInitial(assistant.getInitial());

                                                    sharedPreferencesManager.saveString(SP_AUTH_TOKEN, (String) response);
                                                    sharedPreferencesManager.saveString(SP_AUTH_INITIAL, assistant.getInitial());
                                                    sharedPreferencesManager.saveLong(SP_AUTH_TOKEN_LIFETIME, new Date().getTime());

                                                    Intent intent = new Intent(context, MainActivity.class);
                                                    context.startActivity(intent);
                                                }

                                                @Override
                                                public void onFailedResponse(Object response) {
                                                    view.failAuthentication((String) response);
                                                }
                                            });
                            }

                            @Override
                            public void onFailedResponse(Object response) {
                                Bundle args = new Bundle();
                                args.putString("email", email);

                                Intent intent = new Intent(context, MainActivity.class);

                                intent.putExtras(args);
                                context.getApplicationContext().startActivity(intent);

                            }
                        });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        Bundle parameter = new Bundle();
        parameter.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameter);
        request.executeAsync();
    }
}
