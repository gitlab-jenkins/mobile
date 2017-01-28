package xyz.homapay.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class GuideDetailActivity extends AppCompatActivity {

    @BindView(R.id.guide_webview)
    WebView guide_webview;
    @BindView(R.id.title)
    FacedTextView title;
    @BindView(R.id.close_tc_privacy)
    FacedTextView close_tc_privacy;
    private Bundle bundle;
    private String webPageUrl;
    private HamPayDialog hamPayDialog;
    private SharedPreferences prefs;
    private Context context;

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
        setContentView(R.layout.activity_guide_detail);
        ButterKnife.bind(this);

        context = this;
        bundle = getIntent().getExtras();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        webPageUrl = bundle.getString(Constants.WEB_PAGE_ADDRESS);
        title.setText(bundle.getString(Constants.TAC_PRIVACY_TITLE));
        close_tc_privacy.setOnClickListener(v -> finish());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            guide_webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        hamPayDialog = new HamPayDialog(this);

        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        guide_webview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        guide_webview.loadUrl(webPageUrl);
        guide_webview.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                hamPayDialog.dismisWaitingDialog();
            }
        });

    }
}
