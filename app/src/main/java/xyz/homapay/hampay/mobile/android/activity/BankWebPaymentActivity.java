package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.GetTokenFromPSPRequest;
import xyz.homapay.hampay.common.core.model.response.GetTokenFromPSPResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestGetTokenFromPSP;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class BankWebPaymentActivity extends AppCompatActivity {

    private Activity activity;
    WebView bankWebView;
    TextView urlText;
    HamPayDialog hamPayDialog;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    PaymentInfoDTO paymentInfoDTO = null;
    PurchaseInfoDTO purchaseInfoDTO = null;
    PspInfoDTO pspInfoDTO = null;

    String redirectedURL;

    private Context context;

    String postData;

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();


    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 6000, 1000);
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {

            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        hamPayDialog.dismisWaitingDialog();
                        finish();
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

        activity = BankWebPaymentActivity.this;

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        context = this;

        Intent intent = getIntent();

        paymentInfoDTO = (PaymentInfoDTO)intent.getSerializableExtra(Constants.PAYMENT_INFO);
        purchaseInfoDTO = (PurchaseInfoDTO)intent.getSerializableExtra(Constants.PURCHASE_INFO);
        pspInfoDTO = (PspInfoDTO)intent.getSerializableExtra(Constants.PSP_INFO);

        bankWebView = (WebView)findViewById(R.id.bankWebView);

        urlText = (TextView)findViewById(R.id.urlText);
        urlText.setHorizontallyScrolling(true);
        urlText.setScrollbarFadingEnabled(true);
        urlText.setHorizontallyScrolling(true);
        urlText.setMovementMethod(new ScrollingMovementMethod());

        hamPayDialog = new HamPayDialog(this);

        WebSettings settings = bankWebView.getSettings();


        settings.setJavaScriptEnabled(true);
        bankWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        if (paymentInfoDTO != null) {
            redirectedURL = Constants.IPG_URL + pspInfoDTO.getRedirectURL() + "?authToken=" + prefs.getString(Constants.LOGIN_TOKEN_ID, "");
            postData =
                    "ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
                            "&ResNum3=" + pspInfoDTO.getCardDTO().getSmsToken() +
                            "&RedirectURL=" + redirectedURL +
                            "&Amount=" + (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge()) +
                            "&ResNum=" + paymentInfoDTO.getProductCode() +
                            "&TerminalId=" + pspInfoDTO.getTerminalID();



//                        postData =
//                                "Amount=" + paymentInfoDTO.getAmount() +
//                                        "&TerminalId=" + pspInfoDTO.getTerminalID() +
//                                        "&ResNum=" + paymentInfoDTO.getProductCode() +
//                                        "&ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
//                                        "&RedirectURL=" + redirectedURL;

        }else if (purchaseInfoDTO != null){
            redirectedURL = Constants.IPG_URL + pspInfoDTO.getRedirectURL() + "?authToken=" + prefs.getString(Constants.LOGIN_TOKEN_ID, "");

            long vat = 0;

            if(purchaseInfoDTO.getVat() == null){
                vat = 0;
            }else {
                vat = purchaseInfoDTO.getVat();
            }

            postData =
//                    "ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
//                            "&ResNum3=" + pspInfoDTO.getCardDTO().getSmsToken() +
//                            "&RedirectURL=" + redirectedURL +
//                            "&Amount=" + (purchaseInfoDTO.getAmount() + purchaseInfoDTO.getFeeCharge() + vat) +
//                            "&ResNum=" + purchaseInfoDTO.getProductCode() +
//                            "&TerminalId=" + pspInfoDTO.getTerminalID();

                        "ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
                            "&ResNum3=" + pspInfoDTO.getCardDTO().getSmsToken() +
                            "&RedirectURL=" + redirectedURL +
                            "&Amount=" + (purchaseInfoDTO.getAmount() + purchaseInfoDTO.getFeeCharge() + vat) +
                            "&ResNum=" + purchaseInfoDTO.getProductCode() +
                            "&TerminalId=" + "10516003";


//                        postData =
//                                "Amount=" + /*purchaseInfoDTO.getAmount()*/ "10000" +
//                                        "&TerminalId=" + pspInfoDTO.getTerminalID() +
//                                        "&ResNum=" + purchaseInfoDTO.getProductCode() +
//                                        "&ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
//                                        "&RedirectURL=" + redirectedURL;
        }

        try {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            bankWebView.postUrl(Constants.BANK_GATEWAY_URL, postData.getBytes("UTF-8"));
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        bankWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {

                if (url.startsWith("https://sep.shaparak.ir")) {
                    if (!view.getTitle().contains("سامان")){
                        Toast.makeText(activity, getString(R.string.msg_fail_ipg_loading), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                urlText.setText(url);
                ResultStatus resultStatus = ResultStatus.FAILURE;

                if (url.toLowerCase().contains(pspInfoDTO.getRedirectURL().toLowerCase())) {
//                if (url.toLowerCase().contains("c.php")) {
                    if (view.getTitle().toLowerCase().contains("ref:")) {
                        if (view.getTitle().split(":").length == 2){
                            hamPayDialog.ipgSuccessDialog(view.getTitle().split(":")[1]);
                            resultStatus = ResultStatus.SUCCESS;
                        }else {
                            hamPayDialog.ipgSuccessDialog("");
                            resultStatus = ResultStatus.FAILURE;
                        }
                        startTimer();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.ACTIVITY_RESULT, resultStatus.ordinal());
                        setResult(Activity.RESULT_OK, returnIntent);
                    } else {
                        hamPayDialog.ipgFailDialog();
                        startTimer();
                    }
                } else {
                    hamPayDialog.dismisWaitingDialog();
                }
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

    }

    public class RequestGetTokenFromPSPTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<GetTokenFromPSPResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<GetTokenFromPSPResponse> getTokenFromPSPResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (getTokenFromPSPResponseMessage != null){
                if (getTokenFromPSPResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    if (paymentInfoDTO != null) {
                        redirectedURL = Constants.IPG_URL + pspInfoDTO.getRedirectURL() + "?authToken=" + prefs.getString(Constants.LOGIN_TOKEN_ID, "");
                        postData =
                                "ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
                                "&ResNum3=" + pspInfoDTO.getCardDTO().getSmsToken() +
                                        "&RedirectURL=" + redirectedURL +
                                        "&Amount=" + (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge()) +
                                        "&ResNum=" + paymentInfoDTO.getProductCode() +
                                        "&TermID=" + pspInfoDTO.getTerminalID();
//                        postData =
//                                "Amount=" + paymentInfoDTO.getAmount() +
//                                        "&TerminalId=" + pspInfoDTO.getTerminalID() +
//                                        "&ResNum=" + paymentInfoDTO.getProductCode() +
//                                        "&ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
//                                        "&RedirectURL=" + redirectedURL;

                    }else if (purchaseInfoDTO != null){
                        redirectedURL = Constants.IPG_URL + pspInfoDTO.getRedirectURL() + "?authToken=" + prefs.getString(Constants.LOGIN_TOKEN_ID, "");

                        long vat = 0;

                        if(purchaseInfoDTO.getVat() == null){
                            vat = 0;
                        }else {
                            vat = purchaseInfoDTO.getVat();
                        }

                        postData =
                                "ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
                                        "&ResNum3=" + pspInfoDTO.getCardDTO().getSmsToken() +
                                        "&RedirectURL=" + redirectedURL +
                                        "&Amount=" + (purchaseInfoDTO.getAmount() + purchaseInfoDTO.getFeeCharge() + vat) +
                                        "&ResNum=" + purchaseInfoDTO.getProductCode() +
                                        "&TermID=" + pspInfoDTO.getTerminalID();


//                        postData =
//                                "Amount=" + /*purchaseInfoDTO.getAmount()*/ "10000" +
//                                        "&TerminalId=" + pspInfoDTO.getTerminalID() +
//                                        "&ResNum=" + purchaseInfoDTO.getProductCode() +
//                                        "&ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
//                                        "&RedirectURL=" + redirectedURL;
                    }

                    try {
                        bankWebView.postUrl(Constants.BANK_GATEWAY_URL, postData.getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
                else {

                    finish();

                    Toast.makeText(context, "خطا در انجام عملیات", Toast.LENGTH_SHORT).show();

//                    requestLatestInvoiceContacts = new RequestLatestInvoiceContacts(context, new RequestLatestInvoiceContactsTaskCompleteListener());
//                    new HamPayDialog(activity).showFailLatestInvoiceDialog(requestLatestInvoiceContacts, latestInvoiceContactsRequest,
//                            latestInvoiceContactsResponseMessage.getService().getResultStatus().getCode(),
//                            latestInvoiceContactsResponseMessage.getService().getResultStatus().getDescription());

                }
            }else {
//                requestLatestInvoiceContacts = new RequestLatestInvoiceContacts(context, new RequestLatestInvoiceContactsTaskCompleteListener());
//                new HamPayDialog(activity).showFailLatestInvoiceDialog(requestLatestInvoiceContacts, latestInvoiceContactsRequest,
//                        Constants.LOCAL_ERROR_CODE,
//                        getString(R.string.msg_fail_contacts_enabled));
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }
}
