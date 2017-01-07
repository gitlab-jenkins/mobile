package xyz.homapay.hampay.mobile.android.m.common;

import android.os.Build;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.response.DecryptedResponseInfo;
import xyz.homapay.hampay.mobile.android.img.ImageCacheProvider;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchangerImpl;
import xyz.homapay.hampay.mobile.android.ssl.HamPayX509TrustManager;

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

    private static Interceptor provideDecryptor(boolean gzip) {
        return chain -> {
            try {
                Response response = chain.proceed(chain.request());
                String strDeCompress;
                if (gzip) {
                    strDeCompress = decompress(response.body().bytes());
                } else {
                    strDeCompress = response.body().string();
                }
                DecryptedResponseInfo decryptedResponseInfo = new AESMessageEncryptor().decryptResponse(strDeCompress, KeyExchangerImpl.getKey(), KeyExchangerImpl.getIv());
                if (decryptedResponseInfo.getResponseCode() == 0) {
                    Response.Builder resBuilder = response.newBuilder();
                    resBuilder.body(ResponseBody.create(MediaType.parse("application/json"), decryptedResponseInfo.getPayload()));
                    return resBuilder.build();
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    private static String decompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }

    public static OkHttpClient getTrustedOkHttpClient(ModelLayer modelLayer, boolean decryptEnabled, boolean gzipEnabled) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustedCerts = new X509TrustManager[]{new HamPayX509TrustManager(modelLayer.getSSLKeyStore().getAppKeyStore())};

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
            if (modelLayer.isConnected()) {
                if (decryptEnabled) {
                    okHttpClient = new OkHttpClient().newBuilder()
                            .addNetworkInterceptor(provideCacheInterceptor())
                            .addInterceptor(provideDecryptor(gzipEnabled))
                            .retryOnConnectionFailure(true)
                            .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                            .cache(ImageCacheProvider.getInstance(modelLayer).provideCache())
                            .build();
                } else {
                    okHttpClient = new OkHttpClient().newBuilder()
                            .addNetworkInterceptor(provideCacheInterceptor())
                            .retryOnConnectionFailure(true)
                            .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                            .cache(ImageCacheProvider.getInstance(modelLayer).provideCache())
                            .build();
                }
            } else {
                if (decryptEnabled) {
                    okHttpClient = new OkHttpClient().newBuilder()
                            .addInterceptor(provideOfflineCacheInterceptor())
                            .addInterceptor(provideDecryptor(gzipEnabled))
                            .retryOnConnectionFailure(true)
                            .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                            .cache(ImageCacheProvider.getInstance(modelLayer).provideCache())
                            .build();
                } else {
                    okHttpClient = new OkHttpClient().newBuilder()
                            .addInterceptor(provideOfflineCacheInterceptor())
                            .retryOnConnectionFailure(true)
                            .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                            .cache(ImageCacheProvider.getInstance(modelLayer).provideCache())
                            .build();
                }
            }
            return okHttpClient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static OkHttpClient getTrustedOkHttpClient(ModelLayer modelLayer, Interceptor interceptor) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustedCerts = new X509TrustManager[]{new HamPayX509TrustManager(modelLayer.getSSLKeyStore().getAppKeyStore())};

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
            if (modelLayer.isConnected()) {
                okHttpClient = new OkHttpClient().newBuilder()
                        .addNetworkInterceptor(provideCacheInterceptor())
                        .addNetworkInterceptor(interceptor)
                        .retryOnConnectionFailure(true)
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                        .cache(ImageCacheProvider.getInstance(modelLayer).provideCache())
                        .build();
            } else {
                okHttpClient = new OkHttpClient().newBuilder()
                        .addInterceptor(provideOfflineCacheInterceptor())
                        .addNetworkInterceptor(interceptor)
                        .retryOnConnectionFailure(true)
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                        .cache(ImageCacheProvider.getInstance(modelLayer).provideCache())
                        .build();
            }
            return okHttpClient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
