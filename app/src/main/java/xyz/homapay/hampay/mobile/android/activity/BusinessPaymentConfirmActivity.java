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
import xyz.homapay.hampay.common.core.model.request.LatestPurchaseRequest;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.response.LatestPurchaseResponse;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.dto.CardDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestPurchase;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.webservice.psp.Vectorstring2stringMapEntry;
import xyz.homapay.hampay.mobile.android.webservice.psp.string2stringMapEntry;

public class BusinessPaymentConfirmActivity extends AppCompatActivity {

    ButtonRectangle pay_to_business_button;
    FacedTextView cancel_pay_to_business_button;

    Bundle bundle;

    FacedTextView contact_name;
    FacedEditText contact_message;
    FacedTextView credit_value;
    ImageView credit_value_icon;
    boolean intentContact = false;
    Context context;
    Activity activity;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public void backActionBar(View view){
        finish();
    }

    Long MaxXferAmount = 0L;
    Long MinXferAmount = 0L;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;

    FacedTextView business_name;
    ImageView business_logo;

    FacedTextView paymentPriceValue;
    FacedTextView paymentVAT;
    FacedTextView paymentFeeValue;
    FacedTextView paymentTotalValue;
    FacedTextView cardNumberValue;
//    FacedTextView user_bank_name;
    FacedEditText pin2Value;

    RequestLatestPurchase requestLatestPurchase;
    LatestPurchaseRequest latestPurchaseRequest;

    DatabaseHelper databaseHelper;

    PaymentInfoDTO paymentInfoDTO = null;
    PspInfoDTO pspInfoDTO = null;

    RequestPSPResult requestPSPResult;
    PSPResultRequest pspResultRequest;

    private RequestPurchase requestPurchase;
    private DoWorkInfo doWorkInfo;


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
        setContentView(R.layout.activity_business_payment_confirm);

        context = this;
        activity = BusinessPaymentConfirmActivity.this;


        databaseHelper = new DatabaseHelper(context);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        try {

            MaxXferAmount = prefs.getLong(Constants.MAX_BUSINESS_XFER_AMOUNT, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_BUSINESS_XFER_AMOUNT, 0);

        }catch (Exception ex){
            Log.e("Error", ex.getStackTrace().toString());
        }

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        credit_value = (FacedTextView)findViewById(R.id.credit_value);
        credit_value_icon = (ImageView)findViewById(R.id.credit_value_icon);

        contact_message = (FacedEditText)findViewById(R.id.contact_message);
        contact_name = (FacedTextView)findViewById(R.id.contact_name);

        business_name = (FacedTextView)findViewById(R.id.business_name);
        business_logo = (ImageView)findViewById(R.id.business_logo);
        paymentPriceValue = (FacedTextView)findViewById(R.id.paymentPriceValue);
        paymentVAT = (FacedTextView)findViewById(R.id.paymentVAT);
        paymentFeeValue = (FacedTextView)findViewById(R.id.paymentFeeValue);
        paymentTotalValue = (FacedTextView)findViewById(R.id.paymentTotalValue);
//        user_bank_name = (FacedTextView)findViewById(R.id.user_bank_name);
        cardNumberValue = (FacedTextView)findViewById(R.id.cardNumberValue);
        pin2Value = (FacedEditText)findViewById(R.id.pin2Value);

        bundle = getIntent().getExtras();


        Intent intent = getIntent();

        paymentInfoDTO = (PaymentInfoDTO)intent.getSerializableExtra(Constants.PAYMENT_INFO);
        pspInfoDTO = (PspInfoDTO)intent.getSerializableExtra(Constants.PSP_INFO);


        if (paymentInfoDTO != null) {
            PersianEnglishDigit persianEnglishDigit = new PersianEnglishDigit();


            paymentPriceValue.setText(persianEnglishDigit.E2P(paymentInfoDTO.getAmount().toString()) + " ریال");
            paymentFeeValue.setText(persianEnglishDigit.E2P(paymentInfoDTO.getFeeCharge().toString()) + " ریال");
            paymentTotalValue.setText(persianEnglishDigit.E2P(paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge() + "") + " ریال");


//            String LogoUrl = Constants.HTTPS_SERVER_IP + "/merchant-logo/" + paymentInfoDTO.getMerchantImageId();

//            new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(business_logo)).execute(Constants.HTTPS_SERVER_IP + "/merchant-logo/" + purchaseInfoDTO.getMerchantImageId());


            cardNumberValue.setText(persianEnglishDigit.E2P(pspInfoDTO.getCardNo()));
//            user_bank_name.setText(purchaseInfoDTO.getBankName);
        }

        pay_to_business_button = (ButtonRectangle)findViewById(R.id.pay_to_business_button);
        pay_to_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                if (paymentInfoDTO.getFeeCharge() != null) {
                    s2sMapEntry.value = (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge()) + "";
                }else {
                    s2sMapEntry.value = (paymentInfoDTO.getAmount()) + "";
                }
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new string2stringMapEntry();
                s2sMapEntry.key = "Pin2";
                s2sMapEntry.value = pin2Value.getText().toString();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new string2stringMapEntry();
                s2sMapEntry.key = "ThirdParty";
                s2sMapEntry.value = pspInfoDTO.getProductCode();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new string2stringMapEntry();
                s2sMapEntry.key = "TerminalId";
                s2sMapEntry.value = pspInfoDTO.getTerminalID();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new string2stringMapEntry();
                s2sMapEntry.key = "CardId";
                s2sMapEntry.value = pspInfoDTO.getCardNo();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new string2stringMapEntry();
                s2sMapEntry.key = "Merchant";
                s2sMapEntry.value = pspInfoDTO.getMerchant();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new string2stringMapEntry();
                s2sMapEntry.key = "IPAddress";
                s2sMapEntry.value = pspInfoDTO.getIpAddress();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                doWorkInfo.setVectorstring2stringMapEntry(vectorstring2stringMapEntry);
                requestPurchase.execute(doWorkInfo);


            }
        });

        cancel_pay_to_business_button = (FacedTextView)findViewById(R.id.cancel_pay_to_business_button);
        cancel_pay_to_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
//        Log.e("EXIT", "onUserInteraction");
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
