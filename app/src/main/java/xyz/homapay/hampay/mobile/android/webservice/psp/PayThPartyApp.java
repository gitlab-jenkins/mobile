package xyz.homapay.hampay.mobile.android.webservice.psp;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpsTransportSE;
import org.ksoap2.transport.KeepAliveHttpsTransportSE;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import xyz.homapay.hampay.mobile.android.util.HamPayX509TrustManager;

public class PayThPartyApp {
    
    public String NAMESPACE ="http://soapService.psp.core.hampay.homapay.xyz/";
    public String url="176.58.104.158";
    public int timeOut = 180;
    public IWsdl2CodeEvents eventHandler;
    public WS_Enums.SoapProtocolVersion soapVersion;

    private static Context _context;

    public PayThPartyApp(Context context){
        this._context = context;
    }


    
    public PayThPartyApp(IWsdl2CodeEvents eventHandler)
    {
        this.eventHandler = eventHandler;
    }
    public PayThPartyApp(IWsdl2CodeEvents eventHandler,String url)
    {
        this.eventHandler = eventHandler;
        this.url = url;
    }
    public PayThPartyApp(IWsdl2CodeEvents eventHandler,String url,int timeOutInSeconds)
    {
        this.eventHandler = eventHandler;
        this.url = url;
        this.setTimeOut(timeOutInSeconds);
    }
    public void setTimeOut(int seconds){
        this.timeOut = seconds * 1000;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void getBookIDAsync(String arg0) throws Exception{
        if (this.eventHandler == null)
            throw new Exception("Async Methods Requires IWsdl2CodeEvents");
        getBookIDAsync(arg0, null);
    }
    
    public void getBookIDAsync(final String arg0,final List<HeaderProperty> headers) throws Exception{
        
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPreExecute() {
                eventHandler.Wsdl2CodeStartedRequest();
            };
            @Override
            protected Void doInBackground(Void... params) {
                getBookID(arg0, headers);
                return null;
            }
            @Override
            protected void onPostExecute(Void result)
            {
                eventHandler.Wsdl2CodeEndedRequest();
                if (result != null){
                    eventHandler.Wsdl2CodeFinished("getBookID", result);
                }
            }
        }.execute();
    }
    
    public void getBookID(String arg0){
        getBookID(arg0, null);
    }
    
    public void getBookID(String arg0,List<HeaderProperty> headers){



        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = true;
        SoapObject soapReq = new SoapObject("http://soapService.psp.core.hampay.homapay.xyz/","getBookID");
        soapReq.addProperty("arg0",arg0);
        soapEnvelope.setOutputSoapObject(soapReq);
        KeepAliveHttpsTransportSE httpTransport = new KeepAliveHttpsTransportSE(url,443,"/ptpa",timeOut);

        try{
            if (headers!=null){
                httpTransport.call("http://soapService.psp.core.hampay.homapay.xyz/getBookID", soapEnvelope,headers);
            }else{
                httpTransport.call("http://soapService.psp.core.hampay.homapay.xyz/getBookID", soapEnvelope);
            }
        }catch (Exception e) {
            if (eventHandler != null)
                eventHandler.Wsdl2CodeFinishedWithException(e);
            e.printStackTrace();
        }
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


    public static class _FakeX509TrustManager implements
            javax.net.ssl.X509TrustManager {
        private static final X509Certificate[] _AcceptedIssuers =
                new X509Certificate[]{};

        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        public boolean isClientTrusted(X509Certificate[] chain) {
            return (true);
        }

        public boolean isServerTrusted(X509Certificate[] chain) {
            return (true);
        }

        public X509Certificate[] getAcceptedIssuers() {
            return (_AcceptedIssuers);
        }
    }


    private static TrustManager[] trustManagers;

    public static void allowAllSSL() {

        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new
                                                                            HostnameVerifier() {
                                                                                public boolean verify(String hostname, SSLSession
                                                                                        session) {
                                                                                    return true;
                                                                                }
                                                                            });

        javax.net.ssl.SSLContext context = null;



        if (trustManagers == null) {
            try {

                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            InputStream caInput = new BufferedInputStream(context.getAssets().open("cert/tejarat_nginx.crt"));
                InputStream caInput = new BufferedInputStream(_context.getAssets().open("cert/nginx.crt"));
                Certificate certificate = certificateFactory.generateCertificate(caInput);
//            Log.e("ca=", ((X509Certificate) certificate).getSubjectDN() + "");
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", certificate);

                trustManagers = new TrustManager[]{new
                        HamPayX509TrustManager(keyStore)};
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }catch (CertificateException ex)
            {
                Log.e("CERT FAILD", "Failed to establish SSL connection to server: " + ex.toString());

            }
            catch (IOException ex)
            {
                Log.e("CERT FAILD", "Failed to establish SSL connection to server: " + ex.toString());

            }

        }

        try {
            context = javax.net.ssl.SSLContext.getInstance("TLS");
            context.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {

        } catch (KeyManagementException e) {

        }

        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    public Vectorstring2stringMapEntry DoWork(String arg0,String arg1,String arg2,int arg3,boolean arg3Specified,Vectorstring2stringMapEntry arg4,List<HeaderProperty> headers){

        allowAllSSL();

        SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.implicitTypes = true;
        soapEnvelope.dotNet = true;
        SoapObject soapReq = new SoapObject("http://soapService.psp.core.hampay.homapay.xyz/","DoWork");
        soapReq.addProperty("arg0",arg0);
        soapReq.addProperty("arg1",arg1);
        soapReq.addProperty("arg2",arg2);
        soapReq.addProperty("arg3",arg3);
        soapReq.addProperty("arg3Specified",arg3Specified);
        soapReq.addProperty("arg4", arg4);
        soapEnvelope.setOutputSoapObject(soapReq);
        KeepAliveHttpsTransportSE httpTransport = new KeepAliveHttpsTransportSE(url,443,"/ptpa",timeOut);

        try{
            if (headers!=null){
                httpTransport.call("http://soapService.psp.core.hampay.homapay.xyz/DoWork", soapEnvelope,headers);
            }else{
                httpTransport.call("http://soapService.psp.core.hampay.homapay.xyz/DoWork", soapEnvelope);
            }
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
