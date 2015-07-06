package com.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import com.hampay.common.core.model.response.RegistrationEntryResponse;
import com.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestBankList;
import com.hampay.mobile.android.async.RequestRegistrationSendSmsToken;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.util.NetworkConnectivity;
import com.hampay.mobile.android.webservice.WebServices;

public class VerificationActivity extends ActionBarActivity {

    ButtonRectangle keepOn_button;
    Context context;

    NetworkConnectivity networkConnectivity;
    RelativeLayout loading_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        context = this;
        networkConnectivity = new NetworkConnectivity(context);

        keepOn_button = (ButtonRectangle)findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (networkConnectivity.isNetworkConnected()) {

                    RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest = new RegistrationSendSmsTokenRequest();
                    SharedPreferences prefs = getPreferences(MODE_PRIVATE);

                    registrationSendSmsTokenRequest.setUserId(prefs.getString("UserIdToken", ""));

                    new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener()).execute(registrationSendSmsTokenRequest);
                }else {
                    Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class RequestRegistrationSendSmsTokenTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationSendSmsTokenResponse>>
    {
        @Override
        public void onTaskComplete(ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponse)
        {

            loading_rl.setVisibility(View.GONE);

            if (registrationSendSmsTokenResponse != null) {
                Intent intent = new Intent();
                intent.setClass(VerificationActivity.this, PostVerificationActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onTaskPreRun() {
            loading_rl.setVisibility(View.VISIBLE);
        }
    }

}
