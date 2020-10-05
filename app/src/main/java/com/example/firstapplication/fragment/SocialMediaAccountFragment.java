package com.example.firstapplication.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.firstapplication.R;
import com.example.firstapplication.model.Assistant;
import com.example.firstapplication.shared.SharedPreferencesManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.firstapplication.shared.Constant.SP_AUTH_INITIAL;
import static com.example.firstapplication.shared.Constant.TAG_ASSISTANT_FIREBASE;

public class SocialMediaAccountFragment extends DialogFragment implements View.OnClickListener {

    private EditText googleText;
    private Query query;
    private DatabaseReference databaseReferences;
    private String initial;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_update_social_media_account, container, false);

        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(getContext());

        final Button btnGoogle = v.findViewById(R.id.add_google_btn);
        btnGoogle.setOnClickListener(this);

        final ImageButton btnClose = v.findViewById(R.id.btn_dialog_close);
        btnClose.setOnClickListener(this);

        googleText = v.findViewById(R.id.add_google_email);

        databaseReferences = FirebaseDatabase.getInstance().getReference(TAG_ASSISTANT_FIREBASE);

        initial = sharedPreferencesManager.getString(SP_AUTH_INITIAL);
        query = databaseReferences.orderByChild("initial").equalTo(initial);
        return v;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_google_btn) {
            final String email = googleText.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(getContext(), "You have to fill your gmail account!", Toast.LENGTH_SHORT).show();
                return;
            } else if (!email.endsWith("@gmail.com")) {
                Toast.makeText(getContext(), "Invalid email format!", Toast.LENGTH_SHORT).show();
                return;
            }

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Assistant assistant = snapshot.getValue(Assistant.class);
                            assert assistant != null;
                            updateData(assistant, snapshot.getKey());
                        }
                    } else {
                        /*
                          if not exist, add
                         */
                        addDataAssistant();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (view.getId() == R.id.btn_dialog_close)
            dismiss();
    }

    private void addDataAssistant() {
        Assistant assistant = Assistant.newInstance(
                initial,
                databaseReferences.push().getKey(),
                googleText.getText().toString()
        );
        databaseReferences.child(assistant.getuId()).setValue(assistant);
    }

    private void updateData(Assistant ast, String key) {
        ast.setEmail(googleText.getText().toString());

        databaseReferences.child(key).setValue(ast);
        Toast.makeText(getContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}