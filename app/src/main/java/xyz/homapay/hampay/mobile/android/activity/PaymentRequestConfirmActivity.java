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
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.UserPaymentRequest;
import xyz.homapay.hampay.common.core.model.response.UserPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestUserPayment;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceName;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PaymentRequestConfirmActivity extends AppCompatActivity {


    private ContactDTO hamPayContact;
    private PaymentInfoDTO paymentInfo;
    private String displayName;
    private String cellNumber;
    private String imageId;
    private long contactAmount = 0;

    FacedTextView payment_request_button;

    PersianEnglishDigit persianEnglishDigit;

    private ImageView user_image;
    FacedTextView contact_name;
    FacedTextView cell_number;
    FacedTextView contact_message;
    String contactMssage = "";
    private long amountValue = 0;
    private long calcFeeCharge = 0;
    FacedTextView amount_value;
    FacedTextView fee_value;
    FacedTextView vat_value;

    boolean intentContact = false;

    Context context;
    Activity activity;


    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RequestUserPayment requestUserPayment;
    UserPaymentRequest userPaymentRequest;

    public void backActionBar(View view) {
        finish();
    }

    private long MaxXferAmount = 0;
    private long MinXferAmount = 0;

    HamPayDialog hamPayDialog;
    private String authToken;
    private ImageManager imageManager;


    private long calculatedVat = 0;
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
        setContentView(R.layout.activity_payment_request_confirm);

        context = this;
        activity = PaymentRequestConfirmActivity.this;

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

        hamPayDialog = new HamPayDialog(activity);

        amount_value = (FacedTextView) findViewById(R.id.amount_value);
        fee_value = (FacedTextView) findViewById(R.id.fee_value);

        vat_value = (FacedTextView)findViewById(R.id.vat_value);
        amount_total = (FacedTextView)findViewById(R.id.amount_total);
        contact_message = (FacedTextView) findViewById(R.id.contact_message);
        contact_name = (FacedTextView) findViewById(R.id.contact_name);
        cell_number = (FacedTextView) findViewById(R.id.cell_number);
        user_image = (ImageView) findViewById(R.id.user_image);

        Intent intent = getIntent();


        hamPayContact = (ContactDTO) intent.getSerializableExtra(Constants.HAMPAY_CONTACT);
        paymentInfo = (PaymentInfoDTO) intent.getSerializableExtra(Constants.PAYMENT_INFO);
        displayName = intent.getStringExtra(Constants.CONTACT_NAME);
        cellNumber = intent.getStringExtra(Constants.CONTACT_PHONE_NO);
        imageId = intent.getStringExtra(Constants.IMAGE_ID);
        contactAmount = intent.getLongExtra(Constants.CONTACT_AMOUNT, 0);
        calculatedVat = intent.getLongExtra(Constants.CONTACT_VAT, 0);
        calcFeeCharge = intent.getLongExtra(Constants.CONTACT_FEE, 0);
        amount_value.setText(persianEnglishDigit.E2P(formatter.format(contactAmount)));
        vat_value.setText(persianEnglishDigit.E2P(formatter.format(calculatedVat)));
        fee_value.setText(persianEnglishDigit.E2P(formatter.format(calcFeeCharge)));
        amount_total.setText(persianEnglishDigit.E2P(formatter.format(contactAmount + calculatedVat + calcFeeCharge)));
        contact_message.setText(intent.getStringExtra(Constants.CONTACT_MESSAGE));

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

                contactMssage = contact_message.getText().toString();
                contactMssage = contactMssage.replaceAll(Constants.ENTER_CHARACTERS_REGEX, " ");
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                if (amount_value.getText().toString().indexOf("٬") != -1){
                    amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace("٬", "")));
                }else if (amount_value.getText().toString().indexOf(",") != -1){
                    amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace(",", "")));
                }
                if (amountValue + calculatedVat >= MinXferAmount && amountValue + calculatedVat <= MaxXferAmount) {
                    userPaymentRequest = new UserPaymentRequest();
                    userPaymentRequest.setCalleeCellNumber(cellNumber);
                    userPaymentRequest.setAmount(amountValue);
                    userPaymentRequest.setVat(calculatedVat);
                    userPaymentRequest.setMessage(contactMssage);
                    requestUserPayment = new RequestUserPayment(context, new RequestUserPaymentTaskCompleteListener());
                    requestUserPayment.execute(userPaymentRequest);
                } else {
                    new HamPayDialog(activity).showIncorrectAmountDialog(MinXferAmount, MaxXferAmount);
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
        finish();
    }

    public class RequestUserPaymentTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<UserPaymentResponse>> {



        @Override
        public void onTaskComplete(ResponseMessage<UserPaymentResponse> userPaymentResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ServiceName serviceName;
            LogEvent logEvent = new LogEvent(context);

            if (userPaymentResponseMessage != null) {

                if (userPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceName.USER_PAYMENT_SUCCESS;
                    new HamPayDialog(activity).successPaymentRequestDialog(userPaymentResponseMessage.getService().getProductCode());
                }else if (userPaymentResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceName.USER_PAYMENT_FAILURE;
                    forceLogout();
                }
                else {
                    serviceName = ServiceName.USER_PAYMENT_FAILURE;
                    new HamPayDialog(activity).failurePaymentRequestDialog(userPaymentResponseMessage.getService().getResultStatus().getCode(),
                            userPaymentResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                serviceName = ServiceName.USER_PAYMENT_FAILURE;
                new HamPayDialog(activity).failurePaymentRequestDialog(Constants.LOCAL_ERROR_CODE, getString(R.string.msg_failure_payment_request));
            }
            logEvent.log(serviceName);

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
