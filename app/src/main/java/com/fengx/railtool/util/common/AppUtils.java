package com.fengx.railtool.util.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.fengx.railtool.AppClient;

import java.util.HashMap;

/**
 * 跟App相关的辅助类
 *
 * @author eming
 */
public class AppUtils {

    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");

    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName() {
        try {
            PackageManager packageManager = AppClient.getInstance().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    AppClient.getInstance().getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "unknown version";
    }


    /**
     * @return the version code of the application
     */
    public static int getVersionCode() {
        if (AppClient.getInstance() == null) {
            return -1;
        }

        int versionCode = -1;
        try {
            PackageManager packageMng = AppClient.getInstance().getPackageManager();
            if (packageMng != null) {
                PackageInfo packageInfo = packageMng.getPackageInfo(AppClient.getInstance().getPackageName(), 0);
                if (packageInfo != null) {
                    versionCode = packageInfo.versionCode;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        return versionCode;
    }

    /**
     * 获取机器唯一标识
     *
     * @return
     */
    public static String getLocaldeviceId() {
        TelephonyManager tm = (TelephonyManager) AppClient.getInstance()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (deviceId == null
                || deviceId.trim().length() == 0) {
            deviceId = String.valueOf(System
                    .currentTimeMillis());
        }
        return deviceId;
    }

    /**
     * 获取mac地址
     *
     * @return
     */
    public static String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) AppClient.getInstance().getSystemService(
                Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    /**
     * @return the ANDROID_ID that identify the device, or the "emulator" string
     * on the emulator.
     */
    public static String getAndroidId() {
        String androidId = Settings.Secure.getString(AppClient.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId == null || androidId.length() <= 0) {
            androidId = "emulator";
        }
        return androidId;
    }


    public static HashMap<String, String> getMap(String key, String value) {
        HashMap<String, String> map = new HashMap<>();
        map.put(key, value);
        L.e(map.toString());
        return map;
    }

}
