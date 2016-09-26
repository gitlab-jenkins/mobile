package xyz.homapay.hampay.mobile.android.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.LoginRequest;
import xyz.homapay.hampay.common.core.model.request.PendingFundListRequest;
import xyz.homapay.hampay.common.core.model.request.RecentPendingFundRequest;
import xyz.homapay.hampay.common.core.model.request.TACRequest;
import xyz.homapay.hampay.common.core.model.response.LoginResponse;
import xyz.homapay.hampay.common.core.model.response.RecentPendingFundResponse;
import xyz.homapay.hampay.common.core.model.response.TACResponse;
import xyz.homapay.hampay.common.core.model.response.dto.FundDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Manifest;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PendingFundAdapter;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestLogin;
import xyz.homapay.hampay.mobile.android.async.RequestNewLogin;
import xyz.homapay.hampay.mobile.android.async.RequestPendingFundList;
import xyz.homapay.hampay.mobile.android.async.RequestRecentPendingFund;
import xyz.homapay.hampay.mobile.android.async.RequestTAC;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.NotificationMessageType;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.security.KeyExchange;
import xyz.homapay.hampay.mobile.android.util.AppInfo;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.NetworkConnectivity;
import xyz.homapay.hampay.mobile.android.util.SecurityUtils;


public class HamPayLoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static HamPayLoginActivity instance = null;
    private KeyExchange keyExchange;
    private BroadcastReceiver mIntentReceiver;
    private NetworkConnectivity networkConnectivity;
    private IntentFilter notificationIntentFilter;
    private BroadcastReceiver notificationIntentReceiver;
    private SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onResume() {
        super.onResume();
        intentFilter = new IntentFilter("network.intent.MAIN");
        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("get_status", false)){
                    new KeyExchangeTask().execute();
                }
            }
        };
        this.registerReceiver(mIntentReceiver, intentFilter);


        notificationIntentFilter = new IntentFilter("notification.intent.MAIN");
        notificationIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("get_update", false)){
                    requestAndLoadPhoneState();
                }
            }
        };
        registerReceiver(notificationIntentReceiver, notificationIntentFilter);


        HamPayApplication.setAppSate(AppState.Resumed);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
        unregisterReceiver(notificationIntentReceiver);
    }

    RequestLogin requestLogin;
    FacedTextView hampay_memorableword_text;
    String userIdToken = "";
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
    private IntentFilter intentFilter;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    HamPayDialog hamPayDialog;
    FacedTextView hampay_user;
    TACRequest tacRequest;
    RequestTAC requestTAC;
    Tracker hamPayGaTracker;

    String password = "";
    private LinearLayout pendingFundLayout;
    private ListView recentPendingFundList;
    private RequestRecentPendingFund requestRecentPendingFund;
    private RecentPendingFundRequest recentPendingFundRequest;
    private PendingFundAdapter pendingFundAdapter;

    public void userManual(View view){
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_login);
        intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_login);
        startActivity(intent);
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

        if (requestRecentPendingFund != null){
            if (!requestRecentPendingFund.isCancelled()){
                requestRecentPendingFund.cancel(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HamPayApplication.setAppSate(AppState.Resumed);
        unregisterReceiver(mIntentReceiver);
    }

    @Override
    public void finish() {
        super.finish();
        instance = null;
    }

    private Bundle bundle;

    private ArrayList<PermissionListener> permissionListeners = new ArrayList<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (PermissionListener permissionListener : permissionListeners)
            if (permissionListener.onResult(requestCode, permissions, grantResults)) {
                permissionListeners.remove(permissionListener);
            }
    }

    private void requestAndLoadPhoneState() {
        String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        permissionListeners = new RequestPermissions().request(activity, Constants.READ_PHONE_STATE, permissions, new PermissionListener() {
            @Override
            public boolean onResult(int requestCode, String[] requestPermissions, int[] grantResults) {
                if (requestCode == Constants.READ_PHONE_STATE) {
                    if (requestPermissions[0].equals(Manifest.permission.READ_PHONE_STATE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission granted
                        requestRecentPendingFund = new RequestRecentPendingFund(activity, new RequestRecentFundTaskCompleteListener());
                        recentPendingFundRequest = new RecentPendingFundRequest();
                        recentPendingFundRequest.setImei(new DeviceInfo(activity).getIMEI());
                        recentPendingFundRequest.setNationalCode(userIdToken);
                        requestRecentPendingFund.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, recentPendingFundRequest);
                    } else {
                        // Permission not granted
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
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
        hamPayDialog = new HamPayDialog(activity);
        keyExchange = new KeyExchange(context);
        networkConnectivity = new NetworkConnectivity();
        if (!networkConnectivity.isOnline(context)){
            hamPayDialog.showNoNetwork();
        }else {
            keyExchange = new KeyExchange(context);
            new KeyExchangeTask().execute();
        }

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        userIdToken = prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, "");

        hampay_user = (FacedTextView)findViewById(R.id.hampay_user);
        hampay_user.setText(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        pendingFundLayout = (LinearLayout)findViewById(R.id.pending_fund_layout);
        recentPendingFundList = (ListView)findViewById(R.id.recent_pending_fund_list);
        recentPendingFundList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (keyboard.getVisibility() == View.GONE)
                    new Expand(keyboard).animate();
            }
        });


        bundle = getIntent().getExtras();

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

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

        memorableWord = prefs.getString(Constants.MEMORABLE_WORD, "");

        if (memorableWord != null) {
            hampay_memorableword_text.setText(memorableWord);
        }

        installationToken = prefs.getString(Constants.UUID, "");

        pullToRefresh = (SwipeRefreshLayout)findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestAndLoadPhoneState();
            }
        });

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
                    if (tacResponseMessage.getService().isShouldAcceptTAC()) {
                    } else {
                        Intent intent = new Intent();
                        if (bundle != null) {
                            if (bundle.getBoolean(Constants.HAS_NOTIFICATION)) {
                                NotificationMessageType notificationMessageType;
                                notificationMessageType = NotificationMessageType.valueOf(bundle.getString(Constants.NOTIFICATION_TYPE));
                                intent = getIntent();
                            }
                        }
                        intent.setClass(activity, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(Constants.USER_PROFILE_DTO, tacResponseMessage.getService().getTacDTO().getUserProfile());
                        intent.putExtra(Constants.PENDING_PURCHASE_CODE, tacResponseMessage.getService().getTacDTO().getPurchaseProductCode());
                        intent.putExtra(Constants.PENDING_PAYMENT_CODE, tacResponseMessage.getService().getTacDTO().getPaymentProductCode());
                        intent.putExtra(Constants.PENDING_PURCHASE_COUNT, tacResponseMessage.getService().getTacDTO().getPendingPurchasesCount());
                        intent.putExtra(Constants.PENDING_PAYMENT_COUNT, tacResponseMessage.getService().getTacDTO().getPendingPaymentCount());
                        intent.putExtra(Constants.SHOW_CREATE_INVOICE, tacResponseMessage.getService().getTacDTO().isShowCreateInvoice());
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
        public void onTaskPreRun() {}
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    public class RequestRecentFundTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RecentPendingFundResponse>>
    {
        public RequestRecentFundTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RecentPendingFundResponse> recentPendingFundResponseMessage)
        {
            hamPayDialog.dismisWaitingDialog();
            pullToRefresh.setRefreshing(false);
            if (recentPendingFundResponseMessage != null) {

                if (recentPendingFundResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    List<FundDTO> funds = recentPendingFundResponseMessage.getService().getFundDTOList();

                    if (funds.size() > 0) {
                        pendingFundLayout.setVisibility(View.VISIBLE);
                        pendingFundAdapter = new PendingFundAdapter(activity, funds);
                        recentPendingFundList.setAdapter(pendingFundAdapter);
                    }else {
                        if (keyboard.getVisibility() == View.GONE)
                            new Expand(keyboard).animate();
                    }
                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Request TAC")
                            .setAction("Request")
                            .setLabel("Success")
                            .build());

                }else {
                    if (keyboard.getVisibility() == View.GONE)
                        new Expand(keyboard).animate();
                }
            }
            else {
                if (keyboard.getVisibility() == View.GONE)
                    new Expand(keyboard).animate();
            }

        }

        @Override
        public void onTaskPreRun() {}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.password_holder:
                if (keyboard.getVisibility() == View.GONE)
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

            if (requestRecentPendingFund != null){
                if (!requestRecentPendingFund.isCancelled()){
                    requestRecentPendingFund.cancel(true);
                }
            }

            keyboard.setEnabled(false);

            if (!networkConnectivity.isOnline(context)){
                hamPayDialog.showNoNetwork();
            }else {
                if (keyExchange.getKey() != null && keyExchange.getIv() != null) {
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setPassword(password);
                    loginRequest.setUsername(userIdToken);
                    RequestNewLogin requestNewLogin = new RequestNewLogin(activity, new RequestLoginTaskCompleteListener());
                    requestNewLogin.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, loginRequest);
                } else {
                    resetLogin();
                    Toast.makeText(activity, getString(R.string.system_connectivity), Toast.LENGTH_SHORT).show();
                    new KeyExchangeTask().execute();
                }
            }
        }


        switch (inputPassValue.length()){

            case 0:
                input_digit_1.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_2.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_3.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_4.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
                break;

            case 1:
                input_digit_1.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_2.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_3.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_4.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
                break;
            case 2:
                input_digit_1.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_2.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_3.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_4.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
                break;
            case 3:
                input_digit_1.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_2.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_3.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_4.setImageResource(R.drawable.pass_login_value_empty);
                input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
                break;
            case 4:
                input_digit_1.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_2.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_3.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_4.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
                break;
            case 5:
                input_digit_1.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_2.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_3.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_4.setImageResource(R.drawable.pass_login_value_placeholder);
                input_digit_5.setImageResource(R.drawable.pass_login_value_placeholder);
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


    public class RequestLoginTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<LoginResponse>>
    {
        public RequestLoginTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<LoginResponse> loginResponseResponseMessage)
        {
            if (loginResponseResponseMessage != null) {
                if (loginResponseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    editor.putString(Constants.LOGIN_TOKEN_ID, loginResponseResponseMessage.getService().getAuthToken());
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();
                    tacRequest = new TACRequest();
                    tacRequest.setDeviceId(new DeviceInfo(activity).getAndroidId());
                    tacRequest.setAppVersion(new AppInfo().getVersionCode() + "");
                    requestTAC = new RequestTAC(context, new RequestTACResponseTaskCompleteListener());
                    requestTAC.execute(tacRequest);
                }else if (loginResponseResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE){
                    resetLogin();
                    hamPayDialog.showLoginFailDialog(loginResponseResponseMessage.getService().getRemainRetryCount());
                }else if (loginResponseResponseMessage.getService().getResultStatus() == ResultStatus.BLOCKED_IDP_USER){
                    resetLogin();
                    hamPayDialog.showLoginFailDialog(0);
                }
            }
            else {
                Toast.makeText(activity, getString(R.string.system_connectivity), Toast.LENGTH_SHORT).show();
                resetLogin();
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    private class KeyExchangeTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                keyExchange.exchange();
            } catch (EncryptionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            hamPayDialog.dismisWaitingDialog();
            if (keyExchange.getKey() != null && keyExchange.getIv() != null){
                requestAndLoadPhoneState();
            }else {
                Toast.makeText(activity, getString(R.string.system_connectivity), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            hamPayDialog.dismisWaitingDialog();
            hamPayDialog.showHamPayCommunication();
        }

    }

    private void resetLogin(){
        inputPassValue = "";
        input_digit_1.setImageResource(R.drawable.pass_login_value_empty);
        input_digit_2.setImageResource(R.drawable.pass_login_value_empty);
        input_digit_3.setImageResource(R.drawable.pass_login_value_empty);
        input_digit_4.setImageResource(R.drawable.pass_login_value_empty);
        input_digit_5.setImageResource(R.drawable.pass_login_value_empty);
        hamPayDialog.dismisWaitingDialog();
    }
}
