package com.cjx.helper;

import android.content.Context;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cjx.utils.CheckInput;
import com.cjx.utils.CheckNet;

/**
 * Created by CJX on 2016-8-8.
 */
public class LocateCity {

    private AMapLocationClient aMapLocationClient = null;
    private AMapLocationClientOption aMapLocationClientOption = null;

    public void getCity(Context context){
        if (CheckNet.isNetworkAvailable(context)){

            aMapLocationClient = new AMapLocationClient(context);
            aMapLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    if (aMapLocation != null){
                        if (onLocateCity != null){
                            onLocateCity.onSuccess(aMapLocation.getCity());
                        }
                    }
                }
            });

            aMapLocationClientOption = new AMapLocationClientOption();
            //设置定位模式为Hight_Accuracy高精度模式
            aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            aMapLocationClientOption.setNeedAddress(true);
            //设置是否只定位一次,默认为false
            aMapLocationClientOption.setOnceLocationLatest(true);
            //设置是否强制刷新WIFI，默认为强制刷新
            aMapLocationClientOption.setWifiActiveScan(true);

            aMapLocationClient.setLocationOption(aMapLocationClientOption);
            aMapLocationClient.startLocation();

        }else{
            Toast.makeText(context, "天气更新失败,请检查网络连接", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnLocateCity{
        void onSuccess(String city);
    }

    private OnLocateCity onLocateCity = null;

    public void setOnLocateCity(OnLocateCity onLocateCity) {
        this.onLocateCity = onLocateCity;
    }
}
