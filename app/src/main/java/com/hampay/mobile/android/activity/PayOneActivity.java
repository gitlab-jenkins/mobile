package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import com.hampay.common.core.model.response.IndividualPaymentResponse;
import com.hampay.mobile.android.Helper.DatabaseHelper;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.edittext.CurrencyFormatter;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.model.RecentPay;
import com.hampay.mobile.android.util.Constant;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.webservice.WebServices;

public class PayOneActivity extends ActionBarActivity {

    ButtonRectangle pay_to_one_button;

    Dialog dialog_pay_one;
    Bundle bundle;


    DatabaseHelper dbHelper;

    RecentPay recentPay;

    private String contactPhoneNo;
    private String contactName;
    private String contactMesage;

    FacedTextView contact_name;
    FacedEditText contact_message;
    FacedEditText credit_value;
    boolean creditValueValidation = false;
    ImageView credit_value_icon;

    String number = "";

    boolean intentContact = false;

    Context context;
    Activity activity;

    public void backActionBar(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_one);

        context = this;
        activity = PayOneActivity.this;

        dbHelper = new DatabaseHelper(this);

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



        contact_message = (FacedEditText)findViewById(R.id.contact_message);
        contact_name = (FacedTextView)findViewById(R.id.contact_name);


        bundle = getIntent().getExtras();

        if (bundle != null) {
            contactPhoneNo = bundle.getString(Constants.CONTACT_PHONE_NO);
            contactName = bundle.getString(Constants.CONTACT_NAME);

        }else {

            intentContact = true;

            Uri uri = getIntent().getData();

            Cursor phonesCursor = getContentResolver().query(uri, null, null, null,
                    ContactsContract.CommonDataKinds.Phone.IS_PRIMARY + " DESC");
            if (phonesCursor != null) {
                if (phonesCursor.moveToNext()) {
                    String id = phonesCursor.getString(phonesCursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        contactPhoneNo = pCur.getString(pCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactName = pCur.getString(pCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        if (TextUtils.isEmpty(contactPhoneNo)) continue;
                        if (!number.equals("")) number = number + "&";
//                        contactPhoneNo = PhoneNumberUtils.stripSeparators(contactPhoneNo);

                        //number = number + searchReplaceNumber(getApplicationContext(), n);
                    }
                    pCur.close();
                }
                phonesCursor.close();

                Log.e("URL", contactPhoneNo);

            }
        }

        contact_name.setText(contactName);


        pay_to_one_button = (ButtonRectangle)findViewById(R.id.pay_to_one_button);
        pay_to_one_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                credit_value.clearFocus();

                if (creditValueValidation) {
                    new HttpConfirmIndividualPayment().execute(contactPhoneNo);
                    contactName = contact_name.getText().toString();
                    contactMesage = contact_message.getText().toString();
                }else {
                    (new HamPayDialog(activity)).showIncorrectPrice();
                }

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
//                        new HttpIndividualPayment().execute(bundle.getString("contact_phone_no"));
                        new HttpIndividualPayment().execute(contactPhoneNo);
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
                        finish();
                        onBackPressed();
                    }
                });

                pay_one_confirm_ref.setText(getString(R.string.pay_one_ref, individualPaymentResponse.getService().getRefCode()));

                recentPay = new RecentPay();


                if (!dbHelper.getExistRecentPay(contactPhoneNo)) {

                    recentPay = new RecentPay();
                    recentPay.setName(contactName);
                    recentPay.setPhone(contactPhoneNo);
                    recentPay.setMessage(contactMesage);
                    dbHelper.createRecentPAy(recentPay);

                }else {
                    recentPay = new RecentPay();
                    recentPay = dbHelper.getRecentPay(contactPhoneNo);

                    recentPay.setMessage(contactMesage);
                    dbHelper.updateRecentPay(recentPay);
                }




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

    @Override
    public void onBackPressed() {

        if (intentContact){
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
        }

        finish();

    }


}
