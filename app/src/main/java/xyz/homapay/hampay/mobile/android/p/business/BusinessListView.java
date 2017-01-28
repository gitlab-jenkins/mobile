package xyz.homapay.hampay.mobile.android.p.business;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.mobile.android.p.common.BaseView;

/**
 * Created by mohammad on 1/28/2017 AD.
 */

public interface BusinessListView extends BaseView {

    void onListLoaded(boolean state, ResponseMessage<BusinessListResponse> data, String message);

}
