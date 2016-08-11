package com.cjx.helper;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.cjx.utils.CheckNet;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by CJX on 2016-8-8.
 */
public class WeatherService extends Service implements LocateCity.OnLocateCity{

    private IBinder mBinder = new WeatherServiceBinder();

    private LocateCity locateCity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        locateCity = new LocateCity();
        locateCity.setOnLocateCity(WeatherService.this);

        final Handler handler = new Handler();
        //每隔一小时执行一次
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (CheckNet.isNetworkAvailable(getApplicationContext())){

                    locateCity.getCity(getApplicationContext());

                }else{
                    Toast.makeText(getApplicationContext(), "网络无连接", Toast.LENGTH_SHORT).show();
                }
                handler.postDelayed(this,3600 * 1000);
            }
        },3600 * 1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onSuccess(String city) {
        if (onGetNewWeather != null){
            onGetNewWeather.onGetNew(city);
        }
    }

    public class WeatherServiceBinder extends Binder{

        public WeatherService getService(){
            return WeatherService.this;
        }
    }

    /**
     * 获取到新天气后提供给外界的接口
     * */
    public interface OnGetNewWeather{
        void onGetNew(String city);
    }

    private OnGetNewWeather onGetNewWeather = null;

    public void setOnGetNewWeather(OnGetNewWeather onGetNewWeather) {
        this.onGetNewWeather = onGetNewWeather;
    }
}
