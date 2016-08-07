package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import xyz.homapay.hampay.common.core.model.request.PurchaseDetailRequest;
import xyz.homapay.hampay.common.core.model.request.PurchaseInfoRequest;
import xyz.homapay.hampay.common.core.model.response.LatestPurchaseResponse;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseDetailResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseInfoResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPurchaseDetail;
import xyz.homapay.hampay.mobile.android.async.RequestPurchaseInfo;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.model.SyncPspResult;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.webservice.newpsp.TWAArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.newpsp.TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring;

public class RequestBusinessPayDetailActivity extends AppCompatActivity {


    private DatabaseHelper dbHelper;
    ImageView pay_to_business_button;
    Bundle bundle;
    FacedTextView contact_name;
    FacedEditText contact_message;
    FacedTextView amount_value;
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

    private LinearLayout purchase_status_layout;
    private LinearLayout purchase_payer_name_layout;
    private LinearLayout purchase_payer_cell_layout;
    private FacedTextView purchase_status;
    private FacedTextView purchase_payer_name;
    private FacedTextView purchase_payer_cell;
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
    String providerId = null;

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
        dbHelper = new DatabaseHelper(context);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        String LOGIN_TOKEN = prefs.getString(Constants.LOGIN_TOKEN_ID, null);
        if (LOGIN_TOKEN == null){
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
            return;
        }

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
        purchase_status_layout = (LinearLayout)findViewById(R.id.purchase_status_layout);
        purchase_payer_name_layout = (LinearLayout)findViewById(R.id.purchase_payer_name_layout);
        purchase_payer_cell_layout = (LinearLayout)findViewById(R.id.purchase_payer_cell_layout);
        purchase_status = (FacedTextView)findViewById(R.id.purchase_status);
        purchase_payer_cell = (FacedTextView)findViewById(R.id.purchase_payer_cell);
        purchase_payer_name = (FacedTextView)findViewById(R.id.purchase_payer_name);
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
        providerId = intent.getStringExtra(Constants.PROVIDER_ID);


        if (purchaseInfoDTO != null) {
            fillPurchase(purchaseInfoDTO);
        }else if (purchaseCode != null){
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            requestPurchaseInfo = new RequestPurchaseInfo(activity, new RequestPurchaseInfoTaskCompleteListener());
            purchaseInfoRequest = new PurchaseInfoRequest();
            purchaseInfoRequest.setPurchaseCode(purchaseCode);
            requestPurchaseInfo.execute(purchaseInfoRequest);
        }else if (providerId != null){
            PurchaseDetailRequest  purchaseDetailRequest = new PurchaseDetailRequest();
            purchaseDetailRequest.setProviderId(providerId);
            RequestPurchaseDetail requestPurchaseDetail = new RequestPurchaseDetail(activity, new RequestPurchaseDetailTaskCompleteListener());
            requestPurchaseDetail.execute(purchaseDetailRequest);

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

                if (pspInfoDTO.getCardDTO().getCardId() == null || (purchaseInfoDTO.getAmount() + purchaseInfoDTO.getFeeCharge() + purchaseInfoDTO.getVat() >= Constants.SOAP_AMOUNT_MAX)) {
                    Intent intent = new Intent();
                    intent.setClass(activity, BankWebPaymentActivity.class);
                    intent.putExtra(Constants.PURCHASE_INFO, purchaseInfoDTO);
                    intent.putExtra(Constants.PSP_INFO, pspInfoDTO);
                    startActivityForResult(intent, 45);
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
                    doWorkInfo.setCellNumber(pspInfoDTO.getCellNumber().substring(1, pspInfoDTO.getCellNumber().length()));
                    doWorkInfo.setLangAByte((byte) 0);
                    doWorkInfo.setLangABoolean(false);
                    TWAArrayOfKeyValueOfstringstring vectorstring2stringMapEntry = new TWAArrayOfKeyValueOfstringstring();
                    TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();



                    s2sMapEntry.Key = "Amount";
                    s2sMapEntry.Value = String.valueOf(purchaseInfoDTO.getAmount() + purchaseInfoDTO.getFeeCharge() + purchaseInfoDTO.getVat());
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
            ResultStatus resultStatus = ResultStatus.FAILURE;

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
                        if (purchaseInfoDTO != null) {
                            Intent intent = new Intent(context, PaymentCompletedActivity.class);
                            intent.putExtra(Constants.SUCCESS_PAYMENT_AMOUNT, purchaseInfoDTO.getAmount());
                            intent.putExtra(Constants.SUCCESS_PAYMENT_CODE, purchaseInfoDTO.getProductCode());
                            intent.putExtra(Constants.SUCCESS_PAYMENT_TRACE, pspInfoDTO.getProviderId());
                            startActivityForResult(intent, 45);
                        }
                        resultStatus = ResultStatus.SUCCESS;
                    }else if (responseCode.equalsIgnoreCase("51")) {
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, getString(R.string.msg_insufficient_credit));
                        resultStatus = ResultStatus.FAILURE;
                    }else {
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, description);
                        resultStatus = ResultStatus.FAILURE;
                    }

                    SyncPspResult syncPspResult = new SyncPspResult();
                    syncPspResult.setResponseCode(responseCode);
                    syncPspResult.setProductCode(purchaseInfoDTO.getProductCode());
                    syncPspResult.setType("PURCHASE");
                    syncPspResult.setSwTrace(SWTraceNum);
                    syncPspResult.setTimestamp(System.currentTimeMillis());
                    syncPspResult.setStatus(0);
                    dbHelper.createSyncPspResult(syncPspResult);

                    pspResultRequest.setPspResponseCode(responseCode);
                    pspResultRequest.setProductCode(purchaseInfoDTO.getProductCode());
                    pspResultRequest.setTrackingCode(SWTraceNum);
                    requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(SWTraceNum), 1);
                    requestPSPResult.execute(pspResultRequest);

                }else {
                    new HamPayDialog(activity).pspFailResultDialog(Constants.LOCAL_ERROR_CODE, getString(R.string.msg_soap_timeout));
                }

                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();

                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.ACTIVITY_RESULT, resultStatus.ordinal());
                setResult(Activity.RESULT_OK, returnIntent);

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pending Payment Request")
                        .setAction("Payment")
                        .setLabel("Success")
                        .build());

            } else {

                new HamPayDialog(activity).pspFailResultDialog(Constants.LOCAL_ERROR_CODE, getString(R.string.msg_soap_timeout));

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

        private String SWTrace;

        public RequestPSPResultTaskCompleteListener(String SWTrace){
            this.SWTrace = SWTrace;
        }


        @Override
        public void onTaskComplete(ResponseMessage<PSPResultResponse> pspResultResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pspResultResponseMessage != null){
                if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    if (SWTrace != null) {
                        dbHelper.syncPspResult(SWTrace);
                    }
                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Success")
                            .build());

                }else if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
                else {
                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
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

                    dbHelper.createViewedPurchaseRequest(purchaseInfoDTO.getProductCode());

                    if (purchaseInfoDTO == null){
                        new HamPayDialog(activity).showFailPendingPurchaseDialog(requestLatestPurchase, latestPurchaseRequest,
                                Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_pending_not_found));
                        return;
                    }

                    pspInfoDTO = latestPurchaseResponseMessage.getService().getPurchaseInfo().getPspInfo();

                    if (purchaseInfoDTO != null) {

                        fillPurchase(purchaseInfoDTO);

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

                } else if (latestPurchaseResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
                else {
                    requestLatestPurchase = new RequestLatestPurchase(context, new RequestLatestPurchaseTaskCompleteListener());

                    new HamPayDialog(activity).showFailPendingPurchaseDialog(requestLatestPurchase, latestPurchaseRequest,
                            latestPurchaseResponseMessage.getService().getResultStatus().getCode(),
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

                        fillPurchase(purchaseInfoDTO);
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

                }else if (purchaseInfoResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
                else {
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


    public class RequestPurchaseDetailTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PurchaseDetailResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<PurchaseDetailResponse> purchaseDetailResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (purchaseDetailResponseMessage != null) {

                if (purchaseDetailResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    purchaseInfoDTO = purchaseDetailResponseMessage.getService().getpurchaseInfo();
                    fillPurchase(purchaseInfoDTO);
                }else if (purchaseDetailResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


    private void fillPurchase(PurchaseInfoDTO purchaseInfo){

        pspInfoDTO = purchaseInfo.getPspInfo();

        PersianEnglishDigit persianEnglishDigit = new PersianEnglishDigit();

        String persianPurchaseCode = persianEnglishDigit.E2P(purchaseInfo.getPurchaseCode());

        input_digit_1.setText(persianPurchaseCode.charAt(0) + "");
        input_digit_2.setText(persianPurchaseCode.charAt(1) + "");
        input_digit_3.setText(persianPurchaseCode.charAt(2) + "");
        input_digit_4.setText(persianPurchaseCode.charAt(3) + "");
        input_digit_5.setText(persianPurchaseCode.charAt(4) + "");
        input_digit_6.setText(persianPurchaseCode.charAt(5) + "");

        switch (purchaseInfo.getStatus()){
            case SUCCESSFUL:
                purchase_status.setText(getString(R.string.purchase_status_succeed));
                if (purchaseInfo.getPayerName() != null) {
                    purchase_payer_name.setText(purchaseInfo.getPayerName());
                    purchase_payer_cell.setText(persianEnglishDigit.E2P(purchaseInfo.getPayerCellNumber()));
                }
                purchase_status_layout.setVisibility(View.VISIBLE);
                purchase_payer_name_layout.setVisibility(View.VISIBLE);
                purchase_payer_cell_layout.setVisibility(View.VISIBLE);
                creditInfo.setVisibility(View.GONE);
                pay_to_business_button.setVisibility(View.GONE);
                break;
            case FAILED:
                purchase_status.setText(getString(R.string.purchase_status_failed));
                if (purchaseInfo.getPayerName() != null) {
                    purchase_payer_name.setText(purchaseInfo.getPayerName());
                    purchase_payer_cell.setText(persianEnglishDigit.E2P(purchaseInfo.getPayerCellNumber()));
                }
                purchase_status_layout.setVisibility(View.VISIBLE);
                purchase_payer_name_layout.setVisibility(View.GONE);
                purchase_payer_cell_layout.setVisibility(View.GONE);
                creditInfo.setVisibility(View.GONE);
                pay_to_business_button.setVisibility(View.GONE);
                break;
            case  PROCESSING:
                purchase_status.setText(getString(R.string.purchase_status_processing));
                if (purchaseInfo.getPayerName() != null) {
                    purchase_payer_name.setText(purchaseInfo.getPayerName());
                    purchase_payer_cell.setText(persianEnglishDigit.E2P(purchaseInfo.getPayerCellNumber()));
                }
                purchase_status_layout.setVisibility(View.VISIBLE);
                purchase_payer_name_layout.setVisibility(View.GONE);
                purchase_payer_cell_layout.setVisibility(View.GONE);
                creditInfo.setVisibility(View.GONE);
                pay_to_business_button.setVisibility(View.GONE);
                break;
            case PENDING:
                purchase_status.setText(getString(R.string.purchase_status_pending));
                if (purchaseInfo.getPayerName() != null) {
                    purchase_payer_name.setText(purchaseInfo.getPayerName());
                    purchase_payer_cell.setText(persianEnglishDigit.E2P(purchaseInfo.getPayerCellNumber()));
                }
                purchase_status_layout.setVisibility(View.GONE);
                purchase_payer_name_layout.setVisibility(View.GONE);
                purchase_payer_cell_layout.setVisibility(View.GONE);
                pay_to_business_button.setVisibility(View.VISIBLE);
                if (pspInfoDTO.getCardDTO().getCardId() != null && (purchaseInfoDTO.getAmount() + purchaseInfoDTO.getFeeCharge() + purchaseInfoDTO.getVat() < Constants.SOAP_AMOUNT_MAX)) {
                    creditInfo.setVisibility(View.VISIBLE);
                    cardNumberValue.setText(persianEnglishDigit.E2P(pspInfoDTO.getCardDTO().getMaskedCardNumber()));
                    bankName.setText(pspInfoDTO.getCardDTO().getBankName());
                }
                break;
        }

        paymentPriceValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfo.getAmount())));
        paymentVAT.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfo.getVat())));
        paymentFeeValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfo.getFeeCharge())));
        paymentTotalValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(purchaseInfo.getAmount() + purchaseInfo.getFeeCharge() + purchaseInfo.getVat())));
        business_name.setText(persianEnglishDigit.E2P(purchaseInfo.getMerchantName()));

        if (purchaseInfo.getMerchantImageId() != null) {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            business_image.setTag(purchaseInfo.getMerchantImageId());
            imageManager.displayImage(purchaseInfo.getMerchantImageId(), business_image, R.drawable.user_placeholder);
        }else {
            business_image.setImageResource(R.drawable.user_placeholder);
        }
    }

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(context, HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (activity != null) {
            finish();
            startActivity(intent);
        }
    }

}
