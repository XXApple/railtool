package com.fengx.railtool.util.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fengx.railtool.AppClient;
import com.fengx.railtool.R;
import com.nostra13.universalimageloader.cache.disc.impl.BaseDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：15/11/16 下午10:34
 * 修改人：wengyiming
 * 修改时间：15/11/16 下午10:34
 * 修改备注：
 */
public class GlobalUtils {
    private static Toast toast;


    private static final String TAG = "GlobalUtils";
    private static Context sApplicationContext = null;
    private static String EXTERNAL_STORAGE_DIRECTORY;
    private static String ROOT_DIRECTORY;
    private static String CACHE_DIRECTORY;
    private static boolean isAppActivity = false;
    // 屏幕分辨率
    private static float density;
    private static DisplayMetrics metrics;
    // 屏幕宽度
    private static int widthPixels;
    // 屏幕高度
    private static int heightPixels;
    // 小图图片显示设置
    private static DisplayImageOptions options;
    // 正方形图片
    private static DisplayImageOptions squareOptions;
    // 显示头像设置
    private static DisplayImageOptions headOptions;

    // ------------------------------------------------------
    // Private constructor for utility class
    private GlobalUtils() {
        throw new UnsupportedOperationException("Sorry, you cannot instantiate an utility class!");
    }

    public static void init(Context context) {
        sApplicationContext = context.getApplicationContext();
        EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().toString();
        ROOT_DIRECTORY = EXTERNAL_STORAGE_DIRECTORY + "/Android/data/" + context.getPackageName() + "/";
        if (!isSDCardExists()) {
            EXTERNAL_STORAGE_DIRECTORY = context.getCacheDir().toString();
            ROOT_DIRECTORY = EXTERNAL_STORAGE_DIRECTORY + "/" + context.getPackageName() + "/";
        }
        // CACHE_DIRECTORY = ROOT_DIRECTORY + ".apiCache" + "/";
        CACHE_DIRECTORY = ROOT_DIRECTORY;
        if (!new File(CACHE_DIRECTORY).exists()) {
            new File(CACHE_DIRECTORY).mkdirs();
        }

        // 初始化图片设置
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(0)).build();
//        // 初始化图片设置
//        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.image_loding)
//                .showImageForEmptyUri(R.drawable.image_loding)
//                .showImageOnFail(R.drawable.image_loding)
//                .cacheInMemory(true)
//                .cacheOnDisk(true)
//                .considerExifParams(true)
//                .displayer(new RoundedBitmapDisplayer(20)).build();
        // 初始化正方形图片设置
        setSquareOptions(new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher).showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.ARGB_4444).build());
        // 初始化头像
        headOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher).showImageOnFail(R.drawable.ic_launcher)
                .cacheInMemory(true).cacheOnDisk(true).bitmapConfig(Bitmap.Config.ARGB_4444).build();
    }

    public static boolean isSDCardExists() {
        return (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));
    }

    public static Context getApplicationContext() {
        return sApplicationContext;
    }

    public static ImageLoader getPickImageLoader(Activity context) {

        // String CACHE_DIR =
        // Environment.getExternalStorageDirectory().getAbsolutePath() +
        // "/.temp_tmp";
        // new File(CACHE_DIR).mkdirs();
        // File cacheDir =
        // StorageUtils.getOwnCacheDirectory(context.getBaseContext(),
        // CACHE_DIR);
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.ARGB_8888).build();

        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), Constant.SD_CACHE_DIR);// 缓存地址

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context.getBaseContext())
                .diskCache(new BaseDiskCache(cacheDir) {
                    @Override
                    public File getDirectory() {
                        return super.getDirectory();
                    }
                }).defaultDisplayImageOptions(defaultOptions)
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(8 * 1024 * 1024)).tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheFileCount(1000).memoryCacheSize(3 * 1024 * 1024).discCacheSize(80 * 1024 * 1024);
        ImageLoaderConfiguration config = builder.build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        return imageLoader;
    }


    /**
     * @return the name of the app (as defined in the "label" attribute in the
     * manifest)
     */
    public static String getApplicationLabel() {
        Context context = sApplicationContext;
        String applicationLabel = "Unknown app";
        if (context != null) {
            applicationLabel = getApplicationLabel(context);
        }
        return applicationLabel;
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
        if (flag) {
            return Constant.VERSION_NAME;// 接口使用的版本号
        }

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
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, e);
        }
        return versionName;
    }

    /**
     * @return the version code of the application
     */
    public static int getVersionCode() {
        Context context = sApplicationContext;
        int versionCode = -1;
        if (context != null) {
            versionCode = getVersionCode(context);
        }
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
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, e);
        }

        return versionCode;
    }

    /**
     * @return Android SDK, Version, Manufacturer, Model, Device
     */
    public static String getDeviceInfo() {
        TelephonyManager tm = (TelephonyManager) sApplicationContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId() + "_" + tm.getSimSerialNumber();
        if (deviceId == null || deviceId.trim().length() == 0) {
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
     * 获取不同厂商
     *
     * @return
     */
    public static int getChanelId() {
        String CHANNELID_STRING = "_0";
        try {
            ApplicationInfo ai = getApplicationContext().getPackageManager().getApplicationInfo(
                    getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            Object value = ai.metaData.get("UMENG_CHANNEL");
            if (value != null) {
                CHANNELID_STRING = value.toString();
            }
        } catch (Exception e) {
        }
//        int CHANNELID = ChanelUtil.getChanelId(CHANNELID_STRING);
//        Log.e(CHANNELID_STRING, CHANNELID + "");
        return 1;
    }

    public static boolean isAppActivity() {
        return isAppActivity;
    }

    /**
     * @return true if the app is debuggable, false otherwise
     */
    public static boolean isDebuggable(Context context) {
        if (context == null) {
            return false;
        }

        ApplicationInfo appInfo = context.getApplicationInfo();
        if (appInfo != null) {
            return ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
        } else {
            return (ApplicationInfo.FLAG_DEBUGGABLE != 0);
        }
    }

    /**
     * @return true if this device has a camera
     */
    public static boolean hasCamera(Context context) {
        if (context == null) {
            return false;
        }
        PackageManager packageMng = context.getPackageManager();
        if (packageMng != null) {
            return packageMng.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        }
        return false;
    }

    /**
     * @return true if this device has Amazon Market App installed
     */
    public static boolean hasAmazonMarketApp(Context context) {
        if (context == null) {
            return false;
        }
        try {
            PackageManager packageMng = context.getPackageManager();
            if (packageMng != null) {
                return (null != packageMng.getPackageInfo("com.amazon.venezia", 0));
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param view
     */
    public static void hideKeyboard(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String getCacheDirectory() {
        return CACHE_DIRECTORY;
    }

    public static String getRootDirectory() {
        return ROOT_DIRECTORY;
    }

//    public static String getMd5MacAddress() {
//        WifiManager wifi = (WifiManager) GlobalUtils.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = wifi.getConnectionInfo();
//        return Md5.encode(info.getMacAddress());
//    }

    /**
     * listview和scrollview共存解决方案
     *
     * @param listView
     */
    public static int getListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        int totalHeight = 20;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem != null) {
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        totalHeight += listView.getDividerHeight() * (listView.getAdapter().getCount() - 1);
        return totalHeight;
    }

    /**
     * listview和scrollview共存解决方案
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView, int height) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 如果height如果大于就指定listitem高度
            if (height != 0) {
                totalHeight += height; // 统计所有子项的总高度
            } else {
                if (listItem instanceof ViewGroup) {
                    listItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                }
                listItem.measure(0, 0); // 计算子项View 的宽高
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * gridView和scrollview共存解决方案
     *
     * @param gridView
     */
    public static void getGridViewHeight(GridView gridView, int numColumns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i += numColumns) {
            View listItem = listAdapter.getView(i, null, gridView);
            if (listItem != null) {
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }

    /**
     * 获取color的Id
     *
     * @param cId
     * @return
     */
    public static int getColorById(int cId) {
        Context ctx = GlobalUtils.getApplicationContext();
        return ctx.getResources().getColor(cId);
    }

    /**
     * logout时候，清空相关配置文件
     */
    public static void logout() {
        // MemoryCache.getInstance().resetCurrentMember();
        // SharedPreferencesUtils.resetRegisterMember();
        // SharedPreferencesUtils.resetSuggestRegion();
        // SharedPreferencesUtils.resetSessionId();
    }

    /**
     * 发送定位请求，定位完成发送boardcast，INTENT_ACTION_LOCATION_FINISHED
     */
    public static void startLocating() {
        // LocationManagerV2.getInstance().requestLocation(new
        // MmdOnLocationFinished(), 1);
    }

    /**
     * 获取图片下载的DisplayImageOptions对象
     *
     * @return
     */
    public static DisplayImageOptions getDisplayImageOptions() {
        return options;
    }

    public static DisplayImageOptions getHeadDisplayImageOptions() {
        return headOptions;
    }

    /**
     * 获取屏幕分辨率
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        if (density == 0) {
            if (metrics == null) {
                getDisPlayMetrics(context);
            }
            density = metrics.density;
        }
        return density;
    }

    /**
     * 获取
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisPlayMetrics(Context context) {
        if (metrics == null) {
            metrics = new DisplayMetrics();
            if (context instanceof Activity) {
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            }
        }
        return metrics;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getWidthPixels(Context context) {
        if (widthPixels == 0) {
            widthPixels = getDisPlayMetrics(context).widthPixels;
        }
        return widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getHeightPixels(Context context) {
        if (heightPixels == 0) {
            heightPixels = getDisPlayMetrics(context).heightPixels;
        }
        return heightPixels;
    }

    /**
     * 获取指定宽度
     *
     * @param context
     * @param width   间距
     * @param size
     * @return
     */
    public static int getWidthBySet(Context context, int width, int size) {
        float denstiy = GlobalUtils.getDensity(context);
        return (int) ((GlobalUtils.getWidthPixels(context) - width * denstiy) / size);
    }


    /**
     * 获取状态栏
     *
     * @param context
     * @return
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
     * @return
     */
    public static boolean confirmWeiXinInstalled() {
        PackageInfo packageInfo;
        try {
            packageInfo = getApplicationContext().getPackageManager().getPackageInfo("com.tencent.mm", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取缓存文件
     *
     * @param file
     * @param context
     * @return
     */
    public static File getInCacheFile(String file, Context context) {
        File fl = null;
        try {
            // 读取缓存图片
            fl = ImageLoader.getInstance().getDiscCache().get(file);
            if (!fl.exists()) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        return fl;
    }

    /**
     * 通过反射，跳转到拨打电话界面
     *
     * @param number
     */
    public static void phoneDialog(String number, Context context) {
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Object iTelephony;
            iTelephony = (Object) getITelephonyMethod.invoke(tManager, (Object[]) null);
            Method dial = iTelephony.getClass().getDeclaredMethod("dial", String.class);
            dial.invoke(iTelephony, number);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * @author Devis
//     * @date 2014-11-5-下午2:53:30
//     * @des 获取本次请求的MD5值 sign 参数
//     */
//    public static String getMd5Param(String requeString) {
//        return Md5.encode(requeString + Constant.SECRET_KEY);// MD5加密
//    }


    /**
     * 把对象转化成为HashMap
     *
     * @param objReq
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings("rawtypes")
    public static HashMap<String, String> objToMap(Object objReq, String containsStr, boolean isContains)
            throws IllegalArgumentException, IllegalAccessException {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        Class clazz = objReq.getClass();
        List<Class> clazzs = new ArrayList<Class>();
        do {
            clazzs.add(clazz);
            clazz = clazz.getSuperclass();
        } while (!clazz.equals(Object.class));
        for (Class iClazz : clazzs) {
            Field[] fields = iClazz.getDeclaredFields();
            for (Field field : fields) {
                String objVal = null;
                field.setAccessible(true);//去掉java类中的私有声明
                objVal = String.valueOf(field.get(objReq));
                if ("null".equals(objVal) || (isContains && !field.getName().contains(containsStr)) || field.getName().contains("requestTag") || field.getName().contains("serialVersionUID")) {
                    continue;
                }

                hashMap.put(field.getName(), objVal);
            }
        }
        return hashMap;
    }

    /**
     * 拼接请求url
     *
     * @param args
     * @return
     */
    public static String joinArgs(HashMap<String, String> args) {
        if (args == null || args.size() == 0) {
            return "";
        }
        StringBuffer result = new StringBuffer("");
        for (String str : args.keySet()) {
            String value = args.get(str);
            try {
                value = URLEncoder.encode(args.get(str), "utf-8");
            } catch (UnsupportedEncodingException e) {
            }
            result.append("&").append(str).append("=").append(value);
        }
        return result.toString().substring(1);
    }

    /**
     * 判断app在手机中的状态
     *
     * @param context
     * @return 2在前端 1.在后台 0.没有启动
     */
    public static int isAppRunning(Context context) {
        if (isApplicationTop(context)) {
            return 2;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(context.getPackageName())
                    && info.baseActivity.getPackageName().equals(getApplicationContext().getPackageName())) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 是否为前台进程
     *
     * @param context
     * @return
     */
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

    public static int getHeight(int width, double ratio) {
        if (ratio >= 1.40) {
            ratio = 1.40;
        } else if (ratio <= 1) {
            ratio = 1;
        }
        return (int) (ratio * width + 0.5);
    }

    public static int getAspect(int heigth, double ratio) {
        if (ratio >= 2.0) {
            ratio = 2.0;
        } else if (ratio <= 1) {
            ratio = 1;
        }
        return (int) (ratio * heigth + 0.5);
    }

    /**
     * @author Devis
     * @date 2014-11-17-下午1:41:25
     * @des 把map转成 “,”分割的字符串
     */
    public static String getStringForMap(Map<String, String> imageMd5) {
        Iterator<String> iter = imageMd5.keySet().iterator();
        String image = new String();
        while (iter.hasNext()) {
            image += imageMd5.get(iter.next()) + ",";
        }
        if (image.endsWith(",")) {
            image = image.substring(0, image.toString().length() - 1);
        }
        Log.e("TTTT", "IMAGE  = " + image);
        return image;
    }


    /**
     * @author Devis
     * @date 2014-11-17-下午1:39:16
     * @des 把map转成数组
     */
    public static String[] getArrayFromMap(Map<String, String> imageMd5) {
        return getStringForMap(imageMd5).split(",");
    }

    /**
     * @author wengyiming
     * @date 2015-4-23-上午11:17:16
     * @des 把map转成逗号分割的String
     */
    public static String getStringFromMap(Map<String, String> imageMd5) {
        return getStringForMap(imageMd5);
    }

    public static File getScreenshot() {
        return new File(Environment.getExternalStorageDirectory() + "/formats/");
    }

    public static String getFileName() {
        return String.valueOf((new Date()).getTime()) + ".JPEG";
    }

    public static String loadActivityNewResult(int requestCode, int resultCode, Intent data, Activity context) {
        String path = "";
        if (data != null && data.getData() != null) {
            Uri uri = data.getData();
            if (!TextUtils.isEmpty(uri.getAuthority())) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA},
                        null, null, null);
                if (null == cursor) {
                    // MmdToast.showToast(R.string.prompt_no_image);
                    path = "";
                }
                cursor.moveToFirst();
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
                return path;
            } else {
                return uri.getPath();
            }
        }
        return path;
    }

    /**
     * @author Devis
     * @date 2014-11-13-下午7:08:03
     * @des 如果数字大于一万，保留一位小数 后加 万 字~
     */
    public static String getCountString(int count) {
        if (count < 10000) {
            return count + "";
        } else {
            return String.format("%.1f", (float) (count / 10000) + "万");
        }
    }

    /**
     * @author Devis
     * @date 2014-12-18-下午5:32:43
     * @des 是否有新版本
     */
    public static boolean isNewVersion(String newVersion) {
        try {
            String currentVersionString = getVersionName(false);
            Log.e("TTTT", "newVersion = " + newVersion);
            Log.e("TTTT", "oldVersion = " + currentVersionString);

            String[] newVersions = newVersion.split("\\.");
            String[] currentVersions = currentVersionString.split("\\.");

            if (Integer.parseInt(newVersions[0]) > Integer.parseInt(currentVersions[0])) {
                return true;
            } else if (Integer.parseInt(newVersions[0]) < Integer.parseInt(currentVersions[0])) {
                return false;
            } else {
                if (Integer.parseInt(newVersions[1]) > Integer.parseInt(currentVersions[1])) {
                    return true;
                } else if (Integer.parseInt(newVersions[1]) < Integer.parseInt(currentVersions[1])) {
                    return false;
                } else {
                    if (Integer.parseInt(newVersions[2]) > Integer.parseInt(currentVersions[2])) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @author Devis
     * @date 2014-11-13-下午7:08:03
     * @des 如果数字大于一万，保留一位小数 后加 万 字~
     */

    public static String getCountString(String count) {
        if (count != null && count.length() > 0) {
            int countValue = 0;
            try {
                countValue = Integer.parseInt(count);
            } catch (Exception e) {
            }
            if (countValue < 10000) {
                return count;
            } else {
                return String.format("%.1f", (float) (countValue / 10000) + "万");
            }
        }
        return "";
    }


    /**
     * @author Devis
     * @date 2014-11-14-下午1:43:32
     * @des String to int
     */
    public static int getStringValue(String string) {
        int value = 0;
        try {
            value = Integer.parseInt(string);
        } catch (Exception e) {
            Log.e("TTT PARSE ERROR", "getStringValue error");
        }
        return value;
    }

    /**
     * @author Devis
     * @date 2014-12-26-上午11:22:26
     * @des 销毁缓存文件夹
     */
    public static void deleteDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 存在SD卡
            String path = Environment.getExternalStorageDirectory() + File.separator + "candyjar" + File.separator;
            File dir = new File(path);
            if (dir == null || !dir.exists() || !dir.isDirectory())
                return;

            for (File file : dir.listFiles()) {
                if (file.isFile())
                    file.delete(); // 删除所有文件
                // else if (file.isDirectory())
                // deleteDir(file.getAbsolutePath()); // 递规的方式删除文件夹
            }
            // dir.delete();// 删除目录本身
        }
    }

    /**
     * @author Devis
     * @date 2015-1-14-下午2:20:13
     * @des 从网络获取图片bitmap资源
     */
    public static Bitmap getBitmap(String url) {
        if (url == null || url.length() <= 0) {
            return null;
        }

        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    /**
     * *******
     *
     * @author: Devis 2015-3-23 下午3:26:23
     * @desc : 处理距离字段
     * ********
     */
    public static String getMyDistance(String distance) {
        String resultString = "";
        try {
            int dis = Integer.parseInt(distance);
            if (dis < 1000) {
                resultString = String.valueOf((dis / 10 + 1) * 10);
                resultString = resultString.concat("m");
            } else {
                DecimalFormat df = new DecimalFormat("0.0");
                resultString = String.valueOf(df.format((float) dis / 1000));
                resultString = resultString.concat("km");
            }
            resultString = "<".concat(resultString);
        } catch (Exception e) {
            resultString = "";
        }
        return resultString;
    }


    /**
     * *******
     *
     * @author: Devis 2015-4-10 上午9:54:49
     * @desc : 禁用换行
     * ********
     */
    public static void forbidEnter(EditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
    }

    /**
     * *******
     *
     * @author: Devis 2015-4-13 上午10:57:36
     * @desc : 数组转字符串
     * ********
     */
    public static String arrayToString(String[] array) {
        String result = "";
        if (array != null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                result = result.concat(array[i]);
                if (i < array.length - 1) {
                    result = result.concat(",");
                }
            }
            Log.e("TTT", "result is :" + result);
            return result;
        } else {
            return "";
        }
    }

    /**
     * *******
     *
     * @author: Devis 2015-4-13 上午11:03:11
     * @desc : 字符串转数组
     * ********
     */
    public static String[] stringToArray(String string) {
        if (string != null && string.length() > 0) {
            return string.split(",");
        } else {
            return new String[0];
        }
    }

    /**
     * *******
     *
     * @author: Devis 2015-4-13 上午11:21:37
     * @desc : list 转 string
     * ********
     */
    public static String listToString(List<String> list) {
        String result = "";
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                result = result.concat(list.get(i));
                if (i < list.size() - 1) {
                    result = result.concat(",");
                }
            }
        }
        Log.e("TTT", "result is :" + result);
        return result;
    }

    /**
     * *******
     *
     * @author: Devis 2015-4-13 上午11:03:11
     * @desc : 字符串转list
     * ********
     */
    public static List<String> stringToList(String string) {
        List<String> resultList = new ArrayList<String>();
        if (string != null && string.length() > 0) {
            String[] array = string.split(",");
            if (array != null && array.length > 0) {
                for (int i = 0; i < array.length; i++) {
                    resultList.add(array[i]);
                }
            }
            return resultList;
        } else {
            return resultList;
        }
    }

    public static DisplayImageOptions getSquareOptions() {
        return squareOptions;
    }

    public static void setSquareOptions(DisplayImageOptions squareOptions) {
        GlobalUtils.squareOptions = squareOptions;
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

    public static String getSid() {
        return "";
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
     * @return
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
    public static boolean checkNetState(Context context) {
        boolean netstate = false;
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
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