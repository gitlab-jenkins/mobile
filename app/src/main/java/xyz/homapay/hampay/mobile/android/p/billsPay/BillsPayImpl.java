package xyz.homapay.hampay.mobile.android.p.billsPay;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.encrypt.MessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.pspproxy.model.request.BillRequest;
import xyz.homapay.hampay.common.pspproxy.model.response.BillResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.billsPay.BillsPayNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 2/4/17.
 */

public class BillsPayImpl extends Presenter<BillsPayView> implements BillsPay, OnNetworkLoadListener<ResponseMessage<BillResponse>> {


    private MessageEncryptor messageEncryptor;
    private BillRequest billRequest;
    private String authToken;
    private byte[] encKey;
    private byte[] ivKey;

    public BillsPayImpl(ModelLayer modelLayer, BillsPayView view){
        super(modelLayer, view);
    }

    private void onError() {
        try {
            view.cancelProgress();
            view.onError();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<BillResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onBillPayResponse(status, status ? data : null, status ? message : "Failed");
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void billsPay(BillRequest billRequest, String authToken, byte[] encKey, byte[] ivKey) {
        try {
            view.showProgress();
            messageEncryptor = new AESMessageEncryptor();
            this.billRequest = billRequest;
            this.authToken = authToken;
            this.encKey = encKey;
            this.ivKey = ivKey;
            keyExchange();
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onKeyExchangeDone() {

        try {
            billRequest.setRequestUUID(UUID.randomUUID().toString());
            RequestMessage<BillRequest> message = new RequestMessage<>(billRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());
            String strJson = messageEncryptor.encryptRequest(new Gson().toJson(message), encKey, ivKey, getEncId());
            new BillsPayNetWorker(modelLayer, encKey, ivKey).billsPay(strJson, this);
        }catch (Exception e) {
            view.keyExchangeProblem();
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onKeyExchangeError() {
        onError();
    }
}
