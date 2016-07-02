package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.CalculateVatRequest;
import xyz.homapay.hampay.common.core.model.request.UserPaymentRequest;
import xyz.homapay.hampay.common.core.model.response.CalculateVatResponse;
import xyz.homapay.hampay.common.core.model.response.UserPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCalculateVat;
import xyz.homapay.hampay.mobile.android.async.RequestUserPayment;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.CurrencyFormatterTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PaymentRequestDetailActivity extends AppCompatActivity {


    private ContactDTO hamPayContact;
    private PaymentInfoDTO paymentInfo;
    private String calleeName;
    private String calleeCellNumber;
    private String displayName;
    private String cellNumber;
    private String imageId;

    FacedTextView payment_request_button;

    PersianEnglishDigit persianEnglishDigit;

    private String contactPhoneNo;
    private String contactName;

    private ImageView user_image;
    FacedTextView contact_name;
    FacedTextView cell_number;
    FacedEditText contact_message;
    String contactMssage = "";
    Long amountValue = 0L;
    FacedEditText amount_value;
    FacedTextView vat_value;
    boolean creditValueValidation = false;

    String number = "";

    boolean intentContact = false;

    Context context;
    Activity activity;


    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RequestUserPayment requestUserPayment;
    UserPaymentRequest userPaymentRequest;

    public void backActionBar(View view) {
        finish();
    }

    Long MaxXferAmount = 0L;
    Long MinXferAmount = 0L;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;
    private String authToken;
    private ImageManager imageManager;


    private LinearLayout add_vat;
    private Long calculatedVat = 0L;
    private ImageView vat_icon;
    private FacedTextView amount_total;
    private CurrencyFormatter formatter;

    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HamPayApplication.setAppSate(AppState.Resumed);
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_request_detail);

        context = this;
        activity = PaymentRequestDetailActivity.this;

        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
        imageManager = new ImageManager(activity, 200000, false);

        try {
            MaxXferAmount = prefs.getLong(Constants.MAX_INDIVIDUAL_XFER_AMOUNT, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_INDIVIDUAL_XFER_AMOUNT, 0);

        } catch (Exception ex) {
            Log.e("Error", ex.getStackTrace().toString());
        }

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        amount_value = (FacedEditText) findViewById(R.id.amount_value);
        amount_value.addTextChangedListener(new CurrencyFormatterTextWatcher(amount_value));
        amount_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                vat_icon.setImageResource(R.drawable.add_vat);
                vat_value.setText("۰");
                calculatedVat = 0L;
                amount_total.setText(amount_value.getText().toString());
            }
        });
        amount_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (amount_value.getText().toString().length() == 0) {
//                        credit_value_icon.setImageResource(R.drawable.false_icon);
                        creditValueValidation = false;
                    } else {
//                        credit_value_icon.setImageResource(R.drawable.right_icon);
                        creditValueValidation = true;
                    }
                } else {
//                    credit_value_icon.setImageDrawable(null);
                }

            }
        });

        vat_value = (FacedTextView)findViewById(R.id.vat_value);
        vat_icon = (ImageView)findViewById(R.id.vat_icon);
        amount_total = (FacedTextView)findViewById(R.id.amount_total);
        contact_message = (FacedEditText) findViewById(R.id.contact_message);
        contact_name = (FacedTextView) findViewById(R.id.contact_name);
        cell_number = (FacedTextView) findViewById(R.id.cell_number);
        user_image = (ImageView) findViewById(R.id.user_image);


        add_vat = (LinearLayout) findViewById(R.id.add_vat);
        add_vat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amount_value.getText().toString().length() > 0) {
                    amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace(",", "")));
                    if (calculatedVat == 0){
                        CalculateVatRequest calculateVatRequest = new CalculateVatRequest();
                        amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace(",", "")));
                        calculateVatRequest.setAmount(amountValue);
                        RequestCalculateVat requestCalculateVat = new RequestCalculateVat(activity, new RequestCalculateVatTaskCompleteListener());
                        requestCalculateVat.execute(calculateVatRequest);
                    }else {
                        vat_icon.setImageResource(R.drawable.add_vat);
                        vat_value.setText("۰");
                        calculatedVat = 0L;
                        amount_total.setText(persianEnglishDigit.E2P(formatter.format(amountValue)));
                    }
                }
            }
        });

        Intent intent = getIntent();


        hamPayContact = (ContactDTO) intent.getSerializableExtra(Constants.HAMPAY_CONTACT);
        paymentInfo = (PaymentInfoDTO) intent.getSerializableExtra(Constants.PAYMENT_INFO);
        displayName = intent.getStringExtra(Constants.CONTACT_NAME);
        cellNumber = intent.getStringExtra(Constants.CONTACT_PHONE_NO);
        imageId = intent.getStringExtra(Constants.IMAGE_ID);

        if (hamPayContact != null) {
            displayName = hamPayContact.getDisplayName();
            cellNumber = hamPayContact.getCellNumber();
            imageId = hamPayContact.getContactImageId();
        } else if (paymentInfo != null) {
            displayName = paymentInfo.getCalleeName();
            cellNumber = paymentInfo.getCalleePhoneNumber();
            imageId = paymentInfo.getImageId();
        }


        if (hamPayContact != null || paymentInfo != null || displayName != null) {
            contact_name.setText(displayName);
            cell_number.setText(persianEnglishDigit.E2P(cellNumber));

            if (hamPayContact != null){
                if (hamPayContact.getContactImageId() != null){
                    imageId = hamPayContact.getContactImageId();
                }
            }
            if (paymentInfo != null){
                if (paymentInfo.getImageId() != null){
                    imageId = paymentInfo.getImageId();
                }
            }

            if (imageId != null) {
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + imageId;
                user_image.setTag(userImageUrl.split("/")[6]);
                imageManager.displayImage(userImageUrl, user_image, R.drawable.user_placeholder);
            }else {
                user_image.setImageResource(R.drawable.user_placeholder);
            }
        } else {

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
                    }
                    pCur.close();
                }
                phonesCursor.close();

                Log.e("URL", contactPhoneNo);

            }
        }


        payment_request_button = (FacedTextView) findViewById(R.id.payment_request_button);
        payment_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_value.clearFocus();
                if (amount_value.getText().toString().length() == 0){
                    Toast.makeText(activity, getString(R.string.msg_null_amount), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (creditValueValidation) {

                    contactMssage = contact_message.getText().toString();
                    contactMssage = contactMssage.replaceAll(Constants.ENTER_CHARACTERS_REGEX, " ");
                    amountValue = Long.parseLong(new PersianEnglishDigit(amount_value.getText().toString()).P2E().replace(",", ""));
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();
                    if (amountValue + calculatedVat >= MinXferAmount && amountValue + calculatedVat <= MaxXferAmount) {
                        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                        userPaymentRequest = new UserPaymentRequest();
                        userPaymentRequest.setCalleeCellNumber(cellNumber);
                        userPaymentRequest.setAmount(amountValue);
                        userPaymentRequest.setVat(calculatedVat);
                        userPaymentRequest.setMessage(contactMssage);
                        requestUserPayment = new RequestUserPayment(context, new RequestUserPaymentTaskCompleteListener());
                        requestUserPayment.execute(userPaymentRequest);
                    } else {
                        new HamPayDialog(activity).showIncorrectAmountDialog(MinXferAmount, MaxXferAmount);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (intentContact) {
            Intent i = new Intent();
            i.setClass(this, MainActivity.class);
            startActivity(i);
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", 1024);
        setResult(1024);
        finish();
    }

    public class RequestUserPaymentTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<UserPaymentResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<UserPaymentResponse> userPaymentResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (userPaymentResponseMessage != null) {



                if (userPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    new HamPayDialog(activity).successPaymentRequestDialog(userPaymentResponseMessage.getService().getProductCode());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Individual Payment Confirm")
                            .setAction("Payment Confirm")
                            .setLabel("Success")
                            .build());

                }else if (userPaymentResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
                else {
                    new HamPayDialog(activity).failurePaymentRequestDialog(userPaymentResponseMessage.getService().getResultStatus().getCode(),
                            userPaymentResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Individual Payment Confirm")
                            .setAction("Payment Confirm")
                            .setLabel("Fail(Server)")
                            .build());
                }
            } else {
                new HamPayDialog(activity).failurePaymentRequestDialog(Constants.LOCAL_ERROR_CODE, getString(R.string.msg_failure_payment_request));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Individual Payment Confirm")
                        .setAction("Payment Confirm")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

            payment_request_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() {
        }
    }

    public class RequestCalculateVatTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<CalculateVatResponse>> {
        public RequestCalculateVatTaskCompleteListener() {
        }

        @Override
        public void onTaskComplete(ResponseMessage<CalculateVatResponse> calculateVatResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ResultStatus resultStatus;
            if (calculateVatResponseMessage != null) {
                resultStatus = calculateVatResponseMessage.getService().getResultStatus();
                if (resultStatus == ResultStatus.SUCCESS) {
                    vat_value.setText(persianEnglishDigit.E2P(formatter.format(calculateVatResponseMessage.getService().getAmount())));
                    calculatedVat = calculateVatResponseMessage.getService().getAmount();
                    amount_total.setText(persianEnglishDigit.E2P(formatter.format(calculatedVat + amountValue)));
                    vat_icon.setImageResource(R.drawable.remove_vat);
                }else if (calculateVatResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(context, HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (activity != null) {
            finish();
            startActivity(intent);
        }
    }
}
