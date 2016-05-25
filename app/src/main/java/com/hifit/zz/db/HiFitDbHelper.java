package com.hifit.zz.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hifit.zz.config.HiFitConfig;

/**
 * Created by zz on 2016/5/25.
 */
public class HiFitDbHelper extends SQLiteOpenHelper {

    public static final String TABLE_STEPS = "steps";
    private static final String CREATE_TABLE_STEPS = "CREATE TABLE " + TABLE_STEPS + " ("
            + "id  integer PRIMARY KEY AUTOINCREMENT, "
            + "date vachar(20) NOT NULL,"
            + "step integer NOT NULL"
            + ");";

    public HiFitDbHelper(Context context) {
        super(context, HiFitConfig.DB_NAME, null, HiFitConfig.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STEPS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
