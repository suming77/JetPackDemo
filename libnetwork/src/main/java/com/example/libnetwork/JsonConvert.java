package com.example.libnetwork;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/24 17:16
 * @类描述 ${TODO}
 */
public class JsonConvert implements Convert {
    @Override
    public Object convert(String response, Type type) {
        JSONObject jsonObject = JSON.parseObject(response);
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null) {
            Object d = data.get("data");
            return JSON.parseObject(d.toString(), type);
        }
        return null;
    }

    @Override
    public Object convert(String response, Class cls) {
        JSONObject jsonObject = JSON.parseObject(response);
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null) {
            Object d = data.get("data");
            return JSON.parseObject(d.toString(), cls);
        }
        return null;
    }
}
