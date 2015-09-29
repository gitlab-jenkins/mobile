package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.RegistrationVerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.RegistrationVerifyTransferMoneyResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationVerifyTransferMoney;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.NetworkConnectivity;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

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

    Tracker hamPayGaTracker;


    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/accountVerification.html");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_verify_account_no);

        activity = RegVerifyAccountNoActivity.this;

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        bundle = getIntent().getExtras();

        if (bundle != null && bundle.getString(Constants.TRANSFER_MONEY_COMMENT) != null) {
            TransferMoneyComment = bundle.getString(Constants.TRANSFER_MONEY_COMMENT);
            editor.putString(Constants.TRANSFER_MONEY_COMMENT, TransferMoneyComment);
            editor.commit();
        }else {
            TransferMoneyComment = prefs.getString(Constants.TRANSFER_MONEY_COMMENT, "");
        }

        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, RegVerifyAccountNoActivity.class.getName());
        editor.commit();

        hamPayDialog = new HamPayDialog(activity);
        networkConnectivity = new NetworkConnectivity(this);

        context = this;

        verification_response_text = (FacedTextView)findViewById(R.id.verification_response_text);
        verification_response_text.setText(new PersianEnglishDigit(TransferMoneyComment).E2P());

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

            hamPayDialog.dismisWaitingDialog();

            if (verifyTransferMoneyResponseMessage != null) {

                if (verifyTransferMoneyResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    if (verifyTransferMoneyResponseMessage.getService().getIsVerified()) {
                        Intent intent = new Intent();
                        intent.setClass(RegVerifyAccountNoActivity.this, PostRegVerifyAccountNoActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);

                        hamPayGaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Verify Transfer Money")
                                .setAction("Transfer")
                                .setLabel("Success")
                                .build());

                    }else {
                        requestRegistrationVerifyTransferMoney = new RequestRegistrationVerifyTransferMoney(context, new RequestRegistrationVerifyTransferMoneyTaskCompleteListener());
                        new HamPayDialog(activity).showFailRequestVerifyTransferMoneyDialog(requestRegistrationVerifyTransferMoney, registrationVerifyTransferMoneyRequest,
                                "",
                                verifyTransferMoneyResponseMessage.getService().getMessage());

                        hamPayGaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Verify Transfer Money")
                                .setAction("Transfer")
                                .setLabel("Success(Is Not Verified)")
                                .build());
                    }
                }else if (verifyTransferMoneyResponseMessage.getService().getResultStatus() == ResultStatus.REGISTRATION_INVALID_STEP){
                    new HamPayDialog(activity).showInvalidStepDialog();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Verify Transfer Money")
                            .setAction("Transfer")
                            .setLabel("Success(Invalid)")
                            .build());
                }
                else {
                    requestRegistrationVerifyTransferMoney = new RequestRegistrationVerifyTransferMoney(context, new RequestRegistrationVerifyTransferMoneyTaskCompleteListener());
                    new HamPayDialog(activity).showFailRequestVerifyTransferMoneyDialog(requestRegistrationVerifyTransferMoney, registrationVerifyTransferMoneyRequest,
                            verifyTransferMoneyResponseMessage.getService().getResultStatus().getCode(),
                            verifyTransferMoneyResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Verify Transfer Money")
                            .setAction("Transfer")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestRegistrationVerifyTransferMoney = new RequestRegistrationVerifyTransferMoney(context, new RequestRegistrationVerifyTransferMoneyTaskCompleteListener());
                new HamPayDialog(activity).showFailRequestVerifyTransferMoneyDialog(requestRegistrationVerifyTransferMoney, registrationVerifyTransferMoneyRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.mgs_fail_verify_transfer_money));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Verify Transfer Money")
                        .setAction("Transfer")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }
}
