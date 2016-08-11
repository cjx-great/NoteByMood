package com.cjx.interfaces;

/**
 * Created by CJX on 2016/8/7.
 */
public interface OnGetBackURListener {

    void onPreviousSuccess(String url);
    void onRefreshSuccess(String url);
    void onNextSuccess(String url);

}
