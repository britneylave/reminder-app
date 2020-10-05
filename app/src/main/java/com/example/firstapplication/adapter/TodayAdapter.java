package com.example.firstapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.example.firstapplication.R;
import com.example.firstapplication.model.Jobs;
import com.example.firstapplication.model.Tasks;
import com.example.firstapplication.model.Teaching;

import java.util.List;

public class TodayAdapter extends Adapter<TodayAdapter.ViewHolder>  {

    private Context context;
    private List<Tasks> tasks;

    public TodayAdapter(Context context, List<Tasks> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    public void addTask(List<Tasks> tasksList) {
        tasks.addAll(tasksList);
    }

    @NonNull
    @Override
    public TodayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_today, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayAdapter.ViewHolder holder, int position) {
        Tasks task = tasks.get(position);
        String title;

        if (task instanceof Teaching) {
            Teaching teaching = (Teaching) task;
            title = String.format("%s (%s)", teaching.getSubject(), teaching.getRoom());
            String day = String.format("%s", teaching.getDay());
            holder.txtTitle.setText(title);
            holder.txtClass.setText(teaching.get_class());
            holder.txtDay.setText(day);
            holder.txtRealization.setText("Section " + teaching.getRealization());
            holder.txtShift.setText(teaching.getShift());
        }
        else if(task instanceof Jobs){
            Jobs jobs = (Jobs) task;
            holder.txtTitle.setText(jobs.getJobType());
            holder.txtClass.setText(jobs.getDescription());
        }


        holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtClass, txtDay, txtRealization, txtShift;
        LinearLayout container;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title_inbox);
            txtClass = itemView.findViewById(R.id.txt_class);
            txtDay = itemView.findViewById(R.id.txt_day);
            txtRealization = itemView.findViewById(R.id.txt_realization);
            txtShift = itemView.findViewById(R.id.txt_shift);
            container = itemView.findViewById(R.id.layout_inbox_container);

            txtTitle.setText("");
            txtClass.setText("");
            txtDay.setText("");
            txtRealization.setText("");
            txtShift.setText("");
        }
    }
}
