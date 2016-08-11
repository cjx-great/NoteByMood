package com.cjx.helper;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by CJX on 2016-8-8.
 */
public class WeatherBroadCast extends BroadcastReceiver {

    private boolean isServiceRunning = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_TIME_TICK)){
            //检查Service状态
            ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)){
                if("com.cjx.helper.WeatherService".equals(service.service.getClassName())){
                    isServiceRunning = true;
                }
            }

            if (!isServiceRunning) {
                Intent i = new Intent(context, WeatherService.class);
                context.startService(i);
            }
        }
    }
}
