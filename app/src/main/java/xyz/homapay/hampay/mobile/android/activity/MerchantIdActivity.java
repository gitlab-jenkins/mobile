package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.UserRequestForBeingMerchantProgressRequest;
import xyz.homapay.hampay.common.core.model.request.UserRequestForBeingMerchantRequest;
import xyz.homapay.hampay.common.core.model.response.UserRequestForBeingMerchantProgressResponse;
import xyz.homapay.hampay.common.core.model.response.UserRequestForBeingMerchantResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.task.UserRequestForBeingMerchantProgressTask;
import xyz.homapay.hampay.mobile.android.async.task.UserRequestForBeingMerchantTask;
import xyz.homapay.hampay.mobile.android.async.task.impl.OnTaskCompleted;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class MerchantIdActivity extends AppCompatActivity implements OnTaskCompleted, View.OnClickListener{

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
        setContentView(R.layout.activity_merchant_id);
        activity = MerchantIdActivity.this;
        context = this;
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
        persian = new PersianEnglishDigit();

        requestLayout = (LinearLayout)findViewById(R.id.requestLayout);
        statusLayout = (LinearLayout)findViewById(R.id.statusLayout);
        resultLayout = (LinearLayout)findViewById(R.id.resultLayout);

        requestMerchantIdBtn = (FacedTextView)findViewById(R.id.requestMerchantId);
        requestMerchantIdBtn.setOnClickListener(this);

        closeRequestMerchantId = (FacedTextView)findViewById(R.id.closeRequestMerchantId);
        closeRequestMerchantId.setOnClickListener(this);

        closeMerchantIdBtn = (FacedTextView)findViewById(R.id.closeMerchantId);
        closeMerchantIdBtn.setOnClickListener(this);

        closeViewMerchantId = (FacedTextView)findViewById(R.id.closeViewMerchantId);
        closeViewMerchantId.setOnClickListener(this);

        merchantInfo = (FacedTextView)findViewById(R.id.merchantInfo);
    }


    @Override
    public void OnTaskPreExecute() {

    }

    @Override
    public void OnTaskExecuted(Object object) {

        if (object != null) {
            if (object.getClass().equals(ResponseMessage.class)) {
                ResponseMessage responseMessage = (ResponseMessage) object;
                switch (responseMessage.getService().getServiceDefinition()){
                    case USER_REQUEST_FOR_BEING_MERCHANT:
                        ResponseMessage<UserRequestForBeingMerchantResponse> userRequestForBeingMerchant = (ResponseMessage) object;
                        switch (userRequestForBeingMerchant.getService().getResultStatus()){
                            case SUCCESS:
                                if (userRequestForBeingMerchant.getService().getMerchantProgressForUserStatus().equalsIgnoreCase("PENDING")){
                                    requestLayout.setVisibility(View.GONE);
                                    statusLayout.setVisibility(View.VISIBLE);
                                }else if (userRequestForBeingMerchant.getService().getMerchantProgressForUserStatus().equalsIgnoreCase("ACCEPT")){
                                    UserRequestForBeingMerchantProgressRequest userRequestForBeingMerchantProgress = new UserRequestForBeingMerchantProgressRequest();
                                    new UserRequestForBeingMerchantProgressTask(activity, MerchantIdActivity.this, userRequestForBeingMerchantProgress, authToken).execute();
                                }
                                break;
                            default:
                                break;
                        }
                        break;

                    case USER_REQUEST_FOR_BEING_MERCHANT_RESULT:
                        ResponseMessage<UserRequestForBeingMerchantProgressResponse> userRequestForBeingMerchantProgress = (ResponseMessage) object;
                        switch (userRequestForBeingMerchantProgress.getService().getResultStatus()){
                            case SUCCESS:
                                String merchantId = userRequestForBeingMerchantProgress.getService().getMerchantId();
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(persian.E2P(getString(R.string.merchant_info, merchantId)));
                                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE));
                                spannableStringBuilder.setSpan(foregroundColorSpan, 24, merchantId.length() + 26, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                merchantInfo.setText(spannableStringBuilder);
                                requestLayout.setVisibility(View.GONE);
                                resultLayout.setVisibility(View.VISIBLE);
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.closeMerchantId:
                finish();
                break;
            case R.id.requestMerchantId:
                UserRequestForBeingMerchantRequest userRequestForBeingMerchantRequest = new UserRequestForBeingMerchantRequest();
                new UserRequestForBeingMerchantTask(activity, MerchantIdActivity.this, userRequestForBeingMerchantRequest, authToken).execute();
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
