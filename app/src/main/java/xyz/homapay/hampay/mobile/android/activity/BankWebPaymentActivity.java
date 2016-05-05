package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

    WebView bankWebView;
    EditText urlText;
    HamPayDialog hamPayDialog;
    SharedPreferences prefs;
    PaymentInfoDTO paymentInfoDTO = null;
    PurchaseInfoDTO purchaseInfoDTO = null;
    PspInfoDTO pspInfoDTO = null;

    String redirectedURL;

    RequestGetTokenFromPSP requestGetTokenForSamanPSP;
    GetTokenFromPSPRequest getTokenFromPSPRequest;

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_web_payment);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        context = this;

        Intent intent = getIntent();

        paymentInfoDTO = (PaymentInfoDTO)intent.getSerializableExtra(Constants.PAYMENT_INFO);
        purchaseInfoDTO = (PurchaseInfoDTO)intent.getSerializableExtra(Constants.PURCHASE_INFO);
        pspInfoDTO = (PspInfoDTO)intent.getSerializableExtra(Constants.PSP_INFO);

        bankWebView = (WebView)findViewById(R.id.bankWebView);

        urlText = (EditText)findViewById(R.id.urlText);

        hamPayDialog = new HamPayDialog(this);

        WebSettings settings = bankWebView.getSettings();


        settings.setJavaScriptEnabled(true);
        bankWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        if (purchaseInfoDTO != null) {
            getTokenFromPSPRequest = new GetTokenFromPSPRequest();
            getTokenFromPSPRequest.setProductCode(purchaseInfoDTO.getProductCode());
            requestGetTokenForSamanPSP = new RequestGetTokenFromPSP(context, new RequestGetTokenFromPSPTaskCompleteListener(), 0);
            requestGetTokenForSamanPSP.execute(getTokenFromPSPRequest);
        }else if (paymentInfoDTO != null){
            getTokenFromPSPRequest = new GetTokenFromPSPRequest();
            getTokenFromPSPRequest.setProductCode(paymentInfoDTO.getProductCode());
            requestGetTokenForSamanPSP = new RequestGetTokenFromPSP(context, new RequestGetTokenFromPSPTaskCompleteListener(), 1);
            requestGetTokenForSamanPSP.execute(getTokenFromPSPRequest);
        }

        bankWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {

                urlText.setText(url);

//                if (url.toLowerCase().startsWith("https://sep.shaparak.ir") && url.toLowerCase().contains(pspInfoDTO.getRedirectURL().toLowerCase())) {
                if (url.toLowerCase().contains("c.php")) {
                    if (view.getTitle().equalsIgnoreCase("failure")) {
                        hamPayDialog.ipgFailDialog();
                        startTimer();
                    } else {
                        hamPayDialog.ipgSuccessDialog(view.getTitle());
                        startTimer();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.ACTIVITY_RESULT, ResultStatus.SUCCESS.ordinal());
                        setResult(Activity.RESULT_OK, returnIntent);
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
//                        postData =
//                                "ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
//                                        "&RedirectURL=" + redirectedURL +
//                                        "&Token=" + getTokenFromPSPResponseMessage.getService().getToken();
                        postData =
                                "Amount=" + paymentInfoDTO.getAmount() +
                                        "&TerminalId=" + pspInfoDTO.getTerminalID() +
                                        "&ResNum=" + paymentInfoDTO.getProductCode() +
                                        "&ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
                                        "&RedirectURL=" + redirectedURL;

                    }else if (purchaseInfoDTO != null){
                        redirectedURL = Constants.IPG_URL + pspInfoDTO.getRedirectURL() + "?authToken=" + prefs.getString(Constants.LOGIN_TOKEN_ID, "");
//                        postData =
//                                "ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
//                                        "&RedirectURL=" + redirectedURL +
//                                        "&Token=" + getTokenFromPSPResponseMessage.getService().getToken();


                        postData =
                                "Amount=" + /*purchaseInfoDTO.getAmount()*/ "10000" +
                                        "&TerminalId=" + pspInfoDTO.getTerminalID() +
                                        "&ResNum=" + purchaseInfoDTO.getProductCode() +
                                        "&ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
                                        "&RedirectURL=" + redirectedURL;
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
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }
}
