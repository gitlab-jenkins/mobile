package xyz.homapay.hampay.mobile.android.p.business;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.enums.BizSortFactor;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.business.BusinessNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/28/2017 AD.
 */

public class BusinessListImpl extends Presenter<BusinessListView> implements BusinessList, OnNetworkLoadListener<ResponseMessage<BusinessListResponse>> {

    private int currentPage = 0;
    private int pageSize = Constants.DEFAULT_PAGE_SIZE;
    private BizSortFactor bizSortFactor;

    public BusinessListImpl(ModelLayer modelLayer, BusinessListView view) {
        super(modelLayer, view);
    }

    @Override
    public void loadMore() {
        try {
            view.showProgress();
            currentPage++;
            keyExchange();
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void load(BizSortFactor sortFactor) {
        try {
            view.showProgress();
            currentPage = 0;
            this.bizSortFactor = sortFactor;
            keyExchange();
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void onKeyExchangeDone() {
        try {
            BusinessListRequest request = new BusinessListRequest();
            request.setRequestUUID(UUID.randomUUID().toString());
            request.setPageNumber(currentPage);
            request.setPageSize(pageSize);
            request.setSortFactor(bizSortFactor);

            RequestMessage<BusinessListRequest> businessListRequest = new RequestMessage<>(request, modelLayer.getAuthToken(), Constants.API_LEVEL, System.currentTimeMillis());
            String strJson = new AESMessageEncryptor().encryptRequest(new Gson().toJson(businessListRequest), getKey(), getIv(), getEncId());
            new BusinessNetWorker(modelLayer, getKeyAgreementModel(), true, false).businessList(strJson, this);
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void onKeyExchangeError() {
        try {
            view.onError();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<BusinessListResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onListLoaded(status, data, message);
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }
}
