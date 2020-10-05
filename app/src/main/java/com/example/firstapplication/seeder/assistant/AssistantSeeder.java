package com.example.firstapplication.seeder.assistant;

import com.example.firstapplication.model.Assistant;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AssistantSeeder {

    public static void seed() {
        final String[] assistants = {"AP18-2", "RS18-2", "DL18-2", "LV18-2", "JN18-2", "DD18-2", "KY18-2", "RL18-2", "CL18-2", "KE18-2", "HY18-2", "AM18-2", "RY18-2", "LW18-2", "GN18-2", "KS18-2", "AW18-2", "KF18-2","GB18-2"};

        DatabaseReference astReferences = FirebaseDatabase.getInstance().getReference("assistants");

        Assistant assistant = Assistant.newInstance();

        for (String ast : assistants) {
            assistant.setuId(astReferences.push().getKey());
            assistant.setInitial(ast);
            astReferences.child(assistant.getuId()).setValue(assistant);
        }
    }

}
