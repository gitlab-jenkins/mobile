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

import butterknife.BindView;
import butterknife.ButterKnife;
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
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.img.ImageHelper;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class BusinessPaymentInfoActivity extends AppCompatActivity implements View.OnClickListener {

    PersianEnglishDigit persianEnglishDigit;

    @BindView(R.id.business_name)
    FacedTextView business_name;
    @BindView(R.id.business_hampay_id)
    FacedTextView business_hampay_id;
    @BindView(R.id.business_image)
    ImageView business_image;
    @BindView(R.id.payment_buttonpayment_button)
    ImageView payment_button;
    @BindView(R.id.amount_value)
    FacedEditText amount_value;
    boolean creditValueValidation = false;

    Context context;
    Activity activity;

    Long amountValue = 0L;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Long MaxXferAmount = 0L;
    Long MinXferAmount = 0L;
    HamPayDialog hamPayDialog;
    @BindView(R.id.add_vat)
    LinearLayout add_vat;
    @BindView(R.id.vat_icon)
    ImageView vat_icon;
    @BindView(R.id.vat_value)
    FacedTextView vat_value;
    @BindView(R.id.amount_total)
    FacedTextView amount_total;
    private RequestBusinessPaymentConfirm requestBusinessPaymentConfirm;
    private BusinessPaymentConfirmRequest businessPaymentConfirmRequest;
    private BusinessDTO businessDTO;
    private Long calculatedVat = 0L;
    private CurrencyFormatter formatter;

    public void backActionBar(View view) {
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
        ButterKnife.bind(this);

        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
        context = this;
        activity = BusinessPaymentInfoActivity.this;
        hamPayDialog = new HamPayDialog(activity);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        try {
            MaxXferAmount = prefs.getLong(Constants.MAX_BUSINESS_XFER_AMOUNT, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_BUSINESS_XFER_AMOUNT, 0);
        } catch (Exception ex) {
            Log.e("Error", ex.getStackTrace().toString());
        }

        Intent intent = getIntent();
        businessDTO = (BusinessDTO) intent.getSerializableExtra(Constants.BUSINESS_INFO);
        business_name.setText(persianEnglishDigit.E2P(businessDTO.getTitle()));
        business_hampay_id.setText(getString(R.string.business_id) + persianEnglishDigit.E2P(businessDTO.getCode()));
        if (businessDTO.getBusinessImageId() != null) {
            AppManager.setMobileTimeout(context);
            editor.commit();
            business_image.setTag(businessDTO.getBusinessImageId());
            ImageHelper.getInstance(activity).imageLoader(businessDTO.getBusinessImageId(), business_image, R.drawable.user_placeholder);
        } else {
            business_image.setImageResource(R.drawable.user_placeholder);
        }

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
        amount_value.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                creditValueValidation = amount_value.getText().toString().length() != 0;
            } else {
            }
        });
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_vat:
                AppManager.setMobileTimeout(context);
                editor.commit();
                if (amount_value.getText().toString().length() > 0) {
                    if (amount_value.getText().toString().indexOf("٬") != -1) {
                        amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace("٬", "")));
                    } else if (amount_value.getText().toString().indexOf(",") != -1) {
                        amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace(",", "")));
                    }
                    if (calculatedVat == 0) {
                        CalculateVatRequest calculateVatRequest = new CalculateVatRequest();
                        calculateVatRequest.setAmount(amountValue);
                        RequestCalculateVat requestCalculateVat = new RequestCalculateVat(activity, new RequestCalculateVatTaskCompleteListener());
                        requestCalculateVat.execute(calculateVatRequest);
                    } else {
                        vat_icon.setImageResource(R.drawable.add_vat);
                        vat_value.setText("۰");
                        calculatedVat = 0L;
                        amount_total.setText(persianEnglishDigit.E2P(formatter.format(amountValue)));
                    }
                }
                break;
            case R.id.payment_buttonpayment_button:
                amount_value.clearFocus();
                if (amount_value.getText().toString().length() == 0) {
                    payment_button.setEnabled(false);
                    Toast.makeText(activity, getString(R.string.msg_null_amount), Toast.LENGTH_SHORT).show();
                    payment_button.setEnabled(true);
                    return;
                }
                if (creditValueValidation) {
                    AppManager.setMobileTimeout(context);
                    editor.commit();
                    if (amount_value.getText().toString().indexOf("٬") != -1) {
                        amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace("٬", "")));
                    } else if (amount_value.getText().toString().indexOf(",") != -1) {
                        amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace(",", "")));
                    }
                    if (amountValue + calculatedVat >= MinXferAmount && amountValue + calculatedVat <= MaxXferAmount) {
                        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                        businessPaymentConfirmRequest = new BusinessPaymentConfirmRequest();
                        businessPaymentConfirmRequest.setAmount(amountValue);
                        businessPaymentConfirmRequest.setVat(calculatedVat);
                        businessPaymentConfirmRequest.setBusinessCode(businessDTO.getCode());
                        requestBusinessPaymentConfirm = new RequestBusinessPaymentConfirm(context, new RequestBusinessPaymentConfirmTaskCompleteListener());
                        requestBusinessPaymentConfirm.execute(businessPaymentConfirmRequest);
                    } else {
                        new HamPayDialog(activity).showIncorrectAmountDialog(MinXferAmount, MaxXferAmount);

                    }
                }
                break;
        }
    }

    public class RequestBusinessPaymentConfirmTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessPaymentConfirmResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<BusinessPaymentConfirmResponse> businessPaymentConfirmResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

            if (businessPaymentConfirmResponseMessage != null) {
                if (businessPaymentConfirmResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.BUSINESS_PAYMENT_CONFIRM_SUCCESS;
                    PaymentInfoDTO paymentInfo = businessPaymentConfirmResponseMessage.getService().getPaymentInfo();
                    PspInfoDTO pspInfo = businessPaymentConfirmResponseMessage.getService().getPaymentInfo().getPspInfo();

                    Intent intent = new Intent();
                    intent.putExtra(Constants.PAYMENT_INFO, paymentInfo);
                    intent.putExtra(Constants.PSP_INFO, pspInfo);
                    intent.setClass(activity, BusinessPaymentConfirmActivity.class);
                    startActivity(intent);
                    finish();
                } else if (businessPaymentConfirmResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.BUSINESS_PAYMENT_CONFIRM_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.BUSINESS_PAYMENT_CONFIRM_FAILURE;
                    new HamPayDialog(activity).showFailPaymentDialog(businessPaymentConfirmResponseMessage.getService().getResultStatus().getCode(),
                            businessPaymentConfirmResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                serviceName = ServiceEvent.BUSINESS_PAYMENT_CONFIRM_FAILURE;
                new HamPayDialog(activity).showFailPaymentDialog(Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_fail_payment));
            }

            logEvent.log(serviceName);

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

            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);
            hamPayDialog.dismisWaitingDialog();
            ResultStatus resultStatus;
            if (calculateVatResponseMessage != null) {
                resultStatus = calculateVatResponseMessage.getService().getResultStatus();
                if (resultStatus == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.CALCULATE_VAT_SUCCESS;
                    vat_value.setText(persianEnglishDigit.E2P(formatter.format(calculateVatResponseMessage.getService().getAmount())));
                    calculatedVat = calculateVatResponseMessage.getService().getAmount();
                    amount_total.setText(persianEnglishDigit.E2P(formatter.format(calculatedVat + amountValue)));
                    vat_icon.setImageResource(R.drawable.remove_vat);
                } else if (calculateVatResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.CALCULATE_VAT_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.CALCULATE_VAT_FAILURE;
                }
                logEvent.log(serviceName);
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

}
