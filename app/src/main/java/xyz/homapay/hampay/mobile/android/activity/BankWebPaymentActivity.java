package xyz.homapay.hampay.mobile.android.activity;

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

import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class BankWebPaymentActivity extends AppCompatActivity {

    WebView bankWebView;
    HamPayDialog hamPayDialog;
    SharedPreferences prefs;
    PaymentInfoDTO paymentInfoDTO = null;
    PspInfoDTO pspInfoDTO = null;


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

        paymentInfoDTO = (PaymentInfoDTO)intent.getSerializableExtra(Constants.PAYMENT_INFO);
        pspInfoDTO = (PspInfoDTO)intent.getSerializableExtra(Constants.PSP_INFO);

        bankWebView = (WebView)findViewById(R.id.bankWebView);

        hamPayDialog = new HamPayDialog(this);

        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

        WebSettings settings = bankWebView.getSettings();

        settings.setJavaScriptEnabled(true);
        bankWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        String postData =
                "Amount=" + paymentInfoDTO.getAmount() +
                "&TerminalId=" + pspInfoDTO.getTerminalID() +
                "&ResNum=" + pspInfoDTO.getProductCode() +
                "&ResNum4=" + prefs.getString(Constants.REGISTERED_CELL_NUMBER, "") +
                "&RedirectURL=" + "/hampay/redirect/saman/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") ;

        try {
            bankWebView.postUrl("http://176.58.104.158/assets/psp/index.php", postData.getBytes( "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        bankWebView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                hamPayDialog.dismisWaitingDialog();
            }
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed();
            }
        });

    }
}
