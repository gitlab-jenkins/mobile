package com.hampay.mobile.android.activity;

import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;

public class GuideDetailActivity extends ActionBarActivity {

    WebView guide_webview;
    Bundle bundle;
    String webPageUrl;


    HamPayDialog hamPayDialog;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_detail);

        bundle = getIntent().getExtras();

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        webPageUrl = bundle.getString(Constants.WEB_PAGE_ADDRESS);

        guide_webview = (WebView)findViewById(R.id.guide_webview);

        hamPayDialog = new HamPayDialog(this);

        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

        WebSettings settings = guide_webview.getSettings();

        settings.setJavaScriptEnabled(true);
        guide_webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);


        guide_webview.loadUrl(webPageUrl);
        guide_webview.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                hamPayDialog.dismisWaitingDialog();
            }
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed();
            }
        });

    }
}
