package com.hampay.mobile.android.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.util.Constants;

public class GuideDetailActivity extends ActionBarActivity {

    WebView guide_webview;
    Bundle bundle;
    String webPageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_detail);

        bundle = getIntent().getExtras();

        webPageUrl = "http://" + bundle.getString(Constants.WEB_PAGE_ADDRESS, "");

        guide_webview = (WebView)findViewById(R.id.guide_webview);

        WebSettings settings = guide_webview.getSettings();
        settings.setJavaScriptEnabled(true);
        guide_webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        guide_webview.loadUrl(webPageUrl);

    }
}
