package com.example.firstapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firstapplication.presenter.AuthenticationPresenter;
import com.example.firstapplication.seeder.assistant.AssistantSeeder;
import com.example.firstapplication.service.facebook.FacebookService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, AuthenticationPresenter.View {

    private EditText txtInitial, txtPassword;
    private TextView errorView;

    private AuthenticationPresenter authenticationPresenter;
    private FacebookService facebookService;
    private LinearLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        facebookService = new FacebookService(this);

        authenticationPresenter = new AuthenticationPresenter(this);

        loadingLayout = findViewById(R.id.layout_loading);
        txtInitial = findViewById(R.id.txt_initial);
        txtPassword = findViewById(R.id.txt_password);
        errorView = findViewById(R.id.txt_error);

        final Button btnLoginMessier = findViewById(R.id.btn_login_messier);
        final Button btnLoginFacebook = findViewById(R.id.btn_login_facebook);

        btnLoginMessier.setOnClickListener(this);
        btnLoginFacebook.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        facebookService.getCallbackManager().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        showLoading(true);

        switch (view.getId()) {
            case R.id.btn_login_messier:
                String username = txtInitial.getText().toString().toLowerCase();
                String password = txtPassword.getText().toString();
                authenticationPresenter.findByInitial(username, password);
                break;
            case R.id.btn_login_facebook:
                facebookService.logInWithReadPermissions();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        authenticationPresenter.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        authenticationPresenter.isLoggedIn();
    }

    @Override
    public void failAuthentication(String error) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setText(error);

        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void showLoading(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        loadingLayout.setVisibility(visibility);
    }

}