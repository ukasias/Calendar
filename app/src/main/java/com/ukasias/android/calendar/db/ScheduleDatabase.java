package com.ukasias.android.calendar.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.ukasias.android.calendar.BasicInfo;

public class ScheduleDatabase {
    public static final String TAG = "ScheduleDatabase";

    private static ScheduleDatabase database;

    private Context context;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;

    private static int DATABASE_VERSION = 1;

    public static String TABLE_SCHEDULE = "SCHEDULE";

    ScheduleDatabase(Context context) {
        this.context = context;
    }

    public static ScheduleDatabase getInstance(Context context) {
        if (database == null) {
            database = new ScheduleDatabase(context);
        }
        return database;
    }

    /**
     * DB 열기
     */
    public boolean open() {
        println("opening database [" + BasicInfo.DATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();

        return true;
    }

    /**
     * DB 닫기
     */
    public void close() {
        println("closing database [" + BasicInfo.DATABASE_NAME + "].");

        db.close();

        database = null;
    }

    public Cursor rawQuery(String SQL) {
        println("rawQuery(" + SQL + ") executed.");

        Cursor c = null;
        try {
            c = db.rawQuery(SQL, null);
        }
        catch(Exception e) {
            Log.e(TAG, "Exception in rawQuery()");
        }

        return c;
    }

    public Cursor rawQuery(String SQL, String[] selectionArgs) {
        println("rawQuery(" + SQL);

        Cursor c = null;
        try {
            c = db.rawQuery(SQL, selectionArgs);
        }
        catch(Exception e) {
            Log.e(TAG, "Exception in rawQuery()");
        }

        return c;
    }

    public boolean execSQL(String SQL) {
        println("execSQL() executed: SQL - " + SQL);

        try {
            db.execSQL(SQL);
        }
        catch(Exception e) {
            Log.e(TAG, "Exception in execSQL()");
            return false;
        }

        return true;
    }

    private void println(String message) {
        Log.d(TAG, message + "\n");
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context,
                    BasicInfo.DATABASE_NAME,
                    null,
                    DATABASE_VERSION);
            println("BasicInfo.DATABASE_NAME: " + BasicInfo.DATABASE_NAME);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            println("creating database [" + BasicInfo.DATABASE_NAME + "].");

            /**
             * create table SCHEDULE
             */
            println("creating table [" + TABLE_SCHEDULE + "].");
            String DROP_SQL = "drop table if exists " + TABLE_SCHEDULE;

            try {
                db.execSQL(DROP_SQL);
            } catch (Exception e) {
                Log.e(TAG, "Exception in drop table " + TABLE_SCHEDULE, e);
            }

            String CREATE_SQL = "create table " + TABLE_SCHEDULE + "(" +
                    "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    "INPUT_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "TITLE_TEXT TEXT DEFAULT '', " +
                    "CONTENTS_TEXT TEXT DEFAULT '', " +
                    "CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP " + ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch (Exception e) {
                Log.e(TAG, "Exception in create table " + TABLE_SCHEDULE, e);
            }

            // index table은 구성하지 않음
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + BasicInfo.DATABASE_NAME + "].");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("UPgrading database from version " + oldVersion + " to "
                    + newVersion + ".");
        }
    }
}
