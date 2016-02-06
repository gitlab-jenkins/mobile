package xyz.homapay.hampay.mobile.android.webservice.xyz.homapay.hampay.mobile.android.webservice.soap;




//----------------------------------------------------
//
// Generated by www.easywsdl.com
// Version: 4.1.10.0
//
// Created by Quasar Development at 03-02-2016
//
//---------------------------------------------------




import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;
import org.kxml2.kdom.Element;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HSHPayThPartyAppSoapBinding
{
    interface HSHIWcfMethod
    {
        HSHExtendedSoapSerializationEnvelope CreateSoapEnvelope() throws Exception;

        Object ProcessResult(HSHExtendedSoapSerializationEnvelope __envelope, Object result) throws Exception;
    }

    String url="http://192.168.1.113:8181/ptpa";

    int timeOut=60000;
    public List< HeaderProperty> httpHeaders;
    public boolean enableLogging;

    HSHIServiceEvents callback;
    public HSHPayThPartyAppSoapBinding(){}

    public HSHPayThPartyAppSoapBinding (HSHIServiceEvents callback)
    {
        this.callback = callback;
    }
    public HSHPayThPartyAppSoapBinding(HSHIServiceEvents callback,String url)
    {
        this.callback = callback;
        this.url = url;
    }

    public HSHPayThPartyAppSoapBinding(HSHIServiceEvents callback,String url,int timeOut)
    {
        this.callback = callback;
        this.url = url;
        this.timeOut=timeOut;
    }

    protected Transport createTransport()
    {
        try
        {
            java.net.URI uri = new java.net.URI(url);
            if(uri.getScheme().equalsIgnoreCase("https"))
            {
                int port=uri.getPort()>0?uri.getPort():443;
                return new HttpsTransportSE(uri.getHost(), port, uri.getPath(), timeOut);
            }
            else
            {
                return new HttpTransportSE(url,timeOut);
            }

        }
        catch (java.net.URISyntaxException e)
        {
        }
        return null;
    }
    
    protected HSHExtendedSoapSerializationEnvelope createEnvelope()
    {
        HSHExtendedSoapSerializationEnvelope envelope= new HSHExtendedSoapSerializationEnvelope(HSHExtendedSoapSerializationEnvelope.VER11);
        return envelope;
    }
    
    protected List sendRequest(String methodName,HSHExtendedSoapSerializationEnvelope envelope,Transport transport  )throws Exception
    {
        return transport.call(methodName, envelope, httpHeaders);
    }

    Object getResult(Class destObj,Object source,String resultName,HSHExtendedSoapSerializationEnvelope __envelope) throws Exception
    {
        if(source==null)
        {
            return null;
        }
        if(source instanceof SoapPrimitive)
        {
            SoapPrimitive soap =(SoapPrimitive)source;
            if(soap.getName().equals(resultName))
            {
                Object instance=__envelope.get(source,destObj);
                return instance;
            }
        }
        else
        {
            SoapObject soap = (SoapObject)source;
            if (soap.hasProperty(resultName))
            {
                Object j=soap.getProperty(resultName);
                if(j==null)
                {
                    return null;
                }
                Object instance=__envelope.get(j,destObj);
                return instance;
            }
            else if( soap.getName().equals(resultName)) {
                Object instance=__envelope.get(source,destObj);
                return instance;
            }
       }

       return null;
    }

        
    public Integer getBookID(final String arg0 ) throws Exception
    {
        return (Integer)execute(new HSHIWcfMethod()
        {
            @Override
            public HSHExtendedSoapSerializationEnvelope CreateSoapEnvelope(){
              HSHExtendedSoapSerializationEnvelope __envelope = createEnvelope();
                SoapObject __soapReq = new SoapObject("http://soapService.psp.core.hampay.homapay.xyz/", "getBookID");
                __envelope.setOutputSoapObject(__soapReq);
                
                PropertyInfo __info=null;
                __info = new PropertyInfo();
                __info.namespace="http://soapService.psp.core.hampay.homapay.xyz/";
                __info.name="arg0";
                __info.type=PropertyInfo.STRING_CLASS;
                __info.setValue(arg0!=null?arg0:SoapPrimitive.NullSkip);
                __soapReq.addProperty(__info);
                return __envelope;
            }
            
            @Override
            public Object ProcessResult(HSHExtendedSoapSerializationEnvelope __envelope,Object __result)throws Exception {
                SoapObject __soap=(SoapObject)__result;
                Object obj = __soap.getProperty("return");
                if (obj != null && obj.getClass().equals(SoapPrimitive.class))
                {
                    SoapPrimitive j =(SoapPrimitive) obj;
                    return Integer.parseInt(j.toString());
                }
                else if (obj!= null && obj instanceof Integer){
                    return (Integer)obj;
                }
                return null;
            }
        },"");
    }
    
    public android.os.AsyncTask< Void, Void, HSHOperationResult< Integer>> getBookIDAsync(final String arg0)
    {
        return executeAsync(new HSHFunctions.IFunc< Integer>() {
            public Integer Func() throws Exception {
                return getBookID( arg0);
            }
        });
    }
    
    public String DoWork(final String arg0,final String arg1,final String arg2,final String arg3,final String arg4 ) throws Exception
    {
/*This feature is available in Premium account, Check http://EasyWsdl.com/Payment/PremiumAccountDetails to see all benefits of Premium account*/
        return null;    
    }
    
    public android.os.AsyncTask< Void, Void, HSHOperationResult< String>> DoWorkAsync(final String arg0,final String arg1,final String arg2,final String arg3,final String arg4)
    {
        return executeAsync(new HSHFunctions.IFunc< String>() {
            public String Func() throws Exception {
                return DoWork( arg0,arg1,arg2,arg3,arg4);
            }
        });
    }

    
    protected Object execute(HSHIWcfMethod wcfMethod,String methodName) throws Exception
    {
        Transport __httpTransport=createTransport();
        __httpTransport.debug=enableLogging;
        HSHExtendedSoapSerializationEnvelope __envelope=wcfMethod.CreateSoapEnvelope();
        try
        {
            sendRequest(methodName, __envelope, __httpTransport);
            
        }
        finally {
            if (__httpTransport.debug) {
                if (__httpTransport.requestDump != null) {
                    android.util.Log.i("requestDump",__httpTransport.requestDump);
                }
                if (__httpTransport.responseDump != null) {
                    android.util.Log.i("responseDump",__httpTransport.responseDump);
                }
            }
        }
        Object __retObj = __envelope.bodyIn;
        if (__retObj instanceof org.ksoap2.SoapFault){
            org.ksoap2.SoapFault __fault = (org.ksoap2.SoapFault)__retObj;
            throw convertToException(__fault,__envelope);
        }else{
            return wcfMethod.ProcessResult(__envelope,__retObj);
        }
    }
    
    protected < T> android.os.AsyncTask< Void, Void, HSHOperationResult< T>>  executeAsync(final HSHFunctions.IFunc< T> func)
    {
        return new android.os.AsyncTask< Void, Void, HSHOperationResult< T>>()
        {
            @Override
            protected void onPreExecute() {
                callback.Starting();
            };
            @Override
            protected HSHOperationResult< T> doInBackground(Void... params) {
                HSHOperationResult< T> result = new HSHOperationResult< T>();
                try
                {
                    result.Result= func.Func();
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                    result.Exception=ex;
                }
                return result;
            }
            
            @Override
            protected void onPostExecute(HSHOperationResult< T> result)
            {
                callback.Completed(result);
            }
        }.execute();
    }
        
    Exception convertToException(org.ksoap2.SoapFault fault,HSHExtendedSoapSerializationEnvelope envelope)
    {

        return new Exception(fault.faultstring);
    }
}

