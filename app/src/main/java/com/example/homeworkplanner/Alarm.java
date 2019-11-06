package com.example.homeworkplanner;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import java.security.InvalidParameterException;
import java.util.HashMap;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * THE LINK WE USED FOR THIS WAS: https://www.youtube.com/watch?v=vJOW_Idnx7w
 */

public class Alarm {
    static private int id = 0;
    static private HashMap<Long, OverdueListener> overdueAlarms = new HashMap<Long, OverdueListener>();
    static private HashMap<Long, ReminderListener> reminderAlarms = new HashMap<Long, ReminderListener>();


    static public void setOverdueAlarm(Task task) {
        if (task == null)
            throw new InvalidParameterException("task cannot be null");

        OverdueListener listener = new OverdueListener(task);
        AlarmManager alarmManager = (AlarmManager) HomeworkPlanner.getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, Long.parseLong(task.get(TaskDatabase.DUE)), "Overdue Alarm", listener, null);

        overdueAlarms.put(task.getId(), listener);
    }

    static public void cancelOverdueAlarm(long taskId) {
        OverdueListener listener = overdueAlarms.get(taskId);
        if (listener != null) {
            ((AlarmManager) HomeworkPlanner.getContext().getSystemService(Context.ALARM_SERVICE)).cancel(listener);
            overdueAlarms.remove(taskId);
        }
    }

    static public void cancelReminderAlarm(long taskId) {
        ReminderListener listener = reminderAlarms.get(taskId);
        if (listener != null) {
            ((AlarmManager) HomeworkPlanner.getContext().getSystemService(Context.ALARM_SERVICE)).cancel(listener);
            reminderAlarms.remove(taskId);
        }
    }

    static private class ReminderListener implements AlarmManager.OnAlarmListener {
        Task task;

        ReminderListener(Task task) {
            this.task = task;
        }

        public void onAlarm() {
            reminderAlarms.remove(task.getId());

            // create a new notification
            // TODO: improve notification
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(HomeworkPlanner.getContext())
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentText(task.get(TaskDatabase.CLASS) + " - Task Reminder")
                            .setContentTitle(task.get(TaskDatabase.TASK));

            NotificationManager mNotifyMgr = (NotificationManager) HomeworkPlanner.getContext().getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(id++, mBuilder.build());
        }
    }

    static public void setReminderAlarm(Task task) {
        if (task == null)
            throw new InvalidParameterException("PLEASE FILL OUT A TASK NAME");

        if (task.get(TaskDatabase.REMINDER) == null)
            return;     // reminders are disabled for this task

        Long due = Long.parseLong(task.get(TaskDatabase.DUE));
        Long reminder = Long.parseLong(task.get(TaskDatabase.REMINDER));     // # miliseconds before 'due' to trigger reminder
        ReminderListener listener = new ReminderListener(task);

        AlarmManager alarmManager = (AlarmManager) HomeworkPlanner.getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, due - reminder, "Reminder Alarm", listener, null);

        reminderAlarms.put(task.getId(), listener);
    }

    static private class OverdueListener implements AlarmManager.OnAlarmListener {
        Task task;

        OverdueListener(Task task) {
            this.task = task;
        }

        public void onAlarm() {
            overdueAlarms.remove(task.getId());

            // create a new notification
            // TODO: improve notification
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(HomeworkPlanner.getContext())
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentTitle(task.get(TaskDatabase.CLASS + " - Task Overdue"))
                            .setContentText(task.get(TaskDatabase.TASK));

            NotificationManager mNotifyMgr = (NotificationManager) HomeworkPlanner.getContext().getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(id++, mBuilder.build());
        }
    }
}
