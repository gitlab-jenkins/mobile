package xyz.homapay.hampay.mobile.android.model;

/**
 * Created by amir on 5/25/16.
 */
public class ViewedPaymentRequest {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private int id;
    private String code;

}
