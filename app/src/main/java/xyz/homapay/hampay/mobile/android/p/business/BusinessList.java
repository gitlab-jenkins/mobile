package xyz.homapay.hampay.mobile.android.p.business;

import xyz.homapay.hampay.common.core.model.enums.BizSortFactor;

/**
 * Created by mohammad on 1/28/2017 AD.
 */

public interface BusinessList {

    void loadMore();

    void load(BizSortFactor sortFactor);

}
