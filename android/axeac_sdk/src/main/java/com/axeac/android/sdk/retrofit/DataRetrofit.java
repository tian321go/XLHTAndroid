package com.axeac.android.sdk.retrofit;

import android.content.Context;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.axeac.android.sdk.utils.StaticObject;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


public class DataRetrofit {

    private static ApiData service;
    public static CompositeSubscription mCompositeSubscription;
    public static boolean isNeedRefresh = true;


    private static class CookiesManager implements CookieJar {

        private Context context;

        public CookiesManager(Context context) {
            this.context = context;
            cookieStore = new PersistentCookieStore(context.getApplicationContext());
        }

        private PersistentCookieStore cookieStore;

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                for (Cookie item : cookies) {
                    cookieStore.add(url, item);
                }
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url);
            return cookies;
        }
    }

    public static int MAX_IDLE_CONNECTIONS = 30;
    public static int KEEP_ALIVE_DURATION_MS = 3 * 60 * 1000;

    public static Retrofit createRetrofit(Context context) {
        mCompositeSubscription = new CompositeSubscription();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);


        Interceptor mTokenInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request authorised = originalRequest.newBuilder()
                        .addHeader("Connection", "close")
                        .build();
                return chain.proceed(authorised);
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
//                .cookieJar(new CookiesManager(context))
                .addInterceptor(interceptor)
                .connectTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .addNetworkInterceptor(mTokenInterceptor)
                .build();


        OkHttpClient sClient = new OkHttpClient.Builder()
//                .cookieJar(new CookiesManager(context))
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
//                .addNetworkInterceptor(mTokenInterceptor)
                .connectTimeout(180, TimeUnit.SECONDS)
//                .connectionPool(new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION_MS,TimeUnit.MILLISECONDS))
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .build();
//        sClient.interceptors().add(new Interceptor() {
//            @Override
//            public Response intercept(Interceptor.Chain chain) throws IOException {
//                Request original = chain.request();
//
//                Request.Builder requestBuilder = original.newBuilder()
//                        .addHeader("Connection", "close");
//
//                Request request = requestBuilder.build();
//                return chain.proceed(request);
//            }
//        });
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }

        HostnameVerifier hv1 = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        String workerClassName = "okhttp3.OkHttpClient";
        try {
            Class workerClass = Class.forName(workerClassName);
            Field hostnameVerifier = workerClass.getDeclaredField("hostnameVerifier");
            hostnameVerifier.setAccessible(true);
            hostnameVerifier.set(sClient, hv1);
            Field sslSocketFactory = workerClass.getDeclaredField("sslSocketFactory");
            sslSocketFactory.setAccessible(true);
            sslSocketFactory.set(sClient, sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String [] serverName = StaticObject.read.getString(StaticObject.SERVERURL_SERVERNAME, "").split("/");
        int len = serverName.length;
        boolean isHttps = StaticObject.read.getBoolean(StaticObject.SERVERURL_IS_HTTPS, false);
        Retrofit restAdapter = new Retrofit.Builder()
                .client(sClient)
                .baseUrl((isHttps ? "https://" : "http://") + StaticObject.read.getString(StaticObject.SERVERURL_IP, "") +
                        ":" + StaticObject.read.getString(StaticObject.SERVERURL_HTTP_PORT, "") + "/"+(len>1?serverName[0]+"/":""))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return restAdapter;
    }

    public static ApiData getService(Context context) {
        if (service == null || isNeedRefresh) {
            createService(context);
            isNeedRefresh = false;
        }
        return service;
    }

    private static void createService(Context context) {
        service = createRetrofit(context).create(ApiData.class);
    }


    public static void addSubscription(Subscription s) {

        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(s);
    }


}
