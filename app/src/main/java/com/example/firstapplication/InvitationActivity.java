package com.example.firstapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;

import com.example.firstapplication.adapter.InvitationAdapter;
import com.example.firstapplication.model.CustomTask;
import com.example.firstapplication.service.IService;
import com.example.firstapplication.service.firebase.FirebaseService;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class InvitationActivity extends AppCompatActivity {

    private static final String TAG = "InvitationActivity";
    ShimmerFrameLayout shimmerFrameLayout;

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
        setContentView(R.layout.activity_invitation);

        InvitationAdapter taskAdapter = new InvitationAdapter(this);

        RecyclerView recyclerView = findViewById(R.id.rv_invitation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(taskAdapter);

        shimmerFrameLayout = findViewById(R.id.shimmer_invitation);
        shimmerFrameLayout.startShimmerAnimation();

        final LinearLayout emptyLayout = findViewById(R.id.empty_job_layout);

        FirebaseService service = FirebaseService.getInstance();
        service.getInvitations(new IService() {
            @Override
            public void onSuccessResponse(Object response) {
                List<Pair<String , CustomTask>> customTaskList = (List<Pair<String, CustomTask>>) response;

                Log.d(TAG, "onSuccessResponse: " +  customTaskList.size());

                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);

                if(customTaskList.isEmpty())
                    emptyLayout.setVisibility(View.VISIBLE);

                taskAdapter.setTask(customTaskList);
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailedResponse(Object response) {
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
                emptyLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}
