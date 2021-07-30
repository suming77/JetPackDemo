package com.example.jetpackdemo.utils;

import android.util.DisplayMetrics;

import com.example.libcommon.AppGlobals;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/30 12:00
 * @类描述 ${TODO}
 */
public class PixUtils {
    public static int dp2px(int dpValue) {
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return (int) (metrics.density * dpValue + 0.5f);
    }

    public static int getScreenWidth() {
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics metrics = AppGlobals.getApplication().getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
}
