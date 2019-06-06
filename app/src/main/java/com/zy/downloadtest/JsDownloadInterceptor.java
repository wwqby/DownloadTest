package com.zy.downloadtest;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * /*@Description
 * /*created by wwq on 2018/9/7 0007
 * /*@company zhongyiqiankun
 */
public class JsDownloadInterceptor implements Interceptor {

    private static final String TAG = "LogUtil.JsInterceptor";
    private JsResponseBody.JsDownLoadListener mListener;

    public JsDownloadInterceptor(JsResponseBody.JsDownLoadListener mListener) {
        this.mListener = mListener;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response=chain.proceed(chain.request());
        return response.newBuilder().body(new JsResponseBody(response.body(), mListener)).build();
    }
}
