package com.hifit.zz.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by zz on 2016/5/14.
 */
public class CalcBaseStep {
    private static final String TAG_STEP_SERVICE = "CalcBaseStep";
    public static final String PREFS_NAME = "PrefsFile";
    public static final String PREFS_KEY_BASEDATE = "PrefsFileDate";
    public static final String PREFS_KEY_BASESTEP = "PrefsFileStep";
    public static final String PREFS_KEY_STEP_BEFORE_DATE = "StepBeforeDate";
    public static final String PREFS_KEY_STEP_BEFORE_BOOT = "StepBeforeBoot";

    //private Date mBaseDate;
    private String mBaseDate = null;
    private int mBaseStep = 0;
    private String mStepBeforeDate = null;
    private int mStepBeforeBoot = 0;

    private Context mContext;
    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;


    public CalcBaseStep(Context context) {
        mContext = context;
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefEditor = preferences.edit();

        // 当天标准计步器的起始步数。用于跨天，以及“手机不重启、Service异常重启”的场景
        mBaseDate = preferences.getString(PREFS_KEY_BASEDATE, null);
        mBaseStep = preferences.getInt(PREFS_KEY_BASESTEP, 0);

        // 手机重启后，需要把重启前的步数记录下来，再累加重启后的步数
        mStepBeforeDate = preferences.getString(PREFS_KEY_STEP_BEFORE_DATE, null);
        mStepBeforeBoot = preferences.getInt(PREFS_KEY_STEP_BEFORE_BOOT, 0);

        Toast.makeText(mContext, "calcTodayStep create:" + mBaseDate + "," + mBaseStep
                + "," + mStepBeforeDate + "," + mStepBeforeBoot, Toast.LENGTH_LONG).show();
    }

    public boolean isBase(Date date) {
        //return (date.equals(mBaseDate)) ? false : true;
        if (mBaseDate == null) {
            return true;
        }

        String strDate = getDateString(date);
        if (mBaseDate.equals(strDate)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean hasStepBeforeBoot(Date date) {
        if (mStepBeforeDate == null) {
            return false;
        }

        String strDate = getDateString(date);
        if (mStepBeforeDate.equals(strDate)) {
            return true;
        } else {
            return false;
        }
    }

    @NonNull
    private String getDateString(Date date) {
        return "" + date.getYear() + date.getMonth() + date.getDate();
    }

    public int calcTodayStep(Date date, int step) {
        if (isBase(date)) {
            //mBaseDate = date;
            mBaseDate = getDateString(date); //"" + date.getYear() + date.getMonth() + date.getDate();
            prefEditor.putString(PREFS_KEY_BASEDATE, mBaseDate);
            mBaseStep = step;
            prefEditor.putInt(PREFS_KEY_BASESTEP, mBaseStep);
            prefEditor.commit();

            Toast.makeText(mContext, "calcTodayStep isBase:" + mBaseDate + "," + mBaseStep, Toast.LENGTH_LONG).show();
        }

        if (hasStepBeforeBoot(date)) {
            step += mStepBeforeBoot;
        }

        return (step - mBaseStep);
    }

    public void initTodayStep(Date date, int step) {
        mBaseDate = getDateString(date); //"" + date.getYear() + date.getMonth() + date.getDate();
        prefEditor.putString(PREFS_KEY_BASEDATE, mBaseDate);
        mBaseStep = step;
        prefEditor.putInt(PREFS_KEY_BASESTEP, mBaseStep);
        prefEditor.commit();
    }

    public void saveStepBeforeBoot(Date date, int step) {
        prefEditor.putString(PREFS_KEY_STEP_BEFORE_DATE, getDateString(date));
        prefEditor.putInt(PREFS_KEY_STEP_BEFORE_BOOT, step);
        prefEditor.commit();
    }
}
