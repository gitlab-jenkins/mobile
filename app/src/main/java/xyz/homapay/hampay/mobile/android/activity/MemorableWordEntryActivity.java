package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.GetUserIdTokenRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCredentialEntry;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.edittext.MemorableTextWatcher;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.AESHelper;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.SecurityUtils;

import java.util.UUID;

public class MemorableWordEntryActivity extends Activity {

    ButtonRectangle keepOn_button;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    FacedEditText memorable_value;

    Context context;

    Activity activity;

    HamPayDialog hamPayDialog;

    String Uuid = "";

    Bundle bundle;

    String userEntryPassword;

    Tracker hamPayGaTracker;

    RequestCredentialEntry requestCredentialEntry;
    RegistrationCredentialsRequest registrationCredentialsRequest;

    byte[] mobileKey;
    String serverKey;

//    String encryptedData;

    DeviceInfo deviceInfo;

    public void contactUs(View view){
//        new HamPayDialog(this).showContactUsDialog();
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/memorableKey.html");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorable_word_entry);

        activity = MemorableWordEntryActivity.this;
        context = this;

        bundle = getIntent().getExtras();

        deviceInfo = new DeviceInfo(context);

        userEntryPassword = bundle.getString(Constants.USER_ENTRY_PASSWORD);

        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        memorable_value = (FacedEditText)findViewById(R.id.memorable_value);
        memorable_value.addTextChangedListener(new MemorableTextWatcher(memorable_value));

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, PasswordEntryActivity.class.getName());
        editor.commit();

        keepOn_button = (ButtonRectangle) findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (memorable_value.getText().toString().trim().length() != 0) {
                    registrationCredentialsRequest = new RegistrationCredentialsRequest();
                    registrationCredentialsRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                    registrationCredentialsRequest.setDeviceId(new DeviceInfo(getApplicationContext()).getAndroidId());
                    Uuid = UUID.randomUUID().toString();
                    registrationCredentialsRequest.setInstallationToken(Uuid);
                    registrationCredentialsRequest.setMemorableKey(memorable_value.getText().toString());
                    registrationCredentialsRequest.setPassCode(userEntryPassword);
                    requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                    requestCredentialEntry.execute(registrationCredentialsRequest);
                }else {
                    Toast.makeText(context, getString(R.string.msg_memorable_incorrect), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }




    public class RequestMemorableWordEntryResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationCredentialsResponse>>
    {
        public RequestMemorableWordEntryResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationCredentialsResponse> registrationMemorableWordEntryResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            ResultStatus resultStatus;

            if (registrationMemorableWordEntryResponseMessage != null) {

                resultStatus = registrationMemorableWordEntryResponseMessage.getService().getResultStatus();

                if (resultStatus == ResultStatus.SUCCESS) {
                    try {
                        mobileKey = SecurityUtils.getInstance(context).generateSHA_256(
                                deviceInfo.getMacAddress(),
                                deviceInfo.getIMEI(),
                                deviceInfo.getAndroidId());
                        serverKey = registrationMemorableWordEntryResponseMessage.getService().getUserIdToken();
//                        encryptedData = AESHelper.encrypt(mobileKey, serverKey, memorable_value.getText().toString());
                        editor.putString(Constants.MEMORABLE_WORD, memorable_value.getText().toString());
//                        encryptedData = AESHelper.encrypt(mobileKey, serverKey, Uuid);
                        editor.putString(Constants.UUID, Uuid);
                        editor.commit();
                    }
                    catch (Exception ex){
                        Log.e("Error", ex.getStackTrace().toString());
                    }

                    Intent intent = new Intent();
                    intent.setClass(MemorableWordEntryActivity.this, CompleteRegistrationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Memorable Word Entry")
                            .setAction("Registration")
                            .setLabel("Success")
                            .build());
                }else if (registrationMemorableWordEntryResponseMessage.getService().getResultStatus() == ResultStatus.REGISTRATION_INVALID_STEP){
                    new HamPayDialog(activity).showInvalidStepDialog();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Memorable Word Entry")
                            .setAction("Registration")
                            .setLabel("Success(Invalid)")
                            .build());
                }
                else {
                    requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                    new HamPayDialog(activity).showFailMemorableEntryDialog(requestCredentialEntry, registrationCredentialsRequest,
                            registrationMemorableWordEntryResponseMessage.getService().getResultStatus().getCode(),
                            registrationMemorableWordEntryResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Memorable Word Entry")
                            .setAction("Registration")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                new HamPayDialog(activity).showFailMemorableEntryDialog(requestCredentialEntry, registrationCredentialsRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_memorable_entry));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Registration Memorable Word Entry")
                        .setAction("Registration")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }

}
