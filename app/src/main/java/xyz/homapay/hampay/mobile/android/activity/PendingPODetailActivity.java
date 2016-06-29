package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.PaymentDetailRequest;
import xyz.homapay.hampay.common.core.model.request.PurchaseDetailRequest;
import xyz.homapay.hampay.common.core.model.response.PaymentDetailResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseDetailResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPaymentDetail;
import xyz.homapay.hampay.mobile.android.async.RequestPurchaseDetail;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PendingPODetailActivity extends AppCompatActivity {

    Bundle bundle;
    PaymentInfoDTO paymentInfo;
    PersianEnglishDigit persianEnglishDigit;
    Context context;
    Activity activity;
    private CurrencyFormatter formatter;

    private FacedTextView caller_name;
    private FacedTextView caller_phone_number;
    private FacedTextView amount_value;
    private FacedTextView payment_request_code;
    private FacedTextView create_date;
    private FacedTextView message_text;
    private LinearLayout re_payment_request;
    SharedPreferences prefs;
    private ImageView user_image;
    private ImageManager imageManager;
    private String authToken;


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
        setContentView(R.layout.activity_pending_po_detail);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
        context = this;
        activity = PendingPODetailActivity.this;
        imageManager = new ImageManager(activity, 200000, false);
        bundle = getIntent().getExtras();
        Intent intent = getIntent();
        paymentInfo = (PaymentInfoDTO) intent.getSerializableExtra(Constants.PAYMENT_INFO);

        caller_name = (FacedTextView)findViewById(R.id.caller_name);
        user_image = (ImageView)findViewById(R.id.user_image);
        caller_name.setText(paymentInfo.getCalleeName());
        if (paymentInfo.getImageId() != null) {
            String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + paymentInfo.getImageId();
            user_image.setTag(userImageUrl.split("/")[6]);
            imageManager.displayImage(userImageUrl, user_image, R.drawable.user_placeholder);
        }else {
            user_image.setImageResource(R.drawable.user_placeholder);
        }
        caller_phone_number = (FacedTextView)findViewById(R.id.caller_phone_number);
        caller_phone_number.setText(persianEnglishDigit.E2P(paymentInfo.getCalleePhoneNumber()));
        amount_value = (FacedTextView)findViewById(R.id.amount_value);
        amount_value.setText(persianEnglishDigit.E2P(formatter.format(paymentInfo.getAmount() + paymentInfo.getVat())));
        payment_request_code = (FacedTextView)findViewById(R.id.payment_request_code);
        payment_request_code.setText(persianEnglishDigit.E2P(paymentInfo.getProductCode()));
        create_date = (FacedTextView)findViewById(R.id.create_date);
        create_date.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(paymentInfo.getCreatedBy())));
        message_text = (FacedTextView)findViewById(R.id.message_text);
        message_text.setText("پیام: " + paymentInfo.getMessage());

        re_payment_request = (LinearLayout)findViewById(R.id.re_payment_request);
        re_payment_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Constants.PAYMENT_INFO, paymentInfo);
                intent.setClass(activity, PaymentRequestDetailActivity.class);
                startActivity(intent);
            }
        });



    }
}