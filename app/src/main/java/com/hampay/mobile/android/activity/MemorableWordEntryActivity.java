package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationMemorableWordEntryRequest;
import com.hampay.common.core.model.response.RegistrationMemorableWordEntryResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestMemorableWordEntry;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.DeviceInfo;

import java.util.UUID;

public class MemorableWordEntryActivity extends ActionBarActivity {

    ButtonRectangle keepOn_button;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    FacedEditText memorable_value;

    Context context;

    Activity activity;

    RelativeLayout loading_rl;

    public void contactUs(View view){
        new HamPayDialog(this).showContactUsDialog();
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

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        keepOn_button = (ButtonRectangle) findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegistrationMemorableWordEntryRequest registrationMemorableWordEntryRequest = new RegistrationMemorableWordEntryRequest();
                registrationMemorableWordEntryRequest.setUserIdToken(prefs.getString("UserIdToken", ""));
                registrationMemorableWordEntryRequest.setDeviceId(new DeviceInfo(getApplicationContext()).getDeviceId());
                registrationMemorableWordEntryRequest.setInstallationToken(UUID.randomUUID().toString());
                registrationMemorableWordEntryRequest.setMemorableWord(memorable_value.getText().toString());
                new RequestMemorableWordEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener()).execute(registrationMemorableWordEntryRequest);

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

            if (registrationMemorableWordEntryResponseMessage.getService().getResultStatus() != null) {
                

                editor.putString(Constants.MEMORABLE_WORD, memorable_value.getText().toString());
                editor.commit();

                Intent intent = new Intent();
                intent.setClass(MemorableWordEntryActivity.this, CompleteRegistrationActivity.class);
                finish();
                startActivity(intent);

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
