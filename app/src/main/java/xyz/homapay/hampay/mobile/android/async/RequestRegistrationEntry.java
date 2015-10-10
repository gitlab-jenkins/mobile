package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.dto.DeviceDTO;
import com.hampay.common.core.model.request.RegistrationEntryRequest;
import com.hampay.common.core.model.response.RegistrationEntryResponse;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/3/15.
 */
public class RequestRegistrationEntry extends AsyncTask<RegistrationEntryRequest, Void, ResponseMessage<RegistrationEntryResponse>>  {

    private static final String TAG = "RequestHttpRegistrationEntry";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<RegistrationEntryResponse>> listener;
    private String location;

    public RequestRegistrationEntry(Context context, AsyncTaskCompleteListener<ResponseMessage<RegistrationEntryResponse>> listener, String location)
    {
        this.context = context;
        this.listener = listener;
        this.location = location;
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
        deviceDTO.setOsName(DeviceDTO.OSName.ANDROID);
        deviceDTO.setDisplaySize(deviceInfo.getDisplaySize());
        deviceDTO.setDisplayMetrics(deviceInfo.getDisplaymetrics());
        deviceDTO.setSimState(deviceInfo.getSimState());
        deviceDTO.setAppNames(deviceInfo.getAppNames());
        deviceDTO.setDeviceEmailAccount(deviceInfo.getDeviceEmailAccount());
        deviceDTO.setLocale(deviceInfo.getLocale());
        deviceDTO.setMacAddress(deviceInfo.getMacAddress());
        deviceDTO.setUserLocation(location);


        params[0].setDeviceDTO(deviceDTO);


        WebServices webServices = new WebServices(context);
        return webServices.newRegistrationEntry(params[0]);
    }


    @Override
    protected void onPostExecute(ResponseMessage<RegistrationEntryResponse> registrationEntryResponseMessage)
    {
        super.onPostExecute(registrationEntryResponseMessage);
        listener.onTaskComplete(registrationEntryResponseMessage);
    }

}
