package com.example.firstapplication.component;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.R;
import com.example.firstapplication.adapter.ListAssistantAdapter;
import com.example.firstapplication.model.Assistant;
import com.example.firstapplication.model.CustomTask;
import com.example.firstapplication.service.IService;
import com.example.firstapplication.service.firebase.FirebaseService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ListAssistantDialog extends DialogFragment {

    private static final String TAG = "ListAssistantDialog";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_list_assistant, container, false);

        RecyclerView rvListAssistant = v.findViewById(R.id.rv_list_assistant_dialog);

        rvListAssistant.setHasFixedSize(true);
        rvListAssistant.setLayoutManager(new LinearLayoutManager(getActivity()));

        ListAssistantAdapter listAssistantAdapter =
                new ListAssistantAdapter(getContext());

        rvListAssistant.setAdapter(listAssistantAdapter);

        FirebaseService service = FirebaseService.getInstance();

        final ArrayList<Assistant> assistantList = new ArrayList<>();

        service.getAssistants(new IService() {
            @Override
            public void onSuccessResponse(Object response) {
                assistantList.clear();
                assistantList.addAll((List<Assistant>) response);

                listAssistantAdapter.setData(assistantList);
                listAssistantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailedResponse(Object response) {
            }
        });
        Button btnShared = v.findViewById(R.id.add_messier_btn);

        CustomTask task = getArguments().getParcelable("custom_task");

        btnShared.setOnClickListener(view -> {
            List<Assistant> assistantsClone = (List<Assistant>) assistantList.clone();
            Iterator<Assistant> itr = assistantsClone.iterator();

            while (itr.hasNext()) {
                Assistant assistant = itr.next();
                if (!assistant.isShared()) itr.remove();
            }

            dismiss();
            service.sharedInvitationTask(assistantsClone,task);
            Toast.makeText(getContext(), "Success Shared with " + assistantsClone.size() + " Assistants", Toast.LENGTH_SHORT).show();
        });

        return v;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }
}
