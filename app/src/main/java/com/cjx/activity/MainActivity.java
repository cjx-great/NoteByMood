package com.cjx.activity;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.cjx.MyApplication;
import com.cjx.R;
import com.cjx.adapter.ViewPagerAdapter;
import com.cjx.fragment.LifeFragment;
import com.cjx.fragment.StudyFragment;
import com.cjx.fragment.TrainFragment;
import com.cjx.helper.GetBackImgURL;
import com.cjx.helper.LocateCity;
import com.cjx.helper.WeatherBackToFile;
import com.cjx.helper.WeatherBroadCast;
import com.cjx.helper.WeatherService;
import com.cjx.interfaces.OnGetBackURListener;
import com.cjx.utils.CheckNet;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rey.material.widget.ProgressView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OnGetBackURListener,WeatherService.OnGetNewWeather{

    private static long current_time = 0;      //记录系统当前时间

    private MyApplication myApplication = null;

    private Toolbar toolbar = null;
    private AppBarLayout appBarLayout = null;
    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    private FrameLayout weatherLayout = null;

    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    private ViewPagerAdapter adapter = null;

    //天气
    private ImageView weatherBack = null;
    private TextView city = null;
    private TextView seeMore = null;
    private TextView date = null;
    private TextView weather = null;
    private TextView temperature = null;
    private TextView wind = null;
    private TextView humidity = null;

    private LocateCity locateCity = null;
    private WeatherSearchQuery weatherSearchQuery = null;
    private WeatherSearch weatherSearch = null;

    //壁纸
    private ImageView previous = null;
    private ImageView refresh = null;
    private ImageView next = null;
    private TextView setScreen = null;

    private ProgressView progress = null;

    //标记当前是第几张图片
    private static int index = 0;

    private ImageLoader imageLoader = null;

    private GetBackImgURL getBackImgURL = null;

    private WeatherService weatherService = null;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WeatherService.WeatherServiceBinder binder = (WeatherService.WeatherServiceBinder) iBinder;
            weatherService = binder.getService();
            weatherService.setOnGetNewWeather(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            weatherService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        myApplication = (MyApplication) getApplication();
        imageLoader = myApplication.getImageLoader();

        //绑定服务
        bindService(new Intent(this,WeatherService.class),this.serviceConnection,BIND_AUTO_CREATE);
        //注册广播
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        WeatherBroadCast broadCast = new WeatherBroadCast();
        registerReceiver(broadCast,filter);
    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //默认左上角没有返回图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        weatherLayout = (FrameLayout) findViewById(R.id.weather_layout);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -weatherLayout.getHeight()/2){
                    collapsingToolbarLayout.setTitle("INote");
                }else{
                    collapsingToolbarLayout.setTitle("");
                }
            }
        });

        //-------------------------------------------------
        tabLayout = (TabLayout) findViewById(R.id.tab);

        viewPager = (ViewPager) findViewById(R.id.content_viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TrainFragment(),"运动");
        adapter.addFragment(new StudyFragment(),"学习");
        adapter.addFragment(new LifeFragment(),"生活");
        viewPager.setAdapter(adapter);
        //设置3页缓存
        viewPager.setOffscreenPageLimit(3);

        tabLayout.setupWithViewPager(viewPager);
        //-------------------------------------------------

        progress = (ProgressView) findViewById(R.id.weather_progress);

        weatherBack = (ImageView) findViewById(R.id.weather_back);
        weatherBack.setImageBitmap(WeatherBackToFile.read());

        city = (TextView) findViewById(R.id.city);
        seeMore = (TextView) findViewById(R.id.see_more);
        seeMore.setOnClickListener(this);
        date = (TextView) findViewById(R.id.date);
        weather = (TextView) findViewById(R.id.weather);
        temperature = (TextView) findViewById(R.id.temperature);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);

        //回调
        getBackImgURL = new GetBackImgURL();
        getBackImgURL.setOnGetBackURListener(this);

        previous = (ImageView) findViewById(R.id.previous);
        previous.setOnClickListener(this);

        refresh = (ImageView) findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        next = (ImageView) findViewById(R.id.next);
        next.setOnClickListener(this);

        setScreen = (TextView) findViewById(R.id.set_screen);
        setScreen.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        if((System.currentTimeMillis() - current_time) > 2000){
            Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
            current_time = System.currentTimeMillis();
        } else{
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.previous:
                getURL(index + 1,"P");
                break;
            case R.id.refresh:
                getURL(index,"R");
                break;
            case R.id.next:
                getURL(index - 1,"N");
                break;
            case R.id.set_screen:
                new AlertDialogWrapper.Builder(this)
                        .setTitle("设置壁纸")
                        .setMessage("该操作会修改您的壁纸,是否继续?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Bitmap bitmap = WeatherBackToFile.read();
                                WallpaperManager  manager = WallpaperManager.getInstance(MainActivity.this);
                                try {
                                    manager.setBitmap(bitmap);
                                    Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "壁纸设置失败，请重试", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                break;
            case R.id.see_more:
                //获取后三天天气
                String cityname = city.getText().toString();
                if (cityname != null && !cityname.equals("")){
                    if (CheckNet.isNetworkAvailable(getApplicationContext())){
                        Intent intent = new Intent(this,AfterThreeDayWeatherActivity.class);
                        intent.putExtra("city",cityname);
                        startActivity(intent);
                    }
                }else{
                    Toast.makeText(MainActivity.this, "网络无连接", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 获取图片URL
     * */
    private void getURL(int index,String flag){

        if (CheckNet.isNetworkAvailable(this)){
            if (flag.equals("P")){
                if (index <= 17){
                    progress.setVisibility(View.VISIBLE);
                    getBackImgURL.getURL(this,index,flag);
                }else{
                    Toast.makeText(MainActivity.this, "没有上一张图片", Toast.LENGTH_SHORT).show();
                }

            }else if (flag.equals("R")){
                progress.setVisibility(View.VISIBLE);
                getBackImgURL.getURL(this,index,flag);

                //刷新天气
                locateCity.getCity(getApplicationContext());

            }else if (flag.equals("N")){
                if (index >= -1){
                    progress.setVisibility(View.VISIBLE);
                    getBackImgURL.getURL(this,index,flag);
                }else{
                    Toast.makeText(MainActivity.this, "没有下一张图片", Toast.LENGTH_SHORT).show();
                }
            }

        }else{
            Toast.makeText(MainActivity.this, "网络无连接", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPreviousSuccess(String url) {
        setBack(url);
        index ++;
    }

    @Override
    public void onRefreshSuccess(String url) {
        setBack(url);
    }

    @Override
    public void onNextSuccess(String url) {
        setBack(url);
        index --;
    }

    private void setBack(final String url){
        final String start = "http://s.cn.bing.net";
        imageLoader.loadImage(start + url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                progress.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "图片显示失败，刷新看看", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadingComplete(String s, View view, final Bitmap bitmap) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weatherBack.setImageBitmap(bitmap);
                        progress.setVisibility(View.GONE);

                        WeatherBackToFile.write(bitmap,MainActivity.this);
                    }
                });
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    /**
     * 显示天气信息
     * */
    private void setWeather(String cityName){
        city.setText(cityName);

        //检索参数为城市和天气类型，实况天气为WEATHER_TYPE_LIVE、天气预报为WEATHER_TYPE_FORECAST
        weatherSearchQuery = new WeatherSearchQuery(cityName, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        weatherSearch = new WeatherSearch(MainActivity.this);
        weatherSearch.setOnWeatherSearchListener(new WeatherSearch.OnWeatherSearchListener() {
            @Override
            public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
                if (i == 1000){
                    if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null){
                        LocalWeatherLive result = localWeatherLiveResult.getLiveResult();
                        date.setText(result.getReportTime());
                        weather.setText("天气：" + result.getWeather());
                        temperature.setText("温度：" + result.getTemperature() + "℃");
                        wind.setText("风向：" + result.getWindDirection() + "风 " + result.getWindPower() + "级");
                        humidity.setText("湿度：" + result.getHumidity() + "%");
                    }
                }else {
                    Toast.makeText(MainActivity.this, "天气查询失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

            }
        });
        weatherSearch.setQuery(weatherSearchQuery);
        //异步搜索
        weatherSearch.searchWeatherAsyn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //获取天气信息
        locateCity = new LocateCity();
        locateCity.setOnLocateCity(new LocateCity.OnLocateCity() {
            @Override
            public void onSuccess(String city) {
                if (city != null){
                    setWeather(city);
                }
            }
        });

        locateCity.getCity(getApplicationContext());

    }

    /**
     * 服务回调
     * */
    @Override
    public void onGetNew(String city) {
        setWeather(city);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解绑服务
        unbindService(serviceConnection);
    }
}
