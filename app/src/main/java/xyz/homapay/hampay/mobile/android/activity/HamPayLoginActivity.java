package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.RecentPendingFundRequest;
import xyz.homapay.hampay.common.core.model.request.TACRequest;
import xyz.homapay.hampay.common.core.model.response.RecentPendingFundResponse;
import xyz.homapay.hampay.common.core.model.response.TACResponse;
import xyz.homapay.hampay.common.core.model.response.dto.FundDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PendingFundAdapter;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestLogin;
import xyz.homapay.hampay.mobile.android.async.RequestRecentPendingFund;
import xyz.homapay.hampay.mobile.android.async.RequestTAC;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.FailedLoginResponse;
import xyz.homapay.hampay.mobile.android.model.LoginData;
import xyz.homapay.hampay.mobile.android.model.NotificationMessageType;
import xyz.homapay.hampay.mobile.android.model.SuccessLoginResponse;
import xyz.homapay.hampay.mobile.android.util.AppInfo;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.SecurityUtils;


public class HamPayLoginActivity extends AppCompatActivity implements View.OnClickListener {



    public static HamPayLoginActivity instance = null;

    RequestLogin requestLogin;

    FacedTextView hampay_memorableword_text;
    String nationalCode = "";
    String memorableWord;
    String installationToken;
    FacedTextView digit_1;
    FacedTextView digit_2;
    FacedTextView digit_3;
    FacedTextView digit_4;
    FacedTextView digit_5;
    FacedTextView digit_6;
    FacedTextView digit_7;
    FacedTextView digit_8;
    FacedTextView digit_9;
    FacedTextView digit_0;
    FacedTextView keyboard_dismiss;
    RelativeLayout backspace;

    String inputPassValue = "";

    ImageView input_digit_1;
    ImageView input_digit_2;
    ImageView input_digit_3;
    ImageView input_digit_4;
    ImageView input_digit_5;

    LinearLayout keyboard;
    LinearLayout password_holder;

    Context context;
    Activity activity;

    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    HamPayDialog hamPayDialog;

    FacedTextView hampay_user;

    TACRequest tacRequest;
    RequestTAC requestTAC;

    Tracker hamPayGaTracker;


    boolean fromNotification = false;

    String password = "";
    private LinearLayout pendingFundLayout;
    private ListView recentPendingFundList;
    private RequestRecentPendingFund requestRecentPendingFund;
    private RecentPendingFundRequest recentPendingFundRequest;
    private PendingFundAdapter pendingFundAdapter;

    public void userManual(View view){
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_login);
        startActivity(intent);
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

        if (requestTAC != null){
            if (!requestTAC.isCancelled())
                requestTAC.cancel(true);
        }

        if (requestLogin != null){
            if (!requestLogin.isCancelled()){
                requestLogin.cancel(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HamPayApplication.setAppSate(AppState.Resumed);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HamPayApplication.setAppSate(AppState.Resumed);
    }

    @Override
    public void finish() {
        super.finish();
        instance = null;
    }

    Bundle bundle;

    ImageView image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ham_pay_login);



        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        instance = this;

        context = this;
        activity = HamPayLoginActivity.this;

        image = (ImageView)findViewById(R.id.image);
        String userImageUrl = "http://www.asriran.com/files/fa/news/1395/2/31/586342_830.jpg";

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        requestRecentPendingFund = new RequestRecentPendingFund(activity, new RequestRecentFundTaskCompleteListener());
        recentPendingFundRequest = new RecentPendingFundRequest();
        recentPendingFundRequest.setImei(new DeviceInfo(activity).getIMEI());
        recentPendingFundRequest.setNationalCode(prefs.getString(Constants.REGISTERED_NATIONAL_CODE, ""));
        requestRecentPendingFund.execute(recentPendingFundRequest);

        editor.putBoolean(Constants.FETCHED_HAMPAY_ENABLED, false);
        editor.commit();

        hampay_user = (FacedTextView)findViewById(R.id.hampay_user);
        hampay_user.setText(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        pendingFundLayout = (LinearLayout)findViewById(R.id.pending_fund_layout);
        recentPendingFundList = (ListView)findViewById(R.id.recent_pending_fund_list);



        bundle = getIntent().getExtras();

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        hamPayDialog = new HamPayDialog(activity);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);


        digit_1 = (FacedTextView)findViewById(R.id.digit_1);
        digit_1.setOnClickListener(this);
        digit_2 = (FacedTextView)findViewById(R.id.digit_2);
        digit_2.setOnClickListener(this);
        digit_3 = (FacedTextView)findViewById(R.id.digit_3);
        digit_3.setOnClickListener(this);
        digit_4 = (FacedTextView)findViewById(R.id.digit_4);
        digit_4.setOnClickListener(this);
        digit_5 = (FacedTextView)findViewById(R.id.digit_5);
        digit_5.setOnClickListener(this);
        digit_6 = (FacedTextView)findViewById(R.id.digit_6);
        digit_6.setOnClickListener(this);
        digit_7 = (FacedTextView)findViewById(R.id.digit_7);
        digit_7.setOnClickListener(this);
        digit_8 = (FacedTextView)findViewById(R.id.digit_8);
        digit_8.setOnClickListener(this);
        digit_9 = (FacedTextView)findViewById(R.id.digit_9);
        digit_9.setOnClickListener(this);
        digit_0 = (FacedTextView)findViewById(R.id.digit_0);
        digit_0.setOnClickListener(this);
        keyboard_dismiss = (FacedTextView)findViewById(R.id.keyboard_dismiss);
        keyboard_dismiss.setOnClickListener(this);
        backspace = (RelativeLayout) findViewById(R.id.backspace);
        backspace.setOnClickListener(this);

        input_digit_1 = (ImageView)findViewById(R.id.input_digit_1);
        input_digit_2 = (ImageView)findViewById(R.id.input_digit_2);
        input_digit_3 = (ImageView)findViewById(R.id.input_digit_3);
        input_digit_4 = (ImageView)findViewById(R.id.input_digit_4);
        input_digit_5 = (ImageView)findViewById(R.id.input_digit_5);


        hampay_memorableword_text = (FacedTextView)findViewById(R.id.hampay_memorableword_text);

        nationalCode = prefs.getString(Constants.REGISTERED_NATIONAL_CODE, "");

        memorableWord = prefs.getString(Constants.MEMORABLE_WORD, "");

        if (memorableWord != null) {
            hampay_memorableword_text.setText(memorableWord);
        }

        installationToken = prefs.getString(Constants.UUID, "");

    }


    public class RequestTACResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TACResponse>>
    {
        public RequestTACResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<TACResponse> tacResponseMessage)
        {
            hamPayDialog.dismisWaitingDialog();
            if (tacResponseMessage != null) {

                if (tacResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    editor.putString(Constants.USER_ID_TOKEN, tacResponseMessage.getService().getUserIdToken());
                    editor.commit();

                    if (tacResponseMessage.getService().getShouldAcceptTAC()) {


                    } else {

                        Intent intent = new Intent();

                        if (bundle != null) {
                            if (bundle.getBoolean(Constants.HAS_NOTIFICATION)) {
                                NotificationMessageType notificationMessageType;
                                notificationMessageType = NotificationMessageType.valueOf(bundle.getString(Constants.NOTIFICATION_TYPE));

                                intent = getIntent();

//                                Intent intent;
//
//                                switch (notificationMessageType){
//                                    case PAYMENT:
//                                        break;
//
//                                    case CREDIT_REQUEST:
//                                        intent = getIntent();
//                                        intent.setClass(activity, IndividualPaymentPendingActivity.class);
//                                        startActivity(intent);
//                                        break;
//                                }

                            }
                        }

                        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                        editor.commit();

                        intent.setClass(activity, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(Constants.USER_PROFILE_DTO, tacResponseMessage.getService().getUserProfile());
                        intent.putExtra(Constants.PENDING_PURCHASE_CODE, tacResponseMessage.getService().getProductCode());
                        intent.putExtra(Constants.PENDING_PAYMENT_CODE, tacResponseMessage.getService().getPaymentProductCode());
                        intent.putExtra(Constants.PENDING_PURCHASE_COUNT, tacResponseMessage.getService().getPendingPurchasesCount());
                        intent.putExtra(Constants.PENDING_PAYMENT_COUNT, tacResponseMessage.getService().getPendingPaymentCount());
                        intent.putExtra(Constants.NOTIFICATION, fromNotification);
                        editor.putBoolean(Constants.FORCE_USER_PROFILE, false);
                        editor.commit();
                        finish();
                        startActivity(intent);
                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Request TAC")
                            .setAction("Request")
                            .setLabel("Success")
                            .build());

                }else {
                    requestTAC = new RequestTAC(context, new RequestTACResponseTaskCompleteListener());
                    new HamPayDialog(activity).showFailTCRequestDialog(requestTAC, tacRequest,
                            tacResponseMessage.getService().getResultStatus().getCode(),
                            tacResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Request TAC")
                            .setAction("Request")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }
            else {
                requestTAC = new RequestTAC(context, new RequestTACResponseTaskCompleteListener());
                new HamPayDialog(activity).showFailTCRequestDialog(requestTAC, tacRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_tac_request));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Request TAC")
                        .setAction("Request")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

        }

        @Override
        public void onTaskPreRun() {
//            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }


    public class RequestLoginResponseTaskCompleteListener implements AsyncTaskCompleteListener<String>
    {
        public RequestLoginResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(String loginResponse)
        {

            SuccessLoginResponse successLoginResponse;
            FailedLoginResponse failedLoginResponse;

            if (loginResponse != null) {
                Gson gson = new Gson();
                Type listType = new TypeToken<SuccessLoginResponse>() {}.getType();
                JsonParser jsonParser = new JsonParser();
                JsonElement responseElement = jsonParser.parse(loginResponse.toString());
                successLoginResponse = gson.fromJson(responseElement.toString(), listType);

                if (successLoginResponse == null || successLoginResponse.getSuccessUrl() == null) {
                    listType = new TypeToken<FailedLoginResponse>() {}.getType();
                    jsonParser = new JsonParser();
                    responseElement = jsonParser.parse(loginResponse.toString());
                    failedLoginResponse = gson.fromJson(responseElement.toString(), listType);
                    if (failedLoginResponse != null) {
                        hamPayDialog.dismisWaitingDialog();
                        if (failedLoginResponse.getMessage().equalsIgnoreCase(Constants.USER_ACCOUNT_LOCKET)){
                            failedLoginResponse.setMessage(getString(R.string.msg_locked_hampay_login));
                        }else {
                            failedLoginResponse.setMessage(getString(R.string.msg_fail_hampay_login));
                        }

                        new HamPayDialog(activity).showLoginFailDialog(failedLoginResponse);

                        hamPayGaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("User Login")
                                .setAction("Login")
                                .setLabel("Fail(Server)")
                                .build());
                    }else {
                        hamPayDialog.dismisWaitingDialog();
                        failedLoginResponse = new FailedLoginResponse();
                        failedLoginResponse.setCode(Constants.LOCAL_ERROR_CODE);
                        failedLoginResponse.setMessage(getString(R.string.msg_fail_hampay_server));
                        new HamPayDialog(activity).showLoginFailDialog(failedLoginResponse);

                        hamPayGaTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("User Login")
                                .setAction("Login")
                                .setLabel("Fail(Mobile)")
                                .build());
                    }
                }else {
                    editor.putString(Constants.LOGIN_TOKEN_ID, successLoginResponse.getTokenId());
                    editor.commit();
                    tacRequest = new TACRequest();
                    tacRequest.setDeviceId(new DeviceInfo(activity).getAndroidId());
                    tacRequest.setAppVersion(new AppInfo(context).getVersionCode() + "");
                    requestTAC = new RequestTAC(context, new RequestTACResponseTaskCompleteListener());
                    requestTAC.execute(tacRequest);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("User Login")
                            .setAction("Login")
                            .setLabel("Success")
                            .build());
                }
            }else {
                hamPayDialog.dismisWaitingDialog();
                failedLoginResponse = new FailedLoginResponse();
                failedLoginResponse.setCode(Constants.LOCAL_ERROR_CODE);
                failedLoginResponse.setMessage(getString(R.string.msg_fail_hampay_server));
                new HamPayDialog(activity).showLoginFailDialog(failedLoginResponse);

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("User Login")
                        .setAction("Login")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

            inputPassValue = "";
            input_digit_1.setImageResource(R.drawable.pass_login_value_empty);
            input_digit_2.setImageResource(R.drawable.pass_login_value_empty);
            input_digit_3.setImageResource(R.drawable.pass_login_value_empty);
            input_digit_4.setImageResource(R.drawable.pass_login_value_empty);
            input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
    }

    @Override
    public void onTaskPreRun() {
        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
    }
}



    public class RequestRecentFundTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RecentPendingFundResponse>>
    {
        public RequestRecentFundTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RecentPendingFundResponse> recentPendingFundResponseMessage)
        {
            hamPayDialog.dismisWaitingDialog();
            if (recentPendingFundResponseMessage != null) {

                if (recentPendingFundResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    List<FundDTO> funds = recentPendingFundResponseMessage.getService().getFundDTOList();

                    if (funds.size() > 0) {
                        pendingFundLayout.setVisibility(View.VISIBLE);
                        pendingFundAdapter = new PendingFundAdapter(activity, funds);
                        recentPendingFundList.setAdapter(pendingFundAdapter);
                    }else {
                        new Expand(keyboard).animate();
                    }
                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Request TAC")
                            .setAction("Request")
                            .setLabel("Success")
                            .build());

                }else {
                    new Expand(keyboard).animate();
                }
            }
            else {
                new Expand(keyboard).animate();
            }

        }

        @Override
        public void onTaskPreRun() {
//            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.password_holder:
                if (keyboard.getVisibility() != View.VISIBLE)
                    new Expand(keyboard).animate();
                break;

            case R.id.keyboard_dismiss:
                if (keyboard.getVisibility() == View.VISIBLE)
                    new Collapse(keyboard).animate();
                break;

            case R.id.digit_1:
                inputDigit("1");
                break;

            case R.id.digit_2:
                inputDigit("2");
                break;

            case R.id.digit_3:
                inputDigit("3");
                break;

            case R.id.digit_4:
                inputDigit("4");
                break;

            case R.id.digit_5:
                inputDigit("5");
                break;

            case R.id.digit_6:
                inputDigit("6");
                break;

            case R.id.digit_7:
                inputDigit("7");
                break;

            case R.id.digit_8:
                inputDigit("8");
                break;

            case R.id.digit_9:
                inputDigit("9");
                break;

            case R.id.digit_0:
                inputDigit("0");
                break;

            case R.id.backspace:
                inputDigit("d");
                break;
        }
    }


    private void inputDigit(String digit){


        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        if (digit.contains("d")){
            if (inputPassValue.length() > 0) {
                inputPassValue = inputPassValue.substring(0, inputPassValue.length() - 1);
            }
        }
        else {
            if (inputPassValue.length() <= 4) {
                inputPassValue += digit;
            }
        }

        if (inputPassValue.length() == 5){
            try {

                password = SecurityUtils.getInstance(this).
                        generatePassword(inputPassValue,
                                memorableWord,
                                new DeviceInfo(activity).getAndroidId(),
                                installationToken);

            }catch (NoSuchAlgorithmException ex){}
            catch (UnsupportedEncodingException ex){}

            LoginData loginData = new LoginData();

            loginData.setUserPassword(password);
            loginData.setUserName(new PersianEnglishDigit(nationalCode).P2E());

            keyboard.setEnabled(false);

            requestLogin = new RequestLogin(context, new RequestLoginResponseTaskCompleteListener());
            requestLogin.execute(loginData);

        }


        switch (inputPassValue.length()){

            case 0:
                input_digit_1.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_2.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_3.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_4.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
                vibrator.vibrate(20);
                break;

            case 1:
                input_digit_1.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_2.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_3.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_4.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
                vibrator.vibrate(20);

                break;
            case 2:
                input_digit_1.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_2.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_3.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_4.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
                vibrator.vibrate(20);
                break;
            case 3:
                input_digit_1.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_2.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_3.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_4.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
                vibrator.vibrate(20);
                break;
            case 4:
                input_digit_1.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_2.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_3.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_4.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
                vibrator.vibrate(20);
                break;
            case 5:
                input_digit_1.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_2.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_3.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_4.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_5.setImageResource(R.drawable.pass_login_value_placeholder);
                vibrator.vibrate(20);
                break;
        }

    }


    @Override
    public void onBackPressed() {
        if (keyboard.getVisibility() == View.VISIBLE){
            new Collapse(keyboard).animate();
        }else {
            finish();
        }
    }
}
