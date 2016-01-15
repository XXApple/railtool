package com.fengx.railtool.util.retrofit;

import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.fengx.railtool.AppClient;
import com.fengx.railtool.util.common.AppUtils;
import com.fengx.railtool.util.common.L;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;


/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：15/11/16 下午10:34
 * 修改人：wengyiming
 * 修改时间：15/11/16 下午10:34
 * 修改备注：
 */
public class OkHttpClientManager {
    private static final String TAG = "OkHttpClientManager";
    private static com.squareup.okhttp.OkHttpClient sInstance;

    @SuppressWarnings("ConstantConditions")
    public static com.squareup.okhttp.OkHttpClient getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (sInstance == null) {
                    sInstance = new com.squareup.okhttp.OkHttpClient();
                    //cookie enabled
                    sInstance.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
                    //从主机读取数据超时
                    sInstance.setReadTimeout(15, TimeUnit.SECONDS);
                    //连接主机超时
                    sInstance.setConnectTimeout(20, TimeUnit.SECONDS);
                    sInstance.interceptors().add(new LoggingInterceptor());
                    sInstance.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);
                    File cacheFile = new File(AppClient.getInstance().getCacheDir(), AppClient.getInstance().getExternalCacheDir().getPath());
                    Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
                    sInstance.setCache(cache);

                }
            }
        }
        return sInstance;
    }


    static Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(final Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            Request request = chain.request();
            String cacheControl = request.cacheControl().toString();
            if (TextUtils.isEmpty(cacheControl)) {
                cacheControl = "no-cache";
            }
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma").build();
        }

    };


    /**
     * see http://stackoverflow.com/questions/24952199/okhttp-enable-logs
     */
    static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            String deviceId = AppUtils.getLocaldeviceId();
            String deviceMac = AppUtils.getLocalMacAddress();
            String osName = "android" + android.os.Build.VERSION.RELEASE;
            PackageManager pm = AppClient.getInstance().getPackageManager();
            String appName = AppClient.getInstance().getApplicationInfo().loadLabel(pm).toString();


            L.e("deviceMac：" + "123456");
            L.e("osName" + osName);
            L.e("appName" + appName);


            Request request = original.newBuilder()
                    .header("Cache-Control", "public")
                    .header("max-age", "604800")
                    .header("max-stale", "2419200")

//                    .header("terminal-type", "pad")
//                    .header("device-number", deviceId)
                    .header("device-mac", "123456")
//                    .header("device-imei", deviceId)
//                    .header("device-model", deviceId)
//                    .header("os-name",osName)
//                    .header("os-version", android.os.Build.VERSION.RELEASE)
//                    .header("app-name", appName)
//                    .header("app-id", deviceId)
//                    .header("app-version-code", String.valueOf(AppUtils.getVersionCode()))
//                    .header("app-version-name",AppUtils.getVersionName())
                    .header("Content-Type", "application-json")
//                    .header("sid", GlobalUtils.getSid())
                    .method(original.method(), original.body())
                    .build();


            long t1 = System.nanoTime();
            L.e(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            L.e(String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));


            return response;
        }
    }
}
