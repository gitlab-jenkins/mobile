package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.IllegalAppListRequest;
import xyz.homapay.hampay.common.core.model.response.IllegalAppListResponse;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;
import xyz.homapay.hampay.mobile.android.webservice.xyz.homapay.hampay.mobile.android.webservice.soap.HSHPayThPartyAppSoapBinding;
import xyz.homapay.hampay.mobile.android.webservice.xyz.homapay.hampay.mobile.android.webservice.soap.xyz.homapay.hampay.mobile.android.webservice.xyz.homapay.hampay.mobile.android.webservice.soap.test.PayThPartyApp;
import xyz.homapay.hampay.mobile.android.webservice.xyz.homapay.hampay.mobile.android.webservice.soap.xyz.homapay.hampay.mobile.android.webservice.xyz.homapay.hampay.mobile.android.webservice.soap.test.Vectorstring2stringMapEntry;
import xyz.homapay.hampay.mobile.android.webservice.xyz.homapay.hampay.mobile.android.webservice.soap.xyz.homapay.hampay.mobile.android.webservice.xyz.homapay.hampay.mobile.android.webservice.soap.test.string2stringMapEntry;

/**
 * Created by amir on 7/3/15.
 */
public class RequestIllegalAppList extends AsyncTask<IllegalAppListRequest, Void, ResponseMessage<IllegalAppListResponse>> {

    private static final String TAG = "RequestIllegalAppList";

    private Context context;
    private AsyncTaskCompleteListener<ResponseMessage<IllegalAppListResponse>> listener;

    public RequestIllegalAppList(Context context, AsyncTaskCompleteListener<ResponseMessage<IllegalAppListResponse>> listener)
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
    protected ResponseMessage<IllegalAppListResponse> doInBackground(IllegalAppListRequest... params) {

        WebServices webServices = new WebServices(context);

        try {


//            String UserName,String Password,String CellNo,int lang,boolean lang,Vectorstring2stringMapEntry DicData

            Vectorstring2stringMapEntry vectorstring2stringMapEntry = new Vectorstring2stringMapEntry();
            string2stringMapEntry string2stringMapEntry1 = new string2stringMapEntry();
            string2stringMapEntry1.key = "iraj";
            string2stringMapEntry1.value = "2423423";
            vectorstring2stringMapEntry.add(string2stringMapEntry1);

            PayThPartyApp payThPartyApp = new PayThPartyApp();
            payThPartyApp.DoWork("Amir", "1234", "09126157905", 0, false,vectorstring2stringMapEntry);

//            HSHPayThPartyAppSoapBinding hshPayThPartyAppSoapBinding = new HSHPayThPartyAppSoapBinding();
//
//            HashMap<String,String> hashMap = new HashMap();
//            hashMap.put("hi", "Amir");
//
//            Byte aByte = 0;
//
//            String a = hshPayThPartyAppSoapBinding.DoWork("Amir", "Baghali", "09126157905", aByte.toString(), hashMap.toString());
//


            Log.e("SOAP", "Baghali");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return webServices.getIllegalAppList();
    }


    @Override
    protected void onPostExecute(ResponseMessage<IllegalAppListResponse> illegalAppListResponseMessage)
    {
        super.onPostExecute(illegalAppListResponseMessage);
        listener.onTaskComplete(illegalAppListResponseMessage);
    }

}
