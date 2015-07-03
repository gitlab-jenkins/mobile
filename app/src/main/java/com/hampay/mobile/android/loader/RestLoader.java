package com.hampay.mobile.android.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.hampay.mobile.android.common.exception.RESTException;
import com.hampay.mobile.android.messaging.HttpClientHelper;
import com.hampay.mobile.android.messaging.MessageDispatcher;
import com.hampay.mobile.android.messaging.RestErrorListener;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.ServerURLs;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class RestLoader extends AsyncTaskLoader<RestLoader.RestResponse> {
    private static final String TAG = RestLoader.class.getName();

    private RestErrorListener errorListener;

    public RestErrorListener getErrorListener() {
        return errorListener;
    }

    public void setErrorListener(RestErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    // We use this delta to determine if our cached data is
    // old or not. The value we have here is 10 minutes;

    public enum HTTPVerb {
        GET,
        POST,
        PUT,
        DELETE
    }

    public static class RestResponse {
        private String mData;
        private int mCode;

        public RestResponse() {
        }

        public RestResponse(String data, int code) {
            mData = data;
            mCode = code;
        }

        public String getData() {
            return mData;
        }

        public int getCode() {
            return mCode;
        }
    }

    private HTTPVerb mVerb;
    private Uri mAction;
    private Bundle mParams;
    private Bundle headerParams;
    private RestResponse mRestResponse;

    private long mLastLoad;

    public RestLoader(Context context) {
        super(context);
    }

    public RestLoader(Context context, HTTPVerb verb, Uri action) {
        super(context);

        mVerb = verb;
        mAction = action;
    }

    public RestLoader(Context context, HTTPVerb verb, Uri action, Bundle params) {
        super(context);

        mVerb = verb;
        mAction = action;
        mParams = params;
    }

    public void setHeaderParams(Bundle headerParams) {
        this.headerParams = headerParams;
    }

    @Override
    public RestResponse loadInBackground() {
        try {
            if (mRestResponse == null) {
                mRestResponse = callRest(mAction.toString(), mParams);
                return mRestResponse;
            } else {
                deliverResult(mRestResponse);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void deliverResult(RestResponse data) {
        // Here we cache our response.
        mRestResponse = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        /*if (mRestResponse != null) {
            // We have a cached result, so we can just
            // return right away.
            super.deliverResult(mRestResponse);
        }*/

        // If our response is null or we have hung onto it for a long time,
        // then we perform a force load.

//        if (mRestResponse == null ) forceLoad();
    }

    @Override
    protected void onStopLoading() {
        // This prevents the AsyncTask backing this
        // loader from completing if it is currently running.
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Stop the Loader if it is currently running.
        onStopLoading();

        // Get rid of our cache if it exists.
        mRestResponse = null;
//        isSend = false;

        // Reset our stale timer.
        mLastLoad = 0;
    }

    private RestResponse callRest(String url, Bundle mParams) throws Exception {

//        HttpClient httpClient = new HttpClientHelper().getHttpClient();
        HttpClient httpsClient = new HttpClientHelper().getSSLClient(getContext(), ServerURLs.SERVER_PORT, "https");

        HttpHost host = new HttpHost(ServerURLs.SERVER_IP, ServerURLs.SERVER_PORT, "https");
//        HttpHost host = new HttpHost(ServerURLs.SERVER_IP, ServerURLs.SERVER_PORT);
        HttpRequest request = null;

        HTTPVerb httpVerb = MessageDispatcher.getInstance(getContext()).getMessageType().getVerb();
        String posix = MessageDispatcher.getInstance(getContext()).getMessageType().getPosix();
        switch (httpVerb) {
            case GET:
                for( String key : mParams.keySet() ) {
                    String paramValue = (String)mParams.get(key);
                    url = url + "/" + paramValue;
                }
                if( posix != null && posix.length() > 0 ) {
                    url = url + "/" + posix;
                }

                request = new HttpGet(url);
                break;
            case POST:
                String jsonEntity = null;
                for( String key : mParams.keySet() ) {
                    if( key.equals(Constants.REQUEST_JSON_ENTITY) ) {
                        jsonEntity = (String)mParams.get(key);
                    } else {
                        String paramValue = (String)mParams.get(key);
                        url = url + "/" + paramValue;
                    }
                }
                if( posix != null && posix.length() > 0 ) {
                    url = url + "/" + posix;
                }

                request = new HttpPost(url);
                if( jsonEntity != null ) {
                    try {
                        StringEntity entity = new StringEntity(jsonEntity, "UTF-8");
                        entity.setContentType("application/json");
                        ((HttpPost) request).setEntity(entity);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PUT:
                jsonEntity = null;
                for( String key : mParams.keySet() ) {
                    if( key.equals(Constants.REQUEST_JSON_ENTITY) ) {
                        jsonEntity = (String)mParams.get(key);
                    } else {
                        String paramValue = (String)mParams.get(key);
                        url = url + "/" + paramValue;
                    }
                }
                if( posix != null && posix.length() > 0 ) {
                    url = url + "/" + posix;
                }

                request = new HttpPut(url);
                if( jsonEntity != null ) {
                    try {
                        StringEntity entity = new StringEntity(jsonEntity, Constants.UTF_ENCODING);
                        entity.setContentType(Constants.JSON_CONTENT_TYPE);
                        ((HttpPut) request).setEntity(entity);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        try {

            if( request == null ) {
                Log.d(TAG, "Request is null...");
                throw new RESTException("Request is null");
            }

            if( MessageDispatcher.getInstance(getContext()).getTokenId() != null ) {
                request.addHeader("X-Token", MessageDispatcher.getInstance(getContext()).getTokenId());
            }

            if( headerParams != null ) {
                for( String key : headerParams.keySet() ) {
                    request.addHeader(key, headerParams.getString(key));
                }
            }

//            HttpResponse response = httpClient.execute(host, request);
            HttpResponse response = httpsClient.execute(host, request);

            HttpEntity responseEntity = response.getEntity();
            StatusLine responseStatus = response.getStatusLine();
            int statusCode = responseStatus != null ? responseStatus.getStatusCode() : 0;
            String result = EntityUtils.toString(responseEntity);

            return new RestResponse(responseEntity != null ? result : null, statusCode);
        } catch (NullPointerException e) {
            e.printStackTrace();
            if( errorListener != null )
                errorListener.onError(e);
            throw new RESTException(e.getMessage());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            if( errorListener != null )
                errorListener.onError(e);
            throw new RESTException(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            if( errorListener != null )
                errorListener.onError(e);
            throw new RESTException(e.getMessage());
        } finally {
//            httpClient.getConnectionManager().shutdown();
            httpsClient.getConnectionManager().shutdown();
        }
    }
}

