package com.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import com.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestRegistrationSendSmsToken;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.NetworkConnectivity;

public class VerificationActivity extends ActionBarActivity {

    ButtonRectangle keepOn_button;
    Context context;

    NetworkConnectivity networkConnectivity;
    RelativeLayout loading_rl;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public void contactUs(View view){
        new HamPayDialog(this).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        context = this;
        networkConnectivity = new NetworkConnectivity(context);

        keepOn_button = (ButtonRectangle)findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (networkConnectivity.isNetworkConnected()) {

                    RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest = new RegistrationSendSmsTokenRequest();


                    registrationSendSmsTokenRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));

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
                editor.putString(Constants.REGISTERED_ACTIVITY_DATA, VerificationActivity.class.toString());
                editor.commit();

                Intent intent = new Intent();
                intent.setClass(VerificationActivity.this, SMSVerificationActivity.class);
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
