package xyz.homapay.hampay.mobile.android.common.messages;

import xyz.homapay.hampay.common.common.Operator;

/**
 * Created by mohammad on 1/10/17.
 */

public class MessageSetOperator {
    private Operator operator;
    private String operatorName;

    public MessageSetOperator(Operator operator) {
        this.operator = operator;
        if (operator.equals(Operator.MCI))
            this.operatorName = "MCI";
        else if (operator.equals(Operator.MTN))
            this.operatorName = "MTN";
        else if (operator.equals(Operator.RAYTEL))
            this.operatorName = "RAYTEL";
        else
            this.operatorName = "UNKNOWN";
    }

    public Operator getOperator() {
        return operator;
    }

    public String getOperatorName() {
        return operatorName;
    }
}
