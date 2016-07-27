package com.hifit.zz.engine;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.hifit.zz.activity.MainActivity;
import com.hifit.zz.activity.SettingsFragment;
import com.hifit.zz.hifit.R;
import com.hifit.zz.utils.LogUtil;

import java.util.Date;


public class StepService extends Service implements SensorEventListener {

    private static final String TAG_STEP_SERVICE = "StepService";
    private static final int NOTIFY_ID = 0x111;

    private SensorManager sensorManager;

    private StepBinder mBinder = new StepBinder();
    private StepListener mStepListener;

    private CalcBaseStep mCalcBaseStep;
    private int mTodayStep = 0;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private Notification noti;
    private int mTargetStep = SettingsFragment.SETTINGS_TARGET_STEP_DEFAULT;

    @Override
    public void onCreate() {
        Log.d(TAG_STEP_SERVICE, "****** StepService onCreate()");

        super.onCreate();

        mCalcBaseStep = new CalcBaseStep(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG_STEP_SERVICE, "****** StepService onStartCommand()");
        //super.onStartCommand(intent, flags, startId);

        if (intent != null) {
            String strBoot = intent.getStringExtra(BootCompletedReceiver.KEY_BOOT_SHUTDOWN);
            if (strBoot == null) {
                return START_STICKY;
            }

            if (strBoot.equals(BootCompletedReceiver.VALUE_BOOT)) {
                Log.d(TAG_STEP_SERVICE, "****** StepService onStartCommand() VALUE_BOOT");
                Date date = new Date();
                mCalcBaseStep.initTodayStep(date, 0);
            } else if (strBoot.equals(BootCompletedReceiver.VALUE_SHUTDOWN)) {
                Log.d(TAG_STEP_SERVICE, "****** StepService onStartCommand() VALUE_SHUTDOWN");
                Date date = new Date();
                mCalcBaseStep.saveStepBeforeBoot(date, mTodayStep);
            }
        }

        return START_STICKY;
    }

    private void showNotification() {
        Bitmap btm = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launch);
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launch)
                        .setLargeIcon(btm)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// NOTIFY_ID allows you to update the notification later on.
        noti = mBuilder.build();
        mNotificationManager.notify(NOTIFY_ID, noti);
        startForeground(NOTIFY_ID, noti);
    }

    private void updateNotification(int step) {
        mBuilder.setContentTitle("" + step + " " + getString(R.string.step_uint));
        if (step < mTargetStep) {
            mBuilder.setContentText(getString(R.string.step_to_target)
                    + " " + (mTargetStep - step) + " " + getApplication().getText(R.string.step_uint));
        } else {
            mBuilder.setContentText(getApplication().getText(R.string.step_reach_target));
        }

        noti = mBuilder.build();
        mNotificationManager.notify(NOTIFY_ID, noti);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG_STEP_SERVICE, "****** StepService onBind()");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG_STEP_SERVICE, "****** StepService onUnbind()");
        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG_STEP_SERVICE, "****** StepService onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG_STEP_SERVICE, String.valueOf(event.values[0]));
        LogUtil.d(String.valueOf(event.values[0]));
        //Toast.makeText(this, "Step: " +(int)(event.values[0]), Toast.LENGTH_SHORT).show();
        Date date = new Date();
        int step = (int) event.values[0];
        mTodayStep = mCalcBaseStep.calcTodayStep(date, step);

        updateNotification(mTodayStep);

        if (mStepListener != null) {
            mStepListener.onStepChanged(mTodayStep);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public interface StepListener {
        public void onStepChanged(int step);
    }

    public class StepBinder extends Binder {
        public int getTodayStep() {
            return mTodayStep;
        }

        public void setTargetStep(int step) {
            mTargetStep = step;
            updateNotification(mTodayStep);
        }

        public void addStepListener(StepListener listener) {
            mStepListener = listener;
        }
    }

}
