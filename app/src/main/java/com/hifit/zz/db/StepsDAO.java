package com.hifit.zz.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hifit.zz.utils.LogUtil;

/**
 * Created by zz on 2016/5/26.
 */
public class StepsDAO {
    private SQLiteDatabase mDb;

    public StepsDAO(HiFitDbHelper helper) {
        mDb = helper.getWritableDatabase();
    }

    private ContentValues stepItem2ContentValues(StepItem stepItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", stepItem.date);
        contentValues.put("step", stepItem.step);
        return contentValues;
    }

    public void insert(StepItem stepItem) {
        ContentValues values = stepItem2ContentValues(stepItem);
        mDb.insert(HiFitDbHelper.TABLE_STEPS, null, values);
    }

    // 有此天数据则刷新，没有则插入
    public void update(StepItem stepItem) {
        if (hasItemDate(stepItem.date)) {
            ContentValues values = stepItem2ContentValues(stepItem);
            mDb.update(HiFitDbHelper.TABLE_STEPS, values, "date=?", new String[]{stepItem.date});
        } else {
            insert(stepItem);
        }
    }

    private boolean hasItemDate(String date) {
        Cursor cursor = mDb.query(HiFitDbHelper.TABLE_STEPS, null, "date=?", new String[]{date}, null, null,
                null, null);
        return (cursor.getCount() > 0);
    }

    public void queryStep() {
        // 相当于select * from students ;
        Cursor cursor = mDb.query(HiFitDbHelper.TABLE_STEPS, null, null, null, null, null,
                null, null);
        while (cursor.moveToNext()) {
            // 直接通过索引获取字段名
            int id = cursor.getInt(0);
            // 先获取字段的索引，然后再通过索引获取字段值
            String date = cursor.getString(cursor.getColumnIndex("date"));
            int step = cursor.getInt(cursor.getColumnIndex("step"));
            LogUtil.d("读取数据库: id=" + id + ", date=" + date + ", step=" + step);
        }
        // 关闭光标
        cursor.close();
    }

    public Cursor queryCursorDateDesc() {
        Cursor cursor = mDb.query(HiFitDbHelper.TABLE_STEPS, null, null, null, null, null,
                "date desc", null);
        return cursor;
    }
}
