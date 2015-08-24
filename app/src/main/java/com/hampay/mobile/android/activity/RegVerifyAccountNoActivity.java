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
import com.hampay.common.core.model.request.RegistrationVerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.RegistrationVerifyTransferMoneyResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestRegistrationVerifyTransferMoney;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.NetworkConnectivity;

public class RegVerifyAccountNoActivity extends Activity {

    ButtonRectangle keepOn_button;

    FacedTextView verification_response_text;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Context context;

    HamPayDialog hamPayDialog;

    NetworkConnectivity networkConnectivity;

    Bundle bundle;
    String TransferMoneyComment;

    Activity activity;

    RequestRegistrationVerifyTransferMoney requestRegistrationVerifyTransferMoney;
    RegistrationVerifyTransferMoneyRequest registrationVerifyTransferMoneyRequest;


    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/accountVerification.html");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_verify_account_no);

        activity = RegVerifyAccountNoActivity.this;

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, RegVerifyAccountNoActivity.class.getName());
        editor.commit();


        bundle = getIntent().getExtras();
        TransferMoneyComment = bundle.getString(Constants.TRANSFER_MONEY_COMMENT);

//        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);
        hamPayDialog = new HamPayDialog(activity);
        networkConnectivity = new NetworkConnectivity(this);

        context = this;

        verification_response_text = (FacedTextView)findViewById(R.id.verification_response_text);
        verification_response_text.setText(TransferMoneyComment);

        keepOn_button = (ButtonRectangle)findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registrationVerifyTransferMoneyRequest = new RegistrationVerifyTransferMoneyRequest();
                registrationVerifyTransferMoneyRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                requestRegistrationVerifyTransferMoney = new RequestRegistrationVerifyTransferMoney(context, new RequestRegistrationVerifyTransferMoneyTaskCompleteListener());
                requestRegistrationVerifyTransferMoney.execute(registrationVerifyTransferMoneyRequest);

            }
        });

    }


    public class RequestRegistrationVerifyTransferMoneyTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationVerifyTransferMoneyResponse>>
    {
        public RequestRegistrationVerifyTransferMoneyTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationVerifyTransferMoneyResponse> verifyTransferMoneyResponseMessage)
        {

//            loading_rl.setVisibility(View.GONE);

            hamPayDialog.dismisWaitingDialog();

            if (verifyTransferMoneyResponseMessage != null) {

                if (verifyTransferMoneyResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    if (verifyTransferMoneyResponseMessage.getService().getIsVerified()) {
                        Intent intent = new Intent();
                        intent.setClass(RegVerifyAccountNoActivity.this, PostRegVerifyAccountNoActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }
                }else {
                    requestRegistrationVerifyTransferMoney = new RequestRegistrationVerifyTransferMoney(context, new RequestRegistrationVerifyTransferMoneyTaskCompleteListener());
                    new HamPayDialog(activity).showFailRequestVerifyTransferMoneyDialog(requestRegistrationVerifyTransferMoney, registrationVerifyTransferMoneyRequest,
                            verifyTransferMoneyResponseMessage.getService().getResultStatus().getCode(),
                            verifyTransferMoneyResponseMessage.getService().getResultStatus().getDescription());
                }
            }else {
                requestRegistrationVerifyTransferMoney = new RequestRegistrationVerifyTransferMoney(context, new RequestRegistrationVerifyTransferMoneyTaskCompleteListener());
                new HamPayDialog(activity).showFailRequestVerifyTransferMoneyDialog(requestRegistrationVerifyTransferMoney, registrationVerifyTransferMoneyRequest,
                        "2000",
                        getString(R.string.mgs_fail_verify_transfer_money));
            }

        }

        @Override
        public void onTaskPreRun() {
//            loading_rl.setVisibility(View.VISIBLE);
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }
}
