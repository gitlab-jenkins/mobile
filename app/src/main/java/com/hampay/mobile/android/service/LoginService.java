package com.hampay.mobile.android.service;

import android.content.Context;
import android.os.Bundle;

import com.hampay.mobile.android.messaging.MessageDispatcher;
import com.hampay.mobile.android.util.ConnectionHeaders;


public class LoginService extends Service {

    public LoginService(Context context) {
        super(context);
    }

    public void sendLoginRequest(String userId, String password) {

        Bundle params = new Bundle();
        Bundle headerParams = new Bundle();

        headerParams.putString(ConnectionHeaders.KEYS.USER_NAME, userId);
        headerParams.putString(ConnectionHeaders.KEYS.PASSWORD, password);

        Bundle messageBundle = getMessageBundle(MessageDispatcher.MessageType.LOGIN.getPath(), params);

        MessageDispatcher.getInstance(context).dispatchOutgoingMessage(messageBundle, headerParams, MessageDispatcher.MessageType.LOGIN);

    }

    public void sendLogoutRequest(String userId, String password) {

        Bundle params = new Bundle();
        Bundle headerParams = new Bundle();

        headerParams.putString(ConnectionHeaders.KEYS.USER_NAME, userId);
        headerParams.putString(ConnectionHeaders.KEYS.PASSWORD, password);

        Bundle messageBundle = getMessageBundle(MessageDispatcher.MessageType.LOGIN.getPath(), params);

        MessageDispatcher.getInstance(context).dispatchOutgoingMessage(messageBundle, headerParams, MessageDispatcher.MessageType.LOGIN);

    }

    public void sendTacRequest() {

        Bundle params = new Bundle();
        Bundle messageBundle = getMessageBundle(MessageDispatcher.MessageType.TAC.getPath(), params);

        MessageDispatcher.getInstance(context).dispatchOutgoingMessage(messageBundle, MessageDispatcher.MessageType.TAC);

    }

//    public void sendProductsRequest() {
//
//        Bundle params = new Bundle();
//        Bundle messageBundle = getMessageBundle(MessageDispatcher.MessageType.PRODUCTS.getPath(), params);
//
//        MessageDispatcher.getInstance(context).dispatchOutgoingMessage(messageBundle, MessageDispatcher.MessageType.PRODUCTS);
//
//    }

    public void sendAcceptTacRequest() {
        Bundle params = new Bundle();
        Bundle messageBundle = getMessageBundle(MessageDispatcher.MessageType.ACCEPT_TAC.getPath(), params);

        MessageDispatcher.getInstance(context).dispatchOutgoingMessage(messageBundle, MessageDispatcher.MessageType.ACCEPT_TAC);
    }

}
