package xyz.homapay.hampay.mobile.android.webservice.psp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.KeepAliveHttpTransportSE;
import org.ksoap2.transport.KeepAliveHttpsTransportSE;

import java.util.List;

import xyz.homapay.hampay.mobile.android.ssl.AllowHamPaySSL;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class PayThPartyApp {
    
    public String NAMESPACE ="http://soapService.psp.core.hampay.homapay.xyz/";
//    public String url="http://192.168.1.113:8181/ptpa";
    public int timeOut = 30000;
    public IWsdl2CodeEvents eventHandler;
    public WS_Enums.SoapProtocolVersion soapVersion;

    private AllowHamPaySSL allowHamPaySSL;
    private static Context context;

    public PayThPartyApp(Context context){
        this.context = context;
        allowHamPaySSL = new AllowHamPaySSL(this.context);
    }
    
    public PayThPartyApp(IWsdl2CodeEvents eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    public PayThPartyApp(IWsdl2CodeEvents eventHandler,String url,int timeOutInSeconds)
    {
        this.eventHandler = eventHandler;
        this.setTimeOut(timeOutInSeconds);
    }
    public void setTimeOut(int seconds){
        this.timeOut = seconds * 1000;
    }

    public void DoWorkAsync(String arg0,String arg1,String arg2,int arg3,boolean arg3Specified,Vectorstring2stringMapEntry arg4) throws Exception{
        if (this.eventHandler == null)
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        DoWorkAsync(arg0, arg1, arg2, arg3, arg3Specified, arg4, null);
    }
    
    public void DoWorkAsync(final String arg0,final String arg1,final String arg2,final int arg3,final boolean arg3Specified,final Vectorstring2stringMapEntry arg4,final List<HeaderProperty> headers) throws Exception{
        
        new AsyncTask<Void, Void, Vectorstring2stringMapEntry>(){
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            };
            @Override
            protected Vectorstring2stringMapEntry doInBackground(Void... params) {
                return DoWork(arg0, arg1, arg2, arg3, arg3Specified, arg4, headers);
            }
            @Override
            protected void onPostExecute(Vectorstring2stringMapEntry result)
            {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null){
                    eventHandler.Wsdl2CodeFinished("DoWork", result);
                }
            }
        }.execute();
    }
    
    public Vectorstring2stringMapEntry DoWork(String arg0,String arg1,String arg2,int arg3,boolean arg3Specified,Vectorstring2stringMapEntry arg4){
        return DoWork(arg0, arg1, arg2, arg3, arg3Specified, arg4, null);
    }
    
    public Vectorstring2stringMapEntry DoWork(String arg0,String arg1,String arg2,int arg3,boolean arg3Specified,Vectorstring2stringMapEntry arg4,List<HeaderProperty> headers){

        allowHamPaySSL.enableHamPaySSL();

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = true;
        SoapObject soapReq = new SoapObject("http://soapService.psp.core.hampay.homapay.xyz/","DoWork");
        soapReq.addProperty("arg0",arg0);
        soapReq.addProperty("arg1",arg1);
        soapReq.addProperty("arg2", arg2);
        soapReq.addProperty("arg3", arg3);
        soapReq.addProperty("arg4", arg4);
        soapEnvelope.addMapping(NAMESPACE, "entry", Vectorstring2stringMapEntry.class);
        soapEnvelope.setOutputSoapObject(soapReq);
//        HttpTransportSE httpTransport = new HttpTransportSE(url,timeOut);
        KeepAliveHttpsTransportSE httpTransport = new KeepAliveHttpsTransportSE(Constants.PSP_SOAO_SERVICE_URL, 443, "/ptpa", timeOut);
        httpTransport.debug = true;
        try{
            if (headers!=null){
                httpTransport.call("http://soapService.psp.core.hampay.homapay.xyz/DoWork", soapEnvelope,headers);
            }else{
                httpTransport.call("http://soapService.psp.core.hampay.homapay.xyz/DoWork", soapEnvelope);
            }

            Log.e("test", "request: " + httpTransport.requestDump);
            Log.e("test", "response: " + httpTransport.responseDump);

            Object retObj = soapEnvelope.bodyIn;
            if (retObj instanceof SoapFault){
                SoapFault fault = (SoapFault)retObj;
                Exception ex = new Exception(fault.faultstring);
                if (eventHandler != null)
                    eventHandler.Wsdl2CodeFinishedWithException(ex);
            }else{
                SoapObject result=(SoapObject)retObj;
                if (result.getPropertyCount() > 0){
                    Object obj = result.getProperty(0);
                    SoapObject j = (SoapObject)obj;
                    Vectorstring2stringMapEntry resultVariable = new Vectorstring2stringMapEntry(j);
                    return resultVariable;
                }
            }
        }catch (Exception e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
        return null;
    }
    
}