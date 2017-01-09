package xyz.homapay.hampay.mobile.android.model;

import java.io.Serializable;

/**
 * Created by amir on 1/8/17.
 */

public class SucceedPayment implements Serializable {

    private long amount;
    private String code;
    private String trace;
    private PaymentType paymentType;

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

}
