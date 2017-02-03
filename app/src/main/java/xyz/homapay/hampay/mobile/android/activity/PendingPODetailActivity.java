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

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.img.ImageHelper;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PendingPODetailActivity extends AppCompatActivity {

    @BindView(R.id.caller_name)
    FacedTextView caller_name;
    @BindView(R.id.caller_phone_number)
    FacedTextView caller_phone_number;
    @BindView(R.id.amount_value)
    FacedTextView amount_value;
    @BindView(R.id.payment_request_code)
    FacedTextView payment_request_code;
    @BindView(R.id.create_date)
    FacedTextView create_date;
    @BindView(R.id.message_text)
    FacedTextView message_text;
    @BindView(R.id.re_payment_request)
    LinearLayout re_payment_request;
    @BindView(R.id.user_image)
    ImageView user_image;
    private PaymentInfoDTO paymentInfo;
    private PersianEnglishDigit persianEnglishDigit;
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
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
        setContentView(R.layout.activity_pending_po_detail);
        ButterKnife.bind(this);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
        context = this;
        activity = PendingPODetailActivity.this;
        Intent intent = getIntent();
        paymentInfo = (PaymentInfoDTO) intent.getSerializableExtra(Constants.PAYMENT_INFO);

        caller_name.setText(paymentInfo.getCalleeName());
        if (paymentInfo.getImageId() != null) {
            user_image.setTag(paymentInfo.getImageId());
            ImageHelper.getInstance(activity).imageLoader(paymentInfo.getImageId(), user_image, R.drawable.user_placeholder);
        } else {
            user_image.setImageResource(R.drawable.user_placeholder);
        }
        caller_phone_number.setText(persianEnglishDigit.E2P(paymentInfo.getCalleePhoneNumber()));
        amount_value.setText(persianEnglishDigit.E2P(formatter.format(paymentInfo.getAmount())));
        payment_request_code.setText(persianEnglishDigit.E2P(paymentInfo.getProductCode()));
        create_date.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(paymentInfo.getCreatedBy())));
        message_text.setText("پیام: " + paymentInfo.getMessage());

        re_payment_request.setOnClickListener(v -> {
            Intent intent1 = new Intent();
            intent1.putExtra(Constants.PAYMENT_INFO, paymentInfo);
            intent1.setClass(activity, PaymentRequestDetailActivity.class);
            startActivity(intent1);
        });


    }
}