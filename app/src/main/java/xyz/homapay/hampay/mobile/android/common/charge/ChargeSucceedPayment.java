package xyz.homapay.hampay.mobile.android.common.charge;

import xyz.homapay.hampay.mobile.android.model.SucceedPayment;

/**
 * Created by mohammad on 1/13/2017 AD.
 */

public class ChargeSucceedPayment extends SucceedPayment {

    private ChargeType chargeType;

    public ChargeSucceedPayment(ChargeType chargeType) {
        this.chargeType = chargeType;
    }

    public ChargeType getChargeType() {
        return chargeType;
    }

    public void setChargeType(ChargeType chargeType) {
        this.chargeType = chargeType;
    }
}
