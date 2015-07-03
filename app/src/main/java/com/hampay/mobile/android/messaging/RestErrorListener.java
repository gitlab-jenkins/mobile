package com.hampay.mobile.android.messaging;


import com.hampay.mobile.android.common.exception.RESTException;

public interface RestErrorListener {

    public void onError(RESTException e);

    public void onError(Exception e);

}
