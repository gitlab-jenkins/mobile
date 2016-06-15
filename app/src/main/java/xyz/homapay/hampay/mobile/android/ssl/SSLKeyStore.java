package xyz.homapay.hampay.mobile.android.ssl;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Created by amir on 2/8/16.
 */
public class SSLKeyStore {

    private Context context;

    public SSLKeyStore(Context context){
        this.context = context;
    }

    public KeyStore getAppKeyStore(){
        try{
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            InputStream caInput = new BufferedInputStream(context.getAssets().open("cert/tejarat_nginx.crt"));
//            InputStream caInput = new BufferedInputStream(context.getAssets().open("cert/uat-http-v1.crt"));
            InputStream caInput = new BufferedInputStream(context.getAssets().open("cert/prod-http-v1.crt"));
//            InputStream caInput = new BufferedInputStream(context.getAssets().open("cert/hampay_prod_key.crt"));
            Certificate certificate = certificateFactory.generateCertificate(caInput);
//            Log.e("ca=", ((X509Certificate) certificate).getSubjectDN() + "");
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", certificate);

            return keyStore;
            //String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            // TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(defaultAlgorithm);
            //trustManagerFactory.init(keyStore);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
