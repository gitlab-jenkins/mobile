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

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestPurchase;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceName;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.model.SyncPspResult;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.PspCode;
import xyz.homapay.hampay.mobile.android.webservice.newpsp.TWAArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.newpsp.TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring;

public class BusinessPaymentConfirmActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ImageView pay_to_business_button;
    private boolean intentContact = false;
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private CurrencyFormatter formatter;

    public void backActionBar(View view){
        finish();
    }


    HamPayDialog hamPayDialog;
    FacedTextView business_name;
    ImageView business_image;

    FacedTextView paymentPriceValue;
    FacedTextView paymentFeeValue;
    FacedTextView paymentVAT;
    FacedTextView paymentTotalValue;
    FacedTextView cardNumberValue;
    FacedTextView bankName;
    FacedEditText pin2Value;

    PaymentInfoDTO paymentInfoDTO = null;
    PspInfoDTO pspInfoDTO = null;

    private RequestPurchase requestPurchase;
    private DoWorkInfo doWorkInfo;

    PersianEnglishDigit persianEnglishDigit;
    RequestPSPResult requestPSPResult;
    PSPResultRequest pspResultRequest;
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
        setContentView(R.layout.activity_business_payment_confirm);

        context = this;
        activity = BusinessPaymentConfirmActivity.this;

        dbHelper = new DatabaseHelper(context);

        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        imageManager = new ImageManager(activity, 200000, false);

        try {
        }catch (Exception ex){
            Log.e("Error", ex.getStackTrace().toString());
        }

        hamPayDialog = new HamPayDialog(activity);

        business_name = (FacedTextView)findViewById(R.id.business_name);
        business_image = (ImageView)findViewById(R.id.business_image);
        paymentPriceValue = (FacedTextView)findViewById(R.id.paymentPriceValue);
        paymentFeeValue = (FacedTextView)findViewById(R.id.paymentFeeValue);
        paymentVAT = (FacedTextView)findViewById(R.id.paymentVAT);
        paymentTotalValue = (FacedTextView)findViewById(R.id.paymentTotalValue);
        cardNumberValue = (FacedTextView)findViewById(R.id.cardNumberValue);
        bankName = (FacedTextView)findViewById(R.id.bankName);
        pin2Value = (FacedEditText)findViewById(R.id.pin2Value);

        Intent intent = getIntent();

        paymentInfoDTO = (PaymentInfoDTO)intent.getSerializableExtra(Constants.PAYMENT_INFO);
        pspInfoDTO = (PspInfoDTO)intent.getSerializableExtra(Constants.PSP_INFO);


        if (pspInfoDTO.getCardDTO().getCardId() != null && (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge() + paymentInfoDTO.getVat() < Constants.SOAP_AMOUNT_MAX)) {
            LinearLayout creditInfo = (LinearLayout) findViewById(R.id.creditInfo);
            creditInfo.setVisibility(View.VISIBLE);
            cardNumberValue.setText(persianEnglishDigit.E2P(pspInfoDTO.getCardDTO().getMaskedCardNumber()));
            bankName.setText(pspInfoDTO.getCardDTO().getBankName());
        } else {
        }


        if (paymentInfoDTO != null) {
            PersianEnglishDigit persianEnglishDigit = new PersianEnglishDigit();

            business_name.setText(paymentInfoDTO.getCallerName());
            paymentPriceValue.setText(persianEnglishDigit.E2P(String.valueOf(formatter.format(paymentInfoDTO.getAmount()))));
            paymentVAT.setText(persianEnglishDigit.E2P(String.valueOf(formatter.format(paymentInfoDTO.getVat()))));
            paymentFeeValue.setText(persianEnglishDigit.E2P(String.valueOf(formatter.format(paymentInfoDTO.getFeeCharge()))));
            paymentTotalValue.setText(persianEnglishDigit.E2P(String.valueOf(formatter.format(paymentInfoDTO.getAmount() + paymentInfoDTO.getVat() + paymentInfoDTO.getFeeCharge()))));

            if (paymentInfoDTO.getImageId() != null) {
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                business_image.setTag(paymentInfoDTO.getImageId());
                imageManager.displayImage(paymentInfoDTO.getImageId(), business_image, R.drawable.user_placeholder);
            }
            else {
                business_image.setImageResource(R.drawable.user_placeholder);
            }
        }

        pay_to_business_button = (ImageView) findViewById(R.id.pay_to_business_button);
        pay_to_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();

                if (pspInfoDTO.getCardDTO().getCardId() == null || (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge() + paymentInfoDTO.getVat() >= Constants.SOAP_AMOUNT_MAX)) {
                    Intent intent = new Intent();
                    intent.setClass(activity, BankWebPaymentActivity.class);
                    intent.putExtra(Constants.PAYMENT_INFO, paymentInfoDTO);
                    intent.putExtra(Constants.PSP_INFO, pspInfoDTO);
                    startActivityForResult(intent, 46);
                } else {

                    pay_to_business_button.setEnabled(false);

                    if (pin2Value.getText().toString().length() <= 4) {
                        Toast.makeText(context, getString(R.string.msg_pin2_incurrect), Toast.LENGTH_SHORT).show();
                        pay_to_business_button.setEnabled(true);
                        return;
                    }

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
            pay_to_business_button.setEnabled(true);
            ServiceName serviceName;
            LogEvent logEvent = new LogEvent(context);

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
                        if (paymentInfoDTO != null) {
                            Intent intent = new Intent(context, PaymentCompletedActivity.class);
                            intent.putExtra(Constants.SUCCESS_PAYMENT_AMOUNT, paymentInfoDTO.getAmount() + paymentInfoDTO.getVat() + paymentInfoDTO.getFeeCharge());
                            intent.putExtra(Constants.SUCCESS_PAYMENT_CODE, paymentInfoDTO.getProductCode());
                            intent.putExtra(Constants.SUCCESS_PAYMENT_TRACE, pspInfoDTO.getProviderId());
                            startActivityForResult(intent, 46);
                            serviceName = ServiceName.PSP_PAYMENT_SUCCESS;
                            logEvent.log(serviceName);
                        }
                        resultStatus = ResultStatus.SUCCESS;
                    }else if (responseCode.equalsIgnoreCase("51")) {
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, getString(R.string.msg_insufficient_credit));
                        resultStatus = ResultStatus.FAILURE;
                        serviceName = ServiceName.PSP_PAYMENT_FAILURE;
                        logEvent.log(serviceName);
                    }else {
                        PspCode pspCode = new PspCode(context);
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, pspCode.getDescription(responseCode));
                        resultStatus = ResultStatus.FAILURE;
                        serviceName = ServiceName.PSP_PAYMENT_FAILURE;
                        logEvent.log(serviceName);
                    }

                    SyncPspResult syncPspResult = new SyncPspResult();
                    syncPspResult.setResponseCode(responseCode);
                    syncPspResult.setProductCode(paymentInfoDTO.getProductCode());
                    syncPspResult.setType("PURCHASE");
                    syncPspResult.setSwTrace(SWTraceNum);
                    syncPspResult.setTimestamp(System.currentTimeMillis());
                    syncPspResult.setStatus(0);
                    dbHelper.createSyncPspResult(syncPspResult);

                    pspResultRequest.setPspResponseCode(responseCode);
                    pspResultRequest.setProductCode(paymentInfoDTO.getProductCode());
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
        ServiceName serviceName;
        LogEvent logEvent = new LogEvent(context);

        public RequestPSPResultTaskCompleteListener(String SWTrace){
            this.SWTrace = SWTrace;
        }

        @Override
        public void onTaskComplete(ResponseMessage<PSPResultResponse> pspResultResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pspResultResponseMessage != null){
                if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    serviceName = ServiceName.PSP_RESULT_SUCCESS;
                    if (SWTrace != null) {
                        dbHelper.syncPspResult(SWTrace);
                    }

                }else if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                    serviceName = ServiceName.PSP_RESULT_FAILURE;
                }else {
                    serviceName = ServiceName.PSP_RESULT_FAILURE;
                }
            }
            logEvent.log(serviceName);

            pay_to_business_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
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
