package com.cjx.utils;

import android.text.TextUtils;

/**
 * Created by CJX on 2016/8/6.
 */
public class CheckInput {

    public static boolean isLegal(String text){
        if (TextUtils.isEmpty(text) || text == null){
            return false;
        }else {
            return true;
        }
    }
}
