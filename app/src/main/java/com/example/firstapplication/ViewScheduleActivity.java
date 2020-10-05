package com.example.firstapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.adapter.InboxAdapter;
import com.example.firstapplication.model.Tasks;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.example.firstapplication.ui.main.InboxViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.firstapplication.shared.Constant.JOB_MODE_CURRENT;

public class ViewScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        final ShimmerFrameLayout shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmerAnimation();

        final LinearLayout emptyLayout = findViewById(R.id.empty_job_layout);

        List<Tasks> tasks = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView scheduleData = findViewById(R.id.schedule_data_view);
        final InboxAdapter taskAdapter = new InboxAdapter(this, tasks);

        scheduleData.setAdapter(taskAdapter);
        scheduleData.setLayoutManager(new LinearLayoutManager(this));

        InboxViewModel model = ViewModelProviders.of(this).get(InboxViewModel.class);

        model.setJobOptions(JOB_MODE_CURRENT);

        model.getTaskList().observe(this, tasksList -> {
            shimmerFrameLayout.stopShimmerAnimation();
            shimmerFrameLayout.setVisibility(View.GONE);

            if (tasksList.isEmpty())
                emptyLayout.setVisibility(View.VISIBLE);

            taskAdapter.addTask(tasksList);
            taskAdapter.notifyDataSetChanged();
        });
    }

}
