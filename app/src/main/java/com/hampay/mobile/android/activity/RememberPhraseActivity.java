package com.hampay.mobile.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationMemorableWordEntryRequest;
import com.hampay.common.core.model.request.RegistrationPassCodeEntryRequest;
import com.hampay.common.core.model.response.RegistrationMemorableWordEntryResponse;
import com.hampay.common.core.model.response.RegistrationPassCodeEntryResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedEditText;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.UUID;

public class RememberPhraseActivity extends ActionBarActivity {

    CardView keepOn_CardView;
    SharedPreferences prefs;
    FacedEditText memorable_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_phrase);

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
                new HttpRegistrationMemorableWordEntryResponse().execute(registrationMemorableWordEntryRequest);

            }
        });
    }


    private ResponseMessage<RegistrationMemorableWordEntryResponse> registrationMemorableWordEntryResponse;

    public class HttpRegistrationMemorableWordEntryResponse extends AsyncTask<RegistrationMemorableWordEntryRequest, Void, String> {

        @Override
        protected String doInBackground(RegistrationMemorableWordEntryRequest... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            registrationMemorableWordEntryResponse = webServices.registrationMemorableWordEntryResponse(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (registrationMemorableWordEntryResponse.getService().getResultStatus() != null) {

                Intent intent = new Intent();
                intent.setClass(RememberPhraseActivity.this, CompleteRegistrationActivity.class);
                startActivity(intent);

            }
        }
    }
}
