package com.example.libnetwork;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/24 16:01
 * @类描述 ${TODO}
 */
public class UrlCreator {
    public static String createUrlFromParams(String url, HashMap<String, Object> params) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        if (url.indexOf("?") > 0 || url.indexOf("&") > 0) {
            stringBuilder.append("&");
        } else {
            stringBuilder.append("?");
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            try {
                String value = URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8");
                stringBuilder.append(entry.getKey()).append("=").append(value).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);//删除最后一个多余的&符号
        return stringBuilder.toString();
    }
}
