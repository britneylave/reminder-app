package com.example.firstapplication.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.R;
import com.example.firstapplication.model.CustomTask;
import com.example.firstapplication.service.firebase.FirebaseService;

import java.util.List;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {
    private Context context;
    private List<Pair<String, CustomTask>> data;

    private FirebaseService service;
    private static final int ACCEPT = 1;
    private static final int REJECT = 2;

    public InvitationAdapter(Context context) {
        this.context = context;
        service = FirebaseService.getInstance();
    }

    public void setTask(List<Pair<String, CustomTask>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_invitation, parent, false);
        return new InvitationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomTask task = data.get(position).second;
        String key = data.get(position).first;

        holder.txtTitle.setText(task.getTitleName());
        holder.txtLocation.setText(task.getLocation());
        holder.txtDate.setText(task.getDate());
        holder.txtTime.setText(task.getTime());

        holder.btnAcc.setOnClickListener(view -> approvalDialog(task, key, ACCEPT));
        holder.btnReject.setOnClickListener(view -> approvalDialog(task, key, REJECT));
    }


    private void approvalDialog(CustomTask task, String key, int status) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    if (status == ACCEPT) {
                        service.rejectInvitation(task, key);
                        service.acceptInvitation(task);
                    } else if (status == REJECT)
                        service.rejectInvitation(task, key);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtLocation, txtDate, txtTime;
        ImageButton btnAcc, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title_task);
            txtLocation = itemView.findViewById(R.id.txt_location);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtTime = itemView.findViewById(R.id.txt_time);

            btnAcc = itemView.findViewById(R.id.btn_invite_acc);
            btnReject = itemView.findViewById(R.id.btn_invite_reject);
        }
    }
}
