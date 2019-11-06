package com.example.homeworkplanner;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class TaskDatabase extends SQLiteOpenHelper {
    //task table
    static final String TASKS = "tasks";
    static final String COLUMN= "column";
    static final String CLASS = "class";
    static final String TASK = "task";
    static final String FINISHED = "finished";
    static final String DUE = "due";
    static final String DUR = "duration";
    static final String REMINDER = "reminder";
    static final String REMINDER_HRS = "reminder_hrs";
    static final String REMINDER_DAYS = "reminder_days";
    static final String DUR_UI = "duration_ui";
    static final String REMINDER_UI = "reminder_ui";
    static final String IMPORTANCE = "importance";

    static final String TABLE_COLUMNS[] = {
            COLUMN,
            IMPORTANCE,
            CLASS,
            COLUMN,
            TASK,
            DUR,
            REMINDER,
            REMINDER_HRS,
            REMINDER_DAYS,
            DUR_UI,
            REMINDER_UI,
            FINISHED
    };

    //database values
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 12;

    private static final TaskDatabase instance = new TaskDatabase();
    private final SQLiteDatabase db;

    private TaskDatabase() {
        super(HomeworkPlanner.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    public static SQLiteDatabase getDatabase() {
        return instance.db;
    }

    public static Cursor getUnfinishedTaskCursor() {
        // Sort algorithm: importance / 10 * ( 100 - duration / 30 - EXP(0.001 * time_to_deadline_in_minutes))
        return instance.db.query(true, TASKS, null, FINISHED + " = 0", null, null, null,
                IMPORTANCE + " * 0.1 * ( 100 - 60*1000*" + DUR + " / 30 - 1.06)", null);
    }

    public static Cursor selectTaskById(long id) {
        return instance.db.query(true, TASKS, null, COLUMN + " = " + new Long(id).toString(),
                null, null, null, null, null);
    }

    // This is where we need to write create table statements.
    // This is called when database is created.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "
                + TASKS + " ( "
                + COLUMN + " integer primary key autoincrement default 0, "
                + CLASS + " string, "
                + TASK + " string, "
                + IMPORTANCE + " long, "
                + REMINDER + " long, "
                + REMINDER_DAYS + " long, "
                + REMINDER_HRS + " long, "
                + DUR + " long, "
                + REMINDER_UI + " long, "
                + DUR_UI + " long, "
                + DUE + " long, "
                + FINISHED + " long DEFAULT 0 );");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        // TODO
        db.execSQL("DROP TABLE tasks;");
        onCreate(db);
    }
}
