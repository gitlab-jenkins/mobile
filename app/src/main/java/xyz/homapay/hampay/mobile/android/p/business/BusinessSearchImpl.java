package xyz.homapay.hampay.mobile.android.p.business;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.enums.BizSortFactor;
import xyz.homapay.hampay.common.core.model.request.BusinessSearchRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.business.BusinessNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/28/2017 AD.
 */

public class BusinessSearchImpl extends Presenter<BusinessListView> implements BusinessSearch, OnNetworkLoadListener<ResponseMessage<BusinessListResponse>> {

    private int currentPage = 0;
    private int pageSize = Constants.DEFAULT_PAGE_SIZE;
    private BizSortFactor bizSortFactor;
    private String searchTerm;

    public BusinessSearchImpl(ModelLayer modelLayer, BusinessListView view) {
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
    public void search(String searchTerm, BizSortFactor sortFactor, boolean showProgress) {
        try {
            if (showProgress)
                view.showProgress();
            this.bizSortFactor = sortFactor;
            this.currentPage = 0;
            this.searchTerm = searchTerm;
            keyExchange();
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

    @Override
    public void onKeyExchangeDone() {
        try {
            BusinessSearchRequest request = new BusinessSearchRequest();
            request.setRequestUUID(UUID.randomUUID().toString());
            request.setPageSize(pageSize);
            request.setPageNumber(currentPage);
            request.setTerm(searchTerm);

            RequestMessage<BusinessSearchRequest> businessSearchRequest = new RequestMessage<>(request, modelLayer.getAuthToken(), Constants.API_LEVEL, System.currentTimeMillis());
            String strJson = new AESMessageEncryptor().encryptRequest(new Gson().toJson(businessSearchRequest), getKey(), getIv(), getEncId());
            new BusinessNetWorker(modelLayer, getKeyAgreementModel(), true, false).businessSearch(strJson, this);

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
