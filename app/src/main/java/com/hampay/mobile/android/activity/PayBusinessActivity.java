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
    
    Dialog dialog_pay_business;

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
                        businessPaymentConfirmRequest = new BusinessPaymentConfirmRequest();
                        businessPaymentConfirmRequest.setBusinessCode(bundle.getString("business_code"));
                        requestBusinessPaymentConfirm = new RequestBusinessPaymentConfirm(context, new RequestBusinessPaymentConfirmTaskCompleteListener());
                        requestBusinessPaymentConfirm.execute(businessPaymentConfirmRequest);
                    }

                }else {
                    (new HamPayDialog(activity)).showIncorrectPrice();
                }
            }
        });

    }


//    ResponseMessage<BusinessPaymentConfirmResponse> businessPaymentConfirmResponse;
//
//
//    public class HttpConfirmBusinessPayment extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            WebServices webServices = new WebServices(getApplicationContext());
//            businessPaymentConfirmResponse = webServices.businessPaymentConfirm(params[0]);
//
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            loading_rl.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//            loading_rl.setVisibility(View.GONE);
//
//            if (businessPaymentConfirmResponse != null) {
//
//                Rect displayRectangle = new Rect();
//                Activity parent = (Activity) PayBusinessActivity.this;
//                Window window = parent.getWindow();
//                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//                View view = getLayoutInflater().inflate(R.layout.dialog_pay_one, null);
//
//                FacedTextView pay_one_confirm = (FacedTextView) view.findViewById(R.id.pay_one_confirm);
//                FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
//                FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);
//
//                confirmation.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog_pay_business.dismiss();
//                        new HttpBusinessPayment().execute(bundle.getString("contact_phone_no"));
//                    }
//                });
//
//
//                dis_confirmation.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog_pay_business.dismiss();
//                    }
//                });
//
//                pay_one_confirm.setText(getString(R.string.pay_one_confirm, credit_value.getText().toString(),
//                        businessPaymentConfirmResponse.getService().getFullName(),
//                        businessPaymentConfirmResponse.getService().getBankName()));
//
//
//                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
//                dialog_pay_business = new Dialog(PayBusinessActivity.this);
//                dialog_pay_business.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog_pay_business.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog_pay_business.setContentView(view);
//                dialog_pay_business.setTitle(null);
//                dialog_pay_business.setCanceledOnTouchOutside(false);
//
//                dialog_pay_business.show();
//            }
//
//
//        }
//    }
//
//
//
//    ResponseMessage<BusinessPaymentResponse> businessPaymentResponse;
//
//    public class HttpBusinessPayment  extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            WebServices webServices = new WebServices(getApplicationContext());
//            businessPaymentResponse = webServices.businessPayment(params[0]);
//
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            loading_rl.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//            loading_rl.setVisibility(View.GONE);
//
//            if (businessPaymentResponse != null) {
//
//                Rect displayRectangle = new Rect();
//                Activity parent = (Activity) PayBusinessActivity.this;
//                Window window = parent.getWindow();
//                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//                View view = getLayoutInflater().inflate(R.layout.dialog_pay_one_ref, null);
//
//                FacedTextView pay_one_confirm_ref = (FacedTextView) view.findViewById(R.id.pay_one_confirm_ref);
//                FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
//
//
//                confirmation.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        finish();
//                        dialog_pay_business.dismiss();
//                    }
//                });
//
//                pay_one_confirm_ref.setText(getString(R.string.pay_one_ref, businessPaymentResponse.getService().getRefCode()));
//
//
//                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
//                dialog_pay_business = new Dialog(PayBusinessActivity.this);
//                dialog_pay_business.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog_pay_business.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog_pay_business.setContentView(view);
//                dialog_pay_business.setTitle(null);
//                dialog_pay_business.setCanceledOnTouchOutside(false);
//
//                dialog_pay_business.show();
//            }
//
//
//        }
//    }

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
