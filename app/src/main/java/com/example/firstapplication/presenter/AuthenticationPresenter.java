package com.example.firstapplication.presenter;

import android.content.Context;
import android.content.Intent;

import com.example.firstapplication.MainActivity;
import com.example.firstapplication.model.Assistant;
import com.example.firstapplication.service.IService;
import com.example.firstapplication.service.firebase.FirebaseService;
import com.example.firstapplication.service.messier.MessierService;
import com.example.firstapplication.shared.SharedPreferencesManager;
import com.example.firstapplication.shared.Utilities;

import static com.example.firstapplication.shared.Constant.SP_AUTH_INITIAL;
import static com.example.firstapplication.shared.Constant.SP_AUTH_TOKEN;
import static com.example.firstapplication.shared.Constant.SP_AUTH_TOKEN_LIFETIME;

public class AuthenticationPresenter {

    private View view;
    private Context context;
    private SharedPreferencesManager sharedPreferencesManager;

    public AuthenticationPresenter(Context context) {
        this.context = context;
        view = (View) context;
        sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
    }

    public void isLoggedIn() {
        final String token = sharedPreferencesManager.getString(SP_AUTH_TOKEN);
        final String initial = sharedPreferencesManager.getString(SP_AUTH_INITIAL);
        final long tokenLifetime = sharedPreferencesManager.getLong(SP_AUTH_TOKEN_LIFETIME);

        if (token == null || token.isEmpty())
            return;

        boolean isTokenExpired = Utilities.isTokenExpired(tokenLifetime);

        if (isTokenExpired) {
            sharedPreferencesManager.deleteAll();
            return;
        }

        /*
          store token from shared preferences to model
         */
        Assistant.getInstance().setToken(token);
        Assistant.getInstance().setInitial(initial);
        redirectIfAuthenticate();
    }

    private void redirectIfAuthenticate() {
        Intent homeIntent = new Intent(context, MainActivity.class);
//        Intent homeIntent = new Intent(context, ListChatActivity.class);
        context.getApplicationContext().startActivity(homeIntent);
    }

    public void findByInitial(String username, String password) {
        MessierService service = MessierService.getInstance();

        service.loginAuth(username, password, new IService() {

            @Override
            public void onSuccessResponse(Object response) {
                final String token = (String) response;
                Assistant.saveIdentityToLocal(token, username,context);

                FirebaseService.getInstance().savePassword(username, password);
                redirectIfAuthenticate();
                view.showLoading(false);
            }

            @Override
            public void onFailedResponse(Object response) {
                view.failAuthentication((String) response);
                view.showLoading(false);
            }
        });
    }

    public void onDestroy() {
        view = null;
    }

    public interface View {
        void failAuthentication(String error);

        void showLoading(boolean show);
    }
}
