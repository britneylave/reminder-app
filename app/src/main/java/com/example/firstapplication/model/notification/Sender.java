package com.example.firstapplication.model.notification;

import com.example.firstapplication.model.Chat;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class Sender {
    private Chat chat;
    private String to;

    public Sender(Chat chat, String to) {
        this.chat = chat;
        this.to = "/topics/" + to;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
