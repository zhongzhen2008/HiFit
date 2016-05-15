package com.hifit.zz.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hifit.zz.engine.StepService;
import com.hifit.zz.hifit.R;


/**
 * Created by zz on 2016/5/8.
 */
public class FitFragment extends Fragment {

    private static final String TAG_FIT_FRAGMENT = "FitFragment";
    private static final String ACT_STEP_SENSOE = "com.hifit.zz.engine.StepService.STEP_SENSOR";

    private View rootView;
    private CardView cvSteps;
    private TextView tvTodaySteps;
    private TextView tvTargetSteps;
    private ProgressBar pbTodaySteps;
    private CardView cvRun;
    private Button btRun;

    private StepService.StepBinder mStepBinder;
    private StepChange mStepChange = new StepChange();

    private int mTargetStep;
    private int mTodayStep;

    private class StepChange implements StepService.StepListener {
        @Override
        public void onStepChanged(int step) {
            //Toast.makeText(getActivity(), "StepChange: " + step, Toast.LENGTH_SHORT).show();
            tvTodaySteps.setText(String.valueOf(step));
        }
    }

    private ServiceConnection connStep = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG_FIT_FRAGMENT, "***** FitFragment: onServiceConnected()");
            mStepBinder = (StepService.StepBinder) service;
            updateTodayStep();

            mStepBinder.addStepListener(mStepChange);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG_FIT_FRAGMENT, "***** FitFragment: onServiceDisconnected()");
        }
    };

    public static FitFragment newInstance() {
        FitFragment fragment = new FitFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fit, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intentStep = new Intent();
        intentStep.setAction(ACT_STEP_SENSOE);
        intentStep.setPackage(getActivity().getPackageName());
        //getActivity().startService(intentStep);
        getActivity().bindService(intentStep, connStep, Service.BIND_AUTO_CREATE);

        tvTodaySteps = (TextView) rootView.findViewById(R.id.tv_today_steps);
        tvTargetSteps = (TextView) rootView.findViewById(R.id.tv_target_steps);
        //tvTargetSteps.setText("" + getTargetStep());

        pbTodaySteps = (ProgressBar) rootView.findViewById(R.id.pb_today_steps);
        //pbTodaySteps.setMax(getTargetStep());
        //pbTodaySteps.setProgress(Integer.parseInt((String)tvTodaySteps.getText()));

        cvSteps = (CardView) rootView.findViewById(R.id.card_view_step);
        cvSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StepHistroyActivity.class);
                startActivity(intent);
            }
        });

        cvRun = (CardView) rootView.findViewById(R.id.card_view_run);
        cvRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RunHistroyActivity.class);
                startActivity(intent);
            }
        });

        btRun = (Button) rootView.findViewById(R.id.bt_start_run);
        btRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RunActivity.class);
                startActivity(intent);
/*
                if (mStepBinder != null) {
                    Log.d(TAG_FIT_FRAGMENT, "***** FitFragment: btRun clicked");
                    tvTodaySteps.setText(mStepBinder.getTodayStep());
                }*/
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTargetStep();
        updateTodayStep();

//        ClipDrawable d = new ClipDrawable(new ColorDrawable(Color.BLUE), Gravity.LEFT, ClipDrawable.HORIZONTAL);
//        pbTodaySteps.setProgressDrawable(d);
    }

    private void updateTargetStep() {
        mTargetStep = getTargetStep();
        tvTargetSteps.setText(" /" + mTargetStep);
        pbTodaySteps.setMax(mTargetStep);
        if (mStepBinder != null) {
            mStepBinder.setTargetStep(mTargetStep);
        }
    }

    private void updateTodayStep() {
        if (mStepBinder != null) {
            mTodayStep =  mStepBinder.getTodayStep();
            tvTodaySteps.setText("" + mTodayStep);
            pbTodaySteps.setProgress(mTodayStep);
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(connStep);
        super.onDestroy();
    }

    public int getTargetStep() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        String strStep = settings.getString(SettingsFragment.SETTINGS_TARGET_STEP_KEY, "7000");
        return Integer.parseInt(strStep);
    }


}
