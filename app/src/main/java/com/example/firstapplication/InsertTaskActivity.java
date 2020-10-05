package com.example.firstapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firstapplication.model.CustomTask;
import com.example.firstapplication.shared.SharedPreferencesManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.firstapplication.shared.Constant.SP_AUTH_INITIAL;

public class InsertTaskActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    EditText txtDescription, txtLocation;
    TextView txtTime, txtDate;
    TextView txtError;
    ToggleButton btnNotify;

    Calendar calendar;
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;

    DatabaseReference databaseTask;

    CustomTask task;
    int hour, minute;
    SimpleDateFormat sdf, stf;

    boolean notify = false;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_task);

        sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        stf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        calendar = Calendar.getInstance();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);

        txtDescription = findViewById(R.id.add_task_name);
        txtDate = findViewById(R.id.add_task_date);
        txtTime = findViewById(R.id.add_task_time);
        txtLocation = findViewById(R.id.add_task_location);
        txtError = findViewById(R.id.add_task_error);
        btnNotify = findViewById(R.id.add_task_notify_button);

        if(getIntent().getExtras().getString("mode").equals("update")){
            Button btnAdd = findViewById(R.id.add_task_button);
            Button btnStatus = findViewById(R.id.add_task_notify_button);
            btnAdd.setText("Update Task");
            txtDescription.setText(getIntent().getExtras().getString("name"));
            txtDate.setText(getIntent().getExtras().getString("date"));
            txtTime.setText(getIntent().getExtras().getString("time"));
            txtLocation.setText(getIntent().getExtras().getString("location"));
            if(getIntent().getExtras().getString("notify").equals("false"))
                btnNotify.setChecked(false);
            else btnNotify.setChecked(true);
        }

        final Button btnAddTask = findViewById(R.id.add_task_button);

        databaseTask = FirebaseDatabase.getInstance().getReference().child("tasks");

        date = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            txtDate.setText(sdf.format(calendar.getTime()));
        };

        time = (timePicker, hour, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            this.hour = hour;
            this.minute = minute;
            txtTime.setText(stf.format(calendar.getTime()));
        };

        Date now = new Date();
        txtDate.setText(sdf.format(now));
        txtTime.setText(stf.format(now));

        btnAddTask.setOnClickListener(this);
        txtDate.setOnClickListener(this);
        txtTime.setOnClickListener(this);
        btnNotify.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_task_date:
                new DatePickerDialog(InsertTaskActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.add_task_time:
                new TimePickerDialog(InsertTaskActivity.this, time, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), false).show();
                break;
            case R.id.add_task_button:
//                Log.d("result_date", "onClick: "+getIntent().getExtras().getString("mode"));
                if (txtDescription.getText().toString().isEmpty() ||
                        txtDate.getText().toString().isEmpty() ||
                        txtTime.getText().toString().isEmpty() ||
                        txtTime.getText().toString().isEmpty() ||
                        txtLocation.getText().toString().isEmpty()) {
                    txtError.setText("Please Fulfill the form");
                    return;
                }
                task = new CustomTask(
                        sharedPreferencesManager.getString(SP_AUTH_INITIAL),
                        txtDescription.getText().toString().trim(),
                        txtLocation.getText().toString().trim(),
                        sdf.format(calendar.getTime()),
                        stf.format(calendar.getTime()),
                        notify,
                        true
                );
                if(getIntent().getExtras().getString("mode").equals("update")){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tasks");
                    databaseReference.child(getIntent().getExtras().getString("key")).setValue(task);
                    Toast.makeText(this, "Successfully updated the task!",
                            Toast.LENGTH_SHORT).show();
                    super.onBackPressed();
                }else{
                    String taskId = databaseTask.push().getKey();
                    assert taskId != null;
                    databaseTask.child(taskId).setValue(task);
                    Toast.makeText(this, "Successfully added a new task!",
                            Toast.LENGTH_SHORT).show();
                    super.onBackPressed();
                }

//                task = new CustomTask(
//                        sharedPreferencesManager.getString(SP_AUTH_INITIAL),
//                        txtDescription.getText().toString().trim(),
//                        txtLocation.getText().toString().trim(),
//                        sdf.format(calendar.getTime()),
//                        stf.format(calendar.getTime()),
//                        notify,
//                        true
//                );
//                String taskId = databaseTask.push().getKey();
//                assert taskId != null;
//                databaseTask.child(taskId).setValue(task);
//
//                Toast.makeText(this, "Successfully added a new task!",
//                        Toast.LENGTH_SHORT).show();
//                super.onBackPressed();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        switch (button.getId()) {
            case R.id.add_task_notify_button:
                this.notify = isChecked;
                break;
        }
    }
}

