package com.example.libnetwork;


/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/24 15:53
 * @类描述 ${TODO}
 */
public class GetRequest<T> extends Request<T, GetRequest> {
    public GetRequest(String url) {
        super(url);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        //get 请求把参数拼接在 url后面
        String url = UrlCreator.createUrlFromParams(mUrl, mParams);
        okhttp3.Request request = builder.get().url(url).build();
        return request;
    }
}
