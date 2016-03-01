package com.commonrail.mtf.util.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：15/11/16 下午10:34
 * 修改人：wengyiming
 * 修改时间：15/11/16 下午10:34
 * 修改备注：
 */
public class GlobalUtils {
    private static Toast toast;


    // ------------------------------------------------------
    // Private constructor for utility class
    private GlobalUtils() {
        throw new UnsupportedOperationException("Sorry, you cannot instantiate an utility class!");
    }

    public static void ShowDialog(final Activity activity, String title, String msg, boolean showDismiss, final DialogInterface.OnClickListener positive, final DialogInterface.OnClickListener negative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);  //先得到构造器
        builder.setTitle(title); //设置标题
        builder.setMessage(msg); //设置内容
//        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                positive.onClick(dialog, which);
            }
        });
        if (showDismiss) {
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    negative.onClick(dialog, which);
                }
            });
        }

//        builder.setNeutralButton("忽略", new DialogInterface.OnClickListener() {//设置忽略按钮
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                Toast.makeText(activity, "忽略" + which, Toast.LENGTH_SHORT).show();
//            }
//        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }


    /**
     * @return the name of the app (as defined in the "label" attribute in the
     * manifest)
     */
    public static String getApplicationLabel(Context context) {
        if (context == null) {
            return null;
        }

        ApplicationInfo appInfo = context.getApplicationInfo();
        android.content.pm.PackageManager packageMng = context.getPackageManager();
        if (packageMng != null) {
            return (String) packageMng.getApplicationLabel(appInfo);
        }

        return null;
    }

    /**
     * @return the version name of the application
     */
    public static String getVersionName(boolean flag) {
        Context context = GlobalUtils.getApplicationContext();
        String versionName = "unknown version";
        try {
            android.content.pm.PackageManager packageMng = context.getPackageManager();
            if (packageMng != null) {
                PackageInfo packageInfo = packageMng.getPackageInfo(context.getPackageName(), 0);
                if (packageInfo != null) {
                    versionName = packageInfo.versionName;
                }
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return versionName;
    }

    public static Context getApplicationContext() {
        return AppClient.getInstance();
    }

    /**
     * @return the version code of the application
     */
    public static int getVersionCode() {
        int versionCode;
        versionCode = getVersionCode(AppClient.getInstance());
        return versionCode;
    }

    /**
     * @return the version code of the application
     */
    public static int getVersionCode(Context context) {
        if (context == null) {
            return -1;
        }

        int versionCode = -1;
        try {
            PackageManager packageMng = context.getPackageManager();
            if (packageMng != null) {
                PackageInfo packageInfo = packageMng.getPackageInfo(context.getPackageName(), 0);
                if (packageInfo != null) {
                    versionCode = packageInfo.versionCode;
                }
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        return versionCode;
    }

    /**
     * @return Android SDK, Version, Manufacturer, Model, Device
     */
    public static String getDeviceInfo() {
        TelephonyManager tm = (TelephonyManager) AppClient.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId() + "_" + tm.getSimSerialNumber();
        if (deviceId.trim().length() == 0) {
            deviceId = String.valueOf(System.currentTimeMillis());
        }
        return deviceId;

    }

    /**
     * @return Android SDK, Version, Manufacturer, Model, Device
     */
    @SuppressLint("DefaultLocale")
    public static String getSDK() {
        return String.format("%d-%s", android.os.Build.VERSION.SDK_INT, android.os.Build.VERSION.RELEASE);
    }


    /**
     * @return true if this device has a camera
     */
    public static boolean hasCamera(Context context) {
        if (context == null) {
            return false;
        }
        PackageManager packageMng = context.getPackageManager();
        return packageMng != null && packageMng.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    /**
     * 隐藏键盘
     *
     * @param context context
     * @param view    view
     */
    public static void hideKeyboard(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * 获取状态栏
     *
     * @param context context
     * @return int
     */
    public static int getStatusHeight(Context context) {
        Rect frame = new Rect();
        if (context instanceof Activity) {
            ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            return frame.top;
        }
        return 20;
    }

    /**
     * 判断微信是否安装
     *
     * @return boolean
     */
    public static boolean confirmWeiXinInstalled() {
        PackageInfo packageInfo;
        try {
            packageInfo = getApplicationContext().getPackageManager().getPackageInfo("com.tencent.mm", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }


    /**
     * 通过反射，跳转到拨打电话界面
     *
     * @param number number
     */
    public static void phoneDialog(String number, Context context) {
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Object iTelephony;
            iTelephony = getITelephonyMethod.invoke(tManager, (Object[]) null);
            Method dial = iTelephony.getClass().getDeclaredMethod("dial", String.class);
            dial.invoke(iTelephony, number);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | SecurityException e) {
            e.printStackTrace();
        }
    }


    /**
     * 是否为前台进程
     *
     * @param context context
     * @return boolean
     */
    @SuppressWarnings("deprecation")
    public static boolean isApplicationTop(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            if (tasks.get(0).topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 显示Toast 时间为short
     */
    public static void showToastShort(Context context, String tip) {
        if (TextUtils.isEmpty(tip)) {
            tip = "未知错误";
        }
        if (context == null) {
            globalToast(AppClient.getInstance().getApplicationContext(), tip, Toast.LENGTH_SHORT);
        } else {
            globalToast(context.getApplicationContext(), tip, Toast.LENGTH_SHORT);
        }
    }

    /**
     * 全局Toast
     */
    private synchronized static void globalToast(Context context, String tip, int duration) {
        if (toast != null) {
            toast.cancel();
            toast.setText(tip);
            toast.setDuration(duration);
        } else {
            toast = Toast.makeText(context, tip, duration);
        }
        toast.show();
    }

    /**
     * 显示Toast 时间为long
     */
    public static void showToastLong(Context context, String tip) {
        if (TextUtils.isEmpty(tip)) {
            tip = "未知错误";
        }
        if (context == null) {
            globalToast(AppClient.getInstance().getApplicationContext(), tip, Toast.LENGTH_LONG);
        } else {
            globalToast(context.getApplicationContext(), tip, Toast.LENGTH_LONG);
        }
    }


    /**
     * 是否有SDCard
     */
    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();

        return status.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 通过activity得到屏幕信息
     */
    public static DisplayMetrics getScreenDisplayMetrics(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);

        return metric;
    }

    /**
     * 通过WINDOW_SERVICE获取display对象
     */
    public static String getScreenWidthxHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        return width + "x" + height;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;

        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取版本号和版本次数
     */
    public static String getVersionCode(Context context, int type) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            if (type == 1) {
                return String.valueOf(pi.versionCode);
            } else {
                return pi.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getScreenHeight(Activity context) {
        if (context == null) {
            return 0;
        }
        return context.findViewById(android.R.id.content).getHeight();
    }

    public static int getActionBarSize(Activity context) {
        if (context == null) {
            return 0;
        }
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = context.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();

        return actionBarSize;
    }

    /**
     * 获取设备信息
     */
    public static String getDeviceInfo(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return ("\nDeviceId(IMEI) = " + tm.getDeviceId()) + "\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() + "\nLine1Number = " + tm.getLine1Number() + "\nNetworkCountryIso = " + tm.getNetworkCountryIso() + "\nNetworkOperator = " + tm.getNetworkOperator() + "\nNetworkOperatorName = " + tm.getNetworkOperatorName() + "\nNetworkType = " + tm.getNetworkType() + "\nPhoneType = " + tm.getPhoneType() + "\nSimCountryIso = " + tm.getSimCountryIso() + "\nSimOperator = " + tm.getSimOperator() + "\nSimOperatorName = " + tm.getSimOperatorName() + "\nSimSerialNumber = " + tm.getSimSerialNumber() + "\nSimState = " + tm.getSimState() + "\nSubscriberId(IMSI) = " + tm.getSubscriberId() + "\nVoiceMailNumber = " + tm.getVoiceMailNumber();
    }

    /**
     * 获取设备android id
     */
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取系统时间 格式为："yyyy/MM/dd"
     */
    public static String getCurrentDate() {
        Date d = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");

        return sf.format(d);
    }

    /**
     * 时间戳转换成字符窜
     */
    public static String getDateToString(long time) {
        Date d = new Date(time);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

        return sf.format(d);
    }

    public static String getDateToStringWithYDHM(long time) {
        Date d = new Date(time);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sf = new SimpleDateFormat("MM月dd日 HH:mm");

        return sf.format(d);
    }

    /**
     * 将字符串转为时间戳
     */
    public static long getStringToDate(String time) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 获取layoutInflater
     *
     * @return LayoutInflater
     */
    public static LayoutInflater getLayoutInflater(Context context) {

        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 字符串拼接 by StringBuilder
     */
    public static String getStringByStringBuilder(String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }

    /**
     * 字符串拼接 by StringBuffer
     */
    public static String getStringByStringBuffer(String... strings) {
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string);
        }
        return builder.toString();
    }

    /**
     * 高效拼接url
     */
    public static String getUrl(HashMap<String, String> params) {
        String url = null;
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            url = sb != null ? sb.toString() : "";
        }
        return url;
    }

    /**
     * 判断是否有网络连接
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 检查网络
     */
    @SuppressWarnings("deprecation")
    public static boolean checkNetState(Context context) {
        boolean netstate = false;
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (final NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        netstate = true;
                        break;
                    }
                }
            }
        }
        return netstate;
    }

    /**
     * 判断WIFI网络是否可用
     */
    @SuppressWarnings("deprecation")
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wiFiNetworkInfo
                    = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wiFiNetworkInfo != null) {
                return wiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断移动网络是否可用
     */
    @SuppressWarnings("deprecation")
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileNetworkInfo
                    = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobileNetworkInfo != null) {
                return mobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取当前网络连接的类型信息
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                return networkInfo.getType();
            }
        }
        return -1;
    }

//	public static String getHandSetInfo() {
//		String handSetInfo =
//				"手机型号:" + android.os.Build.MODEL
//						+ ",系统版本:" + android.os.Build.VERSION.RELEASE;
//		return handSetInfo;
//
//	}

    /**
     * Base64 编码
     */
    public static String encodeByBase64(String string) {
        byte[] encode = Base64.encode(string.getBytes(), Base64.DEFAULT);

        return new String(encode);
    }

    /**
     * Base64 解码
     */
    public static String decodeByBase64(String string) {
        byte[] decode = Base64.decode(string, Base64.DEFAULT);

        return new String(decode);
    }

    /**
     * inputStream 转成 string
     *
     * @throws IOException
     */
    public static String inputStream2String(InputStream in) throws IOException {
        byte[] buf = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        for (int i; (i = in.read(buf)) != -1; ) {
            baos.write(buf, 0, i);
        }

        return baos.toString("UTF-8");
    }


    /**
     * 毫秒 转 小时:分钟:秒
     */
    public static String getTimeFromMillisecond(long Millisecond) {
        if (Millisecond <= 0) {
            return "00:00";
        }

        long days = Millisecond / (1000 * 60 * 60 * 24);
        long hours = (Millisecond % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (Millisecond % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (Millisecond % (1000 * 60)) / 1000;

        String hoursStr = hours >= 10 ? String.valueOf(hours) : "0" + String.valueOf(hours);
        String minutesStr = minutes >= 10 ? String.valueOf(minutes) : "0" + String.valueOf(minutes);
        String secondsStr = seconds >= 10 ? String.valueOf(seconds) : "0" + String.valueOf(seconds);

        if (hours > 0) {
            return hoursStr + ":" + minutesStr + ":" + secondsStr;
        }
        return minutesStr + ":" + secondsStr;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isActivityLive(Activity activity) {
        return activity != null && !activity.isDestroyed() && !activity.isFinishing();
    }

}