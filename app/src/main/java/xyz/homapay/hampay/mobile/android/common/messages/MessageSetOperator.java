package xyz.homapay.hampay.mobile.android.common.messages;

import xyz.homapay.hampay.common.common.Operator;

/**
 * Created by mohammad on 1/10/17.
 */

public class MessageSetOperator {
    private Operator operator;

    public MessageSetOperator(Operator operator) {
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }
}
