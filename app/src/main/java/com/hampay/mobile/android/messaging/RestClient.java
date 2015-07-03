package com.hampay.mobile.android.messaging;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.common.exception.RESTException;
import com.hampay.mobile.android.dialog.AlertUtils;
import com.hampay.mobile.android.loader.RestLoader;
import com.hampay.mobile.android.util.Constants;

import java.util.Timer;
import java.util.TimerTask;


public class RestClient implements LoaderManager.LoaderCallbacks<RestLoader.RestResponse>, RestErrorListener {

    private static final String TAG = RestClient.class.getName();
    private static RestClient instance;
    private Context context;
    private RestLoader loader;
    private Handler handler;
    private Timer timer;
    private TimerTask timerTask;
    private RestClient() {

    }

    public static RestClient getInstance() {
        if( instance == null )
            instance = new RestClient();
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public void sendMessage(Context context, Bundle messageBundle, Bundle headerParams) {
        this.context = context;

        handler = new Handler();

        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if( AlertUtils.getInstance().isProgressDialogShowing() ) {
                            AlertUtils.getInstance().hideProgressDialog();
                            if( loader != null ) {
                                loader.stopLoading();
                                if( getContext()  instanceof Activity) {
                                    ( (Activity)getContext() ).getLoaderManager().destroyLoader(0x1);
                                }
                                MessageDispatcher.getInstance(getContext()).setMessageCanceled(true);
                                AlertUtils.getInstance().showConfirmDialog(getContext(),
                                        getContext().getString(R.string.alert_error_title),
                                        getContext().getString(R.string.alert_server_no_response),
                                        getContext().getString(R.string.ok_button), null, null);
                            }
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, Constants.MESSAGE_TIMEOUT);

        try {
            loader = (RestLoader)( (Activity)getContext() ).getLoaderManager().initLoader(0x1, messageBundle, this);
            loader.setErrorListener(this);
            loader.setHeaderParams(headerParams);
            loader.onContentChanged();
            loader.forceLoad();
        } catch (Exception e ) {
            loader.stopLoading();
            Log.e(TAG, e.getMessage(), e);
        }

    }

    public Loader<RestLoader.RestResponse> onCreateLoader(int i, Bundle bundle) {
        if (bundle != null && bundle.containsKey(Constants.ARGS_URI) && bundle.containsKey(Constants.ARGS_PARAMS)) {
            Uri action = bundle.getParcelable(Constants.ARGS_URI);
            Bundle params = bundle.getParcelable(Constants.ARGS_PARAMS);

            return new RestLoader(getContext(), RestLoader.HTTPVerb.GET, action, params);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<RestLoader.RestResponse> loader, RestLoader.RestResponse data) {
        if( data == null || data.getData() == null ) {
            loader.stopLoading();
            if( getContext()  instanceof Activity) {
                ( (Activity)getContext() ).getLoaderManager().destroyLoader(0x1);
            }
            if( timer != null ) {
                timer.cancel();
                timer.purge();
            }
            MessageDispatcher.getInstance(context).dispatchIncomingMessage("", -1, new Exception("Null Response!"));
            return;
        }

        String message = data.getData();
        MessageDispatcher.getInstance(context).dispatchIncomingMessage(message, data.getCode(), null);
        loader.stopLoading();
        if( getContext()  instanceof Activity) {
            ( (Activity)getContext() ).getLoaderManager().destroyLoader(0x1);
        }
    }

    @Override
    public void onLoaderReset(Loader<RestLoader.RestResponse> loader) {

    }

    @Override
    public void onError(RESTException e) {

    }

    @Override
    public void onError(Exception e) {
        MessageDispatcher.getInstance(context).dispatchIncomingMessage(null, -1, e);
        loader.stopLoading();
        if( getContext()  instanceof Activity) {
            ( (Activity)getContext() ).getLoaderManager().destroyLoader(0x1);
        }
        if( timer != null ) {
            timer.cancel();
            timer.purge();
        }
    }
}
