package com.zy.downloadtest;



import android.util.Log;

import androidx.annotation.Nullable;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * /*@Description
 * /*created by wwq on 2018/9/7 0007
 * /*@company zhongyiqiankun
 */
public class JsResponseBody extends ResponseBody {

    private static final String TAG = "LogUtil.JsResponseBody";

    private ResponseBody mResponseBody;
    private JsDownLoadListener mListener;
    private BufferedSource bufferedSource;

    public JsResponseBody(ResponseBody mResponseBody, JsDownLoadListener mListener) {
        this.mResponseBody = mResponseBody;
        this.mListener = mListener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource==null){
            bufferedSource= Okio.buffer(createSource(mResponseBody.source()));
        }
        return bufferedSource;
    }

    private Source createSource(Source source) {
        return new ForwardingSource(source) {
            long totalBytes=0L;
            long contentLength = mResponseBody.contentLength();
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long readBytes=super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytes += readBytes != -1 ? readBytes : 0;
                if (mListener!=null){
                    if (readBytes!=-1){
                        int percent=(int)(totalBytes*100/contentLength);
                        mListener.onProgress(percent,totalBytes,contentLength);
                        Log.i(TAG, "read: percent="+percent);
                        Log.i(TAG, "read: totalBytes="+totalBytes);
                        Log.i(TAG, "read: contentLength="+contentLength);
                    }
                }
                return readBytes;
            }
        };
    }


    public interface JsDownLoadListener {
        void onProgress(int percent, long downloaded, long contentLenth);
    }

}
