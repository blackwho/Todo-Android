package com.example.appjo.todoapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.appjo.todoapp.database.TaskEntry;

public class AddTaskViewModel extends AndroidViewModel {


    // Constant for logging
    private static final String TAG = AddTaskViewModel.class.getSimpleName();
    private TodoRepository todoRepo;
    private LiveData<TaskEntry> task;

    public AddTaskViewModel(Application application) {
        super(application);
        this.todoRepo = TodoRepository.getInstance(application);
    }

    public void init(int id){
        task = todoRepo.loadTaskById(id);
    }

    public LiveData<TaskEntry> getTask() {
        return task;
    }

    public void addTask(TaskEntry task, String checker){
        todoRepo.addDbTask(task, checker);
    }

    public void updateTask(TaskEntry task, String checker){
        todoRepo.updateDbTask(task, checker);

    }

}
