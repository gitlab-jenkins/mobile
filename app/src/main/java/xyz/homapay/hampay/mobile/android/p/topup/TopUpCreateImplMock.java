package xyz.homapay.hampay.mobile.android.p.topup;

import com.google.gson.Gson;

import java.util.Date;
import java.util.UUID;

import xyz.homapay.hampay.common.common.ChargePackage;
import xyz.homapay.hampay.common.common.Operator;
import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.TopUpRequest;
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;
import xyz.homapay.hampay.common.core.model.response.dto.TopUpInfoDTO;
import xyz.homapay.hampay.mobile.android.m.common.KeyAgreementModel;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.topup.CreateChargeNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/12/2017 AD.
 */

public class TopUpCreateImplMock extends Presenter<TopUpCreateView> implements TopUpCreate, OnNetworkLoadListener<ResponseMessage<TopUpResponse>> {

    private Operator operator;
    private TopUpRequest topUpRequest;
    private String cellPhoneNumber;
    private ChargePackage chargePackage;
    private String chargeType;

    public TopUpCreateImplMock(ModelLayer modelLayer, TopUpCreateView view) {
        super(modelLayer, view);
    }

    @Override
    public void onKeyExchangeDone(boolean state, KeyAgreementModel data, String message) {
        try {
            if (state) {
                if (data.getKey() != null && data.getIv() != null && data.getEncId() != null) {
                    topUpRequest = new TopUpRequest();
                    topUpRequest.setRequestUUID(UUID.randomUUID().toString());
                    topUpRequest.setOperator(operator);
                    topUpRequest.setCellNumber(cellPhoneNumber);
                    topUpRequest.setChargePackage(chargePackage);
                    topUpRequest.setChargeType(chargeType);

                    RequestMessage<TopUpRequest> request = new RequestMessage<>(topUpRequest, modelLayer.getAuthToken(), Constants.API_LEVEL, System.currentTimeMillis());
                    String strJson = new AESMessageEncryptor().encryptRequest(new Gson().toJson(request), data.getKey(), data.getIv(), data.getEncId());
                    new CreateChargeNetWorker(modelLayer, data, true, false).createTopUp(strJson, this);
                } else {
                    view.onError();
                }
            } else
                view.onError();
        } catch (Exception e) {
            e.printStackTrace();
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
    public void create(Operator operator, String cellPhoneNumber, ChargePackage chargePackage, String chargeType) {
        this.operator = operator;
        this.cellPhoneNumber = cellPhoneNumber;
        this.chargePackage = chargePackage;
        this.chargeType = chargeType;
        TopUpResponse response = new TopUpResponse();
        TopUpInfoDTO dto = new TopUpInfoDTO();
        dto.setAmount(2000);
        dto.setCellNumber("09194237096");
        dto.setExpirationDate(new Date());
        dto.setFeeCharge(200);
        dto.setImageId("MTN");
        dto.setTopUpName("شارژ سیم کارت");
        dto.setVat(250);
        response.setTopUpInfoDTO(dto);

        ResponseMessage<TopUpResponse> responseResponseMessage = new ResponseMessage<TopUpResponse>(response);
        view.onCreated(true, responseResponseMessage, "ok");
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<TopUpResponse> data, String message) {
        try {
            view.cancelProgress();
            view.onCreated(status, status ? data : null, status ? message : "Failed");
        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }
}
