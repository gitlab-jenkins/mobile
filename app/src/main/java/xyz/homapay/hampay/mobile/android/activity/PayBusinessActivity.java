package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.UserVerificationStatus;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestBusinessPaymentConfirm;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.CurrencyFormatterTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PayBusinessActivity extends ActionBarActivity {


    Bundle bundle;

    PersianEnglishDigit persianEnglishDigit;

    FacedTextView business_name_code;

    ButtonRectangle pay_to_business_button;

    FacedEditText credit_value;
    boolean creditValueValidation = false;
    ImageView credit_value_icon;

    FacedEditText contact_message;

    Context context;
    Activity activity;

    Long amountValue = 0l;
    String businessMssage = "";

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RequestBusinessPaymentConfirm requestBusinessPaymentConfirm;
    BusinessPaymentConfirmRequest businessPaymentConfirmRequest;

    UserVerificationStatus userVerificationStatus;
    String userVerificationMessage = "";

    Long MaxXferAmount = 0L;
    Long MinXferAmount = 0L;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;

    byte[] mobileKey;
    String serverKey;
    String decryptedData;

    DeviceInfo deviceInfo;

    public void backActionBar(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_business);

        persianEnglishDigit = new PersianEnglishDigit();

        context = this;
        activity = PayBusinessActivity.this;

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();


        deviceInfo = new DeviceInfo(context);

        try {

            //            mobileKey = SecurityUtils.getInstance(context).generateSHA_256(
//                    deviceInfo.getMacAddress(),
//                    deviceInfo.getIMEI(),
//                    deviceInfo.getAndroidId());
//            serverKey = prefs.getString(Constants.USER_ID_TOKEN, "");
//            decryptedData = AESHelper.decrypt(mobileKey, serverKey, prefs.getString(Constants.MAX_XFER_Amount, "0"));
//            MaxXferAmount = Long.parseLong(decryptedData);
//            decryptedData = AESHelper.decrypt(mobileKey, serverKey, prefs.getString(Constants.MIN_XFER_Amount, "0"));
//            MinXferAmount = Long.parseLong(decryptedData);

            MaxXferAmount = prefs.getLong(Constants.MAX_XFER_Amount, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_XFER_Amount, 0);

        }catch (Exception ex){
            Log.e("Error", ex.getStackTrace().toString());
        }

//        MaxXferAmount = prefs.getLong(Constants.MAX_XFER_Amount, 0);
//        MinXferAmount = prefs.getLong(Constants.MIN_XFER_Amount, 0);

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


        bundle = getIntent().getExtras();

        business_name_code = (FacedTextView)findViewById(R.id.business_name_code);
        business_name_code.setText(persianEnglishDigit.E2P(bundle.getString("business_name") + " " + "(" + bundle.getString("business_code") + ")"));

        contact_message = (FacedEditText)findViewById(R.id.contact_message);

        credit_value = (FacedEditText)findViewById(R.id.credit_value);
        credit_value.addTextChangedListener(new CurrencyFormatterTextWatcher(credit_value));
        credit_value_icon = (ImageView)findViewById(R.id.credit_value_icon);
        credit_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if (credit_value.getText().toString().length() == 0){
                        credit_value_icon.setImageResource(R.drawable.false_icon);
                        creditValueValidation = false;
                    }
                    else {
                        credit_value_icon.setImageResource(R.drawable.right_icon);
                        creditValueValidation = true;
                    }
                }else {
                    credit_value_icon.setImageDrawable(null);
                }

            }
        });


        pay_to_business_button = (ButtonRectangle)findViewById(R.id.pay_to_business_button);
        pay_to_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                credit_value.clearFocus();

                pay_to_business_button.setEnabled(false);

                if (creditValueValidation) {

                    amountValue = Long.parseLong(new PersianEnglishDigit(credit_value.getText().toString()).P2E().replace(",", ""));
                    businessMssage = contact_message.getText().toString();

                    if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
                        Intent intent = new Intent();
                        intent.setClass(context, HamPayLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }else {
                        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                        editor.commit();
                        if (amountValue >= MinXferAmount && amountValue <= MaxXferAmount) {
                            switch (userVerificationStatus) {
                                case DELEGATED:
                                    hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                                    businessPaymentConfirmRequest = new BusinessPaymentConfirmRequest();
                                    businessPaymentConfirmRequest.setBusinessCode(bundle.getString("business_code"));
                                    requestBusinessPaymentConfirm = new RequestBusinessPaymentConfirm(context, new RequestBusinessPaymentConfirmTaskCompleteListener());
                                    requestBusinessPaymentConfirm.execute(businessPaymentConfirmRequest);
                                    break;

                                default:
                                    new HamPayDialog(activity).showFailPaymentPermissionDialog(userVerificationMessage);
                                    pay_to_business_button.setEnabled(true);
                                    break;
                            }
                        }else {
                            new HamPayDialog(activity).showIncorrectAmountDialog(MinXferAmount, MaxXferAmount);
                            pay_to_business_button.setEnabled(true);
                        }
                    }

                }else {
                    (new HamPayDialog(activity)).showIncorrectPrice();
                    pay_to_business_button.setEnabled(true);
                }
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


    public class RequestBusinessPaymentConfirmTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessPaymentConfirmResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<BusinessPaymentConfirmResponse> businessPaymentConfirmResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (businessPaymentConfirmResponseMessage != null){
                if (businessPaymentConfirmResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    new HamPayDialog(activity).businessPaymentConfirmDialog(businessPaymentConfirmResponseMessage.getService(), amountValue, businessMssage);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business Payment Confirm")
                            .setAction("Payment Confirm")
                            .setLabel("Success")
                            .build());
                }else {
                    new HamPayDialog(activity).showFailPaymentDialog(businessPaymentConfirmResponseMessage.getService().getResultStatus().getCode(),
                            businessPaymentConfirmResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business Payment Confirm")
                            .setAction("Payment Confirm")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog(Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_fail_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Business Payment Confirm")
                        .setAction("Payment Confirm")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
            pay_to_business_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() { }
    }

}
