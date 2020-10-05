package com.example.firstapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.firstapplication.R;
import com.example.firstapplication.ui.model.InboxViewModel;
import com.facebook.shimmer.ShimmerFrameLayout;

public abstract class BaseTaskFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected SwipeRefreshLayout swipeLayout;
    protected ShimmerFrameLayout shimmerFrameLayout;
    protected LinearLayout emptyLayout;
    protected InboxViewModel model;
    protected int jobTaskMode;
    protected final int GET_TASK = 1;
    protected final int UPDATE_TASK = 2;


    BaseTaskFragment(int jobTaskMode) {
        this.jobTaskMode = jobTaskMode;
    }

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        swipeLayout = view.findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this);


        model = ViewModelProviders.of(this).get(InboxViewModel.class);

        return view;
    }

    @Override
    public void onRefresh() {

    }
}
