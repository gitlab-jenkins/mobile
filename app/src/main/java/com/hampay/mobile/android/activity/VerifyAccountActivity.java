package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.VerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.VerifyTransferMoneyResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestVerifyTransferMoney;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;

public class VerifyAccountActivity extends ActionBarActivity {


    FacedTextView verification_response_text;
    ButtonRectangle verify_account_button;
    Bundle bundle;
    String TransferMoneyComment = "";
    Context context;
    Activity activity;

    RequestVerifyTransferMoney requestVerifyTransferMoney;
    VerifyTransferMoneyRequest verifyTransferMoneyRequest;

    public void contactUs(View view){
        (new HamPayDialog(this)).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        context = this;
        activity = VerifyAccountActivity.this;

        bundle = getIntent().getExtras();
        TransferMoneyComment = bundle.getString(Constants.TRANSFER_MONEY_COMMENT, "");
        verification_response_text = (FacedTextView)findViewById(R.id.verification_response_text);
        verification_response_text.setText(TransferMoneyComment);

        verify_account_button = (ButtonRectangle)findViewById(R.id.verify_account_button);
        verify_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verifyTransferMoneyRequest = new VerifyTransferMoneyRequest();
                requestVerifyTransferMoney = new RequestVerifyTransferMoney(context, new RequestVerifyTransferMoneyTaskCompleteListener());
                requestVerifyTransferMoney.execute(verifyTransferMoneyRequest);
            }
        });
    }


    public class RequestVerifyTransferMoneyTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<VerifyTransferMoneyResponse>>
    {
        public RequestVerifyTransferMoneyTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<VerifyTransferMoneyResponse> verifyTransferMoneyResponseMessage)
        {
            if (verifyTransferMoneyResponseMessage != null) {
                if (verifyTransferMoneyResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    if (verifyTransferMoneyResponseMessage.getService().getIsVerified()) {
                        Intent intent = new Intent();
                        intent.setClass(VerifyAccountActivity.this, CongratsAccountActivity.class);
                        startActivityForResult(intent, 1023);
                        finish();
                    }
                }else {
                    requestVerifyTransferMoney = new RequestVerifyTransferMoney(context, new RequestVerifyTransferMoneyTaskCompleteListener());
                    new HamPayDialog(activity).showFailVerifyTransferMoneyDialog(requestVerifyTransferMoney, verifyTransferMoneyRequest,
                            verifyTransferMoneyResponseMessage.getService().getResultStatus().getCode(),
                            verifyTransferMoneyResponseMessage.getService().getResultStatus().getDescription());
                }
            }

            else {
                requestVerifyTransferMoney = new RequestVerifyTransferMoney(context, new RequestVerifyTransferMoneyTaskCompleteListener());
                new HamPayDialog(activity).showFailVerifyTransferMoneyDialog(requestVerifyTransferMoney, verifyTransferMoneyRequest,
                        "2000",
                        getString(R.string.mgs_fail_verify_transfer_money));
            }
        }

        @Override
        public void onTaskPreRun() { }
    }
}
