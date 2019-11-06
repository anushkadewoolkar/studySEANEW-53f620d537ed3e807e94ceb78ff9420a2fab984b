package com.example.homeworkplanner;

/**
 * Created by austin on 10/26/17.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

class DateTimePicker {
    private AddTaskActivity parent;
    private Calendar currentDate = Calendar.getInstance();

    DateTimePicker(AddTaskActivity parent) {
        this.parent = parent;
    }

    DateTimePicker(AddTaskActivity parent, long datetime) {
        this.parent = parent;
        currentDate.setTimeInMillis(datetime);
    }

    public static class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        public DatePickerDialogFragment() {
        }

        // called after user picks a date
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            TimePickerDialogFragment newFragment = new TimePickerDialogFragment();

            Bundle bundle = getArguments();
            bundle.putInt("year", year);
            bundle.putInt("month", month);
            bundle.putInt("dayOfMonth", dayOfMonth);

            newFragment.setArguments(bundle);
            newFragment.show(ft, "time_dialog");
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar currentDate = (Calendar) getArguments().getSerializable("currentDate");
            return new DatePickerDialog(getActivity(),
                    this, currentDate.get(Calendar.YEAR),
                    currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
        }

    }

    // show the date & time picker dialogs
    void show() {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        Bundle bundle = new Bundle(1);

        bundle.putSerializable("parent", parent);
        bundle.putSerializable("currentDate", currentDate);
        fragment.setArguments(bundle);
        fragment.show(parent.getFragmentManager().beginTransaction(), "date_dialog");
    }


    public static class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        public TimePickerDialogFragment() {
        }

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar currentDate = (Calendar) getArguments().getSerializable("currentDate");
            return new TimePickerDialog(getActivity(), this, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
        }

        // called after user picks a time
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Bundle bundle = getArguments();

            AddTaskActivity parent = (AddTaskActivity) bundle.getSerializable("parent");
            int year = bundle.getInt("year");
            int month = bundle.getInt("month");
            int dayOfMonth = bundle.getInt("dayOfMonth");

            parent.setDateTime(year, month, dayOfMonth, hour, minute);
        }
    }
}