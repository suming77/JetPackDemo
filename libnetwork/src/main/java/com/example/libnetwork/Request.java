package com.example.libnetwork;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.libcommon.AppGlobals;
import com.example.libnetwork.cache.CacheManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/24 15:16
 * @类描述 ${TODO}
 */
public abstract class Request<T, R extends Request> implements Cloneable {
    protected String mUrl;
    protected HashMap<String, String> mHeaders = new HashMap<>();
    protected HashMap<String, Object> mParams = new HashMap<>();

    //仅仅访问本地缓存，不网络请求
    public static final int CACHE_ONLY = 1;
    //先访问网络请求，同时发起网络请求，成功后缓存到本地
    public static final int CACHE_FIRST = 2;
    //仅仅进行网络请求，不做任何缓存
    public static final int NET_ONLY = 3;
    //先访问网络，成功后缓存到本地
    public static final int NET_CACHE = 4;
    private String mCacheKey;
    private Type mType;
    private Class mCls;
    private int mCacheStrategy = CACHE_ONLY;

    //使用注解标记这几种类型
    @IntDef({CACHE_ONLY, CACHE_FIRST, NET_ONLY, NET_CACHE})
    public @interface CacheStrategy {

    }

    public Request(String url) {
        mUrl = url;
    }

    public R addHeader(String key, String value) {
        mHeaders.put(key, value);
        return (R) this;
    }

    public R addParams(String key, Object value) {
        //8中基本类型，怎么判断是不是8中基本类型中的一个
        //通过反射获取封装类型中的TYPE判断
        if (value == null) {
            return (R) this;
        }
        try {
            //int byte char short long double float boolean 和他们的包装类型，但是除了 String.class 所以要额外判断
            Field field = value.getClass().getField("TYPE");
            Class claz = (Class) field.get(null);
            if (claz.isPrimitive()) {//是基本类型
                mParams.put(key, value);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (R) this;
    }

    public R cacheKey(String key) {
        mCacheKey = key;
        return (R) this;
    }

    public R cacheStrategy(@CacheStrategy int cacheStrategy) {
        mCacheStrategy = cacheStrategy;
        return (R) this;
    }

    private void addHeaders(okhttp3.Request.Builder builder) {
        for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    protected abstract okhttp3.Request generateRequest(okhttp3.Request.Builder builder);

    public R responseType(Class cls) {
        mCls = cls;
        return (R) this;
    }

    public R responseType(Type type) {
        mType = type;
        return (R) this;
    }

    private Call getCall() {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        addHeaders(builder);
        //这里构造的一个requestBody，GET和POST的是不一样的，通过子类来完成requestBody的构建工作
        okhttp3.Request request = generateRequest(builder);
        Call call = ApiService.sOkHttpClient.newCall(request);
        return call;
    }

    public ApiResponse<T> execute() {
        if (mType == null) {
            throw new RuntimeException("同步方法,response 返回值 类型必须设置");
        }

        if (mCacheStrategy == CACHE_ONLY) {
            return readCache();
        }

        if (mCacheStrategy != CACHE_ONLY) {
            ApiResponse<T> result = null;
            try {
                Response response = getCall().execute();
                result = parseResponse(response, null);
            } catch (IOException e) {
                e.printStackTrace();
                if (result == null) {
                    result = new ApiResponse<>();
                    result.message = e.getMessage();
                }
            }
            return result;
        }
        return null;
    }

    @SuppressWarnings("RestrictedApi")
    public void execute(final JsonCallback<T> callback) {
        if (mCacheStrategy != NET_ONLY) {
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    ApiResponse<T> response = readCache();
                    if (callback != null && response.body != null) {
                        callback.onCacheSuccess(response);
                    }
                }
            });
        }

        if (mCacheStrategy != CACHE_ONLY) {
            getCall().enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    ApiResponse<T> response = new ApiResponse<>();
                    response.message = e.getMessage();
                    callback.onError(response);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    ApiResponse<T> apiResponse = parseResponse(response, callback);
                    if (apiResponse.success) {
                        callback.onSuccess(apiResponse);
                    } else {
                        callback.onError(apiResponse);
                    }
                }
            });
        }
    }

    private ApiResponse<T> readCache() {
        String key = TextUtils.isEmpty(mCacheKey) ? generateCacheKey() : mCacheKey;
        Object cache = CacheManager.getCache(key);
//        Log.e("request", "readCache  cache== "+cache);
        ApiResponse<T> response = new ApiResponse<>();
        response.body = (T) cache;
        response.message = "缓存成功";
        response.status = 304;
        response.success = true;
        return response;
    }


    protected ApiResponse<T> parseResponse(Response response, JsonCallback<T> callback) {
        String message = null;
        int status = response.code();
        boolean success = response.isSuccessful();
        ApiResponse<T> result = new ApiResponse<>();
        Convert convert = ApiService.sConvert;
        try {
            String content = response.body().string();
            if (success) {
                if (callback != null) {
                    ParameterizedType type = (ParameterizedType) callback.getClass().getGenericSuperclass();
                    Type argument = type.getActualTypeArguments()[0];
                    result.body = (T) convert.convert(content, argument);
                } else if (mType != null) {
                    result.body = (T) convert.convert(content, mType);
                } else if (mCls != null) {
                    result.body = (T) convert.convert(content, mCls);
                } else {
                    Log.e("request", "parseResponse == 无法解析");
                    Toast.makeText(AppGlobals.getApplication(),"无法解析",Toast.LENGTH_SHORT).show();
                }
            } else {
                message = content;
            }
        } catch (Exception e) {
            message = e.getMessage();
            success = false;
            status = 0;
        }
        result.status = status;
        result.message = message;
        result.success = success;

        if (mCacheStrategy != NET_ONLY && result.success && result.body != null && result.body instanceof Serializable) {
            saveCache(result.body);
        }
        return result;
    }

    private void saveCache(T body) {
        String key = TextUtils.isEmpty(mCacheKey) ? generateCacheKey() : mCacheKey;
//        Log.e("request", "saveCache  key== "+key);
        CacheManager.save(key, body);
    }

    //参数拼接到url后面当缓存的key
    private String generateCacheKey() {
        mCacheKey = UrlCreator.createUrlFromParams(mUrl, mParams);
        return mCacheKey;
    }

    @NonNull
    @Override
    public Request clone() throws CloneNotSupportedException {
        return (Request<T, R>) super.clone();
    }

}
