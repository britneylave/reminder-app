package com.example.firstapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapplication.adapter.AutoSuggestAdapter;
import com.example.firstapplication.adapter.ListChatAdapter;
import com.example.firstapplication.model.Assistant;
import com.example.firstapplication.model.Chat;
import com.example.firstapplication.service.IService;
import com.example.firstapplication.service.firebase.FirebaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.firstapplication.shared.Constant.TAG_CHAT_FIREBASE;

public class ListChatActivity extends AppCompatActivity implements ValueEventListener, AdapterView.OnItemClickListener {

    private static final String TAG = "ListChatActivity";
    private ListChatAdapter chatAdapter;
    private List<Chat> chatList;
    private DatabaseReference chatReferences;
    private List<Assistant> assistants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);

        FirebaseService firebaseService = FirebaseService.getInstance();

        final Assistant auth = Assistant.getInstance();
        chatReferences = FirebaseDatabase.getInstance()
                .getReference(TAG_CHAT_FIREBASE)
                .child(auth.getInitial());

        chatReferences.addValueEventListener(this);

        final RecyclerView recyclerView = findViewById(R.id.rv_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatList = new ArrayList<>();
        chatAdapter = new ListChatAdapter(this, chatList);
        recyclerView.setAdapter(chatAdapter);

        assistants = new ArrayList<>();

        final AutoCompleteTextView txtSearch = findViewById(R.id.txt_search_chat);
        AutoSuggestAdapter suggestAdapter = new AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line, assistants);

        txtSearch.setThreshold(1);
        txtSearch.setAdapter(suggestAdapter);
        txtSearch.setOnItemClickListener(this);

        firebaseService.getAssistants(new IService() {
            @Override
            public void onSuccessResponse(Object response) {
                List<Assistant> assistantsResponse = (List<Assistant>) response;
                assistants.addAll(assistantsResponse);
                suggestAdapter.setListData(assistants);
            }

            @Override
            public void onFailedResponse(Object response) {
                Log.d(TAG, "onFailedResponse: " + response);
            }
        });
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        Log.d(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

            String key = snapshot.getKey();
            Query lastMessage = chatReferences.child(key).orderByKey().limitToLast(1);

            lastMessage.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                        Chat chat = snapshot1.getValue(Chat.class);
                        chat.getUser().setInitial(key);
                        chatList.add(chat);
                        Log.d(TAG, "onDataChange: List Message " + chat.getMessage()+"#"+  key);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });
        }
        chatAdapter.setChats(chatList);
        Log.d(TAG, "onDataChange: " + chatList.size() + "#" );
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Assistant sendTo = assistants.get(i);
        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("sendTo", sendTo);
        startActivity(chatIntent);
    }
}