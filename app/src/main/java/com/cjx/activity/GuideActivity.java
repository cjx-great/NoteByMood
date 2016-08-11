package com.cjx.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.cjx.R;


/**
 * 地图定位
 * */
public class GuideActivity extends AppCompatActivity {

    private MapView mapView = null;

    private AMap aMap = null;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationClientOption = null;
    private LocationSource.OnLocationChangedListener onLocationChangedListener = null;

    private MyLocationStyle myLocationStyle = null;

    private boolean isFirstLoc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);

        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mapView.onCreate(savedInstanceState);

        init();
    }

    private void init(){
        if (aMap == null){
            aMap = mapView.getMap();
            //设置显示定位按钮 并且可以点击
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            //设置指南针
            aMap.getUiSettings().setCompassEnabled(true);
            //显示定位层并且可以触发定位,默认是flase
            aMap.setMyLocationEnabled(true);

            //自定义图标
            myLocationStyle = new MyLocationStyle();
            myLocationStyle.strokeColor(Color.BLUE);
            // 设置圆形的填充颜色
            myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.locate));
            aMap.setMyLocationStyle(myLocationStyle);

            //激活定位
            aMap.setLocationSource(new LocationSource() {
                @Override
                public void activate(OnLocationChangedListener _onLocationChangedListener) {
                    onLocationChangedListener = _onLocationChangedListener;
                }

                @Override
                public void deactivate() {
                    onLocationChangedListener = null;
                }
            });
        }

        locationClient = new AMapLocationClient(getApplicationContext());
        //定位回调监听
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null && onLocationChangedListener != null){
                    if (aMapLocation.getErrorCode() == 0){
                        //aMapLocation获取地址信息

                        // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                        if (isFirstLoc){
                            //设置缩放级别
                            aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
                            //将地图移动到定位点
                            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(),
                                    aMapLocation.getLongitude())));

                            onLocationChangedListener.onLocationChanged(aMapLocation);
                            isFirstLoc = false;
                        }
                    }
                }
            }
        });

        locationClientOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClientOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        locationClientOption.setOnceLocationLatest(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        locationClientOption.setWifiActiveScan(true);
        //设置定位间隔,单位毫秒,默认为2000ms
        locationClientOption.setInterval(2000);

        locationClient.setLocationOption(locationClientOption);
        locationClient.startLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        locationClient.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
