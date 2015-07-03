package com.hampay.mobile.android.messaging;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.HamPayLoginActivity;
import com.hampay.mobile.android.dialog.AlertUtils;
import com.hampay.mobile.android.loader.RestLoader;
import com.hampay.mobile.android.util.Constants;


public class MessageDispatcher {

    private static final String TAG = MessageDispatcher.class.getName();
    private static MessageDispatcher instance;
    private MessageType messageType;
    private String tokenId;
    private Context context;
    private boolean isMessageCanceled;

    //get ha dar path, post ha dar body

    public static enum MessageType {
        LOGIN("auth" , "", "login", RestLoader.HTTPVerb.POST),
        LOGOUT("unauth" , "", "logout", RestLoader.HTTPVerb.POST),
        TAC("tac" , "", "tac", RestLoader.HTTPVerb.GET),
        ACCEPT_TAC("tac" , "", "acceptTac", RestLoader.HTTPVerb.POST);
//        PRODUCTS("assets" , "", "products", RestLoader.HTTPVerb.GET),
//        FUND_DETAIL("funds" , "", "fundDetail", RestLoader.HTTPVerb.GET),
//        ASSETS("assets", "", "assets", RestLoader.HTTPVerb.GET),
//        ASSET_DETAIL("assets" , "", "assetDetail", RestLoader.HTTPVerb.GET),
//        DISMISS("orders" , "", "dismiss", RestLoader.HTTPVerb.POST),
//        ORDERS("orders" , "", "orders", RestLoader.HTTPVerb.GET),
//        ORDER_DETAIL("orders" , "", "orderDetail", RestLoader.HTTPVerb.GET),
//        SAVE_BASKET("basket" , "", "saveBasket", RestLoader.HTTPVerb.PUT),
//        REMOVE_BASKET_ITEM("basket" , "", "removeBasketItem", RestLoader.HTTPVerb.PUT),
//        FETCH_BASKET("basket" , "", "fetchBasket", RestLoader.HTTPVerb.GET),
//        SUBMIT_BASKET("basket" , "", "submitBasket", RestLoader.HTTPVerb.POST),
//        CUSTOMER_VERIFICATION("users" , "", "customerVerification", RestLoader.HTTPVerb.GET),
//        ADDITIONAL_INFO("customers" , "", "additionalInfo", RestLoader.HTTPVerb.POST);

        private String path;
        private String posix;
        private String service;
        private RestLoader.HTTPVerb verb;

        MessageType(String path,String posix, String service, RestLoader.HTTPVerb verb) {
            this.path = path;
            this.posix = posix;
            this.service = service;
            this.verb = verb;
        }



        public String getService() {
            return service;
        }

        public String getPath() {
            return path;
        }

        public String getPosix() {
            return posix;
        }

        public RestLoader.HTTPVerb getVerb() {
            return verb;
        }

        public static MessageType findByService(String service) {
            MessageType[] messageTypes = MessageType.values();
            for (MessageType messageType : messageTypes) {
                if (messageType.getService().equals(service)) {
                    return messageType;
                }
            }
            return null;
        }
    }



    private MessageDispatcher(Context context) {

    }

    public static MessageDispatcher getInstance(Context context) {
        if (instance == null) {
            instance = new MessageDispatcher(context);
        }
        instance.context = context;
        return instance;
    }

    public boolean isMessageCanceled() {
        return isMessageCanceled;
    }

    public void setMessageCanceled(boolean isMessageCanceled) {
        this.isMessageCanceled = isMessageCanceled;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }


    private boolean checkNetworkConnectivity() throws Exception {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for( NetworkInfo networkInfo : networkInfos ) {
            if( networkInfo.isAvailable() && networkInfo.isConnected() )
                return true;
        }

        return false;

        /*if( ( connectivityManager.getNetworkInfo(0) != null &&
                connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ) ||
                ( connectivityManager.getNetworkInfo(1) != null &&
                connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) ) {
            return true;
        } else {
            return false;
        }*/
        /*else if(connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED   ) {

        }*/


        /*ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();*/
    }

    public void dispatchOutgoingMessage(Bundle messageBundle, MessageType messageType) {
        dispatchOutgoingMessage(messageBundle, null, messageType);
    }

    public void dispatchOutgoingMessage(Bundle messageBundle, Bundle headerParamsBundle, MessageType messageType) {
        this.isMessageCanceled = false;
        this.messageType = messageType;
        try {
            if( checkNetworkConnectivity() ) {
                RestClient.getInstance().sendMessage(context, messageBundle, headerParamsBundle);
            } else {
                AlertUtils.getInstance().hideProgressDialog();
                AlertUtils.getInstance().showConfirmDialog(context, context.getString(R.string.alert_connection_error_title), context.getString(R.string.alert_connection_error_message), context.getString(R.string.ok_button), null, null);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            AlertUtils.getInstance().hideProgressDialog();
            AlertUtils.getInstance().showConfirmDialog(context, context.getString(R.string.alert_connection_error_title), context.getString(R.string.alert_connection_error_message), context.getString(R.string.ok_button), null, null);
        }
    }

    public void dispatchIncomingMessage(Exception exception) {
        dispatchIncomingMessage(null, -1, exception);
    }

    public void dispatchIncomingMessage(String message) {
        dispatchIncomingMessage(message, -1, null);
    }

    public void dispatchIncomingMessage(String message, int statusCode, Exception exception) {

        if( statusCode == 401 && messageType != MessageType.LOGIN ) {
            handleSessionExpiry(message);
            return;
        }

//        if( statusCode == 502 || message == null ) {
//            if( isMessageCanceled && !AlertUtils.getInstance().isProgressDialogShowing() ) {
//                messageType = null;
//                isMessageCanceled = false;
//                return;
//            }
//            AlertUtils.getInstance().hideProgressDialog();
//
//            DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
//                @Override
//                public void onDismiss(DialogInterface dialog) {
//                    if( context instanceof BasketActivity ) {
//                        ((BasketActivity)context).onBackPressed();
//                    }
//                }
//            };
//            AlertUtils.getInstance().showConfirmDialog(context, context.getString(R.string.alert_error_title), context.getString(R.string.alert_server_no_response), context.getString(R.string.ok_button), null, onDismissListener, true);
//            messageType = null;
//            return;
//        }

        if( exception != null ) {
            if( isMessageCanceled && !AlertUtils.getInstance().isProgressDialogShowing() ) {
                messageType = null;
                isMessageCanceled = false;
                return;
            }
            AlertUtils.getInstance().hideProgressDialog();
//            AlertUtils.getInstance().showConfirmDialog(context, "Error", "Error communicating with server...",
//                    context.getString(R.string.ok_button), null, null);
            AlertUtils.getInstance().showConfirmDialog(context, context.getString(R.string.alert_error_title), context.getString(R.string.alert_server_is_down_error), context.getString(R.string.ok_button), null, null);
            messageType = null;
            return;
        }

        try {
            switch (messageType) {
                case LOGIN:
                case TAC:
                    startTargetActivity(context, HamPayLoginActivity.class, message, statusCode);
                    break;
//                case PRODUCTS:
//                    if( instance.context != null && (instance.context instanceof TermsAndConditionsActivity) ) {
//                        startTargetActivity(context, TermsAndConditionsActivity.class, message, statusCode);
//                    } else {
//                        startTargetActivity(context, LoginActivity.class, message, statusCode);
//                    }
//
//                    break;
                case ACCEPT_TAC:
//                    startTargetActivity(context, TermsAndConditionsActivity.class, message, statusCode);
                    break;
//                case ASSETS:
//                case ASSET_DETAIL:
//                case ORDERS:
//                case SAVE_BASKET:
//                    startTargetActivity(context, HomeActivity.class, message, statusCode);
//                    break;
//                case REMOVE_BASKET_ITEM:
//                case SUBMIT_BASKET:
//                    startTargetActivity(context, BasketActivity.class, message, statusCode);
//                    break;
//                case DISMISS:
//                    startTargetActivity(context, DismissActivity.class, message, statusCode);
//                    break;
//                case CUSTOMER_VERIFICATION:
//                    startTargetActivity(context, ProductActivity.class, message, statusCode);
//                    break;
//                case ADDITIONAL_INFO:
//                    startTargetActivity(context, AdditionalInfoActivity.class, message, statusCode);
//                    break;
            }

        } catch( Exception e ) {
            Log.e(TAG, e.getMessage());
        }

    }

    private void startTargetActivity(Context context, Class activityClass, String message, int statusCode) {
        Intent targetIntent = new Intent();
        targetIntent.setClass(context, activityClass);
        targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        targetIntent.putExtra(Constants.MESSAGE_RESPONSE_EXTRA, message);
        targetIntent.putExtra(Constants.MESSAGE_STATUS_EXTRA, statusCode);
        targetIntent.putExtra(Constants.MESSAGE_TYPE_EXTRA, messageType.getService());
        context.startActivity(targetIntent);
    }

    private void handleSessionExpiry(String message) {
        DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if( !(context instanceof HamPayLoginActivity) ) {
                    restartApplication();
                }
            }
        };

//        if( context instanceof AbstractActivity ) {
//            ((AbstractActivity)context).handleServerError(message, onDismissListener);
//        } else if( context instanceof BaseActivity ) {
//            ((BaseActivity)context).handleServerError(message, onDismissListener);
//        }

        messageType = null;
    }


    private void restartApplication() {
        Intent loginIntent = new Intent(context, HamPayLoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        int pendingIntentId = 123456;
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                pendingIntentId, loginIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        if( context instanceof Activity)
            ((Activity)context).finish();
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
        System.exit(0);
    }
}