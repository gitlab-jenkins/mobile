package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class MerchantIdActivity extends AppCompatActivity implements OnTaskCompleted, View.OnClickListener{

    private Activity activity;
    private SharedPreferences prefs;
    private Context context;
    private FacedTextView requestMerchantIdBtn;
    private FacedTextView closeMerchantIdBtn;
    private FacedTextView merchantInfo;
    private String authToken = "";

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

        requestMerchantIdBtn = (FacedTextView)findViewById(R.id.requestMerchantId);
        requestMerchantIdBtn.setOnClickListener(this);

        closeMerchantIdBtn = (FacedTextView)findViewById(R.id.closeMerchantId);
        closeMerchantIdBtn.setOnClickListener(this);

        merchantInfo = (FacedTextView)findViewById(R.id.merchantInfo);
        merchantInfo.setText(getString(R.string.merchant_info, "1", "2"));
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
                                break;
                            default:
                                break;
                        }
                        break;

                    case USER_REQUEST_FOR_BEING_MERCHANT_RESULT:
                        ResponseMessage<UserRequestForBeingMerchantProgressResponse> userRequestForBeingMerchantProgress = (ResponseMessage) object;
                        switch (userRequestForBeingMerchantProgress.getService().getResultStatus()){
                            case SUCCESS:

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
//                UserRequestForBeingMerchantRequest userRequestForBeingMerchantRequest = new UserRequestForBeingMerchantRequest();
//                new UserRequestForBeingMerchantTask(activity, MerchantIdActivity.this, userRequestForBeingMerchantRequest, authToken).execute();
                UserRequestForBeingMerchantProgressRequest userRequestForBeingMerchantProgress = new UserRequestForBeingMerchantProgressRequest();
                new UserRequestForBeingMerchantProgressTask(activity, MerchantIdActivity.this, userRequestForBeingMerchantProgress, authToken).execute();
                break;
        }
    }
}
