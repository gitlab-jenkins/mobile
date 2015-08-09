package com.hampay.mobile.android.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.util.Constants;

public class GuideDetailActivity extends ActionBarActivity {

    WebView guide_webview;
    Bundle bundle;
    String webPageUrl;

    RelativeLayout loading_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_detail);

        bundle = getIntent().getExtras();

        webPageUrl = "http://" + bundle.getString(Constants.WEB_PAGE_ADDRESS, "");

        guide_webview = (WebView)findViewById(R.id.guide_webview);

        loading_rl = (RelativeLayout)findViewById(R.id.loading_rl);

        WebSettings settings = guide_webview.getSettings();
        settings.setJavaScriptEnabled(true);
        guide_webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        guide_webview.loadUrl(webPageUrl);
        guide_webview.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                loading_rl.setVisibility(View.GONE);
            }
        });

    }
}
