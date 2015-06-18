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
import com.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import com.hampay.common.core.model.response.IndividualPaymentResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.webservice.WebServices;

public class PayOneActivity extends ActionBarActivity {

    FacedTextView contact_name;
    CardView pay_to_one;
    EditText credit_value;

    Dialog dialog_pay_one;


    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_one);

        bundle = getIntent().getExtras();

        credit_value = (EditText)findViewById(R.id.credit_value);

        contact_name = (FacedTextView)findViewById(R.id.contact_name);
        contact_name.setText(bundle.getString("contact_name"));

        pay_to_one = (CardView)findViewById(R.id.pay_to_one);
        pay_to_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpConfirmIndividualPayment().execute(bundle.getString("contact_phone_no"));
            }
        });


    }

    ResponseMessage<IndividualPaymentConfirmResponse> individualPaymentConfirmResponse;


    public class HttpConfirmIndividualPayment extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            individualPaymentConfirmResponse = webServices.individualPaymentConfirm(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (individualPaymentConfirmResponse != null) {

                Rect displayRectangle = new Rect();
                Activity parent = (Activity) PayOneActivity.this;
                Window window = parent.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                View view = getLayoutInflater().inflate(R.layout.dialog_pay_one, null);

                FacedTextView pay_one_confirm = (FacedTextView) view.findViewById(R.id.pay_one_confirm);
                FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
                FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);

                confirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_pay_one.dismiss();
                        new HttpIndividualPayment().execute(bundle.getString("contact_phone_no"));
                    }
                });


                dis_confirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_pay_one.dismiss();
                    }
                });

                pay_one_confirm.setText(getString(R.string.pay_one_confirm, credit_value.getText().toString(),
                        individualPaymentConfirmResponse.getService().getFullName(),
                        individualPaymentConfirmResponse.getService().getBankName()));


                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
                dialog_pay_one = new Dialog(PayOneActivity.this);
                dialog_pay_one.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_pay_one.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_pay_one.setContentView(view);
                dialog_pay_one.setTitle(null);
                dialog_pay_one.setCanceledOnTouchOutside(false);

                dialog_pay_one.show();
            }


        }
    }

    ResponseMessage<IndividualPaymentResponse> individualPaymentResponse;

    public class HttpIndividualPayment  extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            individualPaymentResponse = webServices.individualPayment(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (individualPaymentResponse != null) {

                Rect displayRectangle = new Rect();
                Activity parent = (Activity) PayOneActivity.this;
                Window window = parent.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                View view = getLayoutInflater().inflate(R.layout.dialog_pay_one_ref, null);

                FacedTextView pay_one_confirm_ref = (FacedTextView) view.findViewById(R.id.pay_one_confirm_ref);
                FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);


                confirmation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_pay_one.dismiss();
                    }
                });

                pay_one_confirm_ref.setText(getString(R.string.pay_one_ref, individualPaymentResponse.getService().getRefCode()));


                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
                dialog_pay_one = new Dialog(PayOneActivity.this);
                dialog_pay_one.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_pay_one.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_pay_one.setContentView(view);
                dialog_pay_one.setTitle(null);
                dialog_pay_one.setCanceledOnTouchOutside(false);

                dialog_pay_one.show();
            }


        }
    }

}
