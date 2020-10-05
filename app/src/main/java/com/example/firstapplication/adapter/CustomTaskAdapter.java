package com.example.firstapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.InsertTaskActivity;
import com.example.firstapplication.R;
import com.example.firstapplication.component.ListAssistantDialog;
import com.example.firstapplication.component.MessierAccountDialog;
import com.example.firstapplication.model.CustomTask;
import com.example.firstapplication.service.TaskNotificationReceiver;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CustomTaskAdapter extends RecyclerView.Adapter<CustomTaskAdapter.ViewHolder> {

    private Context context;
    private List<CustomTask> tasks;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tasks");

    public CustomTaskAdapter(Context context) {
        this.context = context;
    }

    public void setTasks(List<CustomTask> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_data_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomTask task = tasks.get(position);


        TaskNotificationReceiver taskNotificationReceiver = new TaskNotificationReceiver();

        String[] dateArray = task.getDate().split("/");
        String[] timeArray = task.getTime().split(":");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]));
        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());

        Calendar calendar1 = Calendar.getInstance();
        String[] dates = task.getDate().split("/");
        calendar1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[0]));
        calendar1.set(Calendar.MONTH, Integer.parseInt(dates[1]) - 1);
        calendar1.set(Calendar.YEAR, Integer.parseInt(dates[2]));

        long res = (calendar1.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
        long days = (res / (1000 * 60 * 60 * 24));

        if (days <= -1) {
            /*
              update task to false
             */
            task.setStatus(false);
            databaseReference.child(task.getKey()).setValue(task);
        } else if (task.isNotify() && task.isStatus()) {

            taskNotificationReceiver.setOneTimeAlarm(
                    context,
                    TaskNotificationReceiver.TYPE_ONE_TIME,
                    date,
                    time,
                    task.getName(),
                    task.getDate() + " " + task.getTime()
            );
        }

        holder.txtName.setText(task.getTitleName());
        holder.txtDate.setText("On " + task.getDate());
        holder.txtLocation.setText(task.getLocation());
        holder.txtNotify.setText(task.isNotify() ? "Notifies" : "No Notification");
        holder.txtTime.setText(task.getTime());

        if (!task.isStatus()) {
            holder.layoutContainer.setBackgroundResource(R.drawable.done_teaching_rounded);
            holder.layoutUdpate.setVisibility(View.GONE);
            holder.btnUpdate.setBackgroundColor(Color.parseColor("#407088"));

            holder.layoutInvite.setVisibility(View.GONE);
            holder.viewTask.setVisibility(View.GONE);
        }

        holder.btnUpdate.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("mode", "update");
            bundle.putString("key", task.getKey());
            bundle.putString("name", task.getName());
            bundle.putString("date", task.getDate());
            bundle.putString("time", task.getTime());
            bundle.putString("location", task.getLocation());

            bundle.putString("notify", task.isNotify() ? "true" : "false");

            Intent intent = new Intent(context, InsertTaskActivity.class);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(view -> deleteTask(task, view));

        holder.btnTaskInvite.setOnClickListener(view -> showListAssistantsDialog(task));
    }

    private void showListAssistantsDialog(CustomTask task) {
        ListAssistantDialog listAssistantDialog =
                 new ListAssistantDialog();

        Bundle mBundle = new Bundle();
        mBundle.putParcelable("custom_task",  task);
        listAssistantDialog.setArguments(mBundle);

        ((AppCompatActivity) context)
                .getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(android.R.id.content, listAssistantDialog)
                .addToBackStack(null)
                .commit();

    }

    private void deleteTask(CustomTask task, View view) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Task")
                .setMessage("Delete " + task.getName() + " from your tasks list?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    databaseReference.child(task.getKey()).removeValue();
                    Snackbar snackbar = Snackbar
                            .make(view, task.getName() + " is deleted.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtLocation, txtDate, txtTime, txtNotify;
        ImageButton btnUpdate, btnDelete, btnTaskInvite;
        LinearLayout layoutContainer, layoutAction, layoutDelete, layoutUdpate,layoutInvite;
        View viewTask;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_title_task);
            txtLocation = itemView.findViewById(R.id.txt_location);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtTime = itemView.findViewById(R.id.txt_time);
            txtNotify = itemView.findViewById(R.id.txt_notify);

            btnUpdate = itemView.findViewById(R.id.btn_task_update);
            btnDelete = itemView.findViewById(R.id.btn_task_delete);
            btnTaskInvite = itemView.findViewById(R.id.btn_task_invite);

            layoutContainer = itemView.findViewById(R.id.layout_inbox_container);
            layoutAction = itemView.findViewById(R.id.layout_inbox_action);
            layoutInvite = itemView.findViewById(R.id.layout_invite);
            layoutDelete = itemView.findViewById(R.id.layout_delete);
            layoutUdpate = itemView.findViewById(R.id.layout_update);

            viewTask = itemView.findViewById(R.id.view_task_menu);
        }
    }
}
