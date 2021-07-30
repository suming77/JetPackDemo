package com.example.libnetwork;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @创建者 mingyan.su
 * @创建时间 2021/06/24 14:58
 * @类描述 ${TODO}
 */
public class ApiService {
    protected static final OkHttpClient sOkHttpClient;
    protected static String sBaseUrl;
    protected static Convert sConvert;

    static {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        sOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        //为了解决Https请求证书信任问题
        TrustManager[] trustManagers = {new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }
        };

        //Android9.0以后默认阻止了http请求,目前搭建的服务器基于http类型，
        //还需要在AndroidManifest文件中添加android:usesCleartextTraffic="true",表示允许明文请求，即允许http请求的接口
        try {
            SSLContext ssl = SSLContext.getInstance("SSL");//握手原则
            ssl.init(null, trustManagers, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(ssl.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;//信任所有域名
                }
            });
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static void init(String baseUrl, Convert convert) {
        sBaseUrl = baseUrl;
        if (convert == null) {
            convert = new JsonConvert();
        }
        sConvert = convert;
    }

    public static <T> GetRequest<T> get(String url) {
        return new GetRequest<>(sBaseUrl + url);
    }

    public static <T> PostRequest<T> post(String url) {
        return new PostRequest<>(sBaseUrl + url);
    }
}
