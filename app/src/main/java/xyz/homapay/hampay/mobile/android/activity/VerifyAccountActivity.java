package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.VerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.VerifyTransferMoneyResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationVerifyTransferMoney;
import xyz.homapay.hampay.mobile.android.async.RequestVerifyTransferMoney;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class VerifyAccountActivity extends ActionBarActivity {


    FacedTextView verification_response_text;
    ButtonRectangle verify_account_button;
    Bundle bundle;
    String TransferMoneyComment = "";
    Context context;
    Activity activity;

    RequestVerifyTransferMoney requestVerifyTransferMoney;
    VerifyTransferMoneyRequest verifyTransferMoneyRequest;

    Tracker hamPayGaTracker;

    public void contactUs(View view){
        (new HamPayDialog(this)).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        context = this;
        activity = VerifyAccountActivity.this;

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        bundle = getIntent().getExtras();
        TransferMoneyComment = bundle.getString(Constants.TRANSFER_MONEY_COMMENT);
        verification_response_text = (FacedTextView)findViewById(R.id.verification_response_text);
        verification_response_text.setText(new PersianEnglishDigit(TransferMoneyComment).E2P());

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

    @Override
    protected void onStop() {
        super.onStop();
        if (requestVerifyTransferMoney != null){
            if (!requestVerifyTransferMoney.isCancelled()){
                requestVerifyTransferMoney.cancel(true);
            }
        }
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

                        hamPayGaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Verify Transfer Money")
                                .setAction("Verify")
                                .setLabel("Success")
                                .build());
                    }else {
                        requestVerifyTransferMoney = new RequestVerifyTransferMoney(context, new RequestVerifyTransferMoneyTaskCompleteListener());
                        new HamPayDialog(activity).showFailVerifyTransferMoneyDialog(requestVerifyTransferMoney, verifyTransferMoneyRequest,
                                "",
                                verifyTransferMoneyResponseMessage.getService().getMessage());

                        hamPayGaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Verify Transfer Money")
                                .setAction("Verify")
                                .setLabel("Fail")
                                .build());
                    }
                }else {
                    requestVerifyTransferMoney = new RequestVerifyTransferMoney(context, new RequestVerifyTransferMoneyTaskCompleteListener());
                    new HamPayDialog(activity).showFailVerifyTransferMoneyDialog(requestVerifyTransferMoney, verifyTransferMoneyRequest,
                            verifyTransferMoneyResponseMessage.getService().getResultStatus().getCode(),
                            verifyTransferMoneyResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Verify Transfer Money")
                            .setAction("Verify")
                            .setLabel("Fail")
                            .build());
                }
            }

            else {
                requestVerifyTransferMoney = new RequestVerifyTransferMoney(context, new RequestVerifyTransferMoneyTaskCompleteListener());
                new HamPayDialog(activity).showFailVerifyTransferMoneyDialog(requestVerifyTransferMoney, verifyTransferMoneyRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.mgs_fail_verify_transfer_money));
            }
        }

        @Override
        public void onTaskPreRun() { }
    }
}
