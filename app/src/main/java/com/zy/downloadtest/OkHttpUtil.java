package com.zy.downloadtest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * /*@Description
 * /*created by wwq on 2019/6/5
 * /*@company zhongyiqiankun
 */
public class OkHttpUtil {

    private static okhttp3.OkHttpClient client;
    private static HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    public static okhttp3.OkHttpClient getClient(JsResponseBody.JsDownLoadListener mListener) {
        if (client == null) {
            synchronized (okhttp3.OkHttpClient.class) {
                if (client == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    client=new okhttp3.OkHttpClient.Builder()
                            .cookieJar(new CookieJar() {

                                @Override
                                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                                    cookieStore.put(url.host(), cookies);
                                }

                                @Override
                                public List<Cookie> loadForRequest(HttpUrl url) {
                                    List<Cookie> cookies = cookieStore.get(url.host());
                                    return cookies != null ? cookies : new ArrayList<Cookie>();
                                }
                            })
                            .addInterceptor(logging)
                            .addInterceptor(new JsDownloadInterceptor(mListener))
                            .readTimeout(15000, TimeUnit.MILLISECONDS)
                            .writeTimeout(15000, TimeUnit.MILLISECONDS)
                            .connectTimeout(15000, TimeUnit.MILLISECONDS)
                            .build();
                }
            }
        }
        return client;
    }
}
