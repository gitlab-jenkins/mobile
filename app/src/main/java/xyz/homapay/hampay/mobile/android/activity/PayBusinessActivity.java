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
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.UserVerificationStatus;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentConfirmRequest;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestBusinessPaymentConfirm;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestPurchase;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.CurrencyFormatterTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.webservice.psp.Vectorstring2stringMapEntry;
import xyz.homapay.hampay.mobile.android.webservice.psp.string2stringMapEntry;

public class PayBusinessActivity extends AppCompatActivity {


    Bundle bundle;

    PersianEnglishDigit persianEnglishDigit;

    FacedTextView business_name_code;

    ButtonRectangle pay_to_business_button;

    FacedEditText credit_value;
    boolean creditValueValidation = false;
    ImageView credit_value_icon;

    FacedEditText contact_message;

    Context context;
    Activity activity;

    Long amountValue = 0l;
    String businessMssage = "";

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private RequestBusinessPaymentConfirm requestBusinessPaymentConfirm;
    private BusinessPaymentConfirmRequest businessPaymentConfirmRequest;

    private RequestPurchase requestPurchase;
    private DoWorkInfo doWorkInfo;

    FacedEditText pin2Value;

    Long MaxXferAmount = 0L;
    Long MinXferAmount = 0L;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;

    DeviceInfo deviceInfo;

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_business);

        persianEnglishDigit = new PersianEnglishDigit();

        context = this;
        activity = PayBusinessActivity.this;

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();


        deviceInfo = new DeviceInfo(context);

        try {
            MaxXferAmount = prefs.getLong(Constants.MAX_BUSINESS_XFER_AMOUNT, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_BUSINESS_XFER_AMOUNT, 0);

        }catch (Exception ex){
            Log.e("Error", ex.getStackTrace().toString());
        }

        bundle = getIntent().getExtras();

        pin2Value = (FacedEditText)findViewById(R.id.pin2Value);

        business_name_code = (FacedTextView)findViewById(R.id.business_name_code);
        business_name_code.setText(persianEnglishDigit.E2P(bundle.getString("business_name") + " " + "(" + bundle.getString("business_code") + ")"));

        contact_message = (FacedEditText)findViewById(R.id.contact_message);

        credit_value = (FacedEditText)findViewById(R.id.credit_value);
        credit_value.addTextChangedListener(new CurrencyFormatterTextWatcher(credit_value));
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

                    amountValue = Long.parseLong(new PersianEnglishDigit(credit_value.getText().toString()).P2E().replace(",", ""));
                    businessMssage = contact_message.getText().toString();

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
                            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                            businessPaymentConfirmRequest = new BusinessPaymentConfirmRequest();
                            businessPaymentConfirmRequest.setAmount(amountValue);
                            businessPaymentConfirmRequest.setBusinessCode(bundle.getString("business_code"));
                            requestBusinessPaymentConfirm = new RequestBusinessPaymentConfirm(context, new RequestBusinessPaymentConfirmTaskCompleteListener());
                            requestBusinessPaymentConfirm.execute(businessPaymentConfirmRequest);
                        }else {
                            new HamPayDialog(activity).showIncorrectAmountDialog(MinXferAmount, MaxXferAmount);

                        }
                    }

                }else {
                    (new HamPayDialog(activity)).showIncorrectPrice();

                }
            }
        });

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
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
//                    new HamPayDialog(activity).businessPaymentConfirmDialog(businessPaymentConfirmResponseMessage.getService(), amountValue, businessMssage);

                    PaymentInfoDTO paymentInfo = businessPaymentConfirmResponseMessage.getService().getPaymentInfo();
                    PspInfoDTO pspInfo = businessPaymentConfirmResponseMessage.getService().getPspInfo();

                    if (pin2Value.getText().toString().length() <= 4 ){
                        Toast.makeText(context, getString(R.string.msg_pin2_incurrect), Toast.LENGTH_LONG).show();
                        return;
                    }

                    requestPurchase = new RequestPurchase(activity, new RequestPurchaseTaskCompleteListener());

                    doWorkInfo = new DoWorkInfo();
                    doWorkInfo.setUserName("test");
                    doWorkInfo.setPassword("1234");
                    doWorkInfo.setCellNumber(prefs.getString(Constants.REGISTERED_CELL_NUMBER, ""));
                    doWorkInfo.setLangAByte((byte) 0);
                    doWorkInfo.setLangABoolean(false);
                    Vectorstring2stringMapEntry vectorstring2stringMapEntry = new Vectorstring2stringMapEntry();
                    string2stringMapEntry s2sMapEntry = new string2stringMapEntry();

                    s2sMapEntry.key = "Amount";
                    if (paymentInfo.getFeeCharge() != null) {
                        s2sMapEntry.value = (paymentInfo.getAmount() + paymentInfo.getFeeCharge()) + "";
                    }else {
                        s2sMapEntry.value = (paymentInfo.getAmount()) + "";
                    }
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new string2stringMapEntry();
                    s2sMapEntry.key = "Pin2";
                    s2sMapEntry.value = pin2Value.getText().toString();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new string2stringMapEntry();
                    s2sMapEntry.key = "ThirdParty";
                    s2sMapEntry.value = pspInfo.getProductCode();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new string2stringMapEntry();
                    s2sMapEntry.key = "TerminalId";
                    s2sMapEntry.value = "283129";
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new string2stringMapEntry();
                    s2sMapEntry.key = "CardId";
                    s2sMapEntry.value = /*cardDTO.getCardId()*/ "100";
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new string2stringMapEntry();
                    s2sMapEntry.key = "Merchant";
                    s2sMapEntry.value = "123";
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new string2stringMapEntry();
                    s2sMapEntry.key = "IPAddress";
                    s2sMapEntry.value = "192.168.0.1";
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    doWorkInfo.setVectorstring2stringMapEntry(vectorstring2stringMapEntry);
                    requestPurchase.execute(doWorkInfo);


                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business Payment Confirm")
                            .setAction("Payment Confirm")
                            .setLabel("Success")
                            .build());
                }else {
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

    public class RequestPurchaseTaskCompleteListener implements AsyncTaskCompleteListener<Vectorstring2stringMapEntry> {

        @Override
        public void onTaskComplete(Vectorstring2stringMapEntry purchaseResponseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (purchaseResponseResponseMessage != null){

                new HamPayDialog(activity).pspResultDialog("10000" + "");

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pending Payment Request")
                        .setAction("Payment")
                        .setLabel("Success")
                        .build());

            }
            else {
                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Pending Payment Request")
                        .setAction("Payment")
                        .setLabel("Fail(Server)")
                        .build());
            }

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


}
