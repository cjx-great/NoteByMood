package com.cjx.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CJX on 2016/8/6.
 */
public class TrainDBHelper extends SQLiteOpenHelper {

    private static final String DATABASENAME = "Note";
    private static final int VERSION = 1;
    private static final String TABLEENAME = "book";

    public TrainDBHelper(Context context){
        super(context,DATABASENAME,null,VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists "+TABLEENAME;
        db.execSQL(sql);
        this.onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table "+TABLEENAME+"(" +
                "id integer primary key," +
                "note text," +
                "time DATE)";
        db.execSQL(sql);
    }
}
