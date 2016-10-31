package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.LatestPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.request.PaymentDetailRequest;
import xyz.homapay.hampay.common.core.model.response.LatestPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.PaymentDetailResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPayment;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestPaymentDetail;
import xyz.homapay.hampay.mobile.android.async.RequestPurchase;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.model.SyncPspResult;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.PspCode;
import xyz.homapay.hampay.mobile.android.webservice.newpsp.TWAArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.newpsp.TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring;

public class InvoicePendingConfirmationActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    ImageView pay_button;
    ImageView user_image;
    FacedTextView callerName;
    FacedTextView paymentCode;
    FacedTextView received_message;
    FacedTextView create_date;
    FacedTextView paymentPriceValue;
    FacedTextView paymentVAT;
    FacedTextView paymentFeeValue;
    FacedTextView paymentTotalValue;
    FacedTextView bankName;
    FacedTextView cardNumberValue;
    FacedEditText pin2Value;

    CurrencyFormatter currencyFormatter;

    boolean intentContact = false;

    Context context;
    Activity activity;


    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public void backActionBar(View view) {
        finish();
    }

    HamPayDialog hamPayDialog;
    PaymentInfoDTO paymentInfoDTO = null;
    PspInfoDTO pspInfoDTO = null;
    String providerId = null;

    private RequestPurchase requestPurchase;
    private DoWorkInfo doWorkInfo;

    PersianEnglishDigit persianEnglishDigit;

    RequestPSPResult requestPSPResult;
    PSPResultRequest pspResultRequest;

    RequestLatestPayment requestLatestPayment;
    LatestPaymentRequest latestPaymentRequest;

    private String authToken;
    private ImageManager imageManager;

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
        PugNotification.with(context).cancel(Constants.INVOICE_NOTIFICATION_IDENTIFIER);
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
        PugNotification.with(context).cancel(Constants.INVOICE_NOTIFICATION_IDENTIFIER);
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
        setContentView(R.layout.activity_invoice_payment_pending);

        context = this;
        activity = InvoicePendingConfirmationActivity.this;

        PugNotification.with(context).cancel(Constants.INVOICE_NOTIFICATION_IDENTIFIER);

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

        imageManager = new ImageManager(activity, 200000, false);
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        hamPayDialog = new HamPayDialog(activity);

        persianEnglishDigit = new PersianEnglishDigit();
        currencyFormatter = new CurrencyFormatter();
        user_image = (ImageView)findViewById(R.id.user_image);
        callerName = (FacedTextView) findViewById(R.id.callerName);
        paymentCode = (FacedTextView)findViewById(R.id.paymentCode);
        create_date = (FacedTextView)findViewById(R.id.create_date);
        received_message = (FacedTextView) findViewById(R.id.received_message);
        paymentPriceValue = (FacedTextView) findViewById(R.id.paymentPriceValue);
        paymentVAT = (FacedTextView)findViewById(R.id.paymentVAT);
        paymentFeeValue = (FacedTextView)findViewById(R.id.paymentFeeValue);
        paymentTotalValue = (FacedTextView)findViewById(R.id.paymentTotalValue);
        bankName = (FacedTextView)findViewById(R.id.bankName);
        pin2Value = (FacedEditText) findViewById(R.id.pin2Value);
        cardNumberValue = (FacedTextView) findViewById(R.id.cardNumberValue);

        Intent intent = getIntent();

        paymentInfoDTO = (PaymentInfoDTO) intent.getSerializableExtra(Constants.PAYMENT_INFO);
        pspInfoDTO = (PspInfoDTO) intent.getSerializableExtra(Constants.PSP_INFO);
        providerId = intent.getStringExtra(Constants.PROVIDER_ID);

        if (paymentInfoDTO != null) {
            fillPayment(paymentInfoDTO, pspInfoDTO);
        }else if (providerId != null){
            PaymentDetailRequest paymentDetailRequest = new PaymentDetailRequest();
            paymentDetailRequest.setProviderId(providerId);
            RequestPaymentDetail requestPaymentDetail = new RequestPaymentDetail(activity, new RequestPaymentDetailTaskCompleteListener());
            requestPaymentDetail.execute(paymentDetailRequest);
        }else {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            requestLatestPayment = new RequestLatestPayment(activity, new RequestLatestPaymentTaskCompleteListener());
            latestPaymentRequest = new LatestPaymentRequest();
            requestLatestPayment.execute(latestPaymentRequest);
        }

        pay_button = (ImageView) findViewById(R.id.pay_button);
        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pspInfoDTO.getCardDTO().getCardId() == null || (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge() + paymentInfoDTO.getVat() >= Constants.SOAP_AMOUNT_MAX)) {
                    Intent intent = new Intent();
                    intent.setClass(activity, BankWebPaymentActivity.class);
                    intent.putExtra(Constants.PAYMENT_INFO, paymentInfoDTO);
                    intent.putExtra(Constants.PSP_INFO, pspInfoDTO);
                    startActivityForResult(intent, 46);
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
                    s2sMapEntry.Value = (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge() + paymentInfoDTO.getVat()) + "";
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "Pin2";
                    s2sMapEntry.Value = pin2Value.getText().toString();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "ThirdParty";
                    s2sMapEntry.Value = paymentInfoDTO.getProductCode();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "TerminalId";
                    s2sMapEntry.Value = pspInfoDTO.getTerminalId();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "CardId";
                    s2sMapEntry.Value = pspInfoDTO.getCardDTO().getCardId();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "SenderTerminalId";
                    s2sMapEntry.Value = pspInfoDTO.getTerminalId();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 46) {
            if(resultCode == Activity.RESULT_OK){
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0){
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


    @Override
    public void onBackPressed() {

        if (intentContact) {
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
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

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
                        serviceName = ServiceEvent.PSP_PAYMENT_SUCCESS;
                        if (paymentInfoDTO != null) {
                            Intent intent = new Intent(context, PaymentCompletedActivity.class);
                            intent.putExtra(Constants.SUCCESS_PAYMENT_AMOUNT, paymentInfoDTO.getAmount() + paymentInfoDTO.getVat() + paymentInfoDTO.getFeeCharge());
                            intent.putExtra(Constants.SUCCESS_PAYMENT_CODE, paymentInfoDTO.getProductCode());
                            intent.putExtra(Constants.SUCCESS_PAYMENT_TRACE, pspInfoDTO.getProviderId());
                            startActivityForResult(intent, 46);
                        }
                        resultStatus = ResultStatus.SUCCESS;
                    }else if (responseCode.equalsIgnoreCase("51")) {
                        serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, getString(R.string.msg_insufficient_credit));
                        resultStatus = ResultStatus.FAILURE;
                    }else {
                        serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
                        PspCode pspCode = new PspCode(context);
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, pspCode.getDescription(responseCode));
                        resultStatus = ResultStatus.FAILURE;
                    }
                    logEvent.log(serviceName);

                    SyncPspResult syncPspResult = new SyncPspResult();
                    syncPspResult.setResponseCode(responseCode);
                    syncPspResult.setProductCode(paymentInfoDTO.getProductCode());
                    syncPspResult.setType("PAYMENT");
                    syncPspResult.setSwTrace(SWTraceNum);
                    syncPspResult.setTimestamp(System.currentTimeMillis());
                    syncPspResult.setStatus(0);
                    dbHelper.createSyncPspResult(syncPspResult);

                    pspResultRequest.setPspResponseCode(responseCode);
                    pspResultRequest.setProductCode(paymentInfoDTO.getProductCode());
                    pspResultRequest.setTrackingCode(SWTraceNum);
                    requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(SWTraceNum), 2);
                    requestPSPResult.execute(pspResultRequest);

                }else {
                    new HamPayDialog(activity).pspFailResultDialog(Constants.LOCAL_ERROR_CODE, getString(R.string.msg_soap_timeout));
                }
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();

                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.ACTIVITY_RESULT, resultStatus.ordinal());
                setResult(Activity.RESULT_OK, returnIntent);
            } else {
                new HamPayDialog(activity).pspFailResultDialog(Constants.LOCAL_ERROR_CODE, getString(R.string.msg_soap_timeout));
            }

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    public class RequestPSPResultTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PSPResultResponse>> {

        private String SWTrace;
        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);

        public RequestPSPResultTaskCompleteListener(String SWTrace){
            this.SWTrace = SWTrace;
        }

        @Override
        public void onTaskComplete(ResponseMessage<PSPResultResponse> pspResultResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pspResultResponseMessage != null) {
                if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.PSP_RESULT_SUCCESS;
                    if (SWTrace != null) {
                        dbHelper.syncPspResult(SWTrace);
                    }
                } else if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.PSP_RESULT_FAILURE;
                    forceLogout();
                }else {
                    serviceName = ServiceEvent.PSP_RESULT_FAILURE;
                }
                logEvent.log(serviceName);
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    public class RequestLatestPaymentTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<LatestPaymentResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<LatestPaymentResponse> latestPaymentResponseMessage) {

            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);
            hamPayDialog.dismisWaitingDialog();

            if (latestPaymentResponseMessage != null) {
                if (latestPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.GET_LATEST_PAYMENT_SUCCESS;
                    paymentInfoDTO = latestPaymentResponseMessage.getService().getPaymentInfoDTO();
                    pspInfoDTO = latestPaymentResponseMessage.getService().getPaymentInfoDTO().getPspInfo();
                    if (paymentInfoDTO == null){
                        new HamPayDialog(activity).showFailPendingPaymentDialog(requestLatestPayment, latestPaymentRequest,
                                Constants.LOCAL_ERROR_CODE,
                                getString(R.string.msg_pending_not_found));
                        return;
                    }

                    fillPayment(paymentInfoDTO, pspInfoDTO);

                    dbHelper.createViewedPaymentRequest(paymentInfoDTO.getProductCode());

                }else if (latestPaymentResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.GET_LATEST_PAYMENT_FAILURE;
                    forceLogout();
                }
                else {
                    serviceName = ServiceEvent.GET_LATEST_PAYMENT_FAILURE;
                    requestLatestPayment = new RequestLatestPayment(context, new RequestLatestPaymentTaskCompleteListener());
                    new HamPayDialog(activity).showFailPendingPaymentDialog(requestLatestPayment, latestPaymentRequest,
                            latestPaymentResponseMessage.getService().getResultStatus().getCode(),
                            latestPaymentResponseMessage.getService().getResultStatus().getDescription());
                }
            }
            else
            {
                serviceName = ServiceEvent.GET_LATEST_PAYMENT_FAILURE;
                requestLatestPayment = new RequestLatestPayment(context, new RequestLatestPaymentTaskCompleteListener());
                new HamPayDialog(activity).showFailPendingPaymentDialog(requestLatestPayment, latestPaymentRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_fetch_latest_payment));
            }
            logEvent.log(serviceName);
        }
        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


    public class RequestPaymentDetailTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PaymentDetailResponse>> {

        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);

        @Override
        public void onTaskComplete(ResponseMessage<PaymentDetailResponse> paymentDetailResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (paymentDetailResponseMessage != null) {
                if (paymentDetailResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.PAYMENT_DETAIL_SUCCESS;
                    pay_button.setVisibility(View.VISIBLE);
                    paymentInfoDTO = paymentDetailResponseMessage.getService().getPaymentInfo();
                    pspInfoDTO = paymentDetailResponseMessage.getService().getPaymentInfo().getPspInfo();
                    fillPayment(paymentInfoDTO, pspInfoDTO);
                }else if (paymentDetailResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.PAYMENT_DETAIL_FAILURE;
                    forceLogout();
                }else {
                    serviceName = ServiceEvent.PAYMENT_DETAIL_FAILURE;
                }
                logEvent.log(serviceName);
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


    private void fillPayment(PaymentInfoDTO paymentInfo, PspInfoDTO pspInfo){
        callerName.setText(paymentInfo.getCallerName());
        paymentCode.setText(persianEnglishDigit.E2P(getString(R.string.payment_request_code) + paymentInfo.getProductCode()));
        create_date.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(paymentInfo.getCreatedBy())));
        if (paymentInfo.getMessage() != null) {
            received_message.setText(paymentInfo.getMessage().trim());
        }
        paymentPriceValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfo.getAmount())));
        paymentVAT.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfo.getVat())));
        paymentFeeValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfo.getFeeCharge())));
        paymentTotalValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfo.getAmount() + paymentInfo.getVat() + paymentInfo.getFeeCharge())));


        if (paymentInfo.getImageId() != null) {
            user_image.setTag(paymentInfo.getImageId());
            imageManager.displayImage(paymentInfo.getImageId(), user_image, R.drawable.user_placeholder);
        }else {
            user_image.setImageResource(R.drawable.user_placeholder);
        }

        if (pspInfo.getCardDTO().getCardId() != null && (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge() + paymentInfoDTO.getVat() < Constants.SOAP_AMOUNT_MAX)) {
            LinearLayout creditInfo = (LinearLayout) findViewById(R.id.creditInfo);
            creditInfo.setVisibility(View.VISIBLE);
            cardNumberValue.setText(persianEnglishDigit.E2P(pspInfo.getCardDTO().getMaskedCardNumber()));
            bankName.setText(pspInfo.getCardDTO().getBankName());
        } else {

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
