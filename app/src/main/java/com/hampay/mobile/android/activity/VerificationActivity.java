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
import com.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import com.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.webservice.WebServices;

public class VerificationActivity extends ActionBarActivity {

    CardView keepOn_CardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        keepOn_CardView = (CardView)findViewById(R.id.keepOn_CardView);
        keepOn_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest = new RegistrationSendSmsTokenRequest();
                SharedPreferences prefs = getPreferences(MODE_PRIVATE);

                registrationSendSmsTokenRequest.setUserId(prefs.getString("UserIdToken", ""));

                new HttpRegistrationSendSmsToken().execute(registrationSendSmsTokenRequest);

            }
        });
    }




    private ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponse;

    public class HttpRegistrationSendSmsToken extends AsyncTask<RegistrationSendSmsTokenRequest, Void, String> {

        @Override
        protected String doInBackground(RegistrationSendSmsTokenRequest... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            registrationSendSmsTokenResponse = webServices.registrationSendSmsToken(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (registrationSendSmsTokenResponse.getService().getResultStatus() != null) {

                Intent intent = new Intent();
                intent.setClass(VerificationActivity.this, PostVerificationActivity.class);
                startActivity(intent);

            }
            else {

            }
        }

    }

}
