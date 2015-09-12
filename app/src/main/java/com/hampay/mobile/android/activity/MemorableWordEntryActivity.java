package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.RegistrationCredentialsRequest;
import com.hampay.common.core.model.response.RegistrationCredentialsResponse;
import com.hampay.mobile.android.HamPayApplication;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestCredentialEntry;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.component.edittext.MemorableTextWatcher;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.DeviceInfo;

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
//    RequestMemorableWordEntry requestMemorableWordEntry;
//    RegistrationMemorableWordEntryRequest registrationMemorableWordEntryRequest;

    public void contactUs(View view){
//        new HamPayDialog(this).showContactUsDialog();
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/memorableKey.html");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorable_word_entry);

        bundle = getIntent().getExtras();

        userEntryPassword = bundle.getString(Constants.USER_ENTRY_PASSWORD);

        activity = MemorableWordEntryActivity.this;
        context = this;

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
                registrationCredentialsRequest = new RegistrationCredentialsRequest();
                registrationCredentialsRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                registrationCredentialsRequest.setDeviceId(new DeviceInfo(getApplicationContext()).getAndroidId());
                Uuid = UUID.randomUUID().toString();
                registrationCredentialsRequest.setInstallationToken(Uuid);
                editor.putString("UUID", Uuid);
                editor.commit();
                registrationCredentialsRequest.setMemorableKey(memorable_value.getText().toString());
                registrationCredentialsRequest.setPassCode(userEntryPassword);
                requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                requestCredentialEntry.execute(registrationCredentialsRequest);

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

                    editor.putString(Constants.MEMORABLE_WORD, memorable_value.getText().toString());
                    editor.commit();

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
