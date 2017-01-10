package xyz.homapay.hampay.mobile.android.p.auth;

import com.google.gson.Gson;

import java.util.UUID;

import xyz.homapay.hampay.common.common.OSName;
import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.encrypt.MessageEncryptor;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.KeyAgreementResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.dto.DeviceDTO;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.m.common.OnNetworkLoadListener;
import xyz.homapay.hampay.mobile.android.m.worker.authorization.RegistrationEntryNetWorker;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchangeView;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchanger;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchangerImpl;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;

/**
 * Created by mohammad on 1/7/17.
 */

public class RegisterEntryImpl extends Presenter<RegisterEntryView> implements RegisterEntry, KeyExchangeView, OnNetworkLoadListener<ResponseMessage<RegistrationEntryResponse>> {

    private MessageEncryptor messageEncryptor;
    private KeyExchanger keyExchanger;
    private RegistrationEntryRequest registrationEntryRequest;
    private String authToken;

    public RegisterEntryImpl(ModelLayer modelLayer, RegisterEntryView view) {
        super(modelLayer, view);
    }

    private void onError() {
        try {
            view.dismissProgressDialog();
            view.onError();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExchangeDone(boolean state, ResponseMessage<KeyAgreementResponse> data, String message) {
        try {
            if (state) {
                DeviceInfo deviceInfo = modelLayer.getDeviceInfo();
                DeviceDTO deviceDTO = new DeviceDTO();
                deviceDTO.setImei(deviceInfo.getIMEI());
                deviceDTO.setImsi(deviceInfo.getIMSI());
                deviceDTO.setAndroidId(deviceInfo.getAndroidId());
                deviceDTO.setSimcardSerial(deviceInfo.getSimcardSerial());
                deviceDTO.setNetworkOperatorName(deviceInfo.getNetworkOperator());
                deviceDTO.setDeviceModel(deviceInfo.getDeviceModel());
                deviceDTO.setManufacture(deviceInfo.getManufacture());
                deviceDTO.setBrand(deviceInfo.getBrand());
                deviceDTO.setCpu_abi(deviceInfo.getCpu_abi());
                deviceDTO.setDeviceAPI(deviceInfo.getDeviceAPI());
                deviceDTO.setOsVersion(deviceInfo.getOsVersion());
                deviceDTO.setOsName(OSName.ANDROID);
                deviceDTO.setDisplaySize(deviceInfo.getDisplaySize());
                deviceDTO.setDisplayMetrics(deviceInfo.getDisplaymetrics());
                deviceDTO.setSimState(deviceInfo.getSimState());
                deviceDTO.setLocale(deviceInfo.getLocale());
                deviceDTO.setMacAddress(deviceInfo.getMacAddress());
                registrationEntryRequest.setDeviceDTO(deviceDTO);
                registrationEntryRequest.setRequestUUID(UUID.randomUUID().toString());
                RequestMessage<RegistrationEntryRequest> reqMessage = new RequestMessage<>(registrationEntryRequest, authToken, Constants.API_LEVEL, System.currentTimeMillis());

                String strJson = messageEncryptor.encryptRequest(new Gson().toJson(reqMessage), KeyExchangerImpl.getKey(), KeyExchangerImpl.getIv(), KeyExchangerImpl.getEncId());

                new RegistrationEntryNetWorker(modelLayer).register(strJson, this);
            }
        } catch (Exception e) {
            view.keyExchangeProblem();
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void register(RegistrationEntryRequest registrationEntryRequest, String authToken) {
        try {
            view.showProgressDialog();
            messageEncryptor = new AESMessageEncryptor();
            keyExchanger = new KeyExchangerImpl(modelLayer, this);
            this.registrationEntryRequest = registrationEntryRequest;
            this.authToken = authToken;
            keyExchanger.exchange();
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }

    @Override
    public void onNetworkLoad(boolean status, ResponseMessage<RegistrationEntryResponse> data, String message) {
        try {
            view.dismissProgressDialog();
            view.onRegisterResponse(status, status ? data : null, status ? message : "Failed");
        } catch (Exception e) {
            e.printStackTrace();
            onError();
        }
    }
}
