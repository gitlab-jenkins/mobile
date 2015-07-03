package com.hampay.mobile.android.service;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;
import com.hampay.mobile.android.util.Constants;

import java.util.Set;


public class Service {

    private static final String TAG = Service.class.getName();
    protected Context context;

    public Service(Context context) {
        this.context = context;
    }

    private String buildMessageUri(String path) {
        return Constants.SERVICE_NAMESPACE + path;
    }

    protected Bundle getMessageBundle(String path, Bundle requestParams) {

        Bundle params = new Bundle();
        Set<String> paramsKeySet = requestParams.keySet();
        for( String key : paramsKeySet ) {
            params.putString(key, requestParams.getString(key));
        }

        Uri uri = Uri.parse(buildMessageUri(path));
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARGS_URI, uri);
        args.putParcelable(Constants.ARGS_PARAMS, params);

        return args;
    }

//    public ErrorInfo getErrorResponse(String responseMessage) throws Exception {
//
//        Gson gson = new Gson();
//        ErrorInfo errorInfo = gson.fromJson(responseMessage, ErrorInfo.class);
//        if (errorInfo != null) {
//            return errorInfo;
//        } else {
//            Log.e(TAG, "ErrorInfo is null...");
//            return null;
//        }
//    }
}
