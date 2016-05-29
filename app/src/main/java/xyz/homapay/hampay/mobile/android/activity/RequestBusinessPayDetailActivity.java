package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.LatestPurchaseRequest;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.request.PurchaseInfoRequest;
import xyz.homapay.hampay.common.core.model.response.LatestPurchaseResponse;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseInfoResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPurchaseInfo;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.webservice.newpsp.TWAArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.newpsp.TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring;

public class RequestBusinessPayDetailActivity extends AppCompatActivity {

    ImageView pay_to_business_button;
    FacedTextView cancel_pay_to_business_button;

    Bundle bundle;

    FacedTextView contact_name;
    FacedEditText contact_message;
    FacedTextView amount_value;
//    ImageView credit_value_icon;
    boolean intentContact = false;
    Context context;
    Activity activity;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public void backActionBar(View view){
        finish();
    }

    Long MaxXferAmount = 0L;
    Long MinXferAmount = 0L;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;

    FacedTextView business_name;
    ImageView business_image;

    FacedTextView input_digit_1;
    FacedTextView input_digit_2;
    FacedTextView input_digit_3;
    FacedTextView input_digit_4;
    FacedTextView input_digit_5;
    FacedTextView input_digit_6;

    FacedTextView paymentPriceValue;
    FacedTextView paymentVAT;
    FacedTextView paymentFeeValue;
    FacedTextView paymentTotalValue;
    FacedTextView cardNumberValue;
    FacedTextView bankName;
    FacedEditText pin2Value;

    RequestLatestPurchase requestLatestPurchase;
    LatestPurchaseRequest latestPurchaseRequest;

    DatabaseHelper databaseHelper;

    PurchaseInfoDTO purchaseInfoDTO = null;
    PspInfoDTO pspInfoDTO = null;
    String purchaseCode = null;

    LinearLayout creditInfo;

    RequestPSPResult requestPSPResult;
    PSPResultRequest pspResultRequest;

    private RequestPurchase requestPurchase;
    private DoWorkInfo doWorkInfo;

    RequestPurchaseInfo requestPurchaseInfo;
    PurchaseInfoRequest purchaseInfoRequest;

    private CurrencyFormatter currencyFormatter;
    private ImageManager imageManager;
    private String authToken;


    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PugNotification.with(context).cancel(Constants.MERCHANT_NOTIFICATION_IDENTIFIER);
        HamPayApplication.setAppSate(AppState.Resumed);
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        PugNotification.with(context).cancel(Constants.MERCHANT_NOTIFICATION_IDENTIFIER);
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requets_business_pay_detail);

        context = this;
        activity = RequestBusinessPayDetailActivity.this;
        PugNotification.with(context).cancel(Constants.MERCHANT_NOTIFICATION_IDENTIFIER);
        databaseHelper = new DatabaseHelper(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
        imageManager = new ImageManager(activity, 200000, false);

        try {

            MaxXferAmount = prefs.getLong(Constants.MAX_BUSINESS_XFER_AMOUNT, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_BUSINESS_XFER_AMOUNT, 0);

        }catch (Exception ex){
            Log.e("Error", ex.getStackTrace().toString());
        }

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        currencyFormatter = new CurrencyFormatter();

        amount_value = (FacedTextView)findViewById(R.id.amount_value);
//        credit_value_icon = (ImageView)findViewById(R.id.credit_value_icon);

        contact_message = (FacedEditText)findViewById(R.id.contact_message);
        contact_name = (FacedTextView)findViewById(R.id.contact_name);

        creditInfo = (LinearLayout)findViewById(R.id.creditInfo);

        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView)findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView)findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView)findViewById(R.id.input_digit_4);
        input_digit_5 = (FacedTextView)findViewById(R.id.input_digit_5);
        input_digit_6 = (FacedTextView)findViewById(R.id.input_digit_6);
        business_name = (FacedTextView)findViewById(R.id.business_name);
        business_image = (ImageView)findViewById(R.id.business_image);
        paymentPriceValue = (FacedTextView)findViewById(R.id.paymentPriceValue);
        paymentVAT = (FacedTextView)findViewById(R.id.paymentVAT);
        paymentFeeValue = (FacedTextView)findViewById(R.id.paymentFeeValue);
        bankName = (FacedTextView)findViewById(R.id.bankName);
        cardNumberValue = (FacedTextView)findViewById(R.id.cardNumberValue);
        pin2Value = (FacedEditText)findViewById(R.id.pin2Value);
        paymentTotalValue = (FacedTextView)findViewById(R.id.paymentTotalValue);

        bundle = getIntent().getExtras();


        Intent intent = getIntent();

        purchaseInfoDTO = (PurchaseInfoDTO)intent.getSerializableExtra(Constants.PENDING_PAYMENT_REQUEST_LIST);
        pspInfoDTO = (PspInfoDTO)intent.getSerializableExtra(Constants.PSP_INFO);
        purchaseCode = intent.getStringExtra(Constants.BUSINESS_PURCHASE_CODE);

        if (pspInfoDTO != null) {
            if (pspInfoDTO.getCardDTO().getCardId() == null) {
                creditInfo.setVisibility(View.GONE);
            } else {
                creditInfo.setVisibility(View.VISIBLE);
            }
        }

        if (purchaseInfoDTO != null) {
            PersianEnglishDigit persianEnglishDigit = new PersianEnglishDigit();

            String persianPurchaseCode = persianEnglishDigit.E2P(purchaseInfoDTO.getPurchaseCode());

            input_digit_1.setText(persianPurchaseCode.charAt(0) + "");
            input_digit_2.setText(persianPurchaseCode.charAt(1) + "");
            input_digit_3.setText(persianPurchaseCode.charAt(2) + "");
            input_digit_4.setText(persianPurchaseCode.charAt(3) + "");
            input_digit_5.setText(persianPurchaseCode.charAt(4) + "");
            input_digit_6.setText(persianPurchaseCode.charAt(5) + "");

            paymentPriceValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getAmount())));
            paymentVAT.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getVat())));
            paymentFeeValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getFeeCharge())));
            paymentTotalValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getAmount() + purchaseInfoDTO.getFeeCharge() + purchaseInfoDTO.getVat())));

            business_name.setText(persianEnglishDigit.E2P(purchaseInfoDTO.getMerchantName()));


            if (purchaseInfoDTO.getMerchantImageId() != null) {
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + purchaseInfoDTO.getMerchantImageId();
                business_image.setTag(userImageUrl.split("/")[6]);
                imageManager.displayImage(userImageUrl, business_image, R.drawable.user_placeholder);
            }else {
                business_image.setImageResource(R.drawable.user_placeholder);
            }

            cardNumberValue.setText(persianEnglishDigit.E2P(pspInfoDTO.getCardDTO().getMaskedCardNumber()));
            bankName.setText(pspInfoDTO.getCardDTO().getBankName());
        }else if (purchaseCode != null){
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            requestPurchaseInfo = new RequestPurchaseInfo(activity, new RequestPurchaseInfoTaskCompleteListener());
            purchaseInfoRequest = new PurchaseInfoRequest();
            purchaseInfoRequest.setPurchaseCode(purchaseCode);
            requestPurchaseInfo.execute(purchaseInfoRequest);
        }else {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            requestLatestPurchase = new RequestLatestPurchase(activity, new RequestLatestPurchaseTaskCompleteListener());
            latestPurchaseRequest = new LatestPurchaseRequest();
            requestLatestPurchase.execute(latestPurchaseRequest);
        }


        pay_to_business_button = (ImageView)findViewById(R.id.pay_to_business_button);
        pay_to_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pspInfoDTO.getCardDTO().getCardId() == null) {
                    Intent intent = new Intent();
                    intent.setClass(activity, BankWebPaymentActivity.class);
                    intent.putExtra(Constants.PURCHASE_INFO, purchaseInfoDTO);
                    intent.putExtra(Constants.PSP_INFO, pspInfoDTO);
                    startActivityForResult(intent, 45);
//                    finish();
                } else {
                    if (pin2Value.getText().toString().length() <= 4) {
                        Toast.makeText(context, getString(R.string.msg_pin2_incurrect), Toast.LENGTH_LONG).show();
                        return;
                    }
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();
                    requestPurchase = new RequestPurchase(activity, new RequestPurchaseTaskCompleteListener());

                    doWorkInfo = new DoWorkInfo();
                    doWorkInfo.setUserName("appstore");
                    doWorkInfo.setPassword("sepapp");
                    doWorkInfo.setCellNumber(prefs.getString(Constants.REGISTERED_CELL_NUMBER, "").substring(1, prefs.getString(Constants.REGISTERED_CELL_NUMBER, "").length()));
                    doWorkInfo.setLangAByte((byte) 0);
                    doWorkInfo.setLangABoolean(false);
                    TWAArrayOfKeyValueOfstringstring vectorstring2stringMapEntry = new TWAArrayOfKeyValueOfstringstring();
                    TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();



                    s2sMapEntry.Key = "Amount";
                    s2sMapEntry.Value = (purchaseInfoDTO.getAmount() + purchaseInfoDTO.getFeeCharge()) + "";
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "Pin2";
                    s2sMapEntry.Value = pin2Value.getText().toString();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "ThirdParty";
                    s2sMapEntry.Value = purchaseInfoDTO.getProductCode();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "TerminalId";
                    s2sMapEntry.Value = pspInfoDTO.getTerminalID();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "CardId";
                    s2sMapEntry.Value = pspInfoDTO.getCardDTO().getCardId();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "Merchant";
                    s2sMapEntry.Value = pspInfoDTO.getMerchant();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "SenderTerminalId";
                    s2sMapEntry.Value = pspInfoDTO.getTerminalID();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "IPAddress";
                    s2sMapEntry.Value = pspInfoDTO.getIpAddress();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    doWorkInfo.setVectorstring2stringMapEntry(vectorstring2stringMapEntry);
                    requestPurchase.execute(doWorkInfo);

                }
            }
        });


        //Enable Here
//        cancel_pay_to_business_button = (FacedTextView)findViewById(R.id.cancel_pay_to_business_button);
//        cancel_pay_to_business_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (purchaseInfoDTO != null) {
//                    if (dbHelper.getIsExistPurchaseRequest(purchaseInfoDTO.getProductCode())) {
//                        dbHelper.updatePurchaseRequest(purchaseInfoDTO.getProductCode(), "1");
//                    } else {
//                        dbHelper.createPurchaseRequest(purchaseInfoDTO.getProductCode());
//                        dbHelper.updatePurchaseRequest(purchaseInfoDTO.getProductCode(), "1");
//                    }
//                }
//
//                finish();
//
//            }
//        });
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


    public class RequestPurchaseTaskCompleteListener implements AsyncTaskCompleteListener<TWAArrayOfKeyValueOfstringstring> {

        @Override
        public void onTaskComplete(TWAArrayOfKeyValueOfstringstring purchaseResponseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            String responseCode = null;
            String description = null;
            String SWTraceNum = null;

            if (purchaseResponseResponseMessage != null) {
                pspResultRequest = new PSPResultRequest();
                for (TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry : purchaseResponseResponseMessage) {
                    if (s2sMapEntry.Key.equalsIgnoreCase("ResponseCode")) {
                        responseCode = s2sMapEntry.Value;
                    }else if (s2sMapEntry.Key.equalsIgnoreCase("Description")){
                        description = s2sMapEntry.Value;
                    }else if (s2sMapEntry.Key.equalsIgnoreCase("SWTraceNum")){
                        SWTraceNum = s2sMapEntry.Value;
                    }
                }

                if (responseCode != null){
                    if (responseCode.equalsIgnoreCase("2000")) {
                        new HamPayDialog(activity).pspSuccessResultDialog(SWTraceNum);
                    }else {
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, description);
                    }
                }else {
                    new HamPayDialog(activity).pspFailResultDialog("", "");
                }

                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                pspResultRequest.setPspResponseCode(responseCode);
                pspResultRequest.setProductCode(purchaseInfoDTO.getProductCode());
                pspResultRequest.setTrackingCode(SWTraceNum);
                requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(), 1);
                requestPSPResult.execute(pspResultRequest);

                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.ACTIVITY_RESULT, ResultStatus.SUCCESS.ordinal());
                setResult(Activity.RESULT_OK, returnIntent);

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pending Payment Request")
                        .setAction("Payment")
                        .setLabel("Success")
                        .build());

            } else {

                new HamPayDialog(activity).pspFailResultDialog("", "");

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pending Payment Request")
                        .setAction("Payment")
                        .setLabel("Fail(Server)")
                        .build());
            }
            pay_to_business_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    public class RequestPSPResultTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PSPResultResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<PSPResultResponse> pspResultResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pspResultResponseMessage != null){
                if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){


//                    new HamPayDialog(activity).pspResultDialog(pspResultResponseMessage.getService().getResultStatus().getCode() + "");

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Success")
                            .build());

                }else {

//                    new HamPayDialog(activity).showFailPaymentDialog(pspResultResponseMessage.getService().getResultStatus().getCode(),
//                            pspResultResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
//                new HamPayDialog(activity).showFailPaymentDialog(Constants.LOCAL_ERROR_CODE,
//                        getString(R.string.msg_fail_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pending Payment Request")
                        .setAction("Payment")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

            pay_to_business_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


    public class RequestLatestPurchaseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<LatestPurchaseResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<LatestPurchaseResponse> latestPurchaseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (latestPurchaseResponseMessage != null){
                if (latestPurchaseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    purchaseInfoDTO = latestPurchaseResponseMessage.getService().getPurchaseInfo();

                    pspInfoDTO = latestPurchaseResponseMessage.getService().getPurchaseInfo().getPspInfo();

                    if (purchaseInfoDTO != null) {

                        if (pspInfoDTO.getCardDTO().getCardId() == null) {
                            creditInfo.setVisibility(View.GONE);
                        }
                        else {
                            creditInfo.setVisibility(View.VISIBLE);
                        }

                        PersianEnglishDigit persianEnglishDigit = new PersianEnglishDigit();

                        String persianPurchaseCode = persianEnglishDigit.E2P(purchaseInfoDTO.getPurchaseCode());

                        input_digit_1.setText(persianPurchaseCode.charAt(0) + "");
                        input_digit_2.setText(persianPurchaseCode.charAt(1) + "");
                        input_digit_3.setText(persianPurchaseCode.charAt(2) + "");
                        input_digit_4.setText(persianPurchaseCode.charAt(3) + "");
                        input_digit_5.setText(persianPurchaseCode.charAt(4) + "");
                        input_digit_6.setText(persianPurchaseCode.charAt(5) + "");

                        paymentPriceValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getAmount())));
                        paymentVAT.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getVat())));
                        paymentFeeValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getFeeCharge())));
                        paymentTotalValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getAmount() + purchaseInfoDTO.getFeeCharge() + purchaseInfoDTO.getVat())));
                        business_name.setText(persianEnglishDigit.E2P(purchaseInfoDTO.getMerchantName()));


                        if (purchaseInfoDTO.getMerchantImageId() != null) {
                            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                            editor.commit();
                            String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + purchaseInfoDTO.getMerchantImageId();
                            business_image.setTag(userImageUrl.split("/")[6]);
                            imageManager.displayImage(userImageUrl, business_image, R.drawable.user_placeholder);
                        }else {
                            business_image.setImageResource(R.drawable.user_placeholder);
                        }


                        bankName.setText(pspInfoDTO.getCardDTO().getBankName());
                        cardNumberValue.setText(persianEnglishDigit.E2P(pspInfoDTO.getCardDTO().getMaskedCardNumber()));
                    }
                    else {
                        Toast.makeText(context, getString(R.string.msg_not_found_pending_payment_code), Toast.LENGTH_LONG).show();
                        finish();
                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Latest Pending Payment")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());

                }else {
                    requestLatestPurchase = new RequestLatestPurchase(context, new RequestLatestPurchaseTaskCompleteListener());

                    new HamPayDialog(activity).showFailPendingPurchaseDialog(requestLatestPurchase, latestPurchaseRequest,
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

                new HamPayDialog(activity).showFailPendingPurchaseDialog(requestLatestPurchase, latestPurchaseRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_fetch_latest_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Latest Pending Payment")
                        .setAction("Fetch")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

            pay_to_business_button.setEnabled(true);

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 45) {
            if(resultCode == Activity.RESULT_OK){
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, 0);
                if (result == 1){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.ACTIVITY_RESULT, ResultStatus.SUCCESS.ordinal());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }


    public class RequestPurchaseInfoTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PurchaseInfoResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<PurchaseInfoResponse> purchaseInfoResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (purchaseInfoResponseMessage != null){
                if (purchaseInfoResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    purchaseInfoDTO = purchaseInfoResponseMessage.getService().getPurchaseInfo();
                    pspInfoDTO = purchaseInfoResponseMessage.getService().getPurchaseInfo().getPspInfo();

                    if (purchaseInfoDTO != null) {

                        if (pspInfoDTO.getCardDTO().getCardId() == null) {
                            creditInfo.setVisibility(View.GONE);
                        }
                        else {
                            creditInfo.setVisibility(View.VISIBLE);
                        }

                        PersianEnglishDigit persianEnglishDigit = new PersianEnglishDigit();

                        String persianPurchaseCode = persianEnglishDigit.E2P(purchaseInfoDTO.getPurchaseCode());

                        input_digit_1.setText(persianPurchaseCode.charAt(0) + "");
                        input_digit_2.setText(persianPurchaseCode.charAt(1) + "");
                        input_digit_3.setText(persianPurchaseCode.charAt(2) + "");
                        input_digit_4.setText(persianPurchaseCode.charAt(3) + "");
                        input_digit_5.setText(persianPurchaseCode.charAt(4) + "");
                        input_digit_6.setText(persianPurchaseCode.charAt(5) + "");

                        paymentPriceValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getAmount())));
                        paymentVAT.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getVat())));
                        paymentFeeValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getFeeCharge())));
                        paymentTotalValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfoDTO.getAmount() + purchaseInfoDTO.getFeeCharge() + purchaseInfoDTO.getVat())));
                        business_name.setText(persianEnglishDigit.E2P(purchaseInfoDTO.getMerchantName()));

                        if (purchaseInfoDTO.getMerchantImageId() != null) {
                            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                            editor.commit();
                            String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + purchaseInfoDTO.getMerchantImageId();
                            business_image.setTag(userImageUrl.split("/")[6]);
                            imageManager.displayImage(userImageUrl, business_image, R.drawable.user_placeholder);
                        }else {
                            business_image.setImageResource(R.drawable.user_placeholder);
                        }

                        bankName.setText(pspInfoDTO.getCardDTO().getBankName());
                        cardNumberValue.setText(persianEnglishDigit.E2P(pspInfoDTO.getCardDTO().getMaskedCardNumber()));
                    }
                    else {
                        Toast.makeText(context, getString(R.string.msg_not_found_pending_payment_code), Toast.LENGTH_LONG).show();
                        finish();
                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Latest Pending Payment")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());

                }else {
                    requestPurchaseInfo = new RequestPurchaseInfo(context, new RequestPurchaseInfoTaskCompleteListener());
                    new HamPayDialog(activity).showFailPurchaseInfoDialog(requestPurchaseInfo, purchaseInfoRequest,
                            purchaseInfoResponseMessage.getService().getResultStatus().getCode(),
                            purchaseInfoResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Latest Pending Payment")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestPurchaseInfo = new RequestPurchaseInfo(context, new RequestPurchaseInfoTaskCompleteListener());
                new HamPayDialog(activity).showFailPurchaseInfoDialog(requestPurchaseInfo, purchaseInfoRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_fetch_latest_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Latest Pending Payment")
                        .setAction("Fetch")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

            pay_to_business_button.setEnabled(true);

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


}
