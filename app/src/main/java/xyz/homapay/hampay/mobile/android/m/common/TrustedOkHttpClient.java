package xyz.homapay.hampay.mobile.android.m.common;

import android.os.Build;
import android.util.Log;

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
import xyz.homapay.hampay.mobile.android.img.CacheProvider;
import xyz.homapay.hampay.mobile.android.ssl.HamPayX509TrustManager;

/**
 * Created by mohammad on 12/30/16.
 */

public class TrustedOkHttpClient {

    private static final String CACHE_CONTROL = "Cache-Control";

    private static Interceptor provideOfflineCacheInterceptor() {
        return chain -> {
            Request request = chain.request();
            Response response = null;
            try {
                CacheControl cacheControl;
                cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .onlyIfCached()
                        .build();
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
                response = chain.proceed(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        };
    }

    private static Interceptor provideCacheInterceptor() {
        return chain -> {
            try {
                Response response = chain.proceed(chain.request());
                CacheControl cacheControl;
                cacheControl = new CacheControl.Builder()
                        .maxAge(2, TimeUnit.MINUTES)
                        .build();
                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    private static Interceptor provideDecryptor(KeyAgreementModel keyAgreementModel, boolean gzip) {
        return chain -> {
            try {
                Response response = chain.proceed(chain.request());
                DecryptedResponseInfo decryptedResponseInfo = new AESMessageEncryptor().decryptResponse(deflateGzip(response, gzip), keyAgreementModel.getKey(), keyAgreementModel.getIv());
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

    private static String deflateGzip(Response response, boolean gZip) {
        String strDeCompress = "";
        try {
            if (gZip) {
                strDeCompress = decompress(response.body().bytes());
            } else {
                strDeCompress = response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return strDeCompress;
        }
    }

    private static String decompress(byte[] compressed) throws Exception {
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

    private static Interceptor provideConnectivityInterceptor(ModelLayer modelLayer) {
        return chain -> {
            if (!modelLayer.isConnected())
                throw new NoNetworkException();
            try {
                return chain.proceed(chain.request());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    private static Interceptor provideLogInterceptor() {
        return chain -> new HttpLoggerLayer(HttpLoggerLayer.Level.BODY).proceedAndLog(chain, chain.request());
    }

    public static OkHttpClient getTrustedOkHttpClient(ModelLayer modelLayer, KeyAgreementModel keyAgreementModel, boolean encryption, boolean gZip) {
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

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                    .addNetworkInterceptor(provideLogInterceptor())
                    .addInterceptor(provideConnectivityInterceptor(modelLayer))
                    .cache(CacheProvider.getInstance(modelLayer).provideCache());

            if (modelLayer.isConnected())
                builder.addNetworkInterceptor(provideCacheInterceptor());
            else
                builder.addInterceptor(provideOfflineCacheInterceptor());

            if (encryption)
                builder.addInterceptor(provideDecryptor(keyAgreementModel, gZip));

            return builder.build();
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
                        .addInterceptor(provideConnectionHeader())
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                        .cache(CacheProvider.getInstance(modelLayer).provideCache())
                        .build();
            } else {
                okHttpClient = new OkHttpClient().newBuilder()
                        .addInterceptor(provideOfflineCacheInterceptor())
                        .addNetworkInterceptor(interceptor)
                        .retryOnConnectionFailure(true)
                        .addInterceptor(provideConnectionHeader())
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustedCerts[0])
                        .cache(CacheProvider.getInstance(modelLayer).provideCache())
                        .build();
            }
            return okHttpClient;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Interceptor provideConnectionHeader() {
        return chain -> {
            Request request = chain.request();
            Request newReq = request.newBuilder().addHeader("Connection", "close")
                    .build();
            Response response = chain.proceed(newReq);
            Log.i("XXXX-HEADER", "Header added.");
            return response;
        };
    }

}
