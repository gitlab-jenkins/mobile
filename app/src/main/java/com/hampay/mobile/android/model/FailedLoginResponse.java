package com.hampay.mobile.android.model;

/**
 * Created by amir on 7/23/15.
 */
public class FailedLoginResponse {

    int code;
    String reason;
    String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
