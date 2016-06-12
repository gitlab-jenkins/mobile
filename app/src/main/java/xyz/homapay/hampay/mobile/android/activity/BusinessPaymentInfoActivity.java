package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.request.CalculateVatRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import xyz.homapay.hampay.common.core.model.response.CalculateVatResponse;
import xyz.homapay.hampay.common.core.model.response.dto.BusinessDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestBusinessPaymentConfirm;
import xyz.homapay.hampay.mobile.android.async.RequestCalculateVat;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.CurrencyFormatterTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class BusinessPaymentInfoActivity extends AppCompatActivity {


    PersianEnglishDigit persianEnglishDigit;

    FacedTextView business_name;
    FacedTextView business_hampay_id;
    ImageView business_image;

    ImageView payment_button;

    FacedEditText amount_value;
    boolean creditValueValidation = false;

    Context context;
    Activity activity;

    Long amountValue = 0L;
    Long totalValue = 0L;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private RequestBusinessPaymentConfirm requestBusinessPaymentConfirm;
    private BusinessPaymentConfirmRequest businessPaymentConfirmRequest;

    Long MaxXferAmount = 0L;
    Long MinXferAmount = 0L;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;

    private BusinessDTO businessDTO;

    private LinearLayout add_vat;
    private Long calculatedVat = 0L;
    private ImageView vat_icon;
    private FacedTextView amount_total;
    private CurrencyFormatter formatter;
    private FacedTextView vat_value;

    private ImageManager imageManager;
    private String authToken;

    public void backActionBar(View view){
        finish();
    }


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
        setContentView(R.layout.activity_business_payment_info);

        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
        context = this;
        activity = BusinessPaymentInfoActivity.this;

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
        imageManager = new ImageManager(activity, 200000, false);

        try {
            MaxXferAmount = prefs.getLong(Constants.MAX_BUSINESS_XFER_AMOUNT, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_BUSINESS_XFER_AMOUNT, 0);

        }catch (Exception ex){
            Log.e("Error", ex.getStackTrace().toString());
        }

        Intent intent = getIntent();
        businessDTO = (BusinessDTO)intent.getSerializableExtra(Constants.BUSINESS_INFO);

        business_name = (FacedTextView)findViewById(R.id.business_name);
        business_name.setText(persianEnglishDigit.E2P(businessDTO.getTitle() + " " + "(" + businessDTO.getCode() + ")"));
        business_image = (ImageView)findViewById(R.id.business_image);

        business_hampay_id = (FacedTextView)findViewById(R.id.business_hampay_id);
        business_hampay_id.setText("شناسه: " + persianEnglishDigit.E2P(businessDTO.getCode()));

        if (businessDTO.getBusinessImageId() != null) {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + businessDTO.getBusinessImageId();
            business_image.setTag(userImageUrl.split("/")[6]);
            imageManager.displayImage(userImageUrl, business_image, R.drawable.user_placeholder);
        }else {
            business_image.setImageResource(R.drawable.user_placeholder);
        }


        amount_value = (FacedEditText)findViewById(R.id.amount_value);
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
                if (!hasFocus){
                    if (amount_value.getText().toString().length() == 0){
                        creditValueValidation = false;
                    }
                    else {
                        creditValueValidation = true;
                    }
                }else {
                }

            }
        });

        vat_value = (FacedTextView)findViewById(R.id.vat_value);
        vat_icon = (ImageView)findViewById(R.id.vat_icon);
        amount_total = (FacedTextView)findViewById(R.id.amount_total);
        add_vat = (LinearLayout) findViewById(R.id.add_vat);
        add_vat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
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

        payment_button = (ImageView)findViewById(R.id.payment_button);
        payment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_value.clearFocus();
                if (amount_value.getText().toString().length() == 0){
                    Toast.makeText(activity, getString(R.string.msg_null_amount), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (creditValueValidation) {
                    amountValue = Long.parseLong(new PersianEnglishDigit(amount_value.getText().toString()).P2E().replace(",", ""));
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();
                    if (amountValue + calculatedVat >= MinXferAmount && amountValue + calculatedVat <= MaxXferAmount) {
                        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                        businessPaymentConfirmRequest = new BusinessPaymentConfirmRequest();
                        businessPaymentConfirmRequest.setAmount(calculatedVat + amountValue);
                        businessPaymentConfirmRequest.setBusinessCode(businessDTO.getCode());
                        requestBusinessPaymentConfirm = new RequestBusinessPaymentConfirm(context, new RequestBusinessPaymentConfirmTaskCompleteListener());
                        requestBusinessPaymentConfirm.execute(businessPaymentConfirmRequest);
                    }else {
                        new HamPayDialog(activity).showIncorrectAmountDialog(MinXferAmount, MaxXferAmount);

                    }
                }
            }
        });

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        editor.putString(Constants.USER_ID_TOKEN, "");
        editor.commit();
    }


    public class RequestBusinessPaymentConfirmTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessPaymentConfirmResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<BusinessPaymentConfirmResponse> businessPaymentConfirmResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (businessPaymentConfirmResponseMessage != null){
                if (businessPaymentConfirmResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    PaymentInfoDTO paymentInfo = businessPaymentConfirmResponseMessage.getService().getPaymentInfo();
                    PspInfoDTO pspInfo = businessPaymentConfirmResponseMessage.getService().getPspInfo();

                    Intent intent = new Intent();
                    intent.putExtra(Constants.PAYMENT_INFO, paymentInfo);
                    intent.putExtra(Constants.PSP_INFO, pspInfo);
                    intent.setClass(activity, BusinessPaymentConfirmActivity.class);
                    startActivity(intent);

                    finish();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business Payment Confirm")
                            .setAction("Payment Confirm")
                            .setLabel("Success")
                            .build());
                }else if (businessPaymentConfirmResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
                else {
                    new HamPayDialog(activity).showFailPaymentDialog(businessPaymentConfirmResponseMessage.getService().getResultStatus().getCode(),
                            businessPaymentConfirmResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business Payment Confirm")
                            .setAction("Payment Confirm")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog(Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_fail_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Business Payment Confirm")
                        .setAction("Payment Confirm")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

        }

        @Override
        public void onTaskPreRun() { }
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
