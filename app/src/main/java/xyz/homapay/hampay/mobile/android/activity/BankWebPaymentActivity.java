package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;

import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.account.Log;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class BankWebPaymentActivity extends AppCompatActivity {

    WebView bankWebView;
    HamPayDialog hamPayDialog;
    SharedPreferences prefs;
    PaymentInfoDTO paymentInfoDTO = null;
    PurchaseInfoDTO purchaseInfoDTO = null;
    PspInfoDTO pspInfoDTO = null;

    String redirectedURL;

    Activity activity;

    String postData;

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_web_payment);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        Intent intent = getIntent();

        activity = BankWebPaymentActivity.this;

        paymentInfoDTO = (PaymentInfoDTO)intent.getSerializableExtra(Constants.PAYMENT_INFO);
        purchaseInfoDTO = (PurchaseInfoDTO)intent.getSerializableExtra(Constants.PURCHASE_INFO);
        pspInfoDTO = (PspInfoDTO)intent.getSerializableExtra(Constants.PSP_INFO);

        bankWebView = (WebView)findViewById(R.id.bankWebView);

        hamPayDialog = new HamPayDialog(this);

        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

        WebSettings settings = bankWebView.getSettings();


        settings.setJavaScriptEnabled(true);
        bankWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        if (paymentInfoDTO != null) {
            redirectedURL = Constants.IPG_URL + "/payment/saman-redirect/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "");
            postData =
                    "Amount=" + paymentInfoDTO.getAmount() +
                            "&TerminalId=" + pspInfoDTO.getTerminalID() +
                            "&ResNum=" + paymentInfoDTO.getProductCode() +
                            "&ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
                            "&RedirectURL=" + redirectedURL;

        }else if (purchaseInfoDTO != null){
            redirectedURL = Constants.IPG_URL + "/purchase/saman-redirect/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "");
            postData =
                    "Amount=" + purchaseInfoDTO.getAmount() +
                            "&TerminalId=" + pspInfoDTO.getTerminalID() +
                            "&ResNum=" + purchaseInfoDTO.getProductCode() +
                            "&ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
                            "&RedirectURL=" + redirectedURL;
        }
        android.util.Log.e("POST", postData);

        try {
            bankWebView.postUrl(Constants.BANK_GATEWAY_URL, postData.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



        bankWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {

                if (url.toLowerCase().contains("c.php")) {
                    if (view.getTitle().equalsIgnoreCase("failure")) {
                        hamPayDialog.businessPaymentFailDialog();
                    } else {
                        hamPayDialog.businessPaymentSuccessDialog(view.getTitle());
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
}
