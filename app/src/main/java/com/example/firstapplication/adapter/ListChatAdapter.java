package com.example.firstapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.ChatActivity;
import com.example.firstapplication.R;
import com.example.firstapplication.model.Chat;
import com.example.firstapplication.shared.Utilities;

import java.util.List;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ViewHolder> {
    private Context context;
    private List<Chat> chats;

    public ListChatAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_list_chat, parent, false);
        return new ListChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.chatBody.setText(chat.getMessage());
        holder.chatTime.setText(Utilities.differentTime(chat.getCreatedAt()));
        holder.chatName.setText(chat.getUser().getInitial().toLowerCase());

        holder.cardView.setOnClickListener(view -> {
            Intent chatIntent = new Intent(context.getApplicationContext(), ChatActivity.class);
            chatIntent.putExtra("sendTo", chat.getUser());
            context.getApplicationContext().startActivity(chatIntent);
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView chatName, chatTime, chatBody;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_list_chat);
            chatName = itemView.findViewById(R.id.txt_list_chat_name);
            chatTime = itemView.findViewById(R.id.txt_list_chat_time);
            chatBody = itemView.findViewById(R.id.txt_list_chat_body);
        }
    }
}
