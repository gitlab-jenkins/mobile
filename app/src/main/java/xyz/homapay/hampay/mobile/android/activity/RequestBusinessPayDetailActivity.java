package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.UserVerificationStatus;
import xyz.homapay.hampay.common.core.model.request.GetUserIdTokenRequest;
import xyz.homapay.hampay.common.core.model.request.IndividualPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.request.LatestPurchaseRequest;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.response.GetUserIdTokenResponse;
import xyz.homapay.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import xyz.homapay.hampay.common.core.model.response.LatestPurchaseResponse;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.common.psp.model.request.PurchaseRequest;
import xyz.homapay.hampay.common.psp.model.response.PurchaseResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.RequestIndividualPaymentConfirm;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestUserIdToken;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class RequestBusinessPayDetailActivity extends AppCompatActivity {

    ButtonRectangle pay_to_business_button;
    ButtonRectangle cancel_pay_to_business_button;

    Bundle bundle;

    FacedTextView contact_name;
    FacedEditText contact_message;
    FacedTextView credit_value;
    ImageView credit_value_icon;
    boolean intentContact = false;
    Context context;
    Activity activity;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public void backActionBar(View view){
        finish();
    }

    UserVerificationStatus userVerificationStatus;
    String userVerificationMessage = "";

    Long MaxXferAmount = 0L;
    Long MinXferAmount = 0L;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;

    FacedTextView business_name;
    ImageView business_logo;

    FacedTextView input_digit_1;
    FacedTextView input_digit_2;
    FacedTextView input_digit_3;
    FacedTextView input_digit_4;
    FacedTextView input_digit_5;
    FacedTextView input_digit_6;

    FacedTextView paymentPriceValue;
    FacedTextView paymentFeeValue;
    FacedTextView cardNumberValue;
    FacedEditText pin2Value;

    RequestLatestPurchase requestLatestPurchase;
    LatestPurchaseRequest latestPurchaseRequest;

    DatabaseHelper databaseHelper;

    PurchaseInfoDTO purchaseInfoDTO = null;

    RequestPSPResult requestPSPResult;
    PSPResultRequest pspResultRequest;

    RequestPurchase requestPurchase;
    PurchaseRequest purchaseRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requets_business_pay_detail);

        context = this;
        activity = RequestBusinessPayDetailActivity.this;


        databaseHelper = new DatabaseHelper(context);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        try {

            MaxXferAmount = prefs.getLong(Constants.MAX_XFER_Amount, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_XFER_Amount, 0);

        }catch (Exception ex){
            Log.e("Error", ex.getStackTrace().toString());
        }

        switch (prefs.getInt(Constants.USER_VERIFICATION_STATUS, -1)){

            case 0:
                userVerificationStatus = UserVerificationStatus.UNVERIFIED;
                userVerificationMessage = getString(R.string.unverified_account);
                break;

            case 1:
                userVerificationStatus = UserVerificationStatus.PENDING_REVIEW;
                userVerificationMessage = getString(R.string.pending_review_account);
                break;

            case 2:
                userVerificationStatus = UserVerificationStatus.VERIFIED;
                userVerificationMessage = getString(R.string.verified_account);
                break;

            case 3:
                userVerificationStatus = UserVerificationStatus.DELEGATED;
                userVerificationMessage = getString(R.string.delegate_account);
                break;

        }

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        credit_value = (FacedTextView)findViewById(R.id.credit_value);
        credit_value_icon = (ImageView)findViewById(R.id.credit_value_icon);

        contact_message = (FacedEditText)findViewById(R.id.contact_message);
        contact_name = (FacedTextView)findViewById(R.id.contact_name);


        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView)findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView)findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView)findViewById(R.id.input_digit_4);
        input_digit_5 = (FacedTextView)findViewById(R.id.input_digit_5);
        input_digit_6 = (FacedTextView)findViewById(R.id.input_digit_6);
        business_name = (FacedTextView)findViewById(R.id.business_name);
        business_logo = (ImageView)findViewById(R.id.business_logo);
        paymentPriceValue = (FacedTextView)findViewById(R.id.paymentPriceValue);
        paymentFeeValue = (FacedTextView)findViewById(R.id.paymentFeeValue);
        cardNumberValue = (FacedTextView)findViewById(R.id.cardNumberValue);
        pin2Value = (FacedEditText)findViewById(R.id.pin2Value);



        bundle = getIntent().getExtras();



        if (bundle != null) {

        }else {
            requestLatestPurchase = new RequestLatestPurchase(activity, new RequestLatestPurchaseTaskCompleteListener());
            latestPurchaseRequest = new LatestPurchaseRequest();
            requestLatestPurchase.execute(latestPurchaseRequest);
        }


        pay_to_business_button = (ButtonRectangle)findViewById(R.id.pay_to_business_button);
        pay_to_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPurchase = new RequestPurchase(activity, new RequestPurchaseTaskCompleteListener());
                purchaseRequest = new PurchaseRequest();
                purchaseRequest.setPin2(pin2Value.getText().toString());
                purchaseRequest.setAmount(purchaseInfoDTO.getAmount() + purchaseInfoDTO.getPurchaseFee());
                purchaseRequest.setMerchantId(purchaseInfoDTO.getMerchantLogoName());
                purchaseRequest.setMobileNumber(prefs.getString(Constants.REGISTERED_CELL_NUMBER, ""));
                purchaseRequest.setPurchaseRequestId(purchaseInfoDTO.getPurchaseRequestId());
                requestPurchase.execute(purchaseRequest);
            }
        });

        cancel_pay_to_business_button = (ButtonRectangle)findViewById(R.id.cancel_pay_to_business_button);
        cancel_pay_to_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (purchaseInfoDTO != null){
                    if (databaseHelper.getIsExistPurchaseRequest(purchaseInfoDTO.getPurchaseRequestId())){
                        databaseHelper.updatePurchaseRequest(purchaseInfoDTO.getPurchaseRequestId(), "1");
                    }else {
                        databaseHelper.createPurchaseRequest(purchaseInfoDTO.getPurchaseRequestId());
                        databaseHelper.updatePurchaseRequest(purchaseInfoDTO.getPurchaseRequestId(), "1");
                    }
                }

                finish();

            }
        });
    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        Log.e("EXIT", "onUserInteraction");
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.e("EXIT", "onUserLeaveHint");
        editor.putString(Constants.USER_ID_TOKEN, "");
        editor.commit();
    }

    @Override
    public void onBackPressed() {

        if (intentContact){
            Intent i = new Intent();
            i.setClass(this, MainActivity.class);
            startActivity(i);
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", 1024);
        setResult(1024);

        finish();
    }


    public class RequestPurchaseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PurchaseResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<PurchaseResponse> purchaseResponseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (purchaseResponseResponseMessage != null){
                if (purchaseResponseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    PurchaseResponse purchaseResponse = purchaseResponseResponseMessage.getService();
                    pspResultRequest = new PSPResultRequest();
                    pspResultRequest.setPurchaseRequestId(purchaseInfoDTO.getPurchaseRequestId());
                    pspResultRequest.setPspResponseCode(purchaseResponse.getResponseCode());
                    pspResultRequest.setPurchaseCode(purchaseInfoDTO.getPurchaseCode());
                    requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener());
                    requestPSPResult.execute(pspResultRequest);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Success")
                            .build());

                }else {

                    new HamPayDialog(activity).showFailPaymentDialog(purchaseResponseResponseMessage.getService().getServiceDefinition().getCode(),
                            purchaseResponseResponseMessage.getService().getResponseCode());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog(Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pending Payment Request")
                        .setAction("Payment")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

            pay_to_business_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() {}
    }

    public class RequestPSPResultTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PSPResultResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<PSPResultResponse> pspResultResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pspResultResponseMessage != null){
                if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){


                    new HamPayDialog(activity).purchaseDialog(purchaseInfoDTO.getPurchaseCode() + "");

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Success")
                            .build());

                }else {

                    new HamPayDialog(activity).showFailPaymentDialog(pspResultResponseMessage.getService().getServiceDefinition().getCode(),
                            pspResultResponseMessage.getService().getServiceDefinition().getCode());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog(Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pending Payment Request")
                        .setAction("Payment")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

            pay_to_business_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() {}
    }


    public class RequestLatestPurchaseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<LatestPurchaseResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<LatestPurchaseResponse> latestPurchaseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (latestPurchaseResponseMessage != null){
                if (latestPurchaseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){


                    PersianEnglishDigit persianEnglishDigit = new PersianEnglishDigit();

                    purchaseInfoDTO = latestPurchaseResponseMessage.getService().getPurchaseInfo();


                    String persianPurchaseCode = persianEnglishDigit.E2P(purchaseInfoDTO.getPurchaseCode());

                    input_digit_1.setText(persianPurchaseCode.charAt(0) + "");
                    input_digit_2.setText(persianPurchaseCode.charAt(1) + "");
                    input_digit_3.setText(persianPurchaseCode.charAt(2) + "");
                    input_digit_4.setText(persianPurchaseCode.charAt(3) + "");
                    input_digit_5.setText(persianPurchaseCode.charAt(4) + "");
                    input_digit_6.setText(persianPurchaseCode.charAt(5) + "");

                    paymentPriceValue.setText(persianEnglishDigit.E2P(purchaseInfoDTO.getAmount().toString()) + " ریال");
                    paymentFeeValue.setText(persianEnglishDigit.E2P(purchaseInfoDTO.getPurchaseFee().toString()) + " ریال");

                    business_name.setText(persianEnglishDigit.E2P(purchaseInfoDTO.getMerchantName()));

                    String LogoUrl = Constants.HTTPS_SERVER_IP + "/merchant-logo/" + purchaseInfoDTO.getMerchantLogoName();

                    new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(business_logo)).execute(Constants.HTTPS_SERVER_IP + "/merchant-logo/" + purchaseInfoDTO.getMerchantLogoName());


                    cardNumberValue.setText(persianEnglishDigit.E2P(purchaseInfoDTO.getCardNo()));

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Latest Pending Payment")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());

                }else {
                    requestLatestPurchase = new RequestLatestPurchase(context, new RequestLatestPurchaseTaskCompleteListener());

                    new HamPayDialog(activity).showFailPendingPaymentDialog(requestLatestPurchase, latestPurchaseRequest,
                            latestPurchaseResponseMessage.getService().getServiceDefinition().getCode(),
                            /*latestPurchaseResponseMessage.getService().getMessage()*/"");

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Latest Pending Payment")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {

                requestLatestPurchase = new RequestLatestPurchase(context, new RequestLatestPurchaseTaskCompleteListener());

                new HamPayDialog(activity).showFailPendingPaymentDialog(requestLatestPurchase, latestPurchaseRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_fetch_latest_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Latest Pending Payment")
                        .setAction("Fetch")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

            pay_to_business_button.setEnabled(true);

//            if (requestLatestPurchase.getStatus() == AsyncTask.Status.)
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

}
