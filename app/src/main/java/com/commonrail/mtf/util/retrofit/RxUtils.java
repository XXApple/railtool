package com.commonrail.mtf.util.retrofit;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：15/11/16 下午10:18
 * 修改人：wengyiming
 * 修改时间：15/11/16 下午10:18
 * 修改备注：
 */
public class RxUtils {
    private static final String TAG = "RxUtils";
//    public static String UA = "acfun/1.0 (Linux; U; Android " + Build.VERSION.RELEASE + "; " +
//            Build.MODEL + "; " + Locale.getDefault().getLanguage() + "-" +
//            Locale.getDefault().getCountry().toLowerCase() +
//            ") AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 ";

    private RxUtils() {

    }

    public static void unsubscribeIfNotNull(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed()) {
            return new CompositeSubscription();
        }

        return subscription;
    }


    public static <T> T createApi(Class<T> c, String url) {
        OkHttpClient client = OkHttpClientManager.getInstance(); //create OKHTTPClient
        Retrofit retrofit = RetrofitManager.getInstance()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build();
        return retrofit.create(c);

    }

}
