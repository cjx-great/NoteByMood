package com.cjx;

import android.app.Application;
import android.graphics.Bitmap;
import android.view.View;

import com.cjx.helper.GetBackImgURL;
import com.cjx.helper.WeatherBackToFile;
import com.cjx.helper.WeatherService;
import com.cjx.interfaces.OnGetBackURListener;
import com.cjx.utils.CheckNet;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by CJX on 2016/8/7.
 */
public class MyApplication extends Application {

    private ImageLoader imageLoader = null;

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化ImagLoader
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        WeatherBackToFile.write(null,this);

    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}
