package com.commonrail.mtf.util.common;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Log统一管理类
 *
 * @author way
 */
public class L {

    private static final String TAG = "railtool";
    public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化

    private L() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    // 下面四个是默认tag的函数
    public static void i(String msg) {
        if (isDebug)
            Log.i(TAG, msg);
        writeToFile(TAG + " -- " + msg + " " + msg + "\n\n");
    }

    public static void d(String msg) {
        if (isDebug)
            Log.d(TAG, msg);
        writeToFile(TAG + " -- " + msg + " " + msg + "\n\n");
    }

    public static void e(String msg) {
        if (isDebug)
            Log.e(TAG, msg);
        writeToFile(TAG + " -- " + msg + " " + msg + "\n\n");
    }

    public static void v(String msg) {
        if (isDebug)
            Log.v(TAG, msg);
        writeToFile(TAG + " -- " + msg + " " + msg + "\n\n");
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
        writeToFile(tag + " -- " + msg + " " + msg + "\n\n");
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);
        writeToFile(tag + " -- " + msg + " " + msg + "\n\n");
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            Log.e(tag, msg);
        writeToFile(tag + " -- " + msg + " " + msg + "\n\n");
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            Log.i(tag, msg);

        writeToFile(tag + " -- " + msg + " " + msg + "\n\n");
    }

    private static void writeToFile(String msg) {
        String logdir;
        if (Environment.getExternalStorageDirectory() != null) {
            logdir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "railToolLog"
                    + File.separator + "log";

            File file = new File(logdir);
            boolean mkSuccess;
            if (!file.isDirectory()) {
                mkSuccess = file.mkdirs();
                if (!mkSuccess) {
                    mkSuccess = file.mkdirs();
                }
            }
            try {
                FileWriter fw = new FileWriter(logdir + File.separator + "rtLog.log", true);
                fw.write(new Date() + "\n");
                fw.write(msg + "\n");
                fw.close();
            } catch (IOException e) {
                Log.e("crash handler", "load file failed...", e.getCause());
            }
        }
    }
}