package xyz.homapay.hampay.mobile.android.p.pending;

import xyz.homapay.hampay.common.core.model.enums.FundType;

/**
 * Created by mohammad on 1/22/17.
 */

public interface PendingPayment {

    void getList(FundType type);

}
