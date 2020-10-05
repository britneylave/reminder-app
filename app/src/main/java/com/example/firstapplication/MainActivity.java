package com.example.firstapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.firstapplication.component.MessierAccountDialog;
import com.example.firstapplication.fragment.SocialMediaAccountFragment;
import com.example.firstapplication.model.Assistant;
import com.example.firstapplication.model.Semester;
import com.example.firstapplication.service.messier.MessierService;
import com.example.firstapplication.shared.SharedPreferencesManager;
import com.example.firstapplication.shared.Utilities;
import com.example.firstapplication.ui.main.SectionsPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MaterialSpinner.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView txtUsername, txtInitial;
    private MaterialSpinner spinner;

    private MessierService messierService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messierService = MessierService.getInstance();

        String emailSocialMedia = null;
        if (getIntent().getExtras() != null)
            emailSocialMedia = getIntent().getExtras().getString("email");

        /*
          if tidak kosong berarti tampilkan Messier Form
         */
        if (emailSocialMedia != null)
            showMessierDialog(emailSocialMedia);

        if (!Utilities.isInBinusAddress()) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.nav_drawer), "Connect Wifi Binus to Retrieve List Training", Snackbar.LENGTH_LONG);

            snackbar.show();
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.nav_drawer);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_32dp);
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        getSemesters();

        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false);
        navigationView.addHeaderView(headerView);

        txtInitial = headerView.findViewById(R.id.txt_nav_initial);
        txtUsername = headerView.findViewById(R.id.txt_nav_username);

        getUserIdentify();
    }

    private void getUserIdentify() {
        messierService.getUserIdentify()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Assistant>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Assistant assistant) {
                        Assistant auth = Assistant.getInstance();
                        auth.setName(assistant.getName());
                        txtInitial.setText(auth.getInitial());
                        txtUsername.setText(assistant.getName());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    private void getSemesters() {
        messierService
                .getSemesters()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Semester>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Semester> value) {
                        spinner.setItems(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "Throw : " + e.getMessage());
                    }
                });
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        drawerToggle.syncState();
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.log_out_menu:
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                SharedPreferencesManager.getInstance(this).deleteAll();
                return true;
            case R.id.view_schedule_menu:
                intent1 = new Intent(this, ViewScheduleActivity.class);
                startActivity(intent1);
                break;
            case R.id.view_task_menu:
                Intent intent = new Intent(this, ViewTaskActivity.class);
                startActivity(intent);
                break;
            case R.id.google_account_menu:
                showGoogleDialog();
                break;
            case R.id.send_message_menu:
                intent = new Intent(this, ListChatActivity.class);
                startActivity(intent);
                break;
            case R.id.invitation_menu:
                intent = new Intent(this, InvitationActivity.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(Gravity.LEFT);
        return true;
    }

    private void showMessierDialog(String email) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        MessierAccountDialog messierAccountDialog = new MessierAccountDialog();
        Bundle mBundle = new Bundle();
        mBundle.putString("email", email);
        messierAccountDialog.setArguments(mBundle);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, messierAccountDialog).addToBackStack(null).commit();
    }

    private void showGoogleDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SocialMediaAccountFragment socialMediaAccountFragment = new SocialMediaAccountFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, socialMediaAccountFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        drawerLayout = findViewById(R.id.nav_drawer);
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        Semester semester = (Semester) item;
        EventBus.getDefault().post(semester);
    }

}