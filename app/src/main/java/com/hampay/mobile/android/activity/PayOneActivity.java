package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.IndividualPaymentConfirmRequest;
import com.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import com.hampay.mobile.android.Helper.DatabaseHelper;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestIndividualPaymentConfirm;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.edittext.CurrencyFormatter;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.fragment.PayToOneFragment;
import com.hampay.mobile.android.util.Constants;

public class PayOneActivity extends ActionBarActivity {

    ButtonRectangle pay_to_one_button;

    Bundle bundle;

    DatabaseHelper dbHelper;

    private String contactPhoneNo;
    private String contactName;

    FacedTextView contact_name;
    FacedEditText contact_message;
    String contactMssage = "";
    FacedEditText credit_value;
    Long amountValue;
    boolean creditValueValidation = false;
    ImageView credit_value_icon;

    String number = "";

    boolean intentContact = false;

    Context context;
    Activity activity;

    RelativeLayout loading_rl;

    RequestIndividualPaymentConfirm requestIndividualPaymentConfirm;
    IndividualPaymentConfirmRequest individualPaymentConfirmRequest;

    public void backActionBar(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_one);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

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
                    contactMssage = contact_message.getText().toString();
                    amountValue = Long.parseLong(credit_value.getText().toString().replace(",", ""));

                    individualPaymentConfirmRequest = new IndividualPaymentConfirmRequest();
                    individualPaymentConfirmRequest.setCellNumber(contactPhoneNo);

                    requestIndividualPaymentConfirm = new RequestIndividualPaymentConfirm(context, new RequestIndividualPaymentConfirmTaskCompleteListener());
                    requestIndividualPaymentConfirm.execute(individualPaymentConfirmRequest);

                }else {
                    (new HamPayDialog(activity)).showIncorrectPrice();
                }

            }
        });


    }


    @Override
    public void onBackPressed() {

        PayToOneFragment.updatePayments();

        if (intentContact){
            Intent i = new Intent();
            i.setClass(this, MainActivity.class);
            startActivity(i);
        }

        finish();

    }


    public class RequestIndividualPaymentConfirmTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<IndividualPaymentConfirmResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<IndividualPaymentConfirmResponse> individualPaymentConfirmResponseMessage) {

            if (individualPaymentConfirmResponseMessage != null){
                if (individualPaymentConfirmResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    new HamPayDialog(activity).individualPaymentConfirmDialog(individualPaymentConfirmResponseMessage.getService(), amountValue, contactMssage);
                }else {
                    new HamPayDialog(activity).showFailPaymentDialog(individualPaymentConfirmResponseMessage.getService().getResultStatus().getCode(),
                            individualPaymentConfirmResponseMessage.getService().getResultStatus().getDescription());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog("2000",
                        getString(R.string.msg_fail_payment));
            }
        }

        @Override
        public void onTaskPreRun() { }
    }

}
