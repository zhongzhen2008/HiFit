package com.hifit.zz.activity;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by zz on 2016/5/22.
 */
public class HifitApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
