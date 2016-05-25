package com.hifit.zz.activity;

import android.app.Application;

import com.hifit.zz.db.HiFitDbHelper;
import com.hifit.zz.db.StepsDAO;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by zz on 2016/5/22.
 */
public class HifitApp extends Application {

    private HiFitDbHelper mHiFitDbHelper;
    private StepsDAO mStepsDAO;

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);

        mHiFitDbHelper = new HiFitDbHelper(this);
        mStepsDAO = new StepsDAO(mHiFitDbHelper);
    }

    public StepsDAO getStepsDAO() {
        return mStepsDAO;
    }
}
