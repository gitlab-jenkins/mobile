package xyz.homapay.hampay.mobile.android.webservice.psp;


import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.HttpsTransportSE;
import org.ksoap2.transport.Transport;

import java.util.List;


public class CBUBasicHttpBinding_IGeneralService
{
    interface CBUIWcfMethod
    {
        CBUExtendedSoapSerializationEnvelope CreateSoapEnvelope() throws Exception;

        Object ProcessResult(CBUExtendedSoapSerializationEnvelope __envelope, Object result) throws Exception;
    }

    String url="http://mpay-services.bsw.local:5424/ThPartyGeneralNetPay.svc";

    int timeOut=60000;
    public List< HeaderProperty> httpHeaders;
    public boolean enableLogging;

    CBUIServiceEvents callback;
    public CBUBasicHttpBinding_IGeneralService(){}

    public CBUBasicHttpBinding_IGeneralService(CBUIServiceEvents callback)
    {
        this.callback = callback;
    }
    public CBUBasicHttpBinding_IGeneralService(CBUIServiceEvents callback, String url)
    {
        this.callback = callback;
        this.url = url;
    }

    public CBUBasicHttpBinding_IGeneralService(CBUIServiceEvents callback, String url, int timeOut)
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
    
    protected CBUExtendedSoapSerializationEnvelope createEnvelope()
    {
        CBUExtendedSoapSerializationEnvelope envelope= new CBUExtendedSoapSerializationEnvelope(CBUExtendedSoapSerializationEnvelope.VER11);
        return envelope;
    }
    
    protected List sendRequest(String methodName, CBUExtendedSoapSerializationEnvelope envelope, Transport transport  )throws Exception
    {
        return transport.call(methodName, envelope, httpHeaders);
    }

    Object getResult(Class destObj, Object source, String resultName, CBUExtendedSoapSerializationEnvelope __envelope) throws Exception
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
                Object instance=__envelope.get(source,destObj,false);
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
                Object instance=__envelope.get(j,destObj,false);
                return instance;
            }
            else if( soap.getName().equals(resultName)) {
                Object instance=__envelope.get(source,destObj,false);
                return instance;
            }
       }

       return null;
    }

        
    public CBUArrayOfKeyValueOfstringstring DoWork(final String username, final String password, final String cellnumber, final Integer lang, final CBUArrayOfKeyValueOfstringstring dicData ) throws Exception
    {
        return (CBUArrayOfKeyValueOfstringstring)execute(new CBUIWcfMethod()
        {
            @Override
            public CBUExtendedSoapSerializationEnvelope CreateSoapEnvelope(){
              CBUExtendedSoapSerializationEnvelope __envelope = createEnvelope();
                __envelope.addMapping("http://tempuri.org/","dicData",new CBUArrayOfKeyValueOfstringstring().getClass());
                SoapObject __soapReq = new SoapObject("http://tempuri.org/", "DoWork");
                __envelope.setOutputSoapObject(__soapReq);
                
                PropertyInfo __info=null;
                __info = new PropertyInfo();
                __info.namespace="http://tempuri.org/";
                __info.name="username";
                __info.type= PropertyInfo.STRING_CLASS;
                __info.setValue(username!=null?username: SoapPrimitive.NullSkip);
                __soapReq.addProperty(__info);
                __info = new PropertyInfo();
                __info.namespace="http://tempuri.org/";
                __info.name="password";
                __info.type= PropertyInfo.STRING_CLASS;
                __info.setValue(password!=null?password: SoapPrimitive.NullSkip);
                __soapReq.addProperty(__info);
                __info = new PropertyInfo();
                __info.namespace="http://tempuri.org/";
                __info.name="cellnumber";
                __info.type= PropertyInfo.STRING_CLASS;
                __info.setValue(cellnumber!=null?cellnumber: SoapPrimitive.NullSkip);
                __soapReq.addProperty(__info);
                __info = new PropertyInfo();
                __info.namespace="http://tempuri.org/";
                __info.name="lang";
                __info.type= PropertyInfo.INTEGER_CLASS;
                __info.setValue(lang!=null?lang: SoapPrimitive.NullSkip);
                __soapReq.addProperty(__info);
                __info = new PropertyInfo();
                __info.namespace="http://tempuri.org/";
                __info.name="dicData";
                __info.type= PropertyInfo.VECTOR_CLASS;
                __info.setValue(dicData!=null?dicData: SoapPrimitive.NullSkip);
                __soapReq.addProperty(__info);
                return __envelope;
            }
            
            @Override
            public Object ProcessResult(CBUExtendedSoapSerializationEnvelope __envelope, Object __result)throws Exception {
                return (CBUArrayOfKeyValueOfstringstring)getResult(CBUArrayOfKeyValueOfstringstring.class,__result,"DoWorkResult",__envelope);
            }
        },"http://tempuri.org/IGeneralService/DoWork");
    }
    
    public android.os.AsyncTask<Void, Void, CBUOperationResult< CBUArrayOfKeyValueOfstringstring>> DoWorkAsync(final String username, final String password, final String cellnumber, final Integer lang, final CBUArrayOfKeyValueOfstringstring dicData)
    {
        return executeAsync(new CBUFunctions.IFunc< CBUArrayOfKeyValueOfstringstring>() {
            public CBUArrayOfKeyValueOfstringstring Func() throws Exception {
                return DoWork( username,password,cellnumber,lang,dicData);
            }
        });
    }

    
    protected Object execute(CBUIWcfMethod wcfMethod, String methodName) throws Exception
    {
        Transport __httpTransport=createTransport();
        __httpTransport.debug=enableLogging;
        CBUExtendedSoapSerializationEnvelope __envelope=wcfMethod.CreateSoapEnvelope();
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
    
    protected < T> android.os.AsyncTask<Void, Void, CBUOperationResult< T>>  executeAsync(final CBUFunctions.IFunc< T> func)
    {
        return new android.os.AsyncTask<Void, Void, CBUOperationResult< T>>()
        {
            @Override
            protected void onPreExecute() {
                callback.Starting();
            };
            @Override
            protected CBUOperationResult< T> doInBackground(Void... params) {
                CBUOperationResult< T> result = new CBUOperationResult< T>();
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
            protected void onPostExecute(CBUOperationResult< T> result)
            {
                callback.Completed(result);
            }
        }.execute();
    }
        
    Exception convertToException(org.ksoap2.SoapFault fault, CBUExtendedSoapSerializationEnvelope envelope)
    {

        return new Exception(fault.faultstring);
    }
}


