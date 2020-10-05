package com.example.firstapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.R;
import com.example.firstapplication.adapter.HistoryAdapter;
import com.example.firstapplication.model.Semester;
import com.example.firstapplication.model.Tasks;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends BaseTaskFragment {

    private static final String TAG = "HistoryFragment";
    private HistoryAdapter taskAdapter;
    private RecyclerView recyclerView;

    public HistoryFragment(int jobTaskMode) {
        super(jobTaskMode);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        emptyLayout = view.findViewById(R.id.empty_layout);

        List<Tasks> tasks = new ArrayList<>();

        recyclerView = view.findViewById(R.id.rv_inbox);

        taskAdapter = new HistoryAdapter(getContext(), tasks);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

        model.setJobOptions(jobTaskMode);

        getHistoryTask(GET_TASK);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEvent(Semester semester) {
        model.setSemester(semester.getSemesterId());
        getHistoryTask(UPDATE_TASK);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        emptyLayout.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        recyclerView.setVisibility(View.GONE);

        getHistoryTask(UPDATE_TASK);
        super.onRefresh();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    private void getHistoryTask(int type) {

        Log.d(TAG, "getHistoryTask: (type) " + type);

        if (type == UPDATE_TASK)
            model.refreshTaskList();
        else if (type == GET_TASK)
            model.getTaskList().observe(this, tasksList -> {
                recyclerView.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);

                Log.d(TAG, "getHistoryTask: "  + tasksList.size());

                if (tasksList.isEmpty())
                    emptyLayout.setVisibility(View.VISIBLE);

                taskAdapter.setTask(tasksList);
                taskAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            });
    }
}
