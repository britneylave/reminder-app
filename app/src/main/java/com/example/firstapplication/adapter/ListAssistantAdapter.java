package com.example.firstapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.R;
import com.example.firstapplication.model.Assistant;

import java.util.ArrayList;
import java.util.List;

public class ListAssistantAdapter extends RecyclerView.Adapter<ListAssistantAdapter.ViewHolder> {
    private static final String TAG = "ListAssistantAdapter";
    private List<Assistant> data;
    private Context context;

    public ListAssistantAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Assistant> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ListAssistantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_assistant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAssistantAdapter.ViewHolder holder, int position) {
        Assistant assistant = data.get(position);

        holder.txtEmail.setText(assistant.getEmail());
        holder.txtName.setText(assistant.getInitial());
        holder.checkBox.setOnClickListener(view -> assistant.setShared(true));
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtEmail;
        CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_list_assistant_name);
            txtEmail = itemView.findViewById(R.id.txt_list_assistant_email);
            checkBox = itemView.findViewById(R.id.checkbox_assistant);
        }
    }
}
