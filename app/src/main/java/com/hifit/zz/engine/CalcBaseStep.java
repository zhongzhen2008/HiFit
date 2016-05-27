package com.hifit.zz.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.hifit.zz.activity.HifitApp;
import com.hifit.zz.db.StepItem;
import com.hifit.zz.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    private int lastTodayStep = 0;   // 最近一次今日总步数

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
        LogUtil.d("calcTodayStep create:" + mBaseDate + "," + mBaseStep
                + "," + mStepBeforeDate + "," + mStepBeforeBoot);
    }

    public boolean isBase(Date date) {
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
/*        int year = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day = date.getDate();
        return "" + year + month + day;*/

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String s = sdf.format(date);
        return s;

        //return "" + date.getYear() + date.getMonth() + date.getDate();
    }

    public int calcTodayStep(Date date, int step) {
        if (isBase(date)) {
            // 昨天总步数记录到数据库
            saveTodayStep(date);

            initTodayStep(date, step);
        }

        if (hasStepBeforeBoot(date)) {
            step += mStepBeforeBoot;
        }

        lastTodayStep = step - mBaseStep;
        return lastTodayStep;
    }

    private void saveTodayStep(Date date) {
        StepItem stepItem = new StepItem();
//        long a = date.getTime();
//        long b = a - 24*60*60*1000;
//        Date yesterday = new Date(b);
//        //Date yesterday = new Date(date.getTime() - 24*60*60*1000);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        //Date yesterday = calendar.getTime();

        stepItem.date = getDateString(calendar.getTime());
        stepItem.step = lastTodayStep;
        HifitApp app = (HifitApp) mContext.getApplicationContext();
        //app.getStepsDAO().insert(stepItem);
        app.getStepsDAO().update(stepItem);

        Toast.makeText(mContext, "存储到数据库 saveTodayStep:" + stepItem.date + "," + stepItem.step, Toast.LENGTH_LONG).show();
        LogUtil.d("存储到数据库 saveTodayStep:" + stepItem.date + "," + stepItem.step);

        app.getStepsDAO().queryStep();
    }

    public void initTodayStep(Date date, int step) {
        mBaseDate = getDateString(date);
        prefEditor.putString(PREFS_KEY_BASEDATE, mBaseDate);
        mBaseStep = step;
        prefEditor.putInt(PREFS_KEY_BASESTEP, mBaseStep);
        prefEditor.commit();
        Toast.makeText(mContext, "calcTodayStep initTodayStep:" + mBaseDate + "," + mBaseStep, Toast.LENGTH_LONG).show();
        LogUtil.d("calcTodayStep initTodayStep:" + mBaseDate + "," + mBaseStep);
    }

    public void saveStepBeforeBoot(Date date, int step) {
        prefEditor.putString(PREFS_KEY_STEP_BEFORE_DATE, getDateString(date));
        prefEditor.putInt(PREFS_KEY_STEP_BEFORE_BOOT, step);
        prefEditor.commit();
    }
}
