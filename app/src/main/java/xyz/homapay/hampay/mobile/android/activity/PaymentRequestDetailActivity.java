package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.CalcFeeChargeRequest;
import xyz.homapay.hampay.common.core.model.request.CalculateVatRequest;
import xyz.homapay.hampay.common.core.model.request.UserPaymentRequest;
import xyz.homapay.hampay.common.core.model.response.CalcFeeChargeResponse;
import xyz.homapay.hampay.common.core.model.response.CalculateVatResponse;
import xyz.homapay.hampay.common.core.model.response.UserPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCalcFeeCharge;
import xyz.homapay.hampay.mobile.android.async.RequestCalculateVat;
import xyz.homapay.hampay.mobile.android.async.RequestUserPayment;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.CurrencyFormatterTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PaymentRequestDetailActivity extends AppCompatActivity {


    private ContactDTO hamPayContact;
    private PaymentInfoDTO paymentInfo;
    private String displayName;
    private String cellNumber;
    private String imageId;

    FacedTextView payment_request_button;

    PersianEnglishDigit persianEnglishDigit;

    private ImageView user_image;
    FacedTextView contact_name;
    FacedTextView cell_number;
    FacedEditText contact_message;
    String contactMssage = "";
    private long amountValue = 0;
    private long calcFeeCharge = 0;
    FacedEditText amount_value;
    FacedTextView vat_value;
    boolean creditValueValidation = false;

    String number = "";

    boolean intentContact = false;

    Context context;
    Activity activity;


    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RequestCalcFeeCharge requestCalcFeeCharge;
    CalcFeeChargeRequest calcFeeChargeRequest;

    RequestUserPayment requestUserPayment;
    UserPaymentRequest userPaymentRequest;

    public void backActionBar(View view) {
        finish();
    }

    private long MaxXferAmount = 0;
    private long MinXferAmount = 0;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;
    private String authToken;
    private ImageManager imageManager;


    private LinearLayout add_vat;
    private long calculatedVat = 0;
    private ImageView vat_icon;
    private FacedTextView amount_total;
    private CurrencyFormatter formatter;

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
        setContentView(R.layout.activity_payment_request_detail);

        context = this;
        activity = PaymentRequestDetailActivity.this;

        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
        imageManager = new ImageManager(activity, 200000, false);

        try {
            MaxXferAmount = prefs.getLong(Constants.MAX_INDIVIDUAL_XFER_AMOUNT, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_INDIVIDUAL_XFER_AMOUNT, 0);

        } catch (Exception ex) {
            Log.e("Error", ex.getStackTrace().toString());
        }

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        amount_value = (FacedEditText) findViewById(R.id.amount_value);
        amount_value.addTextChangedListener(new CurrencyFormatterTextWatcher(amount_value));
        amount_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                vat_icon.setImageResource(R.drawable.add_vat);
                vat_value.setText("۰");
                calculatedVat = 0;
                amount_total.setText(amount_value.getText().toString());
            }
        });
        amount_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (amount_value.getText().toString().length() == 0) {
                        creditValueValidation = false;
                    } else {
                        creditValueValidation = true;
                    }
                }

            }
        });

        vat_value = (FacedTextView)findViewById(R.id.vat_value);
        vat_icon = (ImageView)findViewById(R.id.vat_icon);
        amount_total = (FacedTextView)findViewById(R.id.amount_total);
        contact_message = (FacedEditText) findViewById(R.id.contact_message);
        contact_name = (FacedTextView) findViewById(R.id.contact_name);
        cell_number = (FacedTextView) findViewById(R.id.cell_number);
        user_image = (ImageView) findViewById(R.id.user_image);


        add_vat = (LinearLayout) findViewById(R.id.add_vat);
        add_vat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount_value.getText().toString().length() > 0) {
                    if (amount_value.getText().toString().indexOf("٬") != -1){
                        amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace("٬", "")));
                    }else if (amount_value.getText().toString().indexOf(",") != -1){
                        amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace(",", "")));
                    }else {
                        amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString()));
                    }
                    if (calculatedVat == 0){
                        CalculateVatRequest calculateVatRequest = new CalculateVatRequest();
                        calculateVatRequest.setAmount(amountValue);
                        RequestCalculateVat requestCalculateVat = new RequestCalculateVat(activity, new RequestCalculateVatTaskCompleteListener());
                        requestCalculateVat.execute(calculateVatRequest);
                    }else {
                        vat_icon.setImageResource(R.drawable.add_vat);
                        vat_value.setText("۰");
                        calculatedVat = 0;
                        amount_total.setText(persianEnglishDigit.E2P(formatter.format(amountValue)));
                    }
                }
            }
        });

        Intent intent = getIntent();


        hamPayContact = (ContactDTO) intent.getSerializableExtra(Constants.HAMPAY_CONTACT);
        paymentInfo = (PaymentInfoDTO) intent.getSerializableExtra(Constants.PAYMENT_INFO);
        displayName = intent.getStringExtra(Constants.CONTACT_NAME);
        cellNumber = intent.getStringExtra(Constants.CONTACT_PHONE_NO);
        imageId = intent.getStringExtra(Constants.IMAGE_ID);

        if (hamPayContact != null) {
            displayName = hamPayContact.getDisplayName();
            cellNumber = hamPayContact.getCellNumber();
            imageId = hamPayContact.getContactImageId();
        } else if (paymentInfo != null) {
            displayName = paymentInfo.getCalleeName();
            cellNumber = paymentInfo.getCalleePhoneNumber();
            imageId = paymentInfo.getImageId();
        }


        if (hamPayContact != null || paymentInfo != null || displayName != null) {
            contact_name.setText(displayName);
            cell_number.setText(persianEnglishDigit.E2P(cellNumber));

            if (hamPayContact != null){
                if (hamPayContact.getContactImageId() != null){
                    imageId = hamPayContact.getContactImageId();
                }
            }
            if (paymentInfo != null){
                if (paymentInfo.getImageId() != null){
                    imageId = paymentInfo.getImageId();
                }
            }

            if (imageId != null) {
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                user_image.setTag(imageId);
                imageManager.displayImage(imageId, user_image, R.drawable.user_placeholder);
            }else {
                user_image.setImageResource(R.drawable.user_placeholder);
            }
        } else {
        }


        payment_request_button = (FacedTextView) findViewById(R.id.payment_request_button);
        payment_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_value.clearFocus();
                if (amount_value.getText().toString().length() == 0){
                    Toast.makeText(activity, getString(R.string.msg_null_amount), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (creditValueValidation) {
                    contactMssage = contact_message.getText().toString();
                    contactMssage = contactMssage.replaceAll(Constants.ENTER_CHARACTERS_REGEX, " ");
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();
                    if (amount_value.getText().toString().indexOf("٬") != -1){
                        amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace("٬", "")));
                    }else if (amount_value.getText().toString().indexOf(",") != -1){
                        amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace(",", "")));
                    }else {
                        amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString()));
                    }
                    if (amountValue + calculatedVat >= MinXferAmount && amountValue + calculatedVat <= MaxXferAmount) {
                        Intent intent = new Intent(PaymentRequestDetailActivity.this, PaymentRequestConfirmActivity.class);
                        intent.putExtra(Constants.CONTACT_NAME, displayName);
                        intent.putExtra(Constants.CONTACT_PHONE_NO, cellNumber);
                        intent.putExtra(Constants.IMAGE_ID, imageId);
                        intent.putExtra(Constants.CONTACT_AMOUNT, amountValue);
                        intent.putExtra(Constants.CONTACT_VAT, calculatedVat);
//                        intent.putExtra(Constants.CONTACT_FEE, calcFeeCharge);
                        intent.putExtra(Constants.CONTACT_MESSAGE, contact_message.getText().toString());
                        startActivityForResult(intent, 10);

//                        calcFeeChargeRequest = new CalcFeeChargeRequest();
//                        calcFeeChargeRequest.setAmount(amountValue);
//                        requestCalcFeeCharge = new RequestCalcFeeCharge(activity, new RequestCalculateFeeTaskCompleteListener());
//                        requestCalcFeeCharge.execute(calcFeeChargeRequest);
                    } else {
                        new HamPayDialog(activity).showIncorrectAmountDialog(MinXferAmount, MaxXferAmount);
                    }
                }
            }
        });
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

    public class RequestCalculateVatTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<CalculateVatResponse>> {
        public RequestCalculateVatTaskCompleteListener() {
        }

        @Override
        public void onTaskComplete(ResponseMessage<CalculateVatResponse> calculateVatResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ResultStatus resultStatus;
            if (calculateVatResponseMessage != null) {
                resultStatus = calculateVatResponseMessage.getService().getResultStatus();
                if (resultStatus == ResultStatus.SUCCESS) {
                    vat_value.setText(persianEnglishDigit.E2P(formatter.format(calculateVatResponseMessage.getService().getAmount())));
                    calculatedVat = calculateVatResponseMessage.getService().getAmount();
                    amount_total.setText(persianEnglishDigit.E2P(formatter.format(calculatedVat + amountValue)));
                    vat_icon.setImageResource(R.drawable.remove_vat);
                }else if (calculateVatResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

//    public class RequestCalculateFeeTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<CalcFeeChargeResponse>> {
//        public RequestCalculateFeeTaskCompleteListener() {
//        }
//
//        @Override
//        public void onTaskComplete(ResponseMessage<CalcFeeChargeResponse> calcFeeChargeResponseMessage) {
//
//            hamPayDialog.dismisWaitingDialog();
//            ResultStatus resultStatus;
//            if (calcFeeChargeResponseMessage != null) {
//                resultStatus = calcFeeChargeResponseMessage.getService().getResultStatus();
//                if (resultStatus == ResultStatus.SUCCESS) {
//                    calcFeeCharge = calcFeeChargeResponseMessage.getService().getFeeCharge();
//                    Intent intent = new Intent(PaymentRequestDetailActivity.this, PaymentRequestConfirmActivity.class);
//                    intent.putExtra(Constants.CONTACT_NAME, displayName);
//                    intent.putExtra(Constants.CONTACT_PHONE_NO, cellNumber);
//                    intent.putExtra(Constants.IMAGE_ID, imageId);
//                    intent.putExtra(Constants.CONTACT_AMOUNT, amountValue);
//                    intent.putExtra(Constants.CONTACT_VAT, calculatedVat);
//                    intent.putExtra(Constants.CONTACT_FEE, calcFeeCharge);
//                    intent.putExtra(Constants.CONTACT_MESSAGE, contact_message.getText().toString());
//                    startActivityForResult(intent, 10);
//                }else if (calcFeeChargeResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
//                    forceLogout();
//                }else {
//                    new HamPayDialog(activity).failurePaymentRequestDialog(calcFeeChargeResponseMessage.getService().getResultStatus().getCode(),
//                            calcFeeChargeResponseMessage.getService().getResultStatus().getDescription());
//                }
//            }else {
//                new HamPayDialog(activity).failurePaymentRequestDialog(Constants.LOCAL_ERROR_CODE,
//                        getString(R.string.msg_fail_illegal_app_list));
//            }
//        }
//
//        @Override
//        public void onTaskPreRun() {
//            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0) {
                    finish();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    public class RequestUserPaymentTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<UserPaymentResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<UserPaymentResponse> userPaymentResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (userPaymentResponseMessage != null) {

                if (userPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    new HamPayDialog(activity).successPaymentRequestDialog(userPaymentResponseMessage.getService().getProductCode());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Individual Payment Confirm")
                            .setAction("Payment Confirm")
                            .setLabel("Success")
                            .build());

                }else if (userPaymentResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
                else {
                    new HamPayDialog(activity).failurePaymentRequestDialog(userPaymentResponseMessage.getService().getResultStatus().getCode(),
                            userPaymentResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Individual Payment Confirm")
                            .setAction("Payment Confirm")
                            .setLabel("Fail(Server)")
                            .build());
                }
            } else {
                new HamPayDialog(activity).failurePaymentRequestDialog(Constants.LOCAL_ERROR_CODE, getString(R.string.msg_failure_payment_request));
                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Individual Payment Confirm")
                        .setAction("Payment Confirm")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

            payment_request_button.setEnabled(true);
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
