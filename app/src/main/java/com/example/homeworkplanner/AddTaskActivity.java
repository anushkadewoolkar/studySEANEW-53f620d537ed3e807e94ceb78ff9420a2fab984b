package com.example.homeworkplanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.text.DateFormat.getDateTimeInstance;

public class AddTaskActivity extends AppCompatActivity implements Serializable {
    static final TypedArray remTimes = HomeworkPlanner.getContext().getResources().obtainTypedArray(R.array.ReminderTimes);
    static final TypedArray remHrs = HomeworkPlanner.getContext().getResources().obtainTypedArray(R.array.ReminderHours);
    static final TypedArray remDays = HomeworkPlanner.getContext().getResources().obtainTypedArray(R.array.ReminderDays);
    static final String durTimes[] = { // TODO: move to strings.xml
            "0:05 minutes",
            "0:10 minutes",
            "0:15 minutes",
            "0:20 minutes",
            "0:30 minutes",
            "0:45 minutes",
            "1:00 hour",
            "1:15 hours",
            "1:30 hours",
            "1:45 hours",
            "2:00 hours",
            "2:30 hours",
            "3:00 hours",
            "3:30 hours",
            "4:00 hours",
            "5:00 hours",
            "6:00 hours",
            "7:00 hours",
            "8:00 hours",
            "9:00 hours",
            "10:00 hours"
    };
    Task task;
    EditText taskEdit;
    EditText classEdit;
    EditText msDue;
    EditText due;
    SeekBar timeItTakes;
    SeekBar importanceLevel;
    TextView viewTimeLeft;
    Spinner remOptions;
    TextView viewDays;
    TextView hrsLeft;
    Spinner viewDayOptions;
    Spinner viewHrOptions;
    String rem = "None";
    ArrayAdapter<CharSequence> remHrAdapter;
    ArrayAdapter<CharSequence> remDayAdapter;
    ArrayAdapter<CharSequence> remAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        long id = getIntent().getLongExtra("task", -1);
        if (id != -1)
            task = new Task(id);

        taskEdit = (EditText) findViewById(R.id.nameLine);
        classEdit = (EditText) findViewById(R.id.classNameLine);
        timeItTakes = (SeekBar) findViewById(R.id.timeDurationTwo);
        msDue = (EditText) findViewById(R.id.etDueMillis);
        due = (EditText) findViewById(R.id.selectDayandTime);
        viewTimeLeft = (TextView) findViewById(R.id.timeDurationOne);
        importanceLevel = (SeekBar) findViewById(R.id.importanceScroll);
        remOptions = (Spinner) findViewById(R.id.sReminder);
        viewDays = (TextView) findViewById(R.id.tvReminderDays);
        hrsLeft = (TextView) findViewById(R.id.tvReminderHours);
        viewDayOptions = (Spinner) findViewById(R.id.sReminderDays);
        viewHrOptions = (Spinner) findViewById(R.id.sReminderHours);

        hrsLeft.setVisibility(View.INVISIBLE);
        viewDays.setVisibility(View.INVISIBLE);
        viewHrOptions.setVisibility(View.INVISIBLE);
        viewDayOptions.setVisibility(View.INVISIBLE);

        remHrAdapter = ArrayAdapter.createFromResource(this, R.array.ReminderHours, android.R.layout.simple_spinner_item);
        remHrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHrOptions.setAdapter(remHrAdapter);

        remDayAdapter = ArrayAdapter.createFromResource(this, R.array.ReminderDays, android.R.layout.simple_spinner_item);
        remDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewDayOptions.setAdapter(remDayAdapter);

        remAdapter = ArrayAdapter.createFromResource(this, R.array.ReminderTimes, android.R.layout.simple_spinner_item);
        remAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        remOptions.setAdapter(remAdapter);
        remOptions.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (((String) parent.getItemAtPosition(pos)).compareTo("Custom") == 0) {
                    hrsLeft.setVisibility(View.VISIBLE);
                    viewDays.setVisibility(View.VISIBLE);
                    viewHrOptions.setVisibility(View.VISIBLE);
                    viewDayOptions.setVisibility(View.VISIBLE);
                } else {
                    hrsLeft.setVisibility(View.INVISIBLE);
                    viewDays.setVisibility(View.INVISIBLE);
                    viewHrOptions.setVisibility(View.INVISIBLE);
                    viewDayOptions.setVisibility(View.INVISIBLE);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        due.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus)
                    onDateClick(view);
            }
        });

        if (task == null) {
            setTitle("ADD A NEW ASSIGNMENT");
            msDue.setText(Long.toString(Calendar.getInstance().getTimeInMillis()));
            findViewById(R.id.bDelete).setVisibility(View.INVISIBLE);
            timeItTakes.setProgress(4);
            viewTimeLeft.setText(durTimes[4]);
        } else {
            setTitle("EDIT THIS ASSIGNMENT");
            ((Button) findViewById(R.id.bSubmit)).setText("Save");

            // fill inputs with task properties
            taskEdit.setText(task.get(TaskDatabase.TASK));
            classEdit.setText(task.get(TaskDatabase.CLASS));
            setDateTime(Long.parseLong(task.get(TaskDatabase.DUE)));
            timeItTakes.setProgress(Integer.parseInt(task.get(TaskDatabase.DUR_UI)));
            viewTimeLeft.setText(durTimes[Integer.parseInt(task.get((TaskDatabase.DUR_UI)))]);
            remOptions.setSelection(Integer.parseInt(task.get(TaskDatabase.REMINDER_UI)));
            viewHrOptions.setSelection(Integer.parseInt(task.get(TaskDatabase.REMINDER_HRS)));
            viewDayOptions.setSelection(Integer.parseInt(task.get(TaskDatabase.REMINDER_DAYS)));
            importanceLevel.setProgress(Integer.parseInt(task.get(TaskDatabase.IMPORTANCE)));
        }

        timeItTakes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewTimeLeft.setText(durTimes[progress]);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    // called when delete button is clicked
    public void delete(final View view) {
        new AlertDialog.Builder(view.getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to permanently delete this task?")   // TODO: move to strings.xml
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        task.delete();
                        setResult(RESULT_OK, null);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void setDateTime(long millis) {
        msDue.setText(Long.toString(millis));
        due.setText(getDateTimeInstance().format(new Date(millis)));
    }

    // display the date & time picker dialogs
    public void onDateClick(View v) {
        new DateTimePicker(this, Long.parseLong("" + msDue.getText())).show();
    }

    // convert string displayed above duration slider/seekbar to miliseconds (as string)
    private String getDuration() throws InvalidParameterException {
        Pattern p = Pattern.compile("^([0-9]+):([0-9]+)");
        Matcher match = p.matcher(durTimes[timeItTakes.getProgress()]);
        match.find();

        Integer hours = new Integer(match.group(1));
        Integer minutes = new Integer(match.group(2));
        return new Integer(hours * 60 * 60 * 1000 + minutes * 60 * 1000).toString(); // calculate miliseconds and return
    }

    // return the amount of time before the task is due to display a reminder
    private String getReminderTime() throws InvalidParameterException {
        switch ((String) remOptions.getSelectedItem()) {
            case "Custom":
                Integer days = new Integer(viewDayOptions.getSelectedItem().toString());
                Integer hours = new Integer(viewHrOptions.getSelectedItem().toString());

                return new Integer(days * 24 * 60 * 60 * 1000 + days * 60 * 60 * 1000).toString();
            case "None":
                return "0";
            default:
                Pattern pattern = Pattern.compile("^(\\d+) (\\w+[^s])s?$");
                Matcher match = pattern.matcher((String) remOptions.getSelectedItem());
                match.find();

                Integer num = new Integer(match.group(1));
                switch (match.group(2)) {
                    case "minute":
                        return new Integer(num * 60 * 1000).toString();
                    case "hour":
                        return new Integer(num * 60 * 60 * 1000).toString();
                    case "day":
                        return new Integer(num * 24 * 60 * 60 * 1000).toString();
                    case "week":
                        return new Integer(num * 7 * 24 * 60 * 60 * 1000).toString();
                    default:
                        throw new InvalidParameterException();
                }
        }
    }

    // callback for the datetime picker that's triggered by onDateClick
    public void setDateTime(int year, int month, int dayOfMonth, int hour, int minute) {
        Calendar datetime = new GregorianCalendar(year, month, dayOfMonth, hour, minute);

        msDue.setText(Long.toString(datetime.getTimeInMillis()));
        due.setText(getDateTimeInstance().format(datetime.getTime()));
    }


    // create the new task (called when "add task" button is pressed)
    public void submit(View v) throws Exception {
        // TODO: better error handling
        if (taskEdit.getText().length() == 0)
            Snackbar.make(findViewById(android.R.id.content), "Please enter a task name", Snackbar.LENGTH_LONG)
                    .show();

        else if (classEdit.getText().length() == 0)
            Snackbar.make(findViewById(android.R.id.content), "Please enter a class name", Snackbar.LENGTH_LONG)
                    .show();

        else {
            HashMap<String, String> properties = new HashMap<>();

            // load existing task id if editing a task
            if (task != null)
                properties.put(TaskDatabase.COLUMN, task.getId().toString());

            properties.put(TaskDatabase.TASK, taskEdit.getText().toString());
            properties.put(TaskDatabase.CLASS, classEdit.getText().toString());
            properties.put(TaskDatabase.DUE, msDue.getText().toString());
            properties.put(TaskDatabase.DUR, getDuration());
            properties.put(TaskDatabase.IMPORTANCE, Integer.toString(importanceLevel.getProgress()));
            properties.put(TaskDatabase.REMINDER, getReminderTime());
            properties.put(TaskDatabase.DUR_UI, Integer.toString(timeItTakes.getProgress()));
            properties.put(TaskDatabase.REMINDER_UI, Long.toString(remOptions.getSelectedItemId()));
            properties.put(TaskDatabase.REMINDER_DAYS, viewDayOptions.getSelectedItem().toString());
            properties.put(TaskDatabase.REMINDER_HRS, viewHrOptions.getSelectedItem().toString());

            if (task == null) {
                task = new Task(properties);

                // add new task to database
                Long id = new Long(task.insertTask());
                task.set(TaskDatabase.COLUMN, id.toString());
            } else {
                task = new Task(properties);

                // cancel old alarms
                Alarm.cancelOverdueAlarm(task.getId());
                Alarm.cancelReminderAlarm(task.getId());

                // update task in database
                task.updateTask();
            }

            long now = Calendar.getInstance().getTimeInMillis();

            // create an overdue alarm for the task
            if (now < Long.parseLong(task.get(TaskDatabase.DUE)))
                Alarm.setOverdueAlarm(task);

            // create a reminder alarm for the task
            if (task.get(TaskDatabase.REMINDER) != "0")
                Alarm.setReminderAlarm(task);

            setResult(RESULT_OK, null);
            finish();
        }
    }
}
