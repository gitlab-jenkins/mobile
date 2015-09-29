package xyz.homapay.hampay.mobile.android.model;

/**
 * Created by amir on 7/23/15.
 */
public class FailedLoginResponse {

    String code;
    String reason;
    String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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
