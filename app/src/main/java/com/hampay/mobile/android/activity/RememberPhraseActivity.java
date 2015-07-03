package com.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationMemorableWordEntryRequest;
import com.hampay.common.core.model.response.RegistrationMemorableWordEntryResponse;
import com.hampay.common.core.model.response.RegistrationPassCodeEntryResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestMemorableWordEntry;
import com.hampay.mobile.android.async.RequestPassCodeEntry;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.UUID;

public class RememberPhraseActivity extends ActionBarActivity {

    CardView keepOn_CardView;
    SharedPreferences prefs;
    FacedEditText memorable_value;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_phrase);


        context = this;

        memorable_value = (FacedEditText)findViewById(R.id.memorable_value);

        prefs = getPreferences(MODE_PRIVATE);

        keepOn_CardView = (CardView) findViewById(R.id.keepOn_CardView);
        keepOn_CardView.setOnClickListener(new View.OnClickListener() {
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


            if (registrationMemorableWordEntryResponseMessage.getService().getResultStatus() != null) {

                Intent intent = new Intent();
                intent.setClass(RememberPhraseActivity.this, CompleteRegistrationActivity.class);
                startActivity(intent);

            }

        }

        @Override
        public void onTaskPreRun() {

        }
    }
}
