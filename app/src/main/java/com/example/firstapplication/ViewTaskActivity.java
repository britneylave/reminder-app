package com.example.firstapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.adapter.CustomTaskAdapter;
import com.example.firstapplication.model.CustomTask;
import com.example.firstapplication.shared.SharedPreferencesManager;
import com.example.firstapplication.shared.comparator.TaskDateComparator;
import com.example.firstapplication.shared.comparator.TaskStatusComparator;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.firstapplication.shared.Constant.SP_AUTH_INITIAL;

public class ViewTaskActivity extends AppCompatActivity implements ValueEventListener, View.OnClickListener {

    private static final String TAG = "ViewTaskActivity";
    private CustomTaskAdapter taskAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView taskData;

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmerAnimation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(this);

        taskData = findViewById(R.id.rv_tasks);
        shimmerFrameLayout = findViewById(R.id.shimmer_task);

        shimmerFrameLayout.startShimmerAnimation();
        Query query = FirebaseDatabase.getInstance().getReference().child("tasks").orderByChild("initial")
                .equalTo(sharedPreferencesManager.getString(SP_AUTH_INITIAL));

        query.addValueEventListener(this);

        FloatingActionButton fab = findViewById(R.id.add_task_button);
        fab.setOnClickListener(this);

        taskAdapter = new CustomTaskAdapter(this);
        taskData.setAdapter(taskAdapter);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        shimmerFrameLayout.setVisibility(View.GONE);

        List<CustomTask> taskList = new ArrayList<>();

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            CustomTask task = snapshot.getValue(CustomTask.class);
            task.setKey(snapshot.getKey());
            taskList.add(task);
        }

        if (taskList.isEmpty()) {
            findViewById(R.id.empty_job_layout).setVisibility(View.VISIBLE);
        } else {
            Collections.sort(taskList, new TaskDateComparator());
            Collections.sort(taskList, new TaskStatusComparator());
            findViewById(R.id.empty_job_layout).setVisibility(View.GONE);
        }

        taskAdapter.setTasks(taskList);
        taskAdapter.notifyDataSetChanged();
        taskData.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_task_button) {
            Bundle bundle = new Bundle();
            bundle.putString("mode","add");
            Intent intent = new Intent(this, InsertTaskActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
