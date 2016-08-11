package com.cjx.helper;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.cjx.interfaces.OnGetBackURListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CJX on 2016/8/7.
 *
 * Bing访问
 */
public class  GetBackImgURL {

    private OnGetBackURListener onGetBackURListener = null;

    /**
     * 获取图片背景的URL
     * */
    public void getURL(final Context context, int index, final String flag){
        final String path = "http://cn.bing.com/HPImageArchive.aspx?format=xml&idx=" + String.valueOf(index) + "&n=1";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(path).build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()){
                        Document document = Jsoup.parse(response.body().string());
                        String url = document.select("url").text();
                        if (onGetBackURListener != null){
                            if (flag.equals("P")){
                                onGetBackURListener.onPreviousSuccess(url);
                            }else if (flag.equals("R")){
                                onGetBackURListener.onRefreshSuccess(url);
                            }else if (flag.equals("N")){
                                onGetBackURListener.onNextSuccess(url);
                            }
                        }

                    }else{
                        showToast(context);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("GetBackImgURL","error-----1");
                    showToast(context);
                }

                Looper.loop();
            }
        }).start();

    }

    private void showToast(Context context){
        Toast.makeText(context, "背景图片获取失败，刷新看看", Toast.LENGTH_SHORT).show();
    }

    public void setOnGetBackURListener(OnGetBackURListener onGetBackURListener) {
        this.onGetBackURListener = onGetBackURListener;
    }
}
