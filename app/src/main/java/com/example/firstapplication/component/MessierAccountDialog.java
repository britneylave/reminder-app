package com.example.firstapplication.component;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.firstapplication.R;
import com.example.firstapplication.model.Assistant;
import com.example.firstapplication.service.IService;
import com.example.firstapplication.service.messier.MessierService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.firstapplication.shared.Constant.TAG_ASSISTANT_FIREBASE;

public class MessierAccountDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "MessierAccountDialog";
    private DatabaseReference databaseReferences;
    private EditText txtInitial, txtPassword;
    private String email;
    private TextView txtError;
    private Button submitBtn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_messier_account, container, false);

        email = getArguments().getString("email");

        submitBtn = v.findViewById(R.id.add_messier_btn);
        submitBtn.setOnClickListener(this);

        txtInitial = v.findViewById(R.id.txt_messier);
        txtPassword = v.findViewById(R.id.txt_messier_password);
        txtError = v.findViewById(R.id.txt_dialog_messier_error);

        databaseReferences = FirebaseDatabase.getInstance().getReference(TAG_ASSISTANT_FIREBASE);
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
        if (view.getId() == R.id.add_messier_btn) {
            final String initialMessier = txtInitial.getText().toString();
            final String passwordMessier = txtPassword.getText().toString();

            if (initialMessier.isEmpty()) {
                Toast.makeText(getContext(), "You have to fill your txtInitial!", Toast.LENGTH_SHORT).show();
                return;
            } else if (initialMessier.length() != 6) {
                Toast.makeText(getContext(), "Invalid txtInitial format!", Toast.LENGTH_SHORT).show();
                return;
            }

            Query query = databaseReferences.orderByChild("initial").equalTo(initialMessier);

            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Assistant assistant = snapshot.getValue(Assistant.class);

                            submitBtn.setText("Loading...");

                            final String password = assistant.getPassword() == null ?
                                    passwordMessier :
                                    assistant.getPlainPassword();

                            MessierService.getInstance()
                                    .loginAuth(assistant.getInitial(), password, new IService() {
                                        @Override
                                        public void onSuccessResponse(Object response) {
                                            updateData(assistant, snapshot.getKey(), (String) response);
                                            Activity activity = getActivity();
                                            if (activity instanceof IDialogListener)
                                                ((IDialogListener) activity).handleDialogClose();
                                            dismiss();
                                        }

                                        @Override
                                        public void onFailedResponse(Object response) {
                                            txtError.setText((String) response);
                                            submitBtn.setText("submit");
                                        }
                                    });
                        }
                    } else
                        txtError.setText("Please Contact Admin to Register your Initial");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void updateData(Assistant ast, String snapshotKey, String token) {
        ast.setEmail(email);
        ast.setEncryptPassword(txtPassword.getText().toString());

        Assistant.saveIdentityToLocal(token, ast.getInitial(), getContext());

        databaseReferences.child(snapshotKey).setValue(ast);
        Toast.makeText(getContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
    }

}