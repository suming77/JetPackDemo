package com.example.jetpackdemo;

import android.app.Application;

import com.example.libnetwork.ApiService;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/07/01 17:54
 * @类描述 ${TODO}
 */
public class JPApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiService.init("http://123.56.232.18:8080/serverdemo",null);
    }
}
