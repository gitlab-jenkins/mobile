package xyz.homapay.hampay.mobile.android.async;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.UnknownHostException;

import xyz.homapay.hampay.common.common.OSName;
import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.dto.DeviceDTO;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.net.InternetConnectionStatus;
import xyz.homapay.hampay.mobile.android.util.net.InternetConnectivity;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestRegistrationEntry extends AsyncTask<RegistrationEntryRequest, Void, ResponseMessage<RegistrationEntryResponse>> {

    private static final String TAG = "RequestHttpRegistrationEntry";
    private Activity context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationEntryResponse>> listener;
    public InternetConnectionStatus internetConnectionStatus;
    private ResponseMessage<RegistrationEntryResponse> registrationEntryResponseMessage;

    public RequestRegistrationEntry(Activity context, AsyncTaskCompleteListener<ResponseMessage<RegistrationEntryResponse>> listener)
    {
        this.context = context;
        this.listener = listener;
    }


    protected void onPreExecute()
    {
        super.onPreExecute();
        listener.onTaskPreRun();
    }

    @Override
    protected ResponseMessage<RegistrationEntryResponse> doInBackground(RegistrationEntryRequest... params) {


        DeviceInfo deviceInfo = new DeviceInfo(context);

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

        params[0].setDeviceDTO(deviceDTO);

        SecuredWebServices webServices = new SecuredWebServices(context, Constants.CONNECTION_TYPE);
        try {
            registrationEntryResponseMessage = webServices.registrationEntry(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }

        if (registrationEntryResponseMessage == null){
            try {
                internetConnectionStatus = isConnected();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return registrationEntryResponseMessage;
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationEntryResponse> registrationEntryResponseMessage)
    {
        super.onPostExecute(registrationEntryResponseMessage);
        listener.onTaskComplete(registrationEntryResponseMessage);
    }

    public InternetConnectionStatus isConnected() throws UnknownHostException {
        InternetConnectivity internetConnectivity = new InternetConnectivity();
        return internetConnectivity.getStatus();
    }
}
