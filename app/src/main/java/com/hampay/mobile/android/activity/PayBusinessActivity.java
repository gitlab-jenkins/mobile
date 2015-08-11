package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.dto.UserVerificationStatus;
import com.hampay.common.core.model.request.BusinessPaymentConfirmRequest;
import com.hampay.common.core.model.request.IndividualPaymentConfirmRequest;
import com.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestBusinessPaymentConfirm;
import com.hampay.mobile.android.async.RequestIndividualPaymentConfirm;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.edittext.CurrencyFormatter;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;

public class PayBusinessActivity extends ActionBarActivity {


    Bundle bundle;

    FacedTextView business_name_code;

    ButtonRectangle pay_to_business_button;

    FacedEditText credit_value;
    boolean creditValueValidation = false;
    ImageView credit_value_icon;

    FacedEditText contact_message;

    Context context;
    Activity activity;

    RelativeLayout loading_rl;

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

    public void backActionBar(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_business);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        context = this;
        activity = PayBusinessActivity.this;

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();


        MaxXferAmount = prefs.getLong(Constants.MAX_XFER_Amount, 0);
        MinXferAmount = prefs.getLong(Constants.MIN_XFER_Amount, 0);

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
        business_name_code.setText(bundle.getString("business_name") + "(" + bundle.getString("business_code") + ")");

        contact_message = (FacedEditText)findViewById(R.id.contact_message);

        credit_value = (FacedEditText)findViewById(R.id.credit_value);
        credit_value.addTextChangedListener(new CurrencyFormatter(credit_value));
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
                if (creditValueValidation) {

                    amountValue = Long.parseLong(credit_value.getText().toString().replace(",", ""));
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
                                businessPaymentConfirmRequest = new BusinessPaymentConfirmRequest();
                                businessPaymentConfirmRequest.setBusinessCode(bundle.getString("business_code"));
                                requestBusinessPaymentConfirm = new RequestBusinessPaymentConfirm(context, new RequestBusinessPaymentConfirmTaskCompleteListener());
                                requestBusinessPaymentConfirm.execute(businessPaymentConfirmRequest);
                                break;

                            default:
                                new HamPayDialog(activity).showFailPaymentPermissionDialog(userVerificationMessage);
                                break;
                        }
                        }else {
                            new HamPayDialog(activity).showIncorrectAmountDialog(MinXferAmount, MaxXferAmount);
                        }
                    }

                }else {
                    (new HamPayDialog(activity)).showIncorrectPrice();
                }
            }
        });

    }


    public class RequestBusinessPaymentConfirmTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessPaymentConfirmResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<BusinessPaymentConfirmResponse> businessPaymentConfirmResponseMessage) {

            if (businessPaymentConfirmResponseMessage != null){
                if (businessPaymentConfirmResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    new HamPayDialog(activity).businessPaymentConfirmDialog(businessPaymentConfirmResponseMessage.getService(), amountValue, businessMssage);
                }else {
                    new HamPayDialog(activity).showFailPaymentDialog(businessPaymentConfirmResponseMessage.getService().getResultStatus().getCode(),
                            businessPaymentConfirmResponseMessage.getService().getResultStatus().getDescription());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog("2000",
                        activity.getString(R.string.msg_fail_payment));
            }
        }

        @Override
        public void onTaskPreRun() { }
    }

}
