package com.commonrail.mtf.util.retrofit;

import android.content.pm.PackageManager;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.L;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control",
                            String.format("max-age=%d", 60))
                    .build();
        }
    };
    private static OkHttpClient sInstance;

    @SuppressWarnings("ConstantConditions")
    public static OkHttpClient getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpClient();
                    File cacheFile = new File(AppClient.getInstance().getCacheDir(), AppClient.getInstance().getExternalCacheDir().getPath());
                    Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb

                     sInstance = new OkHttpClient.Builder()
                            .addInterceptor(new LoggingInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(15, TimeUnit.SECONDS)
                             .cache(cache)
                            .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
                            .build();
                    
                }
            }
        }
        return sInstance;
    }


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


            L.e(TAG, "deviceMac：" + deviceMac);
            L.e(TAG, "osName" + osName);
            L.e(TAG, "appName" + appName);


            Request request = original.newBuilder()
//                    .header("Cache-Control", "public")
//                    .header("max-age", "604800")
//                    .header("max-stale", "2419200")

//                    .header("terminal-type", "pad")
//                    .header("device-number", deviceId)
                    .header("device-mac", deviceMac)
                    .header("device-imei", deviceId)
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
            L.e(TAG, String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            L.e(TAG, String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }

    public static void setCertificates(InputStream... certificates) throws KeyStoreException {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                }
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "railtoolapi".toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
