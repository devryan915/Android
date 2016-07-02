package com.langlang.global;

import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.net.HttpURLConnection;  
import java.net.MalformedURLException;  
import java.net.URL;  
  


import com.langlang.elderly_langlang.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;  
import android.app.AlertDialog.Builder;  
import android.app.Dialog;  
import android.content.Context;  
import android.content.DialogInterface;  
import android.content.Intent;  
import android.content.DialogInterface.OnClickListener;  
import android.content.IntentFilter;  
import android.net.Uri;  
import android.os.Handler;  
import android.os.Message;  
import android.util.Log;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.widget.ProgressBar;  
  
public class UpdateManager {  
    private Activity mContext;  
    private final String updateMsg = "发现新版本，快下载吧！";            //下载消息提示  
    private Dialog noticeDialog;                                        //下载提示对话框  
    private Dialog downloadDialog;                                      //下载进度对话框  
    private ProgressBar mProgressBar;                                   //进度条  
    private Boolean interceptFlag = false;                              //标记用户是否在下载过程中取消下载  
    private Thread downloadApkThread = null;                            //下载线程  
//    private final String apkUrl = "http://www.ypdm.com/hyjj.apk";  
//    private final String apkUrl = "http://www.langlangit.com/xinweishi/app/Elderly_langlang.apk";
    private final String apkUrl = "http://www.langlangit.com/llx/app/Elderly_langlang.apk";    
//    private final String apkUrl = "http://192.168.0.118:8080/xinweishi/app/Elderly_langlang.apk"; //apk的URL地址  
    @SuppressLint("SdCardPath")
//    private final String savePath = "/sdcard/UpdateDemo/";              //下载的apk存放的路径  
//    private final String saveFileName = savePath + "hyjjrelease.apk";  
	private final String savePath = "/sdcard/langlang/apk/";              //下载的apk存放的路径  
    private final String saveFileName = savePath + "langlangxin.apk";   //下载的apk文件  
    private int progress = 0;                                           //下载进度  
    private final int DOWNLOAD_ING = 1;                                 //标记正在下载  
    private final int DOWNLOAD_OVER = 2;                                //标记下载完成  
    private final String TAG="版本更新";                                    //日志打印标签  
    private Handler mhandler = new Handler() {                          //更新UI的handler  
        @Override  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            switch (msg.what) {  
            case DOWNLOAD_ING:  
                // 更新进度条  
                mProgressBar.setProgress(progress);  
                break;  
            case DOWNLOAD_OVER:  
                downloadDialog.dismiss();  
                installApk();  
                //安装  
                break;  
            default:  
                break;  
            }  
        }  
    };  
      
    /* 
     * 构造方法 
     */  
    public UpdateManager(Activity context) {  
        this.mContext = context;  
    }  
  
    /* 
     * 检查是否有需要更新，具体比较版本xml 
     */  
    public void checkUpdate() {  
        // 到服务器检查软件是否有新版本   zhe
        //如果有则  
        showNoticeDialog();  
    }  
  
    /* 
     * 显示版本更新对话框 
     */  
    private void showNoticeDialog() {  
        AlertDialog.Builder builder = new Builder(mContext);  
        builder.setTitle("版本更新");  
        builder.setMessage(updateMsg);  
        builder.setPositiveButton("立即更新", new OnClickListener() {  
  
            public void onClick(DialogInterface dialog, int which) {  
                noticeDialog.dismiss();  	
                showDownloadDialog();  
            }  
        });  
        builder.setNegativeButton("以后再说", new OnClickListener() {  
  
            public void onClick(DialogInterface dialog, int which) {  
                noticeDialog.dismiss();  
            }  
        });  
        noticeDialog = builder.create();  
        noticeDialog.show();  
    }  
  
    /* 
     * 弹出下载进度对话框 
     */  
    public void showDownloadDialog() {  
        AlertDialog.Builder builder = new Builder(mContext);  
        builder.setTitle("软件更新").setCancelable(false);  
        final LayoutInflater inflater = LayoutInflater.from(mContext);  
        View v = inflater.inflate(R.layout.progress, null);  
        mProgressBar = (ProgressBar) v.findViewById(R.id.updateProgress);  
        builder.setView(v);  
        builder.setNegativeButton("取消", new OnClickListener() {  
  
            public void onClick(DialogInterface dialog, int which) {  
                downloadDialog.dismiss();  
                interceptFlag = true;  
            }  
        });  
        downloadDialog = builder.create();  
        downloadDialog.show();  
        downloadLatestVersionApk();  
  
    }  
      
    /* 
     * 下载最新的apk文件 
     */  
    private void downloadLatestVersionApk() {  
        downloadApkThread = new Thread(downloadApkRunnable);  
        downloadApkThread.start();  
    }  
      
    //匿名内部类，apk文件下载线程  
    private Runnable downloadApkRunnable = new Runnable() {  
  
        public void run() {  
            try {  
                URL url = new URL(apkUrl);  
                HttpURLConnection conn = (HttpURLConnection) url  
                        .openConnection();  
                conn.connect();  
                int length = conn.getContentLength();  
                Log.e(TAG, "总字节数:"+length);  
                InputStream is = conn.getInputStream();  
                File file = new File(savePath);  
                if (!file.exists()) {  
                    file.mkdir();  
                }  
                File apkFile = new File(saveFileName);  
                FileOutputStream out = new FileOutputStream(apkFile);  
                int count = 0;  
                int readnum = 0;  
                byte[] buffer = new byte[1024];  
                do {  
                    readnum = is.read(buffer);  
                    count += readnum;  
                    progress = (int) (((float) count / length) * 100);  
                    Log.e(TAG, "下载进度"+progress);  
                    mhandler.sendEmptyMessage(DOWNLOAD_ING);  
                    if (readnum <= 0) {  
                        // 下载结束  
                        mhandler.sendEmptyMessage(DOWNLOAD_OVER);  
                        break;  
                    }  
                    out.write(buffer,0,readnum);  
                } while (!interceptFlag);  
                is.close();  
                out.close();  
            } catch (MalformedURLException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
    };  
    /* 
     * 安装下载的apk文件 
     */  
    private void installApk() {  
        File file= new File(saveFileName);  
        if(!file.exists()){  
            return;  
        }  
        Intent intent= new Intent(Intent.ACTION_VIEW);  
        intent.setDataAndType(Uri.parse("file://"+file.toString()), "application/vnd.android.package-archive");  
        mContext.startActivity(intent);  
        mContext.finish();
    }  
}  