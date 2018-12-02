package com.example.appjo.todoapp.Utils;

import android.os.Bundle;
import android.os.Message;

public class UtilClass {
    public static final String LOG_TAG = "BackgroundThread";
    public static final int MESSAGE_ID = 1;
    public static final String MESSAGE_BODY = "MESSAGE_BODY";
    public static final String EMPTY_MESSAGE = "<EMPTY_MESSAGE>";

    public static Message createMessage(int id, String dataString) {
        Bundle bundle = new Bundle();
        bundle.putString(UtilClass.MESSAGE_BODY, dataString);
        Message message = new Message();
        message.what = id;
        message.setData(bundle);
        return message;
    }
}
