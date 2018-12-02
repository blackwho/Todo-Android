package com.example.appjo.todoapp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.appjo.todoapp.Utils.UtilClass;
import com.example.appjo.todoapp.database.AppDatabase;
import com.example.appjo.todoapp.database.TaskDao;
import com.example.appjo.todoapp.database.TaskEntry;

import java.sql.Statement;
import java.util.List;
import java.util.function.ToDoubleBiFunction;

//Singleton Repository class and also the data-layer
public class TodoRepository implements MessageUpdateCallback{

    private static final String TAG = TodoRepository.class.getSimpleName();
    private static TodoRepository mInstance;
    private TaskDao mTaskDao;
    private final MutableLiveData<String> status;
    // The handler for the UI thread. Used for handling messages from worker threads.
    private RepoHandler repoHandler;
    // singleton instance
    private TodoThreadPoolManager todoThreadPoolManager;

    private TodoRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        mTaskDao = db.taskDao();
        status = new MutableLiveData<>();

        // Initialize the handler for UI thread to handle message from worker threads
        repoHandler = new RepoHandler(Looper.getMainLooper(), application);
        todoThreadPoolManager = TodoThreadPoolManager.getmInstance();
        todoThreadPoolManager.setUiThreadCallback(TodoRepository.this);
    }

    public static TodoRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new TodoRepository(application);
        }
        return mInstance;
    }

    public LiveData<List<TaskEntry>> loadAllTasks(){
        return mTaskDao.loadAllTasks();
    }

    public LiveData<TaskEntry> loadTaskById(int id){
        return mTaskDao.loadTaskById(id);
    }

    public void addDbTask(TaskEntry task, String checker){
        initiateCallable(task, checker);
    }

    public void updateDbTask(TaskEntry task, String checker){
        initiateCallable(task, checker);
    }

    public void deleteDbTask(TaskEntry task, String checker){
        initiateCallable(task, checker);
    }

    public MutableLiveData<String> getStatus() {
        return status;
    }

    public void setNullStatus(){
        status.setValue("");
    }

    //initialize callable according to the task
    private void initiateCallable(TaskEntry task, String checker){
        TodoCallable callable = new TodoCallable(task, mTaskDao, checker);
        callable.setTodoThreadPoolManager(todoThreadPoolManager);
        todoThreadPoolManager.addCallable(callable);
    }

    @Override
    public void publishToUiThread(Message message) {
        // add the message from worker thread to UI thread's message queue
        if(repoHandler != null){
            repoHandler.sendMessage(message);
        }
    }

    //Handler class to retrieve the msg in UI thread
    public static class RepoHandler extends Handler {
        private Application mApplication;
        public RepoHandler(Looper looper, Application application) {
            super(looper);
            this.mApplication = application;
        }

        // This method will run on UI thread
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                // Our communication protocol for passing a string to the UI thread
                case UtilClass.MESSAGE_ID:
                    Bundle bundle = msg.getData();
                    Log.v("Bundle", "data" + bundle);
                    TodoRepository.getInstance(mApplication).status.setValue(bundle.getString("status"));
                    break;
                default:
                    break;
            }
        }
    }
}
