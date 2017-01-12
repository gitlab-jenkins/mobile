package xyz.homapay.hampay.mobile.android.p.topup;

import xyz.homapay.hampay.common.common.ChargePackage;
import xyz.homapay.hampay.common.common.Operator;

/**
 * Created by mohammad on 1/12/2017 AD.
 */

public interface TopUpCreate {

    void create(Operator operator, String cellPhoneNumber, ChargePackage chargePackage, String chargeType);

}
