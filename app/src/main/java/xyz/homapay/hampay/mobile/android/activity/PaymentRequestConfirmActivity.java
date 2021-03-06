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

import butterknife.BindView;
import butterknife.ButterKnife;
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
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.img.ImageHelper;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PaymentRequestConfirmActivity extends AppCompatActivity {

    @BindView(R.id.payment_request_button)
    FacedTextView payment_request_button;
    PersianEnglishDigit persianEnglishDigit;
    @BindView(R.id.contact_name)
    FacedTextView contact_name;
    @BindView(R.id.cell_number)
    FacedTextView cell_number;
    @BindView(R.id.contact_message)
    FacedTextView contact_message;
    String contactMssage = "";
    @BindView(R.id.amount_value)
    FacedTextView amount_value;
    @BindView(R.id.fee_value)
    FacedTextView fee_value;
    boolean intentContact = false;
    Context context;
    Activity activity;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    RequestUserPayment requestUserPayment;
    UserPaymentRequest userPaymentRequest;
    HamPayDialog hamPayDialog;
    @BindView(R.id.user_image)
    ImageView user_image;
    private ContactDTO hamPayContact;
    private PaymentInfoDTO paymentInfo;
    private String displayName;
    private String cellNumber;
    private String imageId;
    private long contactAmount = 0;
    private long amountValue = 0;
    private long calcFeeCharge = 0;
    private long MaxXferAmount = 0;
    private long MinXferAmount = 0;
    private long calculatedVat = 0;
    private CurrencyFormatter formatter;

    public void backActionBar(View view) {
        finish();
    }

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
        ButterKnife.bind(this);

        context = this;
        activity = PaymentRequestConfirmActivity.this;

        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        try {
            MaxXferAmount = prefs.getLong(Constants.MAX_INDIVIDUAL_XFER_AMOUNT, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_INDIVIDUAL_XFER_AMOUNT, 0);

        } catch (Exception ex) {
            Log.e("Error", ex.getStackTrace().toString());
        }
        hamPayDialog = new HamPayDialog(activity);
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
        fee_value.setText(persianEnglishDigit.E2P(formatter.format(calcFeeCharge)));
        String message = intent.getStringExtra(Constants.CONTACT_MESSAGE);
        if (message != null && !message.equals(""))
            contact_message.setText(message);
        else
            contact_message.setVisibility(View.GONE);

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

            if (hamPayContact != null) {
                if (hamPayContact.getContactImageId() != null) {
                    imageId = hamPayContact.getContactImageId();
                }
            }
            if (paymentInfo != null) {
                if (paymentInfo.getImageId() != null) {
                    imageId = paymentInfo.getImageId();
                }
            }

            if (imageId != null) {
                AppManager.setMobileTimeout(context);
                editor.commit();
                user_image.setTag(imageId);
                ImageHelper.getInstance(activity).imageLoader(imageId, user_image, R.drawable.user_placeholder);
            } else {
                user_image.setImageResource(R.drawable.user_placeholder);
            }
        } else {
        }

        payment_request_button.setOnClickListener(v -> {
            amount_value.clearFocus();
            if (amount_value.getText().toString().length() == 0) {
                Toast.makeText(activity, getString(R.string.msg_null_amount), Toast.LENGTH_SHORT).show();
                return;
            }

            contactMssage = contact_message.getText().toString();
            contactMssage = contactMssage.replaceAll(Constants.ENTER_CHARACTERS_REGEX, " ");
            AppManager.setMobileTimeout(context);
            editor.commit();
            if (amount_value.getText().toString().indexOf("٬") != -1) {
                amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace("٬", "")));
            } else if (amount_value.getText().toString().indexOf(",") != -1) {
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

    public class RequestUserPaymentTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<UserPaymentResponse>> {


        @Override
        public void onTaskComplete(ResponseMessage<UserPaymentResponse> userPaymentResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

            if (userPaymentResponseMessage != null) {

                if (userPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.USER_PAYMENT_SUCCESS;
                    new HamPayDialog(activity).successPaymentRequestDialog(userPaymentResponseMessage.getService().getProductCode());
                } else if (userPaymentResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.USER_PAYMENT_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.USER_PAYMENT_FAILURE;
                    new HamPayDialog(activity).failurePaymentRequestDialog(userPaymentResponseMessage.getService().getResultStatus().getCode(),
                            userPaymentResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                serviceName = ServiceEvent.USER_PAYMENT_FAILURE;
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
}
