package xyz.homapay.hampay.mobile.android.img;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.core.model.request.RetrieveImageRequest;
import xyz.homapay.hampay.mobile.android.m.common.HttpLoggerLayer;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.JsonEncryptor;

/**
 * Created by mohammad on 12/30/16.
 */

public class PicassoInterceptor implements Interceptor {

    private Context ctx;
    private SharedPreferences prefs;
    private String authToken;

    public PicassoInterceptor(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        prefs = ctx.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        final Request original = chain.request();
        final String[] splitted = original.url().toString().split("/");
        final String imageId = splitted[splitted.length - 1];
        RetrieveImageRequest imageRequest = new RetrieveImageRequest();
        imageRequest.setImageId(imageId);
        imageRequest.setRequestUUID(UUID.randomUUID().toString());

        RequestMessage<RetrieveImageRequest> requestMessage = new RequestMessage<>(imageRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
        String requestJsonBody = new Gson().toJson(requestMessage);

        RequestBody body = new MyBody(requestJsonBody);
        final Request request = original.newBuilder()
                .url(original.url())
                .method("POST", body)
                .post(body)
                .addHeader("Transfer-Encoding", "chunked")
                .build();

        Response response = null;

        try {
            response = new HttpLoggerLayer(HttpLoggerLayer.Level.BODY).proceedAndLog(chain, request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response == null) {
            try {
                response = new HttpLoggerLayer(HttpLoggerLayer.Level.BODY).proceedAndLog(chain, request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    class MyBody extends RequestBody {

        private String json;

        public MyBody(String json) {
            this.json = json;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse("application/json; charset=utf-8");
        }

        @Override
        public void writeTo(BufferedSink bufferedSink) throws IOException {
            try {
                bufferedSink.writeUtf8(new JsonEncryptor().encrypt(ctx, json));
            } catch (EncryptionException e) {
                e.printStackTrace();
            }
        }
    }

}