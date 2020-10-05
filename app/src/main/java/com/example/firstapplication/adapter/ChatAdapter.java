package com.example.firstapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.R;
import com.example.firstapplication.model.Chat;
import com.example.firstapplication.shared.SharedPreferencesManager;
import com.example.firstapplication.shared.Utilities;

import java.util.List;

import static com.example.firstapplication.shared.Constant.SP_AUTH_INITIAL;

public class ChatAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final String TAG =  "ChatAdapter";

    private Context context;
    private List<Chat> chats;

    public ChatAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat message = chats.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Chat message = chats.get(position);
        String initialAuth = SharedPreferencesManager.getInstance(context).getString(SP_AUTH_INITIAL);

        if (message.getUser().getInitial().equals(initialAuth)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView readIndicator;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            readIndicator = itemView.findViewById(R.id.read_indicator);
        }

        void bind(Chat message) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            String createdAt = Utilities.formatDateTime(message.getCreatedAt());
            String diff = Utilities.differentTime(message.getCreatedAt());
            timeText.setText(createdAt + " " + diff);
            if(message.isRead())
                readIndicator.setImageResource(R.drawable.ic_chat_read_24dp);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
        }

        void bind(Chat message) {
            messageText.setText(message.getMessage());

            String createdAt = Utilities.formatDateTime(message.getCreatedAt());
            String diff = Utilities.differentTime(message.getCreatedAt());
            timeText.setText(createdAt + " " + diff);

            nameText.setText(message.getUser().getInitial());
        }
    }
}