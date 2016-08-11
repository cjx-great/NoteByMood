package com.cjx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cjx.R;
import com.cjx.bean.Weather;

import java.util.List;

/**
 * Created by CJX on 2016-8-8.
 */
public class ThreeDayWeatherListAdapter extends BaseAdapter {

    private Context context = null;
    private List<Weather> weatherList = null;

    public ThreeDayWeatherListAdapter(Context context, List<Weather> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    @Override
    public int getCount() {
        return weatherList == null ? 0 : weatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return weatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.three_day_weather_item,null);
            holder = new ViewHolder();
            holder.date = (TextView) view.findViewById(R.id.three_date);
            holder.dayWeather = (TextView) view.findViewById(R.id.day_weather);
            holder.dayTemperator = (TextView) view.findViewById(R.id.day_temperature);
            holder.dayWind = (TextView) view.findViewById(R.id.day_wind);
            holder.nightWeather = (TextView) view.findViewById(R.id.night_weather);
            holder.nightTemperator = (TextView) view.findViewById(R.id.night_temperature);
            holder.nightWind = (TextView) view.findViewById(R.id.night_wind);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        Weather weather = weatherList.get(position);
        holder.date.setText(weather.getDate());
        holder.dayWeather.setText("天气：" + weather.getDayWeather());
        holder.dayTemperator.setText("温度：" + weather.getDayTemperator());
        holder.dayWind.setText("风向：" + weather.getDayWind());

        holder.nightWeather.setText("天气：" + weather.getNightWeather());
        holder.nightTemperator.setText("温度：" + weather.getNightTemperator());
        holder.nightWind.setText("风向：" + weather.getNightWind());

        return view;
    }

    class ViewHolder{
        TextView date;
        TextView dayWeather;
        TextView dayTemperator;
        TextView dayWind;
        TextView nightWeather;
        TextView nightTemperator;
        TextView nightWind;
    }
}
