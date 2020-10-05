package com.example.firstapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.R;
import com.example.firstapplication.adapter.InboxAdapter;
import com.example.firstapplication.model.Semester;
import com.example.firstapplication.model.Tasks;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class FutureFragment extends BaseTaskFragment {

    private static final String TAG = "FutureFragment";
    private InboxAdapter taskAdapter;
    private RecyclerView recyclerView;

    public FutureFragment(int jobTaskMode) {
        super(jobTaskMode);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        emptyLayout = view.findViewById(R.id.empty_layout);

        List<Tasks> tasks = new ArrayList<>();

        recyclerView = view.findViewById(R.id.rv_inbox);

        taskAdapter = new InboxAdapter(getContext(), tasks);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskAdapter);

        model.setJobOptions(jobTaskMode);

        getInboxTask(GET_TASK);

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEvent(Semester semester) {
        model.setSemester(semester.getSemesterId());
        Log.d(TAG, "onMessageEvent: "  + semester.getSemesterId() + " called");
        getInboxTask(UPDATE_TASK);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
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
        getInboxTask(UPDATE_TASK);
        super.onRefresh();
    }

    private void getInboxTask(int type) {
        Log.d(TAG, "getInboxTask: (type) "+ type);

        if(type == UPDATE_TASK)
            model.refreshTaskList();
        else if(type == GET_TASK)
            model.getTaskList()
                    .observe(this, tasksList -> {
                        Log.d(TAG, "getInboxTask: " + tasksList.size());

                        recyclerView.setVisibility(View.VISIBLE);
                        shimmerFrameLayout.stopShimmerAnimation();
                        shimmerFrameLayout.setVisibility(View.GONE);

                        if (tasksList.isEmpty())
                            emptyLayout.setVisibility(View.VISIBLE);

                        taskAdapter.addTask(tasksList);
                        taskAdapter.notifyDataSetChanged();
                        swipeLayout.setRefreshing(false);
                    });
    }
}