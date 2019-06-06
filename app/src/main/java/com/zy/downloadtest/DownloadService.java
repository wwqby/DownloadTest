package com.zy.downloadtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class DownloadService extends Service {

    private static final String CHANNEL_ONE_ID = "download_channal";
    private DownloadTask mDownloadTask;
    private String downloadUrl;

    private DownloadListener mDownloadListener = new DownloadListener() {

        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1,getNotification("Downloading...",progress));
        }

        @Override
        public void onSuccess() {
            mDownloadTask = null;
//            关闭前台下载进度条，创建一个新的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download success", 0));
            Toast.makeText(DownloadService.this, "success", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onFailed() {
            mDownloadTask = null;
//            下载失败，关闭进度条，创建一个失败通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failed", 0));
            Toast.makeText(DownloadService.this, "failed", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onPaused() {
            mDownloadTask = null;
            Toast.makeText(DownloadService.this, "paused", Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public void onCanceled() {
            mDownloadTask=null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "canceled", Toast.LENGTH_SHORT)
                    .show();
        }
    };

    private DownloadBind mBind=new DownloadBind();

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "success");
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ONE_ID, "download", NotificationManager.IMPORTANCE_LOW);
            getNotificationManager().createNotificationChannel(mChannel);
            builder.setChannelId(CHANNEL_ONE_ID);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(title);
        if (progress > 0) {
//            当进度大于0的时候才需要显示进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return mBind;
    }

    public class DownloadBind extends Binder {

        public void startDownload(String url){
            if (mDownloadTask==null){
                downloadUrl=url;
                mDownloadTask=new DownloadTask(mDownloadListener);
                mDownloadTask.execute(downloadUrl);
                startForeground(1,getNotification("Downloading...",0));
                Toast.makeText(DownloadService.this, "start download", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        public void pauseDownload(){
//            if (mDownloadTask!=null){
//                mDownloadTask.pauseDowload();
//            }
        }

        public void cancelDownload(){
            if (mDownloadTask!=null){
                mDownloadTask.cancelDownload();
            }else {
                if (downloadUrl!=null){
//                    取消下载时，删除下载文件，关闭前台通知
                    String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file=new File(directory,fileName);
                    if (file.exists()){
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "cancel download", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }
}
