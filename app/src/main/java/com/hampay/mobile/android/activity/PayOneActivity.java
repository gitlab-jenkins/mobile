package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.dto.UserVerificationStatus;
import com.hampay.common.core.model.request.IndividualPaymentConfirmRequest;
import com.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import com.hampay.mobile.android.HamPayApplication;
import com.hampay.mobile.android.Helper.DatabaseHelper;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestIndividualPaymentConfirm;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.edittext.CurrencyFormatter;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.AESHelper;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.util.PersianEnglishDigit;
import com.hampay.mobile.android.util.SecurityUtils;

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

    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    RequestIndividualPaymentConfirm requestIndividualPaymentConfirm;
    IndividualPaymentConfirmRequest individualPaymentConfirmRequest;

    public void backActionBar(View view){
        finish();
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_one);

        context = this;
        activity = PayOneActivity.this;


        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        deviceInfo = new DeviceInfo(context);

        try {

            mobileKey = SecurityUtils.getInstance(context).generateSHA_256(
                    deviceInfo.getMacAddress(),
                    deviceInfo.getIMEI(),
                    deviceInfo.getAndroidId());
            serverKey = prefs.getString(Constants.USER_ID_TOKEN, "");
            decryptedData = AESHelper.decrypt(mobileKey, serverKey, prefs.getString(Constants.MAX_XFER_Amount, "0"));
            MaxXferAmount = Long.parseLong(decryptedData);
            decryptedData = AESHelper.decrypt(mobileKey, serverKey, prefs.getString(Constants.MIN_XFER_Amount, "0"));
            MinXferAmount = Long.parseLong(decryptedData);

        }catch (Exception ex){
            Log.e("Error", ex.getStackTrace().toString());
        }

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



        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

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

                pay_to_one_button.setEnabled(false);

                if (creditValueValidation) {
                    contactMssage = contact_message.getText().toString();
                    amountValue = Long.parseLong(new PersianEnglishDigit(credit_value.getText().toString()).P2E().replace(",", ""));

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
                                    individualPaymentConfirmRequest = new IndividualPaymentConfirmRequest();
                                    individualPaymentConfirmRequest.setCellNumber(contactPhoneNo);
                                    requestIndividualPaymentConfirm = new RequestIndividualPaymentConfirm(context, new RequestIndividualPaymentConfirmTaskCompleteListener());
                                    requestIndividualPaymentConfirm.execute(individualPaymentConfirmRequest);
                                    break;

                                default:
                                    new HamPayDialog(activity).showFailPaymentPermissionDialog(userVerificationMessage);
                                    pay_to_one_button.setEnabled(true);
                                    break;
                            }
                        }else {
                            new HamPayDialog(activity).showIncorrectAmountDialog(MinXferAmount, MaxXferAmount);
                            pay_to_one_button.setEnabled(true);
                        }
                    }
                }else {
                    (new HamPayDialog(activity)).showIncorrectPrice();
                    pay_to_one_button.setEnabled(true);
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

    @Override
    public void onBackPressed() {

        if (intentContact){
            Intent i = new Intent();
            i.setClass(this, MainActivity.class);
            startActivity(i);
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", 1024);
        setResult(1024);

        finish();



    }


    public class RequestIndividualPaymentConfirmTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<IndividualPaymentConfirmResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<IndividualPaymentConfirmResponse> individualPaymentConfirmResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (individualPaymentConfirmResponseMessage != null){
                if (individualPaymentConfirmResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    new HamPayDialog(activity).individualPaymentConfirmDialog(individualPaymentConfirmResponseMessage.getService(), amountValue, contactMssage);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Individual Payment Confirm")
                            .setAction("Payment Confirm")
                            .setLabel("Success")
                            .build());

                }else {
                    new HamPayDialog(activity).showFailPaymentDialog(individualPaymentConfirmResponseMessage.getService().getResultStatus().getCode(),
                            individualPaymentConfirmResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Individual Payment Confirm")
                            .setAction("Payment Confirm")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog(Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Individual Payment Confirm")
                        .setAction("Payment Confirm")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

            pay_to_one_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() {}
    }

}
