package com.example.homeworkplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TaskCursorAdapter extends CursorAdapter implements View.OnClickListener, View.OnLongClickListener {
    private ListActivity context;
    private Task task;

    @Override
    public boolean onLongClick(final View view) {
        new AlertDialog.Builder(this.context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Finished Task")
                .setMessage("Have you finished this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Task(getTaskId(view)).markFinished();
                        context.updateList();
                    }
                })
                .setNegativeButton("No", null)
                .show();
        return true;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context.getApplicationContext(), AddTaskActivity.class);
        intent.putExtra("task", getTaskId(view));
        context.startActivityForResult(intent, 0);
    }

    private static long getTaskId(View view) {
        TextView tvId = (TextView) view.findViewById(R.id.tvId);
        return Long.parseLong("" + tvId.getText());
    }

    TaskCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = (ListActivity) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        task = new Task(cursor);
        task.addToView(view);


        String col_class = task.get(TaskDatabase.CLASS);
        Pattern p = Pattern.compile("([a-zA-Z]?)(.*)$");
        Matcher match = p.matcher(col_class);
        int hue;

        if (match.matches()) {
            String subj = match.group(1).toUpperCase();
            String code = match.group(2).toUpperCase();

            hue = ((subj.hashCode() & 0xF0) ^ (code.hashCode() & 0xFF));
        } else
            hue = col_class.hashCode() & 0xFF;

        float components[] = {hue * 360 / 255, 1, 1};

        view.setBackgroundColor(Color.CYAN);

        ConstraintLayout clTask = (ConstraintLayout) view.findViewById(R.id.clTask);
        clTask.setOnClickListener(this);
        clTask.setOnLongClickListener(this);

        final long due = Long.parseLong(task.get(TaskDatabase.DUE));
        long now = Calendar.getInstance().getTimeInMillis();
        final TextView tvDue = (TextView) view.findViewById(R.id.tvDue);

        if (due <= now)
            tvDue.setText("this assignment is past due!");
        else
            this.context.addTimer(new CountDownTimer(due - now, 1000) {
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    int minutes = (int) (seconds / 60);
                    int hours = minutes / 60;
                    int days = hours / 24;
                    int weeks = days / 7;

                    String dueIn = "Due in ";

                    if (weeks > 1)
                        dueIn += String.format("%d weeks, ", weeks);
                    else if (weeks == 1)
                        dueIn += "one week, ";

                    if (days > 1)
                        dueIn += String.format("%d days, ", days % 7);
                    else if (days == 1)
                        dueIn += "one day, ";

                    tvDue.setText(String.format("%s%d:%02d:%02d", dueIn, hours % 24, minutes % 60,
                            seconds % 60));
                }

                public void onFinish() {
                    tvDue.setText("this assignment is past due!");
                }

            }.start());
    }

}
