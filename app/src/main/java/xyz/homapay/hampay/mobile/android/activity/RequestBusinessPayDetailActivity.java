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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.UserVerificationStatus;
import xyz.homapay.hampay.common.core.model.request.GetUserIdTokenRequest;
import xyz.homapay.hampay.common.core.model.request.IndividualPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.response.GetUserIdTokenResponse;
import xyz.homapay.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import xyz.homapay.hampay.common.psp.model.request.PurchaseRequest;
import xyz.homapay.hampay.common.psp.model.response.PurchaseResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestIndividualPaymentConfirm;
import xyz.homapay.hampay.mobile.android.async.RequestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestUserIdToken;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class RequestBusinessPayDetailActivity extends AppCompatActivity {

    ButtonRectangle pay_to_business_button;

    Bundle bundle;

    private String contactPhoneNo;
    private String contactName;

    FacedTextView contact_name;
    FacedEditText contact_message;
    String contactMssage = "";
    FacedTextView credit_value;
    Long amountValue;
    boolean creditValueValidation = false;
    ImageView credit_value_icon;

    String number = "";

    boolean intentContact = false;

    Context context;
    Activity activity;


    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    String serverKey = "";

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

    GetUserIdTokenRequest getUserIdTokenRequest;
    RequestUserIdToken requestUserIdToken;

    FacedEditText pin2Value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requets_business_pay_detail);

        context = this;
        activity = RequestBusinessPayDetailActivity.this;


        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        try {

            MaxXferAmount = prefs.getLong(Constants.MAX_XFER_Amount, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_XFER_Amount, 0);

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

        credit_value = (FacedTextView)findViewById(R.id.credit_value);
//        credit_value.addTextChangedListener(new CurrencyFormatterTextWatcher(credit_value));
        credit_value_icon = (ImageView)findViewById(R.id.credit_value_icon);
//        credit_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus){
//                    if (credit_value.getText().toString().length() == 0){
//                        credit_value_icon.setImageResource(R.drawable.false_icon);
//                        creditValueValidation = false;
//                    }
//                    else {
//                        credit_value_icon.setImageResource(R.drawable.right_icon);
//                        creditValueValidation = true;
//                    }
//                }else {
//                    credit_value_icon.setImageDrawable(null);
//                }
//
//            }
//        });

        contact_message = (FacedEditText)findViewById(R.id.contact_message);
        contact_name = (FacedTextView)findViewById(R.id.contact_name);

        pin2Value = (FacedEditText)findViewById(R.id.pin2Value);


        bundle = getIntent().getExtras();

        if (bundle != null) {
            contactPhoneNo = bundle.getString(Constants.CONTACT_PHONE_NO);
            contactName = bundle.getString(Constants.CONTACT_NAME);

        }else {

//            intentContact = true;
//
//            Uri uri = getIntent().getData();
//
//            Cursor phonesCursor = getContentResolver().query(uri, null, null, null,
//                    ContactsContract.CommonDataKinds.Phone.IS_PRIMARY + " DESC");
//            if (phonesCursor != null) {
//                if (phonesCursor.moveToNext()) {
//                    String id = phonesCursor.getString(phonesCursor
//                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//                    Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
//                    while (pCur.moveToNext()) {
//                        contactPhoneNo = pCur.getString(pCur
//                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        contactName = pCur.getString(pCur
//                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                        if (TextUtils.isEmpty(contactPhoneNo)) continue;
//                        if (!number.equals("")) number = number + "&";
//                    }
//                    pCur.close();
//                }
//                phonesCursor.close();
//
//                Log.e("URL", contactPhoneNo);
//
//            }
        }

//        contact_name.setText(contactName);


        pay_to_business_button = (ButtonRectangle)findViewById(R.id.pay_to_business_button);
        pay_to_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestPurchase requestPurchase = new RequestPurchase(activity, new RequestPurchaseTaskCompleteListener());
                PurchaseRequest purchaseRequest = new PurchaseRequest();
                purchaseRequest.setAmount(0);
                purchaseRequest.setMerchantId("");
                purchaseRequest.setMobileNumber(Constants.REGISTERED_CELL_NUMBER);
                purchaseRequest.setPin2(pin2Value.getText().toString());
                requestPurchase.execute(purchaseRequest);
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


    public class RequestPurchaseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PurchaseResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<PurchaseResponse> purchaseResponseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (purchaseResponseResponseMessage != null){
                if (purchaseResponseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    new HamPayDialog(activity).purchaseDialog();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Success")
                            .build());

                }else {
                    new HamPayDialog(activity).showFailPaymentDialog(purchaseResponseResponseMessage.getService().getServiceDefinition().getCode(),
                            purchaseResponseResponseMessage.getService().getMessage());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog(Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pending Payment Request")
                        .setAction("Payment")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

            pay_to_business_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() {}
    }

}
