package com.example.appjo.todoapp;

import android.os.Bundle;
import android.os.Message;

import com.example.appjo.todoapp.Utils.UtilClass;
import com.example.appjo.todoapp.database.TaskDao;
import com.example.appjo.todoapp.database.TaskEntry;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

public class TodoCallable implements Callable {

    // Keep a weak reference to the TodoThreadPoolManager singleton object, though not necessary as this is singleton.
    private WeakReference<TodoThreadPoolManager> mTodoThreadPoolManagerWeakReference;
    private TaskEntry taskEntity;
    private TaskDao taskDao;
    private String checker;

    public TodoCallable(TaskEntry entity, TaskDao dao, String mChecker){
        this.taskEntity = entity;
        this.taskDao = dao;
        this.checker = mChecker;
    }
    @Override
    public Object call() throws Exception {
        Message message = new Message();
        Bundle bundle = new Bundle();
        try{
            if (Thread.interrupted()){
                throw new InterruptedException();
            }
            if (taskDao != null && taskEntity != null){
                if (checker.equals("insert")){
                    taskDao.insertTask(taskEntity);
                    bundle.putString("status", "Task Added!");
                }else if (checker.equals("update")){
                    taskDao.updateTask(taskEntity);
                    bundle.putString("status", "Task Updated!");
                }else if (checker.equals("delete")){
                    taskDao.deleteTask(taskEntity);
                    bundle.putString("status", "Task Deleted!");
                }
                message.what = UtilClass.MESSAGE_ID;
                message.setData(bundle);
                if(mTodoThreadPoolManagerWeakReference != null
                        && mTodoThreadPoolManagerWeakReference.get() != null) {

                    mTodoThreadPoolManagerWeakReference.get().sendMessageToUiThread(message);
                }
            }

        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return null;
    }

    public void setTodoThreadPoolManager(TodoThreadPoolManager todoThreadPoolManager) {
        this.mTodoThreadPoolManagerWeakReference = new WeakReference<TodoThreadPoolManager>(todoThreadPoolManager);
    }
}
