package xyz.homapay.hampay.mobile.android.img;

import android.content.Context;
import android.os.Build;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.homapay.hampay.mobile.android.ssl.HamPayX509TrustManager;
import xyz.homapay.hampay.mobile.android.ssl.SSLKeyStore;
import xyz.homapay.hampay.mobile.android.util.Connectivity;

/**
 * Created by mohammad on 12/30/16.
 */

public class TrustedOkHttpClient {

    private static final String CACHE_CONTROL = "Cache-Control";

    private static Interceptor provideOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();
            CacheControl cacheControl;
            cacheControl = new CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .onlyIfCached()
                    .build();
            request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build();
            return chain.proceed(request);
        };
    }

    private static Interceptor provideCacheInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());
            CacheControl cacheControl;
            cacheControl = new CacheControl.Builder()
                    .maxAge(2, TimeUnit.MINUTES)
                    .build();
            return response.newBuilder()
                    .header(CACHE_CONTROL, cacheControl.toString())
                    .build();
        };
    }

    public static OkHttpClient getTrustedOkHttpClient(Context ctx) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustedCerts = new X509TrustManager[]{new HamPayX509TrustManager(new SSLKeyStore(ctx).getAppKeyStore())};

            // Install the all-trusting trust manager
            final SSLContext sslContext;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sslContext = SSLContext.getInstance("TLSv1.2");
            } else {
                sslContext = SSLContext.getInstance("TLS");
            }
            sslContext.init(null, trustedCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient;

            if (Connectivity.isConnected(ctx)) {
                okHttpClient = new OkHttpClient().newBuilder()
                        .addNetworkInterceptor(provideCacheInterceptor())
                        .retryOnConnectionFailure(true)
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                        .cache(ImageCacheProvider.getInstance(ctx).provideCache())
                        .build();
            } else {
                okHttpClient = new OkHttpClient().newBuilder()
                        .addInterceptor(provideOfflineCacheInterceptor())
                        .retryOnConnectionFailure(true)
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                        .cache(ImageCacheProvider.getInstance(ctx).provideCache())
                        .build();
            }
            return okHttpClient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static OkHttpClient getTrustedOkHttpClient(Context ctx, Interceptor interceptor) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustedCerts = new X509TrustManager[]{new HamPayX509TrustManager(new SSLKeyStore(ctx).getAppKeyStore())};

            // Install the all-trusting trust manager
            final SSLContext sslContext;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sslContext = SSLContext.getInstance("TLSv1.2");
            } else {
                sslContext = SSLContext.getInstance("TLS");
            }
            sslContext.init(null, trustedCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient;

            if (Connectivity.isConnected(ctx)) {
                okHttpClient = new OkHttpClient().newBuilder()
                        .addNetworkInterceptor(provideCacheInterceptor())
                        .addNetworkInterceptor(interceptor)
                        .retryOnConnectionFailure(true)
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                        .cache(ImageCacheProvider.getInstance(ctx).provideCache())
                        .build();
            } else {
                okHttpClient = new OkHttpClient().newBuilder()
                        .addInterceptor(provideOfflineCacheInterceptor())
                        .addNetworkInterceptor(interceptor)
                        .retryOnConnectionFailure(true)
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                        .cache(ImageCacheProvider.getInstance(ctx).provideCache())
                        .build();
            }
            return okHttpClient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
