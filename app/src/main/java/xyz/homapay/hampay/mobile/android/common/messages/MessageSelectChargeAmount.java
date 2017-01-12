package xyz.homapay.hampay.mobile.android.common.messages;

/**
 * Created by mohammad on 1/11/17.
 */

public class MessageSelectChargeAmount {

    private String amount;
    private int index;

    public MessageSelectChargeAmount(String amount, int index) {
        this.amount = amount;
        this.index = index;
    }

    public String getAmount() {
        return amount;
    }

    public int getIndex() {
        return index;
    }
}
