package com.cjx.bean;

import android.widget.TextView;

/**
 * Created by CJX on 2016-8-8.
 */
public class Weather {
    private String date;
    private String dayWeather;
    private String dayTemperator;
    private String dayWind;
    private String nightWeather;
    private String nightTemperator;
    private String nightWind;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayWeather() {
        return dayWeather;
    }

    public void setDayWeather(String dayWeather) {
        this.dayWeather = dayWeather;
    }

    public String getDayTemperator() {
        return dayTemperator;
    }

    public void setDayTemperator(String dayTemperator) {
        this.dayTemperator = dayTemperator;
    }

    public String getDayWind() {
        return dayWind;
    }

    public void setDayWind(String dayWind) {
        this.dayWind = dayWind;
    }

    public String getNightWeather() {
        return nightWeather;
    }

    public void setNightWeather(String nightWeather) {
        this.nightWeather = nightWeather;
    }

    public String getNightTemperator() {
        return nightTemperator;
    }

    public void setNightTemperator(String nightTemperator) {
        this.nightTemperator = nightTemperator;
    }

    public String getNightWind() {
        return nightWind;
    }

    public void setNightWind(String nightWind) {
        this.nightWind = nightWind;
    }
}
