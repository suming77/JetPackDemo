package com.example.jetpackdemo.utils;

import android.content.res.AssetManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.jetpackdemo.model.BottomBar;
import com.example.jetpackdemo.model.Destination;
import com.example.libcommon.AppGlobals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/23 10:57
 * @类描述 ${TODO}
 */
public class AppConfig {
    private static HashMap<String, Destination> sDesConfig;
    private static BottomBar sBottomBar;

    public static HashMap<String, Destination> getDesConfig() {
        if (sDesConfig == null) {
            String content = paresFile("destination.json");
            sDesConfig = JSON.parseObject(content, new TypeReference<HashMap<String, Destination>>() {
            }.getType());
        }
        return sDesConfig;
    }

    public static BottomBar getBottomBarConfig(){
        if (sBottomBar==null){

            String content = paresFile("main_tabs_config.json");
            sBottomBar = JSON.parseObject(content, BottomBar.class);
        }
        return sBottomBar;
    }
    private static String paresFile(String fileName) {
        AssetManager assets = AppGlobals.getApplication().getResources().getAssets();

        InputStream stream = null;
        BufferedReader reader = null;
        StringBuffer stringBuffer = new StringBuffer();
        try {
            stream = assets.open(fileName);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stringBuffer.toString();
    }
}
