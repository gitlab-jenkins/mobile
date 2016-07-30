package xyz.homapay.hampay.mobile.android.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.AESMessageEncryptor;
import xyz.homapay.hampay.common.common.encrypt.DiffieHellmanKeyExchanger;
import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.encrypt.KeyExchanger;
import xyz.homapay.hampay.common.common.encrypt.MessageEncryptor;
import xyz.homapay.hampay.common.common.encrypt.PublicKeyPair;
import xyz.homapay.hampay.common.common.encrypt.SecretKeyPair;
import xyz.homapay.hampay.common.common.request.DecryptedRequestInfo;
import xyz.homapay.hampay.common.common.request.RequestMessage;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.KeyAgreementRequest;
import xyz.homapay.hampay.common.core.model.response.KeyAgreementResponse;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestKeyAgreement;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.webservice.ConnectionMethod;
import xyz.homapay.hampay.mobile.android.webservice.ConnectionType;
import xyz.homapay.hampay.mobile.android.webservice.SecuredProxyService;

/**
 * Created by amir on 7/24/16.
 */
public class KeyExchangeService extends IntentService {

    private  KeyExchanger keyExchanger;


    private final IBinder binder = new ServiceBinder();

    private int result = Activity.RESULT_CANCELED;
    public static final String RESULT = "result";


    public static byte[] clientEncKey = null;
    public static byte[] clientEncIv = null;

    public static final String CLIENT_ENC_KEY = "client_enc_key";
    public static final String CLIENT_ENC_IV = "client_enc_iv";
    public static String encryptionId = UUID.randomUUID().toString();

    public static final String NOTIFICATION = "key exchange";
    private KeyAgreementRequest keyAgreementRequest;

    public KeyExchangeService(){
        super("KeyExchangeService");
        keyAgreementRequest = new KeyAgreementRequest();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d("onStart", "onStart");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);

        try {
            keyExchanger = new DiffieHellmanKeyExchanger();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
        PublicKeyPair publicKeyPair = keyExchanger.getPublicKey();

        if (clientEncIv == null) {
            Toast.makeText(getApplicationContext(), getApplication().getString(R.string.start_key_exchange), Toast.LENGTH_SHORT).show();
        }

        RequestKeyAgreement requestKeyAgreement = new RequestKeyAgreement(getApplicationContext(), new RequestKeyAgreementTaskCompleteListener());
        keyAgreementRequest.setRequestUUID(UUID.randomUUID().toString());
        keyAgreementRequest.setId(encryptionId);
        keyAgreementRequest.setKeyData(publicKeyPair.getEncPublicKey().getEncoded());
        keyAgreementRequest.setIvData(publicKeyPair.getIvPublicKey().getEncoded());
        requestKeyAgreement.execute(keyAgreementRequest);


        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        exchangeKey(result, clientEncKey, clientEncIv);

    }

    private void exchangeKey(int result, byte[] clientEncKey, byte[] clientEncIv){
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(CLIENT_ENC_KEY, clientEncKey);
        intent.putExtra(CLIENT_ENC_IV, clientEncIv);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    public class ServiceBinder extends Binder {
        public KeyExchangeService getService() {
            return KeyExchangeService.this;
        }
    }

    public class RequestKeyAgreementTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<KeyAgreementResponse>>
    {
        public RequestKeyAgreementTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<KeyAgreementResponse> keyAgreementResponseMessage)
        {
            if (keyAgreementResponseMessage != null) {

                if (keyAgreementResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    PublicKey peerEncPublicKey;
                    PublicKey peerIvPublicKey;
                    SecretKeyPair secretKeyPair = null;
                    try {
                        peerEncPublicKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(keyAgreementResponseMessage.getService().getKeyData()));
                        peerIvPublicKey = KeyFactory.getInstance("DH").generatePublic(new X509EncodedKeySpec(keyAgreementResponseMessage.getService().getIvData()));
                        secretKeyPair = keyExchanger.generateAndGetSecretKey(new PublicKeyPair(peerEncPublicKey, peerIvPublicKey));
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (clientEncIv == null) {
                        Toast.makeText(getApplicationContext(), getApplication().getString(R.string.finish_key_exchange), Toast.LENGTH_SHORT).show();
                    }

                    clientEncKey = secretKeyPair.getEncSecretKey().getEncoded();
                    clientEncIv = secretKeyPair.getIvSecretKey().getEncoded();

                    exchangeKey(result, clientEncKey, clientEncIv);

                }else {

                }
            }
            else {

            }

        }

        @Override
        public void onTaskPreRun() {}
    }
}
