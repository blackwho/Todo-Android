package com.example.appjo.todoapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.appjo.todoapp.database.AppDatabase;
import com.example.appjo.todoapp.database.TaskEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();
    private TodoRepository todoRepo;
    private MutableLiveData<String> status;

    private LiveData<List<TaskEntry>> tasks;

    public MainViewModel(Application application) {
        super(application);
        this.todoRepo = TodoRepository.getInstance(application);
    }

    public void init(){
        tasks = todoRepo.loadAllTasks();
    }

    public LiveData<List<TaskEntry>> getTasks() {
        return tasks;
    }

    public void deleteTask(TaskEntry entry, String checker){
        todoRepo.deleteDbTask(entry, checker);
    }

    public MutableLiveData<String> getStatus() {
        return todoRepo.getStatus();
    }

    public void setStatusNull(){
        todoRepo.setNullStatus();
    }
}
