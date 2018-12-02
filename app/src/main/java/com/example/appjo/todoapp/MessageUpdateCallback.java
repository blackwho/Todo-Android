package com.example.appjo.todoapp;

import android.os.Message;

// Interface for worker threads to send messages to the UI thread.
public interface MessageUpdateCallback {

    void publishToUiThread(Message message);
}
