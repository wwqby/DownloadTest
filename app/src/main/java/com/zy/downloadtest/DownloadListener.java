package com.zy.downloadtest;

/**
 * /*@Description
 * /*created by wwq on 2019/6/5
 * /*@company zhongyiqiankun
 */
public interface DownloadListener {

    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();
}
