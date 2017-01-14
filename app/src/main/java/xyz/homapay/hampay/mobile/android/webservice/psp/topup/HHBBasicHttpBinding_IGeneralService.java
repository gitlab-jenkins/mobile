package xyz.homapay.hampay.mobile.android.webservice.psp.topup;



import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.*;
import org.ksoap2.transport.*;
import org.kxml2.kdom.Element;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HHBBasicHttpBinding_IGeneralService
{
    interface HHBIWcfMethod
    {
        HHBExtendedSoapSerializationEnvelope CreateSoapEnvelope() throws java.lang.Exception;

        java.lang.Object ProcessResult(HHBExtendedSoapSerializationEnvelope __envelope,java.lang.Object result) throws java.lang.Exception;
    }

    String url="http://mpay-services.bsw.local:5424/ThPartyChargeNetPay.svc";

    int timeOut=60000;
    public List< HeaderProperty> httpHeaders;
    public boolean enableLogging;

    HHBIServiceEvents callback;
    public HHBBasicHttpBinding_IGeneralService(){}

    public HHBBasicHttpBinding_IGeneralService (HHBIServiceEvents callback)
    {
        this.callback = callback;
    }
    public HHBBasicHttpBinding_IGeneralService(HHBIServiceEvents callback,String url)
    {
        this.callback = callback;
        this.url = url;
    }

    public HHBBasicHttpBinding_IGeneralService(HHBIServiceEvents callback,String url,int timeOut)
    {
        this.callback = callback;
        this.url = url;
        this.timeOut=timeOut;
    }

    protected org.ksoap2.transport.Transport createTransport()
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
    
    protected HHBExtendedSoapSerializationEnvelope createEnvelope()
    {
        HHBExtendedSoapSerializationEnvelope envelope= new HHBExtendedSoapSerializationEnvelope(HHBExtendedSoapSerializationEnvelope.VER11);
        return envelope;
    }
    
    protected java.util.List sendRequest(String methodName,HHBExtendedSoapSerializationEnvelope envelope,org.ksoap2.transport.Transport transport  )throws java.lang.Exception
    {
        return transport.call(methodName, envelope, httpHeaders);
    }

    java.lang.Object getResult(java.lang.Class destObj,java.lang.Object source,String resultName,HHBExtendedSoapSerializationEnvelope __envelope) throws java.lang.Exception
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
                java.lang.Object instance=__envelope.get(source,destObj,false);
                return instance;
            }
        }
        else
        {
            SoapObject soap = (SoapObject)source;
            if (soap.hasProperty(resultName))
            {
                java.lang.Object j=soap.getProperty(resultName);
                if(j==null)
                {
                    return null;
                }
                java.lang.Object instance=__envelope.get(j,destObj,false);
                return instance;
            }
            else if( soap.getName().equals(resultName)) {
                java.lang.Object instance=__envelope.get(source,destObj,false);
                return instance;
            }
       }

       return null;
    }

        
    public HHBArrayOfKeyValueOfstringstring DoWork(final String username,final String password,final String cellnumber,final Integer lang,final HHBArrayOfKeyValueOfstringstring dicData ) throws java.lang.Exception
    {
        return (HHBArrayOfKeyValueOfstringstring)execute(new HHBIWcfMethod()
        {
            @Override
            public HHBExtendedSoapSerializationEnvelope CreateSoapEnvelope(){
              HHBExtendedSoapSerializationEnvelope __envelope = createEnvelope();
                __envelope.addMapping("http://tempuri.org/","dicData",new HHBArrayOfKeyValueOfstringstring().getClass());
                SoapObject __soapReq = new SoapObject("http://tempuri.org/", "DoWork");
                __envelope.setOutputSoapObject(__soapReq);
                
                PropertyInfo __info=null;
                __info = new PropertyInfo();
                __info.namespace="http://tempuri.org/";
                __info.name="username";
                __info.type=PropertyInfo.STRING_CLASS;
                __info.setValue(username!=null?username:SoapPrimitive.NullSkip);
                __soapReq.addProperty(__info);
                __info = new PropertyInfo();
                __info.namespace="http://tempuri.org/";
                __info.name="password";
                __info.type=PropertyInfo.STRING_CLASS;
                __info.setValue(password!=null?password:SoapPrimitive.NullSkip);
                __soapReq.addProperty(__info);
                __info = new PropertyInfo();
                __info.namespace="http://tempuri.org/";
                __info.name="cellnumber";
                __info.type=PropertyInfo.STRING_CLASS;
                __info.setValue(cellnumber!=null?cellnumber:SoapPrimitive.NullSkip);
                __soapReq.addProperty(__info);
                __info = new PropertyInfo();
                __info.namespace="http://tempuri.org/";
                __info.name="lang";
                __info.type=PropertyInfo.INTEGER_CLASS;
                __info.setValue(lang!=null?lang:SoapPrimitive.NullSkip);
                __soapReq.addProperty(__info);
                __info = new PropertyInfo();
                __info.namespace="http://tempuri.org/";
                __info.name="dicData";
                __info.type=PropertyInfo.VECTOR_CLASS;
                __info.setValue(dicData!=null?dicData:SoapPrimitive.NullSkip);
                __soapReq.addProperty(__info);
                return __envelope;
            }
            
            @Override
            public java.lang.Object ProcessResult(HHBExtendedSoapSerializationEnvelope __envelope,java.lang.Object __result)throws java.lang.Exception {
                return (HHBArrayOfKeyValueOfstringstring)getResult(HHBArrayOfKeyValueOfstringstring.class,__result,"DoWorkResult",__envelope);
            }
        },"http://tempuri.org/IGeneralService/DoWork");
    }
    
    public android.os.AsyncTask< Void, Void, HHBOperationResult< HHBArrayOfKeyValueOfstringstring>> DoWorkAsync(final String username,final String password,final String cellnumber,final Integer lang,final HHBArrayOfKeyValueOfstringstring dicData)
    {
        return executeAsync(new HHBFunctions.IFunc< HHBArrayOfKeyValueOfstringstring>() {
            public HHBArrayOfKeyValueOfstringstring Func() throws java.lang.Exception {
                return DoWork( username,password,cellnumber,lang,dicData);
            }
        });
    }

    
    protected java.lang.Object execute(HHBIWcfMethod wcfMethod,String methodName) throws java.lang.Exception
    {
        org.ksoap2.transport.Transport __httpTransport=createTransport();
        __httpTransport.debug=enableLogging;
        HHBExtendedSoapSerializationEnvelope __envelope=wcfMethod.CreateSoapEnvelope();
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
        java.lang.Object __retObj = __envelope.bodyIn;
        if (__retObj instanceof org.ksoap2.SoapFault){
            org.ksoap2.SoapFault __fault = (org.ksoap2.SoapFault)__retObj;
            throw convertToException(__fault,__envelope);
        }else{
            return wcfMethod.ProcessResult(__envelope,__retObj);
        }
    }
    
    protected < T> android.os.AsyncTask< Void, Void, HHBOperationResult< T>>  executeAsync(final HHBFunctions.IFunc< T> func)
    {
        return new android.os.AsyncTask< Void, Void, HHBOperationResult< T>>()
        {
            @Override
            protected void onPreExecute() {
                callback.Starting();
            };
            @Override
            protected HHBOperationResult< T> doInBackground(Void... params) {
                HHBOperationResult< T> result = new HHBOperationResult< T>();
                try
                {
                    result.Result= func.Func();
                }
                catch(java.lang.Exception ex)
                {
                    ex.printStackTrace();
                    result.Exception=ex;
                }
                return result;
            }
            
            @Override
            protected void onPostExecute(HHBOperationResult< T> result)
            {
                callback.Completed(result);
            }
        }.execute();
    }
        
    java.lang.Exception convertToException(org.ksoap2.SoapFault fault,HHBExtendedSoapSerializationEnvelope envelope)
    {

        return new java.lang.Exception(fault.faultstring);
    }
}


