package com.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.RegistrationVerifyAccountRequest;
import com.hampay.common.core.model.request.RegistrationVerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.RegistrationVerifyAccountResponse;
import com.hampay.common.core.model.response.RegistrationVerifyTransferMoneyResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestRegisterVerifyAccount;
import com.hampay.mobile.android.async.RequestRegistrationVerifyTransferMoney;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonFlat;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.NetworkConnectivity;

public class RegVerifyAccountNoActivity extends ActionBarActivity {

    ButtonRectangle keepOn_button;

    FacedTextView verification_response_text;

    SharedPreferences prefs;

    Context context;

    RelativeLayout loading_rl;
    NetworkConnectivity networkConnectivity;

    Bundle bundle;
    String TransferMoneyComment;

    public void contactUs(View view){
        new HamPayDialog(this).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_verify_account_no);

        prefs = getPreferences(MODE_PRIVATE);

        bundle = getIntent().getExtras();
        TransferMoneyComment = bundle.getString(Constants.TRANSFER_MONEY_COMMENT);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);
        networkConnectivity = new NetworkConnectivity(this);

        context = this;

        verification_response_text = (FacedTextView)findViewById(R.id.verification_response_text);
        verification_response_text.setText(TransferMoneyComment);

        keepOn_button = (ButtonRectangle)findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (networkConnectivity.isNetworkConnected()) {
                    RegistrationVerifyTransferMoneyRequest registrationVerifyTransferMoneyRequest = new RegistrationVerifyTransferMoneyRequest();
                    registrationVerifyTransferMoneyRequest.setUserIdToken(prefs.getString("UserIdToken", ""));
                    new RequestRegistrationVerifyTransferMoney(context, new RequestRegistrationVerifyTransferMoneyTaskCompleteListener()).execute(registrationVerifyTransferMoneyRequest);
                } else {
                    Toast.makeText(context, getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                }

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

            if (verifyTransferMoneyResponseMessage.getService().getResultStatus() != null) {

                loading_rl.setVisibility(View.GONE);

                if (verifyTransferMoneyResponseMessage.getService().getIsVerified()){
                    Intent intent = new Intent();
                    intent.setClass(RegVerifyAccountNoActivity.this, PrePasswordActivity.class);
                    startActivity(intent);

                }
            }

        }

        @Override
        public void onTaskPreRun() {
            loading_rl.setVisibility(View.VISIBLE);
        }
    }
}
