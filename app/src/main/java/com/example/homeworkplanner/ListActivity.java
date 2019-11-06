package com.example.homeworkplanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements Serializable {
    private TaskCursorAdapter taskAdapter = new TaskCursorAdapter(this, TaskDatabase.getUnfinishedTaskCursor());
    private ArrayList<CountDownTimer> timers = new ArrayList<CountDownTimer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // setOverdueAlarm up (+) button to open an AddTaskActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddTaskActivity.class);
                intent.putExtra("parent", (ListActivity) getParent());
                startActivityForResult(intent, 0);
            }
        });

        ListView lvItems = (ListView) findViewById(R.id.lvTasks);
        lvItems.setAdapter(taskAdapter);

        setTitle("Tasks");
    }

    public void addTimer(CountDownTimer timer) {
        timers.add(timer);
    }

    public void updateList() {
        for (CountDownTimer timer : timers)
            timer.cancel();
        timers.clear();
        taskAdapter.changeCursor(TaskDatabase.getUnfinishedTaskCursor());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            updateList();
    }

}
