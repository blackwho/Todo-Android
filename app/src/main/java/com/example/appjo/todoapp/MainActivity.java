package com.example.appjo.todoapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.appjo.todoapp.database.TaskEntry;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener{

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing the viewModel
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.init();
        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewTasks);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                List<TaskEntry> tasks = mAdapter.getTasks();
                viewModel.deleteTask(tasks.get(position), getString(R.string.delete_task));
            }
        }).attachToRecyclerView(mRecyclerView);

        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });
        //attaching the MainViewModel
        setupViewModel();
    }

    protected void onStop(){
        super.onStop();
        Log.v(TAG, "I am onStop");
    }

    protected void onPause(){
        super.onPause();
        Log.v(TAG, "I am onPause");
    }

    protected void onDestroy(){
        super.onDestroy();
        viewModel.setStatusNull();
        Log.v(TAG, "I am onDestroy");
    }
    private void setupViewModel() {
        viewModel.getTasks().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
            }
        });
        viewModel.getStatus().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != ""){
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }
}
