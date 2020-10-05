package com.example.firstapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.adapter.ChatAdapter;
import com.example.firstapplication.model.Assistant;
import com.example.firstapplication.model.Chat;
import com.example.firstapplication.service.IService;
import com.example.firstapplication.service.firebase.FirebaseService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private EditText txtChat;
    private Assistant sendTo;
    private ChatAdapter chatAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendTo = getIntent().getParcelableExtra("sendTo");

        recyclerView = findViewById(R.id.rv_message_list);

        List<Chat> chats = new ArrayList<>();

        chatAdapter = new ChatAdapter(this, chats);

        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final Button btnChatSend = findViewById(R.id.btn_chat_send);
        btnChatSend.setOnClickListener(this);

        txtChat = findViewById(R.id.txt_chat_box);
        txtChat.setOnKeyListener(this);

        txtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    btnChatSend.setEnabled(true);
                } else {
                    btnChatSend.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        FirebaseService firebaseService = FirebaseService.getInstance();

        firebaseService.listenMessage(sendTo, new IService() {
            @Override
            public void onSuccessResponse(Object response) {
                Chat newChat = (Chat) response;
                chats.add(newChat);
                chatAdapter.notifyDataSetChanged();

                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                Log.d("Chat Listener", String.valueOf(chats.size()));
            }

            @Override
            public void onFailedResponse(Object response) {

            }
        });

//        firebaseService.readAllMessages(sendTo);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_chat_send) {
            sendMessages();
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            // Perform action on key press
            sendMessages();
            return true;
        }
        return false;
    }

    private void sendMessages() {
        Chat newChat = new Chat();
        newChat.setCreatedAt(new Date().getTime());
        newChat.setMessage(txtChat.getText().toString());
        newChat.setUser(sendTo);
        newChat.setRead(false);

        txtChat.setText("");
        FirebaseService firebaseService = FirebaseService.getInstance();
        firebaseService.sendMessage(newChat, sendTo);
        firebaseService.sendNotification(newChat, sendTo.getInitial());
        firebaseService.readMessage(newChat,sendTo);
    }
}