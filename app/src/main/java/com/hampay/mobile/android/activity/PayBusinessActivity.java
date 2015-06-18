package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import com.hampay.common.core.model.response.BusinessPaymentResponse;
import com.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import com.hampay.common.core.model.response.IndividualPaymentResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.webservice.WebServices;

public class PayBusinessActivity extends ActionBarActivity {


    Bundle bundle;

    FacedTextView business_name;
    FacedTextView business_code;

    CardView pay_to_business;
    
    Dialog dialog_pay_business;
    
    EditText credit_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_business);

        bundle = getIntent().getExtras();

        business_name = (FacedTextView)findViewById(R.id.business_name);
        business_code = (FacedTextView)findViewById(R.id.business_code);
        credit_value = (EditText)findViewById(R.id.credit_value);

        business_name.setText(bundle.getString("business_name"));
        business_code.setText(bundle.getString("business_code"));

        pay_to_business = (CardView)findViewById(R.id.pay_to_business);
        pay_to_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpConfirmBusinessPayment().execute(bundle.getString("business_code"));
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
