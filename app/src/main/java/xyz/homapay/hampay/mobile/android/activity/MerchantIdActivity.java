package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.UserMerchantInquiryRequest;
import xyz.homapay.hampay.common.core.model.request.UserMerchantRequest;
import xyz.homapay.hampay.common.core.model.response.UserMerchantInquiryResponse;
import xyz.homapay.hampay.common.core.model.response.UserMerchantResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.task.UserMerchantInquiryTask;
import xyz.homapay.hampay.mobile.android.async.task.UserMerchantTask;
import xyz.homapay.hampay.mobile.android.async.task.impl.OnTaskCompleted;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class MerchantIdActivity extends AppCompatActivity implements OnTaskCompleted, View.OnClickListener {

    private Activity activity;
    private SharedPreferences prefs;
    private Context context;
    private LinearLayout requestLayout;
    private LinearLayout statusLayout;
    private LinearLayout resultLayout;
    private FacedTextView requestMerchantIdBtn;
    private FacedTextView closeRequestMerchantId;
    private FacedTextView closeMerchantIdBtn;
    private FacedTextView closeViewMerchantId;
    private FacedTextView merchantInfo;
    private String authToken = "";
    private PersianEnglishDigit persian;
    private FacedTextView merchantIdText;
    private HamPayDialog hamPayDialog;
    private FacedTextView step1Text;
    private FacedTextView step2Text;

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
        setContentView(R.layout.activity_merchant_id);
        activity = MerchantIdActivity.this;
        context = this;
        hamPayDialog = new HamPayDialog(activity);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
        persian = new PersianEnglishDigit();

        merchantIdText = (FacedTextView) findViewById(R.id.merchant_id_text);
        requestLayout = (LinearLayout) findViewById(R.id.requestLayout);
        statusLayout = (LinearLayout) findViewById(R.id.statusLayout);
        resultLayout = (LinearLayout) findViewById(R.id.resultLayout);

        requestMerchantIdBtn = (FacedTextView) findViewById(R.id.requestMerchantId);
        requestMerchantIdBtn.setOnClickListener(this);

        closeRequestMerchantId = (FacedTextView) findViewById(R.id.closeRequestMerchantId);
        closeRequestMerchantId.setOnClickListener(this);

        closeMerchantIdBtn = (FacedTextView) findViewById(R.id.closeMerchantId);
        closeMerchantIdBtn.setOnClickListener(this);

        closeViewMerchantId = (FacedTextView) findViewById(R.id.closeViewMerchantId);
        closeViewMerchantId.setOnClickListener(this);

        merchantInfo = (FacedTextView) findViewById(R.id.merchantInfo);

        step1Text = (FacedTextView) findViewById(R.id.step_1_text);
        step2Text = (FacedTextView) findViewById(R.id.step_2_text);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.merchant_id_not_set));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE));
        spannableStringBuilder.setSpan(foregroundColorSpan, 75, 82, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        step1Text.setText(spannableStringBuilder);

        Spannable merchantIdStatus = new SpannableString(activity.getString(R.string.merchant_id_status_pending));
        ClickableSpan emailClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", activity.getString(R.string.merchent_document_email), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.merchant_id));
                emailIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.merchant_id_document));
                activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.hampay_contact)));
            }
        };
        merchantIdStatus.setSpan(emailClickableSpan, 58, 75, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        step2Text.setText(merchantIdStatus);
        step2Text.setMovementMethod(LinkMovementMethod.getInstance());

        UserMerchantInquiryRequest userMerchantInquiryRequest = new UserMerchantInquiryRequest();
        new UserMerchantInquiryTask(activity, MerchantIdActivity.this, userMerchantInquiryRequest, authToken).execute();
    }


    @Override
    public void OnTaskPreExecute() {
        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
    }

    @Override
    public void OnTaskExecuted(Object object) {

        hamPayDialog.dismisWaitingDialog();
        if (object != null) {
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);
            if (object.getClass().equals(ResponseMessage.class)) {
                ResponseMessage responseMessage = (ResponseMessage) object;
                switch (responseMessage.getService().getServiceDefinition()) {
                    case USER_MERCHANT_INQUIRY:
                        ResponseMessage<UserMerchantInquiryResponse> userMerchantInquiry = (ResponseMessage) object;
                        switch (userMerchantInquiry.getService().getResultStatus()) {
                            case SUCCESS:
                                serviceName = ServiceEvent.USER_MERCHANT_INQUIRY_SUCCESS;
                                logEvent.log(serviceName);
                                switch (userMerchantInquiry.getService().getStatus()) {
                                    case NONE:
                                        requestLayout.setVisibility(View.VISIBLE);
                                        break;

                                    case REJECT:
                                        requestLayout.setVisibility(View.VISIBLE);
                                        break;

                                    case PENDING:
                                        requestLayout.setVisibility(View.GONE);
                                        statusLayout.setVisibility(View.VISIBLE);
                                        merchantIdText.setText(getString(R.string.merchant_text_page_2));
                                        break;

                                    case ACCEPT:
                                        Toast.makeText(activity, getString(R.string.merchant_id_accept), Toast.LENGTH_LONG).show();
                                        break;
                                }
                                break;
                            default:
                                serviceName = ServiceEvent.USER_MERCHANT_INQUIRY_FAILURE;
                                break;
                        }
                        logEvent.log(serviceName);
                        break;

                    case USER_MERCHANT_REQUEST:
                        ResponseMessage<UserMerchantResponse> userMerchant = (ResponseMessage) object;
                        switch (userMerchant.getService().getResultStatus()) {
                            case SUCCESS:
                                requestLayout.setVisibility(View.GONE);
                                statusLayout.setVisibility(View.VISIBLE);
                                merchantIdText.setText(getString(R.string.merchant_text_page_2));
                                serviceName = ServiceEvent.USER_MERCHANT_REQUEST_SUCCESS;
//                                logEvent.log(serviceName);
//                                String merchantId = "10000";
//                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(persian.E2P(getString(R.string.merchant_info, merchantId)));
//                                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE));
//                                spannableStringBuilder.setSpan(foregroundColorSpan, 24, merchantId.length() + 26, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                                merchantInfo.setText(spannableStringBuilder);
//                                requestLayout.setVisibility(View.GONE);
//                                resultLayout.setVisibility(View.VISIBLE);
//                                merchantIdText.setText(getString(R.string.merchant_text_page_3));
                                break;

                            default:
                                serviceName = ServiceEvent.USER_MERCHANT_REQUEST_FAILURE;
                                break;
                        }
                        logEvent.log(serviceName);
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeMerchantId:
                finish();
                break;
            case R.id.requestMerchantId:
                UserMerchantRequest userMerchantRequest = new UserMerchantRequest();
                new UserMerchantTask(activity, MerchantIdActivity.this, userMerchantRequest, authToken).execute();
                break;

            case R.id.closeRequestMerchantId:
                finish();
                break;

            case R.id.closeViewMerchantId:
                finish();
                break;
        }
    }
}
