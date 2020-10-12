package com.example.shijiewei.crashhandlertest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shijiewei on 2016/12/15.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashTest/log/";
    public static final String FILE_NAME = "crash";
    public static final String FILE_NAME_SUFFIX = ".trace";

    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        // 获取系统默认的UncaughtExceptionHandler
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    /**
     * 这是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用该方法
     * @param thread 出现为捕获异常的线程
     * @param ex 未捕获的异常
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            // 导出异常信息到SD卡中
            dumpExceptionToSDCard(ex);
            // 这里可以上传异常信息到服务器，便于开发人员分析日志
            uploadExceptionToServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ex.printStackTrace();
        // 如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就由自己结束自己
        if (mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private void dumpExceptionToSDCard(Throwable ex) throws IOException {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.w(TAG, "Sdcard unmounted, skip dump exception");
            }
            return;
        }
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
            Log.e(TAG, "Dump crash info failed", e);
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("APP Version: ");
        pw.println(pi.versionName);
        // Android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);
        // 手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);
        // 手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);

        // CPU架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
    }

    private void uploadExceptionToServer() {
        // TODO Upload Exception Message To Your Web Server
    }
}
