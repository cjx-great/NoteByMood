package com.cjx.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CJX on 2016/8/6.
 */
public class TimeFormat {

    public static String getTime(){
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = new Date(time);

        return format.format(d);
    }
}
