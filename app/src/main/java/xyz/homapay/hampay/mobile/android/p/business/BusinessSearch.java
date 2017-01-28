package xyz.homapay.hampay.mobile.android.p.business;

import xyz.homapay.hampay.common.core.model.enums.BizSortFactor;

/**
 * Created by mohammad on 1/28/2017 AD.
 */

public interface BusinessSearch {

    void loadMore();

    void search(String searchTerm, BizSortFactor sortFactor, boolean showProgress);

}
