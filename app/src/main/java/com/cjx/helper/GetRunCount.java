package com.cjx.helper;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by CJX on 2016-8-11.
 *
 * 体会Cookie的使用
 */
public class GetRunCount {

    private static OkHttpClient okHttpClient = null;

    private static String COOKIE = "";

    private static GetRunCount getRunCount = null;

    public GetRunCount(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public void get(final String username, final String password){
        new Thread(new Runnable() {
            @Override
            public void run() {

                RequestBody body = new FormBody.Builder()
                        .add("username",username)
                        .add("password",password)
                        .add("btnlogin.x","39")
                        .add("btnlogin.y","14")
                        .build();
                Request getCookieRequest = new Request.Builder()
                        .url("http://211.69.129.116:8501/login.do")
                        .addHeader("Accept","text/html, application/xhtml+xml, */*")
                        .addHeader("Referer","http://211.69.129.116:8501/login.do")
                        .addHeader("Accept-Language","zh-CN")
                        .addHeader("Content-Type","application/x-www-form-urlencoded")
                        .addHeader("Host","211.69.129.116:8501")
                        .post(body)
                        .build();
                try {
                    Response getCookieResponse = okHttpClient.newCall(getCookieRequest).execute();
                    if (getCookieResponse.isSuccessful()){
                        List<String> cookies = getCookieResponse.headers("Set-Cookie");
                        for(String cookie : cookies){
                            Log.v("----",cookie);
                            COOKIE = cookie.substring(0,cookie.indexOf(";"));
                            Log.v("--cookie--",COOKIE);
                        }

                        //------------------------------------------------------------------

                        Request request = new Request.Builder()
                                .url("http://211.69.129.116:8501/stu/StudentSportModify.do")
                                .addHeader("Accept","text/html, application/xhtml+xml, */*")
                                .addHeader("Referer","http://211.69.129.116:8501/jsp/menuForwardContent.jsp?url=stu/StudentSportModify.do")
                                .addHeader("Accept-Language","zh-CN")
                                .addHeader("Host","211.69.129.116:8501")
                                .addHeader("Cookie",COOKIE)
                                .build();

                        Response response = okHttpClient.newCall(request).execute();
                        if (response.isSuccessful()){
                            Log.v("----","8888");

                            InputStream inputStream = response.body().byteStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
                            final StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null){
                                builder.append(line + "\n");
                            }

                            Log.v("----",builder.toString());

                            Document document = Jsoup.parse(builder.toString());
                            Element element = null;

                            try {
                                element = document.getElementsByClass("fd").get(7);
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (onGetRunCountListener != null){
                                    onGetRunCountListener.onFailed("查询失败");
                                }

                                return;
                            }

                            if (onGetRunCountListener != null){
                                onGetRunCountListener.onSuccess(element.text());
                            }

                        }else{
                            Log.v("----","查询失败");
                            if (onGetRunCountListener != null){
                                onGetRunCountListener.onFailed("查询失败");
                            }
                        }
                    }else {
                        Log.v("----","登录失败");
                        if (onGetRunCountListener != null){
                            onGetRunCountListener.onFailed("查询失败");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface OnGetRunCountListener{
        void onSuccess(String count);
        void onFailed(String me);
    }

    private OnGetRunCountListener onGetRunCountListener = null;

    public void setOnGetRunCountListener(OnGetRunCountListener onGetRunCountListener) {
        this.onGetRunCountListener = onGetRunCountListener;
    }
}
