package com.cjx.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.cjx.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by CJX on 2016/8/7.
 */
public class WeatherBackToFile {

    //存储路径
    private final static String PARENT = "/sdcard/INote";
    private final static String PATH = "weatherBack.png";

    public static void write(Bitmap bitmap, Context context){
        File parent = new File(PARENT);
        if (!parent.exists()){
            parent.mkdir();
        }

        File path = new File(parent,PATH);
        if (!path.exists()){
            try {
                path.createNewFile();
                //务必保证有这张图片
                Bitmap b = BitmapFactory.decodeResource(context.getResources(),R.mipmap.weather_back);
                FileOutputStream outputStream = new FileOutputStream(path);
                b.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                outputStream.flush();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("WeatherBackToFile","新建文件失败");
            }
        }

        if (bitmap != null){
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(path);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("WeatherBackToFile","文件写失败");
            }
        }
    }

    public static Bitmap read(){
        Bitmap bitmap = null;
        try {
            FileInputStream inputStream = new FileInputStream(new File(PARENT,PATH));
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("WeatherBackToFile","文件读失败");
        }

        return bitmap;
    }
}
