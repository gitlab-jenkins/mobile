package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.response.dto.BillInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.PaymentType;
import xyz.homapay.hampay.mobile.android.model.SucceedPayment;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class BankWebPaymentActivity extends AppCompatActivity {

    private WebView bankWebView;
    private ImageView reload;
    private TextView urlText;
    private HamPayDialog hamPayDialog;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private PaymentInfoDTO paymentInfoDTO = null;
    private PurchaseInfoDTO purchaseInfoDTO = null;
    private BillInfoDTO billInfo;
    private PspInfoDTO pspInfoDTO = null;
    private String redirectedURL;
    private Context context;
    private Activity activity;
    private String postData;
    private Timer timer;
    private TimerTask timerTask;
    private String ipgUrl = "";
    private final Handler handler = new Handler();


    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 10000, 1000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        hamPayDialog.dismisWaitingDialog();
                    }
                });
            }
        };
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
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
        stoptimertask();
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
        setContentView(R.layout.activity_bank_web_payment);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        context = this;
        activity = BankWebPaymentActivity.this;

        Intent intent = getIntent();

        paymentInfoDTO = (PaymentInfoDTO)intent.getSerializableExtra(Constants.PAYMENT_INFO);
        purchaseInfoDTO = (PurchaseInfoDTO)intent.getSerializableExtra(Constants.PURCHASE_INFO);
        billInfo = (BillInfoDTO) intent.getSerializableExtra(Constants.BILL_INFO);
        pspInfoDTO = (PspInfoDTO)intent.getSerializableExtra(Constants.PSP_INFO);

        bankWebView = (WebView)findViewById(R.id.bankWebView);

        reload = (ImageView)findViewById(R.id.reload);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankWebView.reload();
            }
        });

        urlText = (TextView)findViewById(R.id.urlText);
        urlText.setHorizontallyScrolling(true);
        urlText.setScrollbarFadingEnabled(true);
        urlText.setHorizontallyScrolling(true);
        urlText.setMovementMethod(new ScrollingMovementMethod());

        hamPayDialog = new HamPayDialog(this);

        WebSettings settings = bankWebView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        if (paymentInfoDTO != null) {
            ipgUrl = Constants.BANK_GATEWAY_URL;
            redirectedURL = pspInfoDTO.getRedirectURL() + "?authToken=" + prefs.getString(Constants.LOGIN_TOKEN_ID, "");
            postData =
                    "ResNum4=" + pspInfoDTO.getCellNumber() +
                            "&ResNum3=" + paymentInfoDTO.getPspInfo().getSmsToken() +
                            "&RedirectURL=" + redirectedURL +
                            "&Amount=" + (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge() + paymentInfoDTO.getVat()) +
                            "&ResNum=" + paymentInfoDTO.getProductCode() +
                            "&TerminalId=" + pspInfoDTO.getSenderTerminalId();

        }else if (purchaseInfoDTO != null){
            ipgUrl = Constants.BANK_GATEWAY_URL;
            redirectedURL = pspInfoDTO.getRedirectURL() + "?authToken=" + prefs.getString(Constants.LOGIN_TOKEN_ID, "");
            postData =
                    "ResNum4=" + pspInfoDTO.getCellNumber() +
                            "&ResNum3=" + purchaseInfoDTO.getPspInfo().getSmsToken() +
                            "&RedirectURL=" + redirectedURL +
                            "&Amount=" + (purchaseInfoDTO.getAmount() + purchaseInfoDTO.getFeeCharge() + purchaseInfoDTO.getVat()) +
                            "&ResNum=" + purchaseInfoDTO.getProductCode() +
                            "&TerminalId=" + pspInfoDTO.getSenderTerminalId();
        }else if (billInfo != null){
            ipgUrl = Constants.BILLS_IPG_URL;
            pspInfoDTO = billInfo.getPspInfo();
            String payId = intent.getStringExtra(Constants.PAY_ID);
            redirectedURL = pspInfoDTO.getRedirectURL() + "?authToken=" + prefs.getString(Constants.LOGIN_TOKEN_ID, "");
            postData =
                    "ResNum4=" + pspInfoDTO.getCellNumber() +
                            "&ResNum3=" + pspInfoDTO.getSmsToken() +
                            "&RedirectURL=" + redirectedURL +
                            "&Amount=" + (billInfo.getAmount() + billInfo.getFeeCharge()) +
                            "&ResNum=" + billInfo.getProductCode() +
                            "&Bills[0].BillId=" + billInfo.getBillId() +
                            "&Bills[0].PayId=" + payId +
                            "&TerminalId=" + pspInfoDTO.getSenderTerminalId();
        }

        try {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            bankWebView.postUrl(ipgUrl, postData.getBytes("UTF-8"));
            hamPayDialog.showFirstIpg(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
            startTimer();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        bankWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("ERROR", String.valueOf(error));
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return false;
            }

            public void onPageFinished(WebView view, String url) {

                urlText.setText(url);
                ResultStatus resultStatus = ResultStatus.FAILURE;
                ServiceEvent serviceName;
                LogEvent logEvent = new LogEvent(context);
                if (url.toLowerCase().contains(pspInfoDTO.getRedirectURL().toLowerCase())) {
                    if (view.getTitle().toLowerCase().contains("ref:")) {
                        if (view.getTitle().split(":").length == 2){
                            serviceName = ServiceEvent.IPG_PAYMENT_SUCCESS;
                            logEvent.log(serviceName);
                            Intent intent = new Intent(context, PaymentCompletedActivity.class);
                            SucceedPayment succeedPayment = new SucceedPayment();
                            if (paymentInfoDTO != null) {
                                succeedPayment.setAmount(paymentInfoDTO.getAmount() + paymentInfoDTO.getVat() + paymentInfoDTO.getFeeCharge());
                                succeedPayment.setCode(paymentInfoDTO.getProductCode());
                                succeedPayment.setPaymentType(PaymentType.PAYMENT);
                            }else if (purchaseInfoDTO != null){
                                succeedPayment.setAmount(purchaseInfoDTO.getAmount());
                                succeedPayment.setCode(purchaseInfoDTO.getPurchaseCode());
                                succeedPayment.setPaymentType(PaymentType.PURCHASE);
                            }else if (billInfo != null){
                                succeedPayment.setAmount(billInfo.getAmount() + billInfo.getFeeCharge());
                                succeedPayment.setCode(billInfo.getBillId());
                                succeedPayment.setPaymentType(PaymentType.BILLS);
                            }
                            succeedPayment.setTrace(pspInfoDTO.getProviderId());
                            intent.putExtra(Constants.SUCCEED_PAYMENT_INFO, succeedPayment);
                            startActivityForResult(intent, 0);
                            resultStatus = ResultStatus.SUCCESS;
                        }else {
                            new HamPayDialog(activity).ipgFailDialog();
                            resultStatus = ResultStatus.FAILURE;
                            serviceName = ServiceEvent.IPG_PAYMENT_FAILURE;
                            logEvent.log(serviceName);
                        }
                    } else {
                        serviceName = ServiceEvent.IPG_PAYMENT_FAILURE;
                        logEvent.log(serviceName);
                        new HamPayDialog(activity).ipgFailDialog();
                    }
                } else {
                    serviceName = ServiceEvent.IPG_PAYMENT_FAILURE;
                    logEvent.log(serviceName);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if(resultCode == Activity.RESULT_OK){
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.ACTIVITY_RESULT, ResultStatus.SUCCESS.ordinal());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }
}
