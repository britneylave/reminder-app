package com.example.firstapplication.service.firebase;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firstapplication.model.Assistant;
import com.example.firstapplication.model.Chat;
import com.example.firstapplication.model.CustomTask;
import com.example.firstapplication.model.notification.Sender;
import com.example.firstapplication.service.GetDataService;
import com.example.firstapplication.service.IService;
import com.example.firstapplication.service.RetrofitClientInstance;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.firstapplication.shared.Constant.TAG_ASSISTANT_FIREBASE;
import static com.example.firstapplication.shared.Constant.TAG_CHAT_FIREBASE;
import static com.example.firstapplication.shared.Constant.TAG_INVITATIONS_FIREBASE;
import static com.example.firstapplication.shared.Constant.TAG_TASK_FIREBASE;

public class FirebaseService {

    private static FirebaseService instance;

    private FirebaseService() {
    }

    public static FirebaseService getInstance() {
        if (instance == null)
            return instance = new FirebaseService();
        return instance;
    }


    public void getAssistants(final IService iService) {
        DatabaseReference astReference = FirebaseDatabase.getInstance().getReference(TAG_ASSISTANT_FIREBASE);
        List<Assistant> assistants = new ArrayList<>();

        astReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Assistant assistant = snapshot.getValue(Assistant.class);
                    Log.d("GET ASSISTANTS", "onDataChange: " + assistant.getInitial() + "#" + assistant.getEmail() + "#" + assistant.getuId());
                    assistants.add(assistant);
                }
                iService.onSuccessResponse(assistants);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase Error", "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    public void sendMessage(Chat chat, Assistant sendTo) {

        final Assistant auth = Assistant.getInstance();
        String initialAuth = auth.getInitial();
        DatabaseReference astReference = FirebaseDatabase.getInstance()
                .getReference(TAG_CHAT_FIREBASE)
                .child(initialAuth)
                .child(sendTo.getInitial());

        final String key = astReference.push().getKey();
        chat.setuId(key);
        chat.setUser(auth);
        astReference.child(key).setValue(chat);

        FirebaseDatabase.getInstance()
                .getReference(TAG_CHAT_FIREBASE)
                .child(sendTo.getInitial())
                .child(initialAuth)
                .child(key)
                .setValue(chat);
    }

    public void listenMessage(Assistant sendTo, final IService iService) {

        String initialAuth = Assistant.getInstance().getInitial();

        DatabaseReference chatReference = FirebaseDatabase.getInstance()
                .getReference(TAG_CHAT_FIREBASE)
                .child(initialAuth)
                .child(sendTo.getInitial());

        chatReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (!chat.getUser().getInitial().equals(initialAuth))
                    chatReference.setValue(true);

                iService.onSuccessResponse(chat);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readAllMessages(Assistant sendTo) {
        String initialAuth = Assistant.getInstance().getInitial();

        DatabaseReference chatReference = FirebaseDatabase.getInstance()
                .getReference(TAG_CHAT_FIREBASE)
                .child(initialAuth)
                .child(sendTo.getInitial());

        Query unreadChat = chatReference.orderByChild("read").equalTo(false);

        unreadChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    String key = snapshot.getKey();

                    if(!chat.getUser().getInitial().equals(initialAuth))
                        chatReference.child(key).child("read").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendNotification(Chat chat, String to) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

        Sender sender = new Sender(chat, to);

        Call<String> call = service.sendNotification(sender);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("Send Notification", "onResponse: : " + response.code());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("Failed Notification", "onFailure: " + t.getMessage());
            }
        });
    }

    public void readMessage(Chat newChat, Assistant sendTo) {
        String initialAuth = Assistant.getInstance().getInitial();

        DatabaseReference chatReference = FirebaseDatabase.getInstance()
                .getReference(TAG_CHAT_FIREBASE)
                .child(initialAuth)
                .child(sendTo.getInitial())
                .child(newChat.getuId())
                .child("read");
    }

    public void isSocialMediaAuthRegistered(String email, final IService iService) {
        Query query =
                FirebaseDatabase.getInstance().getReference(TAG_ASSISTANT_FIREBASE)
                        .orderByChild("email")
                        .equalTo(email);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        iService.onSuccessResponse(snapshot.getValue(Assistant.class));
                } else
                    iService.onFailedResponse(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void savePassword(String username, String password) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(TAG_ASSISTANT_FIREBASE);
        Query query = reference.orderByChild("initial").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Assistant assistant = snapshot.getValue(Assistant.class);
                        /*
                          kalo password belum ke set,
                          set
                         */
                        if (assistant.getPassword() == null) {
                            assistant.setEncryptPassword(password);
                            reference.child(snapshot.getKey()).setValue(assistant);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getInvitations(final IService iService) {
        String initialAuth = Assistant.getInstance().getInitial();

        FirebaseDatabase.getInstance()
                .getReference(TAG_INVITATIONS_FIREBASE)
                .orderByChild("initial")
                .equalTo(initialAuth)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Pair<String, CustomTask>> customTasks = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Pair<String, CustomTask> customTaskPair = new Pair<>(snapshot.getKey(), snapshot.getValue(CustomTask.class));
                            customTasks.add(customTaskPair);
                        }

                        iService.onSuccessResponse(customTasks);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        iService.onFailedResponse(databaseError.getMessage());
                    }
                });
    }

    public void sharedInvitationTask(List<Assistant> sharedAssistant, CustomTask task) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(TAG_INVITATIONS_FIREBASE);

        Assistant auth = Assistant.getInstance();

        for (Assistant assistant : sharedAssistant) {
            String key = reference.push().getKey();
            if (assistant.getInitial().equals(auth.getInitial())) continue;

            /*
              check if already invite , declined
             */

            isAlreadyShared(task,assistant, reference, new IService() {
                @Override
                public void onSuccessResponse(Object response) {
                    boolean isExists = (boolean) response;
                    if (!isExists){
                        task.setInitial(assistant.getInitial());
                        task.setSharedBy(auth);
                        if (key != null) {
                            reference.child(key).setValue(task);
                        }
                    }
                }

                @Override
                public void onFailedResponse(Object response) {

                }
            });
        }

    }

    private void isAlreadyShared(CustomTask task, Assistant assistant,DatabaseReference reference, final IService iService) {
        String initalAuth = Assistant.getInstance().getInitial();
        reference.orderByChild("filterTask")
                    .equalTo(String.format("%s-%s-%s", initalAuth,assistant.getInitial(), task.getKey()))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            iService.onSuccessResponse(dataSnapshot.exists());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            iService.onFailedResponse(true);
                        }
                    });
    }

    public void rejectInvitation(CustomTask task, String key) {
        FirebaseDatabase.getInstance()
                .getReference(TAG_INVITATIONS_FIREBASE)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void acceptInvitation(CustomTask task) {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference(TAG_TASK_FIREBASE);

        String key = reference.push().getKey();

        reference.child(key).setValue(task);

    }
}
