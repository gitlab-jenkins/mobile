package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import com.hampay.common.core.model.response.BusinessPaymentResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.edittext.CurrencyFormatter;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.webservice.WebServices;

public class PayBusinessActivity extends ActionBarActivity {


    Bundle bundle;

    FacedTextView business_name_code;

    ButtonRectangle pay_to_business_button;
    
    Dialog dialog_pay_business;

    FacedEditText credit_value;
    boolean creditValueValidation = false;
    ImageView credit_value_icon;
    Context context;
    Activity activity;

    public void backActionBar(View view){
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_business);

        context = this;
        activity = PayBusinessActivity.this;

        bundle = getIntent().getExtras();

        business_name_code = (FacedTextView)findViewById(R.id.business_name_code);
        business_name_code.setText(bundle.getString("business_name") + "(" + bundle.getString("business_code") + ")");


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
                    new HttpConfirmBusinessPayment().execute(bundle.getString("business_code"));
                }else {
                    (new HamPayDialog(activity)).showIncorrectPrice();
                }
            }
        });

    }


    ResponseMessage<BusinessPaymentConfirmResponse> businessPaymentConfirmResponse;


    public class HttpConfirmBusinessPayment extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            businessPaymentConfirmResponse = webServices.businessPaymentConfirm(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (businessPaymentConfirmResponse != null) {

                Rect displayRectangle = new Rect();
                Activity parent = (Activity) PayBusinessActivity.this;
                Window window = parent.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                View view = getLayoutInflater().inflate(R.layout.dialog_pay_one, null);

                FacedTextView pay_one_confirm = (FacedTextView) view.findViewById(R.id.pay_one_confirm);
                FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
                FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);

                confirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_pay_business.dismiss();
                        new HttpBusinessPayment().execute(bundle.getString("contact_phone_no"));
                    }
                });


                dis_confirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_pay_business.dismiss();
                    }
                });

                pay_one_confirm.setText(getString(R.string.pay_one_confirm, credit_value.getText().toString(),
                        businessPaymentConfirmResponse.getService().getFullName(),
                        businessPaymentConfirmResponse.getService().getBankName()));


                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
                dialog_pay_business = new Dialog(PayBusinessActivity.this);
                dialog_pay_business.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_pay_business.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_pay_business.setContentView(view);
                dialog_pay_business.setTitle(null);
                dialog_pay_business.setCanceledOnTouchOutside(false);

                dialog_pay_business.show();
            }


        }
    }



    ResponseMessage<BusinessPaymentResponse> businessPaymentResponse;

    public class HttpBusinessPayment  extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            businessPaymentResponse = webServices.businessPayment(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (businessPaymentResponse != null) {

                Rect displayRectangle = new Rect();
                Activity parent = (Activity) PayBusinessActivity.this;
                Window window = parent.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                View view = getLayoutInflater().inflate(R.layout.dialog_pay_one_ref, null);

                FacedTextView pay_one_confirm_ref = (FacedTextView) view.findViewById(R.id.pay_one_confirm_ref);
                FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);


                confirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        dialog_pay_business.dismiss();
                    }
                });

                pay_one_confirm_ref.setText(getString(R.string.pay_one_ref, businessPaymentResponse.getService().getRefCode()));


                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
                dialog_pay_business = new Dialog(PayBusinessActivity.this);
                dialog_pay_business.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_pay_business.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_pay_business.setContentView(view);
                dialog_pay_business.setTitle(null);
                dialog_pay_business.setCanceledOnTouchOutside(false);

                dialog_pay_business.show();
            }


        }
    }

}
