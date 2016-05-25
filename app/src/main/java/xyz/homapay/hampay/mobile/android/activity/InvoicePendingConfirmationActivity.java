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
import xyz.homapay.hampay.common.core.model.request.LatestPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.response.LatestPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPayment;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestPurchase;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
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

    Long MaxXferAmount = 0L;
    Long MinXferAmount = 0L;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;

    PaymentInfoDTO paymentInfoDTO = null;
    PspInfoDTO pspInfoDTO = null;

    private RequestPurchase requestPurchase;
    private DoWorkInfo doWorkInfo;

    PersianEnglishDigit persianEnglishDigit;

    RequestPSPResult requestPSPResult;
    PSPResultRequest pspResultRequest;

    RequestLatestPayment requestLatestPayment;
    LatestPaymentRequest latestPaymentRequest;

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
        setContentView(R.layout.activity_invoice_payment_pending);

        context = this;
        activity = InvoicePendingConfirmationActivity.this;
        dbHelper = new DatabaseHelper(context);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        try {
            MaxXferAmount = prefs.getLong(Constants.MAX_INDIVIDUAL_XFER_AMOUNT, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_INDIVIDUAL_XFER_AMOUNT, 0);

        } catch (Exception ex) {
            Log.e("Error", ex.getStackTrace().toString());
        }

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

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

        if (paymentInfoDTO != null) {
            callerName.setText(paymentInfoDTO.getCallerName());
            paymentCode.setText(persianEnglishDigit.E2P("کد فاکتور " + paymentInfoDTO.getProductCode()));
            received_message.setText(paymentInfoDTO.getMessage().trim());
            create_date.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(paymentInfoDTO.getCreatedBy())));
            paymentPriceValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfoDTO.getAmount())));
            paymentVAT.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfoDTO.getVat())));
            paymentFeeValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfoDTO.getFeeCharge())));
            paymentTotalValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfoDTO.getAmount() + paymentInfoDTO.getVat() + paymentInfoDTO.getFeeCharge())));
            cardNumberValue.setText(persianEnglishDigit.E2P(pspInfoDTO.getCardDTO().getMaskedCardNumber()));
            bankName.setText(pspInfoDTO.getCardDTO().getBankName());
            if (paymentInfoDTO.getImageId() != null) {
                new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(user_image)).execute(Constants.IMAGE_PREFIX + authToken + "/" + paymentInfoDTO.getImageId());
            }else {
            }

            if (pspInfoDTO.getCardDTO().getCardId() != null) {
                LinearLayout creditInfo = (LinearLayout) findViewById(R.id.creditInfo);
                creditInfo.setVisibility(View.VISIBLE);
            } else {
                cardNumberValue.setText(persianEnglishDigit.E2P(pspInfoDTO.getCardDTO().getMaskedCardNumber()));
            }
        } else {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            requestLatestPayment = new RequestLatestPayment(activity, new RequestLatestPaymentTaskCompleteListener());
            latestPaymentRequest = new LatestPaymentRequest();
            requestLatestPayment.execute(latestPaymentRequest);
        }


//        else {
//
//            intentContact = true;
//
//            Uri uri = getIntent().getData();
//
//            Cursor phonesCursor = getContentResolver().query(uri, null, null, null,
//                    ContactsContract.CommonDataKinds.Phone.IS_PRIMARY + " DESC");
//            if (phonesCursor != null) {
//                if (phonesCursor.moveToNext()) {
//                    String id = phonesCursor.getString(phonesCursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//                    Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
//                    while (pCur.moveToNext()) {
//                        contactPhoneNo = pCur.getString(pCur
//                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        contactName = pCur.getString(pCur
//                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                        if (TextUtils.isEmpty(contactPhoneNo)) continue;
//                        if (!number.equals("")) number = number + "&";
////                        contactPhoneNo = PhoneNumberUtils.stripSeparators(contactPhoneNo);
//
//                        //number = number + searchReplaceNumber(getApplicationContext(), n);
//                    }
//                    pCur.close();
//                }
//                phonesCursor.close();
//
//                Log.e("URL", contactPhoneNo);
//
//            }
//        }

        pay_button = (ImageView) findViewById(R.id.pay_button);
        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pspInfoDTO.getCardDTO().getCardId() == null) {
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
                    doWorkInfo.setCellNumber(prefs.getString(Constants.REGISTERED_CELL_NUMBER, "").substring(1, prefs.getString(Constants.REGISTERED_CELL_NUMBER, "").length()));
                    doWorkInfo.setLangAByte((byte) 0);
                    doWorkInfo.setLangABoolean(false);
                    TWAArrayOfKeyValueOfstringstring vectorstring2stringMapEntry = new TWAArrayOfKeyValueOfstringstring();
                    TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();

                    s2sMapEntry.Key = "Amount";
                    s2sMapEntry.Value = (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge()) + "";
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
                    s2sMapEntry.Value = pspInfoDTO.getTerminalID();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "CardId";
                    s2sMapEntry.Value = pspInfoDTO.getCardDTO().getCardId();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 46) {
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
                    }
                }else {
                    new HamPayDialog(activity).pspFailResultDialog();
                }
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                pspResultRequest.setPspResponseCode(responseCode);
                pspResultRequest.setProductCode(paymentInfoDTO.getProductCode());
                pspResultRequest.setTrackingCode(SWTraceNum);
                requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(), 2);
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

                new HamPayDialog(activity).pspFailResultDialog();

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pending Payment Request")
                        .setAction("Payment")
                        .setLabel("Fail(Server)")
                        .build());
            }

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

            if (pspResultResponseMessage != null) {
                if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Success")
                            .build());

                } else {

//                    new HamPayDialog(activity).showFailPaymentDialog(pspResultResponseMessage.getService().getResultStatus().getCode(),
//                            pspResultResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Fail(Server)")
                            .build());
                }
            } else {
//                new HamPayDialog(activity).showFailPaymentDialog(Constants.LOCAL_ERROR_CODE,
//                        getString(R.string.msg_fail_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pending Payment Request")
                        .setAction("Payment")
                        .setLabel("Fail(Mobile)")
                        .build());
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

            hamPayDialog.dismisWaitingDialog();

            if (latestPaymentResponseMessage != null) {
                if (latestPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    paymentInfoDTO = latestPaymentResponseMessage.getService().getPaymentInfoDTO();
                    pspInfoDTO = latestPaymentResponseMessage.getService().getPspInfo();
                    callerName.setText(paymentInfoDTO.getCallerName());
                    paymentCode.setText(persianEnglishDigit.E2P("کد فاکتور " + paymentInfoDTO.getProductCode()));
                    create_date.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(paymentInfoDTO.getCreatedBy())));
                    received_message.setText(paymentInfoDTO.getMessage().trim());
                    paymentPriceValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfoDTO.getAmount())));
                    paymentVAT.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfoDTO.getVat())));
                    paymentFeeValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfoDTO.getFeeCharge())));
                    paymentTotalValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfoDTO.getAmount() + paymentInfoDTO.getVat() + paymentInfoDTO.getFeeCharge())));
                    cardNumberValue.setText(persianEnglishDigit.E2P(pspInfoDTO.getCardDTO().getMaskedCardNumber()));
                    bankName.setText(pspInfoDTO.getCardDTO().getBankName());
                    if (paymentInfoDTO.getImageId() != null) {
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(user_image)).execute(Constants.IMAGE_PREFIX + authToken + "/" + paymentInfoDTO.getImageId());
                    }
                    if (pspInfoDTO.getCardDTO().getCardId() != null) {
                        LinearLayout creditInfo = (LinearLayout) findViewById(R.id.creditInfo);
                        creditInfo.setVisibility(View.VISIBLE);
                    } else {
                        cardNumberValue.setText(persianEnglishDigit.E2P(pspInfoDTO.getCardDTO().getMaskedCardNumber()));
                    }
                    dbHelper.createViewedPaymentRequest(paymentInfoDTO.getProductCode());
                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Latest Pending Payment")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());

                } else {
                    requestLatestPayment = new RequestLatestPayment(context, new RequestLatestPaymentTaskCompleteListener());

                    new HamPayDialog(activity).showFailPendingPaymentDialog(requestLatestPayment, latestPaymentRequest,
                            latestPaymentResponseMessage.getService().getServiceDefinition().getCode(),
                            latestPaymentResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Latest Pending Payment")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }
                else
                {
                    requestLatestPayment = new RequestLatestPayment(context, new RequestLatestPaymentTaskCompleteListener());

                    new HamPayDialog(activity).showFailPendingPaymentDialog(requestLatestPayment, latestPaymentRequest,
                            Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_fail_fetch_latest_payment));

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Latest Pending Payment")
                            .setAction("Fetch")
                            .setLabel("Fail(Mobile)")
                            .build());
                }
        }
        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }

    }
}
