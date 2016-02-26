package com.jack.codeviewer;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jack.codeviewer.interf.ICallbackResult;
import com.jack.codeviewer.utils.DownloadState;
import com.jack.codeviewer.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jack on 2/22/16.
 */
public class DownloadService extends Service{

    private DownloadBinder mDownloadBinder;
    private Thread mDownloadThread;

    private NotificationManager mNotificationManager;

    private ProgressBar progressBar;
    private TextView progressCount;
    private TextView downloadInfo;

    private DownloadState downloadState = null;
    private ICallbackResult callBack;

    private int progress;
    private String downloadUrl;
    private String saveFileName;

    private Handler mDownloadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //Download finished
                    downloadState.setState(DownloadState.DOWNLOAD_FINISHED);
                    updateDownloadInfo();
                    unzipThread(downloadState);
                    break;
                case 1:
                    int rate = msg.arg1;
                    String size = msg.getData().getString(AppConfig.BUNDLE_KEY_CURRENT_SIZE);
                    progressBar.setProgress(rate);
                    progressCount.setText(size);
                    updateDownloadInfo();
                    if (rate < 100) {
                        //
                    } else {
                        stopSelf();
                    }
                    break;
                case 2:
                    downloadState.setState(DownloadState.DOWNLOAD_ERROR);
                    updateDownloadInfo();
                    stopSelf();
                    break;
                case 9:
                    int success = msg.arg1;
                    downloadState.setState(DownloadState.EXTRACT_FAILED);
                    if (success == 1 && FileUtils.renameRoot(downloadState)) {
                        downloadState.setState(DownloadState.EXTRACT_SUCCESSS);
                        callBack.onBackResult(downloadState);
                    }
                    updateDownloadInfo();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadBinder = new DownloadBinder();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        downloadState = new DownloadState();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        String originalUrl = intent.getStringExtra(AppConfig.BUNDLE_KEY_DOWNLOAD_URL);
        downloadUrl = getRealDownloadUrl(originalUrl);
        downloadState.setDownloadUrl(downloadUrl);
        String zipName = getDownloadFileName(originalUrl);
        downloadState.setSavedFileName(zipName);
        saveFileName = AppConfig.DEFAULT_SAVE_FILE_PATH + zipName;
        return mDownloadBinder;
    }

    private String getRealDownloadUrl(String url) {
        if (url == null || url.equals("")) {
            return "";
        }
        return url + AppConfig.ZIP_FILE_POSTFIX;
    }

    private String getDownloadFileName(String url) {
        if (url == null || url.equals("")) {
            return "";
        }
        String git = url.substring(url.lastIndexOf("/") + 1);
        downloadState.setGitName(git);
        return git + AppConfig.SAVED_FILE_POSTFIX;
    }

    public class DownloadBinder extends Binder {
        public void start() {
            if (mDownloadThread == null || (!mDownloadThread.isAlive())) {
                progress = 0;
                setUpNotification();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startDownload();
                    }
                }).start();
                updateDownloadInfo();
            }
        }

        public void setDownloadInfo(TextView info) {
            downloadInfo = info;
        }

        public void setProgressBar(ProgressBar bar) {
            progressBar = bar;
        }

        public void setProgressBarCount(TextView count) {
            progressCount = count;
        }

        public void addCallBack(ICallbackResult callback) {
            callBack = callback;
        }
    }

    private Runnable downloadRunnable = new Runnable() {
        @Override
        public void run() {
            File file = new File(AppConfig.DEFAULT_SAVE_FILE_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            String apkFile = saveFileName;
            File saveFile = new File(apkFile);
            try {
                if (downloadGithubFile(downloadUrl, saveFile) <= 0) {
                    mDownloadHandler.sendEmptyMessage(2);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void setUpNotification() {
        //
    }

    private void startDownload() {
        mDownloadThread = new Thread(downloadRunnable);
        mDownloadThread.start();
    }

    private long downloadGithubFile(String downloadUrl, File saveFile) {
        int downloadCount = 0;
        long totalSize = 0;
        int zipTotalSize = 0;

        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(AppConfig.CONNECT_TIME_OUT_MS);
            conn.setReadTimeout(AppConfig.READ_TIME_OUT_MS);
            zipTotalSize = conn.getContentLength();
            downloadState.setTotalSize(zipTotalSize);
            downloadState.setState(DownloadState.DOWNLOAD_ING);
            if (conn.getResponseCode() == 404) {
                throw new Exception("fail!");
            }

            is = conn.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
                totalSize += read;

                if ((downloadCount == 0) ||
                    (int)(totalSize * 100 / zipTotalSize) - 5  >= downloadCount) {
                    downloadCount += 5;
                    Message message = mDownloadHandler.obtainMessage();
                    message.what = 1;
                    message.arg1 = downloadCount;
                    message.setData(getSizeMessage(totalSize, zipTotalSize));
                    mDownloadHandler.sendMessage(message);
                }
            }
            mDownloadHandler.sendEmptyMessage(0);
        } catch (Exception e) {
            mDownloadHandler.sendEmptyMessage(2);
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return totalSize;
    }

    private Bundle getSizeMessage(long currentSize, int totalSize) {
        Bundle bundle = new Bundle();
        String message;
        String total = Integer.toString(totalSize/1024) + "KB";
        if (currentSize < 1024) {
            message =  Long.toString(currentSize) + "B/" + total;
        } else {
            message =  Long.toString(currentSize/1024) + "KB/" + total;
        }
        bundle.putString(AppConfig.BUNDLE_KEY_CURRENT_SIZE, message);
        return bundle;
    }

    private void updateDownloadInfo() {
        downloadInfo.setText(downloadState.toString());
    }

    private void unzipThread(final DownloadState state) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = mDownloadHandler.obtainMessage();
                message.what = 9;
                message.arg1 = 0;
                if (FileUtils.unzipFile(state)) {
                    message.arg1 = 1;
                }
                mDownloadHandler.sendMessage(message);
            }
        }).start();
    }
}
