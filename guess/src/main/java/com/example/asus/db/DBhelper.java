package com.example.asus.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.asus.common.MyToast;

/**
 * Created by Ahab on 2016/10/25.
 *
 */
public class DBhelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Record.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "record";

    private static DBhelper instance = null;

    public static DBhelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DBhelper.class) {
                if (instance == null) {
                    instance = new DBhelper(context, DATABASE_NAME, null, DATABASE_VERSION);
                }
            }
        }
        return instance;
    }

    private DBhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type CHAR(10)," +
                "movieName CHAR(20)," +
                "difficulty CHAR(10)," +
                "score INTEGER)"
        );
        Log.i("TAG", "create");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("TAG", "数据库升级*****************************************");
    }
}
