package com.example.libnetwork;

import java.util.Map;

import okhttp3.FormBody;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/24 16:13
 * @类描述 ${TODO}
 */
public class PostRequest<T> extends Request<T,PostRequest> {
    public PostRequest(String url) {
        super(url);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        //post请求表单提交
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : mParams.entrySet()) {
            bodyBuilder.add(entry.getKey(),String.valueOf(entry.getValue()));
        }
        okhttp3.Request request = builder.url(mUrl).post(bodyBuilder.build()).build();
        return request;
    }
}
