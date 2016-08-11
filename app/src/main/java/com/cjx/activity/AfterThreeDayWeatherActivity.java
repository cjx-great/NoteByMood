package com.cjx.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.cjx.R;
import com.cjx.adapter.ThreeDayWeatherListAdapter;
import com.cjx.bean.Weather;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取后三天天气
 * */
public class AfterThreeDayWeatherActivity extends AppCompatActivity {

    private Toolbar toolbar = null;
    private SwipeRefreshLayout pullToRefreshView = null;
    private ListView listView = null;

    private String city = null;

    private List<Weather> weathers = new ArrayList<>();
    private ThreeDayWeatherListAdapter adapter = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:

                    break;
                case 2:
                    pullToRefreshView.setRefreshing(false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_three_day_weather);

        initView();

        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        initData(1);

    }

    private void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("天气预报");
        setSupportActionBar(toolbar);
        //显示返回按钮并监听
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pullToRefreshView = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setSize(SwipeRefreshLayout.DEFAULT);
        pullToRefreshView.setColorSchemeColors(Color.GREEN,Color.BLUE,Color.YELLOW,Color.RED);
        pullToRefreshView.setProgressViewEndTarget(true,100);
        pullToRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData(2);
            }
        });

        listView = (ListView) findViewById(R.id.list_view);
    }

    /**
     * 获取后三天天气
     * */
    private void initData(final int flag){
        WeatherSearchQuery query = new WeatherSearchQuery(city,WeatherSearchQuery.WEATHER_TYPE_FORECAST);
        WeatherSearch search = new WeatherSearch(this);
        search.setQuery(query);
        //获取成功后回调
        search.setOnWeatherSearchListener(new WeatherSearch.OnWeatherSearchListener() {
            @Override
            public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {

            }

            @Override
            public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int code) {
                if (code == 1000){
                    weathers.clear();

                    LocalWeatherForecast forecast = localWeatherForecastResult.getForecastResult();
                    List<LocalDayWeatherForecast> weatherList = forecast.getWeatherForecast();
                    LocalDayWeatherForecast weatherForecast;

                    for (int i = 0; i < weatherList.size(); i++) {
                        Weather weather = new Weather();
                        weatherForecast = weatherList.get(i);
                        if (i == 0){
                            weather.setDate(weatherForecast.getDate() + "(今天)");
                        }else{
                            weather.setDate(weatherForecast.getDate());
                        }
                        weather.setDayWeather(weatherForecast.getDayWeather());
                        weather.setDayTemperator(weatherForecast.getDayTemp() + "℃");
                        weather.setDayWind(weatherForecast.getDayWindDirection() + " " + weatherForecast.getDayWindPower() + "级");

                        weather.setNightWeather(weatherForecast.getNightWeather());
                        weather.setNightTemperator(weatherForecast.getNightTemp() + "℃");
                        weather.setNightWind(weatherForecast.getNightWindDirection() + " " + weatherForecast.getNightWindPower() + "级");

                        weathers.add(weather);
                    }

                    adapter = new ThreeDayWeatherListAdapter(AfterThreeDayWeatherActivity.this,weathers);
                    listView.setAdapter(adapter);

                    handler.sendEmptyMessage(flag);
                }else{
                    Toast.makeText(AfterThreeDayWeatherActivity.this, "天气查询失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        search.searchWeatherAsyn();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
