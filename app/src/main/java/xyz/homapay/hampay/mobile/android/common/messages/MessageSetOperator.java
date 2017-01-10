package xyz.homapay.hampay.mobile.android.common.messages;

import xyz.homapay.hampay.mobile.android.util.TelephonyUtils;

/**
 * Created by mohammad on 1/10/17.
 */

public class MessageSetOperator {
    private TelephonyUtils.IranMobileOperators operator;

    public MessageSetOperator(TelephonyUtils.IranMobileOperators operator) {
        this.operator = operator;
    }

    public TelephonyUtils.IranMobileOperators getOperator() {
        return operator;
    }
}
