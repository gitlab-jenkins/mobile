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
import com.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import com.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestRegistrationSendSmsToken;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.NetworkConnectivity;

public class VerificationActivity extends Activity {

    ButtonRectangle keepOn_button;
    Context context;

    NetworkConnectivity networkConnectivity;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Activity activity;

    RequestRegistrationSendSmsToken requestRegistrationSendSmsToken;
    RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest;

    HamPayDialog hamPayDialog;

    public void contactUs(View view){
//        new HamPayDialog(this).showContactUsDialog();
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/ver-num.html");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        activity = VerificationActivity.this;

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, VerificationActivity.class.getName());
        editor.commit();

        hamPayDialog = new HamPayDialog(activity);

        context = this;
        networkConnectivity = new NetworkConnectivity(context);

        keepOn_button = (ButtonRectangle)findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationSendSmsTokenRequest = new RegistrationSendSmsTokenRequest();
                registrationSendSmsTokenRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));

                requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener());
                requestRegistrationSendSmsToken.execute(registrationSendSmsTokenRequest);
            }
        });
    }

    public class RequestRegistrationSendSmsTokenTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationSendSmsTokenResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponse)
        {

            keepOn_button.setEnabled(true);
//            loading_rl.setVisibility(View.GONE);

            hamPayDialog.dismisWaitingDialog();

            if (registrationSendSmsTokenResponse != null) {
                if (registrationSendSmsTokenResponse.getService().getResultStatus() == ResultStatus.SUCCESS) {

//                    editor.putString(Constants.REGISTERED_ACTIVITY_DATA, VerificationActivity.class.toString());
//                    editor.commit();

                    Intent intent = new Intent();
                    intent.setClass(VerificationActivity.this, SMSVerificationActivity.class);
//                    editor.putString(Constants.REGISTERED_ACTIVITY_DATA, SMSVerificationActivity.class.toString());
//                    editor.commit();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }else {

                    requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener());
                    new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                            registrationSendSmsTokenResponse.getService().getResultStatus().getCode(),
                            registrationSendSmsTokenResponse.getService().getResultStatus().getDescription());
                }

            }else {
                requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(context, new RequestRegistrationSendSmsTokenTaskCompleteListener());
                new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                        "200",
                        getString(R.string.mgs_fail_registration_send_sms_token));
            }
        }

        @Override
        public void onTaskPreRun() {
            keepOn_button.setEnabled(false);
//            loading_rl.setVisibility(View.VISIBLE);
            hamPayDialog.showWaitingdDialog("");
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }

}
