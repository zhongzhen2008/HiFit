package com.hifit.zz.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hifit.zz.hifit.BuildConfig;
import com.hifit.zz.hifit.R;

/**
 * Created by zz on 2016/5/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG_SETTINGS_FRAGMENT = "SettingsFragment";

    public static final String SETTINGS_TARGET_STEP_KEY = "targetStep";
    public static final int SETTINGS_TARGET_STEP_DEFAULT = 7000;

    private EditTextPreference mTargetStepPref;
    private int mTargetStep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_SETTINGS_FRAGMENT, "***** SettingsFragment onCreate()");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        mTargetStepPref = (EditTextPreference)getPreferenceScreen().findPreference(SETTINGS_TARGET_STEP_KEY);
        mTargetStep = SETTINGS_TARGET_STEP_DEFAULT;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SETTINGS_TARGET_STEP_KEY)) {
            updateTargetStepSummary();
        }
    }

    private void updateTargetStepSummary() {
        //Log.d(TAG_SETTINGS_FRAGMENT, "updateTargetStepSummary");

        try {
            mTargetStep = Integer.parseInt(mTargetStepPref.getText());
            mTargetStepPref.setSummary(mTargetStep + getActivity().getString(R.string.step_uint));
            Log.d(TAG_SETTINGS_FRAGMENT, "***** updateTargetStepSummary:" + mTargetStep);
        } catch (Exception e) {
            Log.d(TAG_SETTINGS_FRAGMENT, "***** updateTargetStepSummary Exception: " + e.toString());
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG_SETTINGS_FRAGMENT, "***** SettingsFragment onResume()");
        super.onResume();
        updateTargetStepSummary();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        Log.d(TAG_SETTINGS_FRAGMENT, "***** SettingsFragment onPause()");
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

}
