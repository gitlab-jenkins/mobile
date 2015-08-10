package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.RegistrationMemorableWordEntryRequest;
import com.hampay.common.core.model.response.RegistrationMemorableWordEntryResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestMemorableWordEntry;
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

    RelativeLayout loading_rl;

    String Uuid = "";

    RequestMemorableWordEntry requestMemorableWordEntry;
    RegistrationMemorableWordEntryRequest registrationMemorableWordEntryRequest;

    public void contactUs(View view){
//        new HamPayDialog(this).showContactUsDialog();
        new HamPayDialog(this).showHelpDialog(Constants.SERVER_IP + ":8080" + "/help/accountVerification.html");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorable_word_entry);

        activity = MemorableWordEntryActivity.this;

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        context = this;

        memorable_value = (FacedEditText)findViewById(R.id.memorable_value);
        memorable_value.addTextChangedListener(new MemorableTextWatcher(memorable_value));

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        keepOn_button = (ButtonRectangle) findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationMemorableWordEntryRequest = new RegistrationMemorableWordEntryRequest();
                registrationMemorableWordEntryRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                registrationMemorableWordEntryRequest.setDeviceId(new DeviceInfo(getApplicationContext()).getAndroidId());
                Uuid = UUID.randomUUID().toString();
                registrationMemorableWordEntryRequest.setInstallationToken(Uuid);
                editor.putString("UUID", Uuid);
                editor.commit();
                registrationMemorableWordEntryRequest.setMemorableWord(memorable_value.getText().toString());
                requestMemorableWordEntry = new RequestMemorableWordEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                requestMemorableWordEntry.execute(registrationMemorableWordEntryRequest);

            }
        });
    }




    public class RequestMemorableWordEntryResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationMemorableWordEntryResponse>>
    {
        public RequestMemorableWordEntryResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationMemorableWordEntryResponse> registrationMemorableWordEntryResponseMessage) {

            loading_rl.setVisibility(View.GONE);

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
                }else {
                    requestMemorableWordEntry = new RequestMemorableWordEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                    new HamPayDialog(activity).showFailMemorableEntryDialog(requestMemorableWordEntry, registrationMemorableWordEntryRequest,
                            registrationMemorableWordEntryResponseMessage.getService().getResultStatus().getCode(),
                            registrationMemorableWordEntryResponseMessage.getService().getResultStatus().getDescription());
                }
            }else {
                requestMemorableWordEntry = new RequestMemorableWordEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                new HamPayDialog(activity).showFailMemorableEntryDialog(requestMemorableWordEntry, registrationMemorableWordEntryRequest,
                        "2000",
                        getString(R.string.msg_fail_memorable_entry));
            }

        }

        @Override
        public void onTaskPreRun() {
            loading_rl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }

}
