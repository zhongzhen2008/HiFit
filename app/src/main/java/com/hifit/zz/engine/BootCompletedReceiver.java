package com.hifit.zz.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by zz on 2016/5/14.
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG_BOOT_RECEIVER = "BootCompletedReceiver";
    public static final String KEY_BOOT_SHUTDOWN = "BootShutdown";
    public static final String VALUE_BOOT = "Boot";
    public static final String VALUE_SHUTDOWN = "Shutdown";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG_BOOT_RECEIVER, "***** recevie boot completed ... ");
            Intent intentBoot = new Intent(context, StepService.class);
            intentBoot.putExtra(KEY_BOOT_SHUTDOWN, VALUE_BOOT);
            context.startService(intentBoot);
        } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            Log.d(TAG_BOOT_RECEIVER, "***** recevie shutdown ... ");
            Intent intentBoot = new Intent(context, StepService.class);
            intentBoot.putExtra(KEY_BOOT_SHUTDOWN, VALUE_SHUTDOWN);
            context.startService(intentBoot);
        }
    }
}
