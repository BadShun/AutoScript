package com.badshun.autoscript;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;

    private final String CREATE_TABLE = "create table script(" +
            "id integer primary key autoincrement, " +
            "task_name text, " +
            "script_path text)";

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    public void insert(String taskName, String scriptPath, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("task_name", taskName);
        contentValues.put("script_path", scriptPath);
        db.insert("script", null, contentValues);
    }

    public void update(String taskName, String scriptPath, String oldTaskName, String oldScriptPath, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("task_name", taskName);
        contentValues.put("script_path", scriptPath);
        db.update("script", contentValues, "task_name = ? and script_path = ?", new String[]{
                oldTaskName,
                oldScriptPath
        });
    }

    public void delete(String taskName, String scriptPath, SQLiteDatabase db) {
        db.delete("script", "task_name = ? and script_path = ?", new String[]{
                taskName,
                scriptPath
        });
    }

    public ArrayList query(SQLiteDatabase db) {
        ArrayList<Script> scriptList = new ArrayList<Script>();

        Cursor cursor = db.query("script", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                 @SuppressLint("Range") String taskName = cursor.getString(cursor.getColumnIndex("task_name"));
                 @SuppressLint("Range") String scriptPath = cursor.getString(cursor.getColumnIndex("script_path"));

                 scriptList.add(new Script(taskName, scriptPath));
            } while(cursor.moveToNext());
        }

        return scriptList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
