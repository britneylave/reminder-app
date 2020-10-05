package com.example.firstapplication.shared;

public class Constant {
    public static final int FEEDBACK_CONNECT_TIMEOUT = 15000; /*seconds*/

    public static final int FEEDBACK_READ_TIMEOUT = 15000;/*seconds*/

    public static final String BINUS_IP_ADDRESS = "10.22";
    public static final String DATE_PATTERN = "MM-dd-yyyy";
    public static final String CHAT_TIME_PATTERN = "HH:mm";

    public static final String SP_NAME = "SP_REMINDER";
    public static final String SP_AUTH_TOKEN_LIFETIME = "SP_AUTH_TOKEN_LIFETIME";
    public static final String SP_AUTH_TOKEN = "SP_AUTH_TOKEN";
    public static final String SP_AUTH_INITIAL = "SP_AUTH_INITIAL";

    public static final int TOKEN_LIFETIME = 2; //2 hours for token's lifetime

    public static final int JOB_MODE_HISTORY = 0;
    public static final int JOB_MODE_CURRENT = 1;
    public static final int JOB_MODE_FUTURE = 2;

    public static final String TAG_CHAT_FIREBASE = "chats";
    public static final String TAG_INVITATIONS_FIREBASE = "invitations";
    public static final String TAG_ASSISTANT_FIREBASE = "assistants";
    public static final String TAG_TASK_FIREBASE = "tasks";

    public static final String NOTIFICATION_CHANEL_ID = "com.example.firstapplication";

}
